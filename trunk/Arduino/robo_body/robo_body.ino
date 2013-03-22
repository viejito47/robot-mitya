// -------------------------------------------------------------------------------------
// file = "robot_body.ino"
// company = "Dzakhov's jag"
// Copyright © Dmitry Dzakhov 2011
//   Скетч, предназначенный для исполнения роботом команд, полученных с уровня
//   Android-приложения и передачи сигналов, полученных от сенсоров робота, на
//   уровень Android-приложения.
// -------------------------------------------------------------------------------------

#ifdef USBCON    // For Leonardo (Romeo V2) board we use SoftwareServo library, because of lack of Timers.
  #include <SoftwareServo.h>
#else
  #include <Servo.h>
#endif

#include <SmartServo.h>
#include <IRremote.h>
#include <RoboScript.h>
#include <EEPROM.h>
#include "robo_body.h"

// Эхо-режим. Возврат всех полученных сообщений.
const boolean ECHO_MODE = false;

// Длина сообщения:
const int MESSAGELENGTH = 5;
// Текущий необработанный текст из буфера, прочитанного на предыдущей итерации чтения:
String MessageBuffer = "";

// Objects to control servodrives.
SmartServo servoHeadHorizontal;
SmartServo servoHeadVertical;
SmartServo servoTail;

int servoHeadCurrentHorizontalDegree;
int servoHeadCurrentVerticalDegree;
int servoTailCurrentDegree;

// Объекты управления ИК-приёмником и ИК-передатчиком.
IRrecv irrecv(targetPin);
decode_results results;
IRsend irsend;

// Программа для робота: РобоСкрипт.
RoboScript happyReflex;
RoboScript readyToPlayReflex;
RoboScript sadReflex;
RoboScript angryReflex;
RoboScript musicReflex;

// Массив пользовательских программ на РобоСкрипте.
const int CUSTOM_ROBOSCRIPTS_COUNT = 10;
RoboScript customRoboScript[CUSTOM_ROBOSCRIPTS_COUNT];
// Признак состояния записи РобоСкрипта.
boolean recordingRoboScript = false;
// Текущий (последний) выполняемый скрипт.
signed int currentRoboScriptIndex = -1;
// Признак для контроля следования команд при записи РобоСкрипта.
boolean actionStarted = false;
// Текущее записываемое действие в РобоСкрипт.
RoboAction recordedAction;

// Varibles for Timer to send VCC Battery data
boolean useVCCTimer = false;
unsigned long VCCTimerInterval=0;
unsigned long timerTimeOld;  // Store previous timer value

int IrServoStep = IR_SERVO_STEP_DEFAULT; // Step for rotating head from remote
unsigned long IrCommands[IR_TOTAL_COMMANDS]; // All command from remote control

unsigned long IrRemoteLastCommand = 0; // Last command received by IR receiver (except IR_COMMAND_SEPARATOR)
const unsigned long IR_COMMAND_SEPARATOR = 0xFFFFFFFF;
byte IrRemoteButtonState = 0; // 0 = Boot state. No buttons were ever pressed,
                              // 1 = Button pressed, we only received the code of the button - very short press.
                              // 2 = Button pressed for short time, we received first IR_COMMAND_SEPARATOR for the first time after IrRemoteLastCommand received.
                              // 3 = Button pressed for long time, we received IR_COMMAND_SEPARATOR for more then one time after IrRemoteLastCommand received.

const int IR_LAST_COMMANDS_BUFFER_SIZE = 3; // Last commands and their state to determine when to enter Programmator mode
byte IrLastCommandsState[IR_LAST_COMMANDS_BUFFER_SIZE]={0}; // Last command state (LONG, SHORT or VERY SHORT press)
unsigned long IrLastCommandsValue[IR_LAST_COMMANDS_BUFFER_SIZE]={0}; // Last command value

boolean IsInIrProgrammMode = false; // Are we in IR command programm mode?
byte IrProgrammatorStep = 0; // Number of command we're processing now

