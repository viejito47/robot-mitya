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
    internal enum GameState { gsMenu, gsRobotControl };
    
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class GameRobot : Microsoft.Xna.Framework.Game
    {
        private RobotHelper robotHelper = new RobotHelper();
        private FlashlightHelper flashlightHelper = new FlashlightHelper();
        private DriveHelper driveHelper = new DriveHelper();
        private LookHelper lookHelper = new LookHelper();
        private MoodHelper moodHelper = new MoodHelper();
        private GunHelper gunHelper = new GunHelper();

        private GraphicsDeviceManager graphics;
        private SpriteBatch spriteBatch;
        
        // Константы для вывода текста:
        private SpriteFont debugFont;
        private static int DebugStringInterval = 25;
        private static int DebugStringColumnWidth = 100;
        private static Vector2 DebugStringPosition1 = new Vector2(20, 20);
        private static Vector2 DebugStringPosition1_1 = new Vector2(DebugStringPosition1.X + DebugStringColumnWidth, DebugStringPosition1.Y);
        private static Vector2 DebugStringPosition2 = new Vector2(20, DebugStringPosition1.Y + DebugStringInterval);
        private static Vector2 DebugStringPosition2_1 = new Vector2(DebugStringPosition2.X + DebugStringColumnWidth, DebugStringPosition2.Y);
        private static Vector2 DebugStringPosition3 = new Vector2(20, DebugStringPosition2.Y + DebugStringInterval);
        private static Vector2 DebugStringPosition3_1 = new Vector2(DebugStringPosition3.X + DebugStringColumnWidth, DebugStringPosition3.Y);
        private static Vector2 DebugStringPosition4 = new Vector2(20, DebugStringPosition3.Y + DebugStringInterval);
        private static Vector2 DebugStringPosition4_1 = new Vector2(DebugStringPosition4.X + DebugStringColumnWidth, DebugStringPosition4.Y);
        private static Vector2 DebugStringPosition5 = new Vector2(20, DebugStringPosition4.Y + DebugStringInterval);
        private static Vector2 DebugStringPosition6 = new Vector2(20, DebugStringPosition5.Y + DebugStringInterval);
        private static Vector2 DebugStringPosition7 = new Vector2(20, DebugStringPosition6.Y + DebugStringInterval);
        private static Vector2 DebugStringPosition8 = new Vector2(20, DebugStringPosition7.Y + DebugStringInterval);

        private GameState gameState = GameState.gsMenu;

        private DateTime lastTimeCommandSent = new DateTime();

        private GamePadState previousGamePadState;

        /// <summary>
        /// Конструктор класса.
        /// </summary>
        public GameRobot()
        {
            graphics = new GraphicsDeviceManager(this);
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
            this.flashlightHelper.Initialize(this.robotHelper);
            this.driveHelper.Initialize(this.robotHelper);
            this.lookHelper.Initialize(this.robotHelper);
            this.moodHelper.Initialize(this.robotHelper);
            this.gunHelper.Initialize(this.robotHelper);
        }

        /// <summary>
        /// Деинициализация управления роботом.
        /// </summary>
        protected override void EndRun()
        {
            base.EndRun();

            if (this.gameState == GameState.gsRobotControl)
            {
                this.InitializeRobot();
            }
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);

            debugFont = Content.Load<SpriteFont>("CourierNew");
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

            if (gamePadState.Buttons.Back == ButtonState.Pressed)
            {
                this.Exit();
            }

            // Запуск режима управления роботом.
            if (this.IsButtonChangedToDown(gamePadState, Buttons.Start))
            {
                this.gameState = GameState.gsRobotControl;
                this.InitializeRobot();
            }

            if (this.gameState == GameState.gsRobotControl)
            {
                this.UpdateInRobotControlState(gameTime, gamePadState);
            }

            this.previousGamePadState = gamePadState;

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
            return (gamePadState.IsButtonDown(button)) && (this.previousGamePadState.IsButtonDown(button) == false);
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
                this.lookHelper.WarModeOn = ! this.lookHelper.WarModeOn;
                this.lookHelper.LookForward();
            }

            // Установка признака плавного фиксированного обзора (джойстик DPAD - при отпускании кнопок 
            // джойстика голова остаётся в установленном положении). При плавном фиксированном обзоре 
            // голова поворачивается с меньшей скоростью.
            this.lookHelper.SlowModeOn = gamePadState.Buttons.RightShoulder == ButtonState.Pressed;

            if (gamePadState.DPad.Left == ButtonState.Pressed)
            {
                // Поворот головы влево с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookLeft(gameTime);
            }

            if (gamePadState.DPad.Right == ButtonState.Pressed)
            {
                // Поворот головы вправо с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookRight(gameTime);
            }

            if (gamePadState.DPad.Up == ButtonState.Pressed)
            {
                // Поворот головы вверх с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookUp(gameTime);
            }

            if (gamePadState.DPad.Down == ButtonState.Pressed)
            {
                // Поворот головы вниз с фиксацией. Угол поворота определяется значением gameTime.
                this.lookHelper.FixedLookDown(gameTime);
            }

            // Скорости двигателей и углы сервоприводов головы определяются и устанавливаются с заданной периодичностью.
            DateTime nowTime = DateTime.Now;
            TimeSpan timePassed = nowTime - lastTimeCommandSent;
            if (timePassed >= Settings.minCommandInterval)
            {
                this.driveHelper.RotationModeOn = gamePadState.Buttons.LeftShoulder == ButtonState.Pressed;
                this.driveHelper.Drive(gamePadState.ThumbSticks.Left.X, gamePadState.ThumbSticks.Left.Y);

                this.lookHelper.Look(gamePadState.ThumbSticks.Right.X, gamePadState.ThumbSticks.Right.Y);

                lastTimeCommandSent = nowTime;
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
            spriteBatch.Begin();

            Color color;
            string motorCommand;

            motorCommand = this.driveHelper.LastLeftMotorCommand;
            color = (motorCommand.Length > 1) && (motorCommand[1] == 'F') ? Color.White : Color.Orange;
            spriteBatch.DrawString(debugFont, motorCommand, DebugStringPosition1, color);

            motorCommand = this.driveHelper.LastRightMotorCommand;
            color = (motorCommand.Length > 1) && (motorCommand[1] == 'F') ? Color.White : Color.Orange;
            spriteBatch.DrawString(debugFont, motorCommand, DebugStringPosition2, color);

            if (this.driveHelper.TurboModeOn)
            {
                spriteBatch.DrawString(debugFont, "Турбо режим", DebugStringPosition1_1, Color.Orange);
            }

            if (this.driveHelper.RotationModeOn)
            {
                spriteBatch.DrawString(debugFont, "Режим разворота", DebugStringPosition2_1, Color.Orange);
            }

            if (this.lookHelper.SlowModeOn)
            {
                spriteBatch.DrawString(debugFont, "Плавный обзор", DebugStringPosition3_1, Color.Orange);
            }

            if (this.lookHelper.WarModeOn)
            {
                spriteBatch.DrawString(debugFont, "Боевой настрой", DebugStringPosition4_1, Color.Orange);
            }

            spriteBatch.DrawString(debugFont, this.lookHelper.LastHorizontalServoCommand, DebugStringPosition3, Color.White);
            spriteBatch.DrawString(debugFont, this.lookHelper.LastVerticalServoCommand, DebugStringPosition4, Color.White);

            color = this.flashlightHelper.FlashlightTurnedOn ? Color.Yellow : Color.White;
            spriteBatch.DrawString(debugFont, this.flashlightHelper.LastFlashlightCommand, DebugStringPosition5, color);

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
            spriteBatch.DrawString(debugFont, moodText, DebugStringPosition6, Color.White);

            spriteBatch.DrawString(debugFont, this.PercentToText(this.gunHelper.GetChargePercent()), DebugStringPosition7, Color.White);

            spriteBatch.DrawString(debugFont, this.robotHelper.LastErrorMessage, DebugStringPosition8, Color.Orange);

            spriteBatch.End();
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
            this.robotHelper.Connect();
            this.driveHelper.Stop();
            this.lookHelper.LookForward();
            this.flashlightHelper.TurnOff();
        }
        
        /// <summary>
        /// Деинициализация робота.
        /// </summary>
        private void FinalizeRobot()
        {
            this.driveHelper.Stop();
            this.lookHelper.LookForward();
            this.flashlightHelper.TurnOff();
        }
    }
}
