using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Workflow
{
    //执行者
    class Actor
    {
        private Diagram diagram;

        public Actor(Diagram diagram)
        {
            this.diagram = diagram;
        }

        public event PropertyChangedEventHandler PropertyChanged;

        public void OnPropertyChanged(string info)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(info));
            }
        }

        //执行者包含的活动
        private ObservableCollection<Activity> activities = new ObservableCollection<Activity>();
        public ObservableCollection<Activity> Activities
        {
            get
            {
                return activities;
            }
        }

        public void AddAactivity(Activity activity)
        {
            activities.Add(activity);
        }

        //执行者的名字
        private string acname;
        public string acName {
            get { return acname; }
            set
            {
                if (this.acname != value)
                {
                    this.acname = value;
                    OnPropertyChanged("acName");
                }
            }
        }
        
        //执行者所对应的y坐标
        public double GetY()
        {
            return diagram.GetY(this);
        }
    }
}
