// --------------------------------------------------------------------------------------------------------------------
// <copyright file="Settings.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс, хранящий настройки приложения.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
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
            VerticalForwardDegree1 = 45;
            VerticalMaximumDegree1 = 90;

            VerticalMinimumDegree2 = 0;
            VerticalForwardDegree2 = 30;
            VerticalMaximumDegree2 = 60;

            VerticalMinimumDegree = VerticalMinimumDegree1;
            VerticalForwardDegree = VerticalForwardDegree1;
            VerticalMaximumDegree = VerticalMaximumDegree1;

            HorizontalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду
            HorizontalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд
            VerticalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду
            VerticalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд

            MinCommandInterval = new TimeSpan(0, 0, 0, 0, 20);

            // byte[] roboHeadAddress = { 192, 168, 1, 1 };
            byte[] roboHeadAddress = { 192, 168, 1, 40 };
            RoboHeadAddress = new IPAddress(roboHeadAddress);

            IpWebcamPort = 8080;

            CommandPort = 51974;

            DriveModeNormalCoef = 190;
            DriveModeTurboCoef = 255;

            GunChargeTime = new TimeSpan(0, 0, 5);

            SingleMessageRepetitionsCount = 3;
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
        /// Gets Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (боевой режим).
        /// </summary>        
        public static int VerticalMinimumDegree2 { get; private set; }

        /// <summary>
        /// Gets Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (боевой режим).
        /// </summary>
        public static int VerticalForwardDegree2 { get; private set; }

        /// <summary>
        /// Gets Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (боевой режим).
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
        public static int CommandPort { get; private set; }

        /// <summary>
        /// Gets Определяет скорость в нормальном (не турбо) режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeNormalCoef / 255.
        /// </summary>
        public static byte DriveModeNormalCoef { get; private set; }

        /// <summary>
        /// Gets Определяет скорость в турбо-режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeTurboCoef / 255.
        /// </summary>
        public static byte DriveModeTurboCoef { get; private set; }

        /// <summary>
        /// Gets Время "заряда" пушки - минимальный временной интервал между выстрелами.
        /// </summary>
        public static TimeSpan GunChargeTime { get; private set; }

        public static byte SingleMessageRepetitionsCount { get; private set; }
    }
}
