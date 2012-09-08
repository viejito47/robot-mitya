// �����������������, ��� ����� ����� �������� ������ ���� ���
#ifndef RoboScript_h
#define RoboScript_h

#include <inttypes.h>
#include <Arduino.h>

// ��������� - ��������� ������ ������� ������ RoboScript
#define ROBOSCRIPT_OK							   0
#define ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY	  -1
#define ROBOSCRIPT_ERROR_NOT_INITIALIZED		  -2
#define ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED	  -3
#define ROBOSCRIPT_ERROR_OUT_OF_BOUNDS			  -4
#define ROBOSCRIPT_ERROR_OTHER					-100

// ���������, ������������ ������������ �������� ������ � RoboScript-�.
struct RoboAction
{
	struct
	{
		uint32_t Command : 8;   // ������� ������ (��. "������ ���������") - 1 ����.
		uint32_t Delay   : 24;  // �������� ����� ���������� ������� - 3 �����. �������� �������� �� 0 �� 16777215.
	};
	uint16_t Value; // �������� ��������� ������� - 2 �����.
};

// �����, ����������� ������ � ������������.
// ��� ������������:
// 1. ������� ���������.
// 2. �������� ������ ��� ������ �������� ������ - ���������� (initialize).
// 3. ��������� ������ �������� (addAction).
// 4. ���������� ���������� ����������� (startExecution).
// 5. � ������ loop() ��������� �������� hasActionToExecute.
//    ���� �� ����� true ��������� �������� �� ��� out-����������.
class RoboScript
{
  public:
    // ����������� ������.
    RoboScript();

    // ���������� ��� ������������� ���������� ������. ����� �������� ������ �� ������ �������� ������.
	// ��������! ������ ����� ���������� � ������� SRAM. � ATmega328, ��������, SRAM - ��� ����� 2 ������.
	// actionsCount - ���������� �������� � �����������. ������ �������� ��� 6 ����. ���������� ��������
	// � ����������� ������� ���� ����� �������. �������� - ��� ������� ������ ���� ��������.
	// �������� � ����������� ��� ���� �������: Pxxxx.
	// xxxx ��� ����� � ������������� � ����������������� ����� �� 0 �� 16777215 (����� �������������� ������ 3 �����!).
	// ����� ������� ROBOSCRIPT_OK ��� ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY.
	signed short initialize(int actionsCount);
	
	// ������������ ������, ������� �������� ��������.
	void finalize();
	
	// ���������� �������� � ������.
	// ����� ������� ROBOSCRIPT_OK, ROBOSCRIPT_ERROR_NOT_INITIALIZED (�� ���� �������� ������ ��� ������) ���
	// ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED (������ ����� ���������� �������� ��������� ������ �������).
	signed short addAction(RoboAction action);
	
	// �������� ���������� ��������, ����������� � ������. ��� �� ������ �������.
	int getActionsCount();
	
	// �������� �������� �� ��� ������� � �������. ��������� �������� �� 0 �� getActionsCount() - 1.
	// ����� ������� ROBOSCRIPT_OK, ROBOSCRIPT_ERROR_NOT_INITIALIZED (�� ���� �������� ������ ��� ������) ���
	// ROBOSCRIPT_ERROR_OUT_OF_BOUNDS (������ ��� ��������� [0, getActionsCount() - 1]).
	signed short getActionAt(int position, RoboAction &action);
	
	// ������� ������� ��������. ������, ������� ��������, ��� ���� �� �������������.
	// ������ ���������� ����������� �������� ������������ � 0.
	void clear();

	// ������ ����������� �� ����������.
	void startExecution();
	
	// ����������� ��������� ����������� �� ��� ����������.
	void stopExecution();
	
	// ��������� ��������� ������� ������. ���������� false ���� ������ ���, ��� ��� ��� ���������,
	// ��� ����� ��������� ������� ��� �� ���������. ���� ���� ������� ��� ����������, ���������� true,
	// � � ����������-���������� command � value ������������ ��������� ���� �������.
	bool hasActionToExecute(String &command, int &value);
  private:
	RoboAction* actions;
	int actionsMaxCount;
	int actionsCount;
	unsigned int startMillis;
	int currentPosition;
	unsigned int nextCommandMillis;
	bool isExecuting;
	bool getIsInitialized();
};

#endif