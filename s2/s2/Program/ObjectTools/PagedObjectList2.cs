using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Json;
using System.Reflection;
using System.Windows.Data;
using System.Collections;
using System.Net;
using Com.Aote.Utils;
using Com.Aote.Marks;
using Com.Aote.Logs;
using System.Windows.Browser;

namespace Com.Aote.ObjectTools
{
    /// <summary>
    /// 可翻页列表，翻页对象加载过程分两步，首先加载总体信息，然后当页面索引发生变化时，加载某页具体信息。
    /// </summary>
    public class PagedObjectList2 : BasePagedList
    {
        private static Log Log = Log.GetInstance("Com.Aote.ObjectTools.PagedObjectList2");

        #region SumNames 求和字段名称，以","分隔
        /// <summary>
        /// 在开始加载总体信息时，要进行求和的字段名称。以","分隔
        /// </summary>
        public String SumNames { get; set; }
        #endregion

        #region Count 总共数据个数

        /// <summary>
        /// 总数据个数，在加载总体信息时获得，并赋值。
        /// </summary>
        private int count = -1;
        override public int Count 
        {
            get {
                if (count < 0) return 0;
                return count; }
            set
            {
                if (count != value)
                {
                    count = value;
                    OnPropertyChanged("Count");
                }
            }
        }
        #endregion

        public string proxy { get; set; }

        #region MultiPath 一对多分页查询时，用于获取总数的路径，这个路径只配置主表条件

        public static readonly DependencyProperty MultiPathProperty =
            DependencyProperty.Register("MultiPath", typeof(string), typeof(PagedObjectList2),
            new PropertyMetadata(new PropertyChangedCallback(OnMultiPathChanged)));

        /// <summary>
        /// 路径改变时，获取数据
        /// </summary>
        /// <param name="dp">自身</param>
        /// <param name="args">新值参数</param>
        public static void OnMultiPathChanged(DependencyObject dp, DependencyPropertyChangedEventArgs args)
        {
            PagedObjectList2 ol = (PagedObjectList2)dp;
            if (ol.LoadOnPathChanged)
            {
                ol.Load();
            }
        }

        /// <summary>
        /// 获取数据的路径，当路径发生变化时，将根据新路径到后台获取数据
        /// </summary>
        public string MultiPath
        {
            get { return (string)GetValue(MultiPathProperty); }
            set { SetValue(MultiPathProperty, value); }
        }
        #endregion

        #region Load 加载数据
        /// <summary>
        /// 数据加载过程，先加载总体信息，然后在页号发生变化时，加载某页数据。
        /// 如果加载总体信息时，没有加载到数据，则直接通知加载过程结束，因为这时不会再有具体页数据加载过程。
        /// 否则，在加载总体数据信息后，并不认为数据加载过程已经结束，而是在加载完页面数据后，才认为加载过程
        /// 结束。
        /// </summary>
        override public void Load()
        {
            Log.Debug("begin load");
            //只要有一个不为空，则可以加载数据
            if (Path == null && MultiPath == null)
            {
                return;
            }

            //Path后跟求和字段名称，代表求总数
            Uri uri = null;
            string uuid = System.Guid.NewGuid().ToString();
            if (MultiPath != null)
            {
                uri = new Uri(WebClientInfo.BaseAddress + "/" + HttpUtility.UrlEncode(proxy) + "/" + HttpUtility.UrlEncode(MultiPath.Replace("%", "%25").Replace("#", "%23").Replace("^", "<") + "|" + SumNames + "?uuid=" + uuid).Replace("+", "%20"));
            }
            else
            {
                uri = new Uri(WebClientInfo.BaseAddress + "/" + HttpUtility.UrlEncode(proxy) + "/" + HttpUtility.UrlEncode(Path.Replace("%", "%25").Replace("#", "%23").Replace("^", "<") + "|" + SumNames + "?uuid=" + uuid).Replace("+", "%20"));
            }
            WebClient client = new WebClient();
            client.OpenReadCompleted += (o, a) =>
            {
                if (a.Error == null)
                {
                    //更新数据
                    JsonObject item = JsonValue.Load(a.Result) as JsonObject;                   
                    FromJson(item);
                    //如果没有数据，必须通知完成，因为不会再有页面变化通知，这时，认为发生了错误，错误状态为没有数据
                    if(Count == 0)
                    {
                        State = State.LoadError;
                        Error = "没有满足条件的数据";
                        OnDataLoaded(a);
                        OnCompleted(a);
                        IsBusy = false;
                    }
                    else
                    {
                        //迫使重新加载数据
                        int nowIndex = pageIndex;
                        PageIndex = nowIndex;
                    }

                }
                Log.Debug("end load");
            };
            IsBusy = true;
            OnLoading();
            State = State.StartLoad;
            client.OpenReadAsync(uri);
        }
        
