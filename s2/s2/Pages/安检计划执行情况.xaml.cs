using System;
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
    public partial class 安检计划执行情况 : UserControl
    {
        public 安检计划执行情况()
        {
            InitializeComponent();
        }

        private Boolean IsNullOrEmpty(String value)
        {
            return value == null || value.Trim().Length ==0;
        }

        /// <summary>
        /// 处理查询按钮
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnSearch_Click(object sender, RoutedEventArgs e)
        {
            //按下时，组装条件，并加载
            SearchObject conditions = (criteriaPanel.DataContext as SearchObject);
            PagedList paperList = (paperGrid.ItemsSource as PagedList);
            paperList.LoadOnPathChanged = false;
            //assemble the conditions
            String criteria = " where 1=1 ";
            String rarea = conditions.GetPropertyValue("UNIT_NAME") as string;
            if(!IsNullOrEmpty(rarea))
                criteria += " and rarea like '%" + rarea + "%'";
            String building = conditions.GetPropertyValue("CUS_DOM") as string;
            if(!IsNullOrEmpty(building))
                criteria += " and building like '%" + building + "%'";
            String unit = conditions.GetPropertyValue("CUS_DY") as string;
            if(!IsNullOrEmpty(unit))
                criteria += " and unit like '%" + unit + "%'";
            String floor = conditions.GetPropertyValue("CUS_FLOOR") as string;
            if(!IsNullOrEmpty(floor))
                criteria += " and floor like '%" + floor + "%'";
            String room = conditions.GetPropertyValue("CUS_ROOM") as string;
            if(!IsNullOrEmpty(room))
                criteria += " and room like '%" + room + "%'";

            String rightJoinCriteria = "";
            String repairman = conditions.GetPropertyValue("REPAIRMAN") as string;
            if (!IsNullOrEmpty(repairman))
                rightJoinCriteria += " and REPAIRMAN like '%" + repairman + "%'";
            String SAVE_PEOPLE = conditions.GetPropertyValue("SAVE_PEOPLE") as string;
            if (!IsNullOrEmpty(SAVE_PEOPLE))
                rightJoinCriteria += " and SAVE_PEOPLE like '%" + SAVE_PEOPLE + "%'";
            String card_id = conditions.GetPropertyValue("CARD_ID") as string;
            if (!IsNullOrEmpty(card_id))
                rightJoinCriteria += " and card_id =" + card_id + "'";


            String state = (cmbState.SelectedItem as ContentControl).Content.ToString();
            if (!IsNullOrEmpty(state))
                state = " having " + (state.Equals("已检") ? "sum(flag)>0" : (state.Equals("未检") ? "sum(flag)=0" : "sum(flag)<0"));
            else
                state = "";
            if (duplicateInspection.IsChecked.Value)
            {
                if (state.Length == 0)
                    state = " having sum(flag)>1 ";
                else
                    state += " and sum(flag)>1 ";
            }

            //如果卡号为空，不用人员查询，全连接
            if (rightJoinCriteria.Length == 0)
            {
                String HQL = @"select sum(abs(flag)) mark, cast(case when sum(flag)=0 then '未检' when sum(flag)>0 then '已检' else '新增' end as varchar(10)) state,  paperid paperid, road road, rarea rarea, building building, unit unit, floor floor, room room FROM                                 (select isnull(pid,  -1) flag, isnull(pid,  paperid) paperid, isnull(proad,  troad) road , isnull(punit_name,  tunit_name) rarea, isnull(pcus_dom,tcus_dom) building,isnull(pcus_dy,  tcus_dy) unit, isnull(pcus_floor,  tcus_floor) floor, isnull(pcus_room,  tcus_room) room from                                 (select t.id tid, p.id pid, t.checkpaper_id paperid,  t.road troad, t.unit_name tunit_name, t.cus_dom tcus_dom, t.cus_dy tcus_dy, t.cus_floor tcus_floor, t.cus_room tcus_room,p.road proad, p.unit_name punit_name, p.cus_dom pcus_dom, p.cus_dy pcus_dy, p.cus_floor pcus_floor, p.cus_room pcus_room   from                                  (select * from  T_IC_SAFECHECK_PAPER where CHECKPLANID='{0}') p full join (select * from T_INSPECTION where checkplan_id='{0}') t on p.id=t.checkpaper_id)a) a    {1}                          group by paperid, road, rarea, building, unit, floor, room  {2} order by road, rarea, len(bilding), building, len(unit), unit, len(floor), floor, len(room), room";
                paperList.HQL = String.Format(HQL, new string[] { conditions.GetPropertyValue("CHECKPLANID") as string, criteria, state });
            }
            //否则退化成右连接
            else
            {
                String HQL = @"select sum(abs(flag)) mark, cast(case when sum(flag)=0 then '未检' when sum(flag)>0 then '已检' else '新增' end as varchar(10)) state,  paperid paperid, road road, rarea rarea, building building, unit unit, floor floor, room room FROM                                 (select isnull(pid,  -1) flag, isnull(pid, paperid) paperid, isnull(proad,  troad) road , isnull(punit_name, tunit_name) rarea,isnull(pcus_dom,tcus_dom) building,isnull(pcus_dy,  tcus_dy) unit, isnull(pcus_floor,  tcus_floor) floor, isnull(pcus_room,  tcus_room) room from                                 (select t.id tid, p.id pid, t.checkpaper_id paperid,  t.road troad, t.unit_name tunit_name, t.cus_dom tcus_dom, t.cus_dy tcus_dy, t.cus_floor tcus_floor, t.cus_room tcus_room,p.road proad, p.unit_name punit_name, p.cus_dom pcus_dom, p.cus_dy pcus_dy, p.cus_floor pcus_floor, p.cus_room pcus_room   from                                  (select * from  T_IC_SAFECHECK_PAPER where CHECKPLANID='{0}') p right join (select * from T_INSPECTION where checkplan_id='{0}' {1} ) t on p.id=t.checkpaper_id)a)  a     {2}                          group by paperid, road, rarea, building, unit, floor, room  {3} order by road, rarea, len(building), building, len(unit), unit, len(floor), floor, len(room), room";
                paperList.HQL = String.Format(HQL, new string[] { conditions.GetPropertyValue("CHECKPLANID") as string, rightJoinCriteria, criteria, state });
            }
            paperList.Load();
        }

        /// <summary>
        /// 安检统计列表选择改变
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void paperGrid_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            GeneralObject go = paperGrid.SelectedItem as GeneralObject;
            if (go == null)
                return;
            SearchObject conditions = (criteriaPanel.DataContext as SearchObject);
            if (go.GetPropertyValue("state").Equals("未检"))
            {
                String HQL = "select id, checkplanid, id id1, cast('未检' as varchar(10)) condition, null deleted, null  hasnotified, null  user_name, null  card_id, road, unit_name, cus_dom, cus_dy, cus_floor, cus_room, null  old_address, null  save_people, null  repairman  from t_ic_safecheck_paper where checkplanid='{0}' and road = '{1}' and unit_name='{2}' and cus_dom='{3}' and cus_dy='{4}' and cus_floor='{5}' and cus_room='{6}' and id='{7}'";
                checkList.HQL = String.Format(HQL, new String[] { conditions.GetPropertyValue("CHECKPLANID") as string, go.GetPropertyValue("road").ToString(), go.GetPropertyValue("rarea").ToString(), go.GetPropertyValue("building").ToString(), go.GetPropertyValue("unit").ToString(), go.GetPropertyValue("floor").ToString(), go.GetPropertyValue("room").ToString(), go.GetPropertyValue("paperid").ToString() });
                checkList.Load();
            }
            else
            {
                String rightJoinCriteria = " and 1=1 ";
                String repairman = conditions.GetPropertyValue("REPAIRMAN") as string;
                if (!IsNullOrEmpty(repairman))
                    rightJoinCriteria += " and REPAIRMAN like '%" + repairman + "%'";
                String SAVE_PEOPLE = conditions.GetPropertyValue("SAVE_PEOPLE") as string;
                if (!IsNullOrEmpty(SAVE_PEOPLE))
                    rightJoinCriteria += " and SAVE_PEOPLE like '%" + SAVE_PEOPLE + "%'";
                String card_id = conditions.GetPropertyValue("CARD_ID") as string;
                if (!IsNullOrEmpty(card_id))
                    rightJoinCriteria += " and card_id =" + card_id + "'";

                String HQL = "select id, checkplan_id, checkpaper_id, condition, deleted, hasnotified, user_name, card_id, road, unit_name, cus_dom, cus_dy, cus_floor, cus_room, old_address, save_people, repairman from t_inspection where checkplan_id='{0}' and road = '{1}' and unit_name='{2}' and cus_dom='{3}' and cus_dy='{4}' and cus_floor='{5}' and cus_room='{6}' and checkpaper_id='{7}' {8}";
                checkList.HQL = String.Format(HQL, new String[] { conditions.GetPropertyValue("CHECKPLANID") as string, go.GetPropertyValue("road").ToString(), go.GetPropertyValue("rarea").ToString(), go.GetPropertyValue("building").ToString(), go.GetPropertyValue("unit").ToString(), go.GetPropertyValue("floor").ToString(), go.GetPropertyValue("room").ToString(), go.GetPropertyValue("paperid").ToString(), rightJoinCriteria });
                checkList.Load();
            }
        }

        /// <summary>
        /// 安检单列表选择改变
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void checkGrid_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            GeneralObject item = checkGrid.SelectedItem as GeneralObject;
            if (item == null)
                return;
            GeneralObject go = new GeneralObject();
            go.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            go.EntityType = "T_INSPECTION";
            if (!item.GetPropertyValue("condition").Equals("未检"))
            {
                go.Path = "one/select distinct t from T_INSPECTION t left join fetch t.LINES left join fetch t.CHECKPLAN_ID where t.id ='" + item.GetPropertyValue("id").ToString() + "'";
                go.DataLoaded += go_DataLoaded;
                if (!item.GetPropertyValue("condition").Equals("正常"))
                    userfile.IsEnabled = false;
                else
                    userfile.IsEnabled = true;
                go.Load();
            }
            else
            {
                userfile.DataContext = null;
                ClearAll();
                userfile.IsEnabled = false;
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
            WARM_OTHER.Text = "";
            JB_METER_NAME_OTHER.Text = "";
            IC_METER_NAME_OTHER.Text = "";
            WARM.SelectedItem = null;
            IC_METER_NAME.SelectedItem = null;
            JB_METER_NAME.SelectedItem = null;
        }

        private void ClearChecks()
        {
            Panel[] panels = { MeterDefectsPane,  PlumbingDefectsPane, PlumbingProofPane, PlumbingPressurePane, PlumbingMeterValvePane, PlumbingCookerValvePane, PlumbingAutomaticValvePane, PlumbingPipePane, PlumbingLeakagePane, 
                                 CookerPipePane, BoilerPipePane, BoilerDefectsPane, WHEDefectsPane, precautionCheckPane };
            foreach (Panel p in panels)
            {
                foreach (UIElement element in p.Children)
                {
                    if (element is CheckBox && !(element as CheckBox).Content.ToString().Equals("正常") && !(element as CheckBox).Content.ToString().Equals("无"))
                    {
                        if (element == cbRIGIDITYLeakage || element == cbRIGIDITYNormal || element == cbPressureNormal || element == cbPressureAbnormal ||
                            element == cbLEAKAGE_COOKER || element == cbLEAKAGE_BOILER || element == cbLEAKAGE_HEATER || element == cbLEAKAGE_NOTIFIED)
                            continue;
                        (element as CheckBox).IsChecked = false;
                    }
                    if (element is RadioButton)
                        (element as RadioButton).IsChecked = false;
                }
            }
        }


        private void PostUITask(GeneralObject go)
        {
            if (go.GetPropertyValue("CONDITION").ToString().Equals("正常"))
            {
                //供暖方式
                ObjectCollection oc = this.Resources["WARM"] as ObjectCollection;
                bool found = false;
                foreach (Pair pair in oc)
                {
                    if (pair.CName.Equals(go.GetPropertyValue("WARM").ToString()))
                    {
                        found = true;
                        WARM.SelectedItem = pair;
                    }
                }
                if (!found)
                {
                    WARM_OTHER.Text = go.GetPropertyValue("WARM").ToString();
                    WARM.SelectedIndex = oc.Count - 1;
                }

                //基表厂家型号
                oc = this.Resources["JB_METER_NAME"] as ObjectCollection;
                found = false;
                foreach (Pair pair in oc)
                {
                    if (pair.CName.Equals(go.GetPropertyValue("JB_METER_NAME").ToString()))
                    {
                        JB_METER_NAME.SelectedItem = pair;
                        found = true;
                    }
                }
                if (!found)
                {
                    JB_METER_NAME_OTHER.Text = go.GetPropertyValue("JB_METER_NAME").ToString();
                    JB_METER_NAME.SelectedIndex = oc.Count - 1;
                }

                //IC卡表厂家型号
                oc = this.Resources["IC_METER_NAME"] as ObjectCollection;
                found = false;
                foreach (Pair pair in oc)
                {
                    if (pair.CName.Equals(go.GetPropertyValue("IC_METER_NAME").ToString()))
                    {
                        found = true;
                        IC_METER_NAME.SelectedItem = pair;
                    }
                }
                if (!found)
                {
                    IC_METER_NAME_OTHER.Text = go.GetPropertyValue("IC_METER_NAME").ToString();
                    go.SetPropertyValue("IC_METER_NAME", (oc.ElementAt(oc.Count - 1) as Pair).Code, true, true);
                    IC_METER_NAME.SelectedIndex = oc.Count - 1;
                }

                ObjectList lines = go.GetPropertyValue("LINES") as ObjectList;
                //不存在隐患
                if (lines == null)
                    return;

                foreach (GeneralObject line in lines)
                {
                    String EQUIPMENT = line.GetPropertyValue("EQUIPMENT") as string;
                    String CONTENT = line.GetPropertyValue("CONTENT") as string;
                    if (EQUIPMENT.Equals("安全隐患"))
                        CheckCheckBox(CONTENT, precautionCheckPane);
                    else if (EQUIPMENT.Equals("燃气表"))
                        CheckCheckBox(CONTENT, MeterDefectsPane);
                    else if (EQUIPMENT.Equals("立管"))
                        CheckPlumbingBox(CONTENT, PlumbingDefectsPane);
                    else if (EQUIPMENT.Equals("阀门表前阀"))
                        CheckCheckBox(CONTENT, PlumbingMeterValvePane);
                    else if (EQUIPMENT.Equals("阀门灶前阀"))
                        CheckCheckBox(CONTENT, PlumbingCookerValvePane);
                    else if (EQUIPMENT.Equals("阀门自闭阀"))
                        CheckCheckBox(CONTENT, PlumbingAutomaticValvePane);
                    else if (EQUIPMENT.Equals("户内管"))
                        CheckCheckBox(CONTENT, PlumbingPipePane);
                    else if (EQUIPMENT.Equals("灶具软管"))
                        CheckCheckBox(CONTENT, CookerPipePane);
                    else if (EQUIPMENT.Equals("热水器软管"))
                        CheckCheckBox(CONTENT, BoilerPipePane);
                    else if (EQUIPMENT.Equals("热水器安全隐患"))
                        CheckCheckBox(CONTENT, BoilerDefectsPane);
                    else if (EQUIPMENT.Equals("壁挂锅炉安全隐患"))
                        CheckCheckBox(CONTENT, WHEDefectsPane);
                }

                //提取用户档案地址
                String card_id = go.GetPropertyValue("CARD_ID") as string;
                if(IsNullOrEmpty(card_id))
                    return;
                WebClient wc = new WebClient();
                wc.DownloadStringCompleted += wc_GetUserProfileCompleted;
                wc.DownloadStringAsync(new Uri(go.WebClientInfo.BaseAddress + "/one/from T_IC_USERFILE where CARD_ID='" + card_id +"'"));
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
                        cbEroded.IsChecked = true;
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

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnDelete_Click(object sender, RoutedEventArgs e)
        {
            GeneralObject item = checkGrid.SelectedItem as GeneralObject;
            if (item == null)
                return;
            GeneralObject go = userfile.DataContext as GeneralObject;
            if (go == null)
            {
                MessageBox.Show("当前选中的行不是正常安检！");
                return;
            }
            string actionStr = "\"operator\":\"sql\",\"entity\":\"\",\"data\":\"update t_inspection set deleted='{0}' where id='{1}'\",\"name\":\"\"";
            String deleted = go.GetPropertyValue("DELETED") as String;
            if (IsNullOrEmpty(deleted))
                actionStr = String.Format(actionStr, new String[] { "是", go.GetPropertyValue("id") as String});
            else
                actionStr = String.Format(actionStr, new String[] { null, go.GetPropertyValue("id") as String });
            WebClient wc = new WebClient();
            wc.UploadStringCompleted += wc_DeletionCompleted;
            wc.Headers["Content-type"] = "application/x-www-form-urlencoded";
            wc.UploadStringAsync(new Uri(go.WebClientInfo.BaseAddress), "POST", "[{" + actionStr + "}]");
        }

        /// <summary>
        /// 删除标记完成
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>        
        private void wc_DeletionCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            paperGrid_SelectionChanged(null, null);
        }


        private void btnWriteBack_Click(object sender, RoutedEventArgs e)
        {
            GeneralObject go = userfile.DataContext as GeneralObject;
            if(go==null)
            {
                MessageBox.Show("当前选中的行不是正常安检！");
                return;
            }
            String condition = go.GetPropertyValue("CONDITION") as String;
            if (IsNullOrEmpty(condition) || !condition.Equals("正常"))
            {
                MessageBox.Show("当前选中的行不是正常安检！");
                return;
            }
            string actionStr = "\"operator\":\"sql\",\"entity\":\"\",\"data\":\"update t_ic_userfile set road='{0}', unit_name='{1}', cus_dom='{2}', cus_dy='{3}', cus_floor='{4}', cus_room='{5}' where card_id='{6}'\",\"name\":\"\"";
            actionStr = String.Format(actionStr, new String[] { go.GetPropertyValue("ROAD") as String, go.GetPropertyValue("UNIT_NAME") as String, go.GetPropertyValue("CUS_DOM") as String, go.GetPropertyValue("CUS_DY") as String, go.GetPropertyValue("CUS_FLOOR") as String, go.GetPropertyValue("CUS_ROOM") as String, go.GetPropertyValue("CARD_ID") as String });
            WebClient wc = new WebClient();
            wc.UploadStringCompleted += wc_UploadStringCompleted;
            wc.Headers["Content-type"] = "application/x-www-form-urlencoded";
            wc.UploadStringAsync(new Uri(go.WebClientInfo.BaseAddress), "POST", "[{" + actionStr + "}]");
        }

        void wc_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            (sender as WebClient).UploadStringCompleted -= wc_UploadStringCompleted;
            if (e.Error == null)
            {
                GeneralObject go = userfile.DataContext as GeneralObject;
                CurrentArchiveAddress.Text = String.Format("{0}\t{1}\t{2}-{3}-{4}-{5}", new String[] { go.GetPropertyValue("ROAD") as String, go.GetPropertyValue("UNIT_NAME") as String, go.GetPropertyValue("CUS_DOM") as String, go.GetPropertyValue("CUS_DY") as String, go.GetPropertyValue("CUS_FLOOR") as String, go.GetPropertyValue("CUS_ROOM") as String });
            }
        }


        private void btnSave_Click(object sender, RoutedEventArgs e)
        {
            GeneralObject go = userfile.DataContext as GeneralObject;
            if(go == null)
            {
                MessageBox.Show("当前选中的行不是正常安检！");
                return;
            }
            if (!TransformBeforeSave(go))
                return;
            go.Name = go.GetPropertyValue("id").ToString();
            go.Save();
        }

        private bool TransformBeforeSave(GeneralObject go)
        {
            if (!go.GetPropertyValue("CONDITION").Equals("正常"))
                return true;
            //必须输入字段
            if (go.GetPropertyValue("CARD_ID").ToString().Length == 0)
            {
                MessageBox.Show("卡号不能为空!");
                return false;
            }
            if (go.GetPropertyValue("USER_NAME").ToString().Length == 0)
            {
                MessageBox.Show("用户名不能为空!");
                return false;
            }
            if (go.GetPropertyValue("TELPHONE").ToString().Length == 0)
            {
                MessageBox.Show("电话号码不能为空!");
                return false;
            }
            if (go.GetPropertyValue("JB_NUMBER").ToString().Length == 0)
            {
                MessageBox.Show("基表读数不能为空!");
                return false;
            }
            //供暖方式
            if (!CheckCombo(go, WARM, WARM_OTHER, "WARM", "请检查供暖方式！"))
                return false;
            //基表厂家型号
            if (!CheckCombo(go, JB_METER_NAME, JB_METER_NAME_OTHER, "JB_METER_NAME", "请检查基表厂家型号！"))
                return false;
            //IC卡表厂家型号
            if (!CheckCombo(go, IC_METER_NAME, IC_METER_NAME_OTHER, "IC_METER_NAME", "请检查IC卡表厂家型号！"))
                return false;
            if (NEEDS_REPAIR.IsChecked.Value && cmbRepair.SelectedItem == null)
            {
                MessageBox.Show("请选择维修人!");
                return false;
            }
            if (NEEDS_REPAIR.IsChecked.Value)
            {
                go.SetPropertyValue("NEEDS_REPAIR", "是", true);
                GeneralObject mechanic = cmbRepair.SelectedItem as GeneralObject;
                go.SetPropertyValue("REPAIRMAN", mechanic.GetPropertyValue("REPAIRMAN"), true);
                go.SetPropertyValue("REPAIRMAN_ID", mechanic.GetPropertyValue("REPAIRMAN_ID"), true);
            }
            else
            {
                go.SetPropertyValue("NEEDS_REPAIR", "否", true);
                go.SetPropertyValue("REPAIRMAN", null, true);
                go.SetPropertyValue("REPAIRMAN_ID", null, true);
            }

            ObjectList lines = new ObjectList();
            //燃气表隐患,必选一个,也可多选
            if (!FillPrecautionsAccordingToChoices(MeterDefectsPane, go, lines, true, false, "燃气表"))
                return false;
            go.SetPropertyValue("RQB", RQB.IsChecked.Value?"正常":"不正常", true);
            //立管隐患
            if (!FillPrecautionsAccordingToChoices2(PlumbingDefectsPane, go, lines, true, false, "立管"))
                return false;
            go.SetPropertyValue("STANDPIPE", STANDPIPE.IsChecked.Value ? "正常" : "不正常", true);
            //严密性测试
            if (cbRIGIDITYNormal.IsChecked.Value)
                go.SetPropertyValue("RIGIDITY", "正常", true);
            else
                go.SetPropertyValue("RIGIDITY", "不正常", true);
            if (cbRIGIDITYLeakage.IsChecked.Value)
                go.SetPropertyValue("RIGIDITY", "漏气", true);
            //静止压力
            if (cbPressureAbnormal.IsChecked.Value && cbPressureNormal.IsChecked.Value)
            {
                MessageBox.Show("请检查静止选项!");
                return false;
            }
            if(cbPressureNormal.IsChecked.Value)
                go.SetPropertyValue("STATIC", "正常", true);
            else if (cbPressureAbnormal.IsChecked.Value)
                go.SetPropertyValue("STATIC", "不正常", true);
            else
                go.SetPropertyValue("STATIC", "", true);
            //表前阀
            if (!FillPrecautionsAccordingToChoices(PlumbingMeterValvePane, go, lines, true, false, "阀门表前阀"))
                return false;
            go.SetPropertyValue("TABLE_TAP", TABLE_TAP.IsChecked.Value ? "正常" : "不正常", true);
            //灶前阀
            if (!FillPrecautionsAccordingToChoices(PlumbingCookerValvePane, go, lines, true, true, "阀门灶前阀"))
                return false;
            go.SetPropertyValue("COOK_TAP", COOK_TAP.IsChecked.Value ? "正常" : "不正常", true);
            //自闭阀
            if (!FillPrecautionsAccordingToChoices(PlumbingAutomaticValvePane, go, lines, true, true, "阀门自闭阀"))
                return false;
            go.SetPropertyValue("CLOSE_TAP", CLOSE_TAP.IsChecked.Value ? "正常" : "不正常", true);
            //户内管
            if (!FillPrecautionsAccordingToChoices(PlumbingPipePane, go, lines, true, true, "户内管"))
                return false;
            go.SetPropertyValue("INDOOR", INDOOR.IsChecked.Value ? "正常" : "不正常", true);
            //漏气
            if (cbLEAKAGE_COOKER.IsChecked.Value)
                go.SetPropertyValue("LEAKAGE_COOKER", "灶具漏气", true);
            else
                go.SetPropertyValue("LEAKAGE_COOKER", "", true);
            if (cbLEAKAGE_HEATER.IsChecked.Value)
                go.SetPropertyValue("LEAKAGE_HEATER", "热水器漏气", true);
            else
                go.SetPropertyValue("LEAKAGE_HEATER", "", true);
            if (cbLEAKAGE_BOILER.IsChecked.Value)
                go.SetPropertyValue("LEAKAGE_BOILER", "壁挂炉漏气", true);
            else
                go.SetPropertyValue("LEAKAGE_BOILER", "", true);
            if (cbLEAKAGE_NOTIFIED.IsChecked.Value)
                go.SetPropertyValue("LEAKAGE_NOTIFIED", "安检告知", true);
            else
                go.SetPropertyValue("LEAKAGE_NOTIFIED", "", true);
            //灶具软管
            if (!FillPrecautionsAccordingToChoices(CookerPipePane, go, lines, false, true, "灶具软管"))
                return false;
            go.SetPropertyValue("COOKPIPE_NORMAL", COOKPIPE_NORMAL.IsChecked.Value ? "正常" : "不正常", true);
            //热水器软管
            if (!FillPrecautionsAccordingToChoices(BoilerPipePane, go, lines, false, true, "热水器软管"))
                return false;
            go.SetPropertyValue("WATER_PIPE", WATER_PIPE.IsChecked.Value ? "正常" : "不正常", true);
            //热水器隐患
            if (!FillPrecautionsAccordingToChoices(BoilerDefectsPane, go, lines, false, true, "热水器安全隐患"))
                return false;
            go.SetPropertyValue("WATER_HIDDEN", WATER_HIDDEN.IsChecked.Value ? "正常" : "不正常", true);
            //壁挂锅炉安全隐患
            if (!FillPrecautionsAccordingToChoices(WHEDefectsPane, go, lines, false, true, "壁挂锅炉安全隐患"))
                return false;
            go.SetPropertyValue("WHE_HIDDEN", WHE_HIDDEN.IsChecked.Value ? "正常" : "不正常", true);
            //安全隐患
            if (!FillPrecautionsAccordingToChoices(precautionCheckPane, go, lines, false, false, "安全隐患"))
                return false;

            go.SetPropertyValue("LINES", lines, true);
            return true;
        }

        private bool CheckCombo(GeneralObject go, ComboBox cbox, TextBox other, string key, string msg)
        {
            ObjectCollection oc = Resources[key] as ObjectCollection;
            if (cbox.SelectedValue.Equals((oc.ElementAt(oc.Count - 1) as Pair).CName))
            {
                if (other.Text.Trim().Length == 0)
                {
                    MessageBox.Show(msg);
                    return false;
                }
                else
                {
                    go.SetPropertyValue(key, other.Text.Trim(), true);
                }
            }
            else
                go.SetPropertyValue(key, cbox.SelectedValue, true);
            return true;
        }

        private bool FillPrecautionsAccordingToChoices(Panel p, GeneralObject go, ObjectList lines, bool mustSelect, bool checkContradition, string equipment)
        {
            bool selected = false;
            bool normalChecked = false;
            bool abnormalChecked = false;
            foreach (UIElement element in p.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.IsChecked.HasValue && aBox.IsChecked.Value)
                    {
                        if (aBox.Content.Equals("无") || aBox.Content.Equals("正常"))
                            normalChecked = true;
                        else
                            abnormalChecked = true;
                    }
                    if (aBox.IsChecked.HasValue)
                        selected |= aBox.IsChecked.Value;
                }
            }
            if (!selected && mustSelect)
            {
                MessageBox.Show("请选择" + equipment + "隐患选项!");
                return false;
            }
            if (normalChecked && abnormalChecked && checkContradition)
            {
                MessageBox.Show("请检查" + equipment + "隐患选项!");
                return false;
            }

            foreach (UIElement element in p.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.IsChecked.HasValue && aBox.IsChecked.Value)
                    {
                        if (aBox.Content.Equals("无") || aBox.Content.Equals("正常"))
                            continue;
                        GeneralObject line = CreateALine(go);
                        lines.Add(line);
                        line.SetPropertyValue("EQUIPMENT", equipment, true);
                        line.SetPropertyValue("CONTENT", aBox.Content, true);
                    }
                }
            }
            return true;
        }

        private bool FillPrecautionsAccordingToChoices2(Panel p, GeneralObject go, ObjectList lines, bool mustSelect, bool checkContradition, string equipment)
        {
            bool selected = false;
            bool normalChecked = false;
            bool abnormalChecked = false;
            foreach (UIElement element in p.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.IsChecked.HasValue && aBox.IsChecked.Value)
                    {
                        if (aBox.Content.Equals("无") || aBox.Content.Equals("正常"))
                            normalChecked = true;
                        else
                            abnormalChecked = true;
                    }
                    if (aBox.IsChecked.HasValue)
                        selected |= aBox.IsChecked.Value;
                }
            }
            if (!selected && mustSelect)
            {
                MessageBox.Show("请选择" + equipment + "隐患选项!");
                return false;
            }
            if (normalChecked && abnormalChecked && checkContradition)
            {
                MessageBox.Show("请检查" + equipment + "隐患选项!");
                return false;
            }

            foreach (UIElement element in p.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.IsChecked.HasValue && aBox.IsChecked.Value)
                    {
                        if (aBox.Content.Equals("无") || aBox.Content.Equals("正常"))
                            continue;
                        GeneralObject line = CreateALine(go);
                        lines.Add(line);
                        line.SetPropertyValue("EQUIPMENT", equipment, true);
                        if (aBox.Content.Equals("腐蚀"))
                        {
                            if(rbErodedSlight.IsChecked.Value)
                                line.SetPropertyValue("CONTENT", rbErodedSlight.Content, true);
                            else if (rbErodedSevere.IsChecked.Value)
                                line.SetPropertyValue("CONTENT", rbErodedSevere.Content, true);
                            else if (rbErodedModerate.IsChecked.Value)
                                line.SetPropertyValue("CONTENT", rbErodedModerate.Content, true);

                        }
                    }
                }
            }
            return true;
        }

        private GeneralObject CreateALine(GeneralObject go)
        {
            GeneralObject me = new GeneralObject();
            me.EntityType = "T_INSPECTION_LINE";
            me.SetPropertyValue("CARD_ID", go.GetPropertyValue("CARD_ID"), true);
            me.SetPropertyValue("USER_NAME", go.GetPropertyValue("USER_NAME"), true);
            me.SetPropertyValue("ROAD", go.GetPropertyValue("ROAD"), true);
            me.SetPropertyValue("UNIT_NAME", go.GetPropertyValue("UNIT_NAME"), true);
            me.SetPropertyValue("CUS_DOM", go.GetPropertyValue("CUS_DOM"), true);
            me.SetPropertyValue("CUS_DY", go.GetPropertyValue("CUS_DY"), true);
            me.SetPropertyValue("CUS_FLOOR", go.GetPropertyValue("CUS_FLOOR"), true);
            me.SetPropertyValue("CUS_ROOM", go.GetPropertyValue("CUS_ROOM"), true);
            me.SetPropertyValue("TELPHONE", go.GetPropertyValue("TELPHONE"), true);
            me.SetPropertyValue("SAVE_PEOPLE", go.GetPropertyValue("SAVE_PEOPLE"), true);
            me.SetPropertyValue("SAVE_DATE", go.GetPropertyValue("SAVE_DATE"), true);
            me.SetPropertyValue("IC_METER_NAME", go.GetPropertyValue("IC_METER_NAME"), true);
            me.SetPropertyValue("JB_METER_NAME", go.GetPropertyValue("JB_METER_NAME"), true);
            me.SetPropertyValue("JB_NUMBER", go.GetPropertyValue("JB_NUMBER"), true);
            me.SetPropertyValue("SURPLUS_GAS", go.GetPropertyValue("SURPLUS_GAS"), true);
            return me;
        }

        private void picture_MouseEnter(object sender, MouseEventArgs e)
        {
            Image image = sender as Image;
            if (image.Source == null)
                return;
            bigPic.Source = image.Source;
        }

        private void cbEroded_Checked(object sender, RoutedEventArgs e)
        {
            if ((sender as CheckBox).IsChecked.Value)
                rbErodedSevere.IsChecked = true;
            else
            {
                rbErodedModerate.IsChecked = false;
                rbErodedSevere.IsChecked = false;
                rbErodedSlight.IsChecked = false;
            }

        }

        private void RadioButton_Checked(object sender, RoutedEventArgs e)
        {
            if ((sender as RadioButton).IsChecked.Value)
                cbEroded.IsChecked = true;
        }
    }

    public class Pair
    {
        public String CName { get; set; }
        public String Code { get; set; }

        public override string ToString()
        {
            return CName;
        }

    }
}
