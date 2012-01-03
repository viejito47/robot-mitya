// -------------------------------------------------------------------------------------
// file = "robot_control.pde"
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
  moveMotor('D', 'F', 0);

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

  // Инициализация структуры для передачи данных в Android-приложение:
  uint8_t message[5] = { 0x00 };
  uint16_t messageLength = sizeof(message);
  
  // Обработка ИК-попадания:
  if (irrecv.decode(&results)) {
    unsigned long hitValue;
    if (debugMode) {
      hitValue = 0xA90;
    }
    else {
      hitValue = 0xABC1;
    }
    boolean gotHit = (results.decode_type == SONY) && (results.value == hitValue);
    irrecv.resume();
    
    // В случае попадания:
    if (gotHit) {
      indicate();
      serialPrintln("Hit ");
      // Шлю команду 'HT000' (hit - попадание) Android-приложению:
      message[0] = (uint8_t)'H';
      message[1] = (uint8_t)'T';
      message[2] = (uint8_t)'0';
      message[3] = (uint8_t)'0';
      message[4] = (uint8_t)'0';
      adk.SndData(messageLength, message);
    }
  }

  // Установка соединения с Andriod-приложением:
  if (adk.isReady() == false) {
    return;
  }

  // Чтение команды из входной очереди:
  adk.RcvData(&messageLength, message);
  if (messageLength == 0) {
    return;
  }
  if (messageLength != 5) {
    indicate();
    serialPrintln("Invalid command.");
    return;
  }
  
  // Парсер команды:
  String command;
  int paramValue;
  command = (char)message[0];
  command += (char)message[1];
  int digit100 = message[2] - '0';
  int digit10 = message[3] - '0';
  int digit1 = message[4] - '0';
  paramValue = 100 * digit100 + 10 * digit10 + digit1;
  serialPrintln(command + (char)(digit100 + '0') + (char)(digit10 + '0') + (char)(digit1 + '0'));
  
  if ((command == "LF") || (command == "RF")
   || (command == "LB") || (command == "RB")
   || (command == "DF") || (command == "DB")) 
  {
    // Команда двигателям:
    moveMotor(command[0], command[1], paramValue);
    serialPrintln("MOVE: OK");
  }
  else if ((command == "HH") || (command == "HV"))
  {
    // Команда голове:
    moveHead(command[1], paramValue);
    serialPrintln("HEAD: OK");
  }
  else if (command == "FR")
  {
    // Команда выстрела пушке:
    irsend.sendSony(0xABC0, 16);
    irrecv.enableIRIn(); // (надо для повторной инициализации ИК-приёмника)
    serialPrintln("GUN: OK");
  }
  else if (command == "TE")
  {
    // Команда выполнения теста робота:
    testRobot(paramValue);
    serialPrintln("TE: OK");
  }
  else
  {
    indicate();
    serialFlush();
    serialPrintln("Unknown command.");
  }
}

// Поворот головы.
void moveHead(char plane, int degree)
{
  if (plane == 'H') // (горизонтальная плоскость)
  {
    servoHorizontal.write(degree);
  }
  else if (plane == 'V') // (вертикальная плоскость)
  {
    servoVertical.write(degree);
  }
}

// Управление двигателями.
void moveMotor(char side, char direction, int speed)
{
  bool directionPinValue = direction == 'F' ? LOW : HIGH;
  if (speed == 0) {
    directionPinValue = LOW; // это для режима отключения мотора
  }
  if ((side == 'L') || (side == 'D')) {
    digitalWrite(motorLeftDirectionPin, directionPinValue);
    analogWrite(motorLeftSpeedPin, speed);
  }
  if ((side == 'R') || (side == 'D')) {
    digitalWrite(motorRightDirectionPin, directionPinValue);
    analogWrite(motorRightSpeedPin, speed);
  }
}

// Выполнение тестовой программы робота:
void testRobot(int testNumber)
{
  if (testNumber == 0)
  {
    moveMotor('D', 'F', 128);
    delay(1000);
    moveMotor('D', 'F', 192);
    delay(1000);
    moveMotor('D', 'F', 255);
    delay(1000);
    moveMotor('D', 'F', 192);
    moveMotor('R', 'B', 192);
    delay(1500);
    moveMotor('L', 'F', 128);
    delay(500);
    moveMotor('D', 'B', 128);
    delay(1500);
    moveMotor('D', 'F', 0);
    moveHead('H', 45);
    delay(1000);
    moveHead('H', 135);
    delay(1000);
    moveHead('H', 90);
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

// Система каманд:
//   XX###
//   XX - двухсимвольный идентификатор команды, ### - трёхзначный параметр команды.
//   Длина команды обязательно 5 символов: 2 буквенных и 3 цифровых.
// Команды:
//   LF### - режим левого двигателя (left forward). ### - скорость (значение от 000 до 255).
//   LB### - режим левого двигателя (left backward). ### - скорость (значение от 000 до 255).
//   RF### - режим правого двигателя (right forward). ### - скорость (значение от 000 до 255).
//   RB### - режим правого двигателя (right backward). ### - скорость (значение от 000 до 255).
//   DF### - режим левого и правого двигателей (drive forward). ### - скорость (значение от 000 до 255).
//   DB### - режим левого и правого двигателей (drive backward). ### - скорость (значение от 000 до 255).
//   HH### - поворот головы по горизонтали на указанный угол. ### - абсолютное значение угла в градусах (от 000 до 360).
//   HV### - поворот головы по вертикали на указанный угол. ### - абсолютное значение угла в градусах (от 000 до 180).
//   FR000 - выстрелить из ИК-пушки.
//   HT000 - попадание (команда, отправляемая на уровень Android-приложения).
// Пример:
//   LF010

