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
        /// Объект для взаимодействия с роботом.
        /// </summary>
        private RobotHelper robotHelper;

        /// <summary>
        /// Initializes a new instance of the FormMain class.
        /// </summary>
        public FormMain()
        {
            this.InitializeComponent();

            ConnectSettings connectSettings = new ConnectSettings(
                Properties.Settings.Default.RoboHeadAddress,
                Properties.Settings.Default.MessagePort);
            connectSettings.SingleMessageRepetitionsCount = Properties.Settings.Default.SingleMessageRepetitionsCount;
            this.robotHelper = new RobotHelper(connectSettings);
        }

        /// <summary>
        /// Обработчик кнопки Отправить.
        /// </summary>
        /// <param name="sender">Источник события.</param>
        /// <param name="e">Аргументы события.</param>
        private void ButtonSend_Click(object sender, EventArgs e)
        {
            SendHelper.CommandLineProcessor(this.textBoxSend, this.textBoxReceive, this.robotHelper);
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
                    SendHelper.CommandLineProcessor(this.textBoxSend, this.textBoxReceive, this.robotHelper);
                    break;
                case Keys.Up:
                    SendHelper.SelectPreviousCommand(this.textBoxSend);
                    e.Handled = true;
                    break;
                case Keys.Down:
                    SendHelper.SelectNextCommand(this.textBoxSend);
                    e.Handled = true;
                    break;
            }
        }
    }
}
