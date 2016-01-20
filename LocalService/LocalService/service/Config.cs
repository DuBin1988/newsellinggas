using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace service
{
    //在card.ini里设置各种配置，从该类里获得这些配置内容
    public class Config
    {
        //所有配置
        private static Dictionary<string, string> configs = null;

        //获取某项配置
        public static string GetConfig(string name)
        {
            if (configs == null)
            {
                configs = new Dictionary<string, string>();
                //从文件里获取所有配置
                StreamReader sr = new StreamReader("card.ini");
                string s;
                while ((s = sr.ReadLine()) != null)
                {
                    string[] str = s.Split('=');
                    configs[str[0]] = str[1];
                }
            }
            string ret = configs[name];
            return ret;
        }
    }
}
