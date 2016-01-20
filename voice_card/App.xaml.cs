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

namespace voice_card
{
    /// <summary>
    /// 
    /// App.xaml 的交互逻辑
    /// </summary>
    public partial class App : Application
    {
        protected override void OnStartup(StartupEventArgs e)
        {
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
            
        }
    }
}
