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
using Com.Aote.ObjectTools;
using Com.Aote.Utils;

namespace Com.Aote.Pages
{
    public partial class 用户信息 : UserControl
    {
        public 用户信息()
        {
            // Required to initialize variables
            InitializeComponent();
            this.Loaded += 用户信息_Loaded;
            kbfee = (ObjectList)(from r in loader.Res where r.Name.Equals("SecondStairlist") select r).First();
            ThirdStairStairlist1 = (ObjectList)(from r in loader.Res where r.Name.Equals("ThirdStairStairlist") select r).First();
            FourthStairlist1 = (ObjectList)(from r in loader.Res where r.Name.Equals("FourthStairlist") select r).First();
            czylistnull1 = (ObjectList)(from r in loader.Res where r.Name.Equals("czylistnull") select r).First();

        }
        ObjectList kbfee;
        ObjectList ThirdStairStairlist1;
        ObjectList FourthStairlist1;
        ObjectList czylistnull1;
        GeneralObject loginUser;
        void 用户信息_Loaded(object sender, RoutedEventArgs e)
        {
            loginUser = (GeneralObject)FrameworkElementExtension.FindResource(this, "LoginUser");

            kbfee.DataLoaded += kbfee_DataLoaded;
            ThirdStairStairlist1.DataLoaded += ThirdStairStairlist1_DataLoaded;
            FourthStairlist1.DataLoaded += FourthStairlist1_DataLoaded;

        }
        void ThirdStairStairlist1_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (ThirdStairStairlist1.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 3)
                {
                    ThirdStair.SelectedValue = str[2];
                }

            }
        }

        void FourthStairlist1_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (FourthStairlist1.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 4)
                {
                    FourthStair.SelectedValue = str[3];
                }

            }
        }

        void kbfee_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (kbfee.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 2)
                {
                    SecondStair.SelectedValue = str[1];
                }

            }
        }

        private void dansearchbutton_Click(object sender, RoutedEventArgs e)
        {
            SearchObject so = daninfosearch.DataContext as SearchObject;
            if (null == so.GetPropertyValue("SecondStair"))
            {
                so.SetPropertyValue("org", so.GetPropertyValue("FirstStair"), false);
            }
            else
            {
                if (null == so.GetPropertyValue("ThirdStair"))
                {
                    so.SetPropertyValue("org", so.GetPropertyValue("FirstStair") + "." + so.GetPropertyValue("SecondStair"), false);
                }
                else
                {
                    if (null == so.GetPropertyValue("FourthStair"))
                    {
                        so.SetPropertyValue("org", so.GetPropertyValue("FirstStair") + "." + so.GetPropertyValue("SecondStair") + "." + so.GetPropertyValue("ThirdStair"), false);
                    }
                    else
                    {
                        if (null == so.GetPropertyValue("f_sgoperator"))
                        {
                            so.SetPropertyValue("org", so.GetPropertyValue("FirstStair") + "." + so.GetPropertyValue("SecondStair") + "." + so.GetPropertyValue("ThirdStair") + "." + so.GetPropertyValue("FourthStair"), false);
                        }
                    }
                }
            }
            so.Search();
        }
    }
}