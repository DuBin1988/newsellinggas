
/*����ǰ�����ݿ����*/
/*1.AdminManage*/
select * from AdminManage
/*�����ݷ������α�Ϊ����Ա��ֻ��PassWordΪ���������룬��������������ı����棬��½�˺�ΪSerils
�˱������ϵͳ��t_user����һ����
*/

/*2.AirsType*/
select * from AirsType
/*
�����ݷ������˱�Ϊ�������ͱ�
1	1	������		2.19	7	
2	2	��ҵ����	2.60	7	
3	3	��ס�û�	2.35	7	
������ϵͳ��t_userfiles���е�f_yhxz<��������>һ��
*/

/*6.DiYu*/
select * from DiYu
/*
�����ݷ��������˱�Ϊ�����������ϵͳ��С����һ��
*/
/*8.FHBICCard*/
select * from FHBICCard
/*��TypeID�п������˱�Ϊ�û��������߲���ʱ�ļ�¼��*/

/*9.ICCardManage*/
select * from ICCardManage
/*
1	1	����	0.00	
2	2	����	20.00	
�˱����Ϊ����������ʱ�õĲ�����
*/
/*12.QiBiaoManage*/
select Name,COUNT(*) from QiBiaoManage group by Name
select * from QiBiaoManage where Name  ='4'
/*ò��Name�������ã�Serils���û���ţ�StateΪʹ��״̬,�˱�Ϊ������Ϣ��*/

/*14.QiBiaoType*/
select * from QiBiaoType
/*
1	NULL	G2.5IC	6	101	qwkrom
2	NULL	G4IC	6	1001	qwkrom
3	NULL	J2.5IC	10	500	goldmy
4	NULL	J4IC	10	1000	goldmy
5	NULL	J6IC	10	1000	goldgs
6	NULL	G6IC	10	1000	qwkrom
�����ݷ��������˱�Ϊ�������ͱ�
*/

/*16.STBQiCard*/
select * from STBQiCard
/*
7748��
�����ݷ������˱�Ϊ����������Ϣ��*/

/*18.UserManage*/
select * from UserManage where Serils = 'SJ00000033'
select * from UserManage where ICNumber is null
select * from QiBiaoManage
/*2415 ��
�˱�Ϊ�û���
*/

/*����*/
/*�Ѿ�ϵͳ�е�FHBICCard<���������� 2478��>�� AirsType<�������ʱ� 3��>��AdminManage<����Ա�� 13��>,ICCardManage<�������������� 2��>��
DiYu<С���� 28>��QiBiaoManage<������Ϣ�� 2419��>,QiBiaoType<�������ͱ� 6��>,STBQiCard<����������Ϣ��  7748��>��UserManage<�û��� 2415��>
�ɹ����뵽sellingtest����
*/
/*�ظ����û����*/
select Serils,COUNT(*) FROM UserManage GROUP BY Serils HAVING COUNT(*)>1;
/*������*/
/*���*/
select * from t_userfiles1;
/*���*/
delete from t_userfiles1;
delete from t_userfiles;
delete from t_SellingGas;
/*��������������f_userid,*/
alter table t_userfiles1 Drop column f_userid;
ALTER TABLE t_userfiles1 ADD f_userid int IDENTITY(11000001,1);

/*��ϵͳ��������---2443*/
select COUNT(*) from UserManage ;

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
    f_oldid,old)


