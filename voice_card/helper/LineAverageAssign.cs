using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using voice_card.entity;
using voice_card.service;
using log4net;

/**
 * 
 * 根据通达接听电话数量平均分配
 **/ 
namespace voice_card.helper
{
    class LineAverageAssign :TelAssign
    {
        private static ILog log = LogManager.GetLogger(typeof(WorkService));

      public int Assign(System.Collections.ObjectModel.ObservableCollection<entity.LineInfo> Lines)
        {
            int result = -1;
            int listedNum = 0;
           foreach (LineInfo line in Lines)
            {
                if(line.Type != (ushort)type.CHTYPE_USER || line.State != (ushort)state.CH_FREE)
                {
                    continue;
                }
                //检查时间
                DateTime now = System.DateTime.Now;
                DateTime lastTime = line.LastTime;
                TimeSpan ts = now.Subtract(lastTime).Duration();
                 log.Debug("通道时间差" + ts.TotalSeconds);
                if (ts.TotalSeconds > 10)
                {
                   
                     //log.Debug("内线" +line.Number+"time out");
                     continue;
                 }
                //查看通道最后接听时间，如果时间是昨天，回复接听数量为0，
                 DateTime lastHandUp = line.Handuptime;
                 TimeSpan comp =  now.Subtract(lastHandUp);
                 if (comp.Days > 0)
                 {
                    // log.Debug("上次接听时间" + line.LastTime.ToString()+",接听数量置0");
                     line.ListenedNums = 0;
                     result = line.Number;
                   return result;
                 }
                 if (result == -1)
                 {
                     result = line.Number;
                     listedNum = line.ListenedNums;
                    // log.Debug("set 内线" + line.Number + "listener");
                     continue;
                 }
                 if (listedNum.CompareTo(line.ListenedNums) > 0)
                 {
                      result = line.Number;
                      listedNum = line.ListenedNums;
                     // log.Debug("compare 内线" + line.Number + "listener");
                     
                 }
               
            }
           log.Debug("返回接听通道号" + result);
           return result;
        }
    }
}
