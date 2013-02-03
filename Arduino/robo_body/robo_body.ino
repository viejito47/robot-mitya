// -------------------------------------------------------------------------------------
// file = "robot_body.ino"
// company = "Dzakhov's jag"
// Copyright © Dmitry Dzakhov 2011
//   Скетч, предназначенный для исполнения роботом команд, полученных с уровня
//   Android-приложения и передачи сигналов, полученных от сенсоров робота, на
//   уровень Android-приложения.
// -------------------------------------------------------------------------------------

#include <Servo.h>
#include <IRremote.h>
#include <Swinger.h>
#include <RoboScript.h>
#include <EEPROM.h>
  
// Эхо-режим. Возврат всех полученных сообщений.
const boolean ECHO_MODE = false;

// Длина сообщения:
const int MESSAGELENGTH = 5;
// Текущий необработанный текст из буфера, прочитанного на предыдущей итерации чтения:
String MessageBuffer = "";

// Пин контроллера, использующийся для ИК-выстрела (цифровой выход).
int gunPin = 3;
// Пин, использующийся для фиксации попаданий (аналоговый вход).
int targetPin = A0;
// Pin for measuring battery voltage (analog in).
int batterySensorPin = A5;

// Пины контроллера для управления двигателями робота (цифровые выходы).
int motorLeftSpeedPin = 5;
int motorLeftDirectionPin = 4;
int motorRightSpeedPin = 6;
int motorRightDirectionPin = 7;

// Пины контроллера для управления сервоприводами робота (цифровые выходы).
int servoHeadHorizontalPin = 9;
int servoHeadVerticalPin = 10;
int servoTailPin = 11;

// Пин управления фарами.
int lightPin = 13;

// Objects to control servodrives.
Servo servoHeadHorizontal;
Servo servoHeadVertical;
Servo servoTail;

int servoHeadHorizontalMinDegree = 0;
int servoHeadHorizontalMaxDegree = 180;
int servoHeadVerticalMinDegree = 0;
int servoHeadVerticalMaxDegree = 90;
int servoTailMinDegree = 10;   // Not ZERO, because in the boundary position servo is vibrating a lot
int servoTailMaxDegree = 170;   // Not 180, because in the boundary position servo is vibrating a lot

int servoHeadCurrentHorizontalDegree;
int servoHeadCurrentVerticalDegree;
int servoTailCurrentDegree;

Swinger tailSwinger; // object to control tail swinging
Swinger headVerticalSwinger; // object to control head vertical swinging
Swinger headHorizontalSwinger; // object to control head horizontal swinging
const int musicPeriod = 535;

// Объекты управления ИК-приёмником и ИК-передатчиком.
IRrecv irrecv(targetPin);
decode_results results;
IRsend irsend;

// Программа для робота: РобоСкрипт.
RoboScript readyToPlayReflex;
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
const long MINIMAL_INTERVAL_VALUE = 1000; // Minimum interval in milliseconds for Timer

// Constants for Voltage Devidor
const float voltPerUnit = 0.004883; // 5V/1024 values = 0,004883 V/value
const float dividerRatio = 2; // (R1+R2)/R2
const float voltRatio = voltPerUnit * dividerRatio*100; // This coefficient we will use

const int IR_MOVE_SPEED = 255; // Speed set to maximum, when control from IR remote
const int IR_SERVO_STEP_DEFAULT = 5; // Step for changing servo head (Horizontal and Vertical) position, when controling from IR remote
const int IR_SERVO_STEP_PROGRAM_MODE = 180; // Step for changing servo head (Horizontal and Vertical) position, when in programm mode. (Set to Maximum, so that user will 100% notice it.
int IrServoStep = IR_SERVO_STEP_DEFAULT; // Step for rotating head from remote
const int IR_TOTAL_COMMANDS = 17; // Total Number of commands, that can be send by IR control
unsigned long IrCommands[IR_TOTAL_COMMANDS]; // All command from remote control

unsigned long IrRemoteLastCommand = 0; // Last command received by IR receiver (except IR_COMMAND_SEPARATOR)
const unsigned long IR_COMMAND_SEPARATOR = 0xFFFFFFFF;
byte IrRemoteButtonState = 0; // 0 = Boot state. No buttons were ever pressed,
                              // 1 = Button pressed, we only received the code of the button - very short press.
                              // 2 = Button pressed for short time, we received first IR_COMMAND_SEPARATOR for the first time after IrRemoteLastCommand received.
                              // 3 = Button pressed for long time, we received IR_COMMAND_SEPARATOR for more then one time after IrRemoteLastCommand received.

