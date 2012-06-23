// --------------------------------------------------------------------------------------------------------------------
// <copyright file="MessageHelper.cs" company="Dzakhov's jag">
//   Copyright © Dmitry Dzakhov 2011
// </copyright>
// <summary>
//   Вспомогательный класс, предназначенный для формирования сообщений, передаваемых роботу.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace RobotGamepad
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Вспомогательный класс, предназначенный для формирования сообщений, передаваемых роботу.
    /// </summary>
    public static class MessageHelper
    {
        /// <summary>
        /// Преобразование числового значения в строковое представление параметра сообщения.
        /// </summary>
        /// <param name="value">Преобразуемое числовое значение. Допустимые значения от -32 768 до 32 767.</param>
        /// <returns>Строка из четырёх шестнадцатиричных цифр. При необходимо левая часть дополняется символами '0' до достижения дляины строки в четыре символа.</returns>
        public static string IntToMessageValue(int value)
        {
            if ((value < -32768) || (value > 32767))
            {
                throw new ArgumentException("Параметр сообщения должен находиться в интервале от -32 768 до 32 767.");
            }

            Int16 shortValue = Convert.ToInt16(value);
            return shortValue.ToString("X4");

            //string result = value.ToString();
            //while (result.Length < 3)
            //{
            //    result = "0" + result;
            //}

            //return result;
        }
    }
}
