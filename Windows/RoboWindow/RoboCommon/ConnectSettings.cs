// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ConnectSettings.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс, хранящий настройки приложения.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboCommon
{
    using System.Net;

    /// <summary>
    /// Класс, хранящий настройки соединения с RoboHead (головой робота).
    /// </summary>
    public class ConnectSettings
    {
        /// <summary>
        /// Initializes a new instance of the ConnectSettings class.
        /// </summary>
        /// <param name="roboHeadAddress">
        /// IP-адрес робота.
        /// </param>
        /// <param name="messagePort">
        /// Порт для сокета.
        /// </param>
        public ConnectSettings(string roboHeadAddress, int messagePort)
        {
            this.RoboHeadAddress = IPAddress.Parse(roboHeadAddress); // IPAddress.Parse(Properties.Settings.Default.RoboHeadAddress);
            this.MessagePort = messagePort; // Properties.Settings.Default.MessagePort;
            this.SingleMessageRepetitionsCount = 3;
        }

        /// <summary>
        /// Gets Адрес головы робота.
        /// </summary>
        public IPAddress RoboHeadAddress { get; private set; }

        /// <summary>
        /// Gets Порт для передачи команд голове робота.
        /// </summary>
        public int MessagePort { get; private set; }

        /// <summary>
        /// Gets or sets Количество повторений для одиночных команд. Например, когда по UDP передаётся команда включить фары, она дублируется несколько раз. Для автоматически повторяющихся команд, это не делается.
        /// </summary>
        public byte SingleMessageRepetitionsCount { get; set; }
    }
}