// Функция инициализации скетча:
void setup()
{
  // Set speed for serial port on pins 0,1 :
  #ifdef USBCON   // For Leonardo (Romeo V2) board support
      Serial1.begin(9600);
  #else
      Serial.begin(9600);
  #endif

  // Инициализация ИК-приёмника:
  irrecv.enableIRIn(); // Start the receiver
  pinMode(gunPin, OUTPUT);
  digitalWrite(gunPin, LOW);

  // Инициализация двигателей (установка нулевой скорости):
  moveMotor("G", 0);

  // Установка горизонтального сервопривода в положение для установки телефона:
  pinMode(servoHeadHorizontalPin, OUTPUT);
  servoHeadHorizontal.attach(servoHeadHorizontalPin, servoHeadHorizontalMinDegree, servoHeadHorizontalMaxDegree);
  moveHead("H", servoHeadHorizontalDefaultState);

  // Установка вертикального сервопривода в положение для установки телефона:
  pinMode(servoHeadVerticalPin, OUTPUT);
  servoHeadVertical.attach(servoHeadVerticalPin, servoHeadVerticalMinDegree, servoHeadVerticalMaxDegree);
  moveHead("V", servoHeadVerticalDefaultState);

  // Установка хвоста пистолетом:
  pinMode(servoTailPin, OUTPUT);
  servoTail.attach(servoTailPin, servoTailMinDegree, servoTailMaxDegree);
  moveTail(servoTailDefaultState);
  
  pinMode(lightPin, OUTPUT);
  digitalWrite(lightPin, LOW);
  
  happyReflexInitialize();
  readyToPlayReflexInitialize();
  sadReflexInitialize();
  angryReflexInitialize();
  musicReflexInitialize();  
  
// Read all IR commands from EEPROM 
  for(int i=0; i<sizeof(IrCommands); i++)
  {
     *((byte*)&IrCommands + i) = EEPROM.read(i);
  }
}

void processEvents()
{
  CheckIrCommands();
  
  CheckVCCTimer();

  ProcessSwinging();

  happyReflexRun();
  readyToPlayReflexRun();
  sadReflexRun();
  angryReflexRun();
  musicReflexRun();
  
  customRoboScriptRun();
}

// Функция главного цикла:
void loop()
{
  processMessageBuffer(); // Receive all messages and process them
  processEvents();
  #ifdef USBCON  // For Leonardo (Romeo V2) board we use SoftwareServo library, because of lack of Timers.
    SoftwareServo::refresh();
  #endif
}


// Проверка ИК-попадания в робота:
boolean checkIrHit()
{
    unsigned long hitValue =  0xA90;
//    if (debugMode) {
//      hitValue = 0xA90;
//    }
//    else {
//      hitValue = 0xABC1;
//    }
    if((results.decode_type == SONY) && (results.value == hitValue))
    {
          // Шлю сообщения Android-приложению об ИК-попадании в нас:
      // один раз '*0001' и один раз '*0000' (hit - попадание).
      // '*0000' нужно чтобы сбросить значение в хэш-таблице 
      // сообщений Android-приложения. Там используется хэш-таблица
      // сообщений для исключения из обработки повторяющихся команд
      // с одинаковыми значениями.
      #ifdef USBCON   // For Leonardo (Romeo V2) board support
        Serial1.print("*0001");
        Serial1.print("*0000");
      #else
        Serial.print("*0001");
        Serial.print("*0000");
      #endif

    }
}

void executeIrCommand(int cmd)
{
//  Serial1.print(cmd); //DEBGUG FOR LEONARDO
//  Serial.print(cmd); // DEBUG FOR OTHER BOARDS
  switch(cmd)
  {
    case 0: // move forward
      moveMotor( "G", IR_MOVE_SPEED );
      break;
    case 1: // move backwards
      moveMotor( "G", -IR_MOVE_SPEED );
      break;        
    case 2: // turn left
      moveMotor( "L", -IR_MOVE_SPEED );
      moveMotor( "R", IR_MOVE_SPEED );
      break;
    case 3: // turn right
      moveMotor( "L", IR_MOVE_SPEED );      
      moveMotor( "R", -IR_MOVE_SPEED );
      break;   
    case 4: //stop
      moveMotor( "G", 0 );
      break;
      
    case 5: // Move Horizontal Head Left
      moveHead( "H", servoHeadHorizontal.read()-IrServoStep );
      break;
    case 6: // Move Horizontal Head Right
      moveHead( "H", servoHeadHorizontal.read()+IrServoStep );
      break;
    case 7: // Move Vertical Head Up
      moveHead( "V", servoHeadVertical.read()-IrServoStep );
      break;        
    case 8: // Move Vertical Head Down
      moveHead( "V", servoHeadVertical.read()+IrServoStep );
      break;
    case 9: //no
      servoHeadHorizontal.startSwing(1, 400, 2.5, 60, 0.75, true);      
      break;
    case 10: //yes
      servoHeadVertical.startSwing(1, 400, 2.5, 30, 0.8, true);
      break;
    case 11: //tail
      servoTail.startSwing(1, 250, 6, 70, 0.9, true);
      break;
    case 12: // Mood 
      executeAction("M", 0x0102, true );
      break;
    case 13: // Mood 
      executeAction("M", 0x0103, true );
      break;
    case 14: // Mood 
      executeAction("M", 0x0104, true );
      break;
    case 15: // Mood 
      executeAction("M", 0x0105, true );
      break;
    case 16: // Mood 
      executeAction("M", 0x0102, true );
      break;
  }
}

