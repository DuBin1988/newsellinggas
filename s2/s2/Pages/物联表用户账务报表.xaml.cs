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
    public partial class 物联表用户账务报表 : UserControl
	{
        public 物联表用户账务报表()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        private void f_inputdate_LostFocus(object sender, RoutedEventArgs e)
        {
            DateTime aa = (DateTime)f_inputdate.SelectedDate;
            f_inputdate.SelectedDate = DateTime.Parse(aa.Year + "-" + aa.Month + "-" + "26");
            f_inputdate_re.Text = DateTime.Parse(aa.Year + "-" + aa.Month + "-" + "26").AddMonths(-1).ToString();
        }

	}
}