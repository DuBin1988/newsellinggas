using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Collections;
using Com.Aote.ObjectTools;

namespace Com.Aote.Behaviors
{
    //批量转换列表动作
    public class BatchTransformAction : BaseAsyncAction
    {
        //转换源
        public PagedObjectList SourceObject { get; set; }

        //转换对象
        public ObjectList TargetObject { get; set; }

        public override void Invoke()
        {
            State = State.Start;
            IsBusy = true;
            Listen();
            //TransSave();
            SourceObject.PageIndex = 0;
        }

        //转换列表
        public void TransSave()
        {
            TargetObject.Clear();
            foreach (GeneralObject item in (IList)SourceObject)
            {
                //模板对象
                if (TargetObject.templetObject == null)
                {
                    throw new Exception("模板对象不能为空!");
                }
                if (TargetObject.TempObj == null)
                {
                    throw new Exception("临时对象不能为空!");
                }
                //将临时对象值赋值为要转换对象
                TargetObject.TempObj.CopyFrom(item);
                //产生新对象
                GeneralObject go = new GeneralObject();
                go.WebClientInfo = TargetObject.templetObject.WebClientInfo;
                go.CopyDataFrom(TargetObject.templetObject);
                TargetObject.Add(go);
            }
            //保存
            TargetObject.Save();
        }

        //监听
        public void Listen()
        {
            //源对象加载完成后
            SourceObject.DataLoaded += (o, e) =>
            {
                //转换保存
                TransSave();
            };
            //列表保存完成后
            TargetObject.Completed += (o, e) =>
            {
                //翻页
                int page = SourceObject.Count / SourceObject.PageSize;
                if (SourceObject.Count % SourceObject.PageSize > 0)
                {
                    page++;
                }
                if (page-1 > SourceObject.PageIndex)
                {
                    SourceObject.PageIndex++;
                }
                else
                {
                    IsBusy = false;
                    State = State.End;
                }
            };
        }


        private bool canInvoke;
        public bool CanInvoke
        {
            get { return canInvoke; }
            set
            {
                if (canInvoke != value)
                {
                    canInvoke = value;
                    if (canInvoke)
                    {
                        Invoke();
                    }
                }
            }
        }
    }
}
