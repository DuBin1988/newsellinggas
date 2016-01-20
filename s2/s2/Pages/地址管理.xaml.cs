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
using System.Windows.Navigation;
using Com.Aote.Marks;
using Com.Aote.ObjectTools;
using System.Collections;

namespace s2.Pages
{
    public partial class 地址管理 : Page
    {
        ObjectList list = null;
        GeneralObject AreaContext = null;

        public 地址管理()
        {
            InitializeComponent();
            pageResource.Loaded += pageResource_Loaded;
            EditGrid.Visibility = System.Windows.Visibility.Collapsed;
            AreaGrid.Visibility = System.Windows.Visibility.Collapsed;
        }

        /// <summary>
        /// 初始化时加载小区
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void pageResource_Loaded(object sender, RoutedEventArgs e)
        {
            pageResource.Loaded -= pageResource_Loaded;
            list = pageResource.Res[0] as ObjectList;
            AreaContext = pageResource.Res[1] as GeneralObject;
            LoadResidentialAreaData();
        }

        /// <summary>
        /// 加载四级地址
        /// </summary>
        /// <param name="level"></param>
        /// <param name="areaId"></param>
        private void LoadBuildingAndOthers(int level, String areaId)
        {
            list.LoadOnPathChanged = false;
            list.EntityType = "t_design_address";
            String sql = @"sql/with rt AS
            (
            select IID, Name, Remark, Level, PID, ResidentialAreaName, rank, ID, addressId, left(iid+REPLICATE('0', 18),30)+RIGHT('000000' + cast(rank AS varchar(6)), 6) as sn from t_design_address
            where level={0} and pid='{1}'
            union ALL
            select t.IID, t.Name, t.Remark, t.Level, t.PID, t.ResidentialAreaName, t.rank, t.ID, t.addressId, left(t.iid+REPLICATE('0', 18),30)+RIGHT('000000' + cast(t.rank AS varchar(6)), 6) as sn from t_design_address t INNER JOIN rt t2
            on t.pid = t2.iid
            )
            select * FROM
            rt
            order by sn";
            list.Path = string.Format(sql, new object[]{level, areaId});
            list.WebClientInfo = App.Current.Resources["dbclient"] as WebClientInfo;
            list.Completed += LoadBuildingAndOthers_Completed;
            list.Load();
        }

        /// <summary>
        /// 构建四级树节点
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void LoadBuildingAndOthers_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            list.Completed -= LoadBuildingAndOthers_Completed;
            if (e.Error != null)
            {
                MessageBox.Show("提取数据错误。");
                return;
            }

            List<TreeViewItem> llist = new List<TreeViewItem>();
            List<int> rankList = new List<int>();
            for (int i = 0; i < 4; i++)
            {
                llist.Add(curItem);
                rankList.Add(1);
            }

            int preLevel = 5;
            int preRank = 1;
            TreeViewItem parentItem = curItem;
            TreeViewItem preItem = curItem;
            for (int i = 0; i < list.Count; i++)
            {
                TreeViewItem item = new TreeViewItem();
                GeneralObject go = (list[i] as GeneralObject);
                item.Header = go.GetPropertyValue("col1").ToString();
                item.Tag = list[i];
                int level = int.Parse(go.GetPropertyValue("col3").ToString());
                int rank = int.Parse(go.GetPropertyValue("col6").ToString());
                if (level < preLevel)
                {
                    parentItem = llist[level];
                    preRank = rankList[level];
                }
                else if(level == preLevel)
                {
                }
                else
                {
                    llist[level] = preItem;
                    parentItem = preItem;
                }
                if (rank >= preRank)
                {
                    parentItem.Items.Add(item);
                    rankList[level] = rank;
                }
                else
                {
                    //查找比他大的最小值
                    int min = 999999;
                    int pos = parentItem.Items.Count;
                    for (int j = 0; j < pos; j++)
                    {
                        GeneralObject go2 = ((parentItem.Items[j] as TreeViewItem).Tag) as GeneralObject;
                        int rank2 = int.Parse(go2.GetPropertyValue("col6").ToString());
                        if (rank2 > rank)
                        {
                            if (min > rank2)
                            {
                                min = rank2;
                                pos = j;
                            }
                        }
                    }
                    //没找到
                    if (min == 999999)
                        parentItem.Items.Add(item);
                    else
                        parentItem.Items.Insert(pos, item);
                }
                preItem = item;
                preLevel = level;
                preRank = rank;
            }
            curItem.IsExpanded = true;
        }

