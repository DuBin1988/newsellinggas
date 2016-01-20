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

namespace Workflow
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        private string tool = "";

        private Diagram diagram = new Diagram();
        Actor actor;
        public MainWindow()
        {
            InitializeComponent();

            //ui_line.Start = new Point(0, 0);
            //ui_line.End = new Point(50, 50);

            //测试数据
            actor = new Actor(diagram) { acName = "abc" };
            Activity activity = new Activity(actor) { ActivityName = "ddd", Pos = 100  };
            actor.AddAactivity(activity);

            Actor act = new Actor(diagram) { acName = "cba" };
            Activity activity1 = new Activity(act) { ActivityName = "1231", Pos = 300 };
            act.AddAactivity(activity1);
            diagram.AddActors(actor);
            diagram.AddActors(act);

            Transfer transtar = new Transfer(activity,activity1);
            diagram.AddTransfer(transtar);

            ui_Diagram.DataContext = diagram;
            //ui_ActorListBox.ItemsSource = diagram.Actors;
            //ui_Transfer.ItemsSource = diagram.Transfer;
        }

        private void ui_NewServer_Click(object sender, RoutedEventArgs e)
        {
            tool = "添加活动";
        }

        //执行者活动区域鼠标点击事件
        private void Grid_MouseDown(object sender, MouseEventArgs e)
        {
            Grid grid = sender as Grid;
            Point p = Mouse.GetPosition(grid); 
            Actor act = grid.DataContext as Actor;
            if (tool == "添加活动")
            {
                if (p.X <= grid.Width)
                {
                    Activity activity = new Activity(act) { ActivityName = "新活动", Pos = p.X };
                    act.AddAactivity(activity);
                    tool = "";
                }
                else
                {
                    MessageBox.Show("请点击相应的执行者！");
                }
            }
        }

        //画线时的开始活动
        private Activity lineStart;

        //活动鼠标点击事件
        private void Activity_MouseDown(object sender, MouseEventArgs e)
        {
            FrameworkElement startElement = sender as FrameworkElement;
            lineStart = startElement.DataContext as Activity;
        }
        
        //活动鼠标释放事件
        private void Activity_MouseUp(object sender, MouseEventArgs e)
        {
            if (lineStart == null)
            {
                return;
            }

            FrameworkElement startElement = sender as FrameworkElement;
            Activity lineEnd = startElement.DataContext as Activity;

            Transfer trans = new Transfer(lineStart, lineEnd);
            diagram.AddTransfer(trans);

            lineStart = null;
        }

        private void ui_NewActor_Click(object sender, RoutedEventArgs e)
        {
            //tool = "添加执行者";
            Actor act = new Actor(diagram) { acName = "1231" };
            diagram.AddActors(act);
        }

        private void ui_DelActor_Click(object sender, RoutedEventArgs e)
        {
            //tool = "删除执行者";
            Actor actor = ui_ActorListBox.SelectedItem as Actor;
            diagram.DelActors(actor);
        }

    }
}
