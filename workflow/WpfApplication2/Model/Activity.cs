using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace Workflow
{
    //活动
    class Activity : INotifyPropertyChanged
    {
        private Actor actor;

        public Activity(Actor actor)
        {
            this.actor = actor;
        }

        #region PropertyChanged事件
        public event PropertyChangedEventHandler PropertyChanged;

        public void OnPropertyChanged(string info)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(info));
            }
        }
        #endregion

        //活动名称
        private string activityname;
        public string ActivityName
        {
            get { return activityname; }
            set
            {
                activityname = value;
                OnPropertyChanged("ActivityName");
            }
        }
        
        //活动的x坐标
        private double pos;
        public double Pos
        {
            get { return pos; }
            set
            {
                pos = value;
            }
        }

        //获得活动具体位置
        public Point GetPos()
        {
            Point p = new Point();
            p.X = pos;
            p.Y = actor.GetY();
            return p;
        }
    }
}
