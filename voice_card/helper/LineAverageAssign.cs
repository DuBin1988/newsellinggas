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
               //通道设置有自动接通时间,当前时间在通道时间范围内,转接到该通道
                if (this.CheckCanAoutLink(line))
                {
                     return line.Number;
                }
                //检查时间
                DateTime now = System.DateTime.Now;
                DateTime lastTime = line.LastTime;
                TimeSpan ts = now.Subtract(lastTime).Duration();
                log.Debug("通道时间差" + ts.TotalSeconds);
                if (ts.TotalSeconds > 10)
                {
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
                      continue;
                 }
                 if (listedNum.CompareTo(line.ListenedNums) > 0)
                 {
                      result = line.Number;
                      listedNum = line.ListenedNums;
                 }
             }
           log.Debug("返回接听通道号" + result);
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
          if(nowHM.CompareTo(line.StartHour) > 0 && nowHM.CompareTo(line.EndHour) < 0)
          {
              return true;
          }
          return false;
      }
    }
}
