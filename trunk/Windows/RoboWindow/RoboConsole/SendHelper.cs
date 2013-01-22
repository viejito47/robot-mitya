// --------------------------------------------------------------------------------------------------------------------
// <copyright file="SendHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2012
// </copyright>
// <summary>
//   Набор функций для управления отправкой сообщений.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboConsole
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Windows.Forms;

    using RoboCommon;

    /// <summary>
    /// Набор функций для управления отправкой сообщений.
    /// </summary>
    public static class SendHelper
    {
        /// <summary>
        /// История команд.
        /// </summary>
        private static CommandHistory commandHistory = new CommandHistory();

        /// <summary>
        /// Обработка введённых команд.
        /// </summary>
        /// <param name="commandLineTextBox">
        /// Поле ввода команд.
        /// </param>
        /// <param name="outputTextBox">
        /// Поле вывода результатов.
        /// </param>
        /// <param name="robotHelper">
        /// Объект, реализующий взаимодействие с роботом.
        /// </param>
        public static void CommandLineProcessor(TextBox commandLineTextBox, TextBox outputTextBox, IRobotHelper robotHelper)
        {
            commandHistory.Add(commandLineTextBox.Text);
            
            IEnumerable<string> commands = RobotHelper.ParseRoboScript(commandLineTextBox.Text);

            var notSentCommands = new List<string>();
            foreach (string command in commands)
            {
                // Пустышки в сторону!
                if (command.Trim().Equals(string.Empty))
                {
                    continue;
                }

                // Если есть ошибка, то все последующие команды не посылаются роботу.
                if (notSentCommands.Count > 0)
                {
                    notSentCommands.Add(command);
                }
                else
                {
                    bool sendResult = SendMessageToRobot(robotHelper, false, command); //...
                    if (sendResult)
                    {
                        outputTextBox.AppendText(robotHelper.LastSentMessage);
                    }
                    else
                    {
                        outputTextBox.AppendText(string.Format("Ошибка в {0}: {1}", command, robotHelper.LastErrorMessage));
                        notSentCommands.Add(command);
                    }

                    outputTextBox.AppendText(Environment.NewLine);
                }
            }

            // В поле ввода команд оставляем только неотправленные команды.
            if (notSentCommands.Count > 0)
            {
                commandLineTextBox.Text = GenerateCommandLine(notSentCommands);
            }
            else
            {
                commandLineTextBox.Text = string.Empty;
            }
        }

        /// <summary>
        /// Выбор и ввод в поле ввода предыдущей команды из истории команд.
        /// </summary>
        /// <param name="commandLineTextBox">
        /// Поле ввода команд.
        /// </param>
        public static void SelectPreviousCommand(TextBox commandLineTextBox)
        {
            commandLineTextBox.Text = commandHistory.GetPreviousCommand();
        }

        /// <summary>
        /// Выбор и ввод в поле ввода следующей команды из истории команд.
        /// </summary>
        /// <param name="commandLineTextBox">
        /// Поле ввода команд.
        /// </param>
        public static void SelectNextCommand(TextBox commandLineTextBox)
        {
            commandLineTextBox.Text = commandHistory.GetNextCommand();
        }

        /// <summary>
        /// Отправляет команду роботу. Или по Wi-Fi или по COM.
        /// </summary>
        /// <param name="robotHelper">
        /// Объект, реализующий взаимодействие с роботом.
        /// </param>
        /// <param name="throughCom">
        /// Если true, то передача команды осуществляется посредством COM-порта, иначе – по WiFi.
        /// </param>
        /// <param name="command">
        /// Команда роботу.
        /// </param>
        /// <returns>
        /// false, если ошибка.
        /// </returns>
        private static bool SendMessageToRobot(IRobotHelper robotHelper, bool throughCom, string command)
        {
            return robotHelper.SendMessageToRobot(command);
        }

        /// <summary>
        /// Получение строки из команд, разделённых запятыми.
        /// </summary>
        /// <param name="commands">
        /// Список команд.
        /// </param>
        /// <returns>
        /// Строка команд, разделённых запятыми.
        /// </returns>
        private static string GenerateCommandLine(IEnumerable<string> commands)
        {
            string result = string.Empty;
            const string CommandSeparator = ", ";
            foreach (string command in commands)
            {
                result += CommandSeparator + command;
            }

            if (result.Length > 0)
            {
                result = result.Remove(0, CommandSeparator.Length);
            }

            return result;
        }
    }
}
