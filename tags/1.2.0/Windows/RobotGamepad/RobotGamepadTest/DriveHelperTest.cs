// --------------------------------------------------------------------------------------------------------------------
// <copyright file="DriveHelperTest.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс, тестирующий класс DriveHelper.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepadTest
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    using NUnit.Framework;

    using RobotGamepad;

    /// <summary>
    /// Класс, тестирующий класс DriveHelper.
    /// </summary>
    [TestFixture]
    public sealed class DriveHelperTest
    {
        /// <summary>
        /// Текст команды для левого двигателя.
        /// </summary>
        private string leftMotorCommand;

        /// <summary>
        /// Текст команды для правого двигателя.
        /// </summary>
        private string rightMotorCommand;

        /// <summary>
        /// Конструктор класса.
        /// </summary>
        public DriveHelperTest()
        {
            // Специально для тестов переопределяю максимальные скорости нормального и турбо режимов моторов.
            Settings.DriveModeNormalMaxSpeed = 190;
            Settings.DriveModeTurboMaxSpeed = 255;
        }

        /// <summary>
        /// Тест движения вперёд и назад в турбо-режиме.
        /// </summary>
        [Test]
        public void TurboDriveTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = true;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(0, 1, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00FF", leftMotorCommand);
            Assert.AreEqual("R00FF", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, 0.5, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00DD", leftMotorCommand);
            Assert.AreEqual("R00DD", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, -1, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF01", leftMotorCommand);
            Assert.AreEqual("RFF01", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, -0.5, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF23", leftMotorCommand);
            Assert.AreEqual("RFF23", rightMotorCommand);
        }

        /// <summary>
        /// Тест движения вперёд и назад без турбо-режима.
        /// </summary>
        [Test]
        public void DriveTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = false;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(0, 1, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00BE", leftMotorCommand);
            Assert.AreEqual("R00BE", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, 0.5, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00A4", leftMotorCommand);
            Assert.AreEqual("R00A4", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, -1, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF42", leftMotorCommand);
            Assert.AreEqual("RFF42", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, -0.5, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF5C", leftMotorCommand);
            Assert.AreEqual("RFF5C", rightMotorCommand);
        }

        /// <summary>
        /// Тест остановки движения.
        /// </summary>
        [Test]
        public void StopTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.GenerateMotorCommands(0, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L0000", leftMotorCommand);
            Assert.AreEqual("R0000", rightMotorCommand);
        }
    
        /// <summary>
        /// Тест движения вперёд.
        /// </summary>
        [Test]
        public void ForwardTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = false;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(0, 1, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L" + MessageHelper.IntToMessageValue(Settings.DriveModeNormalMaxSpeed), leftMotorCommand);
            Assert.AreEqual("R" + MessageHelper.IntToMessageValue(Settings.DriveModeNormalMaxSpeed), rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, 0.5, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            int speed = Convert.ToInt32(255.0 * 0.5);
            speed = DriveHelper.NonlinearSpeedCorrection(speed);
            speed = driveHelper.TurboModeOn ? speed : speed * Settings.DriveModeNormalMaxSpeed / 255;
            Assert.AreEqual("L" + MessageHelper.IntToMessageValue(speed), leftMotorCommand);
            Assert.AreEqual("R" + MessageHelper.IntToMessageValue(speed), rightMotorCommand);
        }

        /// <summary>
        /// Тест движения назад.
        /// </summary>
        [Test]
        public void BackwardTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = false;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(0, -1, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L" + MessageHelper.IntToMessageValue(-Settings.DriveModeNormalMaxSpeed), leftMotorCommand);
            Assert.AreEqual("R" + MessageHelper.IntToMessageValue(-Settings.DriveModeNormalMaxSpeed), rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0, -0.5, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            int speed = Convert.ToInt32(255.0 * 0.5);
            speed = DriveHelper.NonlinearSpeedCorrection(speed);
            speed = driveHelper.TurboModeOn ? speed : speed * Settings.DriveModeNormalMaxSpeed / 255;
            Assert.AreEqual("L" + MessageHelper.IntToMessageValue(-speed), leftMotorCommand);
            Assert.AreEqual("R" + MessageHelper.IntToMessageValue(-speed), rightMotorCommand);
        }

        /// <summary>
        /// Тест разворота.
        /// </summary>
        [Test]
        public void RotationTest1()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(1, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00FF", leftMotorCommand);
            Assert.AreEqual("R0000", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0.5, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00DD", leftMotorCommand);
            Assert.AreEqual("R0000", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(-1, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L0000", leftMotorCommand);
            Assert.AreEqual("R00FF", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(-0.5, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L0000", leftMotorCommand);
            Assert.AreEqual("R00DD", rightMotorCommand);
        }

        /// <summary>
        /// Тест разворота на месте.
        /// </summary>
        [Test]
        public void RotationTest2()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;
            driveHelper.RotationModeOn = true;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(1, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00FF", leftMotorCommand);
            Assert.AreEqual("RFF01", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0.5, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00DD", leftMotorCommand);
            Assert.AreEqual("RFF23", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(-1, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF01", leftMotorCommand);
            Assert.AreEqual("R00FF", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(-0.5, 0, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF23", leftMotorCommand);
            Assert.AreEqual("R00DD", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота вперёд-влево.
        /// </summary>
        [Test]
        public void HalfLeftForward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(-0.7071, 0.7071, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L004A", leftMotorCommand);
            Assert.AreEqual("R00FF", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(-0.3536, 0.3536, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L0037", leftMotorCommand);
            Assert.AreEqual("R00DD", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота вперёд-вправо.
        /// </summary>
        [Test]
        public void HalfRightForward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(0.7071, 0.7071, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00FF", leftMotorCommand);
            Assert.AreEqual("R004A", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0.3536, 0.3536, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("L00DD", leftMotorCommand);
            Assert.AreEqual("R0037", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота назад-влево.
        /// </summary>
        [Test]
        public void HalfLeftBackward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(-0.7071, -0.7071, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFFB6", leftMotorCommand);
            Assert.AreEqual("RFF01", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(-0.3536, -0.3536, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFFC9", leftMotorCommand);
            Assert.AreEqual("RFF23", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота назад-вправо.
        /// </summary>
        [Test]
        public void HalfRightBackward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            int leftSpeed;
            int rightSpeed;

            driveHelper.CalculateMotorsSpeed(0.7071, -0.7071, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF01", leftMotorCommand);
            Assert.AreEqual("RFFB6", rightMotorCommand);

            driveHelper.CalculateMotorsSpeed(0.3536, -0.3536, out leftSpeed, out rightSpeed);
            driveHelper.GenerateMotorCommands(leftSpeed, rightSpeed, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LFF23", leftMotorCommand);
            Assert.AreEqual("RFFC9", rightMotorCommand);
        }
    }
}
