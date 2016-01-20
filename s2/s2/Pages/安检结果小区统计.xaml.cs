using Com.Aote.ObjectTools;
using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Json;
using System.Collections.Generic;

namespace Com.Aote.Pages
{
	public partial class 安检结果小区统计 : UserControl
	{
        PagedList hejilists = new PagedList();
        PagedList areafileslist = new PagedList();
        GeneralObject hejilist = new GeneralObject();
        public 安检结果小区统计()
		{
			InitializeComponent();
		}

        private void so_heji(object sender, RoutedEventArgs e)
        {
            SearchObject so = areafilesearch.DataContext as SearchObject;
            so.Search();

            String sql = "" +
                //小区，安检情况
                        " count(DISTINCT UNIT_NAME) xiaoqs,COUNT(t1.id) yonghs, COUNT(zhuangt1.id) yiaj，COUNT(zhuangt2.id) wuren，COUNT(zhuangt3.id) jujian," +
                //设备用途
                        " (COUNT(sb2.id)+COUNT(sb3.id)+COUNT(sb5.id)+COUNT(sb6.id)) sbhj,COUNT(sb1.id) issb,COUNT(sb2.id) sbdj,COUNT(sb3.id) sbrsq,COUNT(sb5.id) sbbg,COUNT(sb6.id) sbqn," +
                //供暖方式
                        " (COUNT(gn2.id)+COUNT(gn3.id)+COUNT(gn5.id)+COUNT(gn6.id)) gnhj,COUNT(gn1.id) isgn,COUNT(gn2.id) guablgn,COUNT(gn3.id) jizhgn,COUNT(gn5.id) qunqgn,COUNT(gn6.id) qit," +
                //重点户
                        " (COUNT(zd2.id)+COUNT(zd3.id)+COUNT(zd5.id)+COUNT(zd6.id)) zdhj,COUNT(zd1.id) iszd,COUNT(zd2.id) qilcr,COUNT(zd3.id) zhongdyh,COUNT(zd5.id) guglr,COUNT(zd6.id) buszdh," +
                //小区性质
                        " (COUNT(xqxz2.id)+COUNT(xqxz3.id)+COUNT(xqxz5.id)+COUNT(xqxz6.id)) xqxzhj,COUNT(xqxz1.id) isxqxz,COUNT(xqxz2.id) anzhxq,COUNT(xqxz3.id) qishiydw,COUNT(xqxz5.id) wuyxq,COUNT(xqxz6.id) zijf," +
                //租住
                        " (COUNT(zzh2.id)+COUNT(zzh3.id)+COUNT(zzh5.id)+COUNT(zzh6.id)+COUNT(zzh7.id)+COUNT(zzh8.id)) zzhhj,COUNT(zzh1.id) iszzh,COUNT(zzh2.id) caiy,COUNT(zzh3.id) putjz,COUNT(zzh5.id) tuogpx,COUNT(zzh6.id) bang,COUNT(zzh7.id) jitss,COUNT(zzh8.id) fou," +
                //报警器
                        " COUNT (CASE WHEN f_baojingqi='有' THEN 1 ELSE NULL END) baojq,count(CASE WHEN (CASE WHEN f_alarm_expire_time is not null THEN substring(CONVERT(varchar(50),GETDATE(),120),1,7)- substring(CONVERT(varchar(50),f_alarm_expire_time,120),1,7) ELSE NULL END)>0 THEN 1 ELSE NULL END) baojqdq," +
                //热水器
                        " count(CASE WHEN f_rshqshiyong=0 THEN 1 ELSE NULL END) resq,count(CASE WHEN (CASE WHEN f_heater_expire_time is not null THEN substring(CONVERT(varchar(50),GETDATE(),120),1,7)- substring(CONVERT(varchar(50),f_heater_expire_time,120),1,7) ELSE NULL END)>0 THEN 1 ELSE NULL END) resqdq," +
                //热水器隐患
                        " (count(CASE WHEN f_heater_overdue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_heater_softconnector=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_heater_trapped=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_heater_emit=1 THEN 1 ELSE NULL END)+  count(CASE WHEN f_heater_leakage=1 THEN 1 ELSE NULL END)) resqyh,count(CASE WHEN f_heater_overdue=1 THEN 1 ELSE NULL END) resqcq,count(CASE WHEN f_heater_softconnector=1 THEN 1 ELSE NULL END) resqrglj, count(CASE WHEN f_heater_trapped=1 THEN 1 ELSE NULL END) resqwpsw,count(CASE WHEN f_heater_emit=1 THEN 1 ELSE NULL END) resqzp,  count(CASE WHEN f_heater_leakage=1 THEN 1 ELSE NULL END) resqlq," +
                //灶具
                        " count(CASE WHEN f_zjshiyong=0 THEN 1 ELSE NULL END) zaoj,count(CASE WHEN (CASE WHEN f_cooker_expire_time is not null THEN substring(CONVERT(varchar(50),GETDATE(),120),1,7)- substring(CONVERT(varchar(50),f_cooker_expire_time,120),1,7) ELSE NULL END)>0 THEN 1 ELSE NULL END) zaojdq," +
                //灶具隐患
                        " (count(CASE WHEN f_cooker_overdue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_cooker_nofireprotection=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_cooker_leakage=1 THEN 1 ELSE NULL END)) zaojyh,count(CASE WHEN f_cooker_overdue=1 THEN 1 ELSE NULL END) zaojcqsy,count(CASE WHEN f_cooker_nofireprotection=1 THEN 1 ELSE NULL END) zaojwbh, count(CASE WHEN f_cooker_leakage=1 THEN 1 ELSE NULL END) zaojlq," +
                //挂壁炉
                        " count(CASE WHEN f_bglshiyong=0 THEN 1 ELSE NULL END) guabz,count(CASE WHEN (CASE WHEN f_furnace_expire_time is not null THEN substring(CONVERT(varchar(50),GETDATE(),120),1,7)- substring(CONVERT(varchar(50),f_furnace_expire_time,120),1,7) ELSE NULL END)>0 THEN 1 ELSE NULL END) guabzdq," +
                //挂壁炉隐患
                        " (count(CASE WHEN f_furnace_overdue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_furnace_softconnector=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_furnace_emit=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_furnace_trapped=1 THEN 1 ELSE NULL END) +count(CASE WHEN f_furnace_leakage=1 THEN 1 ELSE NULL END)) guabzyh,count(CASE WHEN f_furnace_overdue=1 THEN 1 ELSE NULL END) guabzcqsy,count(CASE WHEN f_furnace_softconnector=1 THEN 1 ELSE NULL END) guabzrglj,count(CASE WHEN f_furnace_emit=1 THEN 1 ELSE NULL END) guabzzp,count(CASE WHEN f_furnace_trapped=1 THEN 1 ELSE NULL END) guabzwpsw,count(CASE WHEN f_furnace_leakage=1 THEN 1 ELSE NULL END) guabzlq," +
                //立管隐患
                        " (count(CASE WHEN f_lgbaoguo=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgguawu=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lghuoyuan=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgweiguding=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgchuanyue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgbubianweixiu=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgfushi=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgsigai=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lglouqi=1 THEN 1 ELSE NULL END)) lgyh,count(CASE WHEN f_lgbaoguo=1 THEN 1 ELSE NULL END) lgbg,count(CASE WHEN f_lgguawu=1 THEN 1 ELSE NULL END) lggw,count(CASE WHEN f_lghuoyuan=1 THEN 1 ELSE NULL END) lghyj,count(CASE WHEN f_lgweiguding=1 THEN 1 ELSE NULL END) lgwgd,count(CASE WHEN f_lgchuanyue=1 THEN 1 ELSE NULL END) lgcyjq,count(CASE WHEN f_lgbubianweixiu=1 THEN 1 ELSE NULL END) lgbbwx,count(CASE WHEN f_lgfushi=1 THEN 1 ELSE NULL END) lgfs,count(CASE WHEN f_lgsigai=1 THEN 1 ELSE NULL END) lgsg,count(CASE WHEN f_lglouqi=1 THEN 1 ELSE NULL END) lglq," +
                //表后管隐患
                        " (count(CASE WHEN f_bhgbaoguan=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_bhglouqi=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_bhgdianyuan=1 THEN 1 ELSE NULL END)) bhgyh,count(CASE WHEN f_bhgbaoguan=1 THEN 1 ELSE NULL END) bhgbg,count(CASE WHEN f_bhglouqi=1 THEN 1 ELSE NULL END) bhglq,count(CASE WHEN f_bhgdianyuan=1 THEN 1 ELSE NULL END) bhgdyj, " +
                //表前阀隐患
                        " count(CASE WHEN f_biaoqianfa='漏气' THEN 1 ELSE NULL END)+count(CASE WHEN f_biaoqianfa='失效' THEN 1 ELSE NULL END) bqfyh,count(CASE WHEN f_biaoqianfa='漏气' THEN 1 ELSE NULL END) bqflq，count(CASE WHEN f_biaoqianfa='失效' THEN 1 ELSE NULL END) bqfsx," +
                //灶前阀隐患
                        " count(CASE WHEN f_zaoqianfa='漏气' THEN 1 ELSE NULL END)+count(CASE WHEN f_zaoqianfa='失效' THEN 1 ELSE NULL END) zqfyh,count(CASE WHEN f_zaoqianfa='漏气' THEN 1 ELSE NULL END) zqflq，count(CASE WHEN f_zaoqianfa='失效' THEN 1 ELSE NULL END) zqfsx," +
                //自闭阀隐患
                        " count(CASE WHEN f_zibifa='漏气' THEN 1 ELSE NULL END)+count(CASE WHEN f_zibifa='失效' THEN 1 ELSE NULL END) zbfyh,count(CASE WHEN f_zibifa='漏气' THEN 1 ELSE NULL END) zbflq，count(CASE WHEN f_zibifa='失效' THEN 1 ELSE NULL END) zbfsx," +
                //其他隐患
                        " (count(CASE WHEN f_precaution_kitchen=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_precaution_openkitchen=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_precaution_multisource=1 THEN 1 ELSE NULL END)+count(f_precaution_otheruse)) qtyh, count(CASE WHEN f_precaution_kitchen=1 THEN 1 ELSE NULL END) qtfbcf,count(CASE WHEN f_precaution_openkitchen=1 THEN 1 ELSE NULL END) qtkfs,count(CASE WHEN f_precaution_multisource=1 THEN 1 ELSE NULL END) qtdzhy,count(f_precaution_otheruse) qtggyt," +
                //是否需要维修
                        " count(CASE WHEN NEEDS_REPAIR='是' THEN 1 ELSE NULL END) NEEDS_REPAIR," +
                //是否维修
                        " count(CASE WHEN REPAIR_STATE='未维修' THEN 1 ELSE NULL END) REPAIR_STATE,count(f_renow_id) f_renow_id," +
                //是否换表
                        " count(CASE WHEN f_newmeter=1 THEN 1 ELSE NULL END) rqbhb," +
                //燃气表故障
                        " (count(CASE WHEN f_sibiao=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_changtong=1 THEN 1 ELSE NULL END)+  count(CASE WHEN f_fanzhuang=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_qblouqi=1 THEN 1 ELSE NULL END)+  count(CASE WHEN f_reading_mismatch=1 THEN 1 ELSE NULL END)) rqbgz, count(CASE WHEN f_sibiao=1 THEN 1 ELSE NULL END) rqbsb,  count(CASE WHEN f_changtong=1 THEN 1 ELSE NULL END) rqbct,  count(CASE WHEN f_fanzhuang=1 THEN 1 ELSE NULL END) rqbfz,  count(CASE WHEN f_qblouqi=1 THEN 1 ELSE NULL END) rqblq,  count(CASE WHEN f_reading_mismatch=1 THEN 1 ELSE NULL END) rqbds," +
                //燃气表隐患
                        " (count(CASE WHEN f_meter_wrapped=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_meter_hanger=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_meter_nearfire=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_meter_unfavorable=1 THEN 1 ELSE NULL END)) rqbyh, count(CASE WHEN f_meter_wrapped=1 THEN 1 ELSE NULL END) rqbbg, count(CASE WHEN f_meter_hanger=1 THEN 1 ELSE NULL END) rqbgw,  count(CASE WHEN f_meter_nearfire=1 THEN 1 ELSE NULL END) rqbjhy,  count(CASE WHEN f_meter_unfavorable=1 THEN 1 ELSE NULL END) rqbbbwx," +
                //各项隐患合计
                        " （(count(CASE WHEN f_heater_overdue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_heater_softconnector=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_heater_trapped=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_heater_emit=1 THEN 1 ELSE NULL END)+  count(CASE WHEN f_heater_leakage=1 THEN 1 ELSE NULL END))" +
                        "+(count(CASE WHEN f_cooker_overdue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_cooker_nofireprotection=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_cooker_leakage=1 THEN 1 ELSE NULL END))" +
                        "+(count(CASE WHEN f_furnace_overdue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_furnace_softconnector=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_furnace_emit=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_furnace_trapped=1 THEN 1 ELSE NULL END) +count(CASE WHEN f_furnace_leakage=1 THEN 1 ELSE NULL END))" +
                        "+(count(CASE WHEN f_lgbaoguo=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgguawu=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lghuoyuan=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgweiguding=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgchuanyue=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgbubianweixiu=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgfushi=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lgsigai=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_lglouqi=1 THEN 1 ELSE NULL END))" +
                        "+(count(CASE WHEN f_bhgbaoguan=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_bhglouqi=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_bhgdianyuan=1 THEN 1 ELSE NULL END))" +
                        "+count(CASE WHEN f_biaoqianfa='漏气' THEN 1 ELSE NULL END)+count(CASE WHEN f_biaoqianfa='失效' THEN 1 ELSE NULL END)" +
                        "+count(CASE WHEN f_zaoqianfa='漏气' THEN 1 ELSE NULL END)+count(CASE WHEN f_zaoqianfa='失效' THEN 1 ELSE NULL END)" +
                        "+count(CASE WHEN f_zibifa='漏气' THEN 1 ELSE NULL END)+count(CASE WHEN f_zibifa='失效' THEN 1 ELSE NULL END)" +
                        "+(count(CASE WHEN f_precaution_kitchen=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_precaution_openkitchen=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_precaution_multisource=1 THEN 1 ELSE NULL END)+count(f_precaution_otheruse))" +
                        "+(count(CASE WHEN f_meter_wrapped=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_meter_hanger=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_meter_nearfire=1 THEN 1 ELSE NULL END)+ count(CASE WHEN f_meter_unfavorable=1 THEN 1 ELSE NULL END))"+
                        "+(count(CASE WHEN f_jpglouqi=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpglaohua=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpgguochang=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpgwuguanjia=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpgdiaoding=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpganmai=1 THEN 1 ELSE NULL END))) yhhj," +
                //燃气表到期 
                        " count(CASE WHEN (CASE WHEN f_meter_manufacture_date is not null THEN substring(CONVERT(varchar(50),GETDATE(),120),1,7)- substring(CONVERT(varchar(50),f_meter_manufacture_date,120),1,7) ELSE NULL END)>3650 THEN 1 ELSE NULL END) rqbdq," +
                //灶具管隐患
                       " (count(CASE WHEN f_jpglouqi=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpglaohua=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpgguochang=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpgwuguanjia=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpgdiaoding=1 THEN 1 ELSE NULL END)+count(CASE WHEN f_jpganmai=1 THEN 1 ELSE NULL END)) jbgyh,count(CASE WHEN f_jpglouqi=1 THEN 1 ELSE NULL END) jbglq,count(CASE WHEN f_jpglaohua=1 THEN 1 ELSE NULL END) jbglh,count(CASE WHEN f_jpgguochang=1 THEN 1 ELSE NULL END) jbggc,count(CASE WHEN f_jpgwuguanjia=1 THEN 1 ELSE NULL END) jbgwgj,count(CASE WHEN f_jpgdiaoding=1 THEN 1 ELSE NULL END) jbgdd,count(CASE WHEN f_jpganmai=1 THEN 1 ELSE NULL END) jbgam，count(CASE WHEN f_jpgzhengchang=1 THEN 1 ELSE NULL END) jbgzc," +
                //用气设备统计

                       //各故障占比

                        " COUNT(t1.id) id from (select * from T_INSPECTION where CONDITION is not null and " + so.Condition + " ) t1" +

                        " LEFT JOIN" +
                        " (select id,F_GONGNUAN from T_INSPECTION  where F_GONGNUAN is NULL) gn1" +
                        " on t1.id=gn1.id" +
                        " LEFT JOIN" +
                        " (select id,F_GONGNUAN from T_INSPECTION  where F_GONGNUAN='壁挂炉供暖') gn2" +
                        " on t1.id=gn2.id " +
                        " LEFT JOIN" +
                        " (select id,F_GONGNUAN from T_INSPECTION  where F_GONGNUAN='集中供暖') gn3" +
                        " on t1.id=gn3.id " +
                        " LEFT JOIN" +
                        " (select id,F_GONGNUAN from T_INSPECTION  where F_GONGNUAN='燃气取暖器供暖') gn5" +
                        " on t1.id=gn5.id" +
                        " LEFT JOIN" +
                        " (select id,F_GONGNUAN from T_INSPECTION  where F_GONGNUAN='其他') gn6" +
                        " on t1.id=gn6.id" +
                        " LEFT JOIN" +
                        " (select id,f_iszhongdian from T_INSPECTION  where f_iszhongdian is NULL) zd1" +
                        " on t1.id=zd1.id" +
                        " LEFT JOIN" +
                        " (select id,f_iszhongdian from T_INSPECTION  where f_iszhongdian='气量出入') zd2" +
                        " on t1.id=zd2.id " +
                        " LEFT JOIN" +
                        " (select id,f_iszhongdian from T_INSPECTION  where f_iszhongdian='重大隐患') zd3" +
                        " on t1.id=zd3.id " +
                        " LEFT JOIN" +
                        " (select id,f_iszhongdian from T_INSPECTION  where f_iszhongdian='孤寡老人') zd5" +
                        " on t1.id=zd5.id" +
                        " LEFT JOIN" +
                        " (select id,f_iszhongdian from T_INSPECTION  where f_iszhongdian='否') zd6" +
                        " on t1.id=zd6.id" +
                        " LEFT JOIN" +
                        " (select id,f_property from T_INSPECTION  where f_property is NULL) xqxz1" +
                        " on t1.id=xqxz1.id" +
                        " LEFT JOIN" +
                        " (select id,f_property from T_INSPECTION  where f_property='安置小区') xqxz2" +
                        " on t1.id=xqxz2.id " +
                        " LEFT JOIN" +
                        " (select id,f_property from T_INSPECTION  where f_property='企事业单位') xqxz3" +
                        " on t1.id=xqxz3.id " +
                        " LEFT JOIN" +
                        " (select id,f_property from T_INSPECTION  where f_property='物业小区') xqxz5" +
                        " on t1.id=xqxz5.id" +
                        " LEFT JOIN" +
                        " (select id,f_property from T_INSPECTION  where f_property='自建房') xqxz6" +
                        " on t1.id=xqxz6.id" +
                        " LEFT JOIN" +
                        " (select id,f_zuzhu from T_INSPECTION  where f_zuzhu is NULL) zzh1" +
                        " on t1.id=zzh1.id" +
                        " LEFT JOIN" +
                        " (select id,f_zuzhu from T_INSPECTION  where f_zuzhu='餐饮') zzh2" +
                        " on t1.id=zzh2.id " +
                        " LEFT JOIN" +
                        " (select id,f_zuzhu from T_INSPECTION  where f_zuzhu='普通居住') zzh3" +
                        " on t1.id=zzh3.id " +
                        " LEFT JOIN" +
                        " (select id,f_zuzhu from T_INSPECTION  where f_zuzhu='托管培训') zzh5" +
                        " on t1.id=zzh5.id" +
                        " LEFT JOIN" +
                        " (select id,f_zuzhu from T_INSPECTION  where f_zuzhu='办公') zzh6" +
                        " on t1.id=zzh6.id" +
                        " LEFT JOIN" +
                        " (select id,f_zuzhu from T_INSPECTION  where f_zuzhu='集体宿舍') zzh7" +
                        " on t1.id=zzh7.id" +
                        " LEFT JOIN" +
                        " (select id,f_zuzhu from T_INSPECTION  where f_zuzhu='否') zzh8" +
                        " on t1.id=zzh8.id" +
                        " LEFT JOIN" +
                        " (select id,CONDITION from T_INSPECTION where CONDITION='正常') zhuangt1" +
                        " on t1.id=zhuangt1.id" +
                        " LEFT JOIN" +
                        " (select id,CONDITION from T_INSPECTION where CONDITION='无人') zhuangt2" +
                        " on t1.id=zhuangt2.id" +
                        " LEFT JOIN" +
                        " (select id,CONDITION from T_INSPECTION where CONDITION='拒检') zhuangt3" +
                        " on t1.id=zhuangt3.id" +
                        " LEFT JOIN" +
                        " (select id,f_devices from T_INSPECTION  where f_devices is NULL) sb1" +
                        " on t1.id=sb1.id" +
                        " LEFT JOIN" +
                        " (select id,f_devices from T_INSPECTION  where f_devices='单灶') sb2" +
                        " on t1.id=sb2.id" +
                        " LEFT JOIN" +
                        " (select id,f_devices from T_INSPECTION  where f_devices='灶具+热水器') sb3" +
                        " on t1.id=sb3.id" +
                        " LEFT JOIN" +
                        " (select id,f_devices from T_INSPECTION  where f_devices='灶具+壁挂炉') sb5" +
                        " on t1.id=sb5.id" +
                        " LEFT JOIN" +
                        " (select id,f_devices from T_INSPECTION  where f_devices='灶具+燃气取暖器') sb6" +
                        " on t1.id=sb6.id";

            areafileslist.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            areafileslist.LoadOnPathChanged = false;
            areafileslist.Path = "sql";
            areafileslist.SumNames = "id,";
            areafileslist.PageSize = 1000;
            areafileslist.HQL = "select road,UNIT_NAME," + sql + " group by UNIT_NAME,road";
            areafileslist.PageIndex = 0;
            areafileslist.DataLoaded += areafileslist_DataLoaded;
            areafileslist.LoadDetail();

            hejilists.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            hejilists.LoadOnPathChanged = false;
            hejilists.Path = "sql";
            hejilists.SumNames = "id,";
            hejilists.PageSize = 10;
            hejilists.HQL = "select " + sql + "";
            hejilists.PageIndex = 0;
            hejilists.DataLoaded += hejilists_Completed;
            hejilists.Load();



        }