void IrProgrammatorProcess()
{
  if (irrecv.decode(&results))
  {
      if((results.value!=IR_COMMAND_SEPARATOR)&&(results.value!=0)&&(results.value!=IrLastCommandsValue[0]))
      {
        if(IrProgrammatorStep>0)
        {
          if(IrCommands[IrProgrammatorStep-1]==results.value) // To avoid pressing the same button as in previous command
          {
            irrecv.resume();
            return;            
          }
        }
        IrCommands[IrProgrammatorStep]=results.value;
        if(IrProgrammatorStep==IR_TOTAL_COMMANDS-1) // Was it the last command?
        {
          // Save all IR commands to EEPROM 
          for(int i=0; i<sizeof(IrCommands); i++)
          {
             EEPROM.write(i, *((byte*)&IrCommands + i) );
          }
          IsInIrProgrammMode = false;
          IrServoStep = IR_SERVO_STEP_DEFAULT;
          executeIrCommand(2); // Tell the user that we finished programm mode
        }else
        {
          executeIrCommand(++IrProgrammatorStep);
        }
      }            
      irrecv.resume();        
  }
}

void ProcessIrCommands()
{
  if(IrRemoteLastCommand==0) return; // The signal was not good enough to get the signal data
  
  // Check for known commands and executing them
  for(int i=0; i<IR_TOTAL_COMMANDS; i++)
  {
    if(IrCommands[i]==IrRemoteLastCommand)
    {
      executeIrCommand(i);
      return;
    }
  }
  // We recieved unknown command. Let's check if we need to enter in programmator mode. (Same button should be pressed in the following order: SHORT->LONG->SHORT
  if(( IrLastCommandsValue[0]==IrLastCommandsValue[1] )&&( IrLastCommandsValue[0]==IrLastCommandsValue[2] )
    &&( IrLastCommandsState[0]==2 || IrLastCommandsState[0]==1 )&&( IrLastCommandsState[1]==3 )&&( IrLastCommandsState[2]==2 || IrLastCommandsState[2]==1 ))
  {
    // Starting Programmator mode.
    IrServoStep = IR_SERVO_STEP_PROGRAM_MODE; // Maximun step for Programm mode, so that user will notice servo movement
    IsInIrProgrammMode = true;
    IrProgrammatorStep = 0;
    executeIrCommand(0); //  Executing command and waiting for user to press the button to save it.
  }
}

// Check if any IR remote buttons pressed
void CheckIrCommands()
{
// If we're in programm mode, jusst go 
  if(IsInIrProgrammMode)
  {
    IrProgrammatorProcess();
    return;
  }
  
  if (irrecv.decode(&results)) {
    // Button was pressed for LONG or SHORT time, but not VERY SHORT )
    if(results.value==IR_COMMAND_SEPARATOR)
    {
      if(IrRemoteButtonState<3)
      { // Set the IR remote button state
        IrRemoteButtonState++;
      }
      // Update last pressed button state
      IrLastCommandsState[0] = IrRemoteButtonState;
    }else
    {
      IrRemoteLastCommand = results.value;
      IrRemoteButtonState = 1;
       // Saving last pressed buttons and their state - we will use it to determine when to start IR programmator.
      for(int i=IR_LAST_COMMANDS_BUFFER_SIZE-1; i>0; i--)
      {
        IrLastCommandsState[i] = IrLastCommandsState[i-1];
        IrLastCommandsValue[i] = IrLastCommandsValue[i-1];
      }
      IrLastCommandsValue[0] = IrRemoteLastCommand;  
      IrLastCommandsState[0] = 2; // For programmator mode short and very short press of the button is equal.

      checkIrHit(); // Check if robot was hit by another robot
    }
    ProcessIrCommands(); 

    irrecv.resume();
  }
}


