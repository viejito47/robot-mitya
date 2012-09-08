#include <RoboScript.h>

RoboScript roboScript;

// Функция инициализации скетча:
void setup()
{
  // Установка скорости последовательного порта (для отладочной информации):
  Serial.begin(9600);

  signed short result;
  
  Serial.print("testNoInitialization()..........");
  Serial.println(resultText(testNoInitialization()));
  
  Serial.print("testSmallSizeInitialization()...");
  Serial.println(resultText(testSmallSizeInitialization()));

  Serial.print("testHugeSizeInitialization()....");
  Serial.println(resultText(testHugeSizeInitialization()));
  
  Serial.print("testGetActionsCount()...........");
  Serial.println(resultText(testGetActionsCount()));
  
  Serial.print("testGetActionAt()...............");
  Serial.println(resultText(testGetActionAt()));
  
  Serial.print("testHasActionToExecute()........");
  Serial.println(resultText(testHasActionToExecute()));
  
  Serial.print("testStopExecution().............");
  Serial.println(resultText(testStopExecution()));
}

// Функция главного цикла:
void loop()
{
}

String resultText(signed short result)
{
  switch (result)
  {
    case ROBOSCRIPT_OK:
      return "ROBOSCRIPT_OK";
    case ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY:
      return "ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY";
    case ROBOSCRIPT_ERROR_NOT_INITIALIZED:
      return "ROBOSCRIPT_ERROR_NOT_INITIALIZED";
    case ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED:
      return "ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED";
    case ROBOSCRIPT_ERROR_OUT_OF_BOUNDS:
      return "ROBOSCRIPT_ERROR_OUT_OF_BOUNDS";
    default:
      return "ROBOSCRIPT_ERROR_OTHER";
  }
}

signed short testNoInitialization()
{
  if (roboScript.getActionsCount() != 0)
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  return ROBOSCRIPT_OK;
}
  
signed short testSmallSizeInitialization()
{
  signed short result = roboScript.initialize(10);
  if (result != ROBOSCRIPT_OK)
  {
    return result;
  }
  roboScript.finalize();
  return ROBOSCRIPT_OK;
}

signed short testHugeSizeInitialization()
{
  signed short result = roboScript.initialize(10000);
  if (result != ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY)
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  roboScript.finalize();
  return ROBOSCRIPT_OK;
}

signed short testAddAction()
{
  signed short result;
  RoboAction action;
  action.Command = 'I';
  action.Value = 1;
  action.Delay = 200;

  result = roboScript.addAction(action);
  if (result != ROBOSCRIPT_ERROR_NOT_INITIALIZED) return ROBOSCRIPT_ERROR_OTHER;

  roboScript.initialize(2);
  
  result = roboScript.addAction(action);
  if (result != ROBOSCRIPT_OK) return result;
  result = roboScript.addAction(action);
  if (result != ROBOSCRIPT_OK) return result;
  result = roboScript.addAction(action);
  if (result != ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED) return ROBOSCRIPT_ERROR_OTHER;
}

signed short testGetActionsCount()
{
  initializeAndFillActions();
  if (roboScript.getActionsCount() != 3)
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  roboScript.finalize();
  return ROBOSCRIPT_OK;
}

signed short testGetActionAt()
{
  RoboAction action;
  signed short result;
  result = roboScript.getActionAt(2, action);
  if (result != ROBOSCRIPT_ERROR_NOT_INITIALIZED)
  {
    return result;
  }
  
  initializeAndFillActions();
  
  result = roboScript.getActionAt(-2, action);
  if (result != ROBOSCRIPT_ERROR_OUT_OF_BOUNDS)
  {
    return result;
  }
  
  result = roboScript.getActionAt(8, action);
  if (result != ROBOSCRIPT_ERROR_OUT_OF_BOUNDS)
  {
    return result;
  }
  
  result = roboScript.getActionAt(1, action);
  if (result != ROBOSCRIPT_OK)
  {
    return result;
  }
  
  if (action.Command != 'I')
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  if (action.Value != 0)
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  if (action.Delay != 50)
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  roboScript.finalize();
  return ROBOSCRIPT_OK;
}

signed short testHasActionToExecute()
{
  String command;
  int value;
  if (roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  initializeAndFillActions();

  if (roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  roboScript.startExecution();
  
  delay(10);
  if (!roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  if ((command != "I") || (value != 1))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  delay(100);
  if (roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  delay(100);
  if (!roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  if ((command != "I") || (value != 0))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  
  delay(50);
  if (!roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  if ((command != "I") || (value != 1))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  delay(10);
  if (roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  roboScript.finalize();
  return ROBOSCRIPT_OK;
}

signed short testStopExecution()
{
  initializeAndFillActions();

  String command;
  int value;
  
  roboScript.startExecution();
  delay(10);
  if (!roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }
  roboScript.stopExecution();
  delay(200);
  if (roboScript.hasActionToExecute(command, value))
  {
    return ROBOSCRIPT_ERROR_OTHER;
  }

  roboScript.finalize();
  return ROBOSCRIPT_OK;
}

void initializeAndFillActions()
{
  roboScript.initialize(3);

  RoboAction actionOn;
  actionOn.Command = 'I';
  actionOn.Value = 1;
  actionOn.Delay = 200;
  
  RoboAction actionOff;
  actionOff.Command = 'I';
  actionOff.Value = 0;
  actionOff.Delay = 50;
  
  roboScript.addAction(actionOn);
  roboScript.addAction(actionOff);
  roboScript.addAction(actionOn);
}

