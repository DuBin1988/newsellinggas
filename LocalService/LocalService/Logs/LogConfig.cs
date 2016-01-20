using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Collections.Generic;

namespace Com.Aote.Logs
{
    public class LogConfigs : List<LogConfig>
    {
    }
    
    public class LogConfig
    {
        //日志名
        public string Name { get; set; }

        //日志级别
        public string Level { get; set; }

        //日志显示器
        public IAppender Appender { get; set; }
    }
}