// Извлечение и обработка команд из входного буфера:
void processMessageBuffer()
{
    
// Если что пришло из буфера, добавляем.
  #ifdef USBCON   // For Leonardo (Romeo V2) board support
    while (Serial1.available() > 0)
    {
      MessageBuffer += (char)Serial1.read();
    }
  #else
    while (Serial.available() > 0)
    {
      MessageBuffer += (char)Serial.read();
    }
  #endif
// Размер буфера
  int bufferLength = MessageBuffer.length();

  if (bufferLength < MESSAGELENGTH)
  {
    return;
  }
//  Serial1.println("MessageBuffer: " + MessageBuffer);

  // Последовательно извлекаю из полученной строки команды длиной MESSAGELENGTH символов.
  // Что не уместилось пойдёт снова в MessageBuffer.
  int i = 0;
  String message;

  while (i < bufferLength-MESSAGELENGTH+1)
  { //Ищем первый символ не-цифру в шестнадцатиричом исчислении. Это будет комманда значит.
    if(((MessageBuffer[i]>='0')&&(MessageBuffer[i]<='9'))||((MessageBuffer[i]>='A')&&(MessageBuffer[i]<='F'))||((MessageBuffer[i]>='a')&&(MessageBuffer[i]<='f')))
    {  //Оказалась цифра
          #ifdef USBCON   // For Leonardo (Romeo V2) board support
             Serial1.print("#0008"); // цифра в шестнадцатеричном исчислении, вместо команды. Если несколько цифр подряд, то придет несколько сообщений.    
          #else
             Serial.print("#0008"); // цифра в шестнадцатеричном исчислении, вместо команды. Если несколько цифр подряд, то придет несколько сообщений.    
          #endif          

          i++;
    } else
    {  //Попалась не цифра
        message = MessageBuffer.substring(i, i+MESSAGELENGTH);
      
        processMessage( message );
        i += MESSAGELENGTH;        
    }
  }
  MessageBuffer = MessageBuffer.substring(i, bufferLength); 
}

boolean parseMessage(String message, String &command, int &value)
{
  if (message.length() != 5)
  {
    command = "";
    value = 0;
    return false;
  }
    
  command = (String)message[0];
  int digit1, digit2, digit3, digit4;
  if (hexCharToInt(message[1], digit1) && hexCharToInt(message[2], digit2) &&
    hexCharToInt(message[3], digit3) && hexCharToInt(message[4], digit4))
  {
    value = 4096 * digit1 + 256 * digit2 + 16 * digit3 + digit4;
    return true;
  }
  else
  {
    command = "";
    value = 0;
    return false;
  }
}

boolean hexCharToInt(char ch, int &value)
{
  if ((ch >= '0') && (ch <= '9'))
    value = ch - '0';
  else if ((ch >= 'A') && (ch <= 'F'))
    value = 10 + ch - 'A';
  else if ((ch >= 'a') && (ch <= 'f'))
    value = 10 + ch - 'a';
  else
  {
    value = 0;
    return false;
  }
  
  return true;
}

// Процедура обработки сообщения:
void processMessage(String message)
{
  if (ECHO_MODE)
  {
    #ifdef USBCON   // For Leonardo (Romeo V2) board support
      Serial1.print(message);
    #else
      Serial.print(message);
    #endif

  }  
  
  // Парсер команды:
  String command;
  int value;
  if (! parseMessage(message, command, value))
  {
    #ifdef USBCON   // For Leonardo (Romeo V2) board support
      Serial1.flush();
      Serial1.print("#0001"); // неверное сообщение – возникает, если сообщение не удалось разобрать на команды/событие и значение
    #else
      Serial.flush();
      Serial.print("#0001"); // неверное сообщение – возникает, если сообщение не удалось разобрать на команды/событие и значение
    #endif
    if (recordingRoboScript)
    {
      stopRecording();
    }
    return;
  }
  
  if (recordingRoboScript)
  {
    addActionToRoboScript(command, value);
  }
  else
  {
    executeAction(command, value, false);
  }
}

void stopRecording()
{
  actionStarted = false;
  customRoboScript[currentRoboScriptIndex].finalize();
  currentRoboScriptIndex = -1;
  recordingRoboScript = false;
  #ifdef USBCON   // For Leonardo (Romeo V2) board support
    Serial1.print("r0200");
  #else
    Serial.print("r0200");
  #endif

}

