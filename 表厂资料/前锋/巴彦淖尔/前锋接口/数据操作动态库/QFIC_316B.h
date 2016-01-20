//////////////////////////////////////////////////////////////////////////////////////
//新用户卡（开户发卡，换表发卡）
long WINAPI QF_NewCard(int Port,long Baud,unsigned char *PstrCardId,unsigned long BuyGas,
			unsigned long AlarmGas,unsigned char BigMeter,unsigned char Passn,unsigned int fg);
			
//参数：  Port：                系统所用串口号，0--COM1、1--COM2等。
//        Baud:                 波特率其值可为1200～115200
//        PstrCardId:           卡编号（00000001-99999999）
//        BuyGas            	购气量（小表为0-1999，大表为0-999999）
//        AlarmGas              报警气量
//	  BigMeter:         	大表标志（民用户（81H）、工业用户（82H）、福利用户；及其他）
//	  Passn:		密码批次，0-9
//        fg:               	低字节片类型标志；1代表102卡，4代表4442卡。（0-255）
//                              高字节(0-255) 作用是当有用户重复时给加一个标志区分是哪个旗县的用户

//返回值：
//        = 0：正确
//        = 1：未检测到USB—KEY 
//        = 2：购气量过大或者错误
//        = 3：连接读卡器失败
//        = 4：读卡器中无卡/卡型不正确
//        = 5: 卡型不正确
//        = 6：不是新卡
//        = 7：校验卡密码失败
//        = 8：读卡失败
//        = 9：写卡失败
//        = 10：写卡数据校验失败
//        = 11：修改卡密码失败
//        Else：系统出错

//新用户卡用于新疆(只针对4442卡)
long WINAPI QF_NewCard30(int Port,long Baud,unsigned char *PstrCardId,unsigned long BuyGas,
			unsigned long AlarmGas,unsigned char BigMeter,unsigned char Passn,unsigned int fg);
//参数：  Port：                系统所用串口号，0--COM1、1--COM2等。
//        Baud:                 波特率其值可为1200～115200
//        PstrCardId:           卡编号（00000001-99999999）
//        BuyGas            	购气量（小表为0-1999，大表为0-999999）
//        AlarmGas              报警气量
//	  BigMeter:         	大表标志（民用户（81H）、工业用户（82H）、福利用户；及其他）
//	  Passn:		密码批次，0-9
//        fg:               	低字节片类型标志；1代表102卡，4代表4442卡。（0-255）
//                              高字节(0-255) 作用是当有用户重复时给加一个标志区分是哪个旗县的用户


//返回值：
//        = 0：正确
//        = 1：未检测到USB—KEY 
//        = 2：购气量过大或者错误
//        = 3：连接读卡器失败
//        = 4：读卡器中无卡
//        = 5: 卡型不正确
//        = 6：不是新卡
//        = 7：校验卡密码失败
//        = 8：读卡失败
//        = 9：写卡失败
//        = 10：写卡数据校验失败
//        = 11：修改卡密码失败
//        Else：系统出错

//补卡
long WINAPI QF_MendCard(int Port,long Baud,unsigned char *PstrCardId,unsigned long BuyCnt,
			unsigned long BuyGas,unsigned long AlarmGas,unsigned char BigMeter,
			unsigned char Passn,unsigned int fg);
			
//参数：  Port：                系统所用串口号，0--COM1、1--COM2等。
//        Baud:                 波特率其值可为1200～115200
//        PstrCardId:           卡编号（00000001-99999999）
//        BuyCnt：            	购气次数（从数据库中读出）
//        BuyGas            	补气量（用户最后一次购气记录）
//        AlarmGas              报警气量
//	  BigMeter:         	大表标志（民用户（81H）、工业用户（82H）、福利用户；及其他）
//        fg:               	低字节片类型标志；1代表102卡，4代表4442卡。（0-255）
//                              高字节(0-255) 作用是当有用户重复时给加一个标志区分是哪个旗县的用户

