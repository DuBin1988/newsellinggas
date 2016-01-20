using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using voice_card.db;
using System.Xml;
using System.Collections;

namespace voice_card.helper
{
    class DBHelper
    {

        public static void DBFactoryInit()
        {
             String currentDir = Environment.CurrentDirectory;
             String ConfigFile =System.IO.Path.Combine(currentDir, "dbconfig.xml");
             System.Xml.XmlDocument xml = new System.Xml.XmlDocument();
             xml.Load(ConfigFile);
             XmlElement root = xml.DocumentElement;
             DBExecFactory.GetInstance().Load(root);
        }


        public static void executeNonQuery(string sql)
        {
            DBExecute exec = DBExecFactory.GetInstance().getExecuteor();
            exec.Execute(sql);
        }

        public static ArrayList executeQuery(string sql)
        {
            DBExecute exec = DBExecFactory.GetInstance().getExecuteor();
            return exec.Query(sql);
        }
        
    }
}
