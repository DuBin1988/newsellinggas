using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Com.Aote.Logs
{
    public class Log
    {
        private static Dictionary<Type, ILog> logs = new Dictionary<Type, ILog>();
        private ILog instance = LogManager.GetLogger(typeof(Log));

        public static Log GetInstance(Type type)
        {
            ILog log;
            if (!logs.ContainsKey(type))
            {
                log = LogManager.GetLogger(type);
                logs.Add(type, log);
            }
            else
            {
                log = logs[type];
                logs.Add(type, log);;
            }
            Log aLog = new Log();
            aLog.instance = log;
            return aLog;
        }

        public static Log GetInstance(String type)
        {
            Type t = Type.GetType(type);
            if (t == null)
                t = Type.GetType(type + ",Activator");

            return GetInstance(t);
        }
        

        public void Info(string message)
        {
            instance.Info(message);
        }

        public void Debug(string message)
        {
            instance.Debug(message);
        }

        public void Warn(string message)
        {
            instance.Warn(message);
        }

        public void Error(string message)
        {
            instance.Error(message);
        }
    }
}
