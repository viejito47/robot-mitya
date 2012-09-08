// --------------------------------------------------------------------------------------------------------------------
// <copyright file="Settings.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс, хранящий настройки приложения.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboControl
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Linq;
    using System.Net;
    using System.Text;

    /// <summary>
    /// Класс, хранящий настройки приложения.
    /// </summary>
    /// <remarks>
    /// Класс должен взаимодействовать с файлом конфигурации приложения (пока не сделано).
    /// </remarks>
    public static class Settings
    {
        /// <summary>
        /// Initializes static members of the Settings class.
        /// </summary>
        static Settings()
        {
            SinAlphaBound = 0.06;

            HorizontalMinimumDegree = 0;
            HorizontalForwardDegree = 90;
            HorizontalMaximumDegree = 180;

            VerticalMinimumDegree1 = 0;
            VerticalForwardDegree1 = 30;
            VerticalMaximumDegree1 = 60;

            VerticalMinimumDegree2 = 0;
            VerticalForwardDegree2 = 45;
            VerticalMaximumDegree2 = 90;

            VerticalMinimumDegree = VerticalMinimumDegree1;
            VerticalForwardDegree = VerticalForwardDegree1;
            VerticalMaximumDegree = VerticalMaximumDegree1;

            VerticalReadyToPlayDegree = 50;

            HorizontalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду
            HorizontalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд
            VerticalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду
            VerticalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд

            ReverseHeadTangage = Properties.Settings.Default.ReverseHeadTangage;

            MinCommandInterval = new TimeSpan(0, 0, 0, 0, 20);

            RoboHeadAddress = IPAddress.Parse(Properties.Settings.Default.RoboHeadAddress);

            IpWebcamPort = Properties.Settings.Default.IpWebcamPort;

            MessagePort = Properties.Settings.Default.MessagePort;

            DriveModeNormalMaxSpeed = Properties.Settings.Default.DriveModeNormalMaxSpeed;
            DriveModeTurboMaxSpeed = Properties.Settings.Default.DriveModeTurboMaxSpeed;

            GunChargeTime = new TimeSpan(0, 0, 5);

            SingleMessageRepetitionsCount = 3;

            Speed1 = Properties.Settings.Default.Speed1;
            Speed2 = Properties.Settings.Default.Speed2;
            Speed3 = Properties.Settings.Default.Speed3;
            Speed4 = Properties.Settings.Default.Speed4;
            Speed5 = Properties.Settings.Default.Speed5;
        }

        /// <summary>
        /// Gets Граница для "малых" углов. Углы, синус которых превышает заданную веричину не считаются "малыми".
        /// Используется вместе с признаком режима разворота для разворота робота на месте.
        /// </summary>
        public static double SinAlphaBound { get; private set; }

        /// <summary>
        /// Gets Минимальный угол поворота сервопривода, управляющего горизонтальным поворотом головы.
        /// </summary>
        public static int HorizontalMinimumDegree { get; private set; }

        /// <summary>
        /// Gets Угол поворота сервопривода, управляющего горизонтальным поворотом головы, соответствующий центральной позиции.
        /// </summary>
        public static int HorizontalForwardDegree { get; private set; }
        
        /// <summary>
        /// Gets Максимальный угол поворота сервопривода, управляющего горизонтальным поворотом головы.
        /// </summary>
        public static int HorizontalMaximumDegree { get; private set; }

        /// <summary>
        /// Gets Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (обычный режим).
        /// </summary>        
        public static int VerticalMinimumDegree1 { get; private set; }

        /// <summary>
        /// Gets Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (обычный режим).
        /// </summary>
        public static int VerticalForwardDegree1 { get; private set; }

        /// <summary>
        /// Gets Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (обычный режим).
        /// </summary>
        public static int VerticalMaximumDegree1 { get; private set; }

        /// <summary>
        /// Gets Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (прогулочный режим).
        /// </summary>        
        public static int VerticalMinimumDegree2 { get; private set; }

        /// <summary>
        /// Gets Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (прогулочный режим).
        /// </summary>
        public static int VerticalForwardDegree2 { get; private set; }

        /// <summary>
        /// Gets Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (прогулочный режим).
        /// </summary>
        public static int VerticalMaximumDegree2 { get; private set; }

        /// <summary>
        /// Gets or sets Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (текущий режим).
        /// </summary>        
        public static int VerticalMinimumDegree { get; set; }
        
        /// <summary>
        /// Gets or sets Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (текущий режим).
        /// </summary>
        public static int VerticalForwardDegree { get; set; }
        
        /// <summary>
        /// Gets or sets Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (текущий режим).
        /// </summary>
        public static int VerticalMaximumDegree { get; set; }

        /// <summary>
        /// Gets Угол поворота сервопривода, управляющего вертикальным поворотом головы для игры.
        /// </summary>
        public static int VerticalReadyToPlayDegree { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее высокой скорости горизонтального поворота головы.
        /// </summary>
        public static float HorizontalHighSpeed { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее низкой скорости горизонтального поворота головы.
        /// </summary>
        public static float HorizontalLowSpeed { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее высокой скорости вертикального поворота головы.
        /// </summary>
        public static float VerticalHighSpeed { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее низкой скорости вертикального поворота головы.
        /// </summary>
        public static float VerticalLowSpeed { get; private set; }

        /// <summary>
        /// Gets a value indicating whether управление движением головы по Y-координате перевёрнуто.
        /// </summary>
        public static bool ReverseHeadTangage { get; private set; }

        /// <summary>
        /// Gets Период опроса джойстика движения и нефиксированного поворота головы.
        /// </summary>
        public static TimeSpan MinCommandInterval { get; private set; }

        /// <summary>
        /// Gets Адрес головы робота.
        /// </summary>
        public static IPAddress RoboHeadAddress { get; private set; }

        /// <summary>
        /// Gets Порт для связи с IP Webcam.
        /// </summary>
        public static int IpWebcamPort { get; private set; }

        /// <summary>
        /// Gets Порт для передачи команд голове робота.
        /// </summary>
        public static int MessagePort { get; private set; }

        /// <summary>
        /// Gets or sets Определяет скорость в нормальном (не турбо) режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeNormalMaxSpeed / 255.
        /// </summary>
        public static byte DriveModeNormalMaxSpeed { get; set; }

        /// <summary>
        /// Gets or sets Определяет скорость в турбо-режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeTurboMaxSpeed / 255.
        /// </summary>
        public static byte DriveModeTurboMaxSpeed { get; set; }

        /// <summary>
        /// Gets Время "заряда" пушки - минимальный временной интервал между выстрелами.
        /// </summary>
        public static TimeSpan GunChargeTime { get; private set; }

        /// <summary>
        /// Gets Количество повторений для одиночных команд. Например, когда по UDP передаётся команда включить фары, она дублируется несколько раз. Для автоматически повторяющихся команд, это не делается.
        /// </summary>
        public static byte SingleMessageRepetitionsCount { get; private set; }

        /// <summary>
        /// Gets 1-ая скорость при управлении от клавиатуры.
        /// </summary>
        public static byte Speed1 { get; private set; }

        /// <summary>
        /// Gets 2-ая скорость при управлении от клавиатуры.
        /// </summary>
        public static byte Speed2 { get; private set; }

        /// <summary>
        /// Gets 3-ья скорость при управлении от клавиатуры.
        /// </summary>
        public static byte Speed3 { get; private set; }

        /// <summary>
        /// Gets 4-ая скорость при управлении от клавиатуры.
        /// </summary>
        public static byte Speed4 { get; private set; }

        /// <summary>
        /// Gets 5-ая скорость при управлении от клавиатуры.
        /// </summary>
        public static byte Speed5 { get; private set; }
    }
}
