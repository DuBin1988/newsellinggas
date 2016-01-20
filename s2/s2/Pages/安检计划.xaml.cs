using System;
using System.Collections;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Automation.Peers;
using System.Windows.Automation.Provider;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.ObjectTools;

namespace Com.Aote.Pages
{
    public partial class 安检计划 : UserControl
    {
        public 安检计划()
        {
            // 为初始化变量所必需
            InitializeComponent();
        }

        private void alltoleft_Click(object sender, RoutedEventArgs e)
        {
            if (userfiles.ItemsSource == null)
                return;
            Com.Aote.ObjectTools.ObjectList c = userfiles.ItemsSource as Com.Aote.ObjectTools.ObjectList;
            Com.Aote.ObjectTools.ObjectList target = GetTarget();
            target.SelectObject = c;
            c.Clear();
        }

        private void toleft_Click(object sender, RoutedEventArgs e)
        {
            if (userfiles.SelectedItem == null)
                return;
            Com.Aote.ObjectTools.ObjectList c = userfiles.ItemsSource as Com.Aote.ObjectTools.ObjectList;
            IList list = new Com.Aote.ObjectTools.ObjectList();
            foreach (Object obj in userfiles.SelectedItems)
                list.Add(obj);
            Com.Aote.ObjectTools.ObjectList target = GetTarget();
            //if (target.selectObject == null)
            target.SelectObject = list;
            //else
            //(target.selectObject as IList).Add(userfiles.SelectedItem);

            foreach (Object obj in list)
                c.Remove(obj);
            c.OnPropertyChanged("Count");
        }

        private ObjectTools.ObjectList GetTarget()
        {
            System.Collections.Generic.IList<Com.Aote.ObjectTools.IName> list = this.pageResource.Res;
            foreach (Com.Aote.ObjectTools.IName name in list)
                if (name.Name.Equals("target"))
                    return name as ObjectTools.ObjectList;
            return null;
        }

        private void toright_Click(object sender, RoutedEventArgs e)
        {
            if (userfiles1.SelectedItem == null)
                return;
            Com.Aote.ObjectTools.ObjectList c = userfiles1.ItemsSource as Com.Aote.ObjectTools.ObjectList;
            GeneralObject checkPaper = userfiles1.SelectedItem as GeneralObject;
            GeneralObject address = new GeneralObject();
            address.EntityType = "t_userfiles";
            address.SetValue("ID", checkPaper.GetPropertyValue("pid"));
            address.SetValue("f_userid", checkPaper.GetPropertyValue("f_userid"));
            address.SetValue("f_districtname", checkPaper.GetPropertyValue("UNIT_NAME"));
            address.SetValue("f_road", checkPaper.GetPropertyValue("ROAD"));
            address.SetValue("f_cusDom", checkPaper.GetPropertyValue("CUS_DOM"));
            address.SetValue("f_cusDy", checkPaper.GetPropertyValue("CUS_DY"));
            address.SetValue("f_cusFloor", checkPaper.GetPropertyValue("CUS_FLOOR"));
            address.SetValue("f_apartment", checkPaper.GetPropertyValue("CUS_ROOM"));
            (userfiles.ItemsSource as ObjectList).Add(address);
            List<Object> list = new List<Object>();
            foreach (Object obj in userfiles1.SelectedItems)
                list.Add(obj);
            foreach (Object obj in list)
                c.Remove(obj);
            c.OnPropertyChanged("Count");
        }

        private void alltoright_Click(object sender, RoutedEventArgs e)
        {
            if (userfiles1.ItemsSource == null)
                return;
            Com.Aote.ObjectTools.ObjectList c = userfiles1.ItemsSource as Com.Aote.ObjectTools.ObjectList;
            Com.Aote.ObjectTools.ObjectList target = GetTarget();
            foreach (GeneralObject checkPaper in c)
            {
                GeneralObject address = new GeneralObject();
                address.EntityType = "t_userfiles";
                address.SetValue("ID", checkPaper.GetPropertyValue("pid"));
                address.SetValue("f_userid", checkPaper.GetPropertyValue("f_userid"));
                address.SetValue("f_districtname", checkPaper.GetPropertyValue("UNIT_NAME"));
                address.SetValue("f_road", checkPaper.GetPropertyValue("ROAD"));
                address.SetValue("f_cusDom", checkPaper.GetPropertyValue("CUS_DOM"));
                address.SetValue("f_cusDy", checkPaper.GetPropertyValue("CUS_DY"));
                address.SetValue("f_cusFloor", checkPaper.GetPropertyValue("CUS_FLOOR"));
                address.SetValue("f_apartment", checkPaper.GetPropertyValue("CUS_ROOM"));
                (userfiles.ItemsSource as ObjectList).Add(address);
            }
            c.Clear();
        }

    }
}