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
            if (this.chargeStartTime == null)
            {
                return 100;
            }

            double chargePercent = (DateTime.Now - (DateTime)this.chargeStartTime).TotalMilliseconds / Settings.GunChargeTime.TotalMilliseconds * 100;
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

            // Выстрел передаётся шестью сообщениями: 3 повторения сообщения на выстрел и 3 повторения на сброс значения.
            // Троекратное повторение требуется для увеличения надёжности передачи сообщения (всё-таки это UDP).
            // Сброс нужен из-за алгоритма приёма сообщений в голове робота. Там есть хэш-таблица команд роботу,
            // и одинаковые команды с повторяющимися значениями игнорируются. Поэтому чтобы выстрел обрабатывался
            // в следующий раз, значение "0001" для команды "h" надо сменить на "0000" в этой хэш-таблице.
            for (int i = 0; i < Settings.SingleMessageRepetitionsCount; i++)
            {
                this.robotHelper.SendMessageToRobot("f0001");
                this.robotHelper.SendMessageToRobot("f0001");
                this.robotHelper.SendMessageToRobot("f0001");
            }

            for (int i = 0; i < Settings.SingleMessageRepetitionsCount; i++)
            {
                this.robotHelper.SendMessageToRobot("f0000");
                this.robotHelper.SendMessageToRobot("f0000");
                this.robotHelper.SendMessageToRobot("f0000");
            }
            
            this.chargeStartTime = DateTime.Now;
        }

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
    }
}
