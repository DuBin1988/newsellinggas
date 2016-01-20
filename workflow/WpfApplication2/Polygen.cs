using System;
using System.Collections.Generic;
using System.ComponentModel;
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

namespace workflow
{
    /// <summary>
    /// 按照步骤 1a 或 1b 操作，然后执行步骤 2 以在 XAML 文件中使用此自定义控件。
    ///
    /// 步骤 1a) 在当前项目中存在的 XAML 文件中使用该自定义控件。
    /// 将此 XmlNamespace 特性添加到要使用该特性的标记文件的根 
    /// 元素中: 
    ///
    ///     xmlns:MyNamespace="clr-namespace:workflow"
    ///
    ///
    /// 步骤 1b) 在其他项目中存在的 XAML 文件中使用该自定义控件。
    /// 将此 XmlNamespace 特性添加到要使用该特性的标记文件的根 
    /// 元素中: 
    ///
    ///     xmlns:MyNamespace="clr-namespace:workflow;assembly=workflow"
    ///
    /// 您还需要添加一个从 XAML 文件所在的项目到此项目的项目引用，
    /// 并重新生成以避免编译错误: 
    ///
    ///     在解决方案资源管理器中右击目标项目，然后依次单击
    ///     “添加引用”->“项目”->[浏览查找并选择此项目]
    ///
    ///
    /// 步骤 2)
    /// 继续操作并在 XAML 文件中使用控件。
    ///
    ///     <MyNamespace:Polygen/>
    ///
    /// </summary>
    public class Polygen : Control
    {
        //开始点
        public static DependencyProperty StartProperty = DependencyProperty.RegisterAttached(
           "Start", typeof(Point), typeof(Polygen), null);
        
        public Point Start
        {
            get { return (Point)GetValue(StartProperty); }
            set { SetValue(StartProperty, value); }
        }

        //结束点
        public static DependencyProperty EndProperty = DependencyProperty.RegisterAttached(
           "End", typeof(Point), typeof(Polygen), null);
        
        public Point End
        {
            get { return (Point)GetValue(EndProperty); }
            set { SetValue(EndProperty, value); }
        }


        Grid partMain;

        static Polygen()
        {
            DefaultStyleKeyProperty.OverrideMetadata(typeof(Polygen), new FrameworkPropertyMetadata(typeof(Polygen)));
        }

        public override void OnApplyTemplate()
        {
            base.OnApplyTemplate();

            this.partMain = GetTemplateChild("PART_MAIN") as Grid;
        }

        protected override Size ArrangeOverride(Size arrangeBounds)
        {
            partMain.Children.Clear();

            //从开始点画竖折线
            Line topLine = new Line()
            {
                X1 = Start.X,
                Y1 = Start.Y,
                X2 = Start.X,
                Y2 = End.Y,
                Stroke = new SolidColorBrush(Colors.Red),
                StrokeThickness = 2
            };
            //从竖折线结束到结束点画横线
            Line leftLine = new Line()
            {
                X1 = Start.X,
                Y1 = End.Y,
                X2 = End.X,
                Y2 = End.Y,
                Stroke = new SolidColorBrush(Colors.Red),
                StrokeThickness = 2
            };

            Line arrorLine1 = null;
            Line arrorLine2 = null;

            if (End.X > Start.X)
            {
                //结束点箭头上面部分
                arrorLine1 = new Line()
                {
                    X1 = End.X,
                    Y1 = End.Y,
                    X2 = End.X - 10,
                    Y2 = End.Y - 10,
                    Stroke = new SolidColorBrush(Colors.Red),
                    StrokeThickness = 2
                };
                //结束点箭头下面部分
                arrorLine2 = new Line()
                {
                    X1 = End.X,
                    Y1 = End.Y,
                    X2 = End.X - 10,
                    Y2 = End.Y + 10,
                    Stroke = new SolidColorBrush(Colors.Red),
                    StrokeThickness = 2
                };
            }
            else
            {
                //结束点箭头上面部分
                arrorLine1 = new Line()
                {
                    X1 = End.X,
                    Y1 = End.Y,
                    X2 = End.X + 10,
                    Y2 = End.Y - 10,
                    Stroke = new SolidColorBrush(Colors.Red),
                    StrokeThickness = 2
                };
                //结束点箭头下面部分
                arrorLine2 = new Line()
                {
                    X1 = End.X,
                    Y1 = End.Y,
                    X2 = End.X + 10,
                    Y2 = End.Y + 10,
                    Stroke = new SolidColorBrush(Colors.Red),
                    StrokeThickness = 2
                };
            }

            partMain.Children.Add(topLine);
            partMain.Children.Add(leftLine);
            partMain.Children.Add(arrorLine1);
            partMain.Children.Add(arrorLine2);

            return base.ArrangeOverride(arrangeBounds);
        }
    }
}
