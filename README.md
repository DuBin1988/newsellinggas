# newsellinggas
###收费系统

### 资源服务后台添加 公司组织架构后 需要执行一次sql  每次添加或者更改都要执行一次
### update o2 set o2.f_parentname=o1.NAME from t_organization o1 ,t_organization o2 where o1.ID =o2.PARENTID
### 关联父节点名字
### 账户在总公司下面的话 该账户t_user表里f_parentname 需要手动录入总公司资源服务里的名称 


## * t_area t_road t_stairprict t_inputtor 中 f_branch字段是 取 loingUser 的f_fengongsi
## * 超级管理员的 分公司 应该是 总公司名称
## * 收费记录 抄表记录 保存 f_orgStr 组织架构信息 和 f_filiale 分公司 字段。

## * 单值设置 用户编号 和 表编号  两个单值 。 建档页面用   /Pages/用户建档/用户建档

## * 信息列表页面用 查询功能下面的用户档案信息查询可以弃用。  /Pages/信息列表/档案信息列表 

## * 短信功能。 /Pages/短信发送 /Pages/短信功能管理	/Pages/短信模板管理	/Pages/短信信息	/Pages/自定义短信发送 t_sms短信表 t_smstemplate短信模板表

```c
#include<stdio.h>

int main()
{
    printf("hello world!\n");
    
    return 0;
}
```
 
