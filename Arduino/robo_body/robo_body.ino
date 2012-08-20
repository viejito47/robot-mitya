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

// Переменные виляния хвостом. Центральная точка это 90 градусов.
signed long waggingTailStartMillis; // момент начала виляния хвостом
int waggingTailStartDegree; // угол с которого начинается виляние
boolean waggingTail = false; // признак виляния хвостом
int waggingTailMode; // режим виляния хвостом (1 - угол меняется по синусу от времени, 2 - линейно от времени)
signed long waggingTailPeriod = 250; // период виляния в милисекундах
double waggingTailIterations = 6; // количество тактов виляния хвостом
signed long waggingTailAmplitude = 70; // амплитуда колебаний хвостом
// (для амплитуды в 70 градусов колебания происходят вокруг 90 градусов - от 55 до 125 градусов)
double waggingTailAmplitudeCoefficient = 0.9; // во сколько раз снизится амплитуда за период

// Переменные мотания головой. Центральная точка это 90 градусов.
signed long waggingNoStartMillis; // момент начала мотания головой
int waggingNoStartDegree; // угол с которого начинается мотание головой
boolean waggingNo = false; // признак мотания головой
int waggingNoMode; // режим мотания головой (1 - угол меняется по синусу от времени, 2 - линейно от времени)
signed long waggingNoPeriod = 400; // период мотания головой в милисекундах
double waggingNoIterations = 2.5; // количество тактов мотания головой
signed long waggingNoAmplitude = 60; // амплитуда мотания головой
double waggingNoAmplitudeCoefficient = 0.75; // во сколько раз снизится амплитуда за период

// Переменные кивания головой. Центральная точка это 90 градусов.
signed long waggingYesStartMillis; // момент начала кивания головой
int waggingYesStartDegree; // угол с которого начинается кивание головой
boolean waggingYes = false; // признак кивания головой
int waggingYesMode; // режим кивания головой (1 - угол меняется по синусу от времени, 2 - линейно от времени)
signed long waggingYesPeriod = 400; // период кивания головой в милисекундах
double waggingYesIterations = 2.5; // количество тактов кивания головой
signed long waggingYesAmplitude = 30; // амплитуда кивания головой
double waggingYesAmplitudeCoefficient = 0.8; // во сколько раз снизится амплитуда за период

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
  wagNo();
  wagYes();
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
    if ((value != 0) && (! waggingTail))
    {
      waggingTailMode = value;
      waggingTail = true;
      waggingTailStartMillis = millis();
      waggingTailStartDegree = servoTailCurrentDegree;
    }
  }
  else if (command == "n")
  {
    if ((value != 0) && (! waggingNo))
    {
      waggingNoMode = value;
      waggingNo = true;
      waggingNoStartMillis = millis();
      waggingNoStartDegree = servoHeadCurrentHorizontalDegree;
    }
  }
  else if (command == "y")
  {
    if ((value != 0) && (! waggingYes))
    {
      waggingYesMode = value;
      waggingYes = true;
      waggingYesStartMillis = millis();
      waggingYesStartDegree = servoHeadCurrentVerticalDegree;
    }
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

signed long wagSomething(
  signed long waggingStartMillis, 
  int waggingStartDegree, 
  int waggingMode, 
  signed long waggingPeriod, 
  double waggingIterations, 
  signed long waggingAmplitude,
  double waggingAmplitudeCoefficient,
  boolean &wagging)
{
  signed long waggingTime = millis() - waggingStartMillis;
  signed long degree;
 
  // Если времени прошло больше, чем положено на все колебания, определяем угол на последний момент колебаний:
  if (waggingTime > waggingPeriod * waggingIterations)
  {
    wagging = false;
    waggingTime = (signed long)(waggingPeriod * waggingIterations);
  }

  // Пока считаем, что колебания происходят вокруг оси t (y = 0):
  if (waggingMode == 1)
  {
    signed long t = waggingTime % waggingPeriod;
    if ((t >= 0) && (t < waggingPeriod / 4))
    {
      degree = 2 * waggingAmplitude * t / waggingPeriod;
    }
    else if ((t >= waggingPeriod / 4) && (t < 3 * waggingPeriod / 4))
    {
      degree = - 2 * waggingAmplitude * t / waggingPeriod + waggingAmplitude;
    }
    else
    {
      degree = 2 * waggingAmplitude * t / waggingPeriod - 2 * waggingAmplitude;
    }
  }
  else
  {
    degree = waggingAmplitude / 2 * sin(2 * 3.1415 * waggingTime / waggingPeriod);
  }
  
  // Если амплитуда, например, затухает (amplitudeCoefficient < 1), пропорционально уменьшаем угол:
  degree = (signed long)degree + degree * (waggingAmplitudeCoefficient - 1) * waggingTime / waggingPeriod;
  if (waggingAmplitudeCoefficient < 1)
  {
    if ((double)waggingTime > (double)waggingPeriod / (1 - waggingAmplitudeCoefficient))
    {
      // Если коэффициент меньше 1 (затухания амплитуды), то начиная с этого момента прямая изменения
      // амплитуды пересекает y = 0 и амплитуда начинает расти по модулю в отрицательном направлении.
      degree = 0;
    }
  }
  
  // Учитываем, что колебания происходят не вокруг оси t, а относительно waggingStartDegree:
  degree += waggingStartDegree;
  
  return degree;
}

// Виляние хвостом.
void wagTail()
{
  if (! waggingTail)
  {
    return;
  }
 
  signed long degree = wagSomething(
    waggingTailStartMillis, 
    waggingTailStartDegree, 
    waggingTailMode, 
    waggingTailPeriod, 
    waggingTailIterations, 
    waggingTailAmplitude, 
    waggingTailAmplitudeCoefficient, 
    waggingTail);
  moveTail(degree);
}

void wagNo()
{
  if (! waggingNo)
  {
    return;
  }
 
  signed long degree = wagSomething(
    waggingNoStartMillis, 
    waggingNoStartDegree, 
    waggingNoMode, 
    waggingNoPeriod, 
    waggingNoIterations, 
    waggingNoAmplitude,
    waggingNoAmplitudeCoefficient, 
    waggingNo);
  moveHead("H", degree);
}

void wagYes()
{
  if (! waggingYes)
  {
    return;
  }
 
  signed long degree = wagSomething(
    waggingYesStartMillis, 
    waggingYesStartDegree, 
    waggingYesMode, 
    waggingYesPeriod, 
    waggingYesIterations, 
    waggingYesAmplitude, 
    waggingYesAmplitudeCoefficient, 
    waggingYes);
  moveHead("V", degree);
}

signed long sign(double value)
{
  if (value > 0) return 1;
  if (value < 0) return -1;
  return 0;
}

