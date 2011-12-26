// --------------------------------------------------------------------------------------------------------------------
// <copyright file="Program.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Главный класс приложения.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;

#if WINDOWS || XBOX
    /// <summary>
    /// Главный класс приложения.
    /// </summary>
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            using (GameRobot game = new GameRobot())
            {
                game.Run();
            }
        }
    }
#endif
}

