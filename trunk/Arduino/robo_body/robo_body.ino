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

// Объекты для управления сервоприводами.
Servo servoHeadHorizontal;
Servo servoHeadVertical;
Servo servoTail;

int servoHeadHorizontalMinDegree = 0;
int servoHeadHorizontalMaxDegree = 180;
int servoHeadVerticalMinDegree = 0;
int servoHeadVerticalMaxDegree = 90;
int servoTailMinDegree = 10;
int servoTailMaxDegree = 170;

int servoHeadCurrentHorizontalDegree;
int servoHeadCurrentVerticalDegree;
int servoTailCurrentDegree;

Swinger tailSwinger; // объект для виляния хвостом
Swinger yesSwinger; // объект для кивания "да"
Swinger noSwinger; // объект для мотания "нет"
Swinger blueVerticalSwinger;
Swinger blueHorizontalSwinger;
Swinger readyToPlayVerticalSwinger;
Swinger readyToPlayHorizontalSwinger;
Swinger musicVerticalSwinger;
Swinger musicHorizontalSwinger;
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
}

void processEvents()
{
   // Проверка ИК-попадания в робота:
  if (checkIrHit())
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

  wagTail();
  showNo(); 
  showYes();
  showBlue();
  showReadyToPlay();
  readyToPlayReflexRun();
  angryReflexRun();
  showMusic();
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
  boolean result = false;
  if (irrecv.decode(&results)) {
    unsigned long hitValue;
/*    if (debugMode) {
      hitValue = 0xA90;
    }
    else {
      hitValue = 0xABC1;
    }*/
    hitValue = 0xA90;
    result = (results.decode_type == SONY) && (results.value == hitValue);
    irrecv.resume();
  }
  return result;
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

// Function reads battery VCC, using bandgap reference. 
// Return value is VCC x 100 in HEX format
// To make it work, you should fix Arduino library (allow to read from pins > 7 ):
// \hardware\arduino\cores\arduino\wiring_analog.c   (path given for Arduino 1.0.3)
// Find string: ADMUX = (analog_reference << 6) | (pin & 0x07); 
// And replace with ADMUX = (analog_reference << 6) | (pin & 0x0f);

// For my board Freeduino bandgap reference is 2,56 V. If in your is different usually 1,1 or 1,05 for better resualts
#define BANDGAP_REF 256   // For 2,56 V.   TODO: Move this to settings.h which will be excluded from the SVN sync, because every device have different values for it.   (Also move pin defs to settings.h)
unsigned int VCCRead()
{
  uint16_t raw_bandgap = 0;      // internal bandgap value
  float volt_battery = 0.0;  
    // Чтение напряжения батареи
  analogReference(DEFAULT);                   // Use Vcc as AREF
  raw_bandgap = analogRead(14);               // idle reading after changing AREF (manual ref: 23.5.2)
  raw_bandgap = analogRead(14);               // Get internal bandgap
  if(raw_bandgap==0) return 0; // We don't wont to receive devision by zero error in any case. 
  volt_battery = (BANDGAP_REF * 1024) / raw_bandgap;  // calculate Vcc   // TODO: Make with out float volt_battery. (if make long volt_battery  - return value is 0)
  return volt_battery*100;
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
        noSwinger.startSwing(servoHeadCurrentHorizontalDegree, value, 400, 2.5, 60, 0.75, true);
      }
      break;
    }
    case 'y':
    {
      if (value != 0)
      {
        yesSwinger.startSwing(servoHeadCurrentVerticalDegree, value, 400, 2.5, 30, 0.8, true);
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
          readyToPlayVerticalSwinger.startSwing(servoHeadCurrentVerticalDegree, 2, 400, 0.25, verticalAmplitude, 1, swingDirection);
          readyToPlayHorizontalSwinger.startSwing(servoHeadCurrentHorizontalDegree, 2, 250, 3.5, 40, 0.8, true);
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
          blueVerticalSwinger.startSwing(servoHeadCurrentVerticalDegree, 2, 6000, 0.25, verticalAmplitude, 1, false);
          blueHorizontalSwinger.startSwing(servoHeadCurrentHorizontalDegree, 2, 750, 2, 60, 0.6, true);
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
          musicVerticalSwinger.startSwing(45, 1, musicPeriod, musicTacts, 30, 1, true);
          musicHorizontalSwinger.startSwing(90, 2, musicPeriod * musicTacts / 1.5, 1.5, 50, 1, true);
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
    case '-':  // '-' = Check the battery status, voltage will be send back as "~" command, and voltage*100. i.e. for 5.02V the command will be "~01F6"  
    {
      sendMessageToRobot("~",VCCRead());
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

// Виляние хвостом.
void wagTail()
{
  int degree;
  if (tailSwinger.swing(degree))
  {
    moveTail(degree);
  }
}

void showNo()
{
  int degree;
  if (noSwinger.swing(degree))
  {
    moveHead("H", degree);
  }
}

void showYes()
{
  int degree;
  if (yesSwinger.swing(degree))
  {
    moveHead("V", degree);
  }
}

void showBlue()
{
  int degree;
  if (blueVerticalSwinger.swing(degree))
  {
    moveHead("V", degree);
  }
  if (blueHorizontalSwinger.swing(degree))
  {
    moveHead("H", degree);
  }
}

void showReadyToPlay()
{
  int degree;
  if (readyToPlayVerticalSwinger.swing(degree))
  {
    moveHead("V", degree);
  }
  if (readyToPlayHorizontalSwinger.swing(degree))
  {
    moveHead("H", degree);
  }
  if (tailSwinger.swing(degree))
  {
    moveTail(degree);
  }
}

void showMusic()
{
  int degree;
  if (musicVerticalSwinger.swing(degree))
  {
    moveHead("V", degree);
  }
  if (musicHorizontalSwinger.swing(degree))
  {
    moveHead("H", degree);
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

