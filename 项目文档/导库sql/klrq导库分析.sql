
/*重庆前卫数据库分析*/
/*1.AdminManage*/
select * from AdminManage
/*从数据分析看次表为管理员表，只是PassWord为机器生成码，密码可能在其他的表里面，登陆账号为Serils
此表和我们系统的t_user表是一样的
*/

/*2.AirsType*/
select * from AirsType
/*
从数据分析看此表为用气类型表
1	1	民用气		2.19	7	
2	2	商业用气	2.60	7	
3	3	商住用户	2.35	7	
和我们系统的t_userfiles表中的f_yhxz<用气性质>一样
*/

/*6.DiYu*/
select * from DiYu
/*
从数据分析看，此表为供气区域表，与系统的小区表一样
*/
/*8.FHBICCard*/
select * from FHBICCard
/*从TypeID中看到，此表为用户发卡或者补卡时的记录表*/

/*9.ICCardManage*/
select * from ICCardManage
/*
1	1	发卡	0.00	
2	2	补卡	20.00	
此表可能为发卡，补卡时用的参数表
*/
/*12.QiBiaoManage*/
select Name,COUNT(*) from QiBiaoManage group by Name
select * from QiBiaoManage where Name  ='4'
/*貌似Name不起作用，Serils是用户编号，State为使用状态,此表为气表信息表*/

/*14.QiBiaoType*/
select * from QiBiaoType
/*
1	NULL	G2.5IC	6	101	qwkrom
2	NULL	G4IC	6	1001	qwkrom
3	NULL	J2.5IC	10	500	goldmy
4	NULL	J4IC	10	1000	goldmy
5	NULL	J6IC	10	1000	goldgs
6	NULL	G6IC	10	1000	qwkrom
从数据分析看，此表为气表类型表
*/

/*16.STBQiCard*/
select * from STBQiCard
/*
7748条
从数据分析看此表为卡表售气信息表*/

/*18.UserManage*/
select * from UserManage where Serils = 'SJ00000033'
select * from UserManage where ICNumber is null
select * from QiBiaoManage
/*2415 条
此表为用户表
*/

/*导库*/
/*把旧系统中的FHBICCard<发卡补卡表 2478条>， AirsType<用气性质表 3条>，AdminManage<管理员表 13条>,ICCardManage<发卡补卡参数表 2条>，
DiYu<小区表 28>，QiBiaoManage<气表信息表 2419条>,QiBiaoType<气表类型表 6条>,STBQiCard<卡表售气信息表  7748条>，UserManage<用户表 2415条>
成功导入到sellingtest库中
*/
/*重复的用户编号*/
select Serils,COUNT(*) FROM UserManage GROUP BY Serils HAVING COUNT(*)>1;
/*不存在*/
/*清库*/
select * from t_userfiles1;
/*清库*/
delete from t_userfiles1;
delete from t_userfiles;
delete from t_SellingGas;
/*重新设置自增列f_userid,*/
alter table t_userfiles1 Drop column f_userid;
ALTER TABLE t_userfiles1 ADD f_userid int IDENTITY(11000001,1);

/*旧系统数据数量---2443*/
select COUNT(*) from UserManage ;

--导入档案表
insert into t_userfiles1(
	f_cardid, f_username,f_address,
	f_usertype,
	f_gasproperties,
	f_gaspricetype,
	f_metergasnums,
	f_cumulativepurchase,
	f_phone,f_dateofopening,f_gaswatchbrand,f_gasmetermanufacturers,
	f_aliasname,
	f_gasmeterstyle,
	f_userstate,f_whethergivecard,f_whethergivepassbook,f_givecarddate,f_districtname,
	f_gasprice,
	f_times,
	f_flownum,
	f_apartment,f_linkname,
    f_metertype,
    f_gasmeteraccomodations,f_meternumber,
    f_filiale,f_fengongsinum,f_yytdepa,f_yytoper,f_yytdate,f_watchnum,f_maichong,f_type,
    f_credentials,f_idnumber,f_beginfee,klx,bjql,czsx,tzed,f_zherownum,
    f_oldid,old)


