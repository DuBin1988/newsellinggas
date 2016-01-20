using Com.Aote.ObjectTools;
using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Com.Aote.Pages
{
	public partial class 安检结果查看 : UserControl
	{
		public 安检结果查看()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        private void picture_MouseEnter(object sender, MouseEventArgs e)
        {
            Image image = sender as Image;
            if (image.Source == null)
                return;
            bigPic.Source = image.Source;
        }

        private void userfile_DataContextChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            //clear up suggestions
            suggestionPane.Children.Clear();
            GeneralObject go = (sender as Grid).DataContext as GeneralObject;
            if (go == null)
                return;
            String id = go.GetPropertyValue("id") as String;
            ObjectList lines = new ObjectList();
            lines.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            lines.EntityType = "T_INSPECTION_LINE";
            lines.LoadOnPathChanged = false;
            lines.Path = "from T_INSPECTION_LINE where INSPECTION_ID='" + id + "'order by EQUIPMENT";
            lines.DataLoaded += lines_DataLoaded;
            lines.Load();
        }

        private void lines_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            String[] devices = { "燃气表", "立管", "表后管", "灶具连接管", "灶具", "热水器", "壁挂炉", "其他隐患" };
            if (e.Error == null)
            {
                ObjectList lines = sender as ObjectList;
                if (lines.Size == 0)
                    return;
                foreach (String aDevice in devices)
                {
                    Border border = new Border();
                    border.Background = new SolidColorBrush(Colors.Blue);
                    border.Width = suggestionPane.Width;
                    TextBlock tb = new TextBlock();
                    border.Margin = new Thickness(10,5,0,0);
                    tb.Text = aDevice + "隐患选项";
                    tb.Foreground = new SolidColorBrush(Colors.White);
                    border.Child = tb;
                    StackPanel nullPane = new StackPanel();
                    nullPane.Orientation = Orientation.Vertical;
                    foreach (GeneralObject go in lines)
                    {
                        String device = go.GetPropertyValue("EQUIPMENT") as string;
                        if (aDevice.Equals(device))
                        {
                            TextBlock atb = new TextBlock();
                            atb.Text = go.GetPropertyValue("NAME") + ":" + go.GetPropertyValue("VALUE");
                            atb.Margin = new Thickness(30,5,0,0);
                            nullPane.Children.Add(atb);
                        }
                    }
                    if (nullPane.Children.Count > 0)
                    {
                        suggestionPane.Children.Add(border);
                        suggestionPane.Children.Add(nullPane);
                    }

                }
            }
        }


	}
}