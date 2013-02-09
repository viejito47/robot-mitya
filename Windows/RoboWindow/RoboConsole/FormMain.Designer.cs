﻿// --------------------------------------------------------------------------------------------------------------------
// <copyright file="FormMain.Designer.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2012
// </copyright>
// <auto-generated />
// <summary>
//   Главная форма приложения.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboConsole
{
    /// <summary>
    /// Главная форма приложения.
    /// </summary>
    public partial class FormMain
    {
        /// <summary>
        /// Требуется переменная конструктора.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Каркас для контролов.
        /// </summary>
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel;

        /// <summary>
        /// Поле ввода команды.
        /// </summary>
        private System.Windows.Forms.TextBox textBoxSend;

        /// <summary>
        /// Кнопка Отправить.
        /// </summary>
        private System.Windows.Forms.Button buttonSend;

        /// <summary>
        /// Освободить все используемые ресурсы.
        /// </summary>
        /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (this.components != null))
            {
                this.components.Dispose();
            }

            base.Dispose(disposing);
        }

        #region Код, автоматически созданный конструктором форм Windows

        /// <summary>
        /// Обязательный метод для поддержки конструктора - не изменяйте
        /// содержимое данного метода при помощи редактора кода.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FormMain));
            this.tableLayoutPanel = new System.Windows.Forms.TableLayoutPanel();
            this.textBoxSend = new System.Windows.Forms.TextBox();
            this.buttonSend = new System.Windows.Forms.Button();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.groupBoxCommunicationType = new System.Windows.Forms.GroupBox();
            this.radioButtonComPort = new System.Windows.Forms.RadioButton();
            this.radioButtonUdpSocket = new System.Windows.Forms.RadioButton();
            this.textBoxHistory = new System.Windows.Forms.RichTextBox();
            this.tableLayoutPanel.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.groupBoxCommunicationType.SuspendLayout();
            this.SuspendLayout();
            // 
            // tableLayoutPanel
            // 
            this.tableLayoutPanel.ColumnCount = 5;
            this.tableLayoutPanel.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 6F));
            this.tableLayoutPanel.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 10F));
            this.tableLayoutPanel.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 135F));
            this.tableLayoutPanel.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 6F));
            this.tableLayoutPanel.Controls.Add(this.textBoxSend, 1, 4);
            this.tableLayoutPanel.Controls.Add(this.buttonSend, 3, 4);
            this.tableLayoutPanel.Controls.Add(this.pictureBox1, 3, 1);
            this.tableLayoutPanel.Controls.Add(this.groupBoxCommunicationType, 3, 2);
            this.tableLayoutPanel.Controls.Add(this.textBoxHistory, 1, 1);
            this.tableLayoutPanel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel.Location = new System.Drawing.Point(0, 0);
            this.tableLayoutPanel.Name = "tableLayoutPanel";
            this.tableLayoutPanel.RowCount = 6;
            this.tableLayoutPanel.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 6F));
            this.tableLayoutPanel.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 125F));
            this.tableLayoutPanel.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 10F));
            this.tableLayoutPanel.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 25F));
            this.tableLayoutPanel.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 6F));
            this.tableLayoutPanel.Size = new System.Drawing.Size(667, 345);
            this.tableLayoutPanel.TabIndex = 0;
            // 
            // textBoxSend
            // 
            this.textBoxSend.Dock = System.Windows.Forms.DockStyle.Fill;
            this.textBoxSend.Font = new System.Drawing.Font("Courier New", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.textBoxSend.Location = new System.Drawing.Point(9, 316);
            this.textBoxSend.Margin = new System.Windows.Forms.Padding(3, 2, 3, 3);
            this.textBoxSend.Name = "textBoxSend";
            this.textBoxSend.Size = new System.Drawing.Size(504, 23);
            this.textBoxSend.TabIndex = 0;
            this.textBoxSend.KeyDown += new System.Windows.Forms.KeyEventHandler(this.TextBoxSend_KeyDown);
            // 
            // buttonSend
            // 
            this.buttonSend.Dock = System.Windows.Forms.DockStyle.Fill;
            this.buttonSend.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.buttonSend.Location = new System.Drawing.Point(526, 314);
            this.buttonSend.Margin = new System.Windows.Forms.Padding(0);
            this.buttonSend.Name = "buttonSend";
            this.buttonSend.Size = new System.Drawing.Size(135, 25);
            this.buttonSend.TabIndex = 1;
            this.buttonSend.Text = "Отправить";
            this.buttonSend.UseVisualStyleBackColor = true;
            this.buttonSend.Click += new System.EventHandler(this.ButtonSend_Click);
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
            this.pictureBox1.Location = new System.Drawing.Point(529, 9);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(129, 117);
            this.pictureBox1.TabIndex = 3;
            this.pictureBox1.TabStop = false;
            // 
            // groupBoxCommunicationType
            // 
            this.groupBoxCommunicationType.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBoxCommunicationType.Controls.Add(this.radioButtonComPort);
            this.groupBoxCommunicationType.Controls.Add(this.radioButtonUdpSocket);
            this.groupBoxCommunicationType.Location = new System.Drawing.Point(529, 231);
            this.groupBoxCommunicationType.Name = "groupBoxCommunicationType";
            this.groupBoxCommunicationType.Size = new System.Drawing.Size(129, 70);
            this.groupBoxCommunicationType.TabIndex = 3;
            this.groupBoxCommunicationType.TabStop = false;
            this.groupBoxCommunicationType.Text = "Тип связи";
            // 
            // radioButtonComPort
            // 
            this.radioButtonComPort.AutoSize = true;
            this.radioButtonComPort.Location = new System.Drawing.Point(7, 44);
            this.radioButtonComPort.Name = "radioButtonComPort";
            this.radioButtonComPort.Size = new System.Drawing.Size(75, 17);
            this.radioButtonComPort.TabIndex = 1;
            this.radioButtonComPort.Text = "COM-порт";
            this.radioButtonComPort.UseVisualStyleBackColor = true;
            this.radioButtonComPort.CheckedChanged += new System.EventHandler(this.RadioButtonComPortCheckedChanged);
            // 
            // radioButtonUdpSocket
            // 
            this.radioButtonUdpSocket.AutoSize = true;
            this.radioButtonUdpSocket.Checked = true;
            this.radioButtonUdpSocket.Location = new System.Drawing.Point(7, 20);
            this.radioButtonUdpSocket.Name = "radioButtonUdpSocket";
            this.radioButtonUdpSocket.Size = new System.Drawing.Size(80, 17);
            this.radioButtonUdpSocket.TabIndex = 0;
            this.radioButtonUdpSocket.TabStop = true;
            this.radioButtonUdpSocket.Text = "UDP-сокет";
            this.radioButtonUdpSocket.UseVisualStyleBackColor = true;
            // 
            // textBoxHistory
            // 
            this.textBoxHistory.BackColor = System.Drawing.SystemColors.Control;
            this.textBoxHistory.Dock = System.Windows.Forms.DockStyle.Fill;
            this.textBoxHistory.Font = new System.Drawing.Font("Courier New", 9.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.textBoxHistory.Location = new System.Drawing.Point(9, 9);
            this.textBoxHistory.Name = "textBoxHistory";
            this.textBoxHistory.ReadOnly = true;
            this.tableLayoutPanel.SetRowSpan(this.textBoxHistory, 2);
            this.textBoxHistory.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.ForcedVertical;
            this.textBoxHistory.Size = new System.Drawing.Size(504, 292);
            this.textBoxHistory.TabIndex = 4;
            this.textBoxHistory.Text = "";
            // 
            // FormMain
            // 
            this.AcceptButton = this.buttonSend;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(667, 345);
            this.Controls.Add(this.tableLayoutPanel);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MinimumSize = new System.Drawing.Size(234, 280);
            this.Name = "FormMain";
            this.Text = "Консоль робота";
            this.tableLayoutPanel.ResumeLayout(false);
            this.tableLayoutPanel.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.groupBoxCommunicationType.ResumeLayout(false);
            this.groupBoxCommunicationType.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.GroupBox groupBoxCommunicationType;
        private System.Windows.Forms.RadioButton radioButtonComPort;
        private System.Windows.Forms.RadioButton radioButtonUdpSocket;
        private System.Windows.Forms.RichTextBox textBoxHistory;

    }
}

