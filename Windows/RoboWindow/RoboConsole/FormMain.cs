// --------------------------------------------------------------------------------------------------------------------
// <copyright file="FormMain.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2012
// </copyright>
// <summary>
//   Главная форма приложения.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboConsole
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel;
    using System.Data;
    using System.Drawing;
    using System.Linq;
    using System.Text;
    using System.Windows.Forms;

    using RoboCommon;

    /// <summary>
    /// Главная форма приложения.
    /// </summary>
    public partial class FormMain : Form
    {
        /// <summary>
        /// Object that contains text area to display history of commands and messages and its functionality.
        /// </summary>
        private readonly HistoryBox historyBox;

        /// <summary>
        /// Object that provide communication with robot.
        /// </summary>
        private CommunicationHelper communicationHelper;

        /// <summary>
        /// Object that provides command's processing: navigation in history list, redrawing text boxes, command line processing.
        /// </summary>
        private CommandProcessor commandProcessor;

        /// <summary>
        /// Initializes a new instance of the FormMain class.
        /// </summary>
        public FormMain()
        {
            this.InitializeComponent();
            this.InitializeUdpCommunication();
            this.historyBox = new HistoryBox(this.textBoxHistory);
        }

        /// <summary>
        /// Обработчик кнопки Отправить.
        /// </summary>
        /// <param name="sender">Источник события.</param>
        /// <param name="e">Аргументы события.</param>
        private void ButtonSend_Click(object sender, EventArgs e)
        {
            this.commandProcessor.ProcessCommand();
        }

        /// <summary>
        /// Обработчик нажатия на клавишу поля ввода команд.
        /// </summary>
        /// <param name="sender">Источник события.</param>
        /// <param name="e">Аргументы события.</param>
        private void TextBoxSend_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Alt || e.Control || e.Shift)
            {
                return;
            }

            switch (e.KeyCode)
            {
                case Keys.Enter:
                    this.commandProcessor.ProcessCommand();
                    break;
                case Keys.Up:
                    this.commandProcessor.SelectPreviousCommand();
                    e.Handled = true;
                    break;
                case Keys.Down:
                    this.commandProcessor.SelectNextCommand();
                    e.Handled = true;
                    break;
            }
        }

        /// <summary>
        /// Radio button checked changed event handler.
        /// </summary>
        /// <param name="sender">Sender control.</param>
        /// <param name="e">Event arguments.</param>
        private void RadioButtonComPortCheckedChanged(object sender, EventArgs e)
        {
            this.communicationHelper.Dispose();

            if (this.radioButtonComPort.Checked)
            {
                this.InitializeComPortCommunication();
            }
            else
            {
                this.InitializeUdpCommunication();
            }
        }

        /// <summary>
        /// Initialization of communication with robot through UDP-socket.
        /// </summary>
        private void InitializeUdpCommunication()
        {
            this.communicationHelper = new UdpCommunicationHelper(
                Properties.Settings.Default.RoboHeadAddress,
                Properties.Settings.Default.MessagePort,
                Properties.Settings.Default.SingleMessageRepetitionsCount);

            this.commandProcessor = new CommandProcessor(this.textBoxSend, this.historyBox, this.communicationHelper);
        }

        /// <summary>
        /// Initialization of communication with robot through COM-port.
        /// </summary>
        private void InitializeComPortCommunication()
        {
            this.communicationHelper = new ComPortCommunicationHelper(
                Properties.Settings.Default.ComPort,
                Properties.Settings.Default.BaudRate,
                Properties.Settings.Default.SingleMessageRepetitionsCount);
            this.communicationHelper.TextReceived += this.OnTextReceived;

            this.commandProcessor = new CommandProcessor(this.textBoxSend, this.historyBox, this.communicationHelper);
        }

        /// <summary>
        /// The event handler to process received data through COM-port or UDP communication.
        /// </summary>
        /// <param name="sender">Sender object.</param>
        /// <param name="e">Event arguments that contains received text.</param>
        private void OnTextReceived(object sender, TextReceivedEventArgs e)
        {
            if (this.InvokeRequired)
            {
                // Using this.Invoke causes deadlock when closing serial port, and BeginInvoke is good practice anyway.                
                this.BeginInvoke(new EventHandler<TextReceivedEventArgs>(this.OnTextReceived), new object[] { sender, e });
                return;
            }

            this.historyBox.AppendTextReceivedFromRobot(e.Text);
        }
    }
}
