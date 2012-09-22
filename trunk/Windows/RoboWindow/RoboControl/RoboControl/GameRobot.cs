// --------------------------------------------------------------------------------------------------------------------
// <copyright file="GameRobot.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Класс управления приложением.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RoboControl
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading;

    using Microsoft.Xna.Framework;
    using Microsoft.Xna.Framework.Audio;
    using Microsoft.Xna.Framework.Content;
    using Microsoft.Xna.Framework.GamerServices;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework.Input;
    using Microsoft.Xna.Framework.Media;

    using RoboCommon;

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
    /// Способ управления роботом.
    /// </summary>
    internal enum ControlType
    {
        /// <summary>
        /// Управление клавиатурой.
        /// </summary>
        ctKeyboard,

        /// <summary>
        /// Управление геймпэдом.
        /// </summary>
        ctGamepad
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
        private static int debugStringColumnWidth = 130;

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
        /// Опции соединения с роботом.
        /// </summary>
        private ConnectSettings connectSettings;

        /// <summary>
        /// Опции управления роботом.
        /// </summary>
        private ControlSettings controlSettings;

        /// <summary>
        /// Объект для взаимодействия с роботом.
        /// </summary>
        private RobotHelper robotHelper;

        /// <summary>
        /// Объект для работы с фарами робота.
        /// </summary>
        private FlashlightHelper flashlightHelper;

        /// <summary>
        /// Объект для работы с ходовыми двигателями робота.
        /// </summary>
        private DriveHelper driveHelper;

        /// <summary>
        /// Объект для работы с сервоприводами головы робота.
        /// </summary>
        private LookHelper lookHelper;

        /// <summary>
        /// Объект для установки эмоций робота.
        /// </summary>
        private MoodHelper moodHelper;

        /// <summary>
        /// Объект для работы с ИК-пушкой робота.
        /// </summary>
        private GunHelper gunHelper;

        /// <summary>
        /// Объект для приёма и воспроизведения видеопотока.
        /// </summary>
        private VideoHelper videoHelper;

        /// <summary>
        /// Объект для приёма и воспроизведения аудиопотока.
        /// </summary>
        private AudioHelper audioHelper;

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
        /// Способ управления роботом: клавиатура или геймпэд.
        /// </summary>
        private ControlType controlType;

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

            this.controlSettings = new ControlSettings();
            this.LoadControlSettingsFromFile();

            this.connectSettings = new ConnectSettings(
                Properties.Settings.Default.RoboHeadAddress,
                Properties.Settings.Default.MessagePort);
            this.connectSettings.SingleMessageRepetitionsCount = Properties.Settings.Default.SingleMessageRepetitionsCount;

            this.robotHelper = new RobotHelper(this.connectSettings);
            this.flashlightHelper = new FlashlightHelper(this.robotHelper);
            this.driveHelper = new DriveHelper(this.robotHelper, this.controlSettings);
            this.lookHelper = new LookHelper(this.robotHelper, this.controlSettings);
            this.moodHelper = new MoodHelper(this.robotHelper, this.controlSettings);
            this.gunHelper = new GunHelper(this.robotHelper, this.controlSettings);
            this.videoHelper = new VideoHelper(this.robotHelper, this.controlSettings);
            this.audioHelper = new AudioHelper(this.robotHelper, this.controlSettings);
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
            KeyboardState keyboardState = Keyboard.GetState(PlayerIndex.One);
            GamePadState gamePadState = GamePad.GetState(PlayerIndex.One);

            // Запуск режима управления роботом клавиатурой.
            if (this.IsKeyChangedToDown(keyboardState, Keys.Space))
            {
                this.controlType = ControlType.ctKeyboard;
                this.gameState = GameState.gsRobotControl;
                this.InitializeRobot();
            }

            // Запуск режима управления роботом геймпэдом.
            if (this.IsButtonChangedToDown(gamePadState, Buttons.Start))
            {
                this.controlType = ControlType.ctGamepad;
                this.gameState = GameState.gsRobotControl;
                this.InitializeRobot();
            }

            if (this.IsButtonPressed(gamePadState, gamePadState.Buttons.Back))
            {
                this.Exit();
            }

            if (this.gameState == GameState.gsRobotControl)
            {
                this.CommonUpdateInRobotControlState(gameTime, keyboardState);

                switch (this.controlType)
                {
                    case ControlType.ctKeyboard:
                        this.KeyboardUpdateInRobotControlState(gameTime, keyboardState);
                        break;
                    case ControlType.ctGamepad:
                        this.GamepadUpdateInRobotControlState(gameTime, gamePadState);
                        break;
                }
                
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
        /// Обнаружение отпускания кнопки геймпэда.
        /// </summary>
        /// <param name="gamePadState">Текущее состояние геймпэда.</param>
        /// <param name="button">Отслеживаемая кнопка.</param>
        /// <returns>true, если кнопка была отпущена.</returns>
        private bool IsButtonChangedToUp(GamePadState gamePadState, Buttons button)
        {
            if (gamePadState.IsConnected == false)
            {
                return false;
            }

            return (gamePadState.IsButtonDown(button) == false) && this.previousGamePadState.IsButtonDown(button);
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
        /// Проверка зажата ли кнопка клавиатуры.
        /// </summary>
        /// <param name="keyboardState">Текущее состояние клавиатуры.</param>
        /// <param name="key">Отслеживаемая кнопка.</param>
        /// <returns>true, если кнопка зажата.</returns>
        private bool IsKeyPressed(KeyboardState keyboardState, Keys key)
        {
            return keyboardState.IsKeyDown(key);
        }

        /// <summary>
        /// Update в обоих режимах: управления роботом посредством клавиатуры и джойстика.
        /// </summary>
        /// <param name="gameTime">Игровое время.</param>
        /// <param name="keyboardState">Состояние клавиатуры.</param>
        private void CommonUpdateInRobotControlState(GameTime gameTime, KeyboardState keyboardState)
        {
            bool shiftIsPressed = this.IsShiftPressed(keyboardState);
            bool shiftIsNotPressed = !shiftIsPressed;

            if (this.IsKeyChangedToDown(keyboardState, Keys.F1) && shiftIsNotPressed)
            {
                this.moodHelper.SetMood(Mood.Normal);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.F2) && shiftIsNotPressed)
            {
                this.moodHelper.SetMood(Mood.Happy);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.F2) && shiftIsPressed)
            {
                this.moodHelper.ShowReadyToPlay(this.lookHelper);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.F3) && shiftIsNotPressed)
            {
                this.moodHelper.SetMood(Mood.Blue);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.F3) && shiftIsPressed)
            {
                this.moodHelper.ShowDepression(this.lookHelper);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.F4) && shiftIsNotPressed)
            {
                this.moodHelper.SetMood(Mood.Disaster);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.F5) && shiftIsNotPressed)
            {
                this.moodHelper.SetMood(Mood.Angry);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.T) && shiftIsNotPressed)
            {
                this.moodHelper.WagTail();
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.Y) && shiftIsNotPressed)
            {
                this.moodHelper.ShowYes();
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.N) && shiftIsNotPressed)
            {
                this.moodHelper.ShowNo();
            }
        }

        /// <summary>
        /// Update в режиме управления роботом посредством клавиатуры.
        /// </summary>
        /// <param name="gameTime">Игровое время.</param>
        /// <param name="keyboardState">Состояние клавиатуры.</param>
        private void KeyboardUpdateInRobotControlState(GameTime gameTime, KeyboardState keyboardState)
        {
            bool shiftIsPressed = this.IsShiftPressed(keyboardState);
            bool shiftIsNotPressed = !shiftIsPressed;

            if (this.IsKeyChangedToDown(keyboardState, Keys.L) && shiftIsNotPressed)
            {
                this.flashlightHelper.Switch();
            }

            if ((this.IsKeyChangedToDown(keyboardState, Keys.LeftControl) || this.IsKeyChangedToDown(keyboardState, Keys.RightControl)) && shiftIsNotPressed)
            {
                this.gunHelper.Fire();
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.Scroll))
            {
                this.driveHelper.SwitchTurboMode();
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.CapsLock))
            {
                // В прогулочном режиме центральное направление взгляда по вертикали - строго горизонтально.
                // Так проще целиться управлять движением. В режиме общения (не прогулочный) - чуть вверх.
                this.lookHelper.WalkModeOn = !this.lookHelper.WalkModeOn;
                this.lookHelper.LookForward();
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.Home) && shiftIsNotPressed)
            {
                // Установка головы в положение "смотреть вперёд".
                this.lookHelper.LookForward();
            }

            // Установка признака быстрого фиксированного обзора (при отпускании кнопок управления
            // голова остаётся в установленном положении). При быстром фиксированном обзоре 
            // голова поворачивается с большей скоростью.
            this.lookHelper.FastModeOn = shiftIsPressed;

            if (this.IsKeyPressed(keyboardState, Keys.Left))
            {
                // Поворот головы влево с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookLeft(gameTime);
            }

            if (this.IsKeyPressed(keyboardState, Keys.Right))
            {
                // Поворот головы вправо с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookRight(gameTime);
            }

            if (this.IsKeyPressed(keyboardState, Keys.Up))
            {
                // Поворот головы вверх с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookUp(gameTime);
            }

            if (this.IsKeyPressed(keyboardState, Keys.Down))
            {
                // Поворот головы вниз с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookDown(gameTime);
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.D1) && shiftIsNotPressed)
            {
                this.driveHelper.SpeedForKeyboardControl = this.controlSettings.Speed1;
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.D2) && shiftIsNotPressed)
            {
                this.driveHelper.SpeedForKeyboardControl = this.controlSettings.Speed2;
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.D3) && shiftIsNotPressed)
            {
                this.driveHelper.SpeedForKeyboardControl = this.controlSettings.Speed3;
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.D4) && shiftIsNotPressed)
            {
                this.driveHelper.SpeedForKeyboardControl = this.controlSettings.Speed4;
            }

            if (this.IsKeyChangedToDown(keyboardState, Keys.D5) && shiftIsNotPressed)
            {
                this.driveHelper.SpeedForKeyboardControl = this.controlSettings.Speed5;
            }

            DateTime nowTime = DateTime.Now;
            TimeSpan timePassed = nowTime - this.lastTimeCommandSent;
            if (timePassed >= this.controlSettings.MinCommandInterval)
            {
                this.driveHelper.Drive(
                    this.IsKeyPressed(keyboardState, Keys.W), 
                    this.IsKeyPressed(keyboardState, Keys.S),
                    this.IsKeyPressed(keyboardState, Keys.A), 
                    this.IsKeyPressed(keyboardState, Keys.D));

                this.lastTimeCommandSent = nowTime;
            }
        }

        /// <summary>
        /// Update в режиме управления роботом посредством геймпэда.
        /// </summary>
        /// <param name="gameTime">Игровое время.</param>
        /// <param name="gamePadState">Состояние геймпэда.</param>
        private void GamepadUpdateInRobotControlState(GameTime gameTime, GamePadState gamePadState)
        {
            if (this.IsButtonChangedToDown(gamePadState, Buttons.A))
            {
                this.moodHelper.ShowYes();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.B))
            {
                this.moodHelper.ShowNo();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.X))
            {
                this.moodHelper.WagTail();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.Y))
            {
                this.flashlightHelper.Switch();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.RightTrigger))
            {
                this.flashlightHelper.TurnOn();
            }

            if (this.IsButtonChangedToUp(gamePadState, Buttons.RightTrigger))
            {
                this.flashlightHelper.TurnOff();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.LeftTrigger))
            {
                this.gunHelper.Fire();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.LeftStick))
            {
                this.driveHelper.SwitchTurboMode();
            }

            if (this.IsButtonChangedToDown(gamePadState, Buttons.RightStick))
            {
                // В прогулочном режиме центральное направление взгляда по вертикали - строго горизонтально.
                // Так проще целиться управлять движением. В режиме общения (не прогулочный) - чуть вверх.
                this.lookHelper.WalkModeOn = !this.lookHelper.WalkModeOn;
                this.lookHelper.LookForward();
            }

            // Установка признака быстрого фиксированного обзора (при отпускании кнопок управления
            // голова остаётся в установленном положении). При быстром фиксированном обзоре 
            // голова поворачивается с большей скоростью.
            this.lookHelper.FastModeOn = this.IsButtonPressed(gamePadState, gamePadState.Buttons.RightShoulder);

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
                if (timePassed >= this.controlSettings.MinCommandInterval)
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
            string speedText = " (" + Math.Round((double)this.driveHelper.SpeedForKeyboardControl * 100 / 255).ToString() + "%)";

            motorCommand = this.driveHelper.LeftMotorCommand + speedText;
            color = Color.White;
            this.spriteBatch.DrawString(this.debugFont, motorCommand, debugStringPosition1, color);

            motorCommand = this.driveHelper.RightMotorCommand + speedText;
            color = Color.White;
            this.spriteBatch.DrawString(this.debugFont, motorCommand, debugStringPosition2, color);

            if (this.driveHelper.TurboModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Турбо режим", debugStringPosition1c1, Color.Orange);
            }

            if (this.driveHelper.RotationModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Режим разворота", debugStringPosition2c1, Color.Orange);
            }

            if (this.lookHelper.FastModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Быстрый обзор", debugStringPosition3c1, Color.Orange);
            }

            if (!this.lookHelper.WalkModeOn)
            {
                this.spriteBatch.DrawString(this.debugFont, "Режим обзора", debugStringPosition4c1, Color.Orange);
            }

            this.spriteBatch.DrawString(this.debugFont, this.lookHelper.HorizontalServoCommand, debugStringPosition3, Color.White);
            this.spriteBatch.DrawString(this.debugFont, this.lookHelper.VerticalServoCommand, debugStringPosition4, Color.White);

            color = this.flashlightHelper.FlashlightTurnedOn ? Color.Yellow : Color.White;
            this.spriteBatch.DrawString(this.debugFont, this.flashlightHelper.FlashlightCommand, debugStringPosition5, color);

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
            this.driveHelper.Stop();
            this.lookHelper.LookForward();
            this.flashlightHelper.TurnOff();

            // Запуск воспроизведения видео:
            this.videoHelper.InitializeVideo();

            // Запуск воспроизведения аудио:
            this.audioHelper.InitializeAudio();
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
            this.audioHelper.FinalizeAudio();
        }

        /// <summary>
        /// Определяет нажата ли хоть одна клавиша Shift.
        /// </summary>
        /// <param name="keyboardState">Состояние клавиатуры.</param>
        /// <returns>True, если нажата клавиша Shift.</returns>
        private bool IsShiftPressed(KeyboardState keyboardState)
        {
            return this.IsKeyPressed(keyboardState, Keys.LeftShift) || this.IsKeyPressed(keyboardState, Keys.RightShift);
        }

        /// <summary>
        /// Чтение епций приложения из файла конфигурации.
        /// </summary>
        private void LoadControlSettingsFromFile()
        {
            this.controlSettings.ReverseHeadTangage = Properties.Settings.Default.ReverseHeadTangage;
            this.controlSettings.IpWebcamPort = Properties.Settings.Default.IpWebcamPort;
            this.controlSettings.DriveModeNormalMaxSpeed = Properties.Settings.Default.DriveModeNormalMaxSpeed;
            this.controlSettings.DriveModeTurboMaxSpeed = Properties.Settings.Default.DriveModeTurboMaxSpeed;
            this.controlSettings.Speed1 = Properties.Settings.Default.Speed1;
            this.controlSettings.Speed2 = Properties.Settings.Default.Speed2;
            this.controlSettings.Speed3 = Properties.Settings.Default.Speed3;
            this.controlSettings.Speed4 = Properties.Settings.Default.Speed4;
            this.controlSettings.Speed5 = Properties.Settings.Default.Speed5;
            this.controlSettings.PlayVideo = Properties.Settings.Default.PlayVideo;
            this.controlSettings.PlayAudio = Properties.Settings.Default.PlayAudio;
        }
    }
}
