          --------------------------------------------------------------------------------------------
           微狗(UMI/UMC/PMH/PMI)Windows 9X/ME/NT/2K/XP/WS2003/Vista/XP64/WS2003x64/Vista64
           /Windows 7 (x86/x64)/Windows server 2008 驱动安装卸载程序使用说明
          --------------------------------------------------------------------------------------------
                       版权所有 (C)  2010  赛孚耐（北京）信息技术有限公司

  微狗(UMI/UMC/PMH/PMI)驱动安装程序是安装在 Windows 9X/ME/NT/2K/XP/WS2003/Vista
  /XP64/WS2003x64/Vista64/Windows 7 (x86/x64)/Windows server 2008环境下的驱动的程序。

  加密后的 Win32 应用程序在 Windows 9x以及Windows ME 下运行时，需要安装硬件狗
  Windows 9x 的设备驱动程序。

  加密后的 DOS16，DOS32，WIN16，WIN32 应用程序在 Windows NT/2K/XP下运行时，必须要安装
  硬件狗 Windows NT/2K/XP 的设备驱动程序。
  
  本版MicroDogInstdrv.exe通过调用RCMicroDogSetup.dll的接口函数DriverDialog来显示驱动安装、
  卸载界面。用户既可以使用MicroDogInstdrv.exe来安装和卸载自己的驱动程序，也可以自行构造界
  面来安装和卸载驱动程序。

  运行MicroDogInstdrv.exe点击安装按钮来安装驱动程序，该程序可自动识别操作系统，并提示安装状态。
  注意：在Windows Vista/WIndows 7/Windows server 2008下，需要右健点击安装程序，在弹出的菜单
  中选择"Run as administrator"来运行安装程序。


  软件开发商不但在加密时需要安装该设备驱动程序，而且在发行软件时，也应根据最终用户的操
  作系统，安装相应的设备驱动程序。

  如果您需要卸载狗的驱动程序，可以运行MicroDogInstdrv.exe点击卸载按钮来卸载驱动程序，软件可以自
  动识别当前环境，完成驱动程序的卸载工作。
 
  同时MicroDogInstdrv.exe支持命令行参数，命令行参数（不区分大小写）说明如下：
  /? 显示命令行帮助
  /i 安装时不显示界面
  /r 卸载时不显示卸载界面
  /a 安装并口狗和USB狗驱动
  /p 只安装或卸载并口狗驱动(需要结合/i或/r参数使用)
  /u 只安装或卸载USB狗驱动(需要结合/i或/r参数使用)
  /s 不显示任何安装和卸载信息
  例: MicroDogInstdrv /i/s 安装过程中不显示任何信息

 	
==========
文件清单
==========

  readme-chn.txt     			本文档
  MicroDogInstdrv.exe  			驱动安装、卸载程序
  RCMicroDogSetup.dll	 		驱动安装动态库
  Delphi<dir> 				调用驱动安装动态库的Delphi例子程序
  InstallShield<dir> 			调用驱动安装动态库的InstallShield例子程序
  VB<dir> 				调用驱动安装动态库的VB例子程序
  VC<dir> 				调用驱动安装动态库的VC例子程序
  PB<dir> 				调用驱动安装动态库的PB例子程序
==================
RCMicroDogSetup.dll 简介
==================

RCMicroDogSetup.dll是WIN32下动态连接库,开发商可用在其加密的软件安装程序中使用如下接口函数。
  
  (1)获取狗驱动程序安装信息接口函数GetDogDriverInfo，定义如下：
  int PASCAL GetDogDriverInfo()；
  参数：无
  返回值：
	0 没有安装驱动程序
	1 驱动版本相同(并口和usb)
	2 USB驱动版本相同
	3 并口驱动版本相同
	4 已安装旧版本驱动 (并口和usb)
	5 已安装旧版本USB驱动
	6 已安装旧版本并口驱动
	7 已安装新版本驱动 (并口和usb)
	8 已安装新版本USB驱动
	9 已安装新版本并口驱动
     3008 没有管理员权限
  (2)驱动安装的函数接口InstDriver，定义如下:
  int PASCAL InstDriver(int iFlag)；
  参数：
  	iFlag = 1 只安装USB狗驱动
	iFlag = 2 只安装并口狗驱动
	iFlag = 3 安装并口狗驱动和USB狗驱动
  返回值：
  如果运行成功则返回0,否则返回错误值。

  (3)驱动卸载的函数接口UninstallDriver，定义如下:
  int PASCAL UninstallDriver(int iFlag)；
  参数：
  	iFlag = 1 只卸载USB狗驱动
	iFlag = 2 只卸载并口狗驱动
	iFlag = 3 卸载并口狗驱动和USB狗驱动
  返回值：
  如果运行成功则返回0,否则返回错误值。
  (4)显示驱动安装和卸载的界面的函数接口DriverDialog，定义如下：
  void PASCAL DriverDialog()；
  参数：无
  返回值：无
  
  (5)在Windows98/me上是否提示重新插拔USB狗的接口NotifyPullOutAndPlugInUsbDog，定义如下：
  void PASCAL NotifyPullOutAndPlugInUsbDog(int iFlag);
  参数：
  	iFlag = 0 不显示提示对话框
	iFlag = 1 显示提示对话框
  返回值：无

  详情请参见各个例子程序的源代码。

=========
 错误码
=========

  3001	打开驱动服务失败
  3002	启动驱动服务失败
  3003  拷贝文件失败
  3004  操作注册表失败
  3005	不支持的操作系统
  3006  停止驱动失败
  3007  驱动卸载失败,没有过安装此类型驱动!
  3008	不是管理员身份
  3009	无效的服务类型
  3010  映射系统库函数错误
  3011	加载系统库错误
  3012	没有发现USB狗设备
  3013	更新usb狗驱动失败
  3014  删除文件失败
  3015	另外一个驱动安装进程正在运行
  3016	另外一个驱动卸载进程正在运行
