// --------------------------------------------------------------------------------------------------------------------
// <copyright file="DriveHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для организации работы с ходовыми двигателями робота.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Вспомогательный класс, предназначенный для организации работы с ходовыми двигателями робота.
    /// </summary>
    public class DriveHelper
    {
        /// <summary>
        /// Объект, упращающий взаимодействие с роботом.
        /// </summary>
        private RobotHelper robotHelper;

        /// <summary>
        /// Признак турбо-режима робота.
        /// </summary>
        /// <remarks>
        /// Питание двигателей осуществляется от 12 В, это предельное их напряжение, поэтому введено 
        /// программное ограничение напряжения, подаваемого на двигатели. Только в турбо-режиме на 
        /// двигатели может быть подано полное напряжение.
        /// </remarks>
        private bool turboModeOn = false;

        /// <summary>
        /// Признак режима разворота.
        /// </summary>
        /// <remarks>
        /// В режиме разворота боковые повороты джойстика движения приводят к вращению левых и правых 
        /// колёс в разные стороны. В обычном режиме одна из сторон только замедляемся (вплоть до остановки).
        /// </remarks>
        private bool rotationModeOn = false;

        /// <summary>
        /// Последняя команда, переданная на левый мотор.
        /// </summary>
        private string lastLeftMotorCommand = string.Empty;

        /// <summary>
        /// Последняя команда, переданная на левый мотор.
        /// </summary>
        private string lastRightMotorCommand = string.Empty;

        /// <summary>
        /// Gets or sets a value indicating whether турбо-режим включен.
        /// </summary>
        public bool TurboModeOn
        {
            get
            {
                return this.turboModeOn;
            }

            set
            {
                this.turboModeOn = value;
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether режим разворота включен.
        /// </summary>
        public bool RotationModeOn
        {
            get
            {
                return this.rotationModeOn;
            }

            set
            {
                this.rotationModeOn = value;
            }
        }

        /// <summary>
        /// Gets Последняя команда, переданная на левый мотор.
        /// </summary>
        public string LastLeftMotorCommand 
        { 
            get 
            { 
                return this.lastLeftMotorCommand; 
            } 
        }

        /// <summary>
        /// Gets Последняя команда, переданная на правый мотор.
        /// </summary>
        public string LastRightMotorCommand 
        { 
            get 
            {
                return this.lastRightMotorCommand; 
            } 
        }

        /// <summary>
        /// Обеспечивает нелинейный прирост скорости. Функция - дуга окружности. f(x) = sqrt(2x - x^2)
        /// </summary>
        /// <param name="speed">Исходная скорость.</param>
        /// <returns>Целевая скорость после подставления в функцию.</returns>
        public static int NonlinearSpeedCorrection(int speed)
        {            
            double floatSpeed = Math.Abs(speed);
            floatSpeed = floatSpeed / 255.0;
            double floatResult = Math.Sqrt((2 * floatSpeed) - (floatSpeed * floatSpeed));
            floatResult = floatResult * 255.0;
            int result = (int)Math.Round(floatResult);
            result = result > 255 ? 255 : result;
            result = result < 0 ? 0 : result;
            if (speed < 0)
            {
                result = -result;
            }

            return result;
        }

        /// <summary>
        /// Формирование команд остановки двигателей.
        /// </summary>
        /// <param name="leftMotorCommand">Команда остановки левых двигателей.</param>
        /// <param name="rightMotorCommand">Команда остановки правых двигателей.</param>
        public void GenerateStopMotorCommands(out string leftMotorCommand, out string rightMotorCommand)
        {
            this.GenerateMotorCommands(0, 0, out leftMotorCommand, out rightMotorCommand);
        }

        /// <summary>
        /// Формирование команд на двигатели исходя из координат джойстика движения.
        /// </summary>
        /// <param name="x">Координата x джойстика в интервале [-1, 1].</param>
        /// <param name="y">Координата y джойстика в интервале [-1, 1].</param>
        /// <param name="leftMotorCommand">Команда на левые двигатели.</param>
        /// <param name="rightMotorCommand">Команда на правые двигатели.</param>
        public void GenerateMotorCommands(double x, double y, out string leftMotorCommand, out string rightMotorCommand)
        {
            int leftSpeed;
            int rightSpeed;
            this.CalculateMotorsSpeed(x, y, out leftSpeed, out rightSpeed);
            leftMotorCommand = this.SpeedToMotorCommand('L', leftSpeed);
            rightMotorCommand = this.SpeedToMotorCommand('R', rightSpeed);
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
        /// Организует движение робота в соответсятвии с заданными координатами джойстака движения.
        /// </summary>
        /// <param name="x">Координата x джойстика в интервале [-1, 1].</param>
        /// <param name="y">Координата y джойстика в интервале [-1, 1].</param>
        public void Drive(double x, double y)
        {
            this.CheckRobotHelper();

            string leftMotorCommand;
            string rightMotorCommand;
            this.GenerateMotorCommands(x, y, out leftMotorCommand, out rightMotorCommand);

            if (Settings.RepeatCommandsFlag || (leftMotorCommand != this.lastLeftMotorCommand))
            {
                this.robotHelper.SendCommandToRobot(leftMotorCommand);
                this.lastLeftMotorCommand = leftMotorCommand;
            }

            if (Settings.RepeatCommandsFlag || (rightMotorCommand != this.lastRightMotorCommand))
            {
                this.robotHelper.SendCommandToRobot(rightMotorCommand);
                this.lastRightMotorCommand = rightMotorCommand;
            }
        }

        /// <summary>
        /// Остановка движения робота.
        /// </summary>
        public void Stop()
        {
            this.Drive(0, 0);
        }

        /// <summary>
        /// Переключение турбо-режима.
        /// </summary>
        public void SwitchTurboMode()
        {
            this.turboModeOn = !this.turboModeOn;
        }

        /// <summary>
        /// Проверка инициализации экземпляра класса для взаимодействия с роботом.
        /// </summary>
        private void CheckRobotHelper()
        {
            if (this.robotHelper == null)
            {
                throw new NullReferenceException("DriveHelper не инициализирован.");
            }
        }

        /// <summary>
        /// Масштабирование значения в интервале [0, 1] на заданный целочисленный интервал.
        /// </summary>
        /// <param name="value">Значение в интервале [0, 1].</param>
        /// <param name="minResult">Начало целевого интервала.</param>
        /// <param name="maxResult">Конец целевого интервала.</param>
        /// <returns>Целое значение на целевом интервале.</returns>
        private int Map(double value, int minResult, int maxResult)
        {
            value = value < 0 ? 0 : value;
            value = value > 1 ? 1 : value;
            double result = minResult + (value * (maxResult - minResult));
            return Convert.ToInt32(result);
        }

        /// <summary>
        /// Корректировка скорости двигателей.
        /// </summary>
        /// <param name="leftSpeed">Скорость левых двигателей.</param>
        /// <param name="rightSpeed">Скорость правых двигателей.</param>
        private void CorrectMotorsSpeed(ref int leftSpeed, ref int rightSpeed)
        {
            // Делаю нелинейный прирост скорости. Функция - дуга окружности. f(x) = sqrt(2x - x^2)
            leftSpeed = NonlinearSpeedCorrection(leftSpeed);
            rightSpeed = NonlinearSpeedCorrection(rightSpeed);

            // Линейное снижение скорости (если не включен режим Турбо) - берегу двигатели.
            int coef = this.turboModeOn ? Settings.DriveModeTurboCoef : Settings.DriveModeNormalCoef;
            leftSpeed = leftSpeed * coef / 255;
            rightSpeed = rightSpeed * coef / 255;
        }

        /// <summary>
        /// Расчёт скоростей двигателей, исходя из значений координат джойстика движения.
        /// </summary>
        /// <param name="x">Координата x джойстика в интервале [-1, 1].</param>
        /// <param name="y">Координата y джойстика в интервале [-1, 1].</param>
        /// <param name="leftSpeed">Возвращаемая скорость левого двигателя.</param>
        /// <param name="rightSpeed">Возвращаемая скорость правого двигателя.</param>
        private void CalculateMotorsSpeed(double x, double y, out int leftSpeed, out int rightSpeed)
        {
            double vectorLength = Math.Sqrt((x * x) + (y * y));
            vectorLength = vectorLength < 0 ? 0 : vectorLength;
            vectorLength = vectorLength > 1 ? 1 : vectorLength;

            double sinAlpha = vectorLength == 0 ? 0 : (y >= 0 ? y / vectorLength : -y / vectorLength);

            // Аж два раза корректирую синус. Дошёл до этого эмпирически. Только так чувствуется эффект.
            sinAlpha = 1 - Math.Sqrt(1 - (sinAlpha * sinAlpha)); // (нелинейная корректировка синуса) f(x) = 1 - sqrt(1 - x^2)
            sinAlpha = 1 - Math.Sqrt(1 - (sinAlpha * sinAlpha)); // (нелинейная корректировка синуса) f(x) = 1 - sqrt(1 - x^2)
            sinAlpha = sinAlpha < 0 ? 0 : sinAlpha;
            sinAlpha = sinAlpha > 1 ? 1 : sinAlpha;

            bool smallAlpha = sinAlpha < Settings.SinAlphaBound;
            bool rotationModeOn = this.rotationModeOn && smallAlpha;

            if ((x >= 0) && (y >= 0))
            {
                leftSpeed = this.Map(vectorLength, 0, 255);
                rightSpeed = this.rotationModeOn ? -leftSpeed : this.Map(sinAlpha * vectorLength, 0, 255);
            }
            else if ((x < 0) && (y >= 0))
            {
                rightSpeed = this.Map(vectorLength, 0, 255);
                leftSpeed = this.rotationModeOn ? -rightSpeed : this.Map(sinAlpha * vectorLength, 0, 255);
            }
            else if ((x < 0) && (y < 0))
            {
                rightSpeed = this.Map(vectorLength, 0, -255);
                leftSpeed = this.rotationModeOn ? -rightSpeed : this.Map(sinAlpha * vectorLength, 0, -255);
            }
            else
            { // (x >= 0) && (y < 0)
                leftSpeed = this.Map(vectorLength, 0, -255);
                rightSpeed = this.rotationModeOn ? -leftSpeed : this.Map(sinAlpha * vectorLength, 0, -255);
            }

            this.CorrectMotorsSpeed(ref leftSpeed, ref rightSpeed);
        }

        /// <summary>
        /// Формирование команды роботу.
        /// </summary>
        /// <param name="motor">Определяет для левого ('L') или правого ('R') двигателя формируется команда. Ввводить enum не стал пока.</param>
        /// <param name="signedSpeed">Скорость двигателя со знаком на интервале [0, 255].</param>
        /// <returns>Команда роботу.</returns>
        private string SpeedToMotorCommand(char motor, int signedSpeed)
        {
            string result = motor.ToString();

            if (signedSpeed >= 0)
            {
                result += "F";
            }
            else
            {
                result += "B";
            }

            result += CommandHelper.IntToCommandValue(Math.Abs(signedSpeed));

            return result;
        }
    }
}
