// --------------------------------------------------------------------------------------------------------------------
// <copyright file="CommandHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для формирования команд, передаваемых роботу.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Вспомогательный класс, предназначенный для формирования команд, передаваемых роботу.
    /// </summary>
    public static class CommandHelper
    {
        /// <summary>
        /// Преобразование числового значения в строковое представление параметра команды.
        /// </summary>
        /// <param name="value">Преобразуемое числовое значение. Допустимые значения от 0 до 999.</param>
        /// <returns>Строка из трёх цифр. При необходимо левая часть дополняется символами '0' до достижения дляины строки в три символа.</returns>
        public static string IntToCommandValue(int value)
        {
            if ((value < 0) || (value > 999))
            {
                throw new ArgumentException("Параметр команды должен находиться в интервале [0, 999].");
            }

            string result = value.ToString();
            while (result.Length < 3)
            {
                result = "0" + result;
            }

            return result;
        }
    }
}
