using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Linq;
using System.Windows.Controls.Primitives;
using Com.Aote.ObjectTools;

namespace Com.Aote.Pages
{
    public partial class 物联表用户每日交费对账表 : UserControl
	{
        PagedObjectList pl = new PagedObjectList();
        public 物联表用户每日交费对账表()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        private void Button_Click1(object sender, RoutedEventArgs e)
        {
            pl = daninfos.ItemsSource as PagedObjectList;
            pl.Save();

        }

	}
}