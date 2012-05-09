// --------------------------------------------------------------------------------------------------------------------
// <copyright file="RobotHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс для взаимодействия с контроллером Arduino.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.IO.Ports;
    using System.Linq;
    using System.Net;
    using System.Net.Sockets;
    using System.Text;

    /// <summary>
    /// Класс для взаимодействия с контроллером Arduino.
    /// </summary>
    public sealed class RobotHelper
    {
        /// <summary>
        /// Клиентский сокет.
        /// </summary>
        private Socket socket;

        /// <summary>
        /// Признак соединения с сервером.
        /// </summary>
        private bool connected;

        /// <summary>
        /// Текст последней ошибки.
        /// </summary>
        private string lastErrorMessage = string.Empty;

        /// <summary>
        /// Список отправленных роботу команд. Используется для отладки.
        /// </summary>
        private MemoryStream sentCommandsLog = new MemoryStream();

        /// <summary>
        /// Список эхо-команд, вернувшихся от робота. Это обработанные роботом команды. Используется для отладки.
        /// </summary>
        private MemoryStream receivedCommandsLog = new MemoryStream();

        /// <summary>
        /// Gets Текст последней ошибки.
        /// </summary>
        public string LastErrorMessage 
        { 
            get 
            { 
                return this.lastErrorMessage; 
            } 
        }

        /// <summary>
        /// Gets a value indicating whether Признак соединения с сервером.
        /// </summary>
        public bool Connected 
        { 
            get 
            { 
                return this.connected; 
            } 
        }

        /// <summary>
        /// Установка соединения с сервером. Сервером является Android-приложение.
        /// </summary>
        /// <returns>
        /// true, если соединение установлено
        /// </returns>
        public bool Connect()
        {
            if (this.connected)
            {
                if (this.socket != null)
                {
                    if (this.socket.Connected)
                    {
                        this.socket.Disconnect(true);
                    }

                    this.socket = null;
                }

                this.connected = false;
            }

            try
            {
                this.socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                this.socket.Connect(Settings.RoboHeadAddress, Settings.TcpSocketServerPort);
                this.connected = this.socket.Connected;

                // this.socketEventArgs.Completed += new EventHandler<SocketAsyncEventArgs>(this.SocketEventArgsCompleted);
                this.Receive(this.socket);
            }
            catch (Exception e)
            {
                this.lastErrorMessage = e.Message;
                return false;
            }

            return this.connected;
        }

        /// <summary>
        /// Передать роботу команду.
        /// </summary>
        /// <param name="command">
        /// Текст команды.
        /// </param>
        /// <returns>
        /// true, если нет ошибок.
        /// </returns>
        public bool SendCommandToRobot(string command)
        {
            if (this.connected)
            {
                try
                {
                    byte[] bytesSent = Encoding.ASCII.GetBytes(command + (char)13 + (char)10);
                    this.socket.Send(bytesSent, bytesSent.Length, 0);
#if DEBUG
                    this.sentCommandsLog.Write(bytesSent, 0, bytesSent.Length);
#endif
                }
                catch (Exception e)
                {
                    string errorText = command + "\t" + "Error: " + e.Message + "\r\n";
                    byte[] errorBytes = Encoding.ASCII.GetBytes(command + (char)13 + (char)10);
#if DEBUG
                    this.sentCommandsLog.Write(errorBytes, 0, errorBytes.Length);
#endif
                    this.lastErrorMessage = e.Message;
                    this.connected = false;
                    return false;
                }
            }
            else
            {
                this.lastErrorMessage = "Нет связи с роботом";
                return false;
            }

            this.lastErrorMessage = string.Empty;
            return true;
        }

        /// <summary>
        /// Очистка журналов отправляемых и принимаемых команд.
        /// </summary>
        public void ClearCommandLogs()
        {
            this.sentCommandsLog = new MemoryStream();
            this.receivedCommandsLog = new MemoryStream();
        }

        /// <summary>
        /// Сохранение журналов отправляемых и принимаемых команд в файлы.
        /// </summary>
        public void SaveLogsToFile()
        {
            string path = Directory.GetCurrentDirectory();

            string sentLogFilename = Path.Combine(path, "Sent.log");
            using (FileStream fileStream = new FileStream(sentLogFilename, FileMode.Create, FileAccess.Write))
            {
                fileStream.Write(this.sentCommandsLog.GetBuffer(), 0, Convert.ToInt32(this.sentCommandsLog.Length));
            }

            string receivedLogFilename = Path.Combine(path, "Received.log");
            using (FileStream fileStream = new FileStream(receivedLogFilename, FileMode.Create, FileAccess.Write))
            {
                fileStream.Write(this.receivedCommandsLog.GetBuffer(), 0, Convert.ToInt32(this.receivedCommandsLog.Length));
            }
        }

        /// <summary>
        /// Запуск асинхронного приёма эхо-команд от Android-приложения.
        /// </summary>
        /// <param name="socket">Клиентский сокет.</param>
        private void Receive(Socket socket)
        {
            try
            {
                // Create the state object.
                StateObject state = new StateObject();
                state.WorkSocket = socket;

                // Begin receiving the data from the remote device.
                socket.BeginReceive(state.Buffer, 0, StateObject.BufferSize, 0, new AsyncCallback(this.ReceiveCallback), state);
            }
            catch (Exception e)
            {
                this.lastErrorMessage = e.Message;
            }
        }

        /// <summary>
        /// Callback-функция приёма эхо-команд от Android-приложения.
        /// </summary>
        /// <param name="asyncResult">Объект IAsyncResult, в котором хранятся сведения о состоянии и любые данные, определенные пользователем, для этой асинхронной операции.</param>
        private void ReceiveCallback(IAsyncResult asyncResult)
        {
            try
            {
                // Retrieve the state object and the socket socket 
                // from the asynchronous state object.
                StateObject state = (StateObject)asyncResult.AsyncState;
                Socket client = state.WorkSocket;

                // Read data from the remote device.
                int bytesRead = client.EndReceive(asyncResult);

                if (bytesRead > 0)
                {
#if DEBUG
                    this.receivedCommandsLog.Write(state.Buffer, 0, bytesRead);
#endif
                }

                // Get the rest of the data.
                client.BeginReceive(state.Buffer, 0, StateObject.BufferSize, 0, new AsyncCallback(this.ReceiveCallback), state);
            }
            catch (Exception e)
            {
                this.lastErrorMessage = e.Message;
            }
        }

        /// <summary>
        /// Пользовательский класс, содержащий информацию об операции приёма.
        /// </summary>
        private sealed class StateObject
        {
            /// <summary>
            /// Размер буфера приёма.
            /// </summary>
            public const int BufferSize = 256;

            /// <summary>
            /// Буфер приёма.
            /// </summary>
            private byte[] buffer = new byte[BufferSize];

            /// <summary>
            /// Gets or sets клиентский сокет.
            /// </summary>
            public Socket WorkSocket { get; set; }

            /// <summary>
            /// Gets буфер приёма.
            /// </summary>
            public byte[] Buffer 
            { 
                get 
                { 
                    return this.buffer; 
                } 
            }
        }
    } // class
} // namespace