const int IR_LAST_COMMANDS_BUFFER_SIZE = 4; // Last commands and their state to determine when to enter Programmator mode
byte IrLastCommandsState[IR_LAST_COMMANDS_BUFFER_SIZE]={0}; // Last command state (LONG, SHORT or VERY SHORT press)
unsigned long IrLastCommandsValue[IR_LAST_COMMANDS_BUFFER_SIZE]={0}; // Last command value

boolean IsInIrProgrammMode = false; // Are we in IR command programm mode?
byte IrProgrammatorStep = 0; // Number of command we're processing now

// Функция инициализации скетча:
void setup()
{
  // Установка скорости последовательного порта (для отладочной информации):
  Serial.begin(9600);

  // Инициализация ИК-приёмника:
  irrecv.enableIRIn(); // Start the receiver
  pinMode(gunPin, OUTPUT);
  digitalWrite(gunPin, LOW);

  // Инициализация двигателей (установка нулевой скорости):
  moveMotor("G", 0);

  // Установка горизонтального сервопривода в положение для установки телефона:
  pinMode(servoHeadHorizontalPin, OUTPUT);
  servoHeadHorizontal.attach(servoHeadHorizontalPin);
  moveHead("H", 90);

  // Установка вертикального сервопривода в положение для установки телефона:
  pinMode(servoHeadVerticalPin, OUTPUT);
  servoHeadVertical.attach(servoHeadVerticalPin);
  moveHead("V", 90);

  // Установка хвоста пистолетом:
  pinMode(servoTailPin, OUTPUT);
  servoTail.attach(servoTailPin);
  moveTail(90);
  
  pinMode(lightPin, OUTPUT);
  digitalWrite(lightPin, LOW);
  
  readyToPlayReflexInitialize();
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

  readyToPlayReflexRun();
  angryReflexRun();
  musicReflexRun();
  
  customRoboScriptRun();
}

// Функция главного цикла:
void loop()
{
  processMessageBuffer(); // Receive all messages and process them
  processEvents();        
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
      // один раз 'h0001' и один раз 'h0000' (hit - попадание).
      // 'h0000' нужно чтобы сбросить значение в хэш-таблице 
      // сообщений Android-приложения. Там используется хэш-таблица
      // сообщений для исключения из обработки повторяющихся команд
      // с одинаковыми значениями.
      Serial.print("h0001");
      Serial.print("h0000");
    }
}

void executeIrCommand(int cmd)
{
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
      headHorizontalSwinger.startSwing(90, 1, 400, 2.5, 60, 0.75, true);      
      break;
    case 10: //yes
      headVerticalSwinger.startSwing(60, 1, 400, 2.5, 30, 0.8, true);
      break;
    case 11: //tail
      tailSwinger.startSwing(90, 1, 250, 6, 70, 0.9, true);
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
  // We recieved unknown command. Let's check if we need to enter in programmator mode. (Same button should be pressed in the following order: LONG->SHORT->LONG->SHORT
  if((IrLastCommandsValue[0]==IrLastCommandsValue[1])&&(IrLastCommandsValue[0]==IrLastCommandsValue[2])&&(IrLastCommandsValue[0]==IrLastCommandsValue[3])
    &&(IrLastCommandsState[0]==2)&&(IrLastCommandsState[1]==3)&&(IrLastCommandsState[2]==2)&&(IrLastCommandsState[1]==3))
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
  while (Serial.available() > 0)
  {
    MessageBuffer += (char)Serial.read();
  }
// Размер буфера
  int bufferLength = MessageBuffer.length();

  if (bufferLength < MESSAGELENGTH)
  {
    return;
  }
//  Serial.println("MessageBuffer: " + MessageBuffer);

  // Последовательно извлекаю из полученной строки команды длиной MESSAGELENGTH символов.
  // Что не уместилось пойдёт снова в MessageBuffer.
  int i = 0;
  String message;

  while (i < bufferLength-MESSAGELENGTH+1)
  { //Ищем первый символ не-цифру в шестнадцатиричом исчислении. Это будет комманда значит.
    if(((MessageBuffer[i]>='0')&&(MessageBuffer[i]<='9'))||((MessageBuffer[i]>='A')&&(MessageBuffer[i]<='F'))||((MessageBuffer[i]>='a')&&(MessageBuffer[i]<='f')))
    {  //Оказалась цифра
          Serial.print("#0008"); // цифра в шестнадцатеричном исчислении, вместо команды. Если несколько цифр подряд, то придет несколько сообщений.    
          i++;
    } else
    {  //Попалась не цифра
        message = MessageBuffer.substring(i, i+MESSAGELENGTH);
//        Serial.println("message: " + message);
      
        processMessage( message );
        i += MESSAGELENGTH;        
    }
  }
  MessageBuffer = MessageBuffer.substring(i, bufferLength); 
//  Serial.println("NewMessageBuffer: " + MessageBuffer);
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
    Serial.print(message);
  }  
  
  // Парсер команды:
  String command;
  int value;
  if (! parseMessage(message, command, value))
  {
    Serial.flush();
//Serial.print(message); // неверное сообщение – возникает, если сообщение не удалось разобрать на команды/событие и значение
    Serial.print("#0001"); // неверное сообщение – возникает, если сообщение не удалось разобрать на команды/событие и значение
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
  Serial.print("r0200");
}

