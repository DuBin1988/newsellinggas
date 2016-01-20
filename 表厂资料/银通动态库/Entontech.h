//---------------------------------------------------------------------------
#define dll_exp extern "C" __declspec(dllexport)

struct UINFO_OLD
{	byte name[8],code[8],date[10],tele[14],addr[24];
};

struct UINFO_V0
{	
	byte name[8],date[4],tele[12],addr[32],flag[8];
/*
name �û�����
date �������� 
tele ��ϵ�绰
addr �û�סַ
flag[4]   0x47 ��Ϊ����д�� 
flag[5]   0x43 ����     0x49 ��ҵ     0x4C ������   (���� 2λС��  ��ҵ 1λС��  ��������С��  Ĭ�ϻ�Ϊ����ʱ��Ϊ����)
flag[6]   0x41 һλС�� 0x42 ��λС�� 0x43 ��λС�� 0x44 ��С��
*/
};



union zone3
{	char b[64];
	struct UINFO_OLD old;
	struct UINFO_V0 v0;
};

struct response
{	union zone3 ui;
	byte version;  //�汾
	ulng code,amt; 
	// 	code ��� ��8λ  7λ��Ч��� ǰ�油0   �� 12345��ʽ����ӦΪ00012345
	//	amt ����   ��С�����߿�ƬС��λ��ȷ������ 1��10��100��100���翨Ƭ����Ҫд100�������ͺ�Ϊ���� ��ô����ʵ��Ϊ10000�����Ϊ��ҵ����ʵ��Ϊ1000��
};

//---------------------------------------------------------------------------
dll_exp char * Get_DLL_Version(void);			//��ȡ�汾��Ϣ
dll_exp char * Get_Ini_Path(void);   			//���ؿ��������ļ�·��
dll_exp int    GetErrorMsg(int wErrNo,char *bErrMsg);	//��ȡ������Ϣ
							//wErrNo  �������
							//bErrMsg 100������  ��ŷ��ش�����Ϣ

dll_exp int Read_Data(response *rsp);			//���û�����Ϣ
dll_exp int Write_Data(response *rsp);			//д�û�������
dll_exp int Modify_Info(response *rsp);			//�޸��û�����Ϣ  ���Ų����޸�
dll_exp int Write_Info(response *rsp);			//��Ƭ����

dll_exp int GetInis();					//��̬���ʼ  ÿ����������ǰ�ȶ�̬���ʼһ��

dll_exp int Set_IC_Com(int Coms);			//���������ļ��е�IC��д�����˿�
dll_exp int Set_Show_Info(int SetShowFrm);		//���ñ�߷��ش�����ʾ SetShowFrm=1 ��ʾ  0 ����ʾ
dll_exp int Get_is_Entontech();				//���ؿ�����  0 ������  2 �¿�  20 �Ѿ������� 104 û�в忨


