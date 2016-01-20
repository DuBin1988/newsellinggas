using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using log4net;
using voice_card.entity;
using voice_card.service;

namespace voice_card.helper
{
    //记录上次接听通道号，从上次接听好下一个开始查找
    class OrderByNumAssign : TelAssign
    {

        private static ILog log = LogManager.GetLogger(typeof(OrderByNumAssign));

        //上次接听通道号
        public  int LastReturnNum  = -1;

        public int Assign(System.Collections.ObjectModel.ObservableCollection<entity.LineInfo> Lines)
        {
            log.Debug("开始查找空闲通道,上次接听通道:" + LastReturnNum);
            int result = -1;
            //先从上次号码加一开始找
            int findStart = LastReturnNum+1;
            //log.Debug("起始通道:" + findStart);
            for (int i = findStart; i < Lines.Count; i++)
            {
                DateTime now = System.DateTime.Now;
                LineInfo line = Lines[i];
                //log.Debug("通道:" + line.Number + ",通道类型:" + line.Type + ",状态:" + line.State);
                if (line.Type != (ushort)type.CHTYPE_USER || line.State != (ushort)state.CH_FREE)
                {
                    continue;
                }
                //通道设置有自动接通时间,当前时间在通道时间范围内,转接到该通道
                 if (this.CheckCanAoutLink(line))
                {
                    return line.Number;
                }
                //检查时间
                DateTime lastTime = line.LastTime;
                TimeSpan ts = now.Subtract(lastTime).Duration();
               // log.Debug("通道时间差" + ts.TotalSeconds);
                if (ts.TotalSeconds > 10)
                {
                    continue;
                }
                log.Debug("第一个循环中返回通道号:" + line.Number);
                result = line.Number;
                LastReturnNum = result;
                return result;

            }
            //再从第一个开始循环
            for (int i = 0; i < findStart ; i++)
            {
                LineInfo line = Lines[i];
                //log.Debug("通道:" + line.Number + ",通道类型:" + line.Type + ",状态:" + line.State);
                if (line.Type != (ushort)type.CHTYPE_USER || line.State != (ushort)state.CH_FREE)
                {
                    continue;
                }
                //通道设置有自动接通时间,当前时间在通道时间范围内,转接到该通道
                //通道设置有自动接通时间,当前时间在通道时间范围内,转接到该通道
                if (this.CheckCanAoutLink(line))
                {
                    return line.Number;
                }
                //检查时间
                DateTime now = System.DateTime.Now;
                DateTime lastTime = line.LastTime;
                TimeSpan ts = now.Subtract(lastTime).Duration();
                //log.Debug("通道时间差" + ts.TotalSeconds);
                if (ts.TotalSeconds > 10)
                {
                    continue;
                }
                log.Debug("第二个循环中返回通道号:" + line.Number);
                result = line.Number;
                LastReturnNum = result;
                return result;
            }
            log.Debug("未找到空闲内线,返回:"+result);
            return result;
         }


        /**
       * 检查是否自动接通
       * */
        public bool CheckCanAoutLink(LineInfo line)
        {
            //通道设置有自动接通时间,当前时间在通道时间范围内,转接到该通道
            DateTime now = System.DateTime.Now;
            string nowHM = now.ToString("HH:mm");
            //无开始时间,不自动接通
            if (line.StartHour != null || line.StartHour.Equals(""))
            {
                return false;
            }
            //无结束时间,不自动接通
            if (line.EndHour != null || line.EndHour.Equals(""))
            {
                return false;
            }
            //当前时间在设置的开始结束之间,可接通
            if (nowHM.CompareTo(line.StartHour) > 0 && nowHM.CompareTo(line.EndHour) < 0)
            {
                return true;
            }
            return false;
        }
    }
}
