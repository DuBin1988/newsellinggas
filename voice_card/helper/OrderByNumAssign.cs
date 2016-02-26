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
            //for (int i = findStart; i < Lines.Count; i++)
            for (int i = findStart; i < Lines.Count - 1; i++) // 留最后一个内线号外拨及听录音
            {
                LineInfo line = Lines[i];
                //log.Debug("通道:" + line.Number + ",通道类型:" + line.Type + ",状态:" + line.State);
                if (line.Type != (ushort)type.CHTYPE_USER || line.State != (ushort)state.CH_FREE)
                {
                    continue;
                }
                //检查时间
                DateTime now = System.DateTime.Now;
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
    }
}
