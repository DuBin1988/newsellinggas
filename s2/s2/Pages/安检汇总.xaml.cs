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
    public partial class 安检汇总 : UserControl
    {
        public 安检汇总()
        {
            InitializeComponent();
        }

        private void btnSearch_Click(object sender, RoutedEventArgs e)
        {
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
            String dt2 = "1=1";
            if (StartDate.Text.Trim().Length != 0)
                dt2 = " SAVE_DATE>='" + StartDate.Text + "'";
            if (EndDate.Text.Trim().Length != 0)
            {
                if (dt2.Length > 3)
                    dt2 += " and SAVE_DATE<='" + EndDate.Text + " 23:59:59'";
                else
                    dt2 = " SAVE_DATE<='" + EndDate.Text + " 23:59:59'";
            }
            checkerList.Path = "sql";
            checkerList.Names = "jiancha,ruhu,wuren,jujian,louqi,biao,tongzhishu";
            String sql = @"SELECT jiancha,ruhu,wuren,jujian,(SELECT COUNT (id) FROM (SELECT id FROM T_INSPECTION WHERE (f_qblouqi = 1 OR f_jpglouqi = 1 OR f_lglouqi = 1 OR f_cooker_leakage = 1 OR f_heater_leakage = 1 OR f_furnace_leakage = 1 OR f_bhglouqi = 1 OR f_biaoqianfa = '漏气' OR f_zaoqianfa = '漏气' OR f_zibifa = '漏气' OR f_zjxianzhuang = '漏气' OR f_rshqxianzhuang = '漏气' OR f_bglxianzhuang = '漏气') AND {1}) a) louqi,(SELECT COUNT (id) FROM(SELECT id FROM T_INSPECTION WHERE (f_sibiao = 1 OR f_changtong = 1 OR f_fanzhuang = 1 OR f_qblouqi = 1 OR f_reading_mismatch = 1 OR (f_qibiao IS NOT NULL AND f_qibiao <> ''))  AND {1}) b) biao,( SELECT COUNT (id) FROM (SELECT id,save_people FROM T_INSPECTION WHERE f_renow_id IS NOT NULL AND f_renow_id <> '' AND {1}) c) tongzhishu FROM (SELECT COUNT (id) jiancha,SUM (ruhu) ruhu,SUM (wuren) wuren,	SUM (jujian) jujian FROM(SELECT id,CAST (CASE condition	WHEN '正常' THEN 1 ELSE 0 END AS INTEGER) ruhu,	CAST (CASE condition WHEN '无人' THEN 1 ELSE 0 END AS INTEGER) wuren,CAST (CASE condition WHEN '拒检' THEN 1 ELSE 0 END AS INTEGER) jujian FROM T_INSPECTION WHERE {1}) d) e";
            checkerList.HQL = String.Format(sql, new String[] { dt2, dt });
            checkerList.Load();
        }
    }
}

