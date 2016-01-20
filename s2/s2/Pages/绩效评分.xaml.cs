﻿using System;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.ObjectTools;

namespace s2.Pages
{
    public partial class 绩效评分 : UserControl
    {
        public 绩效评分()
        {
            InitializeComponent();
        }

        private void btnSearch_Click(object sender, RoutedEventArgs e)
        {
            SearchObject conditions = (criteriaPanel.DataContext as SearchObject);
            conditions.Search();
			WebClientInfo wci = App.Current.Resources["dbclient"] as WebClientInfo;
            String where = conditions.Condition;
            String dt = "1=1";
            if (StartDate.Text.Trim().Length != 0)
                dt = " DEPARTURE_TIME>='" + StartDate.Text + "'";
            if (EndDate.Text.Trim().Length != 0)
            {
                if(dt.Length>3)
                    dt += " and DEPARTURE_TIME<='" + EndDate.Text + " 23:59:59'";
                else
                    dt = " DEPARTURE_TIME<='" + EndDate.Text + " 23:59:59'";
            }   
            checkerList.Path = "sql";
            checkerList.Names = "save_people,days,papers,notifications,notins,defects,leakages";
            String sql = @"SELECT t0.save_people,days,papers,notifications,notins,defects,leakages FROM(SELECT save_people,SUM (ruhu) papers,COUNT (DISTINCT dt) days,SUM (notin) notins FROM(SELECT id,save_people,SUBSTRING (departure_time, 1, 10) dt,CAST (CASE condition WHEN '正常' THEN 1 ELSE 0 END AS INTEGER) ruhu,CAST (CASE condition WHEN '无人' THEN 1 ELSE 0 END AS INTEGER) notin FROM T_INSPECTION WHERE 1 = 1 AND {0} AND {1}) b GROUP BY save_people) t0 LEFT JOIN (SELECT save_people,COUNT (id) leakages FROM(SELECT id, save_people FROM T_INSPECTION WHERE (f_qblouqi = 1 OR f_jpglouqi = 1 OR f_lglouqi = 1 OR f_cooker_leakage = 1 OR f_heater_leakage = 1 OR f_furnace_leakage = 1 OR f_bhglouqi = 1 OR f_biaoqianfa = '漏气' OR f_zaoqianfa = '漏气' OR f_zibifa = '漏气' OR f_zjxianzhuang = '漏气' OR f_rshqxianzhuang = '漏气' OR f_bglxianzhuang = '漏气') AND {0} AND {1}) a GROUP BY	save_people) t1 ON t0.save_people = t1.save_people LEFT JOIN (SELECT save_people,COUNT (id) notifications FROM(SELECT id, save_people FROM T_INSPECTION WHERE f_renow_id IS NOT NULL AND f_renow_id <> '' AND {0} AND {1}) c GROUP BY save_people) t2 ON t0.save_people = t2.save_people LEFT JOIN (SELECT save_people,COUNT (id) defects FROM(SELECT id,save_people FROM T_INSPECTION WHERE (f_sibiao = 1 OR f_changtong = 1 OR f_fanzhuang = 1 OR f_qblouqi = 1 OR f_reading_mismatch = 1 OR (f_qibiao IS NOT NULL AND f_qibiao <> '')) AND {0} AND {1}) d GROUP BY save_people) t3 ON t0.save_people = t3.save_people";
            checkerList.HQL = String.Format(sql, new String[]{where, dt});
            checkerList.Load();
        }


        private void picture_MouseEnter(object sender, MouseEventArgs e)
        {
            Image image = sender as Image;
            if (image.Source == null)
                return;
            bigPic.Source = image.Source;
        }

        /// <summary>
        /// 安检单列表选择改变
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void slipGrid_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            GeneralObject item = slipGrid.SelectedItem as GeneralObject;
            if (item == null)
                return;
            GeneralObject go = new GeneralObject();
            go.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            go.EntityType = "T_INSPECTION";
            if (!item.GetPropertyValue("CONDITION").Equals("未检"))
            {
                go.Path = "one/select distinct t from T_INSPECTION t left join fetch t.LINES left join fetch t.CHECKPLAN_ID where t.id ='" + item.GetPropertyValue("id").ToString() + "'";
                go.DataLoaded += go_DataLoaded;
                //if (!item.GetPropertyValue("CONDITION").Equals("正常"))
                //    userfile.IsEnabled = false;
                //else
                //    userfile.IsEnabled = true;
                go.Load();
            }
            else
            {
                userfile.DataContext = null;
                ClearAll();
                //userfile.IsEnabled = false;
            }
        }

