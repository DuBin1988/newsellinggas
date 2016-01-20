using System;
using System.Windows.Controls;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.ObjectModel;
using System.ComponentModel;

namespace Workflow
{
    
    //整个流程图
    class Diagram
    {
        //执行者集合
        private ObservableCollection<Actor> actors = new ObservableCollection<Actor>();
        public ObservableCollection<Actor> Actors
        {
            get
            {
                return actors;
            }
        }

        //添加执行者
        public void AddActors(Actor actor)
        {
            actors.Add(actor);
        }
        //删除执行者
        public void DelActors(Actor actor)
        {
            actors.Remove(actor);
        }

        //转移线集合
        private ObservableCollection<Transfer> transfers = new ObservableCollection<Transfer>();
        public ObservableCollection<Transfer> Transfer
        {
            get
            {
                return transfers;
            }
        }
        //添加转移线
        public void AddTransfer(Transfer transfer)
        {
            transfers.Add(transfer);
        }

        //获得执行者y坐标
        public double GetY(Actor actor)
        {
            int pos = actors.IndexOf(actor)+1;
            double y = pos * 50 - 30;
            return y;
        }
    }
}
