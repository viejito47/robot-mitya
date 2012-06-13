// --------------------------------------------------------------------------------------------------------------------
// <copyright file="RobotHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс для взаимодействия с головой робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
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
            try
            {
                byte[] commandBytes = Encoding.ASCII.GetBytes(command + (char)13 + (char)10);

                UdpClient udpClient = new UdpClient();
                IPEndPoint endPoint = new IPEndPoint(Settings.RoboHeadAddress, Settings.CommandPort);
                int bytesSent = udpClient.Send(commandBytes, commandBytes.Length, endPoint);
                if (bytesSent != commandBytes.Length)
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
    } // class
} // namespace
