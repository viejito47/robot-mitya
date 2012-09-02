// �����������������, ��� ����� ����� �������� ������ ���� ���
#ifndef Swinger_h
#define Swinger_h

// �����, ����������� ��������� ��������, �������������.
class Swinger
{
  public:
    Swinger();

    // ���������� ��� ������ ���������.
    void startSwing(int startDegree, bool swingMode,
      signed long swingPeriod, double swingIterations,
      signed long amplitude, double amplitudeCoefficient,
	  bool positiveDirection);
	
    // ���������� � ��������� loop. ���������� ���� �
    // ������� ������ �������. ���������� false ���� �
    // ������ ������ ��������� ��� ��� ��� �� ������������.
    bool swing(int &outDegree);
  private:
    // ������ ������ ���������.
    signed long startMillis;

    // �������� ����, ������������ �������� ���������� ���������.
    int startDegree;

    // ������� ������� �������.
    bool isSwinging;

    // ����� ������� ������� (1 - ���� �������� ������� �� �������, ����� - �� ������ �� �������).
    int swingMode;

    // ������ ��������� � ������������.
    signed long swingPeriod;

    // ����������������� ������� ������� � ������.
    // ����� ���� ������� �����.
    double swingIterations;

    // ��������� ��������� ������� (��� ��������� � 70
    // �������� ��������� ���������� ������ 90 �������� -
    // �� 55 �� 125 ��������).
    signed long amplitude;

    // �� ������� ��� �������� ��������� �� ������.
    double amplitudeCoefficient;
	
	// ����������� ���������. ���� true, ��������� ���������� � ���������� ����,
    // ���� false - c ����������.
	bool positiveDirection;
};

#endif