//返回值：
//        = 0：正确
//        = 1：未检测到USB—KEY 
//        = 2：购气量过大或者错误
//        = 3：连接读卡器失败
//        = 4：读卡器中无卡
//        = 5: 卡型不正确
//        = 6：不是新卡
//        = 7：校验卡密码失败
//        = 8：读卡失败
//        = 9：写卡失败
//        = 10：写卡数据校验失败
//        = 11：修改卡密码失败
//        Else：系统出错

//制作工具卡
long WINAPI QF_BulidCard(int Port,long Baud,unsigned char *PstrCardId,unsigned char CardSort,unsigned long GasNum,
				unsigned long AlarmGas,unsigned char Passn,unsigned char Passn_New,unsigned int fg);
			
//参数：  Port：                系统所用串口号，0--COM1、1--COM2等。
//        Baud:                 波特率其值可为1200～115200
//        PstrCardId:           卡编号（00000001-99999999）
//        CardSort：            卡类型标志（3：总初始化卡；4：回收转移卡；5：初始化卡；6：1方气卡；
//					    7：恢复卡；8：生产工具卡；9：清气量卡；10：查询卡；
//					    11、检测卡；12、密钥下载卡；13：，14：停气卡，15：，16：预置卡， 2x为气量卡，其x为气量大小（1-9）
//					    7-10为102卡4442卡共用标志；4，7，8，10为4442，M1卡共用标志；3，5，6，11，12，2x为4442卡专用标志；
//					    14，16为M1卡专用标志。
//        GasNum            	生产工具卡（102，M1）测试气量，4442生产工具卡固定0.1方；M1卡预置气量
//        AlarmGas              报警气量(用于M1卡生产工具卡及预置卡使用)
//	  Passn:		4442卡时:密码批次，0-9。M1卡时：功能下载标志位，00需下载，仅用于预置卡
//	  Passn_New：		4442卡时：新密码批次，0-9，且只用于密钥下载卡。M1卡时：功能参数位，仅用于预制卡
//        fg:               	低字节片类型标志；1代表102卡，4代表4442卡。（0-255）
//                              高字节(0-255) 作用是当有用户重复时给加一个标志区分是哪个旗县的用户

//返回值：
//        = 0：正确
//        = 1：未检测到USB—KEY 
//        = 2：购气量过大或者错误
//        = 3：连接读卡器失败
//        = 4：读卡器中无卡
//        = 5: 卡型不正确
//        = 6：不是新卡
//        = 7：校验卡密码失败
//        = 8：读卡失败
//        = 9：写卡失败
//        = 10：写卡数据校验失败
//        = 11：修改卡密码失败
//        Else：系统出错


//读卡
long WINAPI QF_TestCard(int Port,long Baud,unsigned char *PstrCardId,unsigned char* CardSort,
			unsigned long *BuyCnt,unsigned long *PEnableGas,unsigned long *CumGas,
			unsigned long *lastGas,unsigned long *AlarmGas,unsigned char *BigMeter,
			unsigned char *MeterStu,unsigned int *fg);
			
