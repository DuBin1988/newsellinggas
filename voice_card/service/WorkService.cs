using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using voice_card.entity;
using System.Collections.ObjectModel;
using System.ServiceModel;
using System.ServiceModel.Description;
using log4net;
using System.Net;
using System.ServiceModel.Activation;
using System.IO;
using voice_card.helper;
using System.ServiceModel.Web;
using System.Windows.Forms;
using System.Collections;
using System.Xml.Linq;

namespace voice_card.service
{
    //呼叫中心服务类
    class WorkService : WebServerInterface
    {
        private static ILog log = LogManager.GetLogger(typeof(WorkService));

        //保存通道连接
        public static ObservableCollection<LineInfo> Lines = new ObservableCollection<LineInfo>();

        //话路分配接口
        private TelAssign ta = null;

        //服务主机
        private ServiceHost host;

        //通道配置文件


    
        //加载驱动,obj是为外部显示集合，加载时会在lines和objs中都保存通道
        public void LoadDriver(ObservableCollection<LineInfo> objs)
        {
            log.Debug("加载语音卡驱动");
            //初始化驱动
            if (InvokeVcDll.LoadDRV() != 0)
            {
                log.Warn("驱动加载失败");
                return;
            }
            //设置每条通道初始状态
            InitLines(objs);
            //初始化信号音检测
            InvokeVcDll.Sig_Init(0);
            //本地保存引用
            Lines = objs;
            log.Debug("语音卡驱动加载成功");
            log.Debug("加载语音分配策略");
            LoadAssign();
         }




        /**
         * 初始化语音通道
         **/
        public void InitLines(ObservableCollection<LineInfo> objs)
        {
            //加载语音通道配置文件
            String currentDir = Environment.CurrentDirectory;
            String path = System.IO.Path.Combine(currentDir, "lines.xml");
            XElement lineConfig = XElement.Load(System.Xml.XmlReader.Create(path));

            ushort TotalLine = InvokeVcDll.CheckValidCh();
            for (int ch = 0; ch < TotalLine; ch++)
            {
                ushort type = InvokeVcDll.CheckChTypeNew(ch);
                LineInfo line = new LineInfo();
                line.Number = (ushort)ch;
                line.Type = type;
                line.ConnectToLine = -1;
                line.State = (int)state.CH_FREE;
                line.CallerPhone = "";
                line.RecordFile = "";
                line.LastTime = System.DateTime.Now;
                line.Comingtime = System.DateTime.Now;
                line.Rectime = System.DateTime.Now; ;
                line.Handuptime = System.DateTime.Now;
                line.Id = "";
                line.Gonghao = "";
                line.IsKey = false;
                line.Islink = "no";
                line.ListenNum = -1;
                //查找配置文件中内容，设置通道属性
                if(lineConfig !=null)
                {
                    XElement element = lineConfig.Elements().Where(e => e.Attribute("num").Value == ch+"").FirstOrDefault();
                    if(element !=null)
                    {
                        //设定通道自动接通的开始和结束小时
                        string shour = (string)element.Attribute("starthour").Value;
                        string ehour = (string)element.Attribute("endhour").Value;
                        line.StartHour =shour;
                        line.EndHour = ehour;
                     }
                 }
                objs.Add(line);
            }
            //给每条通道分配初始语音缓冲区
            if (InvokeVcDll.EnableCard(TotalLine, 1024 * 128) != (long)0)
            {
                InvokeVcDll.FreeDRV();
            }
        }
         
        

        /**
         * 加载语音分配策略
         **/
        private void LoadAssign()
        {
            string className = XmlService.getProperty("Assign", "classname");
            Type type = Type.GetType(className);
            ta = (TelAssign)Activator.CreateInstance(type);
        }

        //退出
        public void Exit()
        {
            for (int i = 0; i < Lines.Count; i++)
            {

                ResetLine((ushort)i);
            }
            InvokeVcDll.DisableCard();
            InvokeVcDll.FreeDRV();
        }


        //运行方法
        public void Run()
        {
            try
            {
                InvokeVcDll.PUSH_PLAY();
                InvokeVcDll.FeedSigFunc();
                for (int i = 0; i < Lines.Count; i++)
                {
                    LineInfo line = Lines[i];
                    this.yzDoWork(line);
                }
            }
            catch (Exception e1)
            {
                log.Debug("异常-------------" + e1.ToString());
                Console.WriteLine("异常:--------" + e1.ToString());
            }

        }

     


       

