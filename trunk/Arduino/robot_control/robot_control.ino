// -------------------------------------------------------------------------------------
// file = "robot_control.ino"
// company = "Dzakhov's jag"
// Copyright © Dmitry Dzakhov 2011
//   Скетч, предназначенный для исполнения роботом команд, полученных с уровня
//   Android-приложения и передачи сигналов, полученных от сенсоров робота, на
//   уровень Android-приложения.
// -------------------------------------------------------------------------------------

#include <Servo.h>

#include <avrpins.h>
#include <max3421e.h>
#include <usbhost.h>
#include <usb_ch9.h>
#include <Usb.h>
#include <usbhub.h>
#include <avr/pgmspace.h>
#include <address.h>
#include <adk.h>
#include <printhex.h>
#include <message.h>
#include <hexdump.h>
#include <parsetools.h>
#include <IRremote.h>

// Режим отладки.
boolean debugMode = true;

// Длина сообщения:
const int MESSAGELENGTH = 5;
// Неуместившаяся команда из буфера, прочитанного на предыдущей итерации чтения:
String previousBufferRest = "";

// Пин контроллера, использующийся для ИК-выстрела (цифровой выход).
int gunPin = 3;
// Пин, использующийся для фиксации попаданий (аналоговый вход).
int targetPin = A0;

// Пины контроллера для управления двигателями робота (цифровые выходы).
int motorLeftSpeedPin = 6;
int motorLeftDirectionPin = 7;
int motorRightSpeedPin = 5;
int motorRightDirectionPin = 4;

// Пины контроллера для управления сервоприводами робота (цифровые выходы)
int servoHorizontalPin = 2;
int servoVerticalPin = 9;

// Объекты для управления сервоприводами.
Servo servoHorizontal;
Servo servoVertical;

// Объекты управления USB-хостом.
USB Usb;
USBHub hub0(&Usb);
USBHub hub1(&Usb);
ADK adk(&Usb,"Dzakhov Dmitry",
            "RoboHead",
            "RoboHead Arduino Project",
            "1.0",
            "http://www.android.com",
            "0000000012345678");

// Объекты управления ИК-приёмником и ИК-передатчиком.
IRrecv irrecv(targetPin);
decode_results results;
IRsend irsend;

// Функция инициализации скетча:
void setup()
{
  // Установка скорости последовательного порта (для отладочной информации):
  serialBegin(115200);
  serialPrintln("Start...");

  // Инициализация USB:
  if (Usb.Init() == -1) {
    serialPrintln("OSCOKIRQ failed to assert");
  while(1);
  }

  // Инициализация ИК-приёмника:
  irrecv.enableIRIn(); // Start the receiver
  pinMode(gunPin, OUTPUT);
  digitalWrite(gunPin, LOW);

  // Инициализация двигателей (установка нулевой скорости):
  moveMotor("D", 0);

  // Установка горизонтального сервопривода в положение для установки телефона:
  pinMode(servoHorizontalPin, OUTPUT);
  servoHorizontal.attach(servoHorizontalPin);
  servoHorizontal.write(90);

  // Установка вертикального сервопривода в положение для установки телефона:
  pinMode(servoVerticalPin, OUTPUT);
  servoVertical.attach(servoVerticalPin);
  servoVertical.write(90);
  
  // Индикация режима отладки:
  indicate();
}

// Функция главного цикла:
void loop()
{
  Usb.Task();

  // Установка соединения с Andriod-приложением:
  if (adk.isReady() == false) {
    return;
  }

  // Проверка ИК-попадания в робота:
  if (checkIrHit())
  {
    // Инициализация структуры для передачи данных в Android-приложение:
    uint8_t message[5] = { 0x00 };
    uint16_t messageLength = sizeof(message);
    
    indicate();
    serialPrintln("Hit ");
    // Шлю сообщения Android-приложению об ИК-попадании в нас:
    // один раз 'h0001' и один раз 'h0000' (hit - попадание).
    // 'h0000' нужно чтобы сбросить значение в хэш-таблице 
    // сообщений Android-приложения. Там используется хэш-таблица
    // сообщений для исключения из обработки повторяющихся команд
    // с одинаковыми значениями.
    message[0] = (uint8_t)'h';
    message[1] = (uint8_t)'0';
    message[2] = (uint8_t)'0';
    message[3] = (uint8_t)'0';
    message[4] = (uint8_t)'1';
    adk.SndData(messageLength, message);
    message[4] = (uint8_t)'0';
    adk.SndData(messageLength, message);
  }
  
  // Инициализация структуры для приёма данных от Android-приложения:
  uint8_t buffer[5] = { 0x00 };
  uint16_t bufferLength = sizeof(buffer);

  // Чтение команд из входной очереди:
  adk.RcvData(&bufferLength, buffer);
  
  // Извлечение и обработка команд из входного буфера:
  processMessageBuffer(bufferLength, buffer);
}