        /// <summary>
        /// 加载当前页数据
        /// </summary>
        override public void LoadDetail()
        {
            Log.Debug("begin loaddetail");
            string uuid = System.Guid.NewGuid().ToString();
            if (Path == null) return;
            Uri uri = new Uri(WebClientInfo.BaseAddress + "/" + HttpUtility.UrlEncode(proxy) + "/" + HttpUtility.UrlEncode(Path.Replace("%", "%25").Replace("#", "%23").Replace("^", "<") + "|" + pageIndex + "|" + PageSize + "?uuid=" + uuid).Replace("+", "%20"));
            WebClient client = new WebClient();
            client.OpenReadCompleted += (o, a) =>
            {
                Log.Debug("get data end");
                if (a.Error == null)
                {
                    this.Clear();
                    //更新数据
                    JsonArray items = JsonValue.Load(a.Result) as JsonArray;
                    this.FromJson(items);
                    State = State.Loaded;
                }
                else
                {
                    State = State.LoadError;
                    Error = a.Error.GetMessage();
                }
                IsBusy = false;
                //通知数据加载完成
                OnDataLoaded(a);
                OnCompleted(a);
                Log.Debug("process data end");
            };
            IsBusy = true;
            OnLoading();
            State = State.StartLoad;
            Log.Debug("get data begin");
            client.OpenReadAsync(uri);
        }
        #endregion

        public void FromJson(JsonArray array)
        {
            //整个复制过程完成后，再通知列表发生变化了
            objects.CollectionChanged -= this.OnCollectionChanged;
            List<GeneralObject> delObjs = new List<GeneralObject>(this.objects);
            //新增或重新给对象赋值
            foreach (JsonObject obj in array)
            {
                GeneralObject temp = new GeneralObject();
                temp.FromJson(obj);
                this.Add(temp);
            }
            //删除不在获取的数据中的对象
            foreach (GeneralObject go in delObjs)
            {
                //空行数据不删除
                if (go == EmptyRow)
                {
                    continue;
                }
                this.objects.Remove(go);
            }
            //通知对象序号发生变化
            foreach (GeneralObject go in objects)
            {
                go.OnPropertyChanged("Index");
            }
            //修改状态为新
            //发送列表变化通知, 新增对象为列表本身
            NotifyCollectionChangedEventArgs args = new NotifyCollectionChangedEventArgs(NotifyCollectionChangedAction.Reset);
            this.OnCollectionChanged(args);
            //还原继续监听列表单个数据变化过程
            objects.CollectionChanged += this.OnCollectionChanged;
            IsOld = false;

        }

        public void Clear()
        {
            List<GeneralObject> list = new List<GeneralObject>(objects);
            foreach (GeneralObject go in list)
            {
                RemoveMonity(go);
            }
            this.objects.Clear();
            //OnPropertyChanged("Count");
            //重建空行
            if (HasEmptyRow)
            {
                CreateEmpty();
            }
        }



        private void OnCollectionChanged(object o, NotifyCollectionChangedEventArgs e)
        {
            //数据项发生变化，一定修改过
            IsModified = true;
            OnCollectionChanged(e);
        }

