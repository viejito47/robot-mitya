// --------------------------------------------------------------------------------------------------------------------
// <copyright file="GunHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для организации работы с пушкой робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Microsoft.Xna.Framework;

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
        /// Время начала зарядки пушки.
        /// </summary>
        private DateTime? chargeStartTime;

        /// <summary>
        /// Проверка инициализации экземпляра класса для взаимодействия с роботом.
        /// </summary>
        private void CheckRobotHelper()
        {
            if (this.robotHelper == null)
            {
                throw new NullReferenceException("GunHelper не инициализирован.");
            }
        }

        /// <summary>
        /// Инициализация экземпляра класса для взаимодействия с роботом.
        /// </summary>
        /// <param name="robotHelper">Уже проинициализированный экземпляр.</param>
        public void Initialize(RobotHelper robotHelper)
        {
            this.robotHelper = robotHelper;
        }

        /// <summary>
        /// Получения текущего заряда пушки в процентах.
        /// </summary>
        /// <returns>
        /// Заряд пушки в процентах. Целое число от 0 до 100.
        /// </returns>
        public byte GetChargePercent()
        {
            if (chargeStartTime == null)
            {
                return 100;
            }

            double chargePercent = (DateTime.Now - (DateTime)chargeStartTime).TotalMilliseconds / Settings.GunChargeTime.TotalMilliseconds * 100;
            byte result = chargePercent > 100 ? Convert.ToByte(100) : Convert.ToByte(chargePercent);
            return result;
        }

        /// <summary>
        /// Выстрел. Формирует команду выстрела и передаёт роботу.
        /// </summary>
        public void Fire()
        {
            this.CheckRobotHelper();

            if (this.GetChargePercent() < 100)
            {
                return;
            }

            this.robotHelper.SendCommandToRobot("FR000");
            this.chargeStartTime = DateTime.Now;
        }
    }
}