void addActionToRoboScript(String command, unsigned int value)
{
  if (command == "Z")
  {
    if (value == 0) // Z0000 - команда на окончание записи (Zero)
    {
      if (actionStarted)
      {
        Serial.print("#0004"); // неверная последовательность команд в РобоСкрипт
        stopRecording();
        return;
      }
      
      recordingRoboScript = false;
      Serial.print("r0200");
    }
    else // Zxxxx - команда на выделение памяти для хранения РобоСкрипта (siZe)
    {
      int result = customRoboScript[currentRoboScriptIndex].initialize(value);
      if (result != ROBOSCRIPT_OK)
      {
        Serial.print("#0005"); // невозможно выделить необходимый объём памяти
        stopRecording();
        return;
      }
    }
  }
  else if (command == "r")
  {
    Serial.print("#0003"); // недопустимая команда в РобоСкрипт
    stopRecording();
    return;
  }
  else if (command == "W")
  {
    if (!actionStarted)
    {
      Serial.print("#0004"); // неверная последовательность команд в РобоСкрипт
      stopRecording();
      return;
    }

    actionStarted = false;
    recordedAction.Delay = value;
    int result = customRoboScript[currentRoboScriptIndex].addAction(recordedAction);
    if (result != ROBOSCRIPT_OK)
    {
      Serial.print("#0006"); // выход за границы выделенной для РобоСкрипт памяти
      stopRecording();
      return;
    }
  }
  else
  {
    if (actionStarted)
    {
      Serial.print("#0004"); // неверная последовательность команд в РобоСкрипт
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

// Starts the timer for every 'time' miliseconds to send VCC of the Battery to Serial.
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
      Serial.print("#0007"); // недопустимая команда вне РобоСкрипт
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
    case 'T':  
    {
      moveTail(value);
      break;
    }
    case 't':      
    {
      if (value != 0)
      {
        tailSwinger.startSwing(servoTailCurrentDegree, value, 250, 6, 70, 0.9, true);
      }
      break;
    }
    case 'n':     
    {
      if (value != 0)
      {
        headHorizontalSwinger.startSwing(servoHeadCurrentHorizontalDegree, value, 400, 2.5, 60, 0.75, true);
      }
      break;
    }
    case 'y':
    {
      if (value != 0)
      {
        headVerticalSwinger.startSwing(servoHeadCurrentVerticalDegree, value, 400, 2.5, 30, 0.8, true);
      }
      break;
    }
    case 'M':  
    {
      switch(value)
      {
        case 0x0102:
        {
          const int ReadyToPlayVerticalDegree = 70;
          int verticalAmplitude = abs((ReadyToPlayVerticalDegree - servoHeadCurrentVerticalDegree) * 2);
          boolean swingDirection = ReadyToPlayVerticalDegree > servoHeadCurrentVerticalDegree;
          headVerticalSwinger.startSwing(servoHeadCurrentVerticalDegree, 2, 400, 0.25, verticalAmplitude, 1, swingDirection);
          headHorizontalSwinger.startSwing(servoHeadCurrentHorizontalDegree, 2, 250, 3.5, 40, 0.8, true);
          tailSwinger.startSwing(servoTailCurrentDegree, value, 250, 6, 70, 0.9, true);
          readyToPlayReflexStart();
          if (inPlaybackMode)
          {
            sendMessageToRobot(command, value);
          }
         break; 
        }
       case 0x0103:
       {
          int verticalAmplitude = (servoHeadCurrentVerticalDegree - servoHeadVerticalMinDegree) * 2;
          headVerticalSwinger.startSwing(servoHeadCurrentVerticalDegree, 2, 6000, 0.25, verticalAmplitude, 1, false);
          headHorizontalSwinger.startSwing(servoHeadCurrentHorizontalDegree, 2, 750, 2, 60, 0.6, true);
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
          headVerticalSwinger.startSwing(45, 1, musicPeriod, musicTacts, 30, 1, true);
          headHorizontalSwinger.startSwing(90, 2, musicPeriod * musicTacts / 1.5, 1.5, 50, 1, true);
          tailSwinger.startSwing(servoTailCurrentDegree, value, musicPeriod, musicTacts, 70, 1, true);
          musicReflexStart();
          /*if (inPlaybackMode)
          {
            sendMessageToRobot(command, value);
          }*/
          break;
       }
      }
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
    case '-':  // '-' = Check the battery status, voltage will be send back as "~" command, and voltage*100. i.e. for 5.02V the command will be "~01F6". 
    {          // You can use value to set timer to receive battery status. i.e. "-1000" will send battery status every 4096 millisecons. "-0000" will send battery status only once and will switch off the timer is it was set before.
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
        ////Serial.flush();
        Serial.print("#0002"); // неизвестная команда
        return;
      }
    }
  }//main switch
  Serial.print("#0000"); // Успешное выполнение команды, (потом можно удалить.)
}

int correctDegree(int degree, int minValue, int maxValue)
{
  if (degree < minValue)
  {
    return minValue;
  }
  
  if (degree > maxValue)
  {
    return maxValue;
  }
  
  return degree;
}

// Поворот головы.
void moveHead(String plane, int degree)
{
  if (plane == "H") // (горизонтальная плоскость)
  {
    servoHeadCurrentHorizontalDegree = correctDegree(degree, servoHeadHorizontalMinDegree, servoHeadHorizontalMaxDegree);
    servoHeadHorizontal.write(servoHeadCurrentHorizontalDegree);
  }
  else if (plane == "V") // (вертикальная плоскость)
  {
    servoHeadCurrentVerticalDegree = correctDegree(degree, servoHeadVerticalMinDegree, servoHeadVerticalMaxDegree);
    servoHeadVertical.write(servoHeadCurrentVerticalDegree);
  }
}

// Поворот хвоста.
void moveTail(int degree)
{
  servoTailCurrentDegree = correctDegree(degree, servoTailMinDegree, servoTailMaxDegree);
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
  int degree;
  // Tail swinging.
  if (tailSwinger.swing(degree))
  {
    moveTail(degree);
  }
  
  // Swinging head in horizontal plane.
  if (headHorizontalSwinger.swing(degree))
  {
    moveHead("H", degree);
  }

  // Swinging head in vertical plane.
  if (headVerticalSwinger.swing(degree))
  {
    moveHead("V", degree);
  }
}

signed long sign(double value)
{
  if (value > 0) return 1;
  if (value < 0) return -1;
  return 0;
}

void readyToPlayReflexInitialize()
{
  const int kickSpeed = 192;
  const int kickDuration = 90;
  const int freezeDuration = 200;
  
  readyToPlayReflex.initialize(8);
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
  readyToPlayReflex.addAction(actionLeftKick);
  readyToPlayReflex.addAction(actionLeftStop);
  readyToPlayReflex.addAction(actionRightKick);
  readyToPlayReflex.addAction(actionRightStop);
  readyToPlayReflex.addAction(actionLeftKick);
  readyToPlayReflex.addAction(actionLeftStop);
  readyToPlayReflex.addAction(actionRightKick);
  readyToPlayReflex.addAction(actionRightStop);
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

void angryReflexInitialize()
{
  angryReflex.initialize(3);
  RoboAction action;
  action.Command = 'G';
  action.Value = -192;
  action.Delay = 100;
  angryReflex.addAction(action);
  action.Value = 255;
  action.Delay = 20;
  angryReflex.addAction(action);
  action.Value = 0;
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
  Serial.print(command + hexValue);
}

