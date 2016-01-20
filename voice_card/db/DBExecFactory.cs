using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace voice_card.db
{
    class DBExecFactory
    {
        private DBExecFactory()
        {
        }

        private static DBExecFactory self = new DBExecFactory();
        public static DBExecFactory GetInstance()
        {
            return self;
        }

 
        private XmlElement configRoot;
        public XmlElement ConfigRoot
        {
            set { configRoot = value; }
            get { return configRoot; }
        }

        public void Load(XmlElement configRoot)
        {
            this.ConfigRoot = configRoot;
         
         }

        public DBExecute getExecuteor()
        {
           string execClass = this.ConfigRoot.GetAttribute("classname");
           Type type = Type.GetType(execClass);
           DBExecute exec =(DBExecute) Activator.CreateInstance(type);
           exec.SetConnInfo(this.ConfigRoot);
           return exec;
        }
    }
}
