// --------------------------------------------------------------------------------------------------------------------
// <copyright file="VideoHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2012
// </copyright>
// <summary>
//   Класс для приёма и воспроизведения видео.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    using Microsoft.Xna.Framework.Graphics;

    using MjpegProcessor;

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
        /// Инициализация видеотрансляции.
        /// </summary>
        public void InitializeVideo()
        {
            // Запуск воспроизведения видео:
            this.mjpeg.StopStream();
            this.mjpeg.ParseStream(new Uri(String.Format(
                @"http://{0}:{1}/videofeed",
                Settings.RoboHeadAddress,
                Settings.IpWebcamPort)));
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
