// include core Wiring API
//#include <WProgram.h>

#include "SmartServo.h"

SmartServo::SmartServo()
{
  this->minDegree = 0;
  this->maxDegree = 180;
}

void SmartServo::detach(int pin)
{
#ifndef USBCON
  this->servo.detach(pin);
#endif
}

uint8_t SmartServo::attach(int pin)
{
  this->servo.attach(pin);
}

uint8_t SmartServo::attach(int pin, int minDegree, int maxDegree)
{
  this->minDegree = minDegree < maxDegree ? minDegree : maxDegree;
  this->maxDegree = minDegree > maxDegree ? minDegree : maxDegree;
  this->servo.attach(pin);
}

int SmartServo::read()
{
  return this->degree;
}

void SmartServo::write(int degree)
{
  if (degree < this->minDegree)
  {
    this->degree = minDegree;
  }
  else if (degree > this->maxDegree)
  {
    this->degree = maxDegree;
  }
  else
  {
    this->degree = degree;
  }
  this->servo.write(this->degree);
}

void SmartServo::startSwing(int swingMode,
	  signed long swingPeriod, double swingIterations,
	  signed long amplitude, double amplitudeCoefficient,
	  bool positiveDirection)
{
  this->isTurning = false;

  if (this->isSwinging)
  {
    return;
  }
	
  this->isSwinging = true;
  this->startMillis = millis();
  this->startDegree = this->degree;
  this->swingMode = swingMode;
  this->swingPeriod = swingPeriod;
  this->swingIterations = swingIterations;
  this->amplitude = amplitude;
  this->amplitudeCoefficient = amplitudeCoefficient;
  this->positiveDirection = positiveDirection;
}

// Вызывается для начала равномерного поворота.
// startDegree - исходное положение сервопривода.
// period - период полного поворота в миллисекундах.
void SmartServo::startTurn(signed long period, bool positiveDirection)
{
  this->isSwinging = false;
  this->isTurning = true;
  this->startMillis = millis();
  this->startDegree = this->degree;
  this->swingPeriod = period;
  this->positiveDirection = positiveDirection;
}

void SmartServo::stop()
{
  this->isSwinging = false;
  this->isTurning = false;
}

bool SmartServo::update()
{
  if (this->isSwinging)
  {
    return this->swing();
  }
  else if (this->isTurning)
  {
    return this->turn();
  }
  
  return false;
}

bool SmartServo::swing()
{
  signed long swingingTime = millis() - this->startMillis;
  signed long degree;
  
  // Если времени прошло больше, чем положено на все колебания, определяем угол на последний момент колебаний:
  if (swingingTime > this->swingPeriod * this->swingIterations)
  {
    this->isSwinging = false;
    swingingTime = (signed long)(this->swingPeriod * this->swingIterations);
  }

  // Пока считаем, что колебания происходят вокруг оси t (y = 0):
  if (this->swingMode == 1)
  {
    signed long t = swingingTime % this->swingPeriod;
    if ((t >= 0) && (t < this->swingPeriod / 4))
    {
      degree = 2 * this->amplitude * t / this->swingPeriod;
    }
    else if ((t >= this->swingPeriod / 4) && (t < 3 * this->swingPeriod / 4))
    {
      degree = - 2 * this->amplitude * t / this->swingPeriod + this->amplitude;
    }
    else
    {
      degree = 2 * this->amplitude * t / this->swingPeriod - 2 * this->amplitude;
    }
  }
  else
  {
    degree = this->amplitude / 2 * sin(2 * 3.1415 * swingingTime / this->swingPeriod);
  }
  
  // Если амплитуда, например, затухает (amplitudeCoefficient < 1), пропорционально уменьшаем угол:
  degree = (signed long)degree + degree * (this->amplitudeCoefficient - 1) * swingingTime / this->swingPeriod;
  if (this->amplitudeCoefficient < 1)
  {
    if ((double)swingingTime > (double)this->swingPeriod / (1 - this->amplitudeCoefficient))
    {
      // Если коэффициент меньше 1 (затухания амплитуды), то начиная с этого момента прямая изменения
      // амплитуды пересекает y = 0 и амплитуда начинает расти по модулю в отрицательном направлении.
      degree = 0;
    }
  }
  
  // Если positiveDirection == false, направление колебаний идёт в другую сторону:
  if (!this->positiveDirection)
  {
    degree = -degree;
  }
  
  // Учитываем, что колебания происходят не вокруг оси t, а относительно this->startDegree:
  degree += this->startDegree;
  
  this->degree = (int)degree;
  this->servo.write(this->degree);
  return this->isSwinging;
}

bool SmartServo::turn()
{
  if (this->swingPeriod == 0)
  {
	this->isTurning = false;
  }
  else
  {
    signed long elapsedTime = millis() - this->startMillis;
    signed long degree = 360 * elapsedTime / this->swingPeriod;
	if (this->positiveDirection)
	{
	  degree = this->startDegree + degree;
	}
	else
	{
	  degree = this->startDegree - degree;
	}

    if (degree < this->minDegree)
    {
      degree = minDegree;
	  this->isTurning = false;
    }
    else if (degree > this->maxDegree)
    {
      degree = maxDegree;
	  this->isTurning = false;
    }

    this->degree = (int)degree;
  }

  this->servo.write(this->degree);
  return this->isTurning;
}
