using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Card;
using System.Windows.Markup;

namespace service
{
    //所有卡的相关信息
    public class CardInfos : List<CardConfig>
    {
        public CardConfig GetCardInfo(string name)
        {
            foreach (CardConfig info in this)
            {
                if (info.Name == name)
                {
                    return info;
                }
            }
            return null;
        }
    }

    //代表一个卡的相关信息
    public class CardConfig
    {
        //厂家别名
        public string Name { get; set; }

        //对应的卡处理对象
        public ICard Card { get; set; }
    }
}
