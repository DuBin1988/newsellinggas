using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.Controls;
using Com.Aote.Utils;
using Com.Aote.ObjectTools;

namespace Com.Aote.Pages
{
    public partial class 收费信息列表 : UserControl
    {
        public 收费信息列表()
        {
            // Required to initialize variables
            InitializeComponent();
        }
        //双击事件预先定义变量
        DateTime lastClickTime = DateTime.Now;

        object lastClickItem;

         //收费信息列表事件
        // 模拟鼠标双击DataGridItem效果 
        private void dataGrid_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            //若两次双击时差小于250ms且点击的是同一项目则激发事件
            var nowClickItem = daninfos.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                //抛出datagrid双击事件
                OnDataItemDoubleClick(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot, "openchild1");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }


        //抄表记录
        private void dataGrid_MouseLeftButtonUp2(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = daninfos2.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                OnDataItemDoubleClick2(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick2(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot2, "openchild2");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }

        //换表记录信息
        private void dataGrid_MouseLeftButtonUp3(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = daninfos3.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                OnDataItemDoubleClick3(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick3(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot3, "openchild3");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }



        //发卡记录
        private void dataGrid_MouseLeftButtonUp4(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = fkinfos.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                OnDataItemDoubleClick4(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick4(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot4, "openchild4");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }

        //冲正记录
        private void dataGrid_MouseLeftButtonUp5(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = czinfos.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                OnDataItemDoubleClick5(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick5(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot5, "openchild5");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }

        //补卡记录
        private void dataGrid_MouseLeftButtonUp6(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = bkinfos.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                OnDataItemDoubleClick6(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick6(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot6, "openchild6");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }

        //补气记录
        private void dataGrid_MouseLeftButtonUp7(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = bqinfos.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                OnDataItemDoubleClick7(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick7(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot7, "openchild7");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }
    }
}