        //查找空闲内线通道,找不到内线通道返回-1
        private int GetFreeUser(LineInfo line)
        {
            int i =this.ta.Assign(Lines);
            //如果找不到通道，说明都被使用中，判断号码是否是优先接入号码
            if (i == -1)
            {
                string sql = "select * from t_teshuhaoma where phone='" + line.CallerPhone + "'";
                ArrayList result = DBHelper.executeQuery(sql);
                if (result.Count > 0)
                {
                    i = getRandom();
                    log.Debug("优先接入号码:"+line.CallerPhone +",连接随机通道:"+i);
                    LineInfo inLine = Lines[i];
                    //强行挂段电话,重置通道
                    this.ResetLine(i);
                    this.ResetLine(inLine.ConnectToLine);
                    //设置内线状态已连接，内线自动连接外线
                    inLine.State = (int)state.CH_CHECKSIG;
                }
            }
            return i;
        }
        //获得随即内线通道
        private int getRandom()
        {
            int result = -1;
            ArrayList list = new ArrayList();
            for (int i = 0; i < Lines.Count; i++)
            {
                LineInfo vline = Lines[i];
                if (vline.Type == (int)type.CHTYPE_USER && vline.State==(int)state.CH_CONNECTED)
                {
                    list.Add(vline.Number);
                }
            }
            Random r = new Random();
            int ran = r.Next(list.Count);
            object o = list[ran];
            result = Int32.Parse(o + "");
            return result;
        }


        //得到主叫
        private string getCaller(LineInfo trunk)
        {
            byte[] number = new byte[128];
            InvokeVcDll.GetCallerIDStr(trunk.Number, number);
            string result = Encoding.UTF8.GetString(number);
            result = result.TrimEnd('\0');
            if (result.Length > 8)
            {
                result = result.Substring(8);
            }
            char[] c = {'\0'};
            string[] phone = result.Split(c);
            result = phone[0];
            trunk.CallerPhone = result;
            log.Debug("收外线" + trunk.Number + "主叫号码:" + result);
            return result;
        }

