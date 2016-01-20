using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Card
{
    public interface ICard
    {

        string Test();

        string Name { get; }

        //读卡
        int ReadGasCard(
            Int16 com,          //串口号，从0开始
            Int32 baud,         //波特率
            ref string kh,      //卡号，根据卡号及厂家取档案
            ref Int32 ql,       //气量，卡上有气不能购气
            ref decimal money,  //金额，按金额计算的卡，卡上有金额不能购气
            ref Int16 cs,       //购气次数，以卡上购气次数为准。如果读不出，返回-1。返回-1后以数据库里为准
            ref Int16 bkcs,   //补卡次数，以卡上补卡次数为准。如果读不出，返回-1。返回-1后以数据库里为准
            ref string dqdm      //地区代码
            );

        //写新卡
        int WriteNewCard(
            Int16 com,          //串口号，从0开始
            Int32 baud,         //波特率
            ref string kmm,     //卡密码，写卡后返回新密码
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
            string sxbj        //生效标记，0不生效，1生效，价格管理中取
            
            );

        //写购气卡
        int WriteGasCard(
            Int16 com,          //串口号，从0开始
            Int32 baud,         //波特率
            ref string kmm,     //卡密码，写卡后返回新密码
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
            string sxbj       //生效标记，0不生效，1生效，价格管理中取

            );

        //清卡函数
        int FormatGasCard(
            Int16 com,          //串口号，从0开始
            Int32 baud,         //波特率
            string kmm,         //卡密码，写卡后返回新密码
            string kh,          //卡号
            string dqdm         //地区代码，从气表管理里取
            );

        //判卡函数
        int CheckGasCard(

            Int16 com,          //串口号，从0开始
            Int32 baud          //波特率
            );
    }
}
