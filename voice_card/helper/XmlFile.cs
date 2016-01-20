using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;

namespace voice_card.helper
{
    class XmlFile
    {

        private XElement root = null;
        /// <summary>
        /// xml文件名，加载xml文件
        /// </summary>
        /// <param name="uri"></param>
        public XmlFile(string uri)
        {
            root = XElement.Load(System.Xml.XmlReader.Create( uri));
        }

        /// <summary>
        /// 根据父节点名，子节点名，子节点属性，找到属性值，否则返回空
        /// </summary>
        /// <param name="parentTagName"></param>
        /// <param name="childTagName"></param>
        /// <param name="propertyName"></param>
        /// <returns></returns>
        public string getProperty(string parentTagName, string childTagName, string propertyName)
        {
            XElement xml = root.Element(parentTagName);
            string result = "";
            var req = from node in xml.Elements(childTagName)
                      select node;
            List<XElement> list = new List<XElement>();
            list.AddRange(req);
            if (list.Count > 0)
            {
                //返回找到的第一个节点的属性值
                XElement xele = list[0];
                result = xele.Attribute(propertyName).Value;
            }
            return result;
        }



        public string getProperty(string tagName, string propertyName)
        {
            XElement xml = root.Element(tagName);
            return xml.Attribute(propertyName).Value;
        }





        /// <summary>
        /// 根据父节点名，子节点名找到子节点，否则返回null
        /// </summary>
        /// <param name="parentName"></param>
        /// <param name="childName"></param>
        /// <returns></returns>
        public XElement getElement(string parentName, string childName)
        {
            XElement result = null;
            XElement xml = root.Element(parentName);
            var req = from node in xml.Elements(childName)
                      select node;
            List<XElement> list = new List<XElement>();
            list.AddRange(req);
            if (list.Count > 0)
            {
                //返回找到的第一个节点
                result = list[0];
            }
            return result;
        }


        ///<summary>
        ///找到子节点
        public List<XElement> getElementes(string parentName, string childName)
        {
            XElement xml = root.Element(parentName);
            var req = from node in xml.Elements(childName)
                      select node;
            List<XElement> result = new List<XElement>();
            result.AddRange(req);
            return result;
        }
    }
}
