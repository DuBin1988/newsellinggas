//��������
extern "C" __declspec(dllexport)  int __stdcall ReadGasCard(__int16 com,
															int baud,
															unsigned char *kmm,
															__int16 *klx,
															__int16 *kzt,
															unsigned char *kh,
															unsigned char *dqdm,
															unsigned char *yhh,
															unsigned char *tm,
															int *ql,
															__int16 *cs,
															int *ljgql,
															__int16 *bkcs,
															int *ljyql,
															int *syql,
															int *bjql,
															int *czsx,
															int *tzed,
															unsigned char *sqrq, 
															__int32 *oldprice,
															__int32 *newprice,
															unsigned char *sxrq,
															unsigned char *sxbj);
//�忨����
extern "C" __declspec(dllexport)  int __stdcall FormatGasCard(__int16 com,int baud,unsigned char *kmm,__int16 klx,unsigned char *kh,unsigned char *dqdm);


//д�¿��Ͳ�������
extern "C" __declspec(dllexport)  int __stdcall WriteNewCard(__int16 com, __int32 baud, unsigned char *kmm, __int16 klx, __int16 kzt, unsigned char *kh, unsigned char *dqdm, unsigned char *yhh, unsigned char *tm, __int32 ql, __int16 cs, __int32 ljgql, __int16 bkcs, __int32 ljyql, __int32 bjql, __int32 czsx, __int32 tzed, unsigned char *sqrq ,__int32 * oldprice, __int32 * newprice ,unsigned char * sxrq,unsigned char * sxbj);


//��������
extern "C" __declspec(dllexport)  int __stdcall WriteGasCard(__int16 com, __int32 baud, unsigned char *kmm, __int16 klx, unsigned char *kh, unsigned char *dqdm, __int32 ql, __int16 cs, __int32 ljgql, __int32 bjql, __int32 czsx, __int32 tzed,unsigned char *sqrq, __int32 * oldprice, __int32 * newprice ,unsigned char * sxrq,unsigned char * sxbj);


//����ǲ��ǽ𿨹�˾�Ŀ�
extern "C" __declspec(dllexport)  int __stdcall CheckGasCard(__int16 com,int baud);

 

 
