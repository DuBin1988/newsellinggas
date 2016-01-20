1、厂商判断函数
声明：int rdcompany(int icDev, char* isTrue)
参数：	icDev  端口句柄（in）
	isTrue 是否码（out）0是，1否
2、读卡函数
功能：读出卡上信息
声明：int readCard(int icDev,char* userCode,float* cardAmount,float* meterAmount,float* testAmount,char* inserted,int *times)
参数：	icDev  端口句柄（in）
	userCode 用户号（卡号）（out），至少8字节
	cardAmount 卡上余量（out）
	meterAmount 表上余量（out）
	testAmount 测试用量（out）
	inserted 是否已在气表上插卡（out），1字节
	times 用卡计数（out）
3、制卡、补卡函数
功能：给处于初始化状态的气表发卡（新表和插过初始化卡的气表处于初始化状态）或用户卡丢失或损坏的用户补卡
声明：int makeCard(int icDev,char* userCode,float Amount,char* saveInfo, char mark, float LimiteAmount, float AlarmAmount)
参数：	icDev 端口句柄（in）
	userCode 用户号（卡号）（in），至少8字节
	Amount 写卡量（in），精确到小数点后1为，否则报错
	saveInfo 气表信息数据（in/out），至少16字节，补卡调用时用上次保存的数据填充，函数成功返回后需将这个值保存到数据库，函数出错时这个值无效
	mark 这个值为129时表示制卡，为0或1时表示补卡，补卡时最后一次购气气量是否已输到表中，已输入为1，未输入为0
	LimiteAmount 表内限制囤气量，为0时表示不修改表内的这个值（限制用户气表内囤气量 2013年8月后生产的表）
	AlarmAmount 报警量，为0时表示不修改表内的这个值（提示用户购气 2013年8月后生产的表）
4、购气写卡函数
功能：用户购买气量写卡
声明：int writeCard(int icDev,char* userCode,float Amount,char* saveInfo, float LimiteAmount, float AlarmAmount)
参数：	icDev 端口句柄（in）
	userCode 用户号（卡号）（in），至少8字节
	Amount 写卡量（in），精确到小数点后1为，否则报错，成功写卡后，卡上气量为：Amount+卡上余量
	saveInfo 气表信息数据（in/out），至少16字节，调用时用上次保存的数据填充，函数成功返回后需将这个值保存到数据库，函数出错时这个值无效
	LimiteAmount 表内限制囤气量，为0时表示不修改表内的这个值（限制用户气表内囤气量 2013年8月后生产的表）
	AlarmAmount 报警量，为0时表示不修改表内的这个值（提示用户购气 2013年8月后生产的表）
5、清卡函数
功能：清除IC卡信息，卡返回空白卡状态
声明： int clearCard(int icDev,char* userCode)
参数：	icDev 端口句柄（in）
	userCode 用户号（卡号）（in），至少8字节

6、写补卡信息卡
功能：写补卡用信息卡
声明：int writeInfoCard(int icDev)
参数：icDev 端口句柄（in）

7、写工具卡函数
功能：写工具卡
声明：int writeToolCard(int icDev, int WriteType, float TestAmount, int TestTimes)
参数：	icDev 端口句柄（in）
	WriteType （in） 写工具卡类型*，其传入值见下面说明
	TestAmount （in） 写测试卡用量 不大于100，写非测试卡时传入0
	TestTimes （in） 写测试卡可使用次数 不大于255，写非测试卡时传入0
writeToolCard参数
WriteType = 5; //写初始化卡
WriteType = 6; //异常清除卡
WriteType = 7; //换表卡
WriteType = 8; //测试卡
WriteType = 9; //清零卡
WriteType = 10; //补测试卡，当测试卡使用次数使用完后，用这个功能重写测试卡
WriteType = 11; //恢复工具卡为空白卡



所有函数写卡的气量不能大于99999

返回错误：
0  成功
1  读卡数据是错误的
2  没有这个用户
3  加密数据出错
6  负气量错误
9  用户号和卡不对应
10  写卡出错
11  读卡出错
13  用户号长度错误
14  用户号字符非法
15  用户号已存在
16  非博冠卡
17  校验卡密码错误
18  数据错误
19  IC卡已报废
20  扣气量大于卡内存量错误
21  非用户卡
22  校验和错误
23  气量超大
24  博冠工具卡：参数userCode返回为卡类型号='4':空白卡,'5':初始化卡,'6':异常清除卡,'7':换表卡,'8':测试卡,'9':清零卡


