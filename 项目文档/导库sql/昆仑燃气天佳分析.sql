/*1.AREAINFO区域信息表*/
select * from AREAINFO
/*
1	一片区		1
2	二片区		1
*/

/*2.CardIdInfo 开卡时间*/
select * from PAYDATA
/*
1	2013-12-04 11:12:03.623
*/
/*4.CNGTYPE气价类型表*/
select * from CNGTYPE
/*
2	民用2.19	2.19	0.00	20.00	0.00	0
3	商业2.6	2.60	0.00	20.00	0.00	1
*/

/*5.EMPLOYEES管理员表*/
select * from EMPLOYEES
/*9.METERDATA 表数据表*/
select * from METERDATA
select * from METERDATA WHERE USID = '432'
/*10.METERSINFO 气表类型表*/
select * from METERSINFO
/*
1	2.5立方天佳	银川天佳仪器仪表有限公司		0	0	 	 	  	0	0	0	 	1		 	NULL	NULL	NULL	NULL
2	IC卡（4.0立方）	银川天佳	0	10	0	0	1	00	0	0	0	0	0	NULL	NULL	1.50	0	0	0
3	IC卡（2.5立方）	银川天佳仪器仪表	0	0	0	0	1	00	0	0	0	0	0	NULL	NULL	0.00	0	0	0
*/

/*11.dbo.NONAUTOMEMTERDATA 抄表表*/
select * from NONAUTOMEMTERDATA
select NACNGNUM ,* from NONAUTOMEMTERDATA where NACNGNUM  = 0
select NACNGNUM ,* from NONAUTOMEMTERDATA WHERE USID = 46
select * from NONAUTOMEMTERDATA where NAMETERNO = '0020060929'
select * from NONAUTOMEMTERDATA where NAMETERNO = '0020004490'
select NACOPYUSER from NONAUTOMEMTERDATA group by NACOPYUSER
select * from NONAUTOMEMTERDATA WHERE NACNGDATA > NACNGNUM
select max(NACNGDATA) from NONAUTOMEMTERDATA a,USERINFO c where a.USID = '93' 
select top 1 NACNGDATA from NONAUTOMEMTERDATA where USID = '93' order by NACNGDATA desc 


--2797
/*12.PAYDATA  缴费表*/
select * from PAYDATA WHERE PadGasMoney = 0.00
select * from PAYDATA WHERE PADPAYMONEY = 0.00
select * from PAYDATA where PAMETERNO = '0020060929'
select * from PAYDATA where USID = '577'
select USID,COUNT(*) from PAYDATA  GROUP BY USID
select *,U.USID from PAYDATA p
left join USERINFO u on u.USID = p.USID
where u.USCODENO is not null
GROUP BY U.USID
--3033
/*13.SMALLAREAINFO 小区表*/
select * from SMALLAREAINFO

/*14.USERINFO  用户表*/
select * from USERINFO where USMETERSNO = '20067345'
select * from USERINFO where USMETERSNO = '0020004490'
select * from USERINFO where USCNGNU <> '0'--两户有余额
select * from USERINFO where USID = '20067345'
select * from USERINFO where USNAME like '李灵芝'

select USCODENO,* from USERINFO where USCODENO is not null 
select USAREA,* from USERINFO where USUNIT is  null 
select SMAID from USERINFO GROUP BY SMAID
select USACCOUNT,* from USERINFO where USAREA = '2'
select * from USERINFO c
full join NONAUTOMEMTERDATA n on  n.USID = C.USID
--638



/*导库
1.把旧系统中的<USERINFO  用户表 638>,<SMALLAREAINFO 小区表 8>,<PAYDATA  缴费表 3033>,<NONAUTOMEMTERDATA 抄表表 2797>
<METERSINFO 气表类型表 3>,<METERDATA 表数据表 20>,<CNGTYPE气价类型表 2>,<AREAINFO区域信息表 2>导入到sellingtest库中
*/
/*重复的用户编号*/
select USMETERSNO,COUNT(*) FROM USERINFO GROUP BY USMETERSNO HAVING COUNT(*)>1;
--不存在
select * from t_userfiles1;
/*清库*/
delete from t_userfiles1;
delete from t_userfiles;
delete from t_SellingGas;
/*重新设置自增列f_userid,*/
select f_userid from t_userfiles order by f_userid
alter table t_userfiles1 Drop column f_userid;
ALTER TABLE t_userfiles1 ADD f_userid int IDENTITY(11002416,1);--起始数设置成上个表最后的数+1

/*旧系统用户表数据数量---638*/
select COUNT(*) from USERINFO ;

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
    f_oldid,oldid,f_zhye)
    
    