void addActionToRoboScript(String command, unsigned int value)
{
  if (command == "Z")
  {
    if (value == 0) // Z0000 - команда на окончание записи (Zero)
    {
      if (actionStarted)
      {
        #ifdef USBCON   // For Leonardo (Romeo V2) board support
          Serial1.print("#0004"); // неверная последовательность команд в РобоСкрипт
        #else
          Serial.print("#0004"); // неверная последовательность команд в РобоСкрипт
        #endif
        stopRecording();
        return;
      }
      
      recordingRoboScript = false;
      #ifdef USBCON   // For Leonardo (Romeo V2) board support
        Serial1.print("r0200");
      #else
        Serial.print("r0200");
      #endif      
    }
    else // Zxxxx - команда на выделение памяти для хранения РобоСкрипта (siZe)
    {
      int result = customRoboScript[currentRoboScriptIndex].initialize(value);
      if (result != ROBOSCRIPT_OK)
      {
        #ifdef USBCON   // For Leonardo (Romeo V2) board support
          Serial1.print("#0005"); // невозможно выделить необходимый объём памяти
        #else
          Serial.print("#0005"); // невозможно выделить необходимый объём памяти
        #endif
        
        stopRecording();
        return;
      }
    }
  }
  else if (command == "r")
  {
      #ifdef USBCON   // For Leonardo (Romeo V2) board support
        Serial1.print("#0003"); // недопустимая команда в РобоСкрипт
      #else
        Serial.print("#0003"); // недопустимая команда в РобоСкрипт
      #endif

    stopRecording();
    return;
  }
  else if (command == "W")
  {
    if (!actionStarted)
    {
      #ifdef USBCON   // For Leonardo (Romeo V2) board support
        Serial1.print("#0004"); // неверная последовательность команд в РобоСкрипт
      #else
        Serial.print("#0004"); // неверная последовательность команд в РобоСкрипт
      #endif

      stopRecording();
      return;
    }

    actionStarted = false;
    recordedAction.Delay = value;
    int result = customRoboScript[currentRoboScriptIndex].addAction(recordedAction);
    if (result != ROBOSCRIPT_OK)
    {
      #ifdef USBCON   // For Leonardo (Romeo V2) board support
        Serial1.print("#0006"); // выход за границы выделенной для РобоСкрипт памяти
      #else
        Serial.print("#0006"); // выход за границы выделенной для РобоСкрипт памяти
      #endif

      stopRecording();
      return;
    }
  }
  else
  {
    if (actionStarted)
    {
      #ifdef USBCON   // For Leonardo (Romeo V2) board support
        Serial1.print("#0004"); // неверная последовательность команд в РобоСкрипт
      #else
        Serial.print("#0004"); // неверная последовательность команд в РобоСкрипт
      #endif
      stopRecording();
      return;
    }
    
    actionStarted = true;
    recordedAction.Command = command[0];
    recordedAction.Value = value;
  }
}

// Function reads battery VCC, using Voltage divider.
// I used R1 = R2 = 10 kOhm
// Return value is VCC x 100 in HEX format (voltRatio have *100)
unsigned int VCCRead()
{
  return voltRatio*analogRead(batterySensorPin);
}


// Check if it's time for timer to send VCC of the Battery;
void CheckVCCTimer()
{
  if(!useVCCTimer){return;}
  
  unsigned int time = millis();
  if(timerTimeOld>time){timerTimeOld=time;} // millis() will overflow (go back to zero), after approximately 50 days.
  if(time-timerTimeOld>VCCTimerInterval)
  {
    sendMessageToRobot("~",VCCRead());
    timerTimeOld=time; //Start timer again
  }
}

// Starts the timer for every 'time' miliseconds to send VCC of the Battery to Serial1.
// TO switch off the timer call SetVCCTimer(0);
void SetVCCTimer(unsigned int time)
{
  if(time==0) // Stop Timer
  {
    useVCCTimer=false;
    return;
  }

  if(time<MINIMAL_INTERVAL_VALUE){time=MINIMAL_INTERVAL_VALUE;} // Set Minimum Interval Value 1 second.
  
  VCCTimerInterval = time;
  useVCCTimer = true;
  timerTimeOld = millis();
}