        public void Add(GeneralObject item)
        {
            item.List = this;
            //不需要空行处理，直接添加，否则，添加到空行前面
            if (EmptyRow == null || item == EmptyRow)
            {
                objects.Add(item);
                //OnPropertyChanged("Count");
            }
            else
            {
                int index = objects.IndexOf(EmptyRow);
                objects.Insert(index, item);
            }
            Monity(item);
            item.MonityList();
            //新加对象取列表的PropertySetter
            foreach (PropertySetter ps in this.PropertySetters)
            {
                PropertySetter nps = ps.Clone();
                nps.Object = item;
                item.PropertySetters.Add(nps);
                //对ps中的每一个表达式，复制出一份
                foreach (Exp exp in ps.Exps)
                {
                    Exp nexp = exp.Clone();
                    nexp._targetObject = nps;
                    //触发nps的Loaded事件，让新表达式开始解析
                    nexp.OnLoaded(nps, new RoutedEventArgs());
                }
            }
        }

        #region FromJson 从Json串转换总体信息
        /// <summary>
        /// 把求和等总体数据从json串转换为对象属性，其中包括Count内容。
        /// </summary>
        /// <param name="obj"></param>
        private void FromJson(JsonObject item)
        {
            if (item == null) return;
            foreach (string key in item.Keys)
            {
                //JsonPrimitive value = (JsonPrimitive)item[key];
                //this.NewGetType().GetProperty(key).SetValue(this, value as JsonPrimitive, null);
                object value = item[key];
                value = value.JsonConvert(this.NewGetType().GetProperty(key).PropertyType);
                this.NewGetType().GetProperty(key).SetValue(this, value, null);
            }
        }
        #endregion
    }

    /// <summary>
    /// 为分页对象专做的把Count转换成PagedCollectionView对象的转换器，在分页组件中，必须使用
    /// 这个转换器把分页列表的Count属性转换成其所需的PagedCollectionView。
    /// </summary>
    public class DataPagerConverter : IValueConverter
    {

        #region IValueConverter Members

        /// <summary>
        /// 数据转换过程，根据value值，构造一个自定义的可枚举对象，然后根据这个可枚举对象，构造一个
        /// PagedCollectionViewd对象。
        /// </summary>
        /// <param name="value">Count值</param>
        /// <param name="targetType"></param>
        /// <param name="parameter"></param>
        /// <param name="culture"></param>
        /// <returns>新的PagedCollectionView对象，以便翻页组件重新计算总页数</returns>
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            int v = (int)value;
            //没有数据，返回空数据源
            if (v == 0)
            {
                return null;
            }
            return new PagedCollectionView(new Enumrable(v));
        }

        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException();
        }

        #endregion

        /// <summary>
        /// 为翻页转换器专门做的可枚举对象，这个可枚举对象把Count当做自己的唯一属性
        /// 在获取枚举时，构造了一个专门的枚举对象，把Count作为参数传给该枚举对象。
        /// </summary>
        private class Enumrable : IEnumerable
        {
            private int count;
            public Enumrable(int count)
            {
                this.count = count;
            }
            #region IEnumerable Members

            public IEnumerator GetEnumerator()
            {
                return new Enumerator(count);
            }

            #endregion
        }

        /// <summary>
        /// 为翻页对象专门制作的枚举对象，该枚举对象有一个count属性代表总数，current属性代表当前号。每次迭代
        /// 时，current加1，直到等于count为止。
        /// </summary>
        private class Enumerator : IEnumerator
        {
            private int count;
            private int current = 0;

            public Enumerator(int count)
            {
                this.count = count;
            }

            #region IEnumerator Members

            /// <summary>
            /// 获取当前值，返回current;
            /// </summary>
            public object Current
            {
                get { return current; }
            }

            /// <summary>
            /// 往后迭代，这时current+1，如果到了最后，也就是current=count，返回false。
            /// </summary>
            /// <returns></returns>
            public bool MoveNext()
            {
                if (current < count)
                {
                    current++;
                    return true;
                }
                return false;
            }

            /// <summary>
            /// 重置current的值为0
            /// </summary>
            public void Reset()
            {
                current = 0;
            }

            #endregion
        }
    }
}
