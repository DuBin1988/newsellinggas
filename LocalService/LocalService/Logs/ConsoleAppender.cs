using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using LocalService;

namespace Com.Aote.Logs
{
    //在主窗口的信息去显示信息
    class ConsoleAppender : DependencyObject, IAppender
    {
        delegate void ShowMsgDelegate(string msg);

        public void ShowMessage(string msg)
        {
            ShowMsgDelegate d = new ShowMsgDelegate(ShowMsg);
            this.Dispatcher.BeginInvoke(d, msg);
        }

        public void ShowMsg(string msg)
        {
            MainWindow win = (MainWindow)Application.Current.MainWindow;
            win.ShowMessage(msg);
        }
    }
}
