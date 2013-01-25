// --------------------------------------------------------------------------------------------------------------------
// <copyright file="RobotHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс для взаимодействия с головой робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboCommon
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.IO;
    using System.IO.Ports;
    using System.Linq;
    using System.Net;
    using System.Net.Sockets;
    using System.Text;

    /// <summary>
    /// Класс для взаимодействия с головой робота.
    /// </summary>
    public sealed class RobotHelper : IRobotHelper
    {
        /// <summary>
        /// Опции управления роботом.
        /// </summary>
        private ConnectSettings connectSettings;

        /// <summary>
        /// Initializes a new instance of the RobotHelper class.
        /// </summary>
        /// <param name="connectSettings">
        /// Опции соединения с роботом.
        /// </param>
        public RobotHelper(ConnectSettings connectSettings)
        {
            if (connectSettings == null)
            {
                throw new ArgumentNullException("connectSettings");
            }

            this.connectSettings = connectSettings;
            this.LastErrorMessage = string.Empty;
            this.LastSentMessage = string.Empty;
        }

        /// <summary>
        /// Gets or sets Текст последней ошибки.
        /// </summary>  
        public string LastErrorMessage { get; set; }

        /// <summary>
        /// Gets Последнее успешно отправленное роботу сообщение.
        /// Сообщение, передаваемое методу SendMessageToRobot, может быть представлено в краткой форме.
        /// Числовое значение может отсутствовать или не содержать лидирующих нулей. После успешной отправки
        /// свойство LastSentMessage будет содержать это сообщение, представленное в полной форме.
        /// </summary>  
        public string LastSentMessage { get; private set; }

        /// <summary>
        /// Gets Опции соединения с роботом.
        /// </summary>
        public ConnectSettings ConnectSettings
        {
            get
            {
                return this.connectSettings;
            }
        }

        /// <summary>
        /// Получение списка команд из строки на РобоСкрипте с разделителями-запятыми.
        /// </summary>
        /// <param name="roboScript">Текст РобоСкрипта.</param>
        /// <returns>Список команд.</returns>
        public static IEnumerable<string> ParseRoboScript(string roboScript)
        {
            return roboScript.Split(',').Select(x => x.Trim()).ToArray();
        }

        /// <summary>
        /// Передать роботу сообщение.
        /// </summary>
        /// <param name="message">
        /// Текст сообщения.
        /// </param>
        /// <returns>
        /// true, если нет ошибок.
        /// </returns>
        public bool SendMessageToRobot(string message)
        {
            string correctedMessage;
            try
            {
                if (!this.CorrectMessage(message, out correctedMessage))
                {
                    return false;
                }

                byte[] messageBytes = Encoding.ASCII.GetBytes(correctedMessage + (char)13 + (char)10);

                UdpClient udpClient = new UdpClient();
                IPEndPoint endPoint = new IPEndPoint(this.connectSettings.RoboHeadAddress, this.connectSettings.MessagePort);
                int bytesSent = udpClient.Send(messageBytes, messageBytes.Length, endPoint);
                if (bytesSent != messageBytes.Length)
                {
                    this.LastErrorMessage = "Нет связи с роботом";
                    return false;
                }
            }
            catch (Exception e)
            {
                this.LastErrorMessage = e.Message;
                return false;
            }

            this.LastSentMessage = correctedMessage;
            return true;
        }

        /// <summary>
        /// Передать роботу неповторяющееся сообщение.
        /// </summary>
        /// <param name="message">
        /// Сообщение, передаваемое роботу.
        /// </param>
        /// <param name="voidMessage">
        /// Сообщение с этим же идентификатором, но с "пустым" значением.</param>
        /// <returns>
        /// true, если нет ошибок.
        /// </returns>
        /// <remarks>
        /// Сообщения для управления моторами, например, повторяются постоянно, каждые несколько милисекунд.
        /// Потеря одного такого сообщения несущественно. А сообщения, например, смены настроения (мордочки)
        /// передаются по команде оператора. Потеря - пропуск команды. Поэтому сообщения повторяются 
        /// несколько раз. Особенность обработки сообщений на приёмной стороне (хэш таблица для исключения
        /// обработки абсолютно идентичных поступивших друг за другом сообщений) требует после команды смены
        /// настроения дать аналогичную команду с "пустым" значением.
        /// </remarks>
        public bool SendNonrecurrentMessageToRobot(string message, string voidMessage)
        {
            int repetitions = this.connectSettings.SingleMessageRepetitionsCount;

            for (int i = 0; i < repetitions; i++)
            {
                if (!this.SendMessageToRobot(message))
                {
                    return false;
                }
            }

            for (int i = 0; i < repetitions; i++)
            {
                if (!this.SendMessageToRobot(voidMessage))
                {
                    return false;
                }
            }
            
            return true;
        }

        /// <summary>
        /// Передача текста РобоСкрипта роботу.
        /// </summary>
        /// <param name="roboScript">Текст РобоСкрипта с разделителями-запятыми.</param>
        /// <returns>false, если возникла ошибка. Тест ошибки будет в свойстве LastErrorMessage.</returns>
        public bool SendRoboScriptToRobot(string roboScript)
        {
            IEnumerable<string> commands = ParseRoboScript(roboScript);

            foreach (string command in commands)
            {
                bool result = this.SendMessageToRobot(command);
                if (!result)
                {
                    return false;
                }
            }

            return true;
        }

        /// <summary>
        /// Проверка и коррекция сообщения. Сообщение приводится к виду: идентификатор (1 символ), 
        /// HEX значение (4 символа 0..9, A..F).
        /// </summary>
        /// <param name="message">Исходное значение. В значении могут отсутствовать лидирующие нули.</param>
        /// <param name="correctedMessage">Скорректированное пятисимвольное сообщение.</param>
        /// <returns>true, если удалось, false и LastErrorMessage если нет.</returns>
        private bool CorrectMessage(string message, out string correctedMessage)
        {
            const int IdentifierLength = 1;
            const int ValueLength = 4;

            correctedMessage = string.Empty;

            if ((message.Length == 0) || (message.Length > IdentifierLength + ValueLength))
            {
                this.LastErrorMessage = "Неверный размер сообщения.";
                return false;
            }

            string valueText = message.Remove(0, IdentifierLength);
            valueText = valueText.ToUpper();
            if (valueText.Length < ValueLength)
            {
                int charsToAdd = ValueLength - valueText.Length;
                for (int i = 0; i < charsToAdd; i++)
                {
                    valueText = "0" + valueText;
                }
            }

            try
            {
                int.Parse(valueText, System.Globalization.NumberStyles.AllowHexSpecifier);
            }
            catch (Exception)
            {
                this.LastErrorMessage = "Неверно задано значение сообщения.";
                return false;
            }

            correctedMessage = message[0] + valueText;
            return true;
        }
    } // class
} // namespace
