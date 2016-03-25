# newsellinggas
收费系统

'资源服务后台添加 公司组织架构后 需要执行一次sql  每次添加或者更改都要执行一次
update o2 set o2.f_parentname =o1.NAME 
from t_organization o1 ,t_organization o2 
where o1.ID =o2.PARENTID
关联父节点名字'

账户在总公司下面的话 该账户 t_user 表里 f_parentname 需要手动录入总公司资源服务里的名称
