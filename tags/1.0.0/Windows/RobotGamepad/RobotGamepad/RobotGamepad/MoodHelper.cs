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

            if (mood == this.mood)
            {
                mood = Mood.Normal;
            }

            string command = this.GenerateCommand(mood);
            this.robotHelper.SendCommandToRobot(command);
            this.mood = mood;
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
        private string GenerateCommand(Mood mood)
        {   
            switch (mood)
            {
                case Mood.Happy:
                    return "MD001";
                case Mood.Blue:
                    return "MD002";
                case Mood.Angry:
                    return "MD003";
                case Mood.Disaster:
                    return "MD004";
                default:
                    return "MD000";
            }
        }
    }
}
