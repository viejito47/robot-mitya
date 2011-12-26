// --------------------------------------------------------------------------------------------------------------------
// <copyright file="FlashlightHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для организации работы с фонарём робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Вспомогательный класс, предназначенный для организации работы с фонарём робота.
    /// </summary>
    public class FlashlightHelper
    {
        /// <summary>
        /// Объект, упращающий взаимодействие с роботом.
        /// </summary>
        private RobotHelper robotHelper;
        
        /// <summary>
        /// Признак включённости фонаря.
        /// </summary>
        private bool flashlightTurnedOn;

        /// <summary>
        /// Последняя команда управления фонарём, переданная роботу.
        /// </summary>
        private string lastFlashlightCommand = string.Empty;

        /// <summary>
        /// Формирование команды включения фонаря.
        /// </summary>
        private void GenerateFlashlightTurnOnCommand()
        {
            this.lastFlashlightCommand = "FL001";
        }

        /// <summary>
        /// Формирование команды выключения фонаря.
        /// </summary>
        private void GenerateFlashlightTurnOffCommand()
        {
            this.lastFlashlightCommand = "FL000";
        }

        /// <summary>
        /// Формирование команды изменения состояния фар робота - вкл./выкл.
        /// </summary>
        private void GenerateSwitchFlashlightCommand()
        {
            if (this.flashlightTurnedOn)
            {
                this.GenerateFlashlightTurnOffCommand();
            }
            else
            {
                this.GenerateFlashlightTurnOnCommand();
            }
        }

        /// <summary>
        /// Проверка инициализации экземпляра класса для взаимодействия с роботом.
        /// </summary>
        private void CheckRobotHelper()
        {
            if (this.robotHelper == null)
            {
                throw new NullReferenceException("FlashlightHelper не инициализирован.");
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
        /// Состояние фар робота.
        /// </summary>
        public bool FlashlightTurnedOn { get { return this.flashlightTurnedOn; } }

        /// <summary>
        /// Последняя команда управления фонарём, переданная роботу.
        /// </summary>
        public string LastFlashlightCommand { get { return this.lastFlashlightCommand; } }

        /// <summary>
        /// Включить фонарь робота.
        /// </summary>
        public void TurnOn()
        {
            this.CheckRobotHelper();
            this.GenerateFlashlightTurnOnCommand();
            this.flashlightTurnedOn = true;
            this.robotHelper.SendCommandToRobot(this.LastFlashlightCommand);
        }

        /// <summary>
        /// Выключить фонарь робота.
        /// </summary>
        public void TurnOff()
        {
            this.CheckRobotHelper();
            this.GenerateFlashlightTurnOffCommand();
            this.flashlightTurnedOn = false;
            this.robotHelper.SendCommandToRobot(this.LastFlashlightCommand);
        }

        /// <summary>
        /// Переключить состояние фонаря робота.
        /// </summary>
        public void Switch()
        {
            this.CheckRobotHelper();
            this.GenerateSwitchFlashlightCommand();
            this.flashlightTurnedOn = !this.flashlightTurnedOn;
            this.robotHelper.SendCommandToRobot(this.LastFlashlightCommand);
        }
    }
}