        /// <summary>
        /// 加载小区数据
        /// </summary>
        private void LoadResidentialAreaData()
        {
            list.LoadOnPathChanged = false;
            list.EntityType = "t_area";
            list.Path = "from t_area where f_address is not null";
            list.WebClientInfo = App.Current.Resources["dbclient"] as WebClientInfo;
            list.Completed += LoadResidentialAreaData_Completed;
            list.Load();
        }

        private void LoadResidentialAreaData_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            list.Completed -= LoadResidentialAreaData_Completed;
            if(e.Error != null)
            {
                MessageBox.Show("提取数据错误。");
                return;
            }
            for(int i=0; i<list.Count; i++)
            {
                TreeViewItem item = new TreeViewItem();
                item.Header = (list[i] as GeneralObject).GetPropertyValue("f_address").ToString();
                item.Tag = list[i];
                root.Items.Add(item);
            }
            root.IsExpanded = true;
            curItem = root;
        }


        //默认为null,全部刷新
        TreeViewItem curItem = null;

        /// <summary>
        /// 刷新当前节点的子
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnRefresh_Click(object sender, RoutedEventArgs e)
        {
            curItem.Items.Clear();
            if (curItem.Tag == null)
            {
                LoadResidentialAreaData();
            }
            else
            {
                GeneralObject go = (GeneralObject)curItem.Tag;
                if (go.EntityType != null && go.EntityType.Equals("t_area"))
                {
                    String id = go.GetPropertyValue("id").ToString();
                    LoadBuildingAndOthers(0, id);
                }
                else
                {
                    String id = go.GetPropertyValue("col0").ToString();
                    int level = int.Parse(go.GetPropertyValue("col3").ToString())+1;
                    if(level<=4)
                        LoadBuildingAndOthers(level, id);
                }
            }
        }

        /// <summary>
        /// 跟踪当前节点
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void TreeView_SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            curItem = e.NewValue as TreeViewItem;
            tree_MouseLeftButtonUp(e, null);
        }

        /// <summary>
        /// 鼠标点击，提取下级数据
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void tree_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            if (curItem.Tag == null)
            {
                AreaGrid.Visibility = System.Windows.Visibility.Collapsed;
                EditGrid.Visibility = System.Windows.Visibility.Collapsed;
                return;
            }
            GeneralObject go = curItem.Tag as GeneralObject;
            //处理其他四级
            if (String.IsNullOrEmpty(go.EntityType))
            {
                ShowEditPanel();
            }
            //处理小区
            else if (go.EntityType.Equals("t_area"))
            {
                String id = go.GetPropertyValue("id").ToString();
                if (!curItem.HasItems)
                    LoadBuildingAndOthers(0, id);
                ShowResidentialAreaPanel(id);
            }

        }

        //显示修改面板
        private void ShowEditPanel()
        {
            AreaGrid.Visibility = System.Windows.Visibility.Collapsed;
            EditGrid.Visibility = System.Windows.Visibility.Visible;
            BtnSave.Visibility = System.Windows.Visibility.Visible;
            GeneralObject go = curItem.Tag as GeneralObject;
            TxtName.Text = go.GetPropertyValue("col1").ToString();
            TxtRemark.Text = go.GetPropertyValue("col2").ToString();
        }

        /// <summary>
        /// 显示小区信息
        /// </summary>
        /// <param name="id"></param>
        private void ShowResidentialAreaPanel(string id)
        {
            EditGrid.Visibility = System.Windows.Visibility.Visible;
            BtnSave.Visibility = System.Windows.Visibility.Collapsed;
            (EditGrid.DataContext as SearchObject).SetPropertyValue("AName", null, true);
            (EditGrid.DataContext as SearchObject).SetPropertyValue("Remark", "", true);
            AreaGrid.Visibility = System.Windows.Visibility.Visible;
            AreaContext.Path = "one/from t_area where id='" + id + "'";
        }

        /// <summary>
        /// 删除当前节点
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnDel_Click(object sender, RoutedEventArgs e)
        {
            if(curItem.HasItems)
            {
                MessageBox.Show("请先删除当前项" + curItem.Header + "的下级项。");
                return;
            }
            //根不能删除
            if(curItem.Tag == null)
            {
                return;
            }
            if (MessageBox.Show("确认删除当前项 " + curItem.Header + " 吗？", "确认", MessageBoxButton.OKCancel) != MessageBoxResult.OK)
                return;

            GeneralObject go = curItem.Tag as GeneralObject;

            //从t_design_address删除
            String cmd = String.Format("[{{\"operator\":\"sql\", \"data\":\"delete from t_design_address where id={0}\"}}", go.GetPropertyValue("col7"));
            //如果是房号，从t_gasaddress删除
            if(int.Parse(go.GetPropertyValue("col3").ToString()) == 3)
            {
                cmd += String.Format(", {{\"operator\":\"sql\", \"data\":\"delete from t_gasaddress where id={0}\"}}", go.GetPropertyValue("col8"));
            }
            cmd += "]";

            BatchAction(cmd);

            //删除界面元素
            curItem.GetParentTreeViewItem().Items.Remove(curItem);
        }

        /// <summary>
        /// 交换位置
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnDown_Click(object sender, RoutedEventArgs e)
        {
            if (!String.IsNullOrEmpty((curItem.Tag as GeneralObject).EntityType))
            {
                MessageBox.Show("不能移动小区！");
                return;
            }
            TreeViewItem pvi = curItem.GetParentTreeViewItem();
            int pos = pvi.ItemContainerGenerator.IndexFromContainer(curItem);
            if(pos >= pvi.Items.Count-1)
            {
                return;
            }
            TreeViewItem tvi = pvi.Items[pos + 1] as TreeViewItem;
            ExchangeItems(pvi, pos, tvi, 1);
            //更新数据库中的排序号
            UpdateRanks(curItem, tvi);
        }


        /// <summary>
        /// 交互位置
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnUp_Click(object sender, RoutedEventArgs e)
        {
            if (!String.IsNullOrEmpty((curItem.Tag as GeneralObject).EntityType))
            {
                MessageBox.Show("不能移动小区！");
                return;
            } 
            TreeViewItem pvi = curItem.GetParentTreeViewItem();
            int pos = pvi.ItemContainerGenerator.IndexFromContainer(curItem);
            if (pos == 0)
            {
                return;
            }
            TreeViewItem tvi = pvi.Items[pos - 1] as TreeViewItem;
            ExchangeItems(pvi, pos, tvi, -1);
            //更新数据库中的排序号
            UpdateRanks(curItem, tvi);
        }

        private void ExchangeItems(TreeViewItem pvi, int pos, TreeViewItem tvi, int offset)
        {
            //cost a morning to figure this out, dont try to delete the curItem.
            //在界面中交换位置
            pvi.Items.RemoveAt(pos + offset);
            pvi.Items.Insert(pos, tvi);
            //交换界面的排序号
            object t = (curItem.Tag as GeneralObject).GetPropertyValue("col6");
            (curItem.Tag as GeneralObject).SetPropertyValue("col6", (tvi.Tag as GeneralObject).GetPropertyValue("col6"), false);
            (tvi.Tag as GeneralObject).SetPropertyValue("col6", t, false);
        }


        private void UpdateRanks(TreeViewItem curItem, TreeViewItem tvi)
        {
            object id = (curItem.Tag as GeneralObject).GetPropertyValue("col7");
            object rank = (curItem.Tag as GeneralObject).GetPropertyValue("col6");
            object id2 = (tvi.Tag as GeneralObject).GetPropertyValue("col7");
            object rank2 = (tvi.Tag as GeneralObject).GetPropertyValue("col6");
            String cmd = String.Format("[{{\"operator\":\"sql\", \"data\":\"update t_design_address set rank={0} where id={1}\"}},{{\"operator\":\"sql\", \"data\":\"update t_design_address set rank={2} where id={3}\"}}]", new object[] { rank, id, rank2, id2 });
            BatchAction(cmd);
        }

        /// <summary>
        /// 批量
        /// </summary>
        /// <param name="cmd"></param>
        private void BatchAction(String cmd)
        {
            WebClient wc = new WebClient();
            Indicator.IsBusy = true;
            wc.UploadStringCompleted += wc_UploadStringCompleted;
            wc.UploadStringAsync(new Uri(list.WebClientInfo.BaseAddress), cmd);
        }

        void wc_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            Indicator.IsBusy = false;
            if (e.Error != null)
                MessageBox.Show("操作失败！");
        }

        /// <summary>
        /// 更新当前节点
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BtnSave_Click(object sender, RoutedEventArgs e)
        {
            if ((EditGrid.DataContext as SearchObject).HasErrors)
            {
                MessageBox.Show("请填写名称。");
                return;
            }
            Indicator.IsBusy = true;
            String newName = (EditGrid.DataContext as SearchObject).GetPropertyValue("AName").ToString();
            //判断重复
            if (!IsDuplicated(false, newName))
            {
                string cmd2 = synchronous(curItem, newName);
                cmd2 = "{\"operator\":\"sql\", \"data\":\"" + cmd2 + "\"}";
                curItem.Header = newName;
                GeneralObject go = curItem.Tag as GeneralObject;
                go.SetPropertyValue("col1", newName, false);
                String cmd = String.Format("{{\"operator\":\"sql\", \"data\":\"update t_design_address set name='{0}', remark='{1}' where id={2}\"}}", new object[] { newName, (EditGrid.DataContext as SearchObject).GetPropertyValue("Remark"), go.GetPropertyValue("col7") });
                string data = "[" + cmd + "," + cmd2 + "]";
                BatchAction(data);
            }
            else
            {
                Indicator.IsBusy = false;
                MessageBox.Show("该名称已经存在。");
                return;
            }
                
        }

        private string synchronous(TreeViewItem curItem, string newName)
        {
            string sql = "";
            GeneralObject go = curItem.Tag as GeneralObject;
            int level = Int32.Parse(go.GetPropertyValue("col3") + "");
            switch (level)
            {
                case 0:
                    sql = "update t_gasaddress set f_cusDom='" + newName + "' where f_cusDom='" + curItem.Header + "'";
                    break;
                case 1:
                    sql = "update t_gasaddress set f_cusDy='" + newName + "' where f_cusDy='" + curItem.Header + "'";
                    break;
                case 2:
                    sql = "update t_gasaddress set f_cusFloor='" + newName + "' where f_cusFloor='" + curItem.Header + "'";
                    break;
                case 3:
                    sql = "update t_gasaddress set f_apartment='" + newName + "' where f_apartment='" + curItem.Header + "'";
                    break;
                default:
                    break;
            }
            TreeViewItem tvi = curItem;
            for (int i = level; i >= 0; i--)
            {
                switch (i)
                {
                    case 0:
                        //小区
                        string f_districtname = tvi.GetParentTreeViewItem().Header.ToString();
                        sql += " and f_districtname='" + f_districtname + "' ";
                        break;
                    case 1:
                        //楼号
                        string f_cusDom = tvi.GetParentTreeViewItem().Header.ToString();
                        sql += " and f_cusDom='" + f_cusDom + "' ";
                        break;
                    case 2:
                        //单元
                        string f_cusDy = tvi.GetParentTreeViewItem().Header.ToString();
                        sql += " and f_cusDy='" + f_cusDy + "' ";
                        break;
                    case 3:
                        //楼层
                        string f_cusFloor = tvi.GetParentTreeViewItem().Header.ToString();
                        sql += " and f_cusFloor='" + f_cusFloor + "' ";
                        break;
                    default:
                        break;
                }
                tvi = tvi.GetParentTreeViewItem();
            }
            return sql;
        }

        private bool IsDuplicated(bool checkCurItem, string value)
        {
            TreeViewItem tvi = curItem.GetParentTreeViewItem();
            for(int i=0; i<tvi.Items.Count; i++)
            {
                if (tvi.Items[i] == curItem && checkCurItem)
                    continue;
                if (((tvi.Items[i] as TreeViewItem).Tag as GeneralObject).GetPropertyValue("col1").ToString().Equals(value))
                    return true;
            }
            return false;
        }

        private object[] arguments = null;

        /// <summary>
        /// 添加子节点
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BtnAddChild_Click(object sender, RoutedEventArgs e)
        {
            if ((EditGrid.DataContext as SearchObject).HasErrors)
            {
                MessageBox.Show("请填写名称。");
                return;
            }
            Indicator.IsBusy = true;
            //查找是否重复
            String newName = (EditGrid.DataContext as SearchObject).GetPropertyValue("AName").ToString();
            object remark = (EditGrid.DataContext as SearchObject).GetPropertyValue("Remark");
            if (remark == null)
                remark = "";

            bool duplicated = false;
            GeneralObject curGo = curItem.Tag as GeneralObject;
            int level = 0;
            if(String.IsNullOrEmpty(curGo.EntityType))
                level = int.Parse(curGo.GetPropertyValue("col3").ToString()) + 1;
            if(level==4)
            {
                Indicator.IsBusy = false;
                MessageBox.Show("房间下不能再添加节点！");
                return;
            }
            String iid = "";
            if (String.IsNullOrEmpty(curGo.EntityType))
                iid = curGo.GetPropertyValue("col0") + "000000";
            else
                iid = curGo.GetPropertyValue("id").ToString().PadLeft(6,'0') + "000000";
            ItemCollection ic = curItem.Items;
            for (int i = 0; i < ic.Count; i++)
            {
                GeneralObject item = (ic[i] as TreeViewItem).Tag as GeneralObject;
                if (item.GetPropertyValue("col1").ToString().Equals(newName))
                    duplicated = true;
                String tiid = item.GetPropertyValue("col0").ToString();
                if(tiid.CompareTo(iid) > 0)
                {
                    iid = tiid;
                }
            }

            //判断重复
            if (duplicated)
            {
                Indicator.IsBusy = false;
                MessageBox.Show("该名称已经存在。");
                return;
            }
            
            iid = iid.Substring(0, iid.Length - 6) + (int.Parse(iid.Substring(iid.Length-6))+1).ToString().PadLeft(6,'0');
            int rank = 1;
            object pid = curGo.GetPropertyValue("col0");
            if(!String.IsNullOrEmpty(curGo.EntityType))
                pid = curGo.GetPropertyValue("id").ToString();
            if (curItem.Items.Count != 0)
            {
                //计算排序号
                GeneralObject go = (curItem.Items[curItem.Items.Count - 1] as TreeViewItem).Tag as GeneralObject;
                rank = int.Parse(go.GetPropertyValue("col6").ToString()) + 1;
                level = int.Parse(go.GetPropertyValue("col3").ToString());
            }
            arguments = new object[] { iid, newName, remark, rank, level, pid };

            //如果是叶子，插入t_gasaddress 并保存到t_design_address
            if (level == 3)
            {
                GeneralObject goAddress = new GeneralObject();
                goAddress.EntityType = "t_gasaddress";
                goAddress.WebClientInfo = App.Current.Resources["dbclient"] as WebClientInfo;
                goAddress.SetPropertyValue("f_apartment", newName, false);
                goAddress.SetPropertyValue("f_cusFloor", curItem.Header, false);
                goAddress.SetPropertyValue("f_cusDy", curItem.GetParentTreeViewItem().Header, false);
                goAddress.SetPropertyValue("f_cusDom", curItem.GetParentTreeViewItem().GetParentTreeViewItem().Header, false);
                TreeViewItem residentialArea = curItem.GetParentTreeViewItem().GetParentTreeViewItem().GetParentTreeViewItem();
                goAddress.SetPropertyValue("f_districtname", residentialArea.Header, false);
                goAddress.SetPropertyValue("f_road", (residentialArea.Tag as GeneralObject).GetPropertyValue("f_road"), false);
                goAddress.Completed += goAddress_Completed;
                goAddress.Name = "asd";
                goAddress.Save();
            }

            //只保存入t_design_address
            else 
            {
                GeneralObject address = new GeneralObject();
                address.Name = "address";
                address.EntityType = "t_design_address";
                address.WebClientInfo = App.Current.Resources["dbclient"] as WebClientInfo;
                address.SetPropertyValue("iid", arguments[0], false);
                address.SetPropertyValue("name", arguments[1], false);
                address.SetPropertyValue("remark", arguments[2], false);
                address.SetPropertyValue("rank", arguments[3], false);
                address.SetPropertyValue("level", arguments[4], false);
                address.SetPropertyValue("pid", arguments[5], false);
                address.SetPropertyValue("col0", arguments[0], false);
                address.SetPropertyValue("col1", arguments[1], false);
                address.SetPropertyValue("col2", arguments[2], false);
                address.SetPropertyValue("col6", arguments[3].ToString(), false);
                address.SetPropertyValue("col3", arguments[4].ToString(), false);
                address.SetPropertyValue("col4", arguments[5], false);
                address.Completed += address_Completed;
                address.Save();
            }

        }

        /// <summary>
        /// 保存设计地址成功
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void address_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            Indicator.IsBusy = false;
            if (e.Error != null)
            {
                MessageBox.Show("操作失败！");
                return;
            }

            //修改对象备用
            GeneralObject go = sender as GeneralObject;
            go.SetPropertyValue("col7", go.GetPropertyValue("id").ToString(), false);
            go.EntityType = "";
            TreeViewItem tv = new TreeViewItem();
            tv.Header = arguments[1];
            tv.Tag = go;
            curItem.Items.Add(tv);
        }

        /// <summary>
        /// 保存t_gasaddress
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void goAddress_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if(e.Error != null)
            {
                Indicator.IsBusy = false;
                MessageBox.Show("操作失败！");
                return;
            }

            object addressId = (sender as GeneralObject).GetPropertyValue("ID").ToString();
 //IID, Name, Remark, Level, PID, ResidentialAreaName, rank, ID, addressId
 //arguments = new object[] { iid, newName, remark, rank, level, pid };
            GeneralObject address = new GeneralObject();
            address.Name = "address";
            address.EntityType = "t_design_address";
            address.WebClientInfo = App.Current.Resources["dbclient"] as WebClientInfo;
            address.SetPropertyValue("iid", arguments[0], false);
            address.SetPropertyValue("name", arguments[1], false);
            address.SetPropertyValue("remark", arguments[2], false);
            address.SetPropertyValue("rank", arguments[3], false);
            address.SetPropertyValue("level", arguments[4], false);
            address.SetPropertyValue("pid", arguments[5], false);
            address.SetPropertyValue("addressId", addressId, false);
            address.SetPropertyValue("col0", arguments[0], false);
            address.SetPropertyValue("col1", arguments[1], false);
            address.SetPropertyValue("col2", arguments[2], false);
            address.SetPropertyValue("col6", arguments[3].ToString(), false);
            address.SetPropertyValue("col3", arguments[4].ToString(), false);
            address.SetPropertyValue("col4", arguments[5], false);
            address.SetPropertyValue("col8", addressId, false);
            address.Completed += address_Completed;
            address.Save();
        }

        /// <summary>
        /// 地址同步
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnSync_Click(object sender, RoutedEventArgs e)
        {
            root.Items.Clear();
            WebClient wc = new WebClient();
            Indicator.IsBusy = true;
            wc.DownloadStringCompleted += wc_DownloadStringCompleted;
            wc.DownloadStringAsync(new Uri((App.Current.Resources["server"] as WebClientInfo).BaseAddress + "/uas"));
        }

        void wc_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            (sender as WebClient).DownloadStringCompleted -= wc_DownloadStringCompleted;
            Indicator.IsBusy = false;
            if(e.Error != null)
            {
                MessageBox.Show("同步失败！");
                return;
            }
            curItem = root;
            btnRefresh_Click(null, null);
        }   
}

}