// Проверка ИК-попадания в робота:
boolean checkIrHit()
{
  boolean result = false;
  if (irrecv.decode(&results)) {
    unsigned long hitValue;
    if (debugMode) {
      hitValue = 0xA90;
    }
    else {
      hitValue = 0xABC1;
    }
    result = (results.decode_type == SONY) && (results.value == hitValue);
    irrecv.resume();
  }
  return result;
}

// Извлечение и обработка команд из входного буфера:
void processMessageBuffer(uint16_t bufferSize, uint8_t *buffer)
{
  // Перевожу буфер в строку:
  String bufferText = "";
  for (int i = 0; i < bufferSize; i++)
  {
    bufferText += (char) buffer[i];
  }
  
  // Если от предыдущей итерации остался кусок команды, прибавляю его слева:
  bufferText = previousBufferRest + bufferText;
  previousBufferRest = "";
  
  // Последовательно извлекаю из полученной строки команды длиной MESSAGELENGTH символов.
  // Что не уместилось пойдёт в "довесок" (previousBufferRest) к следующей итереции.
  int i = 0;
  int bufferLength = bufferText.length();
  while (i < bufferLength)
  {
    int currentBufferLength = bufferLength - i;
    
    if (currentBufferLength >= MESSAGELENGTH)
    {
      String message = "";
      for (int j = 0; j < MESSAGELENGTH; j++)
      {
        message += bufferText[i + j];
      }

      // Обработка команды:      
      processMessage(message);
    }
    else
    {
      // "Довесок" к следующей итерации:
      previousBufferRest = "";
      for (int j = 0; j < currentBufferLength; j++)
      {
        previousBufferRest += bufferText[i + j];
      }
    }

    i += MESSAGELENGTH;
  }
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
  value = 
    4096 * hexCharToInt(message[1]) +
    256 * hexCharToInt(message[2]) +
    16 * hexCharToInt(message[3]) +
    hexCharToInt(message[4]);
  
  return true;
}

int hexCharToInt(char ch)
{
  if ((ch >= '0') && (ch <= '9'))
    return ch - '0';
  else if ((ch >= 'A') && (ch <= 'F'))
    return 10 + ch - 'A';
  else if ((ch >= 'a') && (ch <= 'f'))
    return 10 + ch - 'a';
  else
    return 0;
}

// Процедура обработки сообщения:
void processMessage(String message)
{
  // Парсер команды:
  String command;
  int value;
  if (! parseMessage(message, command, value))
  {
    indicate();
    serialFlush();
    serialPrintln("Wrong message.");
  }
  
  if ((command == "L") || (command == "R") || (command == "D")) 
  {
    // Команда двигателям:
    moveMotor(command, value);
    serialPrintln("MOVE: OK");
  }
  else if ((command == "H") || (command == "V"))
  {
    // Команда голове:
    moveHead(command, value);
    serialPrintln("HEAD: OK");
  }
  else if (command == "f")
  {
    // Команда выстрела пушке:
    irsend.sendSony(0xABC0, 16);
    irrecv.enableIRIn(); // (надо для повторной инициализации ИК-приёмника)
    serialPrintln("GUN: OK");
  }
  else
  {
    indicate();
    serialFlush();
    serialPrintln("Unknown command.");
  }
}

// Поворот головы.
void moveHead(String plane, int degree)
{
  if (plane == "H") // (горизонтальная плоскость)
  {
    servoHorizontal.write(degree);
  }
  else if (plane == "V") // (вертикальная плоскость)
  {
    servoVertical.write(degree);
  }
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
  
  if ((side == "L") || (side == "D")) {
    digitalWrite(motorLeftDirectionPin, directionPinValue);
    analogWrite(motorLeftSpeedPin, speed);
  }
  
  if ((side == "R") || (side == "D")) {
    digitalWrite(motorRightDirectionPin, directionPinValue);
    analogWrite(motorRightSpeedPin, speed);
  }
}

// Инициализация последовательного порта.
void serialBegin(int serialSpeed) {
  if (debugMode) {
    Serial.begin(115200);
  }
}

// Вывод строки в последовательный порт.
void serialPrintln(String outputText) {
  if (debugMode) {
    Serial.println(outputText);
  }
}

// Очистка входного буфера последовательного порта.
void serialFlush() {
  if (debugMode) {
    Serial.flush();
  }
}

// Функция для отладки. Индикация какого-либо события светодиодом,
// подключенным вместо ИК-пушки.
void indicate() {
  if (debugMode) {
    digitalWrite(gunPin, HIGH);
    delay(30);
    digitalWrite(gunPin, HIGH);
    delay(30);
    digitalWrite(gunPin, HIGH);
    delay(30);
    digitalWrite(gunPin, LOW);
  }
}

