
//д��Ǻ���
extern "C" __declspec(dllexport)  int __stdcall Gold_Write_logo(__int16 intcom,   //�˿�
																 __int16 logo);

//����Ǻ���
extern "C" __declspec(dllexport)  int __stdcall Gold_Read_logo(__int16 intcom,   //�˿�
																 __int16 *logo); 
//��������
extern "C" __declspec(dllexport)  int __stdcall Gold_Clearcard(__int16 intcom,   //�˿�
																 unsigned char *vskh);
//��������
extern "C" __declspec(dllexport)  int __stdcall Gold_Readcard(int intcom,   //�˿�
																 unsigned char *vskh,//����
																 int *vlql,
																 __int16 *vics,
																 __int16 *viklx);
//��������
extern "C" __declspec(dllexport)  int __stdcall Gold_Formatcard(__int16 intcom,   //�˿�
																 unsigned char *vskh);
//д�¿��Ͳ�������
extern "C" __declspec(dllexport)  int __stdcall Gold_Writecard(__int16 intcom,   //�˿�
															     unsigned char *vskh,  //����
															   int vlql1,
															   __int16 vics,
															   __int16 viklx); 
//��������
extern "C" __declspec(dllexport)  int __stdcall Gold_Buycard(__int16 intcom,   //�˿�
															     unsigned char *vskh,  //����
															   int vlql1,
															   __int16 vics); 
//�������
extern "C" __declspec(dllexport)  void __stdcall Error_message(int a,char *op1);

//����ǲ��ǽ𿨹�˾�Ŀ�
extern "C" __declspec(dllexport)  int __stdcall Gold_CheckCard(__int16 intcom);
