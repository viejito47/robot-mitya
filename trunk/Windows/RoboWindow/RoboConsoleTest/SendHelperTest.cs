// <copyright file="SendHelperTest.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2012
// </copyright>
// <summary>
//   Класс, тестирующий класс SendHelper.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboConsoleTest
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Windows.Forms;

    using Moq;
    using NUnit.Framework;
    
    using RoboCommon;
    using RoboConsole;

    /// <summary>
    /// Класс, тестирующий класс SendHelper.
    /// </summary>
    [TestFixture]
    public sealed class SendHelperTest
    {
        /// <summary>
        /// Тест метода CommandLineProcessor.
        /// </summary>
        [Test]
        public void TestCommandLineProcessorOneGoodCommand()
        {
            var commandLineTextBox = new TextBox();
            commandLineTextBox.Text = "I0001";

            var outputTextBox = new TextBox();

            var mock = new Mock<IRobotHelper>();
            mock.Setup(x => x.SendMessageToRobot(It.IsAny<string>())).Returns(true);
            mock.Setup(x => x.LastSentMessage).Returns("I0001");

            SendHelper.CommandLineProcessor(commandLineTextBox, outputTextBox, mock.Object);

            Assert.AreEqual(string.Empty, commandLineTextBox.Text);
            Assert.AreEqual(2, outputTextBox.Lines.Length);
            Assert.AreEqual("I0001", outputTextBox.Lines[0]);
        }

        /// <summary>
        /// Тест метода CommandLineProcessor.
        /// </summary>
        [Test]
        public void TestCommandLineProcessorOneBadCommand()
        {
            var commandLineTextBox = new TextBox();
            commandLineTextBox.Text = "I0001";

            var outputTextBox = new TextBox();

            var mock = new Mock<IRobotHelper>();
            mock.Setup(x => x.SendMessageToRobot(It.IsAny<string>())).Returns(false);

            SendHelper.CommandLineProcessor(commandLineTextBox, outputTextBox, mock.Object);

            Assert.AreEqual("I0001", commandLineTextBox.Text);
            Assert.AreEqual(2, outputTextBox.Lines.Length);
            Assert.IsTrue(outputTextBox.Lines[0].StartsWith("Ошибка"));
        }

        /// <summary>
        /// Тест метода CommandLineProcessor.
        /// </summary>
        [Test]
        public void TestCommandLineProcessorFewGoodCommands()
        {
            var commandLineTextBox = new TextBox();
            commandLineTextBox.Text = "I0001, , ,, I0000,  F0102, ";

            var outputTextBox = new TextBox();

            var mock = new Mock<IRobotHelper>();
            
            mock.Setup(x => x.SendMessageToRobot(It.IsAny<string>()))
                .Returns(true);

            // LastSentMessage должен возвращать разные значения ("переданные" роботу команды) при каждом вызове.
            string[] sentMessages = { "I0001", "I0000", "F0102" };
            int sentMessageIndex = 0;
            mock.Setup(x => x.LastSentMessage)
                .Returns(() => sentMessages[sentMessageIndex++]);

            SendHelper.CommandLineProcessor(commandLineTextBox, outputTextBox, mock.Object);

            Assert.AreEqual(string.Empty, commandLineTextBox.Text);
            Assert.AreEqual(4, outputTextBox.Lines.Length);
            Assert.AreEqual("I0001", outputTextBox.Lines[0]);
            Assert.AreEqual("I0000", outputTextBox.Lines[1]);
            Assert.AreEqual("F0102", outputTextBox.Lines[2]);
            Assert.AreEqual(string.Empty, outputTextBox.Lines[3]);
        }

        /// <summary>
        /// Тест метода CommandLineProcessor.
        /// </summary>
        [Test]
        public void TestCommandLineProcessorOneBadInFewGoodCommands()
        {
            var commandLineTextBox = new TextBox();
            commandLineTextBox.Text = "I0001, , ,, I0000,  F0102, ";

            var outputTextBox = new TextBox();

            var mock = new Mock<IRobotHelper>();            
            mock.Setup(x => x.SendMessageToRobot(It.IsAny<string>()))
                .Returns((string command) => !command.Equals("I0000"));
            mock.Setup(x => x.LastSentMessage)
                .Returns("I0001");

            SendHelper.CommandLineProcessor(commandLineTextBox, outputTextBox, mock.Object);

            Assert.AreEqual("I0000, F0102", commandLineTextBox.Text);
            Assert.AreEqual(3, outputTextBox.Lines.Length);
            Assert.AreEqual("I0001", outputTextBox.Lines[0]);
            Assert.IsTrue(outputTextBox.Lines[1].StartsWith("Ошибка"));
            Assert.AreEqual(string.Empty, outputTextBox.Lines[2]);
        }

        /// <summary>
        /// Тест метода CommandLineProcessor.
        /// </summary>
        [Test]
        public void TestCommandLineProcessorStrangeCommands1()
        {
            var commandLineTextBox = new TextBox();
            commandLineTextBox.Text = string.Empty;

            var outputTextBox = new TextBox();

            var mock = new Mock<IRobotHelper>();
            mock.Setup(x => x.SendMessageToRobot(It.IsAny<string>()))
                .Returns(false);

            SendHelper.CommandLineProcessor(commandLineTextBox, outputTextBox, mock.Object);

            Assert.AreEqual(string.Empty, commandLineTextBox.Text);
            Assert.AreEqual(string.Empty, outputTextBox.Text);
        }

        /// <summary>
        /// Тест метода CommandLineProcessor.
        /// </summary>
        [Test]
        public void TestCommandLineProcessorStrangeCommands2()
        {
            var commandLineTextBox = new TextBox();
            commandLineTextBox.Text = ",,,";

            var outputTextBox = new TextBox();

            var mock = new Mock<IRobotHelper>();
            mock.Setup(x => x.SendMessageToRobot(It.IsAny<string>()))
                .Returns(false);

            SendHelper.CommandLineProcessor(commandLineTextBox, outputTextBox, mock.Object);

            Assert.AreEqual(string.Empty, commandLineTextBox.Text);
            Assert.AreEqual(string.Empty, outputTextBox.Text);
        }
    }
}