select  
	c.ICNumber,c.UserName,c.UserAddress,--���ţ��û�������ַ
    (case c.YongQiType --�û�����
	when '1' then '����'
	when '2' then '����'
	else '��ס'
	end)f_usertype,
	(case c.YongQiType --��������
	when '1' then '����'
	when '2' then '����'
	else '��ס'
	end)f_gasproperties,
	(case c.YongQiType --��������
	when '1' then '2.19'
	when '2' then '2.60'
	else '2.35'
	end)f_gaspricetype,
	c.SumAirs,--������
	c.SumAirs,--�ܹ�����
    c.LinkTel,c.KHDate,
    (select FactoryCode from QiBiaoType e where e.Serils = q.Name)f_gaswatchbrand, --����Ʒ��
    (select FactoryCode from QiBiaoType e where e.Serils = q.Name)f_gasmetermanufacturers ,--������
	 (select 
	(case FactoryCode
	 when 'qwkrom' then 'keluomu'
	 when 'goldmy' then 'jinka'
	 else 'jinka'
	 end)
	 from QiBiaoType e where e.Serils = q.Name)f_aliasname,--����
	'����',--��������
	 '����','�ѷ�','δ��',c.FKDate,
	 (select GongQiDian from DiYu d where c.DiYuSeris = d.ID)f_districtname,
	(case c.YongQiType --����
	when '1' then '2.19'
	when '2' then '2.60'
	else '2.35'
	end)f_gasprice,
	(select count(*) from STBQiCard s  
	where s.Seris=c.Serils and s.QiShuLiang>0 and s.TypeID in ('����','����') group by Seris)f_times, --д������
	(case c.YongQiType --�����
	when '1' then '2.5'
	else '50'
	end)f_flownum,--�����
	null,c.UserName,--��ϵ��
	(select e.Name from QiBiaoType e 
	 where e.Serils = q.Name)f_metertype ,--�����ͺ�
	 '0',null,
	 '������������ȼ�����޹�˾','11','�๦��������Ӫҵ��',c.KHAdmin,c.KHDate,0,0,'����ƤĤ��',
	  '���֤',--֤������
	 c.PersonID,--֤������
	 '2114-12-30',
	  c.YongQiType ,            --������
	 '10','9999','0',12,
	 (select e.Serils from QiBiaoManage e where c.Airserils = e.id)f_oldid,c.Serils  
   from UserManage c 
   left join QiBiaoManage q on c.Airserils = q.ID
   ;
   
   --xiao hu 20
update U set u.f_userstate = '����' from t_userfiles1 u 
left join UserManage e on e.Serils = u.old
where e.XHAdmin is not null
--weifaka 2
update t_userfiles1 set f_whethergivecard = 'δ��' where f_cardid is null and f_userstate <> '����'

   
   delete from t_userfiles;
   /*Ȼ���t_userfile1�е����ݻ�ԭ��t_userfiles��*/
   select * from t_userfiles
   delete from t_SellingGas;
  --���뿨�������� 7891
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
        '�ֽ�',c.ShifuMoney,c.ShifuMoney,null,null,'�๦��������Ӫҵ��',
       c.UserName,
       '������������ȼ�����޹�˾',u.f_usertype,'�����շ�','��Ч',c.TimeStr,
      null   
from STBQiCard c left join t_userfiles u on c.Seris=u.old; 
--     
select * from t_sellinggas

------����С����
delete  from t_area
insert into t_area(
	f_inputtor,f_watchnum,f_maichong,f_flownum,f_districtname,
	f_address,f_gasproperties,f_gaspricetype,f_gasprice,f_changjia,
	f_metertype,f_gaswatchbrand,f_aliasname,f_gasmeterstyle,f_accountVolume,
	f_userNum,f_operator,f_date,
	f_department,
	f_filiale,f_fengongsinum)
 
select  
    NULL,--ÿ��С���ĳ���Ա��ȷ��
    0,0,2.5,u.GongQiDian,
    u.GongQiDian,'����','��������',2.19,'������',
    'G2.5','����','jibiao','����',0,
    800,NULL,NULL,
    '�๦��������Ӫҵ��',
	'������������ȼ�����޹�˾','11'
from DiYu u
select * from t_area
--�ü��볭��Ա   

select *  from DiYu

--��˻������û�û����
SJ00000033	2	124	1	����
SJ00000034	2	135	1	���
RH00000037	5	229	1	����ѧ
BY00000215	10	671	1	����÷
MD00000035	12	957	1	�ŷﻨ
DS00000337	5	1226	1	������
KL00000008	12	2159	1	����ܿ
MD00000379	12	2200	1	�Ʊ���
 --����Ϊδ��
