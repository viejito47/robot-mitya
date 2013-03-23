// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ControlSettings.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс, хранящий настройки приложения для управления роботом.
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
    public class ControlSettings
    {
        /// <summary>
        /// Initializes a new instance of the ControlSettings class.
        /// </summary>
        public ControlSettings()
        {
            this.SinAlphaBound = 0.06;

            this.HorizontalMinimumDegree = 0;
            this.HorizontalForwardDegree = 90;
            this.HorizontalMaximumDegree = 180;

            this.VerticalMinimumDegree1 = 0;
            this.VerticalForwardDegree1 = 30;
            this.VerticalMaximumDegree1 = 60;

            this.VerticalMinimumDegree2 = 0;
            this.VerticalForwardDegree2 = 45;
            this.VerticalMaximumDegree2 = 90;

            this.VerticalMinimumDegree = this.VerticalMinimumDegree1;
            this.VerticalForwardDegree = this.VerticalForwardDegree1;
            this.VerticalMaximumDegree = this.VerticalMaximumDegree1;

            this.VerticalReadyToPlayDegree = 50;

            this.HorizontalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду
            this.HorizontalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд
            this.VerticalHighSpeed = 180f / 1000f; // 180 градусов за 1 секунду
            this.VerticalLowSpeed = 180f / 5000f; // 180 градусов за 5 секунд

            this.MinCommandInterval = new TimeSpan(0, 0, 0, 0, 20);

            this.GunChargeTime = new TimeSpan(0, 0, 5);

            // Значения по умолчанию для параметров, переопределяемых в конфигурационном файле:
            this.ReverseHeadTangage = false;
            this.IpWebcamPort = 8080;
            this.DriveModeNormalMaxSpeed = 255;
            this.DriveModeTurboMaxSpeed = 255;
            this.Speed1 = 51;
            this.Speed2 = 102;
            this.Speed3 = 153;
            this.Speed4 = 204;
            this.Speed5 = 255;
            this.SlowHeadTurnPeriod = 1000;
            this.FastHeadTurnPeriod = 170;

            this.RoboScripts = new RoboScriptItem[10];
            for (int i = this.RoboScripts.GetLowerBound(0); i <= this.RoboScripts.GetUpperBound(0); i++)
            {
                this.RoboScripts[i] = new RoboScriptItem((byte)i);
            }
        }

        /// <summary>
        /// Gets Граница для "малых" углов. Углы, синус которых превышает заданную веричину не считаются "малыми".
        /// Используется вместе с признаком режима разворота для разворота робота на месте.
        /// </summary>
        public double SinAlphaBound { get; private set; }

        /// <summary>
        /// Gets Минимальный угол поворота сервопривода, управляющего горизонтальным поворотом головы.
        /// </summary>
        public int HorizontalMinimumDegree { get; private set; }

        /// <summary>
        /// Gets Угол поворота сервопривода, управляющего горизонтальным поворотом головы, соответствующий центральной позиции.
        /// </summary>
        public int HorizontalForwardDegree { get; private set; }
        
        /// <summary>
        /// Gets Максимальный угол поворота сервопривода, управляющего горизонтальным поворотом головы.
        /// </summary>
        public int HorizontalMaximumDegree { get; private set; }

        /// <summary>
        /// Gets or sets Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (обычный режим).
        /// </summary>        
        public int VerticalMinimumDegree1 { get; set; }

        /// <summary>
        /// Gets or sets Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (обычный режим).
        /// </summary>
        public int VerticalForwardDegree1 { get; set; }

        /// <summary>
        /// Gets or sets Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (обычный режим).
        /// </summary>
        public int VerticalMaximumDegree1 { get; set; }

        /// <summary>
        /// Gets or sets Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (прогулочный режим).
        /// </summary>        
        public int VerticalMinimumDegree2 { get; set; }

        /// <summary>
        /// Gets or sets Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (прогулочный режим).
        /// </summary>
        public int VerticalForwardDegree2 { get; set; }

        /// <summary>
        /// Gets or sets Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (прогулочный режим).
        /// </summary>
        public int VerticalMaximumDegree2 { get; set; }

        /// <summary>
        /// Gets or sets Минимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (текущий режим).
        /// </summary>        
        public int VerticalMinimumDegree { get; set; }
        
        /// <summary>
        /// Gets or sets Угол поворота сервопривода, управляющего вертикальным поворотом головы, соответствующий 
        /// центральной позиции (текущий режим).
        /// </summary>
        public int VerticalForwardDegree { get; set; }
        
        /// <summary>
        /// Gets or sets Максимальный угол поворота сервопривода, управляющего вертикальным поворотом головы (текущий режим).
        /// </summary>
        public int VerticalMaximumDegree { get; set; }

        /// <summary>
        /// Gets Угол поворота сервопривода, управляющего вертикальным поворотом головы для игры.
        /// </summary>
        public int VerticalReadyToPlayDegree { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее высокой скорости горизонтального поворота головы.
        /// </summary>
        public float HorizontalHighSpeed { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее низкой скорости горизонтального поворота головы.
        /// </summary>
        public float HorizontalLowSpeed { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее высокой скорости вертикального поворота головы.
        /// </summary>
        public float VerticalHighSpeed { get; private set; }

        /// <summary>
        /// Gets Значение, соответствующее низкой скорости вертикального поворота головы.
        /// </summary>
        public float VerticalLowSpeed { get; private set; }

        /// <summary>
        /// Gets or sets a value indicating whether управление движением головы по Y-координате перевёрнуто.
        /// </summary>
        public bool ReverseHeadTangage { get; set; }

        /// <summary>
        /// Gets Период опроса джойстика движения и нефиксированного поворота головы.
        /// </summary>
        public TimeSpan MinCommandInterval { get; private set; }

        /// <summary>
        /// Gets or sets Порт для связи с IP Webcam.
        /// </summary>
        public int IpWebcamPort { get; set; }

        /// <summary>
        /// Gets or sets Определяет скорость в нормальном (не турбо) режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeNormalMaxSpeed / 255.
        /// </summary>
        public byte DriveModeNormalMaxSpeed { get; set; }

        /// <summary>
        /// Gets or sets Определяет скорость в турбо-режиме движения. 
        /// Окончательная скорость определяется умножением на коэффициент, равный DriveModeTurboMaxSpeed / 255.
        /// </summary>
        public byte DriveModeTurboMaxSpeed { get; set; }

        /// <summary>
        /// Gets Время "заряда" пушки - минимальный временной интервал между выстрелами.
        /// </summary>
        public TimeSpan GunChargeTime { get; private set; }

        /// <summary>
        /// Gets or sets 1-ая скорость при управлении от клавиатуры.
        /// </summary>
        public byte Speed1 { get; set; }

        /// <summary>
        /// Gets or sets 2-ая скорость при управлении от клавиатуры.
        /// </summary>
        public byte Speed2 { get; set; }

        /// <summary>
        /// Gets or sets 3-ья скорость при управлении от клавиатуры.
        /// </summary>
        public byte Speed3 { get; set; }

        /// <summary>
        /// Gets or sets 4-ая скорость при управлении от клавиатуры.
        /// </summary>
        public byte Speed4 { get; set; }

        /// <summary>
        /// Gets or sets 5-ая скорость при управлении от клавиатуры.
        /// </summary>
        public byte Speed5 { get; set; }

        /// <summary>
        /// Gets or sets slow head turn period in sentiseconds.
        /// </summary>
        public int SlowHeadTurnPeriod { get; set; }

        /// <summary>
        /// Gets or sets fast head turn period in sentiseconds.
        /// </summary>
        public int FastHeadTurnPeriod { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether видео воспроизводится.
        /// </summary>
        public bool PlayVideo { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether звук воспроизводится.
        /// </summary>
        public bool PlayAudio { get; set; }

        /// <summary>
        /// Gets массив РобоСкриптов.
        /// </summary>
        public RoboScriptItem[] RoboScripts { get; private set; }
    }
}