select  
	c.USCODENO,c.USNAME,c.USADDRESS,--地址
    (case c.USTYPE --用户类型
	when '0' then '民用'
	else '非民用'
	end)f_usertype,
	(case c.USTYPE --用气性质
	when '0' then '民用'
	else '非民用'
	end)f_gasproperties,
	(case c.USTYPE 
	when '0' then '2.19'
	else '2.6'
	end)f_gaspricetype,--气价性质
	(select max(NACNGDATA) 
	from NONAUTOMEMTERDATA a where USID = C.USID)f_metergasnums,--表购气量
	(select max(NACNGDATA) 
	from NONAUTOMEMTERDATA a where USID = C.USID)f_cumulativepurchase,--总购气量
    c.USPHONE,c.OpenerTime
    ,'银川天佳','银川天佳仪器仪表有限公司',
	 'tianjia',--别名
	 (select M.MENAME from METERSINFO m where m.MESID = c.USMETERTYPE)f_gasmeterstyle,--气表类型
	 (case c.USSTATE --用户状态
	when '1' then '正常'
	else '不正常'
	end)f_userstate,
	 '已发','未发',C.InitCardTime,
	 (select M.SMANAME from SMALLAREAINFO m where m.SMAID = c.SMAID)f_districtname,--小区名称
	 (case c.USTYPE --气价
	when '0' then '2.19'
	else '2.60'
	end)f_gasprice,
	(select count(*) from PAYDATA s  where s.USID=c.USID and s.PADNUM>0 and PADTYEP in ('0','1') group by USID)f_times,--写卡次数
	(case c.USTYPE --额定流量
	when '0' then '2.5'
	else '50'
	end)f_flownum,--额定流量
	C.RoomNO,c.USNAME,--联系人
	(select M.MENAME from METERSINFO m where m.MESID = c.USMETERTYPE)f_metertype ,--气表型号
	 c.USCNGNU,c.USMETERSNO,--表号
	 '昆仑燃气有限公司','11','体育场营业厅',
	 (select top 1 M.EMNAME from PAYDATA m where m.USID = c.USID order by M.EMNAME desc)f_yytoper,--操作员
	 C.OpenerTime,0,0,'家用皮膜表',
	  '身份证',--证件类型
	 null,--证件号码
	 '2114-12-30',
	 (case c.USTYPE 
	 when '0' then '1'
	 else '2'
	 end)klx,            --卡类型
	 '10','9999','0',12,
	 c.USMETERSNO,C.USID,C.USACCOUNT
   from USERINFO c 
   ;
 
/*2 把t_userfiles1导入到t_userfiles中*/
select f_userid from t_userfiles group by f_userid
select f_userid from t_userfiles order by f_userid
select * from t_userfiles 

--导入售气表
insert into t_SellingGas(
            f_deliverydate,f_usertype,f_gasproperties,f_gaspricetype,f_gaswatchbrand,f_gasprice,f_userid, 
            f_pregas, f_preamount,f_cardid,f_username,
            f_payment,f_grossproceeds,f_totalcost,f_amountmaintenance,
            f_givechange,f_sgnetwork,f_sgoperator,
            f_filiale,f_oldtype,f_payfeetype,f_payfeevalid,f_deliverytime,
            f_beizhu,f_zhinajin)
select 
       c.PADDATE,
       u.f_usertype,
       u.f_gasproperties,
       u.f_gaspricetype,
       u.f_gaswatchbrand,
       c.PADPRICE,
       u.f_userid,	
       C.PADNUM,C.PADPAYMONEY,U.f_cardid,
       u.f_username,
        '现金',c.PAACTIVEMONEY,c.PADPAYMONEY,null,null,'体育场营业厅',
       c.EMNAME,
       '昆仑燃气有限公司',
       (case c.PADTYEP
        when 0 then '自动扣款'
        when 1 then '正常缴费'
        when 2 then '预存'
       end)
       ,
       (case U.f_cardid 
		 when null then '机表交费'
		 else '卡表交费'
		 end)f_payfeetype,
       '有效'
       ,c.PADDATE,
       NULL,PALATEFEE    
from PAYDATA c left join t_userfiles u on c.USID=u.oldID;

select * from t_userfiles where oldID is not null
select f_userid from t_sellinggas order by f_userid
select f_userid from t_sellinggas where f_userid is null

