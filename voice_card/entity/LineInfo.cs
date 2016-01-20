using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using voice_card.service;


namespace voice_card.entity
{
    /**
     *  单条线路对象 
     * */
    public class LineInfo : DependencyObject
    {
        


        //通道号
        public static DependencyProperty number = DependencyProperty.Register("number", typeof(ushort), typeof(LineInfo));
        public ushort Number
        {
            get { return (ushort)GetValue(number); }
            set { SetValue(number, value); }
        }

        //通道类型
        public static DependencyProperty type = DependencyProperty.Register("type", typeof(ushort), typeof(LineInfo));

        public ushort Type
        {
            get { return (ushort)GetValue(type); }
            set { SetValue(type, value); }
        }

        //通道状态
        public static DependencyProperty state = DependencyProperty.Register("state", typeof(int), typeof(LineInfo));

        public int State
        {
            get { return (int)GetValue(state); }
            set { SetValue(state, value); }
        }



        //对应该通道连接上的通道号，无连接 = -1
        public static DependencyProperty connectToLine = DependencyProperty.Register("connectToLine", typeof(int), typeof(LineInfo));

        public int ConnectToLine
        {
            get { return (int)GetValue(connectToLine); }
            set { SetValue(connectToLine, value); }
        }

        //保存了该通道接受到的电话号码信息
        public static DependencyProperty callerPhone = DependencyProperty.Register("callerPhone", typeof(string), typeof(LineInfo));

        public string CallerPhone
        {
            get { return (string)GetValue(callerPhone); }
            set { SetValue(callerPhone, value); }
        }

        //录音文件地址
        public static DependencyProperty recordFile = DependencyProperty.Register("recordFile", typeof(string), typeof(LineInfo));

        public string RecordFile
        {
            get { return (string)GetValue(recordFile); }
            set { SetValue(recordFile, value); }
        }

        //上次访问时间
        private DateTime lastTime;

        public DateTime LastTime
        {
            set { lastTime = value; }
            get { return lastTime; }
        }

        //id
        private string id;

        public string Id
        {
            set { id = value; }
            get { return id; }
        }

        //工号
        private string gonghao;

        public string Gonghao
        {
            set { gonghao = value; }
            get { return gonghao; }
        }


        //来电时间
        private DateTime comingtime;
        public DateTime Comingtime
        {
            set { comingtime = value; }
            get { return comingtime; }
        }

        //接听时间
        private DateTime rectime;
        public DateTime Rectime
        {
            set { rectime = value; }
            get { return rectime; }
        }

        //挂断时间
        private DateTime handuptime;
        public DateTime Handuptime
        {
            set { handuptime = value; }
            get { return handuptime; }
        }

        //是否快捷摘机
        private Boolean iskey;
        public Boolean IsKey
        {
            set { iskey = value; }
            get { return iskey; }
        
        }

        //是否连接
        private string islink;
        public string Islink
        {
            set { islink = value;}
            get { return islink; }
        }

        //是否连接
        private short listenNum;
        public short ListenNum
        {
            set { listenNum = value; }
            get { return listenNum; }
        }

        private int callertime;
        public int Callertime
        {
            set { callertime = value; }
            get { return callertime; }
        }


        /**
         * 已结听电话数量
         */
        private int listenedNums;
        public int ListenedNums
        {
            set{ this.listenedNums =value;}
            get{return this.listenedNums;}
        }

        /**
         * 查找内线次数
         **/
        private int findInnerTimes;
        public int FindInnerTimes
        {
            set { this.findInnerTimes = value; }
            get { return this.findInnerTimes; }
        }

        //设定自动接通的开始时间
        private string startHour;
        public string StartHour
        {
            set { startHour = value; }
            get { return startHour; }
        }

        //设定自动接通的结束时间
        private string endHour;
        public string EndHour
        {
            set { endHour = value; }
            get { return endHour; }
        }


    }
}
