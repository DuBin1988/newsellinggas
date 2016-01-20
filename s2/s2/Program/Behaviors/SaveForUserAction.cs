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
using System.Collections;
using Com.Aote.ObjectTools;

namespace Com.Aote.Behaviors
{
    public class SaveForUserAction : BaseAsyncAction
    {
        public string ListName { get; set; }


        public string ReturnName { get; set; }


        public string UserName { get; set; }

        //需要保存的列表
        public GeneralObject SaveObj { get; set; }

        public override void Invoke()
        {
            char[] c = new char[] { ';' };
            string[] str = ReturnName.Split(c);
            BaseObjectList ol = (BaseObjectList)SaveObj.GetPropertyValue(ListName);
            foreach (GeneralObject item in ol)
            {
                for (int i = 0; i < str.Length; i++)
                {
                    string value = str[i];
                    string[] split = new string[] { "=>" };
                    string[] objs = value.Split(split, StringSplitOptions.RemoveEmptyEntries);
                    //获得稽查结果
                object result = item.GetPropertyValue(objs[0]);
                if (result == null) continue;
                //获得用户档案
                GeneralObject user = (GeneralObject)item.GetPropertyValue(UserName);
                //给用户档案设置稽查结果
                user.SetPropertyValue(objs[1], result, true);
                }
            }
            SaveObj.Save();
        }
    }
}
