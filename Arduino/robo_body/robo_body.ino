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

// Длина сообщения:
const int MESSAGELENGTH = 5;
// Неуместившаяся команда из буфера, прочитанного на предыдущей итерации чтения:
String previousBufferRest = "";

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

// Объекты управления ИК-приёмником и ИК-передатчиком.
IRrecv irrecv(targetPin);
decode_results results;
IRsend irsend;

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
  moveMotor("D", 0);

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
}

// Функция главного цикла:
void loop()
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
  
  String bufferText = "";
  while (Serial.available() > 0)
  {
    char ch = Serial.read();
    bufferText += ch;
  }
  
  processMessageBuffer(bufferText);
  
  wagTail();
  showNo();
  showYes();
  showBlue();
  showReadyToPlay();
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
void processMessageBuffer(String bufferText)
{
  // Если от предыдущей итерации остался кусок команды, прибавляю его слева:
  bufferText = previousBufferRest + bufferText;
  if (bufferText.length() < MESSAGELENGTH)
  {
    previousBufferRest = bufferText;
    return;
  }
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
  // Парсер команды:
  String command;
  int value;
  if (! parseMessage(message, command, value))
  {
    //Serial.flush();
    Serial.print("E0001"); // неверное сообщение – возникает, если сообщение не удалось разобрать на команды/событие и значение
    return;
  }
  
  if ((command == "L") || (command == "R") || (command == "D")) 
  {
    // Команда двигателям:
    moveMotor(command, value);
  }
  else if ((command == "H") || (command == "V"))
  {
    // Команда голове:
    moveHead(command, value);
  }
  else if (command == "T")
  {
    moveTail(value);
  }
  else if (command == "t")
  {
    if (value != 0)
    {
      tailSwinger.startSwing(servoTailCurrentDegree, value, 250, 6, 70, 0.9, true);
    }
  }
  else if (command == "n")
  {
    if (value != 0)
    {
      noSwinger.startSwing(servoHeadCurrentHorizontalDegree, value, 400, 2.5, 60, 0.75, true);
    }
  }
  else if (command == "y")
  {
    if (value != 0)
    {
      yesSwinger.startSwing(servoHeadCurrentVerticalDegree, value, 400, 2.5, 30, 0.8, true);
    }
  }
  else if ((command == "F") && (value == 0x0102))
  {
    const int ReadyToPlayVerticalDegree = 50;
    int verticalAmplitude = abs((ReadyToPlayVerticalDegree - servoHeadCurrentVerticalDegree) * 2);
    boolean swingDirection = ReadyToPlayVerticalDegree > servoHeadCurrentVerticalDegree;
    readyToPlayVerticalSwinger.startSwing(servoHeadCurrentVerticalDegree, 2, 400, 0.25, verticalAmplitude, 1, swingDirection);
    readyToPlayHorizontalSwinger.startSwing(servoHeadCurrentHorizontalDegree, 2, 250, 3.5, 40, 0.8, true);
    tailSwinger.startSwing(servoTailCurrentDegree, value, 250, 6, 70, 0.9, true);
  }
  else if ((command == "F") && (value == 0x0103))
  {
    int verticalAmplitude = (servoHeadCurrentVerticalDegree - servoHeadVerticalMinDegree) * 2;
    blueVerticalSwinger.startSwing(servoHeadCurrentVerticalDegree, 2, 6000, 0.25, verticalAmplitude, 1, false);
    blueHorizontalSwinger.startSwing(servoHeadCurrentHorizontalDegree, 2, 750, 2, 60, 0.6, true);
  }
  else if (command == "I")
  {
    setHeadlightState(value);
  }
  else if (command == "f")
  {
    // Команда выстрела пушке:
    irsend.sendSony(0xABC0, 16);
    irrecv.enableIRIn(); // (надо для повторной инициализации ИК-приёмника)
  }
  else
  {
    //Serial.flush();
    Serial.print("E0002"); // неизвестная команда
    return;
  }
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
  
  if ((side == "L") || (side == "D")) {
    digitalWrite(motorLeftDirectionPin, directionPinValue);
    analogWrite(motorLeftSpeedPin, speed);
  }
  
  if ((side == "R") || (side == "D")) {
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

signed long sign(double value)
{
  if (value > 0) return 1;
  if (value < 0) return -1;
  return 0;
}

