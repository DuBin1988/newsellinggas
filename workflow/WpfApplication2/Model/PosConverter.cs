using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;

namespace Workflow.Model
{
    /// <summary>
    /// 把空串转换成空值，文本框输入时，把空串按空值对待
    /// </summary>
    public class PosConverter : IValueConverter
    {
        /// <summary>
        /// 正向值不变
        /// </summary>
        /// <param name="value">转换的值</param>
        /// <param name="targetType">值的类型</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">未知</param>
        /// <returns>原来的值，不变</returns>
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            Thickness thickness = new Thickness((double)value, 0, 0, 0);
            return thickness;
        }

        /// <summary>
        /// 反向把空串转换成空
        /// </summary>
        /// <param name="value">值</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">未知</param>
        /// <returns>空串转换成空，有需要过滤的字符，过滤掉</returns>
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            //正向值不变
            return value;
        }
    }
}

