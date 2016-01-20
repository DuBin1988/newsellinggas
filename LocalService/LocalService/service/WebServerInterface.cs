using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.IO;
using System.ServiceModel.Web;


namespace service
{
    [ServiceContract]
    interface WebServerInterface
    {
        [OperationContract, WebGet(ResponseFormat = WebMessageFormat.Json)]
        string Test(string name);

        [OperationContract, WebGet(ResponseFormat = WebMessageFormat.Json)]
        //读卡
        CardInfo ReadCard();

        [OperationContract, WebGet(ResponseFormat = WebMessageFormat.Json)]
        //写新卡
        WriteRet WriteNewCard(
            string factory,     //厂家代码
            string kmm,     //卡密码，写卡后返回新密码
            Int16 kzt,          //卡状态，0开户卡，1用户卡
            string kh,          //卡号
            string dqdm,        //地区代码，从气表管理里取
            string yhh,         //用户号，档案中自己输入
            string tm,          //条码，传用户档案里的条码
            Int32 ql,           //气量
            Int32 csql,         //上次购气量，有些表需要传
            Int32 ccsql,        //上上次购气量，有些表需要传
            Int16 cs,           //购气次数
            Int32 ljgql,        //当前表累计购气量
            Int16 bkcs,         //补卡次数，用户档案里保存补卡次数
            Int32 ljyql,        //累计用气量，有些表要累加原来用气量
            Int32 bjql,         //报警气量
            Int32 czsx,         //充值上限，可以在气表管理中设置
            Int32 tzed,         //透支额度，可以在气表管理中设置
            string sqrq,        //售气日期，格式为YYYYMMDD
            string cssqrq,      //上次售气日期，格式为YYYYMMDD
            Int32 oldprice,     //旧单价，价格管理中取
            Int32 newprice,     //新单价，价格管理中取
            string sxrq,        //生效日期，价格管理中取
            string sxbj         //生效标记，0不生效，1生效，价格管理中取
           
            );

        [OperationContract, WebGet(ResponseFormat = WebMessageFormat.Json)]
        //写购气卡
        WriteRet WriteGasCard(
            string factory,     //厂家
            string kmm,     //卡密码，写卡后返回新密码
            string kh,          //卡号
            string dqdm,        //地区代码，从气表管理里取
            Int32 ql,           //气量
            Int32 csql,         //上次购气量，有些表需要传
            Int32 ccsql,        //上上次购气量，有些表需要传
            Int16 cs,           //购气次数
            Int32 ljgql,        //当前表累计购气量
            Int32 bjql,         //报警气量
            Int32 czsx,         //充值上限，可以在气表管理中设置
            Int32 tzed,         //透支额度，可以在气表管理中设置
            string sqrq,        //售气日期，格式为YYYYMMDD
            string cssqrq,      //上次售气日期，格式为YYYYMMDD
            Int32 oldprice,     //旧单价，价格管理中取
            Int32 newprice,     //新单价，价格管理中取
            string sxrq,        //生效日期，价格管理中取
            string sxbj         //生效标记，0不生效，1生效，价格管理中取
            );

        [OperationContract, WebGet(ResponseFormat = WebMessageFormat.Json)]
        //清卡函数
        Ret FormatGasCard(
            string factory,     //厂家
            string kmm,         //卡密码，写卡后返回新密码
            string kh,          //卡号
            string dqdm         //地区代码，从气表管理里取
            );
    }

    //服务返回结果
    public class Ret
    {
        //错误信息
        private string err;
        public string Err
        {
            get { return err; }
            set { err = value; }
        }

        //异常信息
        private string exception;
        public string Exception
        {
            get { return exception; }
            set { exception = value; }
        }
    }

    //写卡返回结果，卡密码要返回
    public class WriteRet : Ret
    {
        //卡密码
        private string kmm;
        public string Kmm
        {
            get { return kmm; }
            set { kmm = value; }
        }
    }

    //读卡返回结果
    public class CardInfo : Ret
    {
        //厂家
        private string factory;
        public string Factory
        {
            get { return factory; }
            set { factory = value; }
        }
        //地区代码
        private string dqdm;
        public string Dqdm
        {
            get { return dqdm; }
            set { dqdm = value; }
        }
        //卡号
        private string cardID;
        public string CardID
        {
            get { return cardID; }
            set { cardID = value; }
        }

        //气量
        private Int32 gas;
        public Int32 Gas
        {
            get { return gas; }
            set { gas = value; }
        }

        //金额
        private decimal money;
        public decimal Money
        {
            get { return money; }
            set { money = value; }
        }

        //次数
        private Int16 times;
        public Int16 Times
        {
            get { return times; }
            set { times = value; }
        }

        //补卡次数
        private Int16 renewTimes;
        public Int16 RenewTimes
        {
            get { return renewTimes; }
            set { renewTimes = value; }
        }
    }
}
