using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Data;

namespace voice_card.converter
{
    class StateConverter : IValueConverter
    {
        #region IValueConverter 成员

        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            if ((int)value == 0)
            {
                return "空 闲";
            }
            else if ((int)value == 1)
            {
                return "等待拨号";
            }
            else if ((int)value == 2)
            {
                return "拨号1";
            }
            else if ((int)value == 3)
            {
                return "拨号2";
            }
            else if ((int)value == 4)
            {
                return "查找内线";
            }
            else if ((int)value == 5)
            {
                return "等待连通";
            }
            else if ((int)value == 6)
            {
                return "正在振铃";
            }
            else if ((int)value == 7)
            {
                return "拨号9";
            }
            else if ((int)value == 8)
            {
                return "接 通";
            }
            else if ((int)value == 9)
            {
                return "等待挂机";
            }
            else if ((int)value == 10)
            {
                return "摘 机";
            }
            else if ((int)value == 11)
            {
                return "忙 碌";
            }
            else if ((int)value == 12)
            {
                return "收主叫";
            }
            else if ((int)value == 13)
            {
                return "操作员正在记录";
            }
            else if ((int)value == 14)
            {
                return "未登陆";
            }
            else if ((int)value == 100)
            {
                return "正在拨号";
            }
            else if ((int)value == 250)
            {
                return "等待接听";
            }
            else if ((int)value == 114)
            {
                return "播放工号";
            }
            else if ((int)value == 115)
            {
                return "播放坐席忙录音";
            }
            else if ((int)value == 116)
            {
                return "播放欢迎语音";
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
