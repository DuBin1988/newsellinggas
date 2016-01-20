using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

//数据库连接信息
namespace voice_card.db
{
    class ConnInfo
    {
        private Dictionary<string, string> attrs = new Dictionary<string, string>();
        public Dictionary<string, string> Attrs
        {
            get { return attrs; }
            set { attrs = value; }
        }


        private ConnInfo()
        {
        }

        public ConnInfo(XmlElement config)
        {
            foreach (XmlNode node in config.ChildNodes)
            {
                string key = node.Name;
                string value = node.InnerText;
                attrs.Add(key, value);
            }
         }

        

    }
}
