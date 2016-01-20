using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.Net;
using System.ServiceModel.Activation;
using System.IO;
using System.ServiceModel.Web;
using System.Collections;
using Com.Aote.Logs;
using System.Xaml;
using System.Windows;
using Card;
using System.ComponentModel;

namespace service
{
    //服务类
    //串行化声明，指定一次接受一个请求处理
    // InstanceContextMode.Single 所有客户端代理都使用服务端同一个静态服务实例对象为其服务
    // ConcurrencyMode.Single  某时刻只处理一个客户端请求，其他请求放入请求队列中等待处理。如果多个客户端对服务并发访问，这些服务最终在服务端也是通过串行化进行处理
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single, ConcurrencyMode = ConcurrencyMode.Single)]
    class WorkService : WebServerInterface
    {
        private static Log Log = Log.GetInstance("service.WorkService");

        //加载的厂家动态库
        private static CardInfos Cards;

        //端口号
        private static short Port;

        //波特率
        private static int Baud;

        //服务主机
        private ServiceHost host;

        #region 错误状态表
        //错误状态表
        private string[] Errors = 
        {
            "不是注册用户,请注册", //-1
            "端口初始化失败", //-2
            "读设备状态失败", //-3
            "无卡",          //-4
            "读卡密码次数失败", //-5
            "该卡已经损坏",     //-6
            "读卡错误",         //-7
            "该卡不是用户卡",  //-8
            "核对密码错误",   //-9
            "写卡失败",         //-10
            "备份气量不正确",  //-11
            "关闭通讯端口失败", //-12
            "该卡可能是新卡",   //-13
            "该卡非本系统卡",      //-14
            "该卡不是新卡",       //-15
            "用户卡号（地区代码）与卡内的值不匹配",   //-16
            "清卡失败",         //-17
            "气量超限",          //-18
            "卡插反"            //-19
        };
        #endregion

        #region WebServer服务
        public void StartServer()
        {
            string hostName = Dns.GetHostName();
            IPAddress[] ipes = Dns.GetHostAddresses(hostName);
            IPAddress addres = ipes[0];
            string baseAddress = "http://127.0.0.1:8000/";
            host = new ServiceHost(typeof(WorkService), new Uri(baseAddress));
            host.AddServiceEndpoint(typeof(WebServerInterface), new WebHttpBinding(), "").Behaviors.Add(new WebHttpBehavior());
            host.Open();
            //在后台加载相关动态库
            BackgroundWorker worker = new BackgroundWorker();
            worker.DoWork += new DoWorkEventHandler(worker_DoWork);
            worker.RunWorkerAsync();
            Log.Debug("StartServer");
        }

        void worker_DoWork(object sender, DoWorkEventArgs e)
        {
            Log.Debug("in do work");
            try
            {
                //获取本地端口号，波特率
                Port = short.Parse(Config.GetConfig("Port"));
                Baud = int.Parse(Config.GetConfig("Baud"));                   
                Log.Debug("Port,Baud" + Port + Baud);
                //获取后台ip地址
                string ip = Config.GetConfig("RemoteIP");
                //从后台获取所有文件信息
                WebClient client = new WebClient();
                string str = client.DownloadString(ip + "/rs/card");
                Log.Debug("download str" + str);
                string[] infos = str.Split('|');
                //对于每一个文件，检查时间是否一致，不一致则到后台获取文件
                foreach (string item in infos)
                {
                    string[] items = item.Split(',');
                    string fpath = items[0];
                    Log.Debug("find fpath is " + fpath);
                    long ftime = long.Parse(items[1]);
                    DateTime from1970 = new DateTime(1970, 1, 1, 8, 0, 0, DateTimeKind.Local);
                    DateTime time = from1970.AddMilliseconds(ftime);
                    //如果与本地文件时间不同，去后台获取文件
                    int index = fpath.LastIndexOf('\\');
                    string fname = fpath.Substring(index + 1);
                    Log.Debug("find fname is " + fname);
                    DateTime local = File.GetLastWriteTime(fname);
                    Log.Debug("time = " + time.ToString());
                    Log.Debug("local = " + local.ToString());
                    if (local != time)
                    {
                        Log.Debug("load file " + fname);
                        client.DownloadFile(ip + "/rs/card/" + fpath.Replace("\\", "^"), fname);
                        File.SetLastWriteTime(fname, time);
                    }
                }
                //加载xaml文件
                StreamReader reader = new StreamReader("Card.xaml");
                string s = reader.ReadToEnd();
                ResourceDictionary dic = (ResourceDictionary)XamlServices.Parse(s);
                Cards = (CardInfos)dic["CardInfos"];
                Log.Debug(Cards.Count + "");
            }
            catch (Exception ex)
            {
                Log.Debug(ex.Message);
            }
        }

        public void CloseServer()
        {
            if (host.State == CommunicationState.Opened)
            {
                host.Close();
            }
            //Log.Debug("CloseServer");
        }

        private ICard GetCard(string name)
        {
            //未加载，先加载
            ICard card = Cards.GetCardInfo(name).Card;
            if (card == null)
            {
                throw new Exception("not found config: " + name);
            }
            return card;
        }
        #endregion

        public string Test(string name)
        {
            return host == null ? "null" : "no";
        }

        #region 卡操作
        //写新卡
        public WriteRet WriteNewCard(
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
            string sxbj        //生效标记，0不生效，1生效，价格管理中取
            
            )
        {
            Log.Debug("发卡或者补卡 start" + "cardid-" + kh + "sqrq-" + sqrq + "czsx-" + czsx + "bjql-" + bjql + "gmje-" + ql + "buyprice-" + oldprice);
            WriteRet ret = new WriteRet();
            try
            {
                ICard card = GetCard(factory);
                int r = card.WriteNewCard(Port, Baud, ref kmm, kzt, kh, dqdm, yhh, tm, ql, csql, ccsql, cs, ljgql, bkcs,
                    ljyql, bjql, czsx, tzed, sqrq, cssqrq, oldprice, newprice, sxrq, sxbj);
                if (r < 0)
                {
                    ret.Err = FindErrorInfo(r);
                }
                else
                {
                    ret.Kmm = kmm;
                }
                return ret;
            }
            catch (Exception e)
            {
                Log.Debug("发卡 exception: " + e.Message);
                ret.Exception = e.Message;
                return ret;
            }
        }

        //写购气卡
        public WriteRet WriteGasCard(
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
            )
        {
            Log.Debug("SellGas start");
            WriteRet ret = new WriteRet();
            try
            {
                ICard card = GetCard(factory);
                int r = card.WriteGasCard(Port, Baud, ref kmm, kh, dqdm, ql, csql, ccsql, cs, ljgql, bjql, czsx,
                    tzed, sqrq, cssqrq, oldprice, newprice, sxrq, sxbj);
                Log.Debug("WriteGasCard  r " + r);
                if (r < 0)
                {
                    ret.Err = FindErrorInfo(r);
                }
                else
                {
                    ret.Kmm = kmm;
                }
                Log.Debug("WriteGasCard  ret " + ret);
                return ret;
            }
            catch (Exception e)
            {
                Log.Debug("SellGas exception: " + e.Message);
                ret.Exception = e.Message;
                return ret;
            }
        }

        //读卡
        public CardInfo ReadCard()
        {
            Log.Debug("ReadCard start");
            CardInfo ret = new CardInfo();
            try
            {
                //检查卡的初始状态
                int result = MingHua.CheckCard(Port, Baud);
                Log.Debug("check card result" + result);
                //有错误，显示错误内容，不是新卡不当做错误
                if (result != 0 && result != -15)
                {
                    //获取错误代码
                    ret.Err = FindErrorInfo(result);
                    return ret;
                }

                if (Cards == null)
                {
                    throw new Exception("Cards is null");
                }
                //循环调用所有厂家的
                foreach (CardConfig info in Cards)
                {
                    ICard card = info.Card;
                    //如果不是本厂家的，看下一个
                    int r = card.CheckGasCard(Port, Baud);
                    Log.Debug("check " + info.Name + " is " + r);
                    if (r != 0)
                    {
                        continue;
                    }
                    //读卡
                    string kh = "";
                    Int32 ql = 0;
                    decimal money = 0;
                    Int16 cs = 0;
                    Int16 bkcs = 0;
                    string dqdm = "";
                    r = card.ReadGasCard(Port, Baud, ref kh, ref ql, ref money, ref cs, ref bkcs, ref dqdm);
                    if (r < 0)
                    {
                        //获取错误代码
                        ret.Err = FindErrorInfo(r);
                    }
                    else
                    {
                        //返回读取结果
                        ret.Factory = info.Name;
                        ret.CardID = kh;
                        ret.Gas = ql;
                        ret.Money = money;
                        ret.Times = cs;
                        ret.RenewTimes = bkcs;
                        ret.Dqdm = dqdm;
                        Log.Debug("WorkService中地区代码 " + dqdm + " is " + ret.Dqdm);
                    }
                    return ret;
                }
                //一个都没有找到
                ret.Err = "未知厂家";
                return ret;
            }
            catch (Exception e)
            {
                Log.Debug("ReadCard exception");
                ret.Exception = e.Message;
                return ret;
            }
        }

        //格式化卡
        public Ret FormatGasCard(
            string factory,     //厂家
            string kmm,         //卡密码，写卡后返回新密码
            string kh,          //卡号
            string dqdm         //地区代码，从气表管理里取
            )
        {
            Ret ret = new Ret();
            try
            {
                ICard card = GetCard(factory);
                int r = card.FormatGasCard(Port, Baud, kmm, kh, dqdm);
                if (r < 0)
                {
                    ret.Err = FindErrorInfo(r);
                }
                return ret;
            }
            catch (Exception e)
            {
                Log.Debug("FormatCard exception");
                ret.Exception = e.Message;
                return ret;
            }
        }

        #endregion

        #region 查找错误代码，如果未找到,返回错误代码值
        private string FindErrorInfo(int err)
        {
            try
            {
                return Errors[-err - 1];
            }
            catch(Exception e)
            {
                return err + "";
            }
         }
        #endregion

        #region 跨域访问
        private Stream StringToStream(string result)
        {
            WebOperationContext.Current.OutgoingResponse.ContentType = "application/xml";
            return new MemoryStream(Encoding.UTF8.GetBytes(result));
        }

        public Stream GetFlashPolicy()
        {
            string result = @"<?xml version=""1.0""?>
                              <cross-domain-policy>
                                  <allow-access-from domain=""*""/>
                              </cross-domain-policy> ";
            return StringToStream(result);
        }

        public Stream GetSilverlightPolicy()
        {
            string result = @"<?xml version=""1.0"" encoding=""utf-8""?>
                            <access-policy>
                                <cross-domain-access>
                                    <policy>
                                        <allow-from>
                                            <domain uri=""*""/>
                                        </allow-from>
                                        <grant-to>
                                            <resource path=""/"" include-subpaths=""true""/>
                                        </grant-to>
                                    </policy>
                                </cross-domain-access>
                            </access-policy>";
            return StringToStream(result);
        }
        #endregion
    }
}
