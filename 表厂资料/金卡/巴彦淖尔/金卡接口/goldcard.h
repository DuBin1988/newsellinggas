
//写标记函数
extern "C" __declspec(dllexport)  int __stdcall Gold_Write_logo(__int16 intcom,   //端口
																 __int16 logo);

//读标记函数
extern "C" __declspec(dllexport)  int __stdcall Gold_Read_logo(__int16 intcom,   //端口
																 __int16 *logo); 
//清气函数
extern "C" __declspec(dllexport)  int __stdcall Gold_Clearcard(__int16 intcom,   //端口
																 unsigned char *vskh);
//读卡函数
extern "C" __declspec(dllexport)  int __stdcall Gold_Readcard(int intcom,   //端口
																 unsigned char *vskh,//卡号
																 int *vlql,
																 __int16 *vics,
																 __int16 *viklx);
//擦卡函数
extern "C" __declspec(dllexport)  int __stdcall Gold_Formatcard(__int16 intcom,   //端口
																 unsigned char *vskh);
//写新卡和补卡函数
extern "C" __declspec(dllexport)  int __stdcall Gold_Writecard(__int16 intcom,   //端口
															     unsigned char *vskh,  //卡号
															   int vlql1,
															   __int16 vics,
															   __int16 viklx); 
//购气函数
extern "C" __declspec(dllexport)  int __stdcall Gold_Buycard(__int16 intcom,   //端口
															     unsigned char *vskh,  //卡号
															   int vlql1,
															   __int16 vics); 
//错误代码
extern "C" __declspec(dllexport)  void __stdcall Error_message(int a,char *op1);

//检查是不是金卡公司的卡
extern "C" __declspec(dllexport)  int __stdcall Gold_CheckCard(__int16 intcom);