        void go_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            (sender as GeneralObject).DataLoaded -= go_DataLoaded;
            transformData(sender as GeneralObject);
            userfile.DataContext = sender;
        }

        private void transformData(GeneralObject go)
        {
            ClearAll();

            if (go == null)
                return;

            PostUITask(go);
        }

        private void ClearAll()
        {
            //清除以前的选择
            ClearChecks();
            bigPic.Source = null;
            CurrentArchiveAddress.Text = "";
            //WARM_OTHER.Text = "";
            //JB_METER_NAME_OTHER.Text = "";
            //IC_METER_NAME_OTHER.Text = "";
            //WARM.SelectedItem = null;
            //IC_METER_NAME.SelectedItem = null;
            //JB_METER_NAME.SelectedItem = null;
        }

        private void ClearChecks()
        {
            //Panel[] panels = { MeterDefectsPane,  PlumbingDefectsPane, PlumbingProofPane, PlumbingPressurePane, PlumbingMeterValvePane, PlumbingCookerValvePane, PlumbingAutomaticValvePane, PlumbingPipePane, PlumbingLeakagePane, 
            //                     CookerPipePane, BoilerPipePane, BoilerDefectsPane, WHEDefectsPane, precautionCheckPane };
            //foreach (Panel p in panels)
            //{
            //    foreach (UIElement element in p.Children)
            //    {
            //        if (element is CheckBox && !(element as CheckBox).Content.ToString().Equals("正常") && !(element as CheckBox).Content.ToString().Equals("无"))
            //        {
            //            if (element == cbRIGIDITYLeakage || element == cbRIGIDITYNormal || element == cbPressureNormal || element == cbPressureAbnormal ||
            //                element == cbLEAKAGE_COOKER || element == cbLEAKAGE_BOILER || element == cbLEAKAGE_HEATER || element == cbLEAKAGE_NOTIFIED)
            //                continue;
            //            (element as CheckBox).IsChecked = false;
            //        }
            //        if (element is RadioButton)
            //            (element as RadioButton).IsChecked = false;
            //    }
            //}
        }


        private void PostUITask(GeneralObject go)
        {
            if (go.GetPropertyValue("CONDITION").ToString().Equals("正常"))
            {
            //    //供暖方式
            //    ObjectCollection oc = this.Resources["WARM"] as ObjectCollection;
            //    bool found = false;
            //    foreach (Pair pair in oc)
            //    {
            //        if (pair.CName.Equals(go.GetPropertyValue("WARM").ToString()))
            //        {
            //            found = true;
            //            WARM.SelectedItem = pair;
            //        }
            //    }
            //    if (!found)
            //    {
            //        WARM_OTHER.Text = go.GetPropertyValue("WARM").ToString();
            //        WARM.SelectedIndex = oc.Count - 1;
            //    }

            //    //基表厂家型号
            //    oc = this.Resources["JB_METER_NAME"] as ObjectCollection;
            //    found = false;
            //    foreach (Pair pair in oc)
            //    {
            //        if (pair.CName.Equals(go.GetPropertyValue("JB_METER_NAME").ToString()))
            //        {
            //            JB_METER_NAME.SelectedItem = pair;
            //            found = true;
            //        }
            //    }
            //    if (!found)
            //    {
            //        JB_METER_NAME_OTHER.Text = go.GetPropertyValue("JB_METER_NAME").ToString();
            //        JB_METER_NAME.SelectedIndex = oc.Count - 1;
            //    }

            //    //IC卡表厂家型号
            //    oc = this.Resources["IC_METER_NAME"] as ObjectCollection;
            //    found = false;
            //    foreach (Pair pair in oc)
            //    {
            //        if (pair.CName.Equals(go.GetPropertyValue("IC_METER_NAME").ToString()))
            //        {
            //            found = true;
            //            IC_METER_NAME.SelectedItem = pair;
            //        }
            //    }
            //    if (!found)
            //    {
            //        IC_METER_NAME_OTHER.Text = go.GetPropertyValue("IC_METER_NAME").ToString();
            //        go.SetPropertyValue("IC_METER_NAME", (oc.ElementAt(oc.Count - 1) as Pair).Code, true, true);
            //        IC_METER_NAME.SelectedIndex = oc.Count - 1;
            //    }

            //    ObjectList lines = go.GetPropertyValue("LINES") as ObjectList;
            //    //不存在隐患
            //    if (lines == null)
            //        return;

            //    foreach (GeneralObject line in lines)
            //    {
            //        String EQUIPMENT = line.GetPropertyValue("EQUIPMENT") as string;
            //        String CONTENT = line.GetPropertyValue("CONTENT") as string;
            //        if (EQUIPMENT.Equals("安全隐患"))
            //            CheckCheckBox(CONTENT, precautionCheckPane);
            //        else if (EQUIPMENT.Equals("燃气表"))
            //            CheckCheckBox(CONTENT, MeterDefectsPane);
            //        else if (EQUIPMENT.Equals("立管"))
            //            CheckPlumbingBox(CONTENT, PlumbingDefectsPane);
            //        else if (EQUIPMENT.Equals("阀门表前阀"))
            //            CheckCheckBox(CONTENT, PlumbingMeterValvePane);
            //        else if (EQUIPMENT.Equals("阀门灶前阀"))
            //            CheckCheckBox(CONTENT, PlumbingCookerValvePane);
            //        else if (EQUIPMENT.Equals("阀门自闭阀"))
            //            CheckCheckBox(CONTENT, PlumbingAutomaticValvePane);
            //        else if (EQUIPMENT.Equals("户内管"))
            //            CheckCheckBox(CONTENT, PlumbingPipePane);
            //        else if (EQUIPMENT.Equals("灶具软管"))
            //            CheckCheckBox(CONTENT, CookerPipePane);
            //        else if (EQUIPMENT.Equals("热水器软管"))
            //            CheckCheckBox(CONTENT, BoilerPipePane);
            //        else if (EQUIPMENT.Equals("热水器安全隐患"))
            //            CheckCheckBox(CONTENT, BoilerDefectsPane);
            //        else if (EQUIPMENT.Equals("壁挂锅炉安全隐患"))
            //            CheckCheckBox(CONTENT, WHEDefectsPane);
            //    }

                //提取用户档案地址
                String user_id = go.GetPropertyValue("f_userid") as string;
                if(IsNullOrEmpty(user_id))
                    return;
                WebClient wc = new WebClient();
                wc.DownloadStringCompleted += wc_GetUserProfileCompleted;
                wc.DownloadStringAsync(new Uri(go.WebClientInfo.BaseAddress + "/one/from t_userfiles where f_userid='" + user_id + "'"));
            }
            
        }

        private void wc_GetUserProfileCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                JsonObject item = JsonValue.Parse(e.Result) as JsonObject;
                String ROAD = null;
                String UNIT_NAME = null;
                String CUS_DOM = null;
                String CUS_DY = null;
                String CUS_FLOOR = null;
                String CUS_ROOM = null;
                if (item.ContainsKey("ROAD"))
                    ROAD = item["ROAD"];
                if (item.ContainsKey("UNIT_NAME"))
                    UNIT_NAME = item["UNIT_NAME"];
                if (item.ContainsKey("CUS_DOM"))
                    CUS_DOM = item["CUS_DOM"];
                if (item.ContainsKey("CUS_DY"))
                    CUS_DY = item["CUS_DY"];
                if (item.ContainsKey("CUS_FLOOR"))
                    CUS_FLOOR = item["CUS_FLOOR"];
                if (item.ContainsKey("CUS_ROOM"))
                    CUS_ROOM = item["CUS_ROOM"];
                CurrentArchiveAddress.Text = String.Format("{0}\t{1}\t{2}-{3}-{4}-{5}",
                    new String[] { ROAD, UNIT_NAME, CUS_DOM,CUS_DY, CUS_FLOOR, CUS_ROOM});                
            }
        }

        private void CheckPlumbingBox(String CONTENT, Panel panel)
        {
            foreach (UIElement element in panel.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.Content.ToString().Equals(CONTENT))
                        aBox.IsChecked = true;
                }
                if (element is RadioButton)
                {
                    RadioButton aBox = element as RadioButton;
                    if (aBox.Content.ToString().Equals(CONTENT))
                    {
                        aBox.IsChecked = true;
                        //cbEroded.IsChecked = true;
                    }
                }
            }
        }

        private void CheckCheckBox(String CONTENT, Panel panel)
        {
            foreach (UIElement element in panel.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.Content.ToString().Equals(CONTENT))
                        aBox.IsChecked = true;
                }
            }
        }

        private Boolean IsNullOrEmpty(String value)
        {
            return value == null || value.Trim().Length == 0;
        }

        /// <summary>
        /// 安检员统计列表改变
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void checkerGrid_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            GeneralObject go = checkerGrid.SelectedItem as GeneralObject;
            if (go == null)
                return;
            String dt = "1=1";
            if (StartDate.Text.Trim().Length != 0)
                dt = " DEPARTURE_TIME>='" + StartDate.Text + "'";
            if (EndDate.Text.Trim().Length != 0)
            {
                if (dt.Length > 3)
                    dt += " and DEPARTURE_TIME<='" + EndDate.Text + " 23:59:59'";
                else
                    dt = " DEPARTURE_TIME<='" + EndDate.Text + " 23:59:59'";
            }
            slipList.LoadOnPathChanged = false;
            slipList.MultiPath = "from T_INSPECTION t where  t.SAVE_PEOPLE like '%" + go.GetPropertyValue("save_people")  + "%' and " + dt;
            slipList.Path = "select distinct t from T_INSPECTION t left join fetch t.LINES where t.SAVE_PEOPLE like '%" + go.GetPropertyValue("save_people") + "%' and " + dt;
            slipList.Load();
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
                    border.Margin = new Thickness(10, 5, 0, 0);
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
                            atb.Margin = new Thickness(30, 5, 0, 0);
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