update t_userfiles set f_whethergivecard = 'δ��' where f_cardid is null and old is not null
--610�������û���f_gasmeterstyle����Ϊ����
update t_userfiles set  f_gasmeterstyle = '����' where f_cardid is null and old is  null
--2470�����˿��Ŀ����û�f_gasmeterstyle����Ϊ����
update t_userfiles set  f_gasmeterstyle = '����' where f_cardid is not null
--7��û�����Ŀ����û�f_gasmeterstyle����Ϊ����
update t_userfiles set f_gasmeterstyle = '����' where f_cardid is null and old is not null
--�����¼��δ�����Ϊ��82
update t_handplan set shifoujiaofei = '��' where shifoujiaofei = 'δ����'
--�����¼�б��γ����������Ϊ10,��ѯǷ�Ѽ�¼ʱ�õ�
update t_handplan set lastrecord = 10 where shifoujiaofei = '��'
--��������Ʒ��
update t_userfiles set f_gaswatchbrand = '���' where f_cardid is null and f_whethergivecard ='�ѷ�'
--��������Ʒ��
update t_userfiles set f_gaswatchbrand = '���' where f_cardid is not null and f_aliasname ='tianjia'
--���½��ѽ�ֹ����
update t_handplan set f_endjfdate = DATEADD("D" ,20,"lastinputdate")
--����linjieqi
update t_handplan set linjienqj = '2.19',linjiewqj = '2.19' 
--���������Ϊ�յ� 
update t_userfiles set f_zhye = '0' where f_gasmeterstyle = '����' and f_zhye is null
select f_zhye from  t_userfiles  where f_gasmeterstyle = '����' and f_zhye is null
--��������
update t_userfiles set f_yytdepa = '�๦��������Ӫҵ��'
--��������14
update t_userfiles set f_gaspricetype = '��������' where f_gaspricetype = '2.60'
--��������3
update t_userfiles set f_gaspricetype = '��ס����' where f_gaspricetype = '2.35'
--��������
update t_userfiles set f_usertype = '����' , f_gasproperties = '����' where f_gaspricetype = '��ס����'
--��������3070
update t_userfiles set f_gaspricetype = '��������' where f_gaspricetype = '2.19'
--����Ա����
update t_user set f_parentname = '�๦��������Ӫҵ��'
--����������������
update t_sellinggas set f_gaspricetype = '��������' where f_gaspricetype = '2.19'
update t_sellinggas set f_gaspricetype = '��ס����' where f_gaspricetype = '2.35'
update t_sellinggas set f_gaspricetype = '��������' where f_gaspricetype = '2.60'
--С���зֹ�˾��
update t_area set f_filiale = '������������ȼ�����޹�˾'
--t_cardresetwatch�зֹ�˾
update t_cardresetwatch set f_fengongsi = '������������ȼ�����޹�˾'
--���±��3087
update t_userfiles set f_meternumber = f_oldid
--�����û����п���ķ�𿨿���
update t_userfiles set f_cardid = SUBSTRING(old,3,8) + SUBSTRING(old,1,2) where old is not null
--�����û�����ѵĿ���Ϊ���
update t_userfiles set f_cardid = f_oldid where  f_gaswatchbrand = '���' and f_cardid is not null
--�����������п���ķ�ͽ𿨵Ŀ���
update t set f_cardid = SUBSTRING(old,3,8) + SUBSTRING(old,1,2) from t_sellinggas t 
left join t_userfiles u on t.f_userid = u.f_userid where
old is not null
--��������������ѿ��Ŀ���Ϊ���
update s set f_cardid  = t.f_cardid from t_sellinggas s
left join t_userfiles t on s.f_userid = t.f_userid where 
t.f_gaswatchbrand = '���' and t.f_cardid is not null
--�����������й�˾����
update t_sellinggas set f_comtype = '��Ȼ����˾'
--�����������еı���
update s set s.f_aliasname = u.f_aliasname
from  t_sellinggas s
left join t_userfiles u on s.f_userid = u.f_userid
--���µ����н𿨵Ŀ���
update t_userfiles set f_cardid = SUBSTRING(old,3,10) where f_aliasname = 'jinka'
--�����������н𿨵Ŀ���
update t_sellinggas set f_cardid = SUBSTRING(f_cardid,1,8) where f_aliasname = 'jinka'
--���»���������¼��Ӧ�����Ϊ����������Ϊ����
update t_sellinggas set f_preamount = -1*f_preamount where f_preamount < 0