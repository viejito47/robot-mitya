// Подстраховываемся, что класс будет объявлен только один раз
#ifndef Swinger_h
#define Swinger_h

// Класс, реализующий колебания например, сервоприводом.
class Swinger
{
  public:
    Swinger();

    // Вызывается для начала колебаний.
    void startSwing(int startDegree, bool swingMode,
      signed long swingPeriod, double swingIterations,
      signed long amplitude, double amplitudeCoefficient,
	  bool positiveDirection);
	
    // Вызывается в процедуре loop. Определяет угол в
    // текущий момент времени. Возвращает false если в
    // данный момент колебания уже или ещё не производятся.
    bool swing(int &outDegree);
  private:
    // Момент начала колебаний.
    signed long startMillis;

    // Исходный угол, относительно которого происходят колебания.
    int startDegree;

    // Признак виляния хвостом.
    bool isSwinging;

    // Режим виляния хвостом (1 - угол меняется линейно от времени, иначе - по синусу от времени).
    int swingMode;

    // Период колебаний в милисекундах.
    signed long swingPeriod;

    // Продолжительность виляния хвостом в тактах.
    // Может быть нецелое число.
    double swingIterations;

    // Амплитуда колебаний хвостом (для амплитуды в 70
    // градусов колебания происходят вокруг 90 градусов -
    // от 55 до 125 градусов).
    signed long amplitude;

    // Во сколько раз снизится амплитуда за период.
    double amplitudeCoefficient;
	
	// Направление колебаний. Если true, колебания начинаются с увеличения угла,
    // если false - c уменьшения.
	bool positiveDirection;
};

#endif