select  
	c.ICNumber,c.UserName,c.UserAddress,--卡号，用户名，地址
    (case c.YongQiType --用户类型
	when '1' then '民用'
	when '2' then '商用'
	else '商住'
	end)f_usertype,
	(case c.YongQiType --用气性质
	when '1' then '民用'
	when '2' then '商用'
	else '商住'
	end)f_gasproperties,
	(case c.YongQiType --气价类型
	when '1' then '2.19'
	when '2' then '2.60'
	else '2.35'
	end)f_gaspricetype,
	c.SumAirs,--表购气量
	c.SumAirs,--总购气量
    c.LinkTel,c.KHDate,
    (select FactoryCode from QiBiaoType e where e.Serils = q.Name)f_gaswatchbrand, --气表品牌
    (select FactoryCode from QiBiaoType e where e.Serils = q.Name)f_gasmetermanufacturers ,--气表厂家
	 (select 
	(case FactoryCode
	 when 'qwkrom' then 'keluomu'
	 when 'goldmy' then 'jinka'
	 else 'jinka'
	 end)
	 from QiBiaoType e where e.Serils = q.Name)f_aliasname,--别名
	'卡表',--气表类型
	 '正常','已发','未发',c.FKDate,
	 (select GongQiDian from DiYu d where c.DiYuSeris = d.ID)f_districtname,
	(case c.YongQiType --气价
	when '1' then '2.19'
	when '2' then '2.60'
	else '2.35'
	end)f_gasprice,
	(select count(*) from STBQiCard s  
	where s.Seris=c.Serils and s.QiShuLiang>0 and s.TypeID in ('售气','补气') group by Seris)f_times, --写卡次数
	(case c.YongQiType --额定流量
	when '1' then '2.5'
	else '50'
	end)f_flownum,--额定流量
	null,c.UserName,--联系人
	(select e.Name from QiBiaoType e 
	 where e.Serils = q.Name)f_metertype ,--气表型号
	 '0',null,
	 '阿拉善盟昆仑燃气有限公司','11','多功能体育场营业厅',c.KHAdmin,c.KHDate,0,0,'家用皮膜表',
	  '身份证',--证件类型
	 c.PersonID,--证件号码
	 '2114-12-30',
	  c.YongQiType ,            --卡类型
	 '10','9999','0',12,
	 (select e.Serils from QiBiaoManage e where c.Airserils = e.id)f_oldid,c.Serils  
   from UserManage c 
   left join QiBiaoManage q on c.Airserils = q.ID
   ;
   
   --xiao hu 20
update U set u.f_userstate = '销户' from t_userfiles1 u 
left join UserManage e on e.Serils = u.old
where e.XHAdmin is not null
--weifaka 2
update t_userfiles1 set f_whethergivecard = '未发' where f_cardid is null and f_userstate <> '销户'

   
   delete from t_userfiles;
   /*然后把t_userfile1中的数据还原到t_userfiles中*/
   select * from t_userfiles
   delete from t_SellingGas;
  --导入卡表售气表 7891
   insert into t_sellinggas(
            f_deliverydate,f_usertype,f_gasproperties,f_gaspricetype,f_gaswatchbrand,f_gasprice,f_userid, 
            f_pregas, f_preamount,f_cardid,f_username,
            f_payment,f_grossproceeds,f_totalcost,f_amountmaintenance,
            f_givechange,f_sgnetwork,f_sgoperator,
            f_filiale,f_oldtype,f_payfeetype,f_payfeevalid,f_deliverytime,
            f_beizhu)
            
   select 
       c.TimeStr,
       u.f_usertype,
       u.f_gasproperties,
       u.f_gaspricetype,
       u.f_gaswatchbrand,
       c.Price,
       u.f_userid,	
       c.QiShuLiang,c.SumMoney,c.ChaiBiao,
       u.f_username,
        '现金',c.ShifuMoney,c.ShifuMoney,null,null,'多功能体育场营业厅',
       c.UserName,
       '阿拉善盟昆仑燃气有限公司',u.f_usertype,'卡表收费','有效',c.TimeStr,
      null   
from STBQiCard c left join t_userfiles u on c.Seris=u.old; 
--     
select * from t_sellinggas

------导入小区表
delete  from t_area
insert into t_area(
	f_inputtor,f_watchnum,f_maichong,f_flownum,f_districtname,
	f_address,f_gasproperties,f_gaspricetype,f_gasprice,f_changjia,
	f_metertype,f_gaswatchbrand,f_aliasname,f_gasmeterstyle,f_accountVolume,
	f_userNum,f_operator,f_date,
	f_department,
	f_filiale,f_fengongsinum)
 
select  
    NULL,--每个小区的抄表员得确认
    0,0,2.5,u.GongQiDian,
    u.GongQiDian,'民用','民用气价',2.19,'机表厂家',
    'G2.5','卡表','jibiao','卡表',0,
    800,NULL,NULL,
    '多功能体育场营业厅',
	'阿拉善盟昆仑燃气有限公司','11'
from DiYu u
select * from t_area
--得加入抄表员   

select *  from DiYu

