// --------------------------------------------------------------------------------------------------------------------
// <copyright file="GunHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для организации работы с пушкой робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboControl
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    using Microsoft.Xna.Framework;

    using RoboCommon;

    /// <summary>
    /// Вспомогательный класс, предназначенный для организации работы с пушкой робота.
    /// </summary>
    public sealed class GunHelper
    {
        /// <summary>
        /// Объект, упращающий взаимодействие с роботом.
        /// </summary>
        private RobotHelper robotHelper;

        /// <summary>
        /// Опции управления роботом.
        /// </summary>
        private ControlSettings controlSettings;

        /// <summary>
        /// Время начала зарядки пушки.
        /// </summary>
        private DateTime? chargeStartTime;

        /// <summary>
        /// Initializes a new instance of the GunHelper class.
        /// </summary>
        /// <param name="robotHelper">
        /// Объект для взаимодействия с головой робота.
        /// </param>
        /// <param name="controlSettings">
        /// Опции управления роботом.
        /// </param>
        public GunHelper(RobotHelper robotHelper, ControlSettings controlSettings)
        {
            if (robotHelper == null)
            {
                throw new ArgumentNullException("robotHelper");
            }

            if (controlSettings == null)
            {
                throw new ArgumentNullException("controlSettings");
            }

            this.robotHelper = robotHelper;
            this.controlSettings = controlSettings;
        }

        /// <summary>
        /// Получения текущего заряда пушки в процентах.
        /// </summary>
        /// <returns>
        /// Заряд пушки в процентах. Целое число от 0 до 100.
        /// </returns>
        public byte GetChargePercent()
        {
            if (this.chargeStartTime == null)
            {
                return 100;
            }

            double chargePercent = (DateTime.Now - (DateTime)this.chargeStartTime).TotalMilliseconds / this.controlSettings.GunChargeTime.TotalMilliseconds * 100;
            byte result = chargePercent > 100 ? Convert.ToByte(100) : Convert.ToByte(chargePercent);
            return result;
        }

        /// <summary>
        /// Выстрел. Формирует команду выстрела и передаёт роботу.
        /// </summary>
        public void Fire()
        {
            if (this.GetChargePercent() < 100)
            {
                return;
            }

            // Выстрел передаётся шестью сообщениями: 3 повторения сообщения на выстрел и 3 повторения на сброс значения.
            // Троекратное повторение требуется для увеличения надёжности передачи сообщения (всё-таки это UDP).
            // Сброс нужен из-за алгоритма приёма сообщений в голове робота. Там есть хэш-таблица команд роботу,
            // и одинаковые команды с повторяющимися значениями игнорируются. Поэтому чтобы выстрел обрабатывался
            // в следующий раз, значение "0001" для команды "h" надо сменить на "0000" в этой хэш-таблице.
            for (int i = 0; i < this.robotHelper.ConnectSettings.SingleMessageRepetitionsCount; i++)
            {
                this.robotHelper.SendMessageToRobot("s0001");
                this.robotHelper.SendMessageToRobot("s0001");
                this.robotHelper.SendMessageToRobot("s0001");
            }

            for (int i = 0; i < this.robotHelper.ConnectSettings.SingleMessageRepetitionsCount; i++)
            {
                this.robotHelper.SendMessageToRobot("s0000");
                this.robotHelper.SendMessageToRobot("s0000");
                this.robotHelper.SendMessageToRobot("s0000");
            }
            
            this.chargeStartTime = DateTime.Now;
        }
    }
}
