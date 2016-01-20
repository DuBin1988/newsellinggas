using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Data;

namespace voice_card.converter
{
    class TypeConverter : IValueConverter
    {
        #region IValueConverter 成员

        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            if ((ushort)value == 0)
            {
                return "内 线";
            }
            else if ((ushort)value == 1)
            {
                return "外 线";
            }
            else if ((ushort)value == 2)
            {
                return "悬 空";
            }
            throw new NotImplementedException();
        }

        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}
