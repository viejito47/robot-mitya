// Пин контроллера, использующийся для ИК-выстрела (цифровой выход).
const int gunPin = 3;
// Пин, использующийся для фиксации попаданий (аналоговый вход).
const int targetPin = A0;

// Пины контроллера для управления двигателями робота (цифровые выходы).
const int motorLeftSpeedPin = 5;
const int motorLeftDirectionPin = 4;
const int motorRightSpeedPin = 6;
const int motorRightDirectionPin = 7;

// Пины контроллера для управления сервоприводами робота (цифровые выходы).
const int servoHeadHorizontalPin = 9;
const int servoHeadVerticalPin = 10;
const int servoTailPin = 11;

// Пин управления фарами.
const int lightPin = 13;


const int servoHeadHorizontalMinDegree = 0;
const int servoHeadHorizontalMaxDegree = 180;
const int servoHeadVerticalMinDegree = 0;
const int servoHeadVerticalMaxDegree = 180;
const int servoTailMinDegree = 10;   // Not ZERO, because in the boundary position servo is vibrating a lot
const int servoTailMaxDegree = 170;   // Not 180, because in the boundary position servo is vibrating a lot

const int servoHeadHorizontalDefaultState = 90;
const int servoHeadVerticalDefaultState = 90;
const int servoTailDefaultState = 90;

// Constants for Voltage Divider
#define TOTAL_TIMERS 2  // Total Voltage Dividers

// Pin for measuring battery voltage (analog in).
const int batterySensorPin[TOTAL_TIMERS] = {A5, A4};

const float voltPerUnit = 0.004883; // 5V/1024 values = 0,004883 V/value
const float dividerRatio0 = 5; // (R1+R2)/R2
const float dividerRatio1 = 5; // (R1+R2)/R2
const float voltRatio[TOTAL_TIMERS] = {voltPerUnit * dividerRatio0*100, voltPerUnit * dividerRatio1*100}; // This coefficient we will use

const long MINIMAL_INTERVAL_VALUE = 1000; // Minimum interval in milliseconds for Timer

const int IR_MOVE_SPEED = 255; // Speed set to maximum, when control from IR remote
const int IR_SERVO_STEP_DEFAULT = 5; // Step for changing servo head (Horizontal and Vertical) position, when controling from IR remote
const int IR_SERVO_STEP_PROGRAM_MODE = 180; // Step for changing servo head (Horizontal and Vertical) position, when in programm mode. (Set to Maximum, so that user will 100% notice it.

const int IR_TOTAL_COMMANDS = 12; // Total Number of commands, that can be send by IR control

const int musicPeriod = 535;