void executeAction(String command, unsigned int value, boolean inPlaybackMode)
{
  switch(command[0]) {  // Сейчас у нас односимвольные команды, но на случай развития команда определена как String
    case 'r':
    {
      unsigned int recording = value >> 8;
      unsigned int scriptIndex = value & 0xFF;
      if ((scriptIndex >= 0) && (scriptIndex < CUSTOM_ROBOSCRIPTS_COUNT))
      {
        currentRoboScriptIndex = scriptIndex;
        if (recording != 0)
        {
          recordingRoboScript = true;
        }
        else
        {
          customRoboScript[currentRoboScriptIndex].startExecution();
        }
      }
      break;
    }
    case 'W':
    case 'Z':
    {
      #ifdef USBCON   // For Leonardo (Romeo V2) board support
        Serial1.print("#0007"); // недопустимая команда вне РобоСкрипт
      #else
        Serial.print("#0007"); // недопустимая команда вне РобоСкрипт
      #endif

      return;
    }
    case 'L':
    case 'R':
    case 'G':  
    {
      // Команда двигателям:
      moveMotor(command, value);
      break;
    }
    case 'H':  
    case 'V':  
    {
      // Команда голове:
      moveHead(command, value);
      break;
    }
    case 'h':
    {
      // startTurn method requires a signed millisecond parameter.
      // And the command 'h' has value stored in centiseconds (1/100
      // of a second). That's why we need to multiply by 10.
      signed long sentiSeconds = (signed int)value;
      servoHeadHorizontal.startTurn(sentiSeconds * 10, true);
      break;
    }
    case 'v':
    {
      // startTurn method requires a signed millisecond parameter.
      // And the command 'v' has value stored in centiseconds (1/100
      // of a second). That's why we need to multiply by 10.
      signed long sentiSeconds = (signed int)value;
      servoHeadVertical.startTurn(sentiSeconds * 10, true);
      break;
    }
    case 'T':  
    {
      moveTail(value);
      break;
    }
    case 't':      
    {
      if (value != 0)
      {
        servoTail.startSwing(value, 250, 6, 70, 0.9, true);
      }
      break;
    }
    case 'n':     
    {
      if (value != 0)
      {
        servoHeadHorizontal.startSwing(value, 400, 2.5, 60, 0.75, true);
      }
      break;
    }
    case 'y':
    {
      if (value != 0)
      {
        servoHeadVertical.startSwing(value, 400, 2.5, 30, 0.8, true);
      }
      break;
    }
    case 'M':  
    {
      switch(value)
      {
        case 0x0101:
        {
          happyReflexStart();
          if (inPlaybackMode)
          {
            sendMessageToRobot(command, value);
          }
          break; 
        }
        case 0x0102:
        {
          const int ReadyToPlayVerticalDegree = 70;
          int verticalAmplitude = abs((ReadyToPlayVerticalDegree - servoHeadVertical.read()) * 2);
          boolean swingDirection = ReadyToPlayVerticalDegree > servoHeadVertical.read();
          servoHeadVertical.startSwing(2, 400, 0.25, verticalAmplitude, 1, swingDirection);
          servoHeadHorizontal.startSwing(2, 250, 3.5, 40, 0.8, true);
          servoTail.startSwing(value, 250, 6, 70, 0.9, true);
          readyToPlayReflexStart();
          if (inPlaybackMode)
          {
            sendMessageToRobot(command, value);
          }
          break; 
        }
        case 0x0103:
        {
          int verticalAmplitude = (servoHeadVertical.read() - servoHeadVerticalMinDegree) * 2;
          servoHeadVertical.startSwing(2, 6000, 0.25, verticalAmplitude, 1, false);
          servoHeadHorizontal.startSwing(2, 750, 2, 60, 0.6, true);
          sadReflexStart();
          if (inPlaybackMode)
          {
            sendMessageToRobot(command, value);
          }
          break;
        }
        case 0x0104:
        {
          angryReflexStart();
          if (inPlaybackMode)
          {
            sendMessageToRobot(command, value);
          }
          break;
        }
        case 0x0105:  
        {
          int musicTacts = 12;
          servoHeadVertical.startSwing(1, musicPeriod, musicTacts, 30, 1, true);
          servoHeadHorizontal.startSwing(2, musicPeriod * musicTacts / 1.5, 1.5, 50, 1, true);
          servoTail.startSwing(value, musicPeriod, musicTacts, 70, 1, true);
          musicReflexStart();
          break;
        }
        default:
        {
          sendMessageToRobot(command, value);
        }
      } // (M-command's switch)
      break;
    }
    case 'I':    
    {
      setHeadlightState(value);
      break;
    }
    case 's':    
    {
      // Команда выстрела пушке:
      irsend.sendSony(0xABC0, 16);
      irrecv.enableIRIn(); // (надо для повторной инициализации ИК-приёмника)
      break;
    }
    case '=':  // '=' checks the battery status, voltage will be send back as "~" command and voltage*100. i.e. for 5.02V the command will be "~01F6". 
    {          // You can use value to set timer to receive battery status. i.e. "=1000" will send battery status every 4096 millisecons. "=0000" will send battery status only once and will switch off the timer is it was set before.
      sendMessageToRobot("~",VCCRead());
      SetVCCTimer(value);
      break;
    }
    default:
    {
      if (inPlaybackMode)
      {
        sendMessageToRobot(command, value);
      }
      else
      {
        ////Serial1.flush();
        #ifdef USBCON   // For Leonardo (Romeo V2) board support
          Serial1.print("#0002"); // неизвестная команда
        #else
          Serial.print("#0002"); // неизвестная команда
        #endif        
        return;
      }
    }
  }//main switch
  #ifdef USBCON   // For Leonardo (Romeo V2) board support
    Serial1.print("#0000"); // Успешное выполнение команды, (потом можно удалить.)
  #else
    Serial.print("#0000"); // Успешное выполнение команды, (потом можно удалить.)
  #endif

}

// Поворот головы.
void moveHead(String plane, int degree)
{
  if (plane == "H") // (горизонтальная плоскость)
  {
    servoHeadHorizontal.write(degree);
  }
  else if (plane == "V") // (вертикальная плоскость)
  {
    servoHeadVertical.write(degree);
  }
}

// Поворот хвоста.
void moveTail(int degree)
{
  servoTail.write(degree);
}

