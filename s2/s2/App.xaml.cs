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
using Com.Aote.ObjectTools;
using Com.Aote.Pages;
using System.Threading;
using System.Globalization;
using System.Net.Browser;

namespace s2
{
    public partial class App : Application
    {
        public App()
        {
            //注册http和https请求，这样WebClient才能正确返回相应状态码
            WebRequest.RegisterPrefix("http://", WebRequestCreator.ClientHttp);
            WebRequest.RegisterPrefix("https://", WebRequestCreator.ClientHttp);


            if (App.Current.InstallState == InstallState.Installed)
            {
                App.Current.CheckAndDownloadUpdateCompleted += OnCheckAndDownloadUpdateCompleted;
                App.Current.CheckAndDownloadUpdateAsync();
               
            }
            this.Startup += (o, e) =>
            {
                this.RootVisual = new Com.Aote.Pages.Frame();
            };
            this.Exit += this.Application_Exit;
            this.UnhandledException += this.Application_UnhandledException;

            InitializeComponent();

            //日期格式化
            Thread.CurrentThread.CurrentCulture = (CultureInfo)Thread.CurrentThread.CurrentCulture.Clone();
            Thread.CurrentThread.CurrentCulture.DateTimeFormat.ShortDatePattern = "yyyy-MM-dd";
        }





        private void OnCheckAndDownloadUpdateCompleted(object sender, CheckAndDownloadUpdateCompletedEventArgs e)
        {

            if (e.UpdateAvailable && e.Error == null)
            {
               
                MessageBox.Show("检测到有新版本要更新，重启后生效。");

                Application.Current.MainWindow.Close();

            }
            else if (e.Error != null)
            {
                MessageBox.Show("在检测应用更新时, 在"
                             + "出现以下错误信息:"
                             + Environment.NewLine
                             + Environment.NewLine
                             + e.Error.Message);
            }
        }

        private void Application_Exit(object sender, EventArgs e)
        {
        }

        private void Application_UnhandledException(object sender, ApplicationUnhandledExceptionEventArgs e)
        {
            string errorMsg = e.ExceptionObject.Message + e.ExceptionObject.StackTrace;
            //errorMsg = errorMsg.Replace('"', '\'').Replace("\r\n", @"\n");
            e.Handled = false;
            MessageBox.Show(errorMsg);
        }

        private void ReportErrorToDOM(ApplicationUnhandledExceptionEventArgs e)
        {
            try
            {
                string errorMsg = e.ExceptionObject.Message + e.ExceptionObject.StackTrace;
                errorMsg = errorMsg.Replace('"', '\'').Replace("\r\n", @"\n");

                System.Windows.Browser.HtmlPage.Window.Eval("throw new Error(\"Unhandled Error in Silverlight Application " + errorMsg + "\");");
            }
            catch (Exception)
            {

            }
        }
    }
}