--导入抄表记录
/*清库*/
delete from t_handplan;
insert into t_handplan(
            f_inputdate,f_userid,f_username,f_usertype,f_gasproperties,f_gaspricetype,f_gaswatchbrand,f_metertype,
            f_gasprice,f_cardid,f_address,f_aliasname,f_districtname,f_phone,f_gasmetermanufacturers,f_gasmeterstyle,  
            oughtamount, oughtfee,lastinputgasnum,lastrecord,lastinputdate,f_zhye,f_bczhye,
            f_inputtor,
            f_network,f_operator,f_filiale,f_usermc,f_bankname,f_idofcard,f_olduserid,shifoujiaofei,f_state
            ,paytype,f_meternumber,f_zhinajin)
            

select 
       gas.WriteDateTime,u.f_userid,gas.USNAME,
        U.f_usertype,
		u.f_gasproperties,
        u.f_gaspricetype,
        u.f_gaswatchbrand,u.f_metertype,gas.NAPRICE,
       NULL,u.f_address,u.f_aliasname,u.f_districtname,u.f_phone,u.f_gasmetermanufacturers,u.f_gasmeterstyle, 
       gas.NACNGNUM,0,NULL,NULL,gas.WriteDateTime,U.f_zhye,u.f_zhye,--账户结余
       (case gas.NACOPYUSER
        WHEN '4' THEN '张慧玲'
		when '5' then '杨滨滋'
		when '6' then '聂贝贝'
		when '10' then '王莉'
		when '11' then '武海燕'
		when '15' then '安钢'
		end
       )f_inputtor,--抄表员
		'体育场营业厅',null,--录入员
      '昆仑燃气气有限公司',null,
	  null,null,gas.USID,
	  (case gas.NAPAYMENT 
		 when '0' then '否'
		 else '是'
		 end)f_payfeetype,
	  '已抄表','现金',
      gas.NAMETERNO,gas.NALATEFEE 
from NONAUTOMEMTERDATA gas
 left join t_userfiles u on gas.USID=u.oldid

select oughtfee,* from t_handplan

--根据气量和气价算出应交金额
update t_handplan set oughtfee = f_gasprice * oughtamount where oughtamount<>0;
--对上步计算出的应交金额进行四舍五入并保留两位有效数字
update t_handplan set oughtfee = round(oughtfee,2);
select oughtfee,* from t_handplan where oughtamount = 0


------导入小区表

insert into t_area(
	f_inputtor,f_watchnum,f_maichong,f_flownum,f_districtname,
	f_address,f_gasproperties,f_gaspricetype,f_gasprice,f_changjia,
	f_metertype,f_gaswatchbrand,f_aliasname,f_gasmeterstyle,f_accountVolume,
	f_userNum,f_operator,f_date,
	f_department,
	f_filiale,f_fengongsinum)
 
select  
    NULL,--每个小区的抄表员得确认
    0,0,2.5,u.SMANAME,
    u.SMANAME,'民用','民用气价',2.19,'机表厂家',
    'G2.5','机表','jibiao','机表',0,
    800,NULL,NULL,
    '体育场营业厅',
	'昆仑燃气有限公司','11'
from SMALLAREAINFO u


SELECT * FROM t_area 
select * from SMALLAREAINFO



update t_sellinggas set f_paytype = '现金' --交费方式设为现金
update t_sellinggas set f_payment = '现金'--交费方式设为现金
update t_userfiles set f_payment = '现金'--交费方式设为现金

select f_gaswatchbrand,* from t_userfiles

update t_userfiles set f_gaswatchbrand = '克罗姆' --更改表名
where f_aliasname = 'keluomu';

update t_userfiles set f_gaswatchbrand = '金卡'--更改表名
where f_aliasname = 'jinka';

update t_userfiles set f_gaswatchbrand = '金卡'--更改表名
where f_aliasname = 'tianjia';
 
select f_userstate,* from t_userfiles where f_userstate = '不正常'
update t_userfiles set f_userstate = '销户' where f_userstate = '不正常'  --更改不正常用户为销户

select f_payfeetype,f_cardid,* from t_sellinggas where f_payfeetype = '卡表交费'
update t_sellinggas set f_payfeetype = '机表收费' where f_cardid is null
update t_sellinggas set f_payfeetype = '卡表收费' where f_payfeetype = '卡表交费'
update t_sellinggas set f_payfeetype = '机表收费' where f_payfeetype = '基表交费'

--账户余额
select f_payfeetype,f_zhye,* from t_sellinggas where f_payfeetype = '机表收费'
select * from t_sellinggas where  f_zhye is not null
select * from t_userfiles where  f_zhye is not null
select * from t_userfiles where  f_cardid is  null
update s 
set s.f_zhye = u.f_zhye
from t_sellinggas s
left join  t_userfiles u on u.f_userid = s.f_userid 
where u.f_cardid is  null

--更新机表售气记录中应交金额为负数的数据为正数
update t_sellinggas set f_preamount = -1*f_preamount where f_preamount < 0