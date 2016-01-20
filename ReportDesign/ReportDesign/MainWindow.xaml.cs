using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Markup;
using Microsoft.Win32;

namespace WpfApplication1
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();

            ui_sqllist.ItemsSource = ui_report.sqls;
        }

       /* public void win_LoadedEvent(object sender, RoutedEventArgs e)
        {
            foreach (FontFamily _f in Fonts.SystemFontFamilies)
            {
                LanguageSpecificStringDictionary _fontDic = _f.FamilyNames;
                if (_fontDic.ContainsKey(XmlLanguage.GetLanguage("zh-cn")))
                {
                    string _fontName = null;
                    if (_fontDic.TryGetValue(XmlLanguage.GetLanguage("zh-cn"), out _fontName))
                    {
                       // cbo_Demo.Items.Add(_fontName);
                    }
                }
            }
        }*/
        private void ui_compose_Click(object sender, RoutedEventArgs e)
        {
            ui_report.ComposeCell();
        }

        private void ui_decompose_Click(object sender, RoutedEventArgs e)
        {
            ui_report.DecomposeCell();
        }

        private void ui_insertrow_Click(object sender, RoutedEventArgs e)
        {
            ui_report.InsertRow();
        }

        private void ui_insertcolumn_Click(object sender, RoutedEventArgs e)
        {
            ui_report.InsertColumn();
        }

        private void ui_deleterow_Click(object sender, RoutedEventArgs e)
        {
            ui_report.DeleteRow();
        }

        private void ui_setleft_Click(object sender, RoutedEventArgs e)
        {
            ui_report.setleft();
        }

        private void ui_setcenter_Click(object sender, RoutedEventArgs e)
        {
            ui_report.setcenter();
        }

        private void ui_setright_Click(object sender, RoutedEventArgs e)
        {
            ui_report.setright();
        }

        private void ui_biaotou_Click(object sender, RoutedEventArgs e)
        {
            ui_report.markhead();
        }

        private void ui_font_Selected(object sender, RoutedEventArgs e)
        {
            ui_report.setfont();
          
            
        }
        private void ui_fontsize_Selected(object sender, RoutedEventArgs e)
        {
            ui_report.setfontsize();
        }

        private void ui_save_Click(object sender, RoutedEventArgs e)
        {
            //打开文件对话框
            SaveFileDialog dialog = new SaveFileDialog();
            dialog.DefaultExt = "rpt";
            dialog.ShowDialog();
            if (dialog.FileName != "")
            {
                ui_report.Save(dialog.FileName);
            }
        }

        private void ui_load_Click(object sender, RoutedEventArgs e)
        {
            //打开文件对话框
            OpenFileDialog dialog = new OpenFileDialog();
            dialog.DefaultExt = "rpt";
            dialog.ShowDialog();
            if (dialog.FileName != "")
            {
                ui_report.Load(dialog.FileName);
            }
        }

        //新增一条sql语句
        private void ui_insertsql_Click(object sender, RoutedEventArgs e)
        {
            ui_report.AddSql();
        }

        private void ui_left_Click(object sender, RoutedEventArgs e)
        {
            ui_report.markleft();
        }

        private void ui_main_Click(object sender, RoutedEventArgs e)
        {
            ui_report.markmain();
        }

        private void ui_bottom_Click(object sender, RoutedEventArgs e)
        {
            ui_report.markbottom();
        }

        private void ui_drop_Click(object sender, RoutedEventArgs e)
        {
            ui_report.markdrop();
        }

        private void ui_head_Click(object sender, RoutedEventArgs e)
        {
            ui_report.markheadchange();
        }
    }
}