//参数：  Port：                系统所用串口号，0--COM1、1--COM2等。
//        Baud:                 波特率其值可为1200～115200
//        PstrCardId:           返回卡编号（00000001-99999999）
//        CardSort：            返回卡类型标志（1：新用户卡（用户传递卡），2：旧用户卡，3：总初始化卡
//					    4：回收卡，5：初始化卡，6：1方气卡
//					    7：恢复卡，8：生产工具卡，9：清气量卡，10：查询卡，11：检测卡
//					    12：密钥卡，13：30新用户卡，14停气卡，15:转移卡，16：预置卡,2x:气量卡，x为1-9
//					    7-10为102卡4442卡共用标志；4，7，8，10，15为4442，M1卡共用标志；3，5，6，11，12，13，2x为4442卡专用标志；
//					    14，16为M1卡专用标志。
//	  BuyCnt：		返回购气次数（用户卡）
//        PEnableGas：          返回用户卡、生产工具卡（102，M1）、M1预置卡中气量；其他无效。
//        CumGas：            	返回气表累计购气量（用于查询卡查询表中累计购气量-102工业表）
//	  lastGas:		返回表内累计用气量（暂定义为4442用户卡，M1用户卡,M1生产工具卡，M1预置卡返回表内剩余气量，查询卡，转移卡表中剩余量）
//        AlarmGas              报警气量(4442、M1用户卡，M1查询卡,M1转移卡)
//	  BigMeter:         	返回大表标志（民用户（81H）、工业用户（82H）、福利用户；及其他）
//	  MeterStu:		返回表状态(用户卡、查询卡，M1生产工具卡,M1预置卡状态字，4442密钥下载卡为新、旧密钥批次)
//				M1卡查询卡在状态字后面紧跟最新4笔购气记录，最新8天用气记录。M1预置卡在状态字后面为功能标志位及功能参数
//
//        fg:                   低字节片类型标志；1代表102卡，4代表4442卡。（0-255）
//                              高字节(0-255) 作用是当有用户重复时给加一个标志区分是哪个旗县的用户

//返回值：
//        = 0：正确
//        = 1：未检测到USB—KEY 
//        = 3：连接读卡器失败
//        = 4：读卡器中无卡
//        = 5: 卡型不正确
//        = 12：空卡
//        = 7：校验卡密码失败
//        = 8：读卡失败
//        = 13：用户卡气量区与备份气量区不等。
//        = 20：不是本系统的卡。
//        Else：系统出错


//清卡
long WINAPI QF_ClearCard(int Port,long Baud);
			
//参数：  Port：                系统所用串口号，0--COM1、1--COM2等。
//        Baud:                 波特率其值可为1200～115200


//返回值：

//        = 0：正确
//        = 1：未检测到USB—KEY 
//        = 2：连接读卡器失败
//        = 3：读卡器中无卡
//        = 7：校验卡密码失败
//        = 8：读卡失败
//        = 9：写卡失败
//        = 11：修改卡密码失败
//        Else：系统出错


long WINAPI QF_BuyGas(int Port,long Baud,unsigned char *PstrCardId,unsigned long BuyCnt,
			unsigned long BuyGas,unsigned long AlarmGas,unsigned int fg,unsigned char BigMeter);
			
//参数：  Port：                系统所用串口号，0--COM1、1--COM2等。
//        Baud:                 波特率其值可为1200～115200
//        PstrCardId:           卡编号（00000001-99999999）
//	  BuyCnt:		购气次数，不做比较直接写入
//        BuyGas：              购气量（小表为0-1999，大表为0-999999）不做叠加，直接写入
//        AlarmGas              报警气量
//        fg:                   低字节片类型标志；1代表102卡，4代表4442卡。（0-255）
//                              高字节(0-255) 作用是当有用户重复时给加一个标志区分是哪个旗县的用户
//	  BigMeter：		大表标志（民用户（81H）、工业用户（82H）、福利用户；及其他）


//返回值：
//        = 0：正确
//        = 1：未检测到USB—KEY 
//        = 2：购气量过大或者错误
//        = 3：连接读卡器失败
//        = 4：读卡器中无卡
//        = 5: 卡型不正确
//        = 6：
//        = 7：校验卡密码失败
//        = 8：读卡失败
//        = 9：写卡失败
//        = 10：写卡数据校验失败
//        = 11：修改卡密码失败
//	  = 12：空卡
//        = 13：用户卡气量区与备份气量区不等。
//        = 14：非用户卡。
//        = 15：卡编号不正确。
//        = 16：
//        = 17：
//        = 18：
//        = 19：
//        = 20：不是本系统的卡。
//        Else：系统出错

