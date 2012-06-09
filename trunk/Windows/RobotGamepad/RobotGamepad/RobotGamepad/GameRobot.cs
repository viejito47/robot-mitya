// --------------------------------------------------------------------------------------------------------------------
// <copyright file="GameRobot.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс управления приложением.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading;

    using AXVLC;

    using Microsoft.Xna.Framework;
    using Microsoft.Xna.Framework.Audio;
    using Microsoft.Xna.Framework.Content;
    using Microsoft.Xna.Framework.GamerServices;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework.Input;
    using Microsoft.Xna.Framework.Media;

    /// <summary>
    /// Состояние приложения: главное меню или управление роботом.
    /// </summary>
    internal enum GameState 
    {
        /// <summary>
        /// Режим отображения меню.
        /// </summary>
        gsMenu,

        /// <summary>
        /// Режим управления роботом.
        /// </summary>
        gsRobotControl 
    }
    
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class GameRobot : Microsoft.Xna.Framework.Game
    {
        /// <summary>
        /// Вертикальный интервал между строками на экране.
        /// </summary>
        private static int debugStringInterval = 25;

        /// <summary>
        /// Ширина строк на экране.
        /// </summary>
        private static int debugStringColumnWidth = 100;

        /// <summary>
        /// Координаты первой ячейки 1-й строки.
        /// </summary>
        private static Vector2 debugStringPosition1 = new Vector2(20, 20);

        /// <summary>
        /// Координаты второй ячейки 1-й строки.
        /// </summary>
        private static Vector2 debugStringPosition1c1 = new Vector2(debugStringPosition1.X + debugStringColumnWidth, debugStringPosition1.Y);

        /// <summary>
        /// Координаты первой ячейки 2-й строки.
        /// </summary>
        private static Vector2 debugStringPosition2 = new Vector2(20, debugStringPosition1.Y + debugStringInterval);

        /// <summary>
        /// Координаты второй ячейки 2-й строки.
        /// </summary>
        private static Vector2 debugStringPosition2c1 = new Vector2(debugStringPosition2.X + debugStringColumnWidth, debugStringPosition2.Y);

        /// <summary>
        /// Координаты первой ячейки 3-й строки.
        /// </summary>
        private static Vector2 debugStringPosition3 = new Vector2(20, debugStringPosition2.Y + debugStringInterval);

        /// <summary>
        /// Координаты второй ячейки 3-й строки.
        /// </summary>
        private static Vector2 debugStringPosition3c1 = new Vector2(debugStringPosition3.X + debugStringColumnWidth, debugStringPosition3.Y);

        /// <summary>
        /// Координаты первой ячейки 4-й строки.
        /// </summary>
        private static Vector2 debugStringPosition4 = new Vector2(20, debugStringPosition3.Y + debugStringInterval);

        /// <summary>
        /// Координаты второй ячейки 4-й строки.
        /// </summary>
        private static Vector2 debugStringPosition4c1 = new Vector2(debugStringPosition4.X + debugStringColumnWidth, debugStringPosition4.Y);

        /// <summary>
        /// Координаты первой ячейки 5-й строки.
        /// </summary>
        private static Vector2 debugStringPosition5 = new Vector2(20, debugStringPosition4.Y + debugStringInterval);

        /// <summary>
        /// Координаты первой ячейки 6-й строки.
        /// </summary>
        private static Vector2 debugStringPosition6 = new Vector2(20, debugStringPosition5.Y + debugStringInterval);

        /// <summary>
        /// Координаты первой ячейки 7-й строки.
        /// </summary>
        private static Vector2 debugStringPosition7 = new Vector2(20, debugStringPosition6.Y + debugStringInterval);

        /// <summary>
        /// Координаты первой ячейки 8-й строки.
        /// </summary>
        private static Vector2 debugStringPosition8 = new Vector2(20, debugStringPosition7.Y + debugStringInterval);

        /// <summary>
        /// Объект для взаимодействия с роботом.
        /// </summary>
        private RobotHelper robotHelper = new RobotHelper();

        /// <summary>
        /// Объект для работы с фарами робота.
        /// </summary>
        private FlashlightHelper flashlightHelper = new FlashlightHelper();

        /// <summary>
        /// Объект для работы с ходовыми двигателями робота.
        /// </summary>
        private DriveHelper driveHelper = new DriveHelper();

        /// <summary>
        /// Объект для работы с сервоприводами головы робота.
        /// </summary>
        private LookHelper lookHelper = new LookHelper();

        /// <summary>
        /// Объект для установки эмоций робота.
        /// </summary>
        private MoodHelper moodHelper = new MoodHelper();

        /// <summary>
        /// Объект для работы с ИК-пушкой робота.
        /// </summary>
        private GunHelper gunHelper = new GunHelper();

        /// <summary>
        /// Объект для приёма и воспроизведения видеопотока.
        /// </summary>
        private VideoHelper videoHelper = new VideoHelper();

        /// <summary>
        /// Менеджер графического устройства.
        /// </summary>
        private GraphicsDeviceManager graphics;
        
        /// <summary>
        /// Область для рисования.
        /// </summary>
        private SpriteBatch spriteBatch;

        /// <summary>
        /// Текстура для вывода видео.
        /// </summary>
        private Texture2D videoTexture;

        /// <summary>
        /// Плагин VLC. 
        /// Используется для воспроизведения потокового аудио, полученного от IP Webcam.
        /// </summary>
        /// <remarks>
        /// Объект из ActiveX-библиотеки VLC (www.videolan.org).
        /// Требует установки VLC, регистрации ActiveX-библиотеки axvlc.dll, а затем добавления в ссылки проекта COM-компоненты "VideoLAN VLC ActiveX Plugin" (в обозревателе решений отображается как "AXVLC").
        /// </remarks>
        private AXVLC.VLCPlugin2 audio;

        /// <summary>
        /// Шрифт для вывода текста.
        /// </summary>
        private SpriteFont debugFont;

        /// <summary>
        /// Текущее состояние приложения.
        /// </summary>
        private GameState gameState = GameState.gsMenu;

        /// <summary>
        /// Последний момент времени, когда отправлялись команды управления двигателями и поворотом головы.
        /// </summary>
        private DateTime lastTimeCommandSent = new DateTime();

        /// <summary>
        /// Предыдущее состояние геймпэда.
        /// </summary>
        private GamePadState previousGamePadState;

        /// <summary>
        /// Предыдущее состояние клавиатуры.
        /// </summary>
        private KeyboardState previousKeyboardState;

        /// <summary>
        /// Initializes a new instance of the GameRobot class.
        /// </summary>
        public GameRobot()
        {
            this.IsFixedTimeStep = false;
            this.graphics = new GraphicsDeviceManager(this);
            this.graphics.SynchronizeWithVerticalRetrace = false;
            
            // this.graphics.IsFullScreen = true;
            // this.IsMouseVisible = false;
            Content.RootDirectory = "Content";
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            this.graphics.IsFullScreen = true;

            base.Initialize();

            this.previousGamePadState = GamePad.GetState(PlayerIndex.One);
            this.previousKeyboardState = Keyboard.GetState(PlayerIndex.One);
            this.flashlightHelper.Initialize(this.robotHelper);
            this.driveHelper.Initialize(this.robotHelper);
            this.lookHelper.Initialize(this.robotHelper);
            this.moodHelper.Initialize(this.robotHelper);
            this.gunHelper.Initialize(this.robotHelper);

            this.audio = new AXVLC.VLCPlugin2Class();
        }

        /// <summary>
        /// Деинициализация управления роботом.
        /// </summary>
        protected override void EndRun()
        {
            base.EndRun();

            if (this.gameState == GameState.gsRobotControl)
            {
                this.FinalizeRobot();
            }
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            this.spriteBatch = new SpriteBatch(GraphicsDevice);

            this.debugFont = Content.Load<SpriteFont>("CourierNew");
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            GamePadState gamePadState = GamePad.GetState(PlayerIndex.One);
            KeyboardState keyboardState = Keyboard.GetState(PlayerIndex.One);

            if (this.IsButtonPressed(gamePadState, gamePadState.Buttons.Back))
            {
                this.Exit();
            }

            // Запуск режима управления роботом.
            if (this.IsButtonChangedToDown(gamePadState, Buttons.Start) || this.IsKeyChangedToDown(keyboardState, Keys.Space))
            {
                this.gameState = GameState.gsRobotControl;
                this.InitializeRobot();
            }

            if (this.gameState == GameState.gsRobotControl)
            {
                this.UpdateInRobotControlState(gameTime, gamePadState);
                this.videoTexture = this.videoHelper.GetVideoTexture(this.GraphicsDevice);
            }

            this.previousGamePadState = gamePadState;
            this.previousKeyboardState = keyboardState;

            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.CornflowerBlue);
            
            // Отрисовка экрана пока производится только режиме управления роботом.
            // В режиме меню экран чистый.
            if (this.gameState == GameState.gsRobotControl)
            {
                this.DrawInRobotControlState(gameTime);
            }

            base.Draw(gameTime);
        }

        /// <summary>
        /// Обнаружение нажатия на кнопку геймпэда.
        /// </summary>
        /// <param name="gamePadState">Текущее состояние геймпэда.</param>
        /// <param name="button">Отслеживаемая кнопка.</param>
        /// <returns>true, если кнопка была нажата.</returns>
        private bool IsButtonChangedToDown(GamePadState gamePadState, Buttons button)
        {
            if (gamePadState.IsConnected == false)
            {
                return false;
            }

            return gamePadState.IsButtonDown(button) && (this.previousGamePadState.IsButtonDown(button) == false);
        }

        /// <summary>
        /// Проверка зажата ли кнопка геймпэда.
        /// </summary>
        /// <param name="gamePadState">Текущее состояние геймпэда.</param>
        /// <param name="buttonState">Отслеживаемая кнопка.</param>
        /// <returns>true, если кнопка зажата.</returns>
        private bool IsButtonPressed(GamePadState gamePadState, ButtonState buttonState)
        {
            if (gamePadState.IsConnected == false)
            {
                return false;
            }

            return buttonState == ButtonState.Pressed;
        }

        /// <summary>
        /// Обнаружение нажатия на клавишу клавиатуры.
        /// </summary>
        /// <param name="keyboardState">Текущее состояние клавиатуры.</param>
        /// <param name="key">Отслеживаемая клавиша.</param>
        /// <returns>true, если клавиша была нажата.</returns>
        private bool IsKeyChangedToDown(KeyboardState keyboardState, Keys key)
        {
            return keyboardState.IsKeyDown(key) && (this.previousKeyboardState.IsKeyDown(key) == false);
        }

        /// <summary>
        /// Update в режиме управления роботом.
        /// </summary>
        /// <param name="gameTime">Игровое время.</param>
        /// <param name="gamePadState">Состояние геймпэда.</param>
        private void UpdateInRobotControlState(GameTime gameTime, GamePadState gamePadState)
        {
            if (this.IsButtonChangedToDown(gamePadState, Buttons.A))
            {
                this.moodHelper.SetMood(Mood.Happy);
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.X))
            {
                this.moodHelper.SetMood(Mood.Blue);
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.Y))
            {
                this.moodHelper.SetMood(Mood.Disaster);
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.B))
            {
                this.moodHelper.SetMood(Mood.Angry);
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.LeftTrigger))
            {
                this.flashlightHelper.Switch();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.RightTrigger))
            {
                this.gunHelper.Fire();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.LeftStick))
            {
                this.driveHelper.SwitchTurboMode();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.RightStick))
            {
                // В боевом режиме центральное направление взгляда по вертикали - строго горизонтально.
                // Так проще целиться и стрелять. В обычном режиме - чуть вверх.
                this.lookHelper.WarModeOn = !this.lookHelper.WarModeOn;
                this.lookHelper.LookForward();
            }

            // Установка признака плавного фиксированного обзора (джойстик DPAD - при отпускании кнопок 
            // джойстика голова остаётся в установленном положении). При плавном фиксированном обзоре 
            // голова поворачивается с меньшей скоростью.
            this.lookHelper.SlowModeOn = this.IsButtonPressed(gamePadState, gamePadState.Buttons.RightShoulder);

            if (this.IsButtonPressed(gamePadState, gamePadState.DPad.Left))
            {
                // Поворот головы влево с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookLeft(gameTime);
            }

            if (this.IsButtonPressed(gamePadState, gamePadState.DPad.Right))
            {
                // Поворот головы вправо с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookRight(gameTime);
            }

            if (this.IsButtonPressed(gamePadState, gamePadState.DPad.Up))
            {
                // Поворот головы вверх с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookUp(gameTime);
            }

            if (this.IsButtonPressed(gamePadState, gamePadState.DPad.Down))
            {
                // Поворот головы вниз с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookDown(gameTime);
            }

            // Скорости двигателей и углы сервоприводов головы определяются и устанавливаются с заданной периодичностью.
            if (gamePadState.IsConnected)
            {
                DateTime nowTime = DateTime.Now;
                TimeSpan timePassed = nowTime - this.lastTimeCommandSent;
                if (timePassed >= Settings.MinCommandInterval)
                {
                    this.driveHelper.RotationModeOn = gamePadState.Buttons.LeftShoulder == ButtonState.Pressed;
                    this.driveHelper.Drive(gamePadState.ThumbSticks.Left.X, gamePadState.ThumbSticks.Left.Y);

                    this.lookHelper.Look(gamePadState.ThumbSticks.Right.X, gamePadState.ThumbSticks.Right.Y);

                    this.lastTimeCommandSent = nowTime;
                }
            }
        }

        /// <summary>
        /// Отрисовка экрана в режиме управления роботом.
        /// </summary>
        /// <param name="gameTime">
        /// Игровое время.
        /// </param>
        private void DrawInRobotControlState(GameTime gameTime)
        {
            this.spriteBatch.Begin();

            if (this.videoTexture != null)
            {
                // this.spriteBatch.Draw(this.videoTexture, Vector2.Zero, Color.White);
                // Rectangle rectangle = new Rectangle(
                //    this.graphics.PreferredBackBufferWidth - this.videoTexture.Width - 10,
                //    (this.graphics.PreferredBackBufferHeight - this.videoTexture.Height) / 2,
                //    this.videoTexture.Width,
                //    this.videoTexture.Height);
                Rectangle rectangle = new Rectangle(
                    0,
                    0,
                    this.graphics.PreferredBackBufferWidth,
                    this.graphics.PreferredBackBufferHeight);
                this.spriteBatch.Draw(this.videoTexture, rectangle, Color.White);
            }

            Color color;
            string motorCommand;

            motorCommand = this.driveHelper.LastLeftMotorCommand;
            color = (motorCommand.Length > 1) && (motorCommand[1] == 'F') ? Color.White : Color.Orange;
            this.spriteBatch.DrawString(this.debugFont, motorCommand, debugStringPosition1, color);

            motorCommand = this.driveHelper.LastRightMotorCommand;
            color = (motorCommand.Length > 1) && (motorCommand[1] == 'F') ? Color.White : Color.Orange;
            this.spriteBatch.DrawString(this.debugFont, motorCommand, debugStringPosition2, color);

            if (this.driveHelper.TurboModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Турбо режим", debugStringPosition1c1, Color.Orange);
            }

            if (this.driveHelper.RotationModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Режим разворота", debugStringPosition2c1, Color.Orange);
            }

            if (this.lookHelper.SlowModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Плавный обзор", debugStringPosition3c1, Color.Orange);
            }

            if (this.lookHelper.WarModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Боевой настрой", debugStringPosition4c1, Color.Orange);
            }

            this.spriteBatch.DrawString(this.debugFont, this.lookHelper.LastHorizontalServoCommand, debugStringPosition3, Color.White);
            this.spriteBatch.DrawString(this.debugFont, this.lookHelper.LastVerticalServoCommand, debugStringPosition4, Color.White);

            color = this.flashlightHelper.FlashlightTurnedOn ? Color.Yellow : Color.White;
            this.spriteBatch.DrawString(this.debugFont, this.flashlightHelper.LastFlashlightCommand, debugStringPosition5, color);

            string moodText = "Настроение: ";
            switch (this.moodHelper.Mood)
            {
                case Mood.Happy:
                    moodText += "счастлив";
                    break;
                case Mood.Blue:
                    moodText += "грустно";
                    break;
                case Mood.Angry:
                    moodText += "злой";
                    break;
                case Mood.Disaster:
                    moodText += "раздавлен";
                    break;
                default:
                    moodText += "нормально";
                    break;
            }

            this.spriteBatch.DrawString(this.debugFont, moodText, debugStringPosition6, Color.White);

            this.spriteBatch.DrawString(this.debugFont, this.PercentToText(this.gunHelper.GetChargePercent()), debugStringPosition7, Color.White);

            this.spriteBatch.DrawString(this.debugFont, this.robotHelper.LastErrorMessage, debugStringPosition8, Color.Orange);

            this.spriteBatch.End();
        }

        /// <summary>
        /// Получение строки для отображения степени "заряда" пушки для выстрела.
        /// Время "заряда" пушки определяется в Settings.GunChargeTime.
        /// </summary>
        /// <param name="percent">Величина "заряда" в процентах.</param>
        /// <returns>Строка вида ++++++---</returns>
        private string PercentToText(int percent)
        {
            const int PercentStep = 5;

            string result = string.Empty;
            for (int i = 0; i < 100 / PercentStep; i++)
            {
                int endPercent = (i + 1) * PercentStep;
                if (endPercent <= percent)
                {
                    result += "+";
                }
                else
                {
                    result += "-";
                }
            }

            return result;
        }

        /// <summary>
        /// Инициализация робота.
        /// </summary>
        private void InitializeRobot()
        {
            this.robotHelper.ClearCommandLogs();
            this.robotHelper.Connect();
            this.driveHelper.Stop();
            this.lookHelper.LookForward();
            this.flashlightHelper.TurnOff();

            // Запуск воспроизведения видео:
            this.videoHelper.InitializeVideo();

            // Запуск воспроизведения аудио:
            this.audio.Visible = false;
            this.audio.playlist.items.clear();
            this.audio.AutoPlay = true;
            this.audio.Volume = 200;
            string[] options = new string[] { @":network-caching=20" };
            this.audio.playlist.add(
                String.Format(
                    @"http://{0}:{1}/audio.wav",
                    Settings.RoboHeadAddress,
                    Settings.IpWebcamPort),
                null,
                options);
            this.audio.playlist.playItem(0);
        }
        
        /// <summary>
        /// Деинициализация робота.
        /// </summary>
        private void FinalizeRobot()
        {
            this.driveHelper.Stop();
            this.lookHelper.LookForward();
            this.flashlightHelper.TurnOff();
            Thread.Sleep(1000); // (немного ждем прихода последних эхо-команд от Android-приложения)

            this.videoHelper.FinalizeVideo();
            if (this.audio.playlist.items.count > 0)
            {
                if (this.audio.playlist.isPlaying)
                {
                    this.audio.playlist.stop();
                }
            }

#if DEBUG
            this.robotHelper.SaveLogsToFile();
#endif
        }
    }
}
