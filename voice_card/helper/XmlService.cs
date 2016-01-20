using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;

namespace voice_card.helper
{
    class XmlService
    {
        private static XmlFile xml = null;

        public static void load(string path)
        {
            xml = new XmlFile(path);
        }
        /// <summary>
        /// 根据父节点名，子节点名，子节点属性，找到属性值
        /// </summary>
        /// <param name="parentTagName"></param>
        /// <param name="childTagName"></param>
        /// <param name="propertyName"></param>
        /// <returns></returns>
        public static string getProperty(string parentTagName, string childTagName, string propertyName)
        {
            return xml.getProperty(parentTagName, childTagName, propertyName);
        }

        public static string getProperty(string tagName, string propertyName)
        {
            return xml.getProperty(tagName, propertyName);
        }

        public static XElement getElement(string parentName, string childName)
        {
            return xml.getElement(parentName, childName);
        }

        public static List<XElement> getElements(string parentName, string childName)
        {
            return xml.getElementes(parentName, childName);
        }
    }
}