        void areafileslist_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            PagedList areafileslist = (PagedList)sender;
            if (null != areafileslist && hejilists.Count >= 1)
            {
                areafiles.ItemsSource = areafileslist;
            }
        }


        void hejilists_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            PagedList hejilists = (PagedList)sender;
            if (null != hejilists && hejilists.Count >= 1)
            {
                hejilist = hejilists[0];
                hejiitems.DataContext = null;
                hejiitems.DataContext = hejilist;
            }
            

        }

        private void CheckBox_Click(object sender, RoutedEventArgs e)
        {
            //CheckBox chbx = sender as CheckBox;
            //if (null != chbx.IsChecked && chbx.IsChecked.Value == false)
            //{
            //    chbx.IsChecked = true;

            //}
            
        }

        

	}
    


    /// <summary>
    /// 数据转换    
    /// </summary>
    //public class youwu : IValueConverter
    //{
    //    public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
    //    {
    //        if (null != value)
    //        {
    //            if (targetType != typeof(string)) throw new InvalidOperationException("to String ");
    //            return (bool)value == true ? "无" : "有";
    //        }
    //        return value;
    //    }
    //    public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
    //    {
    //        if (null != value)
    //        {
    //            if (targetType != typeof(bool)) throw new InvalidOperationException("to bool");
    //            return "有".Equals(value.ToString()) ? false : true;
    //        }
    //        return value;
    //    }
    //}

    //public class yesno : IValueConverter
    //{
    //    public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
    //    {
    //        if (null != value)
    //        {
    //            if (targetType != typeof(string)) throw new InvalidOperationException("to String ");
    //            return (bool)value == true ? "是" : "否";
    //        }
    //        return value;
    //    }
    //    public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
    //    {
    //        if (null != value)
    //        {
    //            if (targetType != typeof(bool)) throw new InvalidOperationException("to bool");
    //            return "否".Equals(value.ToString()) ? false : true;
    //        }
    //        return value;
    //    }
    //}


}