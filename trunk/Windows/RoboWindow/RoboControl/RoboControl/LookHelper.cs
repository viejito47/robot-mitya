﻿// --------------------------------------------------------------------------------------------------------------------
// <copyright file="LookHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для управления поворотами головы робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboControl
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Microsoft.Xna.Framework;

    using RoboCommon;

    /// <summary>
    /// Вспомогательный класс, предназначенный для управления поворотами головы робота.
    /// </summary>
    public class LookHelper
    {
        /// <summary>
        /// Объект, упращающий взаимодействие с роботом.
        /// </summary>
        private RobotHelper robotHelper;

        /// <summary>
        /// Опции управления роботом.
        /// </summary>
        private ControlSettings controlSettings;

        /// <summary>
        /// Признак медленного поворота головы.
        /// </summary>
        /// <remarks>
        /// Используется только в режиме фиксированного обзора (джойстик DPAD).
        /// </remarks>
        private bool fastModeOn;

        /// <summary>
        /// Признак управления поворотами головы в режиме фиксации горизонтального угла.
        /// </summary>
        private bool horizontalFixedControl;

        /// <summary>
        /// Признак управления поворотами головы в режиме фиксации вертикального угла.
        /// </summary>
        private bool verticalFixedControl;

        /// <summary>
        /// Последняя обработанная x-координата (горизонтальная) для режима обзора без фиксации.
        /// </summary>
        private float lookX = 0;

        /// <summary>
        /// Последняя обработанная y-координата (вертикальная) для режима обзора без фиксации.
        /// </summary>
        private float lookY = 0;

        /// <summary>
        /// Последняя обработанная x-координата (горизонтальная) ThumbStick-джойстика для режима обзора с фиксацией.
        /// </summary>
        private float fixedLookX;

        /// <summary>
        /// Последняя обработанная y-координата (вертикальная) ThumbStick-джойстика для режима обзора с фиксацией.
        /// </summary>
        private float fixedLookY;

        /// <summary>
        /// Последняя отправленная "горизонтальному" сервоприводу команда.
        /// </summary>
        private string horizontalServoCommand = string.Empty;

        /// <summary>
        /// Последняя отправленная "вертикальному" сервоприводу команда.
        /// </summary>
        private string verticalServoCommand = string.Empty;

        /// <summary>
        /// Initializes a new instance of the LookHelper class.
        /// </summary>
        /// <param name="robotHelper">
        /// Объект для взаимодействия с головой робота.
        /// </param>
        /// <param name="controlSettings">
        /// Опции управления роботом.
        /// </param>
        public LookHelper(RobotHelper robotHelper, ControlSettings controlSettings)
        {
            if (robotHelper == null)
            {
                throw new ArgumentNullException("robotHelper");
            }

            if (controlSettings == null)
            {
                throw new ArgumentNullException("controlSettings");
            }

            this.robotHelper = robotHelper;
            this.controlSettings = controlSettings;

            this.fixedLookX = this.controlSettings.HorizontalForwardDegree;
            this.fixedLookY = this.controlSettings.VerticalForwardDegree;
        }

        /// <summary>
        /// Gets Последняя команда, переданная "горизонтальному" сервоприводу.
        /// </summary>
        public string HorizontalServoCommand 
        { 
            get 
            { 
                return this.horizontalServoCommand; 
            } 
        }

        /// <summary>
        /// Gets Последняя команда, переданная "вертикальному" сервоприводу.
        /// </summary>
        public string VerticalServoCommand 
        { 
            get 
            { 
                return this.verticalServoCommand; 
            } 
        }

        /// <summary>
        /// Gets or sets a value indicating whether установлен режим быстрого поворота головы.
        /// </summary>
        /// <remarks>
        /// Используется только в режиме фиксированного обзора (джойстик DPAD или клавиатура).
        /// </remarks>
        public bool FastModeOn
        {
            get
            {
                return this.fastModeOn;
            }

            set
            {
                this.fastModeOn = value;
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether установлен "прогулочный" режим. В этом режиме центральное положение 
        /// ThumbStick-джойстика по вертикали соответствует направлению взгляда, параллельному плоскости поверхности 
        /// пола. Режим введён для упрощения управления движением. В "не прогулочном" режиме направление взгляда робота
        /// при центральном положении джойстика направлено чуть вверх. Это удобнее при общении с великанами.
        /// </summary>
        public bool WalkModeOn
        {
            get
            {
                return this.controlSettings.VerticalForwardDegree == this.controlSettings.VerticalForwardDegree1;
            }

            set
            {
                if (value)
                {
                    this.controlSettings.VerticalMinimumDegree = this.controlSettings.VerticalMinimumDegree1;
                    this.controlSettings.VerticalForwardDegree = this.controlSettings.VerticalForwardDegree1;
                    this.controlSettings.VerticalMaximumDegree = this.controlSettings.VerticalMaximumDegree1;
                }
                else
                {
                    this.controlSettings.VerticalMinimumDegree = this.controlSettings.VerticalMinimumDegree2;
                    this.controlSettings.VerticalForwardDegree = this.controlSettings.VerticalForwardDegree2;
                    this.controlSettings.VerticalMaximumDegree = this.controlSettings.VerticalMaximumDegree2;
                }
            }
        }

        /// <summary>
        /// Gets Последняя обработанная x-координата (горизонтальная) ThumbStick-джойстика для режима обзора с фиксацией.
        /// </summary>
        public float FixedLookX 
        { 
            get 
            { 
                return this.fixedLookX; 
            } 
        }

        /// <summary>
        /// Gets Последняя обработанная y-координата (вертикальная) ThumbStick-джойстика для режима обзора с фиксацией.
        /// </summary>
        public float FixedLookY 
        { 
            get 
            { 
                return this.fixedLookY; 
            } 
        }

        /// <summary>
        /// Преобразование координат для поворота головы из пространства круга (область ThumbStick-джойстика)
        /// в пространсво квадрата (область поворота головы, доступная сервоприводам).
        /// </summary>
        /// <param name="x">x-координата. Инициируется x-координатой ThumbStick-джойстика. После работы функции
        /// заполняется x-координатой в квадратном пространстве.</param>
        /// <param name="y">y-координата. Инициируется x-координатой ThumbStick-джойстика. После работы функции
        /// заполняется y-координатой в квадратном пространстве.</param>
        /// <remarks>
        /// При координате джойстика (1, 0) голова повёрнута вправо (0 градусов). При координате (0, 1) голова 
        /// повёрнута вверх (0 градусов). Но если джойстик отклоняется не по горизонтали или вертикали, а по 
        /// диагонали, голова уже не сможет добраться до нуля градусов ни по одному, ни по другому сервоприводу. 
        /// Область джойстика окружность, поэтому, например, координата (1, 1) не будет доступна. Отсюда вывод: 
        /// круговую область джойстика с центром в начале координат и диаметром равным 2 надо «растянуть» на 
        /// квадратную область с тем же центром и стороной равной 2.
        /// </remarks>
        public static void CorrectCoordinatesFromCyrcleToSquareArea(ref float x, ref float y)
        {
            if ((x >= 0) && (y >= 0))
            {
                LookHelper.CorrectCoordinatesFromCyrcleToSquareAreaForFirstQuadrant(ref x, ref y);
            }
            else if ((x < 0) && (y >= 0))
            {
                x = -x;
                LookHelper.CorrectCoordinatesFromCyrcleToSquareAreaForFirstQuadrant(ref x, ref y);
                x = -x;
            }
            else if ((x < 0) && (y < 0))
            {
                x = -x;
                y = -y;
                LookHelper.CorrectCoordinatesFromCyrcleToSquareAreaForFirstQuadrant(ref x, ref y);
                x = -x;
                y = -y;
            }
            else if ((x >= 0) && (y < 0))
            {
                y = -y;
                LookHelper.CorrectCoordinatesFromCyrcleToSquareAreaForFirstQuadrant(ref x, ref y);
                y = -y;
            }

            x = x < -1 ? -1 : x;
            x = x > 1 ? 1 : x;
            y = y < -1 ? -1 : y;
            y = y > 1 ? 1 : y;
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
        /// Посылка команд в соответствии с координатами ThumbStick-джойстика.
        /// </summary>
        /// <param name="x">x-координата ThumbStick-джойстика.</param>
        /// <param name="y">y-координата ThumbStick-джойстика.</param>
        public void Look(float x, float y)
        {
            this.CheckRobotHelper();

            if (!this.controlSettings.ReverseHeadTangage)
            {
                y = -y;
            }

            LookHelper.CorrectCoordinatesFromCyrcleToSquareArea(ref x, ref y);

            if ((x != this.lookX) || (y != this.lookY))
            {
                if (this.horizontalFixedControl)
                {
                    this.horizontalFixedControl = false;
                    this.fixedLookX = this.controlSettings.HorizontalForwardDegree;
                }

                if (this.verticalFixedControl)
                {
                    this.verticalFixedControl = false;
                    this.fixedLookY = this.controlSettings.VerticalForwardDegree;
                }
            }

            if ((this.horizontalFixedControl == false) && (this.verticalFixedControl == false))
            {
                // x = f(x)...
                this.GenerateHorizontalServoCommand(x, out this.horizontalServoCommand);
                this.robotHelper.SendMessageToRobot(this.horizontalServoCommand);
                this.lookX = x;

                // y = f(y)...
                this.GenerateVerticalServoCommand(y, out this.verticalServoCommand);
                this.robotHelper.SendMessageToRobot(this.verticalServoCommand);
                this.lookY = y;
            }
        }

        /// <summary>
        /// Переход в режим фиксации углов и установка заданных углов.
        /// </summary>
        /// <param name="fixedX">Фиксированный угол по горизонтали.</param>
        /// <param name="fixedY">Фиксированный угол по вертикали.</param>
        public void FixedLook(float fixedX, float fixedY)
        {
            this.CheckRobotHelper();

            this.horizontalFixedControl = true;
            this.verticalFixedControl = true;
            this.fixedLookX = fixedX;
            this.fixedLookY = fixedY;
            this.lookX = 0;
            this.lookY = 0;

            this.GenerateHorizontalServoCommandByDegree(this.fixedLookX, out this.horizontalServoCommand);
            this.robotHelper.SendMessageToRobot(this.horizontalServoCommand);

            this.GenerateVerticalServoCommandByDegree(this.fixedLookY, out this.verticalServoCommand);
            this.robotHelper.SendMessageToRobot(this.verticalServoCommand);
        }

        /// <summary>
        /// Поворот головы в начальное положение (вперёд, для режима общения (не прогулочного) чуть вверх).
        /// </summary>
        public void LookForward()
        {
            this.CheckRobotHelper();

            this.fixedLookX = this.controlSettings.HorizontalForwardDegree;
            this.fixedLookY = this.controlSettings.VerticalForwardDegree;
            this.lookX = 0;
            this.lookY = 0;
            
            this.GenerateServoCommand(0, 0, out this.horizontalServoCommand, out this.verticalServoCommand);
            this.robotHelper.SendMessageToRobot(this.horizontalServoCommand);
            this.robotHelper.SendMessageToRobot(this.verticalServoCommand);
        }
        
        /// <summary>
        /// Поворот головы влево в режиме фиксации головы (DPad-джойстик).
        /// </summary>
        /// <param name="gameTime">Игровое время (время, прошедшее с момента последнего вызова).</param>
        /// <remarks>
        /// Изменение угла поворота головы определяется исходя из времени, прошедшего с момента последнего вызова, и
        /// константы скорости поворота головы.
        /// </remarks>
        public void FixedLookLeft(GameTime gameTime)
        {
            this.CheckRobotHelper();

            if (this.horizontalFixedControl == false)
            {
                this.horizontalFixedControl = true;
                this.lookX = 0;
            }

            this.IncrementHorizontalDegree(ref this.fixedLookX, gameTime);

            this.GenerateHorizontalServoCommandByDegree(this.fixedLookX, out this.horizontalServoCommand);
            this.robotHelper.SendMessageToRobot(this.horizontalServoCommand);
        }

        /// <summary>
        /// Поворот головы вправо в режиме фиксации головы (DPad-джойстик).
        /// </summary>
        /// <param name="gameTime">Игровое время (время, прошедшее с момента последнего вызова).</param>
        /// <remarks>
        /// Изменение угла поворота головы определяется исходя из времени, прошедшего с момента последнего вызова, и
        /// константы скорости поворота головы.
        /// </remarks>
        public void FixedLookRight(GameTime gameTime)
        {
            this.CheckRobotHelper();

            if (this.horizontalFixedControl == false)
            {
                this.horizontalFixedControl = true;
                this.lookX = 0;
            }

            this.DecrementHorizontalDegree(ref this.fixedLookX, gameTime);
            this.GenerateHorizontalServoCommandByDegree(this.fixedLookX, out this.horizontalServoCommand);
            this.robotHelper.SendMessageToRobot(this.horizontalServoCommand);
        }

        /// <summary>
        /// Поворот головы вверх в режиме фиксации головы (DPad-джойстик).
        /// </summary>
        /// <param name="gameTime">Игровое время (время, прошедшее с момента последнего вызова).</param>
        /// <remarks>
        /// Изменение угла поворота головы определяется исходя из времени, прошедшего с момента последнего вызова, и
        /// константы скорости поворота головы.
        /// </remarks>
        public void FixedLookUp(GameTime gameTime)
        {
            this.CheckRobotHelper();

            if (this.verticalFixedControl == false)
            {
                this.verticalFixedControl = true;
                this.lookY = 0;
            }

            if (this.controlSettings.ReverseHeadTangage)
            {
                this.DecrementVerticalDegree(ref this.fixedLookY, gameTime);
            }
            else
            {
                this.IncrementVerticalDegree(ref this.fixedLookY, gameTime);
            }

            this.GenerateVerticalServoCommandByDegree(this.fixedLookY, out this.verticalServoCommand);
            this.robotHelper.SendMessageToRobot(this.verticalServoCommand);
        }

        /// <summary>
        /// Поворот головы вниз в режиме фиксации головы (DPad-джойстик).
        /// </summary>
        /// <param name="gameTime">Игровое время (время, прошедшее с момента последнего вызова).</param>
        /// <remarks>
        /// Изменение угла поворота головы определяется исходя из времени, прошедшего с момента последнего вызова, и
        /// константы скорости поворота головы.
        /// </remarks>
        public void FixedLookDown(GameTime gameTime)
        {
            this.CheckRobotHelper();

            if (this.verticalFixedControl == false)
            {
                this.verticalFixedControl = true;
                this.lookY = 0;
            }

            if (this.controlSettings.ReverseHeadTangage)
            {
                this.IncrementVerticalDegree(ref this.fixedLookY, gameTime);
            }
            else
            {
                this.DecrementVerticalDegree(ref this.fixedLookY, gameTime);
            }

            this.GenerateVerticalServoCommandByDegree(this.fixedLookY, out this.verticalServoCommand);
            this.robotHelper.SendMessageToRobot(this.verticalServoCommand);
        }

        /// <summary>
        /// Преобразование координат для первой половины первого квадранта (остальные получаются отражением).
        /// </summary>
        /// <param name="x">Координата x, полученная от джойстика.</param>
        /// <param name="y">Координата y, полученная от джойстика.</param>
        private static void CorrectCoordinatesFromCyrcleToSquareAreaForFirstQuadrant(ref float x, ref float y)
        {
            // Исключение деления на 0:
            if (x == 0)
            {
                return;
            }

            if ((x >= 0) && (y >= 0))
            {
                bool firstSectorInOctet = x >= y;
                if (firstSectorInOctet == false)
                {
                    float temp = x;
                    x = y;
                    y = temp;
                }

                double resultX = Math.Sqrt((x * x) + (y * y));
                double resultY = y * resultX / x;
                x = (float)resultX;
                y = (float)resultY;

                if (firstSectorInOctet == false)
                {
                    float temp = x;
                    x = y;
                    y = temp;
                }
            }
            else
            {
                throw new InvalidOperationException("Неверные координаты для первого квадранта.");
            }
        }

        /// <summary>
        /// Формирование команды горизонтального поворота головы.
        /// </summary>
        /// <param name="degree">
        /// Угол поворота в градусах.
        /// </param>
        /// <param name="horizontalServoCommand">
        /// Выходной параметр. Сформированная команда.
        /// </param>
        private void GenerateHorizontalServoCommandByDegree(float degree, out string horizontalServoCommand)
        {
            horizontalServoCommand = "H" + MessageHelper.IntToMessageValue(Convert.ToInt32(degree));
        }

        /// <summary>
        /// Формирование команды горизонтального поворота головы.
        /// </summary>
        /// <param name="x">
        /// x-координата ThumbStick-джойстика поворота головы.
        /// </param>
        /// <param name="horizontalServoCommand">
        /// Выходной параметр. Сформированная команда.
        /// </param>
        private void GenerateHorizontalServoCommand(float x, out string horizontalServoCommand)
        {
            double degree = ((1 - x) * ((this.controlSettings.HorizontalMaximumDegree - this.controlSettings.HorizontalMinimumDegree) / 2)) + this.controlSettings.HorizontalMinimumDegree;
            this.GenerateHorizontalServoCommandByDegree(Convert.ToInt32(degree), out horizontalServoCommand);
        }

        /// <summary>
        /// Формирование команды вертикального поворота головы.
        /// </summary>
        /// <param name="degree">
        /// Угол поворота в градусах.
        /// </param>
        /// <param name="verticalServoCommand">
        /// Выходной параметр. Сформированная команда.
        /// </param>
        private void GenerateVerticalServoCommandByDegree(float degree, out string verticalServoCommand)
        {
            verticalServoCommand = "V" + MessageHelper.IntToMessageValue(Convert.ToInt32(degree));
        }

        /// <summary>
        /// Формирование команды вертикального поворота головы.
        /// </summary>
        /// <param name="y">
        /// y-координата ThumbStick-джойстика поворота головы.
        /// </param>
        /// <param name="verticalServoCommand">
        /// Выходной параметр. Сформированная команда.
        /// </param>
        private void GenerateVerticalServoCommand(float y, out string verticalServoCommand)
        {
            double degree = ((y + 1) * ((this.controlSettings.VerticalMaximumDegree - this.controlSettings.VerticalMinimumDegree) / 2)) + this.controlSettings.VerticalMinimumDegree;
            degree = this.controlSettings.VerticalMinimumDegree + this.controlSettings.VerticalMaximumDegree - degree; // Удобнее, когда если джойстик от себя, робот смотрит вниз (увеличение "y" должно уменьшать угол.
            this.GenerateVerticalServoCommandByDegree(Convert.ToInt32(degree), out verticalServoCommand);
        }

        /// <summary>
        /// Формирование команд горизонтального и вертикального поворотов головы по координатам ThumbStick-джойстика (нефиксируемый обзор).
        /// </summary>
        /// <param name="x">x-координата ThumbStick-джойстика.</param>
        /// <param name="y">y-координата ThumbStick-джойстика.</param>
        /// <param name="horizontalServoCommand">Выходной параметр. Сформированная команда горизонтального поворота головы.</param>
        /// <param name="verticalServoCommand">Выходной параметр. Сформированная команда вертикального поворота головы.</param>
        private void GenerateServoCommand(float x, float y, out string horizontalServoCommand, out string verticalServoCommand)
        {
            this.GenerateHorizontalServoCommand(x, out horizontalServoCommand);
            this.GenerateVerticalServoCommand(y, out verticalServoCommand);
        }

        /// <summary>
        /// Формирование команд горизонтального и вертикального поворотов головы для установки взгляда вперёд.
        /// </summary>
        /// <param name="horizontalServoCommand">Выходной параметр. Сформированная команда горизонтального поворота головы.</param>
        /// <param name="verticalServoCommand">Выходной параметр. Сформированная команда вертикального поворота головы.</param>
        private void GenerateLookForwardServoCommand(out string horizontalServoCommand, out string verticalServoCommand)
        {
            this.GenerateHorizontalServoCommandByDegree(this.controlSettings.HorizontalForwardDegree, out horizontalServoCommand);
            this.GenerateVerticalServoCommandByDegree(this.controlSettings.VerticalForwardDegree, out verticalServoCommand);
        }

        /// <summary>
        /// Уменьшение горизонтального угла обзора на основе константы скорости поворота головы и времени, прошедшего с последнего вызова.
        /// </summary>
        /// <param name="degree">Уменьшаемая переменная, инициированная значением угла на момент последнего вызова.</param>
        /// <param name="gameTime">Игровое время (время, прошедшее с последнего вызова).</param>
        private void DecrementHorizontalDegree(ref float degree, GameTime gameTime)
        {
            float speed = this.fastModeOn ? this.controlSettings.HorizontalHighSpeed : this.controlSettings.HorizontalLowSpeed;
            degree -= gameTime.ElapsedGameTime.Milliseconds * speed;
            degree = (degree < this.controlSettings.HorizontalMinimumDegree) ? this.controlSettings.HorizontalMinimumDegree : degree;
        }

        /// <summary>
        /// Увеличение горизонтального угла обзора на основе константы скорости поворота головы и времени, прошедшего с последнего вызова.
        /// </summary>
        /// <param name="degree">Увеличиваемая переменная, инициированная значением угла на момент последнего вызова.</param>
        /// <param name="gameTime">Игровое время (время, прошедшее с последнего вызова).</param>
        private void IncrementHorizontalDegree(ref float degree, GameTime gameTime)
        {
            float speed = this.fastModeOn ? this.controlSettings.HorizontalHighSpeed : this.controlSettings.HorizontalLowSpeed;
            degree += gameTime.ElapsedGameTime.Milliseconds * speed;
            degree = (degree > this.controlSettings.HorizontalMaximumDegree) ? this.controlSettings.HorizontalMaximumDegree : degree;
        }

        /// <summary>
        /// Уменьшение вертикального угла обзора на основе константы скорости поворота головы и времени, прошедшего с последнего вызова.
        /// </summary>
        /// <param name="degree">Уменьшаемая переменная, инициированная значением угла на момент последнего вызова.</param>
        /// <param name="gameTime">Игровое время (время, прошедшее с последнего вызова).</param>
        private void DecrementVerticalDegree(ref float degree, GameTime gameTime)
        {
            float speed = this.fastModeOn ? this.controlSettings.VerticalHighSpeed : this.controlSettings.VerticalLowSpeed;
            degree -= gameTime.ElapsedGameTime.Milliseconds * speed;
            degree = (degree < this.controlSettings.VerticalMinimumDegree) ? this.controlSettings.VerticalMinimumDegree : degree;
        }

        /// <summary>
        /// Увеличение вертикального угла обзора на основе константы скорости поворота головы и времени, прошедшего с последнего вызова.
        /// </summary>
        /// <param name="degree">Увеличиваемая переменная, инициированная значением угла на момент последнего вызова.</param>
        /// <param name="gameTime">Игровое время (время, прошедшее с последнего вызова).</param>
        private void IncrementVerticalDegree(ref float degree, GameTime gameTime)
        {
            float speed = this.fastModeOn ? this.controlSettings.VerticalHighSpeed : this.controlSettings.VerticalLowSpeed;
            degree += gameTime.ElapsedGameTime.Milliseconds * speed;
            degree = (degree > this.controlSettings.VerticalMaximumDegree) ? this.controlSettings.VerticalMaximumDegree : degree;
        }

        /// <summary>
        /// Проверка инициализации экземпляра класса для взаимодействия с роботом.
        /// </summary>
        private void CheckRobotHelper()
        {
            if (this.robotHelper == null)
            {
                throw new NullReferenceException("LookHelper не инициализирован.");
            }
        }
    }
}