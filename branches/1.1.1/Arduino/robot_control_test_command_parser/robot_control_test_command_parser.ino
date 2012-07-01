// -------------------------------------------------------------------------------------
// file = "robot_control_test_command_parser.ino"
// company = "Dzakhov's jag"
// Copyright © Dmitry Dzakhov 2012
//   Скетч, предназначенный для тестирования функций скетча robot_control.
// -------------------------------------------------------------------------------------

// Функция инициализации скетча:
void setup()
{
  // Установка скорости последовательного порта (для отладочной информации):
  Serial.begin(9600);

  char ch;
  ch = '0';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = '5';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = '9';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = 'A';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = 'C';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = 'F';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = 'a';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = 'd';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = 'f';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
  ch = 'h';
  Serial.println("hexChatToInt(\"" + (String)ch + "\") = " + (String)hexCharToInt(ch));
    
  String message;
  String command;
  int value;
  
  Serial.println("");
  message = "Hello world!";
  if (parseMessage(message, command, value))
    Serial.println("Error!");
  else
  {
    Serial.println("Message = \"" + message + "\"");
    Serial.println("Command = \"" + command + "\"");
    Serial.println("Value = \"" + (String)value + "\"");
  }
  
  Serial.println("");
  message = "DFFFF"; // (-1)
  if (parseMessage(message, command, value))
  {
    Serial.println("Message = \"" + message + "\"");
    Serial.println("Command = \"" + command + "\"");
    Serial.println("Value = \"" + (String)value + "\"");
  }
  
  Serial.println("");
  message = "L00FF"; // (255)
  if (parseMessage(message, command, value))
  {
    Serial.println("Message = \"" + message + "\"");
    Serial.println("Command = \"" + command + "\"");
    Serial.println("Value = \"" + (String)value + "\"");
  }
  
  Serial.println("");
  message = "RFF01"; // (-255)
  if (parseMessage(message, command, value))
  {
    Serial.println("Message = \"" + message + "\"");
    Serial.println("Command = \"" + command + "\"");
    Serial.println("Value = \"" + (String)value + "\"");
  }
}

// Функция главного цикла:
void loop()
{
}

boolean parseMessage(String message, String &command, int &value)
{
  if (message.length() != 5)
  {
    command = "";
    value = 0;
    return false;
  }
    
  command = (String)message[0];
  value = 
    4096 * hexCharToInt(message[1]) +
    256 * hexCharToInt(message[2]) +
    16 * hexCharToInt(message[3]) +
    hexCharToInt(message[4]);
  
  return true;
}

int hexCharToInt(char ch)
{
  if ((ch >= '0') && (ch <= '9'))
    return ch - '0';
  else if ((ch >= 'A') && (ch <= 'F'))
    return 10 + ch - 'A';
  else if ((ch >= 'a') && (ch <= 'f'))
    return 10 + ch - 'a';
  else
    return 0;
}

