// --------------------------------------------------------------------------------------------------------------------
// <copyright file="VideoHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2012
// </copyright>
// <summary>
//   Класс для приёма и воспроизведения видео.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboControl
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    using Microsoft.Xna.Framework.Graphics;

    using MjpegProcessor;
    using RoboCommon;

    /// <summary>
    /// Класс для приёма и воспроизведения видео.
    /// </summary>
    public sealed class VideoHelper
    {
        /// <summary>
        /// Декодер MJPEG.
        /// Используется для воспроизведения потокового видео (точнее, MJPEG), полученного от IP Webcam.
        /// </summary>
        /// <remarks>
        /// Требует подключения .NET библиотеки "MjpegProcessorXna4".
        /// </remarks>
        private MjpegDecoder mjpeg = new MjpegDecoder();

        /// <summary>
        /// Опции соединения с роботом.
        /// </summary>
        private ConnectSettings connectSettings;

        /// <summary>
        /// Опции управления роботом.
        /// </summary>
        private ControlSettings controlSettings;

        /// <summary>
        /// Initializes a new instance of the VideoHelper class.
        /// </summary>
        /// <param name="connectSettings">
        /// Опции соединения с роботом.
        /// </param>
        /// <param name="controlSettings">
        /// Опции управления роботом.
        /// </param>
        public VideoHelper(ConnectSettings connectSettings, ControlSettings controlSettings)
        {
            if (connectSettings == null)
            {
                throw new ArgumentNullException("connectSettings");
            }

            if (controlSettings == null)
            {
                throw new ArgumentNullException("controlSettings");
            }

            this.connectSettings = connectSettings;
            this.controlSettings = controlSettings;
        }

        /// <summary>
        /// Инициализация видеотрансляции.
        /// </summary>
        public void InitializeVideo()
        {
            // Запуск воспроизведения видео:
            this.mjpeg.StopStream();
            this.mjpeg.ParseStream(new Uri(String.Format(
                @"http://{0}:{1}/videofeed",
                this.connectSettings.RoboHeadAddress,
                this.controlSettings.IpWebcamPort)));
        }

        /// <summary>
        /// Остановка видеотрансляции.
        /// </summary>
        public void FinalizeVideo()
        {
            this.mjpeg.StopStream();
        }

        /// <summary>
        /// Получение кадра для отображения.
        /// </summary>
        /// <param name="graphicsDevice">Текущее графическое устройство.</param>
        /// <returns>Текстура с кадром.</returns>
        public Texture2D GetVideoTexture(GraphicsDevice graphicsDevice)
        {
            return this.mjpeg.GetMjpegFrame(graphicsDevice);
        }
    }
}
