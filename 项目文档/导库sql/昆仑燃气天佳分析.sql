/*1.AREAINFO������Ϣ��*/
select * from AREAINFO
/*
1	һƬ��		1
2	��Ƭ��		1
*/

/*2.CardIdInfo ����ʱ��*/
select * from PAYDATA
/*
1	2013-12-04 11:12:03.623
*/
/*4.CNGTYPE�������ͱ�*/
select * from CNGTYPE
/*
2	����2.19	2.19	0.00	20.00	0.00	0
3	��ҵ2.6	2.60	0.00	20.00	0.00	1
*/

/*5.EMPLOYEES����Ա��*/
select * from EMPLOYEES
/*9.METERDATA �����ݱ�*/
select * from METERDATA
select * from METERDATA WHERE USID = '432'
/*10.METERSINFO �������ͱ�*/
select * from METERSINFO
/*
1	2.5�������	������������Ǳ����޹�˾		0	0	 	 	  	0	0	0	 	1		 	NULL	NULL	NULL	NULL
2	IC����4.0������	�������	0	10	0	0	1	00	0	0	0	0	0	NULL	NULL	1.50	0	0	0
3	IC����2.5������	������������Ǳ�	0	0	0	0	1	00	0	0	0	0	0	NULL	NULL	0.00	0	0	0
*/

/*11.dbo.NONAUTOMEMTERDATA �����*/
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
/*12.PAYDATA  �ɷѱ�*/
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
/*13.SMALLAREAINFO С����*/
select * from SMALLAREAINFO

/*14.USERINFO  �û���*/
select * from USERINFO where USMETERSNO = '20067345'
select * from USERINFO where USMETERSNO = '0020004490'
select * from USERINFO where USCNGNU <> '0'--���������
select * from USERINFO where USID = '20067345'
select * from USERINFO where USNAME like '����֥'

select USCODENO,* from USERINFO where USCODENO is not null 
select USAREA,* from USERINFO where USUNIT is  null 
select SMAID from USERINFO GROUP BY SMAID
select USACCOUNT,* from USERINFO where USAREA = '2'
select * from USERINFO c
full join NONAUTOMEMTERDATA n on  n.USID = C.USID
--638



/*����
1.�Ѿ�ϵͳ�е�<USERINFO  �û��� 638>,<SMALLAREAINFO С���� 8>,<PAYDATA  �ɷѱ� 3033>,<NONAUTOMEMTERDATA ����� 2797>
<METERSINFO �������ͱ� 3>,<METERDATA �����ݱ� 20>,<CNGTYPE�������ͱ� 2>,<AREAINFO������Ϣ�� 2>���뵽sellingtest����
*/
/*�ظ����û����*/
select USMETERSNO,COUNT(*) FROM USERINFO GROUP BY USMETERSNO HAVING COUNT(*)>1;
--������
select * from t_userfiles1;
/*���*/
delete from t_userfiles1;
delete from t_userfiles;
delete from t_SellingGas;
/*��������������f_userid,*/
select f_userid from t_userfiles order by f_userid
alter table t_userfiles1 Drop column f_userid;
ALTER TABLE t_userfiles1 ADD f_userid int IDENTITY(11002416,1);--��ʼ�����ó��ϸ���������+1

/*��ϵͳ�û�����������---638*/
select COUNT(*) from USERINFO ;

--���뵵����
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
	c.USCODENO,c.USNAME,c.USADDRESS,--��ַ
    (case c.USTYPE --�û�����
	when '0' then '����'
	else '������'
	end)f_usertype,
	(case c.USTYPE --��������
	when '0' then '����'
	else '������'
	end)f_gasproperties,
	(case c.USTYPE 
	when '0' then '2.19'
	else '2.6'
	end)f_gaspricetype,--��������
	(select max(NACNGDATA) 
	from NONAUTOMEMTERDATA a where USID = C.USID)f_metergasnums,--������
	(select max(NACNGDATA) 
	from NONAUTOMEMTERDATA a where USID = C.USID)f_cumulativepurchase,--�ܹ�����
    c.USPHONE,c.OpenerTime
    ,'�������','������������Ǳ����޹�˾',
	 'tianjia',--����
	 (select M.MENAME from METERSINFO m where m.MESID = c.USMETERTYPE)f_gasmeterstyle,--��������
	 (case c.USSTATE --�û�״̬
	when '1' then '����'
	else '������'
	end)f_userstate,
	 '�ѷ�','δ��',C.InitCardTime,
	 (select M.SMANAME from SMALLAREAINFO m where m.SMAID = c.SMAID)f_districtname,--С������
	 (case c.USTYPE --����
	when '0' then '2.19'
	else '2.60'
	end)f_gasprice,
	(select count(*) from PAYDATA s  where s.USID=c.USID and s.PADNUM>0 and PADTYEP in ('0','1') group by USID)f_times,--д������
	(case c.USTYPE --�����
	when '0' then '2.5'
	else '50'
	end)f_flownum,--�����
	C.RoomNO,c.USNAME,--��ϵ��
	(select M.MENAME from METERSINFO m where m.MESID = c.USMETERTYPE)f_metertype ,--�����ͺ�
	 c.USCNGNU,c.USMETERSNO,--���
	 '����ȼ�����޹�˾','11','������Ӫҵ��',
	 (select top 1 M.EMNAME from PAYDATA m where m.USID = c.USID order by M.EMNAME desc)f_yytoper,--����Ա
	 C.OpenerTime,0,0,'����ƤĤ��',
	  '���֤',--֤������
	 null,--֤������
	 '2114-12-30',
	 (case c.USTYPE 
	 when '0' then '1'
	 else '2'
	 end)klx,            --������
	 '10','9999','0',12,
	 c.USMETERSNO,C.USID,C.USACCOUNT
   from USERINFO c 
   ;
 
