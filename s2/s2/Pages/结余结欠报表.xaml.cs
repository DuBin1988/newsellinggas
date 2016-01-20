using Com.Aote.ObjectTools;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Com.Aote.Pages
{
    public partial class 结余结欠报表 : UserControl
    {
        //结余
        private PagedList jyList = new PagedList();

        //欠费
        private PagedList qfList = new PagedList();

        //保存的信息
        private PagedList detailList = new PagedList();

        private int sellEvent = 0;
        private double myjbzhye = 0;
        private double mykbzhye = 0;
        private double fmyjbzhye = 0;
        private double fmykbzhye = 0;
        private double myjbqf = 0;
        private double fmyjbqf = 0;
        private string date = "";
        private string now = "";
        private int count;
        public 结余结欠报表()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            ui_SearchBusy.IsBusy = true;
            if(tj1.SelectedDate==null){
                MessageBox.Show("请选择日期！");
                ui_SearchBusy.IsBusy = false;
                return;
            }
            count = 0;
            date = tj1.SelectedDate.ToString().Substring(0,10);
            now = DateTime.Now.ToString("yyyy-MM-dd");
            if (tj1.SelectedDate > DateTime.Now)
            {
                MessageBox.Show("请选择正确的时间！");
                ui_SearchBusy.IsBusy = false;
                return;
            }
            string sqla = "select f_tjdate,f_civiljb,f_unciviljb,f_civilkb,f_uncivilkb,f_sumfee" +
                    " from t_qianfeijieyu where f_tjdate='" + date + "'";
            detailList.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            detailList.LoadOnPathChanged = false;
            detailList.Path = "sql";
            detailList.HQL = sqla;
            detailList.PageSize = 1000;
            detailList.DataLoaded += detailList_DataLoaded;
            detailList.PageIndex = 0;
        }

        private void detailList_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (detailList.Size != 0)
            {
                foreach (GeneralObject go in detailList)
                {
                    ui_tjdate.Text = go.GetPropertyValue("f_tjdate").ToString();
                    ui_myjb.Text = go.GetPropertyValue("f_civiljb").ToString();
                    ui_mykb.Text = go.GetPropertyValue("f_civilkb").ToString();
                    ui_fmyjb.Text = go.GetPropertyValue("f_unciviljb").ToString();
                    ui_fmykb.Text = go.GetPropertyValue("f_uncivilkb").ToString();
                    ui_sumfee.Text = go.GetPropertyValue("f_sumfee").ToString();
                }
                ui_SearchBusy.IsBusy = false;
            }
            else 
            {
                if (date.Equals(now))
                {
                    //查询结余情况
                    string sql = "select isnull(ROUND(SUM(f_zhye),2),0)f_zhye,ISNULL(f_usertype,'民用')f_usertype," +
                        " isnull(f_gasmeterstyle,'机表')f_gasmeterstyle from t_userfiles where f_userstate='正常'" +
                        " group by f_usertype,f_gasmeterstyle";
                    jyList.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    jyList.LoadOnPathChanged = false;
                    jyList.Path = "sql";
                    jyList.HQL = sql;
                    jyList.PageSize = 1000;
                    jyList.DataLoaded += jyList_DataLoaded;
                    jyList.PageIndex = 0;

                    //查询欠费情况
                    string sql1 = "select ROUND(SUM(oughtfee),2)oughtfee,f_usertype from t_handplan " +
                        " where shifoujiaofei='否'group by f_usertype";
                    qfList.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    qfList.LoadOnPathChanged = false;
                    qfList.Path = "sql";
                    qfList.HQL = sql1;
                    qfList.PageSize = 1000;
                    qfList.DataLoaded += qfList_DataLoaded;
                    qfList.PageIndex = 0;
                }
                else
                {
                    count++;
                    if (count == 1)
                    {
                        MessageBox.Show("该日期未保存结余欠费信息！");
                        ui_SearchBusy.IsBusy = false;
                        return;
                    }
                }
            }
        }
        

        private void qfList_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            var ol = (from p in qfList where p.GetPropertyValue("f_usertype").Equals("民用") select p);
            myjbqf = ol.Sum(o => double.Parse(o.GetPropertyValue("oughtfee").ToString()));
            ol = (from p in qfList where p.GetPropertyValue("f_usertype").Equals("非民用") select p);
            myjbqf = ol.Sum(o => double.Parse(o.GetPropertyValue("oughtfee").ToString()));

            //如果都回来了，求总和
            sellEvent++;
            if (sellEvent == 2)
            {
                SumFee();
            }

        }

        private void jyList_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            var ol = (from p in jyList
                      where p.GetPropertyValue("f_usertype").Equals("民用") && p.GetPropertyValue("f_gasmeterstyle").Equals("机表")
                      select p);
            myjbzhye = ol.Sum(o => double.Parse(o.GetPropertyValue("f_zhye").ToString()));
            ol = (from p in jyList
                      where p.GetPropertyValue("f_usertype").Equals("民用") && p.GetPropertyValue("f_gasmeterstyle").Equals("卡表")
                      select p);
            mykbzhye = ol.Sum(o => double.Parse(o.GetPropertyValue("f_zhye").ToString()));
            ol = (from p in jyList
                  where p.GetPropertyValue("f_usertype").Equals("非民用") && p.GetPropertyValue("f_gasmeterstyle").Equals("机表")
                  select p);
            fmyjbzhye = ol.Sum(o => double.Parse(o.GetPropertyValue("f_zhye").ToString()));
            ol = (from p in jyList
                  where p.GetPropertyValue("f_usertype").Equals("非民用") && p.GetPropertyValue("f_gasmeterstyle").Equals("卡表")
                  select p);
            fmykbzhye = ol.Sum(o => double.Parse(o.GetPropertyValue("f_zhye").ToString()));
            //如果都回来了，求总和
            sellEvent++;
            if (sellEvent == 2)
            {
                SumFee();
            }
        }
        private void SumFee()
        {
            ui_myjb.Text = (myjbzhye - myjbqf).ToString();
            ui_mykb.Text = mykbzhye.ToString();
            ui_fmykb.Text = fmykbzhye.ToString();
            ui_fmyjb.Text = (fmyjbzhye - fmyjbqf).ToString();
            ui_sumfee.Text = ((myjbzhye - myjbqf) + mykbzhye + fmykbzhye + (fmyjbzhye - fmyjbqf)).ToString();
            ui_tjdate.Text = date;

            GeneralObject obj = new GeneralObject();
            obj.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            obj.EntityType = "t_qianfeijieyu";
            obj.SetPropertyValue("f_tjdate", date, false);
            obj.SetPropertyValue("f_civiljb", myjbzhye - myjbqf, false);
            obj.SetPropertyValue("f_unciviljb", fmyjbzhye - fmyjbqf, false);
            obj.SetPropertyValue("f_civilkb", mykbzhye, false);
            obj.SetPropertyValue("f_uncivilkb", fmykbzhye, false);
            obj.SetPropertyValue("f_sumfee", (myjbzhye - myjbqf) + mykbzhye + fmykbzhye + (fmyjbzhye - fmyjbqf), false);
            obj.Name = "t_qianfeijieyu";
            obj.Completed += obj_Completed;
            obj.Save();
        }

        private void obj_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ui_SearchBusy.IsBusy = false;
        }

    }
}