// Управление двигателями.
void moveMotor(String side, int speed)
{
  bool directionPinValue = speed > 0 ? LOW : HIGH;
  
  if (speed < 0) {
    speed = - speed;
  }
 
  if (speed > 255) {
    speed = 255;
  }
  
  if (speed == 0) {
    directionPinValue = LOW; // это для режима отключения мотора
  }
  
  if ((side == "L") || (side == "G")) {
    digitalWrite(motorLeftDirectionPin, directionPinValue);
    analogWrite(motorLeftSpeedPin, speed);
  }
  
  if ((side == "R") || (side == "G")) {
    digitalWrite(motorRightDirectionPin, directionPinValue);
    analogWrite(motorRightSpeedPin, speed);
  }
}

// Управление фарами.
void setHeadlightState(int value)
{
  if (value == 0)
  {
    digitalWrite(lightPin, LOW);
  }
  else if (value == 1)
  {
    digitalWrite(lightPin, HIGH);
  }
}

// Process swinging on three servodrives.
void ProcessSwinging()
{
  // Tail swinging.
  if (servoTail.update())
  {
  }
  
  // Swinging head in horizontal plane.
  if (servoHeadHorizontal.update())
  {
  }

  // Swinging head in vertical plane.
  if (servoHeadVertical.update())
  {
  }
}

signed long sign(double value)
{
  if (value > 0) return 1;
  if (value < 0) return -1;
  return 0;
}

void happyReflexInitialize()
{
  happyReflex.initialize(3);
  RoboAction actionHappyFace;
  actionHappyFace.Command = 'M';
  actionHappyFace.Value = 2;
  actionHappyFace.Delay = 0;
  RoboAction actionWagTail;
  actionWagTail.Command = 't';
  actionWagTail.Value = 1;
  actionWagTail.Delay = 4000;
  RoboAction actionNormalFace = actionHappyFace;
  actionNormalFace.Value = 1;
  actionNormalFace.Delay = 0;
  happyReflex.addAction(actionHappyFace);
  happyReflex.addAction(actionWagTail);
  happyReflex.addAction(actionNormalFace);
}

void happyReflexRun()
{
  String command;
  int value;
  if (happyReflex.hasActionToExecute(command, value))
  {
    executeAction(command, value, true);
  }
}

void happyReflexStart()
{
  happyReflex.startExecution();
}

void readyToPlayReflexInitialize()
{
  const int kickSpeed = 192;
  const int kickDuration = 90;
  const int freezeDuration = 200;
  
  readyToPlayReflex.initialize(10);
  RoboAction actionSmile;
  actionSmile.Command = 'M';
  actionSmile.Value = 2;
  actionSmile.Delay = 200;
  RoboAction actionNormalFace = actionSmile;
  actionNormalFace.Value = 1;
  actionNormalFace.Delay = 0;
  RoboAction actionLeftKick;
  actionLeftKick.Command = 'L';
  actionLeftKick.Value = kickSpeed;
  actionLeftKick.Delay = kickDuration;
  RoboAction actionRightKick = actionLeftKick;
  actionRightKick.Command = 'R';
  RoboAction actionLeftStop;
  actionLeftStop.Command = 'L';
  actionLeftStop.Value = 0;
  actionLeftStop.Delay = freezeDuration;
  RoboAction actionRightStop = actionLeftStop;
  actionRightStop.Command = 'R';
  readyToPlayReflex.addAction(actionSmile);
  readyToPlayReflex.addAction(actionLeftKick);
  readyToPlayReflex.addAction(actionLeftStop);
  readyToPlayReflex.addAction(actionRightKick);
  readyToPlayReflex.addAction(actionRightStop);
  readyToPlayReflex.addAction(actionLeftKick);
  readyToPlayReflex.addAction(actionLeftStop);
  readyToPlayReflex.addAction(actionRightKick);
  actionRightStop.Delay = 2000;
  readyToPlayReflex.addAction(actionRightStop);
  readyToPlayReflex.addAction(actionNormalFace);
}

void readyToPlayReflexRun()
{
  String command;
  int value;
  if (readyToPlayReflex.hasActionToExecute(command, value))
  {
    executeAction(command, value, true);
  }
}

void readyToPlayReflexStart()
{
  readyToPlayReflex.startExecution();
}

void sadReflexInitialize()
{
  sadReflex.initialize(2);
  RoboAction actionSadFace;
  actionSadFace.Command = 'M';
  actionSadFace.Value = 3;
  actionSadFace.Delay = 5000;
  RoboAction actionNormalFace = actionSadFace;
  actionNormalFace.Value = 1;
  actionNormalFace.Delay = 0;
  sadReflex.addAction(actionSadFace);
  sadReflex.addAction(actionNormalFace);
}

