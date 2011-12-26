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
    using System.Text;
    using System.Net;

    /// <summary>
    /// Класс, хранящий настройки приложения.
    /// </summary>
    /// <remarks>
    /// Класс должен взаимодействовать с файлом конфигурации приложения (пока не сделано).
    /// </remarks>
    public static class Settings
    {
        /// <summary>
        /// Граница для "малых" углов. Углы, синус которых превышает заданную веричину не считаются "малыми".
        /// Используется вместе с признаком режима разворота для разворота робота на месте.
        /// </summary>
        public static double SinAlphaBound = 0.06;


        /// <summary>
        /// Признак повтора одинаковых команд. Если установлен в false, подряд идущие обинаковые команда не 
        /// повторяются.
        /// </summary>
        public static bool RepeatCommandsFlag = false;

        
        /// <summary>
        /// Минимальный угол поворота сервопривода, управляющего горизонтальным поворотом головы.
        /// </summary>
        public static int HorizontalMinimumDegree = 0;
        /// <summary>
        /// Угол поворота сервопривода, управляющего горизонтальным поворотом головы, соответствующий центральной позиции.
        /// </summary>
        public static int HorizontalForwardDegree = 90;
        /// <summary>
        /// Максимальный угол поворота сервопривода, управляющего горизонтальным поворотом головы.
        /// </summary>
        public static int HorizontalMaximumDegree = 180;


        /// <summary>
        /// Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (обычный режим).
        /// </summary>        
        public static int VerticalMinimumDegree1 = 0;

        /// <summary>
        /// Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (обычный режим).
        /// </summary>
        public static int VerticalForwardDegree1 = 45;

        /// <summary>
        /// Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (обычный режим).
        /// </summary>
        public static int VerticalMaximumDegree1 = 90;


        /// <summary>
        /// Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (боевой режим).
        /// </summary>        
        public static int VerticalMinimumDegree2 = 0;

        /// <summary>
        /// Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (боевой режим).
        /// </summary>
        public static int VerticalForwardDegree2 = 30;

        /// <summary>
        /// Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (боевой режим).
        /// </summary>
        public static int VerticalMaximumDegree2 = 60;


        /// <summary>
        /// Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (текущий режим).
        /// </summary>        
        public static int VerticalMinimumDegree = VerticalMinimumDegree1;
        
        /// <summary>
        /// Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (текущий режим).
        /// </summary>
        public static int VerticalForwardDegree = VerticalForwardDegree1;
        
        /// <summary>
        /// Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (текущий режим).
        /// </summary>
        public static int VerticalMaximumDegree = VerticalMaximumDegree1;

        
        /// <summary>
        /// Значение, соответствующее высокой скорости горизонтального поворота головы.
        /// </summary>
        public static float HorizontalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду

        /// <summary>
        /// Значение, соответствующее низкой скорости горизонтального поворота головы.
        /// </summary>
        public static float HorizontalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд

        /// <summary>
        /// Значение, соответствующее высокой скорости вертикального поворота головы.
        /// </summary>
        public static float VerticalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду

        /// <summary>
        /// Значение, соответствующее низкой скорости вертикального поворота головы.
        /// </summary>
        public static float VerticalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд


        /// <summary>
        /// Период опроса джойстика движения и нефиксированного поворота головы.
        /// </summary>
        public static TimeSpan minCommandInterval = new TimeSpan(0, 0, 0, 0, 20);


        /// <summary>
        /// Адрес серверного сокета.
        /// </summary>
        public static IPAddress TcpSocketServerAddress
        {
            get
            {
                Byte[] serverAddress = { 192, 168, 1, 1 };
                //Byte[] serverAddress = { 192, 168, 1, 40 };
                return new IPAddress(serverAddress);
            }
        }

        /// <summary>
        /// Порт для связи с сервером.
        /// </summary>
        public static int TcpSocketServerPort = 51974;


        /// <summary>
        /// Определяет скорость в нормальном (не турбо) режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeNormalCoef / 255.
        /// </summary>
        public static Byte DriveModeNormalCoef = 190;

        /// <summary>
        /// Определяет скорость в турбо-режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeTurboCoef / 255.
        /// </summary>
        public static Byte DriveModeTurboCoef = 255;


        /// <summary>
        /// Время "заряда" пушки - минимальный временной интервал между выстрелами.
        /// </summary>
        public static TimeSpan GunChargeTime = new TimeSpan(0, 0, 5);


        /// <summary>
        /// Статический конструктор класса.
        /// </summary>
        static Settings()
        {
            //Settings.SinAlphaBound += 1;
            //Settings.SinAlphaBound -= 1;

            //Settings.RepeatCommandsFlag = false;
            //Settings.RepeatCommandsFlag = true;
        }
    }
}
