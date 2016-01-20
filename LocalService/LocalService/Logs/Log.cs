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
    public delegate void ShowMessage(string message);

    public class Log
    {
        //实例缓存表
        private static Dictionary<string, Log> logs = new Dictionary<string, Log>();
        //获取实例
        public static Log GetInstance(string name)
        {
            if (logs.ContainsKey(name))
            {
                return logs[name];
            }
            //没有，添加新的
            Log log = new Log();
            logs.Add(name, log);
            //获取log的级别等其余配置
            LogConfig lc = GetConfig(name);
            if (lc != null)
            {
                log.name = name;
                log.level = lc.Level;
                log.appender = lc.Appender;
            }
            return log;
        }

        //根据log名获取log配置
        private static LogConfig GetConfig(string name)
        {
            LogConfigs lcs = (LogConfigs)Application.Current.Resources["LogConfigs"];
            if (lcs == null)
            {
                return null;
            }
            foreach (LogConfig lc in lcs)
            {
                if (name.IndexOf(lc.Name) == 0)
                {
                    //返回配置
                    return lc;
                }
            }
            return null;
        }

        //级别排序
        private static string[] Levels = { "Info", "Debug", "Warn", "Error" };
        //获取级别
        private static int GetLevel(string levelName)
        {
            for (int i = 0; i < Levels.Length; i++)
            {
                if (Levels[i].Equals(levelName))
                {
                    return i;
                }
            }
            return -1;
        }

        //显示数据的代理
        public static ShowMessage Logger;


        //日志名称
        private string name;

        //日志级别
        private string level;

        //日志显示器
        private IAppender appender;

        //获得显示字符串
        private string GetShowMessage(string message)
        {
            //格式固定为时间，名称，内容
            string result = DateTime.Now.ToString("yyyy-mm-dd hh:mm:tt:ss");
            result += " " + name + " " + message;
            return result;
        }

        //打印给定级别信息
        public void ShowMessage(string message, string level)
        {
            //如果级别满足要求，输出
            if (this.appender != null && GetLevel(this.level) <= GetLevel(level))
            {
                message = GetShowMessage(message);
                appender.ShowMessage(message);
            }
        }
        public void Info(string message)
        {
            ShowMessage(message, "Info");
        }
        public void Debug(string message)
        {
            ShowMessage(message, "Debug");
        }
        public void Warn(string message)
        {
            ShowMessage(message, "Warn");
        }
        public void Error(string message)
        {
            ShowMessage(message, "Error");
        }
    }
}