void sadReflexRun()
{
  String command;
  int value;
  if (sadReflex.hasActionToExecute(command, value))
  {
    executeAction(command, value, true);
  }
}

void sadReflexStart()
{
  sadReflex.startExecution();
}

void angryReflexInitialize()
{
  angryReflex.initialize(5);
  RoboAction action;
  action.Command = 'M';
  action.Value = 4;
  action.Delay = 700;
  angryReflex.addAction(action);
  action.Command = 'G';
  action.Value = -192;
  action.Delay = 100;
  angryReflex.addAction(action);
  action.Value = 255;
  action.Delay = 20;
  angryReflex.addAction(action);
  action.Value = 0;
  action.Delay = 4000;
  angryReflex.addAction(action);
  action.Command = 'M';
  action.Value = 1;
  action.Delay = 0;
  angryReflex.addAction(action);
}

void angryReflexRun()
{
  String command;
  int value;
  if (angryReflex.hasActionToExecute(command, value))
  {
    executeAction(command, value, true);
  }
}

void angryReflexStart()
{
  angryReflex.startExecution();
}

void musicReflexInitialize()
{
  const int kickSpeed = 192;
  const int kickDuration = 90;
  const int freezeDuration = musicPeriod - kickDuration;
  
  musicReflex.initialize(26);
  
  RoboAction actionHappyFace;
  actionHappyFace.Command = 'M';
  actionHappyFace.Value = 2;
  actionHappyFace.Delay = 0;
  RoboAction actionNormalFace;
  actionNormalFace.Command = 'M';
  actionNormalFace.Value = 1;
  actionNormalFace.Delay = 0;

  RoboAction actionLeftKick;
  actionLeftKick.Command = 'L';
  actionLeftKick.Value = kickSpeed;
  actionLeftKick.Delay = kickDuration;
  RoboAction actionRightKick = actionLeftKick;
  actionRightKick.Command = 'R';

  RoboAction actionLeftBackKick = actionLeftKick;
  actionLeftBackKick.Value = -kickSpeed;
  RoboAction actionRightBackKick = actionRightKick;
  actionRightBackKick.Value = -kickSpeed;

  RoboAction actionLeftStop;
  actionLeftStop.Command = 'L';
  actionLeftStop.Value = 0;
  actionLeftStop.Delay = freezeDuration;
  RoboAction actionRightStop = actionLeftStop;
  actionRightStop.Command = 'R';

  musicReflex.addAction(actionHappyFace);
  
  musicReflex.addAction(actionLeftKick);
  musicReflex.addAction(actionLeftStop);
  musicReflex.addAction(actionRightKick);
  musicReflex.addAction(actionRightStop);
  musicReflex.addAction(actionLeftKick);
  musicReflex.addAction(actionLeftStop);
  
  musicReflex.addAction(actionLeftBackKick);
  musicReflex.addAction(actionLeftStop);
  musicReflex.addAction(actionRightBackKick);
  musicReflex.addAction(actionRightStop);
  musicReflex.addAction(actionLeftBackKick);
  musicReflex.addAction(actionLeftStop);

  musicReflex.addAction(actionRightKick);
  musicReflex.addAction(actionRightStop);
  musicReflex.addAction(actionLeftKick);
  musicReflex.addAction(actionLeftStop);
  musicReflex.addAction(actionRightKick);
  musicReflex.addAction(actionRightStop);
  
  musicReflex.addAction(actionRightBackKick);
  musicReflex.addAction(actionRightStop);
  musicReflex.addAction(actionLeftBackKick);
  musicReflex.addAction(actionLeftStop);
  musicReflex.addAction(actionRightBackKick);
  musicReflex.addAction(actionRightStop);
  
  musicReflex.addAction(actionNormalFace);
}

void musicReflexRun()
{
  String command;
  int value;
  if (musicReflex.hasActionToExecute(command, value))
  {
    executeAction(command, value, true);
  }
}

void musicReflexStart()
{
  musicReflex.startExecution();
}

void customRoboScriptRun()
{
  if (currentRoboScriptIndex >= 0)
  {
    String command;
    int value;
    if (customRoboScript[currentRoboScriptIndex].hasActionToExecute(command, value))
    {
      executeAction(command, value, true);
    }
  }
}

void sendMessageToRobot(String command, unsigned int value)
{
  String hexValue = String(value, HEX);
  hexValue.toUpperCase();
  while (hexValue.length() < 4) hexValue = "0" + hexValue;
  #ifdef USBCON   // For Leonardo (Romeo V2) board support
    Serial1.print(command + hexValue);
  #else
    Serial.print(command + hexValue);
  #endif
}

