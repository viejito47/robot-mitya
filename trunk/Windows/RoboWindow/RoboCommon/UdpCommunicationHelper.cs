// --------------------------------------------------------------------------------------------------------------------
// <copyright file="UdpCommunicationHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2013
// </copyright>
// <summary>
//   Implementation of UDP-communication between PC and robot.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboCommon
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Net;
    using System.Net.Sockets;
    using System.Text;

    /// <summary>
    /// Implementation of UDP-communication between PC and robot.
    /// </summary>
    public sealed class UdpCommunicationHelper : CommunicationHelper
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="UdpCommunicationHelper" /> class.
        /// </summary>
        /// <param name="roboHeadAddress">Phone's IP-address string.</param>
        /// <param name="messagePort">Port for socket.</param>
        /// <param name="nonrecurrentMessageRepetitions">Number of repetitions we send nonrecurrent messages to the robot.</param>
        public UdpCommunicationHelper(string roboHeadAddress, int messagePort, int nonrecurrentMessageRepetitions)
            : base(nonrecurrentMessageRepetitions)
        {
            this.RoboHeadAddress = IPAddress.Parse(roboHeadAddress);
            this.MessagePort = messagePort;
        }

        /// <summary>
        /// Gets phone's IP-address.
        /// </summary>
        public IPAddress RoboHeadAddress { get; private set; }

        /// <summary>
        /// Gets the port for socket.
        /// </summary>
        public int MessagePort { get; private set; }

        /// <summary>
        /// Internal method for message transmition throught UDP socket.
        /// Doesn't correct message. Doesn't handle errors, just generate exceptions.
        /// </summary>
        /// <param name="message">
        /// Message to transmit.
        /// </param>
        protected override void TransmitMessage(string message)
        {
            byte[] messageBytes = Encoding.ASCII.GetBytes(message + (char)13 + (char)10);
            UdpClient udpClient = new UdpClient();
            IPEndPoint endPoint = new IPEndPoint(this.RoboHeadAddress, this.MessagePort);
            int bytesSent = udpClient.Send(messageBytes, messageBytes.Length, endPoint);
            if (bytesSent != messageBytes.Length)
            {
                throw new IOException("Нет связи с роботом");
            }
        }

        /// <summary>
        /// Communication finalization.
        /// </summary>
        protected override void FinalizePort()
        {
        }
    }
}
