//------------------------------
// SmartServo library.
// Copyright (c) Dmitry Dzakhov.
// www.robot-mitya.ru
// info@robot-mitya.ru
//------------------------------

#ifndef Swinger_h
#define Swinger_h

#include <Arduino.h>
#include <Servo.h>

// Класс, реализующий колебания например, сервоприводом.
class SmartServo
{
  public:
    SmartServo();

	uint8_t attach(int pin);
	uint8_t attach(int pin, int minDegree, int maxDegree);
	
	int read();
	
	void write(int degree);

    // Вызывается для начала колебаний.
    void startSwing(int swingMode,
      signed long swingPeriod, double swingIterations,
      signed long amplitude, double amplitudeCoefficient,
	  bool positiveDirection);

	// Вызывается для начала равномерного поворота.
	// startDegree - исходное положение сервопривода.
	// period - период полного поворота в миллисекундах.
	void startTurn(signed long period, bool positiveDirection);
	
	void stop();
	  
    // Вызывается в процедуре loop. Определяет угол в
    // текущий момент времени. Возвращает false если в
    // данный момент колебания или поворос уже или ещё
	// не производятся.
	bool update();
  private:
    Servo servo;
    int degree;
	int minDegree;
	int maxDegree;

    // Момент начала колебаний.
    signed long startMillis;

    // Исходный угол, относительно которого происходят колебания.
    int startDegree;

    // Признак равномерного поворота.
    bool isTurning;

    // Признак колебательного движения.
    bool isSwinging;

    // Режим колебаний (1 - угол меняется линейно от времени, иначе - по синусу от времени).
    int swingMode;

    // Период колебаний в милисекундах.
    signed long swingPeriod;

    // Продолжительность колебаний в тактах.
    // Может быть нецелое число.
    double swingIterations;

    // Амплитуда колебаний (для амплитуды в 70
    // градусов колебания происходят вокруг 90 градусов -
    // от 55 до 125 градусов).
    signed long amplitude;

    // Во сколько раз снизится амплитуда за период.
    double amplitudeCoefficient;
	
	// Направление колебаний. Если true, колебания начинаются с увеличения угла,
    // если false - c уменьшения.
	bool positiveDirection;

    // Вызывается в процедуре loop. Определяет угол в
    // текущий момент времени. Возвращает false если в
    // данный момент колебания уже или ещё не производятся.
    bool swing();

    // Вызывается в процедуре loop. Определяет угол в
    // текущий момент времени. Возвращает false если в
    // данный момент поворот уже или ещё не производится.
    bool turn();
};

#endif