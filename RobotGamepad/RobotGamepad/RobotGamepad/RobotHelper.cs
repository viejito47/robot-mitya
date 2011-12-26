// --------------------------------------------------------------------------------------------------------------------
// <copyright file="RobotHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс для взаимодействия с контроллером Arduino.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.IO.Ports;
    using System.Net;
    using System.Net.Sockets;

    /// <summary>
    /// Класс для взаимодействия с контроллером Arduino.
    /// </summary>
    public sealed class RobotHelper
    {
        /// <summary>
        /// Клиентский сокет.
        /// </summary>
        private Socket socket;

        /// <summary>
        /// Признак соединения с сервером.
        /// </summary>
        private bool connected;

        /// <summary>
        /// Текст последней ошибки.
        /// </summary>
        private string lastErrorMessage = string.Empty;

        /// <summary>
        /// Текст последней ошибки.
        /// </summary>
        public string LastErrorMessage { get { return this.lastErrorMessage; } }

        /// <summary>
        /// Признак соединения с сервером.
        /// </summary>
        public bool Connected { get { return this.connected; } }

        /// <summary>
        /// Установка соединения с сервером. Сервером является Android-приложение.
        /// </summary>
        /// <returns>
        /// true, если соединение установлено
        /// </returns>
        public bool Connect()
        {
            if (this.connected)
            {
                if (this.socket != null)
                {
                    if (this.socket.Connected)
                    {
                        this.socket.Disconnect(true);
                    }

                    this.socket = null;
                }

                this.connected = false;
            }

            try
            {
                this.socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                this.socket.Connect(Settings.TcpSocketServerAddress, Settings.TcpSocketServerPort);
                this.connected = socket.Connected;
            }
            catch (Exception e)
            {
                this.lastErrorMessage = e.Message;
                return false;
            }

            return this.connected;
        }

        /// <summary>
        /// Передать роботу команду.
        /// </summary>
        /// <param name="command">
        /// Текст команды.
        /// </param>
        /// <returns>
        /// true, если нет ошибок.
        /// </returns>
        public bool SendCommandToRobot(string command)
        {
            if (connected)
            {
                try
                {
                    Byte[] bytesSent = Encoding.ASCII.GetBytes(command + (char)13 + (char)10);
                    socket.Send(bytesSent, bytesSent.Length, 0);
                }
                catch (Exception e)
                {
                    this.lastErrorMessage = e.Message;
                    connected = false;
                    return false;
                }
            }
            else
            {
                this.lastErrorMessage = "Нет связи с роботом";
                return false;
            }

            this.lastErrorMessage = string.Empty;
            return true;
        }
    }
}