--这八户卡表用户没发卡
SJ00000033	2	124	1	陈涛
SJ00000034	2	135	1	托娅
RH00000037	5	229	1	刘尚学
BY00000215	10	671	1	刘红梅
MD00000035	12	957	1	张凤花
DS00000337	5	1226	1	王利明
KL00000008	12	2159	1	孙立芸
MD00000379	12	2200	1	闫宝库
 --更新为未发
update t_userfiles set f_whethergivecard = '未发' where f_cardid is null and old is not null
--610户机表用户的f_gasmeterstyle更新为机表
update t_userfiles set  f_gasmeterstyle = '机表' where f_cardid is null and old is  null
--2470个发了卡的卡表用户f_gasmeterstyle更新为卡表
update t_userfiles set  f_gasmeterstyle = '卡表' where f_cardid is not null
--7户没发卡的卡表用户f_gasmeterstyle更新为卡表
update t_userfiles set f_gasmeterstyle = '卡表' where f_cardid is null and old is not null
--抄表记录中未抄表改为否82
update t_handplan set shifoujiaofei = '否' where shifoujiaofei = '未交费'
--抄表记录中本次抄表底数更新为10,查询欠费记录时用到
update t_handplan set lastrecord = 10 where shifoujiaofei = '否'
--更正气表品牌
update t_userfiles set f_gaswatchbrand = '天佳' where f_cardid is null and f_whethergivecard ='已发'
--更正气表品牌
update t_userfiles set f_gaswatchbrand = '天佳' where f_cardid is not null and f_aliasname ='tianjia'
--更新交费截止日期
update t_handplan set f_endjfdate = DATEADD("D" ,20,"lastinputdate")
--更新linjieqi
update t_handplan set linjienqj = '2.19',linjiewqj = '2.19' 
--更新账余额为空的 
update t_userfiles set f_zhye = '0' where f_gasmeterstyle = '机表' and f_zhye is null
select f_zhye from  t_userfiles  where f_gasmeterstyle = '机表' and f_zhye is null
--更新网点
update t_userfiles set f_yytdepa = '多功能体育场营业厅'
--气价类型14
update t_userfiles set f_gaspricetype = '公服气价' where f_gaspricetype = '2.60'
--气价类型3
update t_userfiles set f_gaspricetype = '商住气价' where f_gaspricetype = '2.35'
--气价类型
update t_userfiles set f_usertype = '民用' , f_gasproperties = '民用' where f_gaspricetype = '商住气价'
--气价类型3070
update t_userfiles set f_gaspricetype = '民用气价' where f_gaspricetype = '2.19'
--操作员网点
update t_user set f_parentname = '多功能体育场营业厅'
--售气表中气价类型
update t_sellinggas set f_gaspricetype = '民用气价' where f_gaspricetype = '2.19'
update t_sellinggas set f_gaspricetype = '商住气价' where f_gaspricetype = '2.35'
update t_sellinggas set f_gaspricetype = '公服气价' where f_gaspricetype = '2.60'
--小区中分公司名
update t_area set f_filiale = '阿拉善盟昆仑燃气有限公司'
--t_cardresetwatch中分公司
update t_cardresetwatch set f_fengongsi = '阿拉善盟昆仑燃气有限公司'
--更新表号3087
update t_userfiles set f_meternumber = f_oldid
--更新用户表中克罗姆金卡卡号
update t_userfiles set f_cardid = SUBSTRING(old,3,8) + SUBSTRING(old,1,2) where old is not null
--更新用户表天佳的卡号为表号
update t_userfiles set f_cardid = f_oldid where  f_gaswatchbrand = '天佳' and f_cardid is not null
--更新售气表中克罗姆和金卡的卡号
update t set f_cardid = SUBSTRING(old,3,8) + SUBSTRING(old,1,2) from t_sellinggas t 
left join t_userfiles u on t.f_userid = u.f_userid where
old is not null
--更新售气表中天佳卡的卡号为表号
update s set f_cardid  = t.f_cardid from t_sellinggas s
left join t_userfiles t on s.f_userid = t.f_userid where 
t.f_gaswatchbrand = '天佳' and t.f_cardid is not null
--更新售气表中公司类型
update t_sellinggas set f_comtype = '天然气公司'
--更新售气表中的别名
update s set s.f_aliasname = u.f_aliasname
from  t_sellinggas s
left join t_userfiles u on s.f_userid = u.f_userid
--更新档案中金卡的卡号
update t_userfiles set f_cardid = SUBSTRING(old,3,10) where f_aliasname = 'jinka'
--更新售气表中金卡的卡号
update t_sellinggas set f_cardid = SUBSTRING(f_cardid,1,8) where f_aliasname = 'jinka'
--更新机表售气记录中应交金额为负数的数据为正数
update t_sellinggas set f_preamount = -1*f_preamount where f_preamount < 0