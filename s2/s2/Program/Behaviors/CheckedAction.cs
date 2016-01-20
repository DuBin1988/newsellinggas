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
using Com.Aote.ObjectTools;

namespace Com.Aote.Behaviors
{
    //机表缴费，选择判断
    public class CheckedAction : BaseAsyncAction
    {
        //列表对象
        public ObjectList List { get; set; }

        public GeneralObject Item { get; set; }


        public override void Invoke()
        {
            int index = 0;
            string currentCheck = Item.GetPropertyValue("f_checked").ToString();
            string flag = "";
            foreach (GeneralObject go in List)
            {
                index++;
                //第一个必须选中
                if (index == 1)
                {
                    go.SetPropertyValue("f_checked", "True", true);
                }
                if (Item.Equals(go) && index!=1)
                {
                    //如果当前项是False，则大于此项的都为False
                    if (currentCheck == "False")
                    {
                        flag = "False";
                    }
                    else if (currentCheck == "True")
                    {
                        //如果当前项是True，则小于此项的都为True
                        flag = "True";
                        break;
                    }
                }
                if (flag == "False")
                {
                    go.SetPropertyValue("f_checked", flag, true);
                }
            }
            if (flag == "True")
            {
                for (int i = 0; i < index; i++)
                {
                    GeneralObject go = (GeneralObject)List[i];
                    go.SetPropertyValue("f_checked", flag, true);
                }
            }

        }
    }
}
