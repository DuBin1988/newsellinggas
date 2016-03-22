﻿using System;
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
using System.Net;
using System.Collections.Generic;
using System.Json;
using Com.Aote.Utils;

namespace Com.Aote.Pages
{
    public partial class 民用抄表批量走收录入 : UserControl
    {
        ObjectList list = new ObjectList();
        public 民用抄表批量走收录入()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        #region saveButton_Click 保存按钮按下时
        private void saveButton_Click(object sender, RoutedEventArgs e)
        {
            ui_handBusy.IsBusy = true;

            BaseObjectList list = daninfos.ItemsSource as BaseObjectList;

            ObjectList data = new ObjectList();
            //参数对象
            GeneralObject param = FrameworkElementExtension.FindResource(this.saveButton, "param") as GeneralObject;

            //对于每一条记录
            foreach (GeneralObject go in list)
            {
                //表状态
                var meterstate = meter.SelectedValue;

                // 抄表记录里的上期指数
                var lastinputnum = go.GetPropertyValue("lastinputgasnum");

                // 本次抄表指数
                var lastrecord = go.GetPropertyValue("lastrecord");

                // 本次指数为空，这条不上传
                if (lastrecord == null)
                {
                    continue;
                }
                // 本期指数小于上期指数，不上传
                if (double.Parse(lastrecord.ToString()) < double.Parse(lastinputnum.ToString()))
                {
                    continue;
                }
                go.CopyDataFrom(param);
                data.Add(go);


            }
            if (data.Count == 0) return;
            //将产生的json串送后台服务进行处理
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string uri = wci.BaseAddress + "/handcharge/record/payfeeforhand?uuid=" + System.Guid.NewGuid().ToString();
            WebClient client = new WebClient();
            client.UploadStringCompleted += client_UploadStringCompleted;
            client.UploadStringAsync(new Uri(uri), data.ToJson().ToString());
        }

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            ui_handBusy.IsBusy = false;
            if (e.Error == null)
            {
                //弹出错误信息
                if (e.Result != "")
                {
                    MessageBox.Show(e.Result);
                }
                dansearchbutton_Click(null, null);
            }
            //有错误
            if (e.Error != null)
            {
                MessageBox.Show("上次报错数据有错，请重新查询！");
            }
        }
        #endregion

        private void countGas(object sender, RoutedEventArgs e)
        {
            //ui_handBusy.IsBusy = true;
            try
            {
                TextBox box = sender as TextBox;
                GeneralObject go = box.DataContext as GeneralObject;

                //上期底数从数据对象取
                double lastinputgasnum = double.Parse(go.GetPropertyValue("lastinputgasnum").ToString());
                if (box.Text == null || box.Text == "")
                {
                    go.SetPropertyValue("oughtamount", null, false);
                }
                //由于焦点离开时，数据未传递到对象中，从界面取
                double lastrecord = double.Parse(box.Text);

                //设置气量
                double oughtamount = lastrecord - lastinputgasnum;
                go.SetPropertyValue("oughtamount", oughtamount, false);
                //获取应交金额
                //用户编号
                string f_userinfoid = go.GetPropertyValue("f_userinfoid") + "";
                WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
                SystemTime systemTime =FrameworkElementExtension.FindResource(this.saveButton,"SysTime") as SystemTime;
                Uri uri = new Uri(wci.BaseAddress + "/handcharge/num/" + f_userinfoid + "/" + oughtamount + "/" + ((DateTime)systemTime.Now).ToString("yyyyMMdd") + "?uuid=" + System.Guid.NewGuid().ToString());
                WebClient client = new WebClient();
                client.OpenReadCompleted += (o, a) =>
                {
                    if (a.Error == null)
                    {
                        JsonObject json = JsonValue.Load(a.Result) as JsonObject;
                        double chargenum = (double)json["f_chargenum"];
                        double f_zhye = double.Parse(go.GetPropertyValue("f_zhye").ToString());
                        go.SetPropertyValue("gasfee", chargenum, false);
                        double oughtfee = chargenum - f_zhye;
                        if (oughtfee < 0) oughtfee = 0;
                        go.SetPropertyValue("oughtfee", oughtfee, true);
                        go.SetPropertyValue("f_grossproceeds", oughtfee+"", true);
                        go.FromJson(json);
                    }
                    ui_handBusy.IsBusy = false;
                };
                client.OpenReadAsync(uri);
            }
            catch (Exception ex)
            {
                ui_handBusy.IsBusy = false;
            }
        }

        #region 查询按钮处理过程
        // 查询按钮处理过程
        private void dansearchbutton_Click(object sender, RoutedEventArgs e)
        {
            ui_handBusy.IsBusy = true;
            // 掉用search对象的search方法，产生条件
            SearchObject search = daninfosearch.DataContext as SearchObject;
            search.Search();

            // 调用服务
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string uri = wci.BaseAddress + "/handcharge/download" + "?uuid=" + System.Guid.NewGuid().ToString();
            WebClient client = new WebClient();
            client.UploadStringCompleted += dansearch_UploadStringCompleted;
            client.UploadStringAsync(new Uri(uri), search.Condition);
        }

        void dansearch_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            //有错误
            if (e.Error != null)
            {
                MessageBox.Show(e.Error.Message);
            }
            else
            {

                //把数据转换成JSON
                JsonArray items = JsonValue.Parse(e.Result) as JsonArray;
                daninfos.ItemsSource = list;
                if (list.Size != 0)
                {
                    list.New();
                }
                foreach (JsonObject json in items)
                {
                    GeneralObject go = new GeneralObject();
                    try
                    {
                        go.EntityType = "t_handplan";
                        string f_userinfoid = (string)json["f_userinfoid"];//用户编号
                        go.SetPropertyValue("f_userinfoid", f_userinfoid, false);
                        string f_userid = (string)json["f_userid"];//表编号
                        go.SetPropertyValue("f_userid", f_userid, false);
                        string f_username = (string)json["f_username"];//用户名
                        go.SetPropertyValue("f_username", f_username, false);
                        string f_address = (string)json["f_address"];//地址
                        go.SetPropertyValue("f_address", f_address, false);
                        decimal lastinputgasnum = (decimal)json["lastinputgasnum"];//上期指数
                        go.SetPropertyValue("lastinputgasnum", lastinputgasnum, false);
                        decimal f_zhye = (decimal)json["f_zhye"];
                        go.SetPropertyValue("f_zhye", f_zhye, false);
                        decimal id = (decimal)json["id"];
                        go.SetPropertyValue("id", id, false);
                        list.Add(go);
                    }
                    catch (Exception ex)
                    {
                        ui_handBusy.IsBusy = false;
                        MessageBox.Show("用户:" + go.GetPropertyValue("f_userid") + "抄表数据有问题,请核查!");
                        MessageBox.Show(ex.ToString() + "" + go.GetPropertyValue("f_userid"));
                        return;
                    }
                }
            }
            ui_handBusy.IsBusy = false;
        }

        #endregion
    }
}
