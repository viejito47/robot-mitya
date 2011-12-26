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
        /// Тест движения вперёд и назад в турбо-режиме.
        /// </summary>
        [Test]
        public void TurboDriveTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = true;

            driveHelper.GenerateMotorCommands(0, 1, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF255", leftMotorCommand);
            Assert.AreEqual("RF255", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, 0.5, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF221", leftMotorCommand);
            Assert.AreEqual("RF221", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, -1, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB255", leftMotorCommand);
            Assert.AreEqual("RB255", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, -0.5, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB221", leftMotorCommand);
            Assert.AreEqual("RB221", rightMotorCommand);
        }

        /// <summary>
        /// Тест движения вперёд и назад без турбо-режима.
        /// </summary>
        [Test]
        public void DriveTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = false;

            driveHelper.GenerateMotorCommands(0, 1, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF190", leftMotorCommand);
            Assert.AreEqual("RF190", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, 0.5, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF164", leftMotorCommand);
            Assert.AreEqual("RF164", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, -1, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB190", leftMotorCommand);
            Assert.AreEqual("RB190", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, -0.5, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB164", leftMotorCommand);
            Assert.AreEqual("RB164", rightMotorCommand);
        }

        /// <summary>
        /// Тест остановки движения.
        /// </summary>
        [Test]
        public void StopTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.GenerateMotorCommands(0, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF000", leftMotorCommand);
            Assert.AreEqual("RF000", rightMotorCommand);
        }
    
        /// <summary>
        /// Тест движения вперёд.
        /// </summary>
        [Test]
        public void ForwardTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = false;

            driveHelper.GenerateMotorCommands(0, 1, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF" + CommandHelper.IntToCommandValue(Settings.DriveModeNormalCoef), leftMotorCommand);
            Assert.AreEqual("RF" + CommandHelper.IntToCommandValue(Settings.DriveModeNormalCoef), rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, 0.5, out this.leftMotorCommand, out this.rightMotorCommand);
            int speed = Convert.ToInt32(255.0 * 0.5);
            speed = DriveHelper.NonlinearSpeedCorrection(speed);
            speed = driveHelper.TurboModeOn ? speed : speed * Settings.DriveModeNormalCoef / 255;
            Assert.AreEqual("LF" + CommandHelper.IntToCommandValue(speed), leftMotorCommand);
            Assert.AreEqual("RF" + CommandHelper.IntToCommandValue(speed), rightMotorCommand);
        }

        /// <summary>
        /// Тест движения назад.
        /// </summary>
        [Test]
        public void BackwardTest()
        {
            var driveHelper = new DriveHelper();
            driveHelper.TurboModeOn = false;

            driveHelper.GenerateMotorCommands(0, -1, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB" + CommandHelper.IntToCommandValue(Settings.DriveModeNormalCoef), leftMotorCommand);
            Assert.AreEqual("RB" + CommandHelper.IntToCommandValue(Settings.DriveModeNormalCoef), rightMotorCommand);

            driveHelper.GenerateMotorCommands(0, -0.5, out this.leftMotorCommand, out this.rightMotorCommand);
            int speed = Convert.ToInt32(255.0 * 0.5);
            speed = DriveHelper.NonlinearSpeedCorrection(speed);
            speed = driveHelper.TurboModeOn ? speed : speed * Settings.DriveModeNormalCoef / 255;
            Assert.AreEqual("LB" + CommandHelper.IntToCommandValue(speed), leftMotorCommand);
            Assert.AreEqual("RB" + CommandHelper.IntToCommandValue(speed), rightMotorCommand);
        }
            
        /// <summary>
        /// Тест разворота.
        /// </summary>
        [Test]
        public void RotationTest1()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            driveHelper.GenerateMotorCommands(1, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF255", leftMotorCommand);
            Assert.AreEqual("RF000", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0.5, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF221", leftMotorCommand);
            Assert.AreEqual("RF000", rightMotorCommand);

            driveHelper.GenerateMotorCommands(-1, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF000", leftMotorCommand);
            Assert.AreEqual("RF255", rightMotorCommand);

            driveHelper.GenerateMotorCommands(-0.5, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF000", leftMotorCommand);
            Assert.AreEqual("RF221", rightMotorCommand);
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

            driveHelper.GenerateMotorCommands(1, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF255", leftMotorCommand);
            Assert.AreEqual("RB255", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0.5, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF221", leftMotorCommand);
            Assert.AreEqual("RB221", rightMotorCommand);

            driveHelper.GenerateMotorCommands(-1, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB255", leftMotorCommand);
            Assert.AreEqual("RF255", rightMotorCommand);

            driveHelper.GenerateMotorCommands(-0.5, 0, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB221", leftMotorCommand);
            Assert.AreEqual("RF221", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота вперёд-влево.
        /// </summary>
        [Test]
        public void HalfLeftForward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            driveHelper.GenerateMotorCommands(-0.7071, 0.7071, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF074", leftMotorCommand);
            Assert.AreEqual("RF255", rightMotorCommand);

            driveHelper.GenerateMotorCommands(-0.3536, 0.3536, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF055", leftMotorCommand);
            Assert.AreEqual("RF221", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота вперёд-вправо.
        /// </summary>
        [Test]
        public void HalfRightForward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            driveHelper.GenerateMotorCommands(0.7071, 0.7071, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF255", leftMotorCommand);
            Assert.AreEqual("RF074", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0.3536, 0.3536, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LF221", leftMotorCommand);
            Assert.AreEqual("RF055", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота назад-влево.
        /// </summary>
        [Test]
        public void HalfLeftBackward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            driveHelper.GenerateMotorCommands(-0.7071, -0.7071, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB074", leftMotorCommand);
            Assert.AreEqual("RB255", rightMotorCommand);

            driveHelper.GenerateMotorCommands(-0.3536, -0.3536, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB055", leftMotorCommand);
            Assert.AreEqual("RB221", rightMotorCommand);
        }

        /// <summary>
        /// Тест плавного поворота назад-вправо.
        /// </summary>
        [Test]
        public void HalfRightBackward()
        {
            var driveHelper = new DriveHelper();

            driveHelper.TurboModeOn = true;

            driveHelper.GenerateMotorCommands(0.7071, -0.7071, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB255", leftMotorCommand);
            Assert.AreEqual("RB074", rightMotorCommand);

            driveHelper.GenerateMotorCommands(0.3536, -0.3536, out this.leftMotorCommand, out this.rightMotorCommand);
            Assert.AreEqual("LB221", leftMotorCommand);
            Assert.AreEqual("RB055", rightMotorCommand);
        }
    }
}