/*2 ��t_userfiles1���뵽t_userfiles��*/
select f_userid from t_userfiles group by f_userid
select f_userid from t_userfiles order by f_userid
select * from t_userfiles 

--����������
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
        '�ֽ�',c.PAACTIVEMONEY,c.PADPAYMONEY,null,null,'������Ӫҵ��',
       c.EMNAME,
       '����ȼ�����޹�˾',
       (case c.PADTYEP
        when 0 then '�Զ��ۿ�'
        when 1 then '�����ɷ�'
        when 2 then 'Ԥ��'
       end)
       ,
       (case U.f_cardid 
		 when null then '������'
		 else '������'
		 end)f_payfeetype,
       '��Ч'
       ,c.PADDATE,
       NULL,PALATEFEE    
from PAYDATA c left join t_userfiles u on c.USID=u.oldID;

select * from t_userfiles where oldID is not null
select f_userid from t_sellinggas order by f_userid
select f_userid from t_sellinggas where f_userid is null

--���볭���¼
/*���*/
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
       gas.NACNGNUM,0,NULL,NULL,gas.WriteDateTime,U.f_zhye,u.f_zhye,--�˻�����
       (case gas.NACOPYUSER
        WHEN '4' THEN '�Ż���'
		when '5' then '�����'
		when '6' then '������'
		when '10' then '����'
		when '11' then '�亣��'
		when '15' then '����'
		end
       )f_inputtor,--����Ա
		'������Ӫҵ��',null,--¼��Ա
      '����ȼ�������޹�˾',null,
	  null,null,gas.USID,
	  (case gas.NAPAYMENT 
		 when '0' then '��'
		 else '��'
		 end)f_payfeetype,
	  '�ѳ���','�ֽ�',
      gas.NAMETERNO,gas.NALATEFEE 
from NONAUTOMEMTERDATA gas
 left join t_userfiles u on gas.USID=u.oldid

select oughtfee,* from t_handplan

--�����������������Ӧ�����
update t_handplan set oughtfee = f_gasprice * oughtamount where oughtamount<>0;
--���ϲ��������Ӧ���������������벢������λ��Ч����
update t_handplan set oughtfee = round(oughtfee,2);
select oughtfee,* from t_handplan where oughtamount = 0


------����С����

insert into t_area(
	f_inputtor,f_watchnum,f_maichong,f_flownum,f_districtname,
	f_address,f_gasproperties,f_gaspricetype,f_gasprice,f_changjia,
	f_metertype,f_gaswatchbrand,f_aliasname,f_gasmeterstyle,f_accountVolume,
	f_userNum,f_operator,f_date,
	f_department,
	f_filiale,f_fengongsinum)
 
select  
    NULL,--ÿ��С���ĳ���Ա��ȷ��
    0,0,2.5,u.SMANAME,
    u.SMANAME,'����','��������',2.19,'������',
    'G2.5','����','jibiao','����',0,
    800,NULL,NULL,
    '������Ӫҵ��',
	'����ȼ�����޹�˾','11'
from SMALLAREAINFO u


SELECT * FROM t_area 
select * from SMALLAREAINFO



update t_sellinggas set f_paytype = '�ֽ�' --���ѷ�ʽ��Ϊ�ֽ�
update t_sellinggas set f_payment = '�ֽ�'--���ѷ�ʽ��Ϊ�ֽ�
update t_userfiles set f_payment = '�ֽ�'--���ѷ�ʽ��Ϊ�ֽ�

select f_gaswatchbrand,* from t_userfiles

update t_userfiles set f_gaswatchbrand = '����ķ' --���ı���
where f_aliasname = 'keluomu';

update t_userfiles set f_gaswatchbrand = '��'--���ı���
where f_aliasname = 'jinka';

update t_userfiles set f_gaswatchbrand = '��'--���ı���
where f_aliasname = 'tianjia';
 
select f_userstate,* from t_userfiles where f_userstate = '������'
update t_userfiles set f_userstate = '����' where f_userstate = '������'  --���Ĳ������û�Ϊ����

select f_payfeetype,f_cardid,* from t_sellinggas where f_payfeetype = '������'
update t_sellinggas set f_payfeetype = '�����շ�' where f_cardid is null
update t_sellinggas set f_payfeetype = '�����շ�' where f_payfeetype = '������'
update t_sellinggas set f_payfeetype = '�����շ�' where f_payfeetype = '������'

--�˻����
select f_payfeetype,f_zhye,* from t_sellinggas where f_payfeetype = '�����շ�'
select * from t_sellinggas where  f_zhye is not null
select * from t_userfiles where  f_zhye is not null
select * from t_userfiles where  f_cardid is  null
update s 
set s.f_zhye = u.f_zhye
from t_sellinggas s
left join  t_userfiles u on u.f_userid = s.f_userid 
where u.f_cardid is  null

--���»���������¼��Ӧ�����Ϊ����������Ϊ����
update t_sellinggas set f_preamount = -1*f_preamount where f_preamount < 0