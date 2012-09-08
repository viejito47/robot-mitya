// Подстраховываемся, что класс будет объявлен только один раз
#ifndef RoboScript_h
#define RoboScript_h

#include <inttypes.h>
#include <Arduino.h>

// Константы - результат работы функций класса RoboScript
#define ROBOSCRIPT_OK							   0
#define ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY	  -1
#define ROBOSCRIPT_ERROR_NOT_INITIALIZED		  -2
#define ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED	  -3
#define ROBOSCRIPT_ERROR_OUT_OF_BOUNDS			  -4
#define ROBOSCRIPT_ERROR_OTHER					-100

// Структура, определяющая элементарное действие робота в RoboScript-е.
struct RoboAction
{
	struct
	{
		uint32_t Command : 8;   // Команда роботу (см. "Формат сообщений") - 1 байт.
		uint32_t Delay   : 24;  // Ожидание после выполнения команды - 3 байта. Диапазон значений от 0 до 16777215.
	};
	uint16_t Value; // Значение параметра команды - 2 байта.
};

// Класс, реализующий работу с РобоСкриптом.
// Как использовать:
// 1. Создать экземпляр.
// 2. Выделить память под массив действий робота - РобоСкрипт (initialize).
// 3. Заполнить массив действий (addAction).
// 4. Стартовать выполнение РобоСкрипта (startExecution).
// 5. В методе loop() постоянно вызывать hasActionToExecute.
//    Если он вернёт true выполнить действие по его out-параметрам.
class RoboScript
{
  public:
    // Конструктор класса.
    RoboScript();

    // Вызывается для инициализации экземпляра класса. Метод выделяет память по массив действий робота.
	// Внимание! Память будет выделяться в области SRAM. У ATmega328, например, SRAM - это всего 2 КБайта.
	// actionsCount - количество действий в робоскрипте. Каждое действие это 6 байт. Количество действий
	// в последующем скрипте надо знать заранее. Действие - это команда роботу плюс ожидание.
	// Ожидание в робоскрипте это тоже команда: Pxxxx.
	// xxxx это время в миллисекундах в шестнадцатиричной форме от 0 до 16777215 (будет использоваться только 3 байта!).
	// Может вернуть ROBOSCRIPT_OK или ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY.
	signed short initialize(int actionsCount);
	
	// Освобождение памяти, занятой массивом действий.
	void finalize();
	
	// Добавление действия в скрипт.
	// Может вернуть ROBOSCRIPT_OK, ROBOSCRIPT_ERROR_NOT_INITIALIZED (не была выделена память под массив) или
	// ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED (скорее всего количество действий превысило размер массива).
	signed short addAction(RoboAction action);
	
	// Получить количество действий, добавленных в скрипт. Это не размер массива.
	int getActionsCount();
	
	// Получить действие по его индексу в массиве. Нумерация действий от 0 до getActionsCount() - 1.
	// Может вернуть ROBOSCRIPT_OK, ROBOSCRIPT_ERROR_NOT_INITIALIZED (не была выделена память под массив) или
	// ROBOSCRIPT_ERROR_OUT_OF_BOUNDS (индекс вне диапазона [0, getActionsCount() - 1]).
	signed short getActionAt(int position, RoboAction &action);
	
	// Очистка массива действий. Память, занятая массивом, при этом не освобождается.
	// Просто количество добавленных действий сбрасывается в 0.
	void clear();

	// Запуск РобоСкрипта на выполнение.
	void startExecution();
	
	// Немедленная остановка РобоСкрипта до его завершения.
	void stopExecution();
	
	// Получение очередной команды роботу. Возвращает false если команд нет, или они все выполнены,
	// или время очередной команды ещё не наступило. Если есть команда для выполнения, возвращает true,
	// а в параметрах-переменных command и value возвращаются параметры этой команды.
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