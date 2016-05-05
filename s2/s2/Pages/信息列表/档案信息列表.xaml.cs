using Com.Aote.Behaviors;
using Com.Aote.ObjectTools;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.Controls;
using Com.Aote.Utils;


namespace Com.Aote.Pages
{
    public partial class 档案信息列表 : UserControl
    { 

        public 档案信息列表()
        {
            // Required to initialize variables
            InitializeComponent(); 
 
        }
        DateTime lastClickTime = DateTime.Now;

        object lastClickItem;
        private void dataGrid_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = daninfos.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
              
                OnDataItemDoubleClick(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot, "openchild2");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }

        //小区信息
        private void dataGrid_MouseLeftButtonUp2(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = userfiles.SelectedItem;
            if (diff < 250 && lastClickItem == nowClickItem)
            {
                OnDataItemDoubleClick2(nowClickItem);
            }
            lastClickItem = nowClickItem;
        }
        protected virtual void OnDataItemDoubleClick2(object sender)
        {
            ChildWindowObj childpage = (ChildWindowObj)FrameworkElementExtension.FindResource(LayoutRoot2, "openchild1");
            childpage.ParamObj = sender;
            childpage.IsOpen = true;

        }

       


        //单位信息
        private void dataGrid_MouseLeftButtonUp3(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = unitinfos.SelectedItem;
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

        //单表信息
        private void dataGrid_MouseLeftButtonUp4(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = unitinfos1.SelectedItem;
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
        //街道信息
        private void dataGrid_MouseLeftButtonUp5(object sender, MouseButtonEventArgs e)
        {
            var eventSource = e.OriginalSource;
            var now = DateTime.Now;
            var ts = now.Subtract(lastClickTime);
            var diff = ts.TotalMilliseconds;
            lastClickTime = now;
            var nowClickItem = unitinfos2.SelectedItem;
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

      
    }
}