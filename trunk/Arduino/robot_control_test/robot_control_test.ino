// -------------------------------------------------------------------------------------
// file = "robot_control_test.pde"
// company = "Dzakhov's jag"
// Copyright © Dmitry Dzakhov 2012
//   Скетч, предназначенный для тестирования функций скетча robot_control.
// -------------------------------------------------------------------------------------

// Длина команды:
const int COMMANDLENGTH = 5;

// Неуместившаяся команда из буфера, прочитанного на предыдущей итерации чтения:
String previousBufferRest = "";

// Функция инициализации скетча:
void setup()
{
  // Установка скорости последовательного порта (для отладочной информации):
  Serial.begin(115200);
  Serial.println("Start...");
  String bufferText;
  
  Serial.println("Test 1:");
  bufferText = "";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  Serial.println("");
  
  Serial.println("Test 2:");
  bufferText = "LF123";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  Serial.println("");
  
  Serial.println("Test 3:");
  bufferText = "LF";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  bufferText = "012";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  Serial.println("");
  
  Serial.println("Test 3:");
  bufferText = "LF";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  bufferText = "012R";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  bufferText = "B222FR000F";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  bufferText = "L001FL000";
  Serial.println("bufferText = \"" + bufferText + "\"");
  processCommandBuffer(bufferText.length(), (uint8_t *)&bufferText[0]);
  Serial.println("");
}

// Функция главного цикла:
void loop()
{
}

// Извлечение и обработка команд из входного буфера:
void processCommandBuffer(uint16_t bufferSize, uint8_t *buffer)
{
  // Перевожу буфер в строку:
  String bufferText = "";
  for (int i = 0; i < bufferSize; i++)
  {
    bufferText += (char) buffer[i];
  }
  
  // Если от предыдущей итерации остался кусок команды, прибавляю его слева:
  bufferText = previousBufferRest + bufferText;
  previousBufferRest = "";
  
  // Последовательно извлекаю из полученной строки команды длиной COMMANDLENGTH символов.
  // Что не уместилось пойдёт в "довесок" (previousBufferRest) к следующей итереции.
  int i = 0;
  int bufferLength = bufferText.length();
  while (i < bufferLength)
  {
    int currentBufferLength = bufferLength - i;
    
    if (currentBufferLength >= COMMANDLENGTH)
    {
      String command = "";
      for (int j = 0; j < COMMANDLENGTH; j++)
      {
        command += bufferText[i + j];
      }

      // Обработка команды:      
      processCommand(command);
    }
    else
    {
      // "Довесок" к следующей итерации:
      previousBufferRest = "";
      for (int j = 0; j < currentBufferLength; j++)
      {
        previousBufferRest += bufferText[i + j];
      }
    }

    i += COMMANDLENGTH;
  }
}

// Процедура обработки команды:
void processCommand(String command)
{
  Serial.println(command);
}
