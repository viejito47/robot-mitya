// include core Wiring API
//#include <WProgram.h>

#include "Swinger.h"

// include description files for other libraries used (if any)
//#include <HardwareSerial.h>
#include <Arduino.h>

Swinger::Swinger()
{
}

void Swinger::startSwing(int startDegree, bool swingMode,
	  signed long swingPeriod, double swingIterations,
	  signed long amplitude, double amplitudeCoefficient,
	  bool positiveDirection)
{
  if (isSwinging)
  {
    return;
  }
	
  this->isSwinging = true;
  this->startMillis = millis();
  this->startDegree = startDegree;
  this->swingMode = swingMode;
  this->swingPeriod = swingPeriod;
  this->swingIterations = swingIterations;
  this->amplitude = amplitude;
  this->amplitudeCoefficient = amplitudeCoefficient;
  this->positiveDirection = positiveDirection;
}

bool Swinger::swing(int &outDegree)
{
  signed long swingingTime = millis() - this->startMillis;
  signed long degree;
  
  // ���� ������� ������ ������, ��� �������� �� ��� ���������, ���������� ���� �� ��������� ������ ���������:
  if (swingingTime > this->swingPeriod * this->swingIterations)
  {
    this->isSwinging = false;
    swingingTime = (signed long)(this->swingPeriod * this->swingIterations);
  }

  // ���� �������, ��� ��������� ���������� ������ ��� t (y = 0):
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
  
  // ���� ���������, ��������, �������� (amplitudeCoefficient < 1), ��������������� ��������� ����:
  degree = (signed long)degree + degree * (this->amplitudeCoefficient - 1) * swingingTime / this->swingPeriod;
  if (this->amplitudeCoefficient < 1)
  {
    if ((double)swingingTime > (double)this->swingPeriod / (1 - this->amplitudeCoefficient))
    {
      // ���� ����������� ������ 1 (��������� ���������), �� ������� � ����� ������� ������ ���������
      // ��������� ���������� y = 0 � ��������� �������� ����� �� ������ � ������������� �����������.
      degree = 0;
    }
  }
  
  // ���� positiveDirection == false, ����������� ��������� ��� � ������ �������:
  if (!this->positiveDirection)
  {
    degree = -degree;
  }
  
  // ���������, ��� ��������� ���������� �� ������ ��� t, � ������������ this->startDegree:
  degree += this->startDegree;
  
  outDegree = (int)degree;
  return this->isSwinging;
}
