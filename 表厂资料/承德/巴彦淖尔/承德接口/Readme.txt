1�������жϺ���
������int rdcompany(int icDev, char* isTrue)
������	icDev  �˿ھ����in��
	isTrue �Ƿ��루out��0�ǣ�1��
2����������
���ܣ�����������Ϣ
������int readCard(int icDev,char* userCode,float* cardAmount,float* meterAmount,float* testAmount,char* inserted,int *times)
������	icDev  �˿ھ����in��
	userCode �û��ţ����ţ���out��������8�ֽ�
	cardAmount ����������out��
	meterAmount ����������out��
	testAmount ����������out��
	inserted �Ƿ����������ϲ忨��out����1�ֽ�
	times �ÿ�������out��
3���ƿ�����������
���ܣ������ڳ�ʼ��״̬�����������±�Ͳ����ʼ�����������ڳ�ʼ��״̬�����û�����ʧ���𻵵��û�����
������int makeCard(int icDev,char* userCode,float Amount,char* saveInfo, char mark, float LimiteAmount, float AlarmAmount)
������	icDev �˿ھ����in��
	userCode �û��ţ����ţ���in��������8�ֽ�
	Amount д������in������ȷ��С�����1Ϊ�����򱨴�
	saveInfo ������Ϣ���ݣ�in/out��������16�ֽڣ���������ʱ���ϴα����������䣬�����ɹ����غ��轫���ֵ���浽���ݿ⣬��������ʱ���ֵ��Ч
	mark ���ֵΪ129ʱ��ʾ�ƿ���Ϊ0��1ʱ��ʾ����������ʱ���һ�ι��������Ƿ����䵽���У�������Ϊ1��δ����Ϊ0
	LimiteAmount �������ƶ�������Ϊ0ʱ��ʾ���޸ı��ڵ����ֵ�������û������ڶ����� 2013��8�º������ı�
	AlarmAmount ��������Ϊ0ʱ��ʾ���޸ı��ڵ����ֵ����ʾ�û����� 2013��8�º������ı�
4������д������
���ܣ��û���������д��
������int writeCard(int icDev,char* userCode,float Amount,char* saveInfo, float LimiteAmount, float AlarmAmount)
������	icDev �˿ھ����in��
	userCode �û��ţ����ţ���in��������8�ֽ�
	Amount д������in������ȷ��С�����1Ϊ�����򱨴��ɹ�д���󣬿�������Ϊ��Amount+��������
	saveInfo ������Ϣ���ݣ�in/out��������16�ֽڣ�����ʱ���ϴα����������䣬�����ɹ����غ��轫���ֵ���浽���ݿ⣬��������ʱ���ֵ��Ч
	LimiteAmount �������ƶ�������Ϊ0ʱ��ʾ���޸ı��ڵ����ֵ�������û������ڶ����� 2013��8�º������ı�
	AlarmAmount ��������Ϊ0ʱ��ʾ���޸ı��ڵ����ֵ����ʾ�û����� 2013��8�º������ı�
5���忨����
���ܣ����IC����Ϣ�������ؿհ׿�״̬
������ int clearCard(int icDev,char* userCode)
������	icDev �˿ھ����in��
	userCode �û��ţ����ţ���in��������8�ֽ�

6��д������Ϣ��
���ܣ�д��������Ϣ��
������int writeInfoCard(int icDev)
������icDev �˿ھ����in��

7��д���߿�����
���ܣ�д���߿�
������int writeToolCard(int icDev, int WriteType, float TestAmount, int TestTimes)
������	icDev �˿ھ����in��
	WriteType ��in�� д���߿�����*���䴫��ֵ������˵��
	TestAmount ��in�� д���Կ����� ������100��д�ǲ��Կ�ʱ����0
	TestTimes ��in�� д���Կ���ʹ�ô��� ������255��д�ǲ��Կ�ʱ����0
writeToolCard����
WriteType = 5; //д��ʼ����
WriteType = 6; //�쳣�����
WriteType = 7; //����
WriteType = 8; //���Կ�
WriteType = 9; //���㿨
WriteType = 10; //�����Կ��������Կ�ʹ�ô���ʹ����������������д���Կ�
WriteType = 11; //�ָ����߿�Ϊ�հ׿�



���к���д�����������ܴ���99999

���ش���
0  �ɹ�
1  ���������Ǵ����
2  û������û�
3  �������ݳ���
6  ����������
9  �û��źͿ�����Ӧ
10  д������
11  ��������
13  �û��ų��ȴ���
14  �û����ַ��Ƿ�
15  �û����Ѵ���
16  �ǲ��ڿ�
17  У�鿨�������
18  ���ݴ���
19  IC���ѱ���
20  ���������ڿ��ڴ�������
21  ���û���
22  У��ʹ���
23  ��������
24  ���ڹ��߿�������userCode����Ϊ�����ͺ�='4':�հ׿�,'5':��ʼ����,'6':�쳣�����,'7':����,'8':���Կ�,'9':���㿨


