//---------------------------------------------------------------------------
#define dll_exp extern "C" __declspec(dllexport)

struct UINFO_OLD
{	byte name[8],code[8],date[10],tele[14],addr[24];
};

struct UINFO_V0
{	
	byte name[8],date[4],tele[12],addr[32],flag[8];
/*
name 用户姓名
date 开户日期 
tele 联系电话
addr 用户住址
flag[4]   0x47 表为气表写气 
flag[5]   0x43 民用     0x49 工业     0x4C 流量计   (民用 2位小数  工业 1位小数  流量计无小数  默认或不为以上时设为民用)
flag[6]   0x41 一位小数 0x42 二位小数 0x43 三位小数 0x44 无小数
*/
};



union zone3
{	char b[64];
	struct UINFO_OLD old;
	struct UINFO_V0 v0;
};

struct response
{	union zone3 ui;
	byte version;  //版本
	ulng code,amt; 
	// 	code 编号 共8位  7位有效编号 前面补0   如 12345格式化后应为00012345
	//	amt 气量   无小数更具卡片小数位来确定数据 1、10、100、100（如卡片上需要写100，卡表型号为民用 那么数据实际为10000，如果为工业数据实际为1000）
};

//---------------------------------------------------------------------------
dll_exp char * Get_DLL_Version(void);			//获取版本信息
dll_exp char * Get_Ini_Path(void);   			//返回库文配置文件路径
dll_exp int    GetErrorMsg(int wErrNo,char *bErrMsg);	//获取错误信息
							//wErrNo  错误序号
							//bErrMsg 100个缓冲  存放返回错误信息

dll_exp int Read_Data(response *rsp);			//读用户卡信息
dll_exp int Write_Data(response *rsp);			//写用户卡数据
dll_exp int Modify_Info(response *rsp);			//修改用户卡信息  卡号不能修改
dll_exp int Write_Info(response *rsp);			//卡片开户

dll_exp int GetInis();					//动态库初始  每次启动程序前先动态库初始一下

dll_exp int Set_IC_Com(int Coms);			//设置配置文件中的IC卡写卡器端口
dll_exp int Set_Show_Info(int SetShowFrm);		//设置表具返回窗口提示 SetShowFrm=1 提示  0 不提示
dll_exp int Get_is_Entontech();				//返回卡类型  0 错误卡型  2 新卡  20 已经开户卡 104 没有插卡


