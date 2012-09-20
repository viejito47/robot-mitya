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
    public sealed class RobotHelper
    {
        /// <summary>
        /// Текст последней ошибки.
        /// </summary>
        private string lastErrorMessage = string.Empty;

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
        }

        /// <summary>
        /// Gets Текст последней ошибки.
        /// </summary>
        public string LastErrorMessage 
        { 
            get
            { 
                return this.lastErrorMessage; 
            } 
        }

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
            /*if (message.StartsWith("L") || message.StartsWith("R"))
            {
                Debug.Print(message);
            }*/
            try
            {
                byte[] messageBytes = Encoding.ASCII.GetBytes(message + (char)13 + (char)10);

                UdpClient udpClient = new UdpClient();
                IPEndPoint endPoint = new IPEndPoint(this.connectSettings.RoboHeadAddress, this.connectSettings.MessagePort);
                int bytesSent = udpClient.Send(messageBytes, messageBytes.Length, endPoint);
                if (bytesSent != messageBytes.Length)
                {
                    this.lastErrorMessage = "Нет связи с роботом";
                    return false;
                }
            }
            catch (Exception e)
            {
                this.lastErrorMessage = e.Message;
                return false;
            }

            this.lastErrorMessage = string.Empty;
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
            for (int i = 0; i < this.connectSettings.SingleMessageRepetitionsCount; i++)
            {
                if (!this.SendMessageToRobot(message))
                {
                    return false;
                }
            }

            for (int i = 0; i < this.connectSettings.SingleMessageRepetitionsCount; i++)
            {
                if (!this.SendMessageToRobot(voidMessage))
                {
                    return false;
                }
            }
            
            return true;
        }
    } // class
} // namespace
