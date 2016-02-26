using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;
using log4net.Config;
using System.IO;
using voice_card.helper;
using System.Data.OleDb;
using voice_card.db;
using System.Runtime.InteropServices;
using System.Threading;
using System.Diagnostics;

namespace voice_card
{
    /// <summary>
    /// 
    /// App.xaml 的交互逻辑
    /// </summary>
    public partial class App : Application
    {
        Window1 window1;

        protected override void OnStartup(StartupEventArgs e)
        {
            Process thisProc = Process.GetCurrentProcess();
            // Check how many total processes have the same name as the current one
            if (Process.GetProcessesByName(thisProc.ProcessName).Length > 1)
            {
                // If ther is more than one, than it is already running.
                MessageBox.Show("请不要重复运行本程序，服务系统可以切换用户查看是否在运行！");
                Application.Current.Shutdown();
                return;
            }else{
                base.OnStartup(e);
                log4net.Config.XmlConfigurator.Configure(new FileInfo("log4net.config"));
                String currentDir = Environment.CurrentDirectory;
                XmlService.load(currentDir+"\\config.xml");
                DBHelper.DBFactoryInit();

                Random r = new Random();
                for (int i = 0; i < 10; i++)
                {
                    int u = r.Next(5);
                    Console.WriteLine(u);
                }

                window1 = new Window1();
                if (e.Args.Length > 0)
                {
                    window1.ShowEx(window1,e.Args[0]);
                }else{ 
                    window1.Show();
                }
            }

            
        }

    }
}