        //主工作方法
        public void yzDoWork(LineInfo line)
        {
            int tmpCode;
            //通道对应的连接通道号
            int tmpCTL = line.ConnectToLine;
            //根据类型处理
            switch (line.Type)
            {
                //内线
                case (int)type.CHTYPE_USER:
                    //未摘机 ,不是空闲，不是响铃
                    if (!line.IsKey && !InvokeVcDll.OffHookDetect(line.Number) && line.State != (int)state.CH_FREE && line.State != (int)state.CH_RINGING)
                    {
                        switch (line.State)
                        {
                            case (int)state.CH_WAITONHOOK:
                                log.Debug("内线" + line.Number + "waitonhook时挂机");
                                InvokeVcDll.StopRecordFile((ushort)line.Number);
                                ResetLine(line.Number);
                                break;
                            case (int)state.CH_CHECKSIG:
                                log.Debug("内线" + line.Number + "checksig等待连接时挂机");
                                InvokeVcDll.FeedPower((ushort)tmpCTL);
                                ResetLine(line.ConnectToLine);
                                ResetLine(line.Number);
                                break;
                            case (int)state.CH_CONNECTED:
                                log.Debug("内线" + line.Number + "连接时挂断");
                                InvokeVcDll.StopRecordFile((ushort)line.Number);
                                int duan = InvokeVcDll.ClearLink(line.Number, (ushort)tmpCTL);
                                log.Debug("断开连接返回值:" + duan);
                                ResetLineInner(line.Number);
                                ResetLine(tmpCTL);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case (int)type.CHTYPE_TRUNK:
                   if (InvokeVcDll.Sig_CheckBusy(line.Number) == 1)
                    {
                        switch (line.State)
                        {
                            case (int)state.CH_DIALING:
                                log.Debug("外线" + line.Number + "dialing响铃时时挂断");
                                ResetLine(line.Number);
                                break;
                            case (int)state.CH_CHECKSIG:
                                log.Debug("外线" + line.Number + "checksig等待连接时挂断");
                                this.stopWelcome(line.Number);
                                InvokeVcDll.FeedPower((ushort)line.ConnectToLine);
                                ResetLine(line.ConnectToLine);
                                ResetLine(line.Number);
                                break;
                            case (int)state.CH_CONNECTED:
                                log.Debug("外线" + line.Number + "连接时挂断");
                                InvokeVcDll.StopRecordFile((ushort)line.ConnectToLine);
                                int duan = InvokeVcDll.ClearLink((ushort)tmpCTL, line.Number);
                                log.Debug("通话时挂断返回值:" + duan);
                                int inerline = line.ConnectToLine;
                                ResetLine(line.Number);
                                this.ResetLineInner(inerline);
                                break;
                            case (int)state.CH_GETCAllNUM:
                                int nx = line.ConnectToLine;
                                ResetLine(line.Number);
                                if (nx != -1)
                                {
                                    ResetLine(nx);
                                }
                                break;
                            //播放欢迎语音
                            case (int) state.CH_PlAYWELCOME:
                               int nx1 = line.ConnectToLine;
                                ResetLine(line.Number);
                                if (nx1 != -1)
                                {
                                    ResetLine(nx1);
                                }
                                break;
                            //播放工号
                            case (int)state.CH_PLAYGONGHAO:
                                int nx2 = line.ConnectToLine;
                                ResetLine(line.Number);
                                if (nx2 != -1)
                                {
                                    ResetLine(nx2);
                                }
                                break;
                            //外拨
                            case (int)state.CH_CALLPHONE:
                                log.Debug("内线" + line.ConnectToLine + "外拨"+line.Number+"时,外线拨号时挂断");
                                InvokeVcDll.FeedPower((ushort)line.ConnectToLine);
                                ResetLine(line.Number);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
            //根据状态处理(实际电话处理)
            switch (line.State)
            {
                //空闲状态
                case (int)state.CH_FREE:
                    //监听响铃
                    if (InvokeVcDll.RingDetect(line.Number))
                    {
                        //如果是内线,播放忙音，状态改为摘机
                        if (line.Type == (int)type.CHTYPE_USER)
                        {
                          
                            if(line.State == (int)state.CH_FREE)
                            {
                                log.Debug("内线" + line.Number + "摘机");
                                 InvokeVcDll.InitDtmfBuf(line.Number);
                                 line.State = (int)state.CH_DETECT;
                            }
                           ;
                        }
                        //如果是外线
                        else if (line.Type == (int)type.CHTYPE_TRUNK)
                        {
                            log.Debug("外线" + line.Number + "电话打入");
                            InvokeVcDll.StartSigCheck(line.Number);
                            InvokeVcDll.ResetCallerIDBuffer(line.Number);
                            line.State = (int)state.CH_GETCAllNUM;
                            //设置来电时间
                            line.Comingtime = System.DateTime.Now;
                            //id在comingcall方法内部产生
                            LineRecordHelper.ComingCall(line, null);

                        }
                    }
                    break;
                
                case (int)state.CH_GETCAllNUM:
                    bool bOffHook = false;
                    if (line.Callertime > 1000)
                    {
                        bOffHook = true;
                    }
                    if (bOffHook)
                    {
                        string zhujiao = getCaller(line);
                        line.State = (int)state.CH_PlAYWELCOME;
                        LineRecordHelper.ComingCall(line, null);
                    }
                    else
                    {
                        line.Callertime += 70;
                    }
                    break;

                //播放欢迎语音
                case (int)state.CH_PlAYWELCOME:
                    InvokeVcDll.OffHook(line.Number);
                    this.playWelcome(line.Number);
                    line.State = (int)state.CH_DIALING;
                    break;
                //查找内线连接
                case (int)state.CH_DIALING:
                    tmpCTL = this.GetFreeUser(line);
                    if (tmpCTL == -1)
                    {
                        //欢迎语音结束,播放坐席忙录音
                        if (InvokeVcDll.CheckPlayEnd(line.Number))
                        {
                            log.Debug("欢迎语音播放完成，播放忙音");
                            this.stopWelcome(line.Number);
                            string weladd = Environment.CurrentDirectory + "\\busy1";
                            InvokeVcDll.StartPlayFile(line.Number, weladd, 0);
                            line.State = (int)state.CH_PLAYBUSY;
                         }
                        break;
                    }
                    Lines[tmpCTL].CallerPhone = line.CallerPhone;
                    InvokeVcDll.FeedRealRing((ushort)tmpCTL);
                    line.ConnectToLine = tmpCTL;
                    Lines[tmpCTL].ConnectToLine = line.Number;
                    line.State = (int)state.CH_CHECKSIG;
                    Lines[tmpCTL].State = (int)state.CH_RINGING;
                    log.Debug("外线" + line.Number + "打入,连接内线" + tmpCTL);
                    LineRecordHelper.ComingCall(line, Lines[tmpCTL]);
                    break;
                //坐席全忙
               case (int)state.CH_PLAYBUSY:
                    if (InvokeVcDll.CheckPlayEnd(line.Number))
                    {
                            log.Debug(line.Number+"忙音播放完，断开");
                            InvokeVcDll.StopPlayFile(line.Number);
                            this.ResetLine(line.Number);
                    }
                    break;
                //等待连接状态
                case (int)state.CH_CHECKSIG:
                    tmpCTL = line.ConnectToLine;
                     //检测内线摘机
                    if (InvokeVcDll.OffHookDetect((ushort)tmpCTL) || Lines[tmpCTL].IsKey)
                    {
                        log.Debug("内线" + tmpCTL + "摘机,与外线" + line.Number + "连通");
                        this.stopWelcome(line.Number);
                        //播放工号
                        log.Debug("播放工号");
                        string weladd = Environment.CurrentDirectory + "\\" + Lines[tmpCTL].Gonghao;
                        InvokeVcDll.StartPlayFile(line.Number, weladd, 0);
                        line.State = (int)state.CH_PLAYGONGHAO;
                        break;
                     }
                    else
                    {
                        LineRecordHelper.ComingCall(line, Lines[tmpCTL]);
                    }
                    break;
                case (int)state.CH_PLAYGONGHAO:
                    tmpCTL = line.ConnectToLine;
                    //检测内线摘机
                    if (InvokeVcDll.OffHookDetect((ushort)tmpCTL) || Lines[tmpCTL].IsKey)
                    {
                        log.Debug("内线" + tmpCTL + "摘机,与外线" + line.Number + "连通");
                        if (InvokeVcDll.CheckPlayEnd(line.Number))
                        {
                            InvokeVcDll.StopPlayFile(line.Number);
                        }
                        else
                        {
                            break;
                        }
                         InvokeVcDll.FeedPower((ushort)tmpCTL);
                        InvokeVcDll.SetLink(line.Number, (ushort)tmpCTL);
                        line.State = (int)state.CH_CONNECTED;
                        Lines[tmpCTL].State = (int)state.CH_CONNECTED;
                        //录音
                        string guid = Guid.NewGuid().ToString();
                        string file = GetSavePath() + guid + ".wav";
                        InvokeVcDll.StartRecordFile((ushort)tmpCTL, file, 1024 * 8 * 60 * 30);
                        Lines[tmpCTL].RecordFile = guid;
                        //设置接通时间
                        line.Rectime = System.DateTime.Now;
                        line.Islink = "yes";
                        
                        LineRecordHelper.ComingCall(line, Lines[tmpCTL]);
                    }
                    else
                    {
                      //  LineRecordHelper.ComingCall(line, Lines[tmpCTL]);
                    }
                    break;
                case (int)state.CH_RINGING:
                    break;
                case (int)state.CH_CONNECTED:
                   if (InvokeVcDll.CheckSendEnd(line.Number))
                    {
                        InvokeVcDll.StartSigCheck(line.Number);
                    }
                    break;
                case (int)state.CH_WAITONHOOK:
                    if (line.Type == (int)type.CHTYPE_TRUNK)
                    {
                        log.Debug("外线" + line.Number + "等待挂机");
                        ResetLine(line.Number);
                    }
                    break;
                //摘机状态
                case (int)state.CH_DETECT:
                    //如果是内线,检测是否挂机
                    if (line.Type == (int)type.CHTYPE_USER)
                    {
                        short code = InvokeVcDll.GetDtmfCode(line.Number);
                        if (code != -1)
                        {
                            log.Debug("内线" + line.Number + "按键" + code);
                            if (line.ListenNum != -1)
                            {
                                InvokeVcDll.ClearOneFromAnother((ushort)line.Number, (ushort)line.ListenNum);
                            }
                            line.ListenNum = ConvertDtmf(code);
                        }
                        InvokeVcDll.StartHangUpDetect(line.Number);
                        if (InvokeVcDll.HangUpDetect(line.Number) == 2)
                        {
                            InvokeVcDll.ClearOneFromAnother((ushort)line.Number, (ushort)line.ListenNum);
                            log.Debug("内线" + line.Number + "挂机111");
                            int num = line.ConnectToLine;
                            ResetLine(line.Number);
                            if (num != -1)
                            {
                                ResetLine(num);
                            }
                        }
                    }
                    break;
                    //外拨
                case (int)state.CH_CALLPHONE:
                    //拨号完成后，建立通道
                    if (InvokeVcDll.CheckSendEnd((ushort)line.Number))
                    {
                        InvokeVcDll.StartSigCheck((ushort)line.Number);
                        InvokeVcDll.StartSigCheck((ushort)line.ConnectToLine);
                        InvokeVcDll.SetLink((ushort)line.Number, (ushort)line.ConnectToLine);
                        //设置为接通
                        line.State = (int)state.CH_CONNECTED;
                        Lines[line.ConnectToLine].State = (int)state.CH_CONNECTED;
                    }
                    break;
                default:
                    break;
            }
        }

        private bool first = true;


        //设置监听
        private void setListen(LineInfo line)
        {
            LineInfo anther = null;
            try
            {
                anther = Lines[line.ListenNum];
            }
            catch (Exception e)
            {
                return;
            }
            if (anther != null && anther.State == (ushort)state.CH_CONNECTED)
            {
                int ren = InvokeVcDll.LinkOneToAnother(line.Number, anther.Number);
                log.Debug(line.Number + "监听" + anther.Number + ",返回值:" + ren);
                ren = InvokeVcDll.LinkOneToAnother(line.Number, (ushort)anther.ConnectToLine);
                log.Debug(line.Number + "监听" + anther.ConnectToLine + ",返回值:" + ren);

            }
        }

        /**
         * 号码转换
         * */
        private short ConvertDtmf(short ch)
        {
            short result;
            switch (ch)
            {
                case 10:
                    result = 0;
                    break;
                //case 11:
                //    result = '*';
                //    break;
                // case 12:
                //     result = '#';
                //     break;
                // case 13:
                // case 14:
                // case 15:
                //    result= (char) (ch - 13 + 'a');
                //    break;
                //case 0:
                //     result = 'd';
                //    break;
                default:
                    result = ch;//change DTMF from number to ASCII
                    break;
            }
            return result;
        }

        private string GetSavePath()
        {
            return @"d:\录音文件\";
        }

        //重置通道
        public void ResetLine(int LineNo)
        {
            if (Lines[LineNo].Type == (int)type.CHTYPE_TRUNK)
            {
                LineInfo trunk = Lines[LineNo];
                InvokeVcDll.FeedPower((ushort)Lines[LineNo].ConnectToLine);
                InvokeVcDll.HangUp((ushort)LineNo);
                InvokeVcDll.Sig_ResetCheck((ushort)LineNo);
                InvokeVcDll.StartSigCheck((ushort)LineNo);
                //设置挂断时间
                Lines[LineNo].Handuptime = System.DateTime.Now;
                LineRecordHelper.ComingCall(trunk, null);
            }
            if (Lines[LineNo].Type == (int)type.CHTYPE_USER)
            {
                InvokeVcDll.FeedPower((ushort)LineNo);
                 if (Lines[LineNo].ConnectToLine != -1 && Lines[Lines[LineNo].ConnectToLine].Type == (int)type.CHTYPE_TRUNK)
                    InvokeVcDll.HangUp((ushort)Lines[LineNo].ConnectToLine);

            }
            if (Lines[LineNo].ConnectToLine != -1)
            {
                int duan = InvokeVcDll.ClearLink((ushort)LineNo, (ushort)Lines[LineNo].ConnectToLine);
                log.Debug("断开连接返回值:" + duan + " 线路:" + LineNo + "," + Lines[LineNo].ConnectToLine);
                Lines[Lines[LineNo].ConnectToLine].ConnectToLine = -1;
            }
            Lines[LineNo].ConnectToLine = -1;
            Lines[LineNo].State = (int)state.CH_FREE;
            Lines[LineNo].CallerPhone = "";
            Lines[LineNo].RecordFile = "";
            Lines[LineNo].IsKey = false;
            //清空id
            Lines[LineNo].Id = "";
            Lines[LineNo].Islink = "no";
            Lines[LineNo].Callertime = 0;
        }


        //重置内线通道，状态为正在记录
        public void ResetLineInner(int LineNo)
        {
             
            if (Lines[LineNo].Type == (int)type.CHTYPE_USER)
            {
                InvokeVcDll.FeedPower((ushort)LineNo);
                 if (Lines[LineNo].ConnectToLine != -1 && Lines[Lines[LineNo].ConnectToLine].Type == (int)type.CHTYPE_TRUNK)
                    InvokeVcDll.HangUp((ushort)Lines[LineNo].ConnectToLine);

            }
            if (Lines[LineNo].ConnectToLine != -1)
            {
                int duan = InvokeVcDll.ClearLink((ushort)LineNo, (ushort)Lines[LineNo].ConnectToLine);
                log.Debug("断开连接返回值:" + duan + " 线路:" + LineNo + "," + Lines[LineNo].ConnectToLine);
                Lines[Lines[LineNo].ConnectToLine].ConnectToLine = -1;
            }
            Lines[LineNo].ConnectToLine = -1;
            Lines[LineNo].State = (int)state.CH_WAITCONFIRM;
            Lines[LineNo].CallerPhone = "";
            Lines[LineNo].RecordFile = "";
            Lines[LineNo].IsKey = false;
            //清空id
            Lines[LineNo].Id = "";
            Lines[LineNo].Islink = "no";
            Lines[LineNo].Callertime = 0;


        }


        //播放请等待语音
        public void playWelcome(ushort lineNum)
        {
            string weladd = Environment.CurrentDirectory + "\\wait";
            if (InvokeVcDll.CheckPlayEnd(lineNum))
            {
                InvokeVcDll.StartPlayFile(lineNum, weladd, 0);
            }
        }

        ////停止播放等待语音
        public void stopWelcome(ushort lineNum)
        {
            InvokeVcDll.StopPlayFile(lineNum);
        }

        #region WebServer服务

        public void StartServer()
        {
            string hostName = Dns.GetHostName();
            IPAddress[] ipes = Dns.GetHostAddresses(hostName);
            IPAddress addres = ipes[0];
            string baseAddress = "http://" + addres + ":8000/";
            host = new ServiceHost(typeof(WorkService), new Uri(baseAddress));
            host.AddServiceEndpoint(typeof(WebServerInterface), new WebHttpBinding(), "").Behaviors.Add(new WebHttpBehavior());
            host.Open();
        }



        public void CloseServer()
        {
            host.Close();
        }

        public LineInfo GetLineInfo(int lineNum, string gonghao)
        {
            Lines[lineNum].LastTime = System.DateTime.Now;
            Lines[lineNum].Gonghao = gonghao;
            return Lines[lineNum];
        }

        public void StateChange(int lineNum, int state)
        {
            LineInfo line = Lines[lineNum];
            line.State = state;

        }

        public string test()
        {
            return "123";
        }


        private Stream StringToStream(string result)
        {
            WebOperationContext.Current.OutgoingResponse.ContentType = "application/xml";
            return new MemoryStream(Encoding.UTF8.GetBytes(result));
        }

        public Stream GetFlashPolicy()
        {
            string result = @"<?xml version=""1.0""?>
                                        <cross-domain-policy>
                                        <allow-access-from domain=""*"" />
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



        public Stream Play(string recordId)
        {
            string fileName = GetSavePath() + recordId + ".wav";
            Stream file;
            try
            {
                file = new FileStream(fileName, FileMode.Open);
            }
            catch (Exception e)
            {
                return null;
            }
            return file;
        }


        //add 20100419
        public String Receiver(int lineNum)
        {
            LineInfo line = Lines[lineNum];
            //如果是内线，并且状态为响铃
            if (line.Type == (int)type.CHTYPE_USER && line.State == (int)state.CH_RINGING)
            {
                log.Debug("快捷摘机.............");
                line.IsKey = true;
            }
            return "sucessful";
        }

        public String HandUp(int lineNum)
        {
            int trunk = Lines[lineNum].ConnectToLine;
            if (trunk != -1)
            {
                this.ResetLine(lineNum);
                this.ResetLine(trunk);
                Lines[lineNum].State = (int)state.CH_WAITCONFIRM;
                return "sucessful";
            }
            else
            {
                return "nocalll";
            }
        }

        public void ConfirmHandup(int lineNum)
        {
            log.Debug(lineNum+"提交挂断.............");
            Lines[lineNum].State = (int)state.CH_FREE;
        }

        //拨号
        public string CallPhone(int lineNum, string phone)
        {
            LineInfo line = Lines[lineNum];
            //判断话务员是否摘机
            if (line.State!=(int)state.CH_DETECT)
            {
                return "请先摘机，然后拨号!";
            }
            //如果是内线，并且状态为空闲
            if (line.Type == (int)type.CHTYPE_USER)
            {
                //执行拨号
                LineInfo inline = Lines[lineNum];
                int trunk = getFreeTrunk();
                LineInfo outline = Lines[trunk];
                InvokeVcDll.StartPlaySignal((ushort)lineNum, (ushort)signal.SIG_STOP);
                InvokeVcDll.StopPlayFile((ushort)lineNum);
                InvokeVcDll.StopPlayFile((ushort)trunk);
                inline.ConnectToLine = trunk;
                inline.CallerPhone = phone;
                outline.ConnectToLine = lineNum;
                outline.CallerPhone = phone;
                outline.IsKey = false;
                InvokeVcDll.OffHook((ushort)trunk);
                outline.State = (int)state.CH_CALLPHONE;
                InvokeVcDll.SendDtmfBuf((ushort)trunk, phone);
            }
            return "sucessful";
        }

        //至忙
        public void SetBusy(int lineNum)
        {
            LineInfo line = Lines[lineNum];
            //内线,空闲
            if (line.Type == (int)type.CHTYPE_USER && line.State==(int)state.CH_FREE)
            {
                //至忙
                line.State = (int)state.CH_BUSY;
            }
        }

        //获得空闲外线通道
        public int getFreeTrunk()
        {
            int result = -1;
            foreach (LineInfo line in Lines)
            {
                if (line.Type == (ushort)type.CHTYPE_TRUNK && line.State == (ushort)state.CH_FREE)
                {
                    result = line.Number;
                    break;
                }
            }
            return result;
        }

        #endregion


        
    }



    //状态枚举
    enum state
    {
        //空闲
        CH_FREE,
        //没有使用
        CH_WAITDIAL,
        //没有使用
        CH_GETDIALCODE1,
        //没有使用
        CH_GETDIALCODE2,
        //查找内线连接
        CH_DIALING,
        //等待连接
        CH_CHECKSIG,
        //正在振铃
        CH_RINGING,
        //没有使用
        CH_CONNECTED9,
        //接通
        CH_CONNECTED,
        //等待挂机
        CH_WAITONHOOK,
        //内线摘机
        CH_DETECT,
        //忙碌
        CH_BUSY,
        //收主叫
        CH_GETCAllNUM,
        //等待操作员填写记录
        CH_WAITCONFIRM,
        //电话外拨
        CH_CALLPHONE=100,
        //播放工号
        CH_PLAYGONGHAO=114,
        //播放忙音
        CH_PLAYBUSY= 115,
        //播放欢迎语音
        CH_PlAYWELCOME=116,
        

    }

    //类型枚举
    public enum type
    {
        //内线
        CHTYPE_USER,
        //外线
        CHTYPE_TRUNK,
    }

    //声音枚举
    public enum signal
    {
        //停止播放信号音
        SIG_STOP = 0,
        //拨号音
        SIG_DIALTONE = 1,
        //忙音一
        SIG_BUSY1 = 2,
        //忙音2
        SIG_BUSY2 = 3,
        //回铃音
        SIG_RINGBACK = 4,
        SIG_CUIGUA = 5,
        SIG_STOP_NEW = 10,
        SIG_DOUBLE_RINGBACK = 20,
        HANG_UP_FLAG_FALSE = 0,
        HANG_UP_FLAG_TRUE = 1,
        HANG_UP_FLAG_START = 2,
        HANG_UP_FLAG_PRESS_R = 3,
    }

    enum Check
    {
        //尚未得出结果。 
        S_NORESULT,
        //没有拨号音。 
        S_NODIALTONE,
        //检测到对方占线的忙音。 
        S_BUSY,
        //对方摘机，可以进行通话。 
        S_CONNECT,
        //：振铃若干次，无人接听电话。 
        S_NOBODY,
        //没有信号音。 
        S_NOSIGNAL,
        S_RINGBACK,//检测到回铃音
    }

}
