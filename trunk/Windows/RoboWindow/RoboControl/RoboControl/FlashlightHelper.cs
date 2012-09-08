// --------------------------------------------------------------------------------------------------------------------
// <copyright file="FlashlightHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для организации работы с фонарём робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboControl
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
        private string flashlightCommand = string.Empty;

        /// <summary>
        /// Gets a value indicating whether Состояние фар робота.
        /// </summary>
        public bool FlashlightTurnedOn 
        { 
            get 
            { 
                return this.flashlightTurnedOn; 
            } 
        }

        /// <summary>
        /// Gets Последняя команда управления фонарём, переданная роботу.
        /// </summary>
        public string FlashlightCommand 
        { 
            get 
            { 
                return this.flashlightCommand; 
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
        /// Включить фонарь робота.
        /// </summary>
        public void TurnOn()
        {
            this.CheckRobotHelper();
            this.GenerateFlashlightTurnOnCommand();
            this.flashlightTurnedOn = true;

            for (int i = 0; i < Settings.SingleMessageRepetitionsCount; i++)
            {
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
            }
        }

        /// <summary>
        /// Выключить фонарь робота.
        /// </summary>
        public void TurnOff()
        {
            this.CheckRobotHelper();
            this.GenerateFlashlightTurnOffCommand();
            this.flashlightTurnedOn = false;

            for (int i = 0; i < Settings.SingleMessageRepetitionsCount; i++)
            {
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
            }
        }

        /// <summary>
        /// Переключить состояние фонаря робота.
        /// </summary>
        public void Switch()
        {
            this.CheckRobotHelper();
            this.GenerateSwitchFlashlightCommand();
            this.flashlightTurnedOn = !this.flashlightTurnedOn;

            for (int i = 0; i < Settings.SingleMessageRepetitionsCount; i++)
            {
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
                this.robotHelper.SendMessageToRobot(this.FlashlightCommand);
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
        /// Формирование команды включения фонаря.
        /// </summary>
        private void GenerateFlashlightTurnOnCommand()
        {
            // this.flashlightCommand = "Z0001"; (проверка вывода сообщения о ошибке - неизвестная команды)
            this.flashlightCommand = "I0001";
        }

        /// <summary>
        /// Формирование команды выключения фонаря.
        /// </summary>
        private void GenerateFlashlightTurnOffCommand()
        {
            // this.flashlightCommand = "Z0000"; (проверка вывода сообщения о ошибке - неизвестная команды)
            this.flashlightCommand = "I0000";
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
    }
}
