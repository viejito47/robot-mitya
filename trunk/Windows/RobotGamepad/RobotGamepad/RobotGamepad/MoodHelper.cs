// --------------------------------------------------------------------------------------------------------------------
// <copyright file="MoodHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для управления настроением робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Настроения робота.
    /// </summary>
    public enum Mood 
    { 
        /// <summary>
        /// Нормальное настроение.
        /// </summary>
        Normal,

        /// <summary>
        /// Счастливое настроение.
        /// </summary>
        Happy,

        /// <summary>
        /// Грустное настроение.
        /// </summary>
        Blue,

        /// <summary>
        /// Злое настроение.
        /// </summary>
        Angry,

        /// <summary>
        /// Убитое настроение.
        /// </summary>
        Disaster
    }

    /// <summary>
    /// Вспомогательный класс, предназначенный для управления настроением робота.
    /// </summary>
    public sealed class MoodHelper
    {
        /// <summary>
        /// Объект, упращающий взаимодействие с роботом.
        /// </summary>
        private RobotHelper robotHelper;

        /// <summary>
        /// Текущее настроение робота.
        /// </summary>
        private Mood mood = Mood.Normal;

        /// <summary>
        /// Gets Текущее настроение робота.
        /// </summary>
        public Mood Mood
        {
            get
            {
                return this.mood;
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
        /// Установка настроения робота.
        /// </summary>
        /// <param name="mood">
        /// Новое настроение.
        /// </param>
        public void SetMood(Mood mood)
        {
            this.CheckRobotHelper();

            //if (mood == this.mood)
            //{
            //    mood = Mood.Normal;
            //}

            string command = this.GenerateMoodCommand(mood);
            this.robotHelper.SendNonrecurrentMessageToRobot(command, "F0000");
            this.mood = mood;
        }

        /// <summary>
        /// Генерация и передача команды виляния хвостом.
        /// </summary>
        public void WagTail()
        {
            this.robotHelper.SendNonrecurrentMessageToRobot("t0002", "t0000");
        }

        /// <summary>
        /// Генерация и передача команды кивания головой (ответ "да").
        /// </summary>
        public void WagYes()
        {
            this.robotHelper.SendNonrecurrentMessageToRobot("y0002", "y0000");
        }

        /// <summary>
        /// Генерация и передача команды кивания головой (ответ "нет").
        /// </summary>
        public void WagNo()
        {
            this.robotHelper.SendNonrecurrentMessageToRobot("n0002", "n0000");
        }

        /// <summary>
        /// Проверка инициализации экземпляра класса для взаимодействия с роботом.
        /// </summary>
        private void CheckRobotHelper()
        {
            if (this.robotHelper == null)
            {
                throw new NullReferenceException("MoodHelper не инициализирован.");
            }
        }

        /// <summary>
        /// Формирование команды для задания настроения роботу.
        /// </summary>
        /// <param name="mood">Новое настроение.</param>
        /// <returns>Текст команды.</returns>
        private string GenerateMoodCommand(Mood mood)
        {   
            switch (mood)
            {
                case Mood.Normal:
                    return "F0001";
                case Mood.Happy:
                    return "F0002";
                case Mood.Blue:
                    return "F0003";
                case Mood.Angry:
                    return "F0004";
                case Mood.Disaster:
                    return "F0005";
                default:
                    return "F0000";
            }
        }
    }
}
