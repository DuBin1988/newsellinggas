// Mwic_32.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "Mwic_32.h"
#include "time.h"
#include <cstdio>

HINSTANCE hModule = NULL;

BOOL APIENTRY DllMain( HANDLE hModul, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved
					 )
{
    switch (ul_reason_for_call)
	{
		case DLL_PROCESS_ATTACH:
			{
				hModule = LoadLibrary("mwic.dll");
			}
			break;
		case DLL_THREAD_ATTACH:
			break;
		case DLL_THREAD_DETACH:
			break;
		case DLL_PROCESS_DETACH:
			{
				FreeLibrary(hModule);
			}
			break;
    }
    return TRUE;
}

void timestamp(FILE * fp);
char * getFileName();

// Mwic_32.cpp : Defines the entry point for the DLL application.
//

extern "C" __declspec(dllexport) HANDLE __stdcall ic_init(__int16 port,unsigned long baud)
{
	pic_init ic_init_copy = NULL;
	ic_init_copy = (pic_init)GetProcAddress(hModule,"ic_init");
	if(ic_init_copy == NULL)
	{
		::MessageBox(NULL, "proc is null", "in", MB_OK);
	}

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_init(__int16 port,unsigned long baud): %d, %d", port, baud);
	fclose(fp);

	HANDLE result = ic_init_copy(port, baud);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_init end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) HANDLE __stdcall auto_init(__int16 port,unsigned long baud)
{
	pauto_init auto_init_copy = NULL;
	auto_init_copy = (pauto_init)GetProcAddress(hModule,"auto_init");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "auto_init(__int16 port,unsigned long baud): %d, %d", port, baud);
	fclose(fp);

	HANDLE result = auto_init_copy(port, baud);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "auto_init end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall ic_exit(HANDLE icdev)
{
	pic_exit ic_exit_copy = NULL;
	ic_exit_copy = (pic_exit)GetProcAddress(hModule,"ic_exit");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_exit(HANDLE icdev): %d", icdev);
	fclose(fp);
	
	__int16 result = ic_exit_copy(icdev);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_exit end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_ver(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	psrd_ver srd_ver_copy = NULL;
	srd_ver_copy = (psrd_ver)GetProcAddress(hModule,"srd_ver");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_ver(HANDLE icdev, __int16 len, unsigned char *data_buffer): %d, %d", icdev, len);
	fclose(fp);
	
	__int16 result = srd_ver_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_ver end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall set_baud(HANDLE icdev, unsigned long baud)
{
	pset_baud set_baud_copy = NULL;
	set_baud_copy = (pset_baud)GetProcAddress(hModule,"set_baud");
	
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "set_baud(HANDLE icdev, unsigned long baud): %d, %d", icdev, baud);
	fclose(fp);
		
	__int16 result = set_baud_copy(icdev, baud);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "set_baud end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall cmp_dvsc(HANDLE icdev, __int16 len, unsigned char *dta_buffer)
{
	pcmp_dvsc cmp_dvsc_copy = NULL;
	cmp_dvsc_copy = (pcmp_dvsc)GetProcAddress(hModule,"cmp_dvsc");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "cmp_dvsc(HANDLE icdev, __int16 len, unsigned char *dta_buffer): %d, %d", icdev, len);
	fclose(fp);
	
	__int16 result = cmp_dvsc_copy(icdev, len, dta_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "cmp_dvsc end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_dvsc(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	psrd_dvsc srd_dvsc_copy = NULL;
	srd_dvsc_copy = (psrd_dvsc)GetProcAddress(hModule,"srd_dvsc");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_dvsc(HANDLE icdev, __int16 len, unsigned char *data_buffer): %d, %d", icdev, len);
	fclose(fp);
	
	__int16 result = srd_dvsc_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_dvsc end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_dvsc(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	pswr_dvsc swr_dvsc_copy = NULL;
	swr_dvsc_copy = (pswr_dvsc)GetProcAddress(hModule,"swr_dvsc");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_dvsc(HANDLE icdev, __int16 len, unsigned char *data_buffer): %d, %d", icdev, len);
	fclose(fp);
	
	__int16 result = swr_dvsc_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_dvsc end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall setsc_md(HANDLE icdev, __int16 mode)
{
	psetsc_md setsc_md_copy = NULL;
	setsc_md_copy = (psetsc_md)GetProcAddress(hModule,"setsc_md");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "setsc_md(HANDLE icdev, __int16 mode): %d, %d", icdev, mode);
	fclose(fp);

	__int16 result = setsc_md_copy(icdev, mode);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "setsc_md end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall turn_on(HANDLE icdev)
{
	pturn_on turn_on_copy = NULL;
	turn_on_copy = (pturn_on)GetProcAddress(hModule,"turn_on");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "turn_on(HANDLE icdev): %d", icdev);
	fclose(fp);
	
	__int16 result = turn_on_copy(icdev);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "turn_on end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall turn_off(HANDLE icdev)
{
	pturn_off turn_off_copy = NULL;
	turn_off_copy = (pturn_off)GetProcAddress(hModule,"turn_off");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "turn_off(HANDLE icdev): %d", icdev);
	fclose(fp);
	
	__int16 result = turn_off_copy(icdev);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "turn_off end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall auto_pull(HANDLE icdev)
{
	pauto_pull auto_pull_copy = NULL;
	auto_pull_copy = (pauto_pull)GetProcAddress(hModule,"auto_pull");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "auto_pull(HANDLE icdev): %d", icdev);
	fclose(fp);
		
	__int16 result = auto_pull_copy(icdev);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "auto_pull end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall get_status(HANDLE icdev,__int16 *state)
{
	pget_status get_status_copy = NULL;
	get_status_copy = (pget_status)GetProcAddress(hModule,"get_status");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "get_status(HANDLE icdev,__int16 *state): %d", icdev);
	fclose(fp);
			
	__int16 result = get_status_copy(icdev, state);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "get_status end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall get_status_DPEx(HANDLE icdev,unsigned char CardType,__int16 *state)
{
	pget_status_DPEx get_status_DPEx_copy = NULL;
	get_status_DPEx_copy = (pget_status_DPEx)GetProcAddress(hModule,"get_status_DPEx");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "get_status_DPEx(HANDLE icdev,unsigned char CardType,__int16 *state): %d, %d", icdev, CardType);
	fclose(fp);
				
	__int16 result = get_status_DPEx_copy(icdev, CardType, state);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "get_status_DPEx end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall dv_beep(HANDLE icdev, __int16 time)
{
	pdv_beep dv_beep_copy = NULL;
	dv_beep_copy = (pdv_beep)GetProcAddress(hModule,"dv_beep");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "dv_beep(HANDLE icdev, __int16 time): %d, %d", icdev, time);
	fclose(fp);
					
	__int16 result = dv_beep_copy(icdev, time);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "dv_beep end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_eeprom(HANDLE icdev, __int16 offset,__int16 len, unsigned char *data_buffer)
{
	pswr_eeprom swr_eeprom_copy = NULL;
	swr_eeprom_copy = (pswr_eeprom)GetProcAddress(hModule,"swr_eeprom");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_eeprom(HANDLE icdev, __int16 offset,__int16 len, unsigned char *data_buffer): %d, %d, %d", icdev, offset, len);
	fclose(fp);
					
	__int16 result = swr_eeprom_copy(icdev, offset, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_eeprom end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_eeprom(HANDLE icdev, __int16 offset,__int16 len, unsigned char *data_buffer)
{
	psrd_eeprom srd_eeprom_copy = NULL;
	srd_eeprom_copy = (psrd_eeprom)GetProcAddress(hModule,"srd_eeprom");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_eeprom(HANDLE icdev, __int16 offset,__int16 len, unsigned char *data_buffer): %d, %d, %d", icdev, offset, len);
	fclose(fp);
					
	__int16 result = srd_eeprom_copy(icdev, offset, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_eeprom end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_snr(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	psrd_snr srd_snr_copy = NULL;
	srd_snr_copy = (psrd_snr)GetProcAddress(hModule,"srd_snr");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_snr(HANDLE icdev, __int16 len, unsigned char *data_buffer): %d, %d", icdev, len);
	fclose(fp);
					
	__int16 result = srd_snr_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_snr end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall val_read(HANDLE icdev,unsigned long *p_Value)
{
	pval_read val_read_copy = NULL;
	val_read_copy = (pval_read)GetProcAddress(hModule,"val_read");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_read(HANDLE icdev,unsigned long *p_Value): %d", icdev);
	fclose(fp);

	__int16 result = val_read_copy(icdev, p_Value);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_read end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall val_inc(HANDLE icdev, unsigned long _Value)
{
	pval_inc val_inc_copy = NULL;
	val_inc_copy = (pval_inc)GetProcAddress(hModule,"val_inc");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_inc(HANDLE icdev, unsigned long _Value): %d, %d", icdev, _Value);
	fclose(fp);

	__int16 result = val_inc_copy(icdev, _Value);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_inc end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall val_dec(HANDLE icdev, unsigned long _Value)
{
	pval_dec val_dec_copy = NULL;
	val_dec_copy = (pval_dec)GetProcAddress(hModule,"val_dec");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_dec(HANDLE icdev, unsigned long _Value): %d, %d", icdev, _Value);
	fclose(fp);

	__int16 result = val_dec_copy(icdev, _Value);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_dec end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall val_set(HANDLE icdev, unsigned long _Value)
{
	pval_set val_set_copy = NULL;
	val_set_copy = (pval_set)GetProcAddress(hModule,"val_set");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_set(HANDLE icdev, unsigned long _Value): %d, %d", icdev, _Value);
	fclose(fp);

	__int16 result = val_set_copy(icdev, _Value);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "val_set end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int32 __stdcall chk_baud(__int16 port)
{
	pchk_baud chk_baud_copy = NULL;
	chk_baud_copy = (pchk_baud)GetProcAddress(hModule,"chk_baud");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "chk_baud(__int16 port): %d", port);
	fclose(fp);

	__int32 result = chk_baud_copy(port);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "chk_baud end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_card(HANDLE icdev)
{
	pchk_card chk_card_copy = NULL;
	chk_card_copy = (pchk_card)GetProcAddress(hModule,"chk_card");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "chk_card(HANDLE icdev): %d", icdev);
	fclose(fp);

	__int16 result = chk_card_copy(icdev);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "chk_card end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall ic_encrypt(char *key,char *ptrSource, unsigned short msgLen, char *ptrDest)
{
	pic_encrypt ic_encrypt_copy = NULL;
	ic_encrypt_copy = (pic_encrypt)GetProcAddress(hModule,"ic_encrypt");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_encrypt(char *key,char *ptrSource, unsigned short msgLen, char *ptrDest): %d", msgLen);
	fclose(fp);

	__int16 result = ic_encrypt_copy(key, ptrSource, msgLen, ptrDest);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_encrypt end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall ic_decrypt(char *key,char *ptrSource, unsigned short msgLen, char *ptrDest)
{
	pic_decrypt ic_decrypt_copy = NULL;
	ic_decrypt_copy = (pic_decrypt)GetProcAddress(hModule,"ic_decrypt");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_decrypt(char *key,char *ptrSource, unsigned short msgLen, char *ptrDest): %d", msgLen);
	fclose(fp);

	__int16 result = ic_decrypt_copy(key, ptrSource, msgLen, ptrDest);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ic_decrypt end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) unsigned long __stdcall DES_Encrypt(unsigned char *Key,__int16 KeyLen, unsigned char *Source, unsigned long SrcLen, unsigned char *result)
{
	pDES_Encrypt DES_Encrypt_copy = NULL;
	DES_Encrypt_copy = (pDES_Encrypt)GetProcAddress(hModule,"DES_Encrypt");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "DES_Encrypt(unsigned char *Key,__int16 KeyLen, unsigned char *Source, unsigned long SrcLen, unsigned char *result): %d, %d", KeyLen, SrcLen);
	fclose(fp);

	unsigned long _result = DES_Encrypt_copy(Key, KeyLen, Source, SrcLen, result);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "DES_Encrypt end: %d", _result);
	fclose(fp);

	return _result;
}

extern "C" __declspec(dllexport) unsigned long __stdcall DES_Decrypt(unsigned char *Key,__int16 KeyLen, unsigned char *Source, unsigned long SrcLen, unsigned char *result)
{
	pDES_Decrypt DES_Decrypt_copy = NULL;
	DES_Decrypt_copy = (pDES_Decrypt)GetProcAddress(hModule,"DES_Decrypt");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "DES_Decrypt(unsigned char *Key,__int16 KeyLen, unsigned char *Source, unsigned long SrcLen, unsigned char *result): %d, %d", KeyLen, SrcLen);
	fclose(fp);
	
	unsigned long _result = DES_Decrypt_copy(Key, KeyLen, Source, SrcLen, result);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "DES_Decrypt end: %d", _result);
	fclose(fp);

	return _result;
}

extern "C" __declspec(dllexport) __int16 __stdcall lib_ver(char *VerStr)
{
	plib_ver lib_ver_copy = NULL;
	lib_ver_copy = (plib_ver)GetProcAddress(hModule,"lib_ver");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "lib_ver(char *VerStr): ");
	fclose(fp);

	__int16 result = lib_ver_copy(VerStr);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "lib_ver end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall asc_hex(unsigned char *asc, unsigned char *hex, long pair_len)
{
	pasc_hex asc_hex_copy = NULL;
	asc_hex_copy = (pasc_hex)GetProcAddress(hModule,"asc_hex");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "asc_hex(unsigned char *asc, unsigned char *hex, long pair_len): %d", pair_len);
	fclose(fp);

	__int16 result = asc_hex_copy(asc, hex, pair_len);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "asc_hex end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall hex_asc(unsigned char *hex,unsigned char *asc,long length)
{
	phex_asc hex_asc_copy = NULL;
	hex_asc_copy = (phex_asc)GetProcAddress(hModule,"hex_asc");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "hex_asc(unsigned char *hex,unsigned char *asc,long length): %d", length);
	fclose(fp);

	__int16 result = hex_asc_copy(hex, asc, length);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "hex_asc end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall asc_asc(unsigned char *src,unsigned char *des,long len)
{
	pasc_asc asc_asc_copy = NULL;
	asc_asc_copy = (pasc_asc)GetProcAddress(hModule,"asc_asc");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "asc_asc(unsigned char *src,unsigned char *des,long len): %d", len);
	fclose(fp);

	__int16 result = asc_asc_copy(src, des, len);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "asc_asc end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall float_uchar(float f,unsigned char *c)
{
	pfloat_uchar float_uchar_copy = NULL;
	float_uchar_copy = (pfloat_uchar)GetProcAddress(hModule,"float_uchar");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "float_uchar(float f,unsigned char *c): %g", f);
	fclose(fp);

	__int16 result = float_uchar_copy(f, c);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "float_uchar end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall uchar_float(unsigned char *c,float *f)
{
	puchar_float uchar_float_copy = NULL;
	uchar_float_copy = (puchar_float)GetProcAddress(hModule,"uchar_float");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "uchar_float(unsigned char *c,float *f): ");
	fclose(fp);

	__int16 result = uchar_float_copy(c, f);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "uchar_float end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall short_uchar(short i,unsigned char *c)
{
	pshort_uchar short_uchar_copy = NULL;
	short_uchar_copy = (pshort_uchar)GetProcAddress(hModule,"short_uchar");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "short_uchar(short i,unsigned char *c): %d", i);
	fclose(fp);

	__int16 result = short_uchar_copy(i, c);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "short_uchar end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall uchar_short(unsigned char *c,short *i)
{
	puchar_short uchar_short_copy = NULL;
	uchar_short_copy = (puchar_short)GetProcAddress(hModule,"uchar_short");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "uchar_short(unsigned char *c,short *i): ");
	fclose(fp);

	__int16 result = uchar_short_copy(c, i);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "uchar_short end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall long_uchar(long l,unsigned char *c)
{
	plong_uchar long_uchar_copy = NULL;
	long_uchar_copy = (plong_uchar)GetProcAddress(hModule,"long_uchar");

	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "long_uchar(long l,unsigned char *c): %d", l);
	fclose(fp);

	__int16 result = long_uchar_copy(l, c);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "long_uchar end: %d", result);
	fclose(fp);

	return result;
}

extern "C" __declspec(dllexport) __int16 __stdcall uchar_long(unsigned char *c,long *l)
{
	puchar_long uchar_long_copy = NULL;
	uchar_long_copy = (puchar_long)GetProcAddress(hModule,"uchar_long");
	return uchar_long_copy(c, l);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_4404(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	psrd_4404 srd_4404_copy = NULL;
	srd_4404_copy = (psrd_4404)GetProcAddress(hModule,"srd_4404");
	return srd_4404_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_4404(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pswr_4404 swr_4404_copy = NULL;
	swr_4404_copy = (pswr_4404)GetProcAddress(hModule,"swr_4404");
	return swr_4404_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_4404(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pcsc_4404 csc_4404_copy = NULL;
	csc_4404_copy = (pcsc_4404)GetProcAddress(hModule,"csc_4404");
	return csc_4404_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_4404(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pwsc_4404 wsc_4404_copy = NULL;
	wsc_4404_copy = (pwsc_4404)GetProcAddress(hModule,"wsc_4404");
	return wsc_4404_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_4404(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	prsc_4404 rsc_4404_copy = NULL;
	rsc_4404_copy = (prsc_4404)GetProcAddress(hModule,"rsc_4404");
	return rsc_4404_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_4404(HANDLE icdev,__int16 *counter)
{
	prsct_4404 rsct_4404_copy = NULL;
	rsct_4404_copy = (prsct_4404)GetProcAddress(hModule,"rsct_4404");
	return rsct_4404_copy(icdev, counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall cesc_4404(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pcesc_4404 cesc_4404_copy = NULL;
	cesc_4404_copy = (pcesc_4404)GetProcAddress(hModule,"cesc_4404");
	return cesc_4404_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wesc_4404(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pwesc_4404 wesc_4404_copy = NULL;
	wesc_4404_copy = (pwesc_4404)GetProcAddress(hModule,"wesc_4404");
	return wesc_4404_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall resc_4404(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	presc_4404 resc_4404_copy = NULL;
	resc_4404_copy = (presc_4404)GetProcAddress(hModule,"resc_4404");
	return resc_4404_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall resct_4404(HANDLE icdev,__int16 *counter)
{
	presct_4404 resct_4404_copy = NULL;
	resct_4404_copy = (presct_4404)GetProcAddress(hModule,"resct_4404");
	return resct_4404_copy(icdev, counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall ser_4404(HANDLE icdev,__int16 offset,__int16 len)
{
	pser_4404 ser_4404_copy = NULL;
	ser_4404_copy = (pser_4404)GetProcAddress(hModule,"ser_4404");
	return ser_4404_copy(icdev, offset, len);
}

extern "C" __declspec(dllexport) __int16 __stdcall fakefus_4404(HANDLE icdev,__int16 mode)
{
	pfakefus_4404 fakefus_4404_copy = NULL;
	fakefus_4404_copy = (pfakefus_4404)GetProcAddress(hModule,"fakefus_4404");
	return fakefus_4404_copy(icdev, mode);
}

extern "C" __declspec(dllexport) __int16 __stdcall clrpr_4404(HANDLE icdev)
{
	pclrpr_4404 clrpr_4404_copy = NULL;
	clrpr_4404_copy = (pclrpr_4404)GetProcAddress(hModule,"clrpr_4404");
	return clrpr_4404_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall clrrd_4404(HANDLE icdev)
{
	pclrrd_4404 clrrd_4404_copy = NULL;
	clrrd_4404_copy = (pclrrd_4404)GetProcAddress(hModule,"clrrd_4404");
	return clrrd_4404_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall psnl_4404(HANDLE icdev)
{
	ppsnl_4404 psnl_4404_copy = NULL;
	psnl_4404_copy = (ppsnl_4404)GetProcAddress(hModule,"psnl_4404");
	return psnl_4404_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_4404(HANDLE icdev)
{
	pchk_4404 chk_4404_copy = NULL;
	chk_4404_copy = (pchk_4404)GetProcAddress(hModule,"chk_4404");
	return chk_4404_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_4406(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	psrd_4406 srd_4406_copy = NULL;
	srd_4406_copy = (psrd_4406)GetProcAddress(hModule,"srd_4406");
	return srd_4406_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_4406(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pswr_4406 swr_4406_copy = NULL;
	swr_4406_copy = (pswr_4406)GetProcAddress(hModule,"swr_4406");
	return swr_4406_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_4406(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pcsc_4406 csc_4406_copy = NULL;
	csc_4406_copy = (pcsc_4406)GetProcAddress(hModule,"csc_4406");
	return csc_4406_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_4406(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pwsc_4406 wsc_4406_copy = NULL;
	wsc_4406_copy = (pwsc_4406)GetProcAddress(hModule,"wsc_4406");
	return wsc_4406_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_4406(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	prsc_4406 rsc_4406_copy = NULL;
	rsc_4406_copy = (prsc_4406)GetProcAddress(hModule,"rsc_4406");
	return rsc_4406_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_4406(HANDLE icdev,__int16 *Counter)
{
	prsct_4406 rsct_4406_copy = NULL;
	rsct_4406_copy = (prsct_4406)GetProcAddress(hModule,"rsct_4406");
	return rsct_4406_copy(icdev, Counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall eswc_4406(HANDLE icdev,__int16 offset)
{
	peswc_4406 eswc_4406_copy = NULL;
	eswc_4406_copy = (peswc_4406)GetProcAddress(hModule,"eswc_4406");
	return eswc_4406_copy(icdev, offset);
}

extern "C" __declspec(dllexport) __int16 __stdcall psnl_4406(HANDLE icdev)
{
	ppsnl_4406 psnl_4406_copy = NULL;
	psnl_4406_copy = (ppsnl_4406)GetProcAddress(hModule,"psnl_4406");
	return psnl_4406_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_4406(HANDLE icdev)
{
	pchk_4406 chk_4406_copy = NULL;
	chk_4406_copy = (pchk_4406)GetProcAddress(hModule,"chk_4406");
	return chk_4406_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer): %d, %d, %d, %d,", icdev, zone, offset, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	psrd_102 srd_102_copy = NULL;
	srd_102_copy = (psrd_102)GetProcAddress(hModule,"srd_102");
	__int16 r = srd_102_copy(icdev, zone, offset, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer)= %d\n", r);
	fprintf(fp, "srd_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer) data_buffer= ");
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer): %d, %d, %d, %d,", icdev, zone, offset, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	pswr_102 swr_102_copy = NULL;
	swr_102_copy = (pswr_102)GetProcAddress(hModule,"swr_102");
	__int16 r = swr_102_copy(icdev, zone, offset, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer) = %d\n", r);
	fprintf(fp, "swr_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer) data_buffer= ");
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall ser_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ser_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len): %d, %d, %d, %d\n", icdev, zone, offset, len);
	fclose(fp);

	pser_102 ser_102_copy = NULL;
	ser_102_copy = (pser_102)GetProcAddress(hModule,"ser_102");
	__int16 r = ser_102_copy(icdev, zone, offset, len);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "ser_102(HANDLE icdev,__int16 zone,__int16 offset,__int16 len)=%d\n", r);
	fclose(fp);

	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "csc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer): %d, %d,", icdev, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	fp = fopen(getFileName(), "a");
	fprintf(fp, "102卡密码：");
	for(i = 0; i < 2; i++)
	{
		fprintf(fp, "%02x", data_buffer[i]);
	}
	fprintf(fp, "\n");
	fclose(fp);

	pcsc_102 csc_102_copy = NULL;
	csc_102_copy = (pcsc_102)GetProcAddress(hModule,"csc_102");
	int r = csc_102_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "csc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer) = %d\n", r);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer): %d, %d", icdev, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	prsc_102 rsc_102_copy = NULL;
	rsc_102_copy = (prsc_102)GetProcAddress(hModule,"rsc_102");
	__int16 r = rsc_102_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer) = %d\n", r);
	fprintf(fp, "rsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer) data_buffer =");
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "wsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer): %d, %d", icdev, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	pwsc_102 wsc_102_copy = NULL;
	wsc_102_copy = (pwsc_102)GetProcAddress(hModule,"wsc_102");
	__int16 r = wsc_102_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "wsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer) = %d\n", r);
	fprintf(fp, "wsc_102(HANDLE icdev,__int16 len,unsigned char* data_buffer) data_buffer =");
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_102(HANDLE icdev,__int16* counter)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsct_102(HANDLE icdev,__int16* counter): %d, %d\n", icdev, *counter);
	fclose(fp);

	prsct_102 rsct_102_copy = NULL;
	rsct_102_copy = (prsct_102)GetProcAddress(hModule,"rsct_102");
	__int16 r = rsct_102_copy(icdev, counter);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsct_102(HANDLE icdev,__int16* counter) = %d, counter=%d\n", r, *counter);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall cesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "cesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer): %d, %d, %d,", icdev, zone, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	pcesc_102 cesc_102_copy = NULL;
	cesc_102_copy = (pcesc_102)GetProcAddress(hModule,"cesc_102");
	__int16 r = cesc_102_copy(icdev, zone, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "cesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer) = %d\n", r);
	fprintf(fp, "cesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer) data_buffer =");
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall resc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "resc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer): %d, %d, %d,", icdev, zone, len);
	fclose(fp);

	presc_102 resc_102_copy = NULL;
	resc_102_copy = (presc_102)GetProcAddress(hModule,"resc_102");
	__int16 r = resc_102_copy(icdev, zone, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "resc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer) = %d\n", r);
	fprintf(fp, "resc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer) data_buffer =");
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall wesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "wesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer): %d, %d, %d,", icdev, zone, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	pwesc_102 wesc_102_copy = NULL;
	wesc_102_copy = (pwesc_102)GetProcAddress(hModule,"wesc_102");
	__int16 r = wesc_102_copy(icdev, zone, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "wesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer) = %d\n", r);
	fprintf(fp, "wesc_102(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer) data_buffer =");
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall resct_102(HANDLE icdev,__int16 zone,__int16* counter)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "resct_102(HANDLE icdev,__int16 zone,__int16* counter): %d, %d\n", icdev, zone);
	fclose(fp);

	presct_102 resct_102_copy = NULL;
	resct_102_copy = (presct_102)GetProcAddress(hModule,"resct_102");
	__int16 r = resct_102_copy(icdev, zone, counter);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "resct_102(HANDLE icdev,__int16 zone,__int16* counter): counter:%d = %d\n", *counter, r);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall clrpr_102(HANDLE icdev,__int16 zone)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "clrpr_102(HANDLE icdev,__int16 zone): %d, %d\n", icdev, zone);
	fclose(fp);

	pclrpr_102 clrpr_102_copy = NULL;
	clrpr_102_copy = (pclrpr_102)GetProcAddress(hModule,"clrpr_102");
	__int16 r =  clrpr_102_copy(icdev, zone);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "clrpr_102(HANDLE icdev,__int16 zone) = %d\n", r);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall clrrd_102(HANDLE icdev,__int16 zone)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "clrrd_102(HANDLE icdev,__int16 zone): %d, %d\n", icdev, zone);
	fclose(fp);

	pclrrd_102 clrrd_102_copy = NULL;
	clrrd_102_copy = (pclrrd_102)GetProcAddress(hModule,"clrrd_102");
	__int16 r = clrrd_102_copy(icdev, zone);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "clrrd_102(HANDLE icdev,__int16 zone) = %d\n", r);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall fakefus_102(HANDLE icdev,__int16 mode)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "fakefus_102(HANDLE icdev,__int16 mode): %d, %d\n", icdev, mode);
	fclose(fp);

	pfakefus_102 fakefus_102_copy = NULL;
	fakefus_102_copy = (pfakefus_102)GetProcAddress(hModule,"fakefus_102");
	__int16 r = fakefus_102_copy(icdev, mode);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "fakefus_102(HANDLE icdev,__int16 mode) = %d\n", r);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall psnl_102(HANDLE icdev)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "psnl_102(HANDLE icdev): %d\n", icdev);
	fclose(fp);

	ppsnl_102 psnl_102_copy = NULL;
	psnl_102_copy = (ppsnl_102)GetProcAddress(hModule,"psnl_102");
	__int16 r = psnl_102_copy(icdev);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "psnl_102(HANDLE icdev) = %d\n", r);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_102(HANDLE icdev)
{
	if (NULL == hModule)
	{
		FILE * fp = fopen(getFileName(), "a");
		timestamp(fp);
		fprintf(fp, "chk_102动态库载入错误\n");
		fclose(fp);
	}
	else
	{
		FILE * fp = fopen(getFileName(), "a");
		timestamp(fp);
		fprintf(fp, "chk_102动态库载入成功\n");
		fclose(fp);
	}
	
	pchk_102 chk_102_copy = NULL;
	chk_102_copy = (pchk_102)GetProcAddress(hModule,"chk_102");

	if (NULL == chk_102_copy)
	{
		FILE * fp = fopen(getFileName(), "a");
		timestamp(fp);
		fprintf(fp, "chk_102入口点错误\n");
		fclose(fp);
	}
	else
	{
		FILE * fp = fopen(getFileName(), "a");
		timestamp(fp);
		fprintf(fp, "chk_102入口点成功\n");
		fclose(fp);
	}
	
	__int16 r = chk_102_copy(icdev);
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "chk_102(HANDLE icdev) = %d\n", r);
	fclose(fp);
	
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_1604(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer)
{
	psrd_1604 srd_1604_copy = NULL;
	srd_1604_copy = (psrd_1604)GetProcAddress(hModule,"srd_1604");
	return srd_1604_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_1604(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer)
{
	pswr_1604 swr_1604_copy = NULL;
	swr_1604_copy = (pswr_1604)GetProcAddress(hModule,"swr_1604");
	return swr_1604_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall ser_1604(HANDLE icdev,__int16 zone,__int16 offset,__int16 len)
{
	pser_1604 ser_1604_copy = NULL;
	ser_1604_copy = (pser_1604)GetProcAddress(hModule,"ser_1604");
	return ser_1604_copy(icdev, zone, offset, len);
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_1604(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pcsc_1604 csc_1604_copy = NULL;
	csc_1604_copy = (pcsc_1604)GetProcAddress(hModule,"csc_1604");
	return csc_1604_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_1604(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	prsc_1604 rsc_1604_copy = NULL;
	rsc_1604_copy = (prsc_1604)GetProcAddress(hModule,"rsc_1604");
	return rsc_1604_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_1604(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pwsc_1604 wsc_1604_copy = NULL;
	wsc_1604_copy = (pwsc_1604)GetProcAddress(hModule,"wsc_1604");
	return wsc_1604_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_1604(HANDLE icdev,__int16 zone,__int16* counter)
{
	prsct_1604 rsct_1604_copy = NULL;
	rsct_1604_copy = (prsct_1604)GetProcAddress(hModule,"rsct_1604");
	return rsct_1604_copy(icdev, zone, counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall cesc_1604(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pcesc_1604 cesc_1604_copy = NULL;
	cesc_1604_copy = (pcesc_1604)GetProcAddress(hModule,"cesc_1604");
	return cesc_1604_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall resc_1604(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	presc_1604 resc_1604_copy = NULL;
	resc_1604_copy = (presc_1604)GetProcAddress(hModule,"resc_1604");
	return resc_1604_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wesc_1604(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pwesc_1604 wesc_1604_copy = NULL;
	wesc_1604_copy = (pwesc_1604)GetProcAddress(hModule,"wesc_1604");
	return wesc_1604_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall resct_1604(HANDLE icdev,__int16 zone,__int16* counter)
{
	presct_1604 resct_1604_copy = NULL;
	resct_1604_copy = (presct_1604)GetProcAddress(hModule,"resct_1604");
	return resct_1604_copy(icdev, zone, counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall clrpr_1604(HANDLE icdev,__int16 zone)
{
	pclrpr_1604 clrpr_1604_copy = NULL;
	clrpr_1604_copy = (pclrpr_1604)GetProcAddress(hModule,"clrpr_1604");
	return clrpr_1604_copy(icdev, zone);
}

extern "C" __declspec(dllexport) __int16 __stdcall clrrd_1604(HANDLE icdev,__int16 zone)
{
	pclrrd_1604 clrrd_1604_copy = NULL;
	clrrd_1604_copy = (pclrrd_1604)GetProcAddress(hModule,"clrrd_1604");
	return clrrd_1604_copy(icdev, zone);
}

extern "C" __declspec(dllexport) __int16 __stdcall fakefus_1604(HANDLE icdev,__int16 mode)
{
	pfakefus_1604 fakefus_1604_copy = NULL;
	fakefus_1604_copy = (pfakefus_1604)GetProcAddress(hModule,"fakefus_1604");
	return fakefus_1604_copy(icdev, mode);
}

extern "C" __declspec(dllexport) __int16 __stdcall psnl_1604(HANDLE icdev)
{
	ppsnl_1604 psnl_1604_copy = NULL;
	psnl_1604_copy = (ppsnl_1604)GetProcAddress(hModule,"psnl_1604");
	return psnl_1604_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_1604(HANDLE icdev)
{
	pchk_1604 chk_1604_copy = NULL;
	chk_1604_copy = (pchk_1604)GetProcAddress(hModule,"chk_1604");
	return chk_1604_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_1604b(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer)
{
	psrd_1604b srd_1604b_copy = NULL;
	srd_1604b_copy = (psrd_1604b)GetProcAddress(hModule,"srd_1604b");
	return srd_1604b_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_1604b(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char* data_buffer)
{
	pswr_1604b swr_1604b_copy = NULL;
	swr_1604b_copy = (pswr_1604b)GetProcAddress(hModule,"swr_1604b");
	return swr_1604b_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall ser_1604b(HANDLE icdev,__int16 zone,__int16 offset,__int16 len)
{
	pser_1604b ser_1604b_copy = NULL;
	ser_1604b_copy = (pser_1604b)GetProcAddress(hModule,"ser_1604b");
	return ser_1604b_copy(icdev, zone, offset, len);
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_1604b(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pcsc_1604b csc_1604b_copy = NULL;
	csc_1604b_copy = (pcsc_1604b)GetProcAddress(hModule,"csc_1604b");
	return csc_1604b_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_1604b(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	prsc_1604b rsc_1604b_copy = NULL;
	rsc_1604b_copy = (prsc_1604b)GetProcAddress(hModule,"rsc_1604b");
	return rsc_1604b_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_1604b(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pwsc_1604b wsc_1604b_copy = NULL;
	wsc_1604b_copy = (pwsc_1604b)GetProcAddress(hModule,"wsc_1604b");
	return wsc_1604b_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_1604b(HANDLE icdev,__int16 zone,__int16* counter)
{
	prsct_1604b rsct_1604b_copy = NULL;
	rsct_1604b_copy = (prsct_1604b)GetProcAddress(hModule,"rsct_1604b");
	return rsct_1604b_copy(icdev, zone, counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall cesc_1604b(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pcesc_1604b cesc_1604b_copy = NULL;
	cesc_1604b_copy = (pcesc_1604b)GetProcAddress(hModule,"cesc_1604b");
	return cesc_1604b_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall resc_1604b(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	presc_1604b resc_1604b_copy = NULL;
	resc_1604b_copy = (presc_1604b)GetProcAddress(hModule,"resc_1604b");
	return resc_1604b_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wesc_1604b(HANDLE icdev,__int16 zone,__int16 len,unsigned char* data_buffer)
{
	pwesc_1604b wesc_1604b_copy = NULL;
	wesc_1604b_copy = (pwesc_1604b)GetProcAddress(hModule,"wesc_1604b");
	return wesc_1604b_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall resct_1604b(HANDLE icdev,__int16 zone,__int16* counter)
{
	presct_1604b resct_1604b_copy = NULL;
	resct_1604b_copy = (presct_1604b)GetProcAddress(hModule,"resct_1604b");
	return resct_1604b_copy(icdev, zone, counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall clrpr_1604b(HANDLE icdev,__int16 zone)
{
	pclrpr_1604b clrpr_1604b_copy = NULL;
	clrpr_1604b_copy = (pclrpr_1604b)GetProcAddress(hModule,"clrpr_1604b");
	return clrpr_1604b_copy(icdev, zone);
}

extern "C" __declspec(dllexport) __int16 __stdcall clrrd_1604b(HANDLE icdev,__int16 zone)
{
	pclrrd_1604b clrrd_1604b_copy = NULL;
	clrrd_1604b_copy = (pclrrd_1604b)GetProcAddress(hModule,"clrrd_1604b");
	return clrrd_1604b_copy(icdev, zone);
}

extern "C" __declspec(dllexport) __int16 __stdcall fakefus_1604b(HANDLE icdev,__int16 mode)
{
	pfakefus_1604b fakefus_1604b_copy = NULL;
	fakefus_1604b_copy = (pfakefus_1604b)GetProcAddress(hModule,"fakefus_1604b");
	return fakefus_1604b_copy(icdev, mode);
}

extern "C" __declspec(dllexport) __int16 __stdcall psnl_1604b(HANDLE icdev)
{
	ppsnl_1604b psnl_1604b_copy = NULL;
	psnl_1604b_copy = (ppsnl_1604b)GetProcAddress(hModule,"psnl_1604b");
	return psnl_1604b_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_1604b(HANDLE icdev)
{
	pchk_1604b chk_1604b_copy = NULL;
	chk_1604b_copy = (pchk_1604b)GetProcAddress(hModule,"chk_1604b");
	return chk_1604b_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_24c01a(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	pswr_24c01a swr_24c01a_copy = NULL;
	swr_24c01a_copy = (pswr_24c01a)GetProcAddress(hModule,"swr_24c01a");
	return swr_24c01a_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_24c01a(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	psrd_24c01a srd_24c01a_copy = NULL;
	srd_24c01a_copy = (psrd_24c01a)GetProcAddress(hModule,"srd_24c01a");
	return srd_24c01a_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_24c01a(HANDLE icdev)
{
	pchk_24c01a chk_24c01a_copy = NULL;
	chk_24c01a_copy = (pchk_24c01a)GetProcAddress(hModule,"chk_24c01a");
	return chk_24c01a_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall  swr_24c02(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	pswr_24c02 swr_24c02_copy = NULL;
	swr_24c02_copy = (pswr_24c02)GetProcAddress(hModule,"swr_24c02");
	return swr_24c02_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  srd_24c02(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	psrd_24c02 srd_24c02_copy = NULL;
	srd_24c02_copy = (psrd_24c02)GetProcAddress(hModule,"srd_24c02");
	return srd_24c02_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  chk_24c02(HANDLE icdev)
{
	pchk_24c02 chk_24c02_copy = NULL;
	chk_24c02_copy = (pchk_24c02)GetProcAddress(hModule,"chk_24c02");
	return chk_24c02_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall  swr_24c04(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	pswr_24c04 swr_24c04_copy = NULL;
	swr_24c04_copy = (pswr_24c04)GetProcAddress(hModule,"swr_24c04");
	return swr_24c04_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  srd_24c04(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	psrd_24c04 srd_24c04_copy = NULL;
	srd_24c04_copy = (psrd_24c04)GetProcAddress(hModule,"srd_24c04");
	return srd_24c04_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  chk_24c04(HANDLE icdev)
{
	pchk_24c04 chk_24c04_copy = NULL;
	chk_24c04_copy = (pchk_24c04)GetProcAddress(hModule,"chk_24c04");
	return chk_24c04_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall  swr_24c08(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	pswr_24c08 swr_24c08_copy = NULL;
	swr_24c08_copy = (pswr_24c08)GetProcAddress(hModule,"swr_24c08");
	return swr_24c08_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  srd_24c08(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	psrd_24c08 srd_24c08_copy = NULL;
	srd_24c08_copy = (psrd_24c08)GetProcAddress(hModule,"srd_24c08");
	return srd_24c08_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  chk_24c08(HANDLE icdev)
{
	pchk_24c08 chk_24c08_copy = NULL;
	chk_24c08_copy = (pchk_24c08)GetProcAddress(hModule,"chk_24c08");
	return chk_24c08_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall  swr_24c16(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	pswr_24c16 swr_24c16_copy = NULL;
	swr_24c16_copy = (pswr_24c16)GetProcAddress(hModule,"swr_24c16");
	return swr_24c16_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  srd_24c16(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	psrd_24c16 srd_24c16_copy = NULL;
	srd_24c16_copy = (psrd_24c16)GetProcAddress(hModule,"srd_24c16");
	return srd_24c16_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall  chk_24c16(HANDLE icdev)
{
	pchk_24c16 chk_24c16_copy = NULL;
	chk_24c16_copy = (pchk_24c16)GetProcAddress(hModule,"chk_24c16");
	return chk_24c16_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_24c32(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	pswr_24c32 swr_24c32_copy = NULL;
	swr_24c32_copy = (pswr_24c32)GetProcAddress(hModule,"swr_24c32");
	return swr_24c32_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_24c32(HANDLE icdev, __int16 offset, __int16 len, unsigned char* data_buffer)
{
	psrd_24c32 srd_24c32_copy = NULL;
	srd_24c32_copy = (psrd_24c32)GetProcAddress(hModule,"srd_24c32");
	return srd_24c32_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_24c32(HANDLE icdev)
{
	pchk_24c32 chk_24c32_copy = NULL;
	chk_24c32_copy = (pchk_24c32)GetProcAddress(hModule,"chk_24c32");
	return chk_24c32_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_24c64(HANDLE icdev, __int16 offset, __int16 len, unsigned char *data_buffer)
{
	pswr_24c64 swr_24c64_copy = NULL;
	swr_24c64_copy = (pswr_24c64)GetProcAddress(hModule,"swr_24c64");
	return swr_24c64_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_24c64(HANDLE icdev, __int16 offset, __int16 len, unsigned char *data_buffer)
{
	psrd_24c64 srd_24c64_copy = NULL;
	srd_24c64_copy = (psrd_24c64)GetProcAddress(hModule,"srd_24c64");
	return srd_24c64_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_24c64(HANDLE icdev)
{
	pchk_24c64 chk_24c64_copy = NULL;
	chk_24c64_copy = (pchk_24c64)GetProcAddress(hModule,"chk_24c64");
	return chk_24c64_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_4418(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	psrd_4418 srd_4418_copy = NULL;
	srd_4418_copy = (psrd_4418)GetProcAddress(hModule,"srd_4418");
	return srd_4418_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_4418(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pswr_4418 swr_4418_copy = NULL;
	swr_4418_copy = (pswr_4418)GetProcAddress(hModule,"swr_4418");
	return swr_4418_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rdwpb_4418(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	prdwpb_4418 rdwpb_4418_copy = NULL;
	rdwpb_4418_copy = (prdwpb_4418)GetProcAddress(hModule,"rdwpb_4418");
	return rdwpb_4418_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wrwpb_4418(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pwrwpb_4418 wrwpb_4418_copy = NULL;
	wrwpb_4418_copy = (pwrwpb_4418)GetProcAddress(hModule,"wrwpb_4418");
	return wrwpb_4418_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall pwr_4418(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	ppwr_4418 pwr_4418_copy = NULL;
	pwr_4418_copy = (ppwr_4418)GetProcAddress(hModule,"pwr_4418");
	return pwr_4418_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_4418(HANDLE icdev)
{
	pchk_4418 chk_4418_copy = NULL;
	chk_4418_copy = (pchk_4418)GetProcAddress(hModule,"chk_4418");
	return chk_4418_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_4428(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	psrd_4428 srd_4428_copy = NULL;
	srd_4428_copy = (psrd_4428)GetProcAddress(hModule,"srd_4428");
	return srd_4428_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_4428(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pswr_4428 swr_4428_copy = NULL;
	swr_4428_copy = (pswr_4428)GetProcAddress(hModule,"swr_4428");
	return swr_4428_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rdwpb_4428(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	prdwpb_4428 rdwpb_4428_copy = NULL;
	rdwpb_4428_copy = (prdwpb_4428)GetProcAddress(hModule,"rdwpb_4428");
	return rdwpb_4428_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wrwpb_4428(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pwrwpb_4428 wrwpb_4428_copy = NULL;
	wrwpb_4428_copy = (pwrwpb_4428)GetProcAddress(hModule,"wrwpb_4428");
	return wrwpb_4428_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall pwr_4428(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	ppwr_4428 pwr_4428_copy = NULL;
	pwr_4428_copy = (ppwr_4428)GetProcAddress(hModule,"pwr_4428");
	return pwr_4428_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_4428(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pcsc_4428 csc_4428_copy = NULL;
	csc_4428_copy = (pcsc_4428)GetProcAddress(hModule,"csc_4428");
	return csc_4428_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_4428(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	pwsc_4428 wsc_4428_copy = NULL;
	wsc_4428_copy = (pwsc_4428)GetProcAddress(hModule,"wsc_4428");
	return wsc_4428_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_4428(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	prsc_4428 rsc_4428_copy = NULL;
	rsc_4428_copy = (prsc_4428)GetProcAddress(hModule,"rsc_4428");
	return rsc_4428_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_4428(HANDLE icdev,__int16 *counter)
{
	prsct_4428 rsct_4428_copy = NULL;
	rsct_4428_copy = (prsct_4428)GetProcAddress(hModule,"rsct_4428");
	return rsct_4428_copy(icdev, counter);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_4428(HANDLE icdev)
{
	pchk_4428 chk_4428_copy = NULL;
	chk_4428_copy = (pchk_4428)GetProcAddress(hModule,"chk_4428");
	return chk_4428_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer): %d, %d, %d, ", icdev, offset, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	psrd_4442 srd_4442_copy = NULL;
	srd_4442_copy = (psrd_4442)GetProcAddress(hModule,"srd_4442");
	__int16 r = srd_4442_copy(icdev, offset, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "srd_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)= %d, data_buffer=", r);
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer): %d, %d, %d, ", icdev, offset, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	pswr_4442 swr_4442_copy = NULL;
	swr_4442_copy = (pswr_4442)GetProcAddress(hModule,"swr_4442");
	__int16 r = swr_4442_copy(icdev, offset, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "swr_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)= %d, data_buffer=", r);
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall prd_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "prd_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer): %d, %d, ", icdev, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	pprd_4442 prd_4442_copy = NULL;
	prd_4442_copy = (pprd_4442)GetProcAddress(hModule,"prd_4442");
	int r = prd_4442_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "prd_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)= %d, data_buffer=", r);
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall pwr_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "pwr_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer): %d, %d, %d, ", icdev, offset, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	ppwr_4442 pwr_4442_copy = NULL;
	pwr_4442_copy = (ppwr_4442)GetProcAddress(hModule,"pwr_4442");
	int r =  pwr_4442_copy(icdev, offset, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "pwr_4442(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)= %d, data_buffer=", r);
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	return r;

}

extern "C" __declspec(dllexport) __int16 __stdcall csc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "csc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer): %d, %d, ", icdev, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);


	fp = fopen(getFileName(), "a");
	fprintf(fp, "4442卡密码：");
	for(i = 0; i < 3; i++)
	{
		fprintf(fp, "%02x", data_buffer[i]);
	}
	fprintf(fp, "\n");
	fclose(fp);

	pcsc_4442 csc_4442_copy = NULL;
	csc_4442_copy = (pcsc_4442)GetProcAddress(hModule,"csc_4442");
	int r = csc_4442_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "csc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)= %d, data_buffer=", r);
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "wsc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer): %d, %d, ", icdev, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	fp = fopen(getFileName(), "a");
	fprintf(fp, "写4442卡密码：");
	for(i = 0; i < 3; i++)
	{
		fprintf(fp, "%02x", data_buffer[i]);
	}
	fprintf(fp, "\n");
	fclose(fp);

	pwsc_4442 wsc_4442_copy = NULL;
	wsc_4442_copy = (pwsc_4442)GetProcAddress(hModule,"wsc_4442");
	int r = wsc_4442_copy(icdev, len, data_buffer);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "wsc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)= %d, data_buffer=", r);
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer): %d, %d, ", icdev, len);
	for(int i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);

	prsc_4442 rsc_4442_copy = NULL;
	rsc_4442_copy = (prsc_4442)GetProcAddress(hModule,"rsc_4442");
	int r = rsc_4442_copy(icdev, len, data_buffer);


	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsc_4442(HANDLE icdev,__int16 len, unsigned char *data_buffer)= %d, data_buffer=", r);
	for(i=0; i<len; i++)
		fprintf(fp, "%02x ", data_buffer[i]);
	fprintf(fp, "\n");
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_4442(HANDLE icdev,__int16 *counter)
{
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsct_4442(HANDLE icdev,__int16 *counter): %d, %d\n", icdev, *counter);
	fclose(fp);

	prsct_4442 rsct_4442_copy = NULL;
	rsct_4442_copy = (prsct_4442)GetProcAddress(hModule,"rsct_4442");
	int r = rsct_4442_copy(icdev, counter);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "rsct_4442(HANDLE icdev,__int16 *counter)=%d\n", r);
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_4442(HANDLE icdev)
{	
	FILE * fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "chk_4442(HANDLE icdev): %d\n", icdev);
	fclose(fp);

	pchk_4442 chk_4442_copy = NULL;
	chk_4442_copy = (pchk_4442)GetProcAddress(hModule,"chk_4442");
	int r = chk_4442_copy(icdev);

	fp = fopen(getFileName(), "a");
	timestamp(fp);
	fprintf(fp, "chk_4442(HANDLE icdev)= %d\n", r);
	fclose(fp);
	return r;
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_4432(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)
{
	psrd_4432 srd_4432_copy = NULL;
	srd_4432_copy = (psrd_4432)GetProcAddress(hModule,"srd_4432");
	return srd_4432_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_4432(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)
{
	pswr_4432 swr_4432_copy = NULL;
	swr_4432_copy = (pswr_4432)GetProcAddress(hModule,"swr_4432");
	return swr_4432_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall prd_4432(HANDLE icdev,__int16 len, unsigned char *data_buffer)
{
	pprd_4432 prd_4432_copy = NULL;
	prd_4432_copy = (pprd_4432)GetProcAddress(hModule,"prd_4432");
	return prd_4432_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall pwr_4432(HANDLE icdev,__int16 offset,__int16 len, unsigned char *data_buffer)
{
	ppwr_4432 pwr_4432_copy = NULL;
	pwr_4432_copy = (ppwr_4432)GetProcAddress(hModule,"pwr_4432");
	return pwr_4432_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_4432(HANDLE icdev)
{
	pchk_4432 chk_4432_copy = NULL;
	chk_4432_copy = (pchk_4432)GetProcAddress(hModule,"chk_4432");
	return chk_4432_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_45d041(HANDLE icdev,__int16 page,__int16 offset,unsigned long len, unsigned char *data_buffer)
{
	psrd_45d041 srd_45d041_copy = NULL;
	srd_45d041_copy = (psrd_45d041)GetProcAddress(hModule,"srd_45d041");
	return srd_45d041_copy(icdev, page, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_45d041(HANDLE icdev,__int16 page,__int16 offset,unsigned long len, unsigned char *data_buffer)
{
	pswr_45d041 swr_45d041_copy = NULL;
	swr_45d041_copy = (pswr_45d041)GetProcAddress(hModule,"swr_45d041");
	return swr_45d041_copy(icdev, page, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rdstrg_45d041(HANDLE icdev,unsigned char *ch)
{
	prdstrg_45d041 rdstrg_45d041_copy = NULL;
	rdstrg_45d041_copy = (prdstrg_45d041)GetProcAddress(hModule,"rdstrg_45d041");
	return rdstrg_45d041_copy(icdev, ch);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_45d041(HANDLE icdev)
{
	pchk_45d041 chk_45d041_copy = NULL;
	chk_45d041_copy = (pchk_45d041)GetProcAddress(hModule,"chk_45d041");
	return chk_45d041_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_93c46a(HANDLE icdev,__int16 offset, __int16 len, unsigned char *data_buffer)
{
	pswr_93c46a swr_93c46a_copy = NULL;
	swr_93c46a_copy = (pswr_93c46a)GetProcAddress(hModule,"swr_93c46a");
	return swr_93c46a_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_93c46a(HANDLE icdev,__int16 offset, __int16 len, unsigned char *data_buffer)
{
	psrd_93c46a srd_93c46a_copy = NULL;
	srd_93c46a_copy = (psrd_93c46a)GetProcAddress(hModule,"srd_93c46a");
	return srd_93c46a_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall eral_93c46a(HANDLE icdev)
{
	peral_93c46a eral_93c46a_copy = NULL;
	eral_93c46a_copy = (peral_93c46a)GetProcAddress(hModule,"eral_93c46a");
	return eral_93c46a_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_93c46a(HANDLE icdev)
{
	pchk_93c46a chk_93c46a_copy = NULL;
	chk_93c46a_copy = (pchk_93c46a)GetProcAddress(hModule,"chk_93c46a");
	return chk_93c46a_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_93c46(HANDLE icdev,__int16 offset, __int16 len, unsigned char *data_buffer)
{
	pswr_93c46 swr_93c46_copy = NULL;
	swr_93c46_copy = (pswr_93c46)GetProcAddress(hModule,"swr_93c46");
	return swr_93c46_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_93c46(HANDLE icdev,__int16 offset, __int16 len, unsigned char *data_buffer)
{
	psrd_93c46 srd_93c46_copy = NULL;
	srd_93c46_copy = (psrd_93c46)GetProcAddress(hModule,"srd_93c46");
	return srd_93c46_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall eral_93c46(HANDLE icdev)
{
	peral_93c46 eral_93c46_copy = NULL;
	eral_93c46_copy = (peral_93c46)GetProcAddress(hModule,"eral_93c46");
	return eral_93c46_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_93c46(HANDLE icdev)
{
	pchk_93c46 chk_93c46_copy = NULL;
	chk_93c46_copy = (pchk_93c46)GetProcAddress(hModule,"chk_93c46");
	return chk_93c46_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall cpu_reset(HANDLE icdev,unsigned char *data_buffer)
{
	pcpu_reset cpu_reset_copy = NULL;
	cpu_reset_copy = (pcpu_reset)GetProcAddress(hModule,"cpu_reset");
	return cpu_reset_copy(icdev, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall cpu_protocol(HANDLE icdev,__int16 len, unsigned char *send_cmd, unsigned char *receive_data)
{
	pcpu_protocol cpu_protocol_copy = NULL;
	cpu_protocol_copy = (pcpu_protocol)GetProcAddress(hModule,"cpu_protocol");
	return cpu_protocol_copy(icdev, len, send_cmd, receive_data);
}

extern "C" __declspec(dllexport) __int16 __stdcall cpu_comres(HANDLE icdev,__int16 len, unsigned char *send_cmd, unsigned char *receive_data)
{
	pcpu_comres cpu_comres_copy = NULL;
	cpu_comres_copy = (pcpu_comres)GetProcAddress(hModule,"cpu_comres");
	return cpu_comres_copy(icdev, len, send_cmd, receive_data);
}

extern "C" __declspec(dllexport) __int16 __stdcall set_card_baud(HANDLE icdev, unsigned char CardType,unsigned char BaudCode)
{
	pset_card_baud set_card_baud_copy = NULL;
	set_card_baud_copy = (pset_card_baud)GetProcAddress(hModule,"set_card_baud");
	return set_card_baud_copy(icdev, CardType, BaudCode);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_power_on(HANDLE icdev)
{
	psam_power_on sam_power_on_copy = NULL;
	sam_power_on_copy = (psam_power_on)GetProcAddress(hModule,"sam_power_on");
	return sam_power_on_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_power_down(HANDLE icdev)
{
	psam_power_down sam_power_down_copy = NULL;
	sam_power_down_copy = (psam_power_down)GetProcAddress(hModule,"sam_power_down");
	return sam_power_down_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_reset(HANDLE icdev,__int16 *len,unsigned char *receive_data)
{
	psam_reset sam_reset_copy = NULL;
	sam_reset_copy = (psam_reset)GetProcAddress(hModule,"sam_reset");
	return sam_reset_copy(icdev, len, receive_data);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_select(HANDLE icdev, unsigned char SelectCard)
{
	psam_select sam_select_copy = NULL;
	sam_select_copy = (psam_select)GetProcAddress(hModule,"sam_select");
	return sam_select_copy(icdev, SelectCard);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_protocol(HANDLE icdev,__int16 sLen, unsigned char *send_cmd, unsigned char *receive_data)
{
	psam_protocol sam_protocol_copy = NULL;
	sam_protocol_copy = (psam_protocol)GetProcAddress(hModule,"sam_select");
	return sam_protocol_copy(icdev, sLen, send_cmd, receive_data);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_comres(HANDLE icdev,__int16 sLen, unsigned char *send_cmd, unsigned char *receive_data)
{
	psam_comres sam_comres_copy = NULL;
	sam_comres_copy = (psam_comres)GetProcAddress(hModule,"sam_comres");
	return sam_comres_copy(icdev, sLen, send_cmd, receive_data);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_slt_power_on(HANDLE icdev,unsigned char CardType)
{
	psam_slt_power_on sam_slt_power_on_copy = NULL;
	sam_slt_power_on_copy = (psam_slt_power_on)GetProcAddress(hModule,"sam_slt_power_on");
	return sam_slt_power_on_copy(icdev, CardType);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_slt_power_down(HANDLE icdev,unsigned char CardType)
{
	psam_slt_power_down sam_slt_power_down_copy = NULL;
	sam_slt_power_down_copy = (psam_slt_power_down)GetProcAddress(hModule,"sam_slt_power_down");
	return sam_slt_power_down_copy(icdev, CardType);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_slt_reset(HANDLE icdev,unsigned char CardType,__int16 *len,unsigned char *receive_data)
{
	psam_slt_reset sam_slt_reset_copy = NULL;
	sam_slt_reset_copy = (psam_slt_reset)GetProcAddress(hModule,"sam_slt_reset");
	return sam_slt_reset_copy(icdev, CardType, len, receive_data);
}

extern "C" __declspec(dllexport) __int16 __stdcall sam_slt_protocol(HANDLE icdev,unsigned char CardType,__int16 sLen, unsigned char *send_cmd, __int16 *rLen,unsigned char *receive_data)
{
	psam_slt_protocol sam_slt_protocol_copy = NULL;
	sam_slt_protocol_copy = (psam_slt_protocol)GetProcAddress(hModule,"sam_slt_protocol");
	return sam_slt_protocol_copy(icdev, CardType, sLen, send_cmd, rLen, receive_data);
}

extern "C" __declspec(dllexport) __int16 __stdcall reset_1608(HANDLE icdev,__int16 len, unsigned char *data_buffer)
{
	preset_1608 reset_1608_copy = NULL;
	reset_1608_copy = (preset_1608)GetProcAddress(hModule,"reset_1608");
	return reset_1608_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall init_auth_1608(HANDLE icdev,__int16 len, unsigned char *data_buffer)
{
	pinit_auth_1608 init_auth_1608_copy = NULL;
	init_auth_1608_copy = (pinit_auth_1608)GetProcAddress(hModule,"init_auth_1608");
	return init_auth_1608_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall callsrd_1608(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pcallsrd_1608 callsrd_1608_copy = NULL;
	callsrd_1608_copy = (pcallsrd_1608)GetProcAddress(hModule,"callsrd_1608");
	return callsrd_1608_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_1608(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	psrd_1608 srd_1608_copy = NULL;
	srd_1608_copy = (psrd_1608)GetProcAddress(hModule,"srd_1608");
	return srd_1608_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srfus_1608(HANDLE icdev,__int16 len,unsigned char *data_buffer)
{
	psrfus_1608 srfus_1608_copy = NULL;
	srfus_1608_copy = (psrfus_1608)GetProcAddress(hModule,"srfus_1608");
	return srfus_1608_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall callswr_1608(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pcallswr_1608 callswr_1608_copy = NULL;
	callswr_1608_copy = (pcallswr_1608)GetProcAddress(hModule,"callswr_1608");
	return callswr_1608_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_1608(HANDLE icdev,__int16 zone,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pswr_1608 swr_1608_copy = NULL;
	swr_1608_copy = (pswr_1608)GetProcAddress(hModule,"swr_1608");
	return swr_1608_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_1608(HANDLE icdev,__int16 zone,__int16 len,__int16 rw,unsigned char *data_buffer)
{
	pcsc_1608 csc_1608_copy = NULL;
	csc_1608_copy = (pcsc_1608)GetProcAddress(hModule,"csc_1608");
	return csc_1608_copy(icdev, zone, len, rw, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall psnl_1608(HANDLE icdev)
{
	ppsnl_1608 psnl_1608_copy = NULL;
	psnl_1608_copy = (ppsnl_1608)GetProcAddress(hModule,"psnl_1608");
	return psnl_1608_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_1608(HANDLE icdev,__int16 zone,__int16 len, __int16 rw,unsigned char *data_buffer)
{
	pwsc_1608 wsc_1608_copy = NULL;
	wsc_1608_copy = (pwsc_1608)GetProcAddress(hModule,"wsc_1608");
	return wsc_1608_copy(icdev, zone, len, rw, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_1608(HANDLE icdev,__int16 zone,__int16 len,__int16 rw,unsigned char *data_buffer)
{
	prsc_1608 rsc_1608_copy = NULL;
	rsc_1608_copy = (prsc_1608)GetProcAddress(hModule,"rsc_1608");
	return rsc_1608_copy(icdev, zone, len, rw, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_1608(HANDLE icdev,__int16 zone,__int16 len, __int16 rw,unsigned char *data_buffer)
{
	prsct_1608 rsct_1608_copy = NULL;
	rsct_1608_copy = (prsct_1608)GetProcAddress(hModule,"rsct_1608");
	return rsct_1608_copy(icdev, zone, len, rw, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rac_1608(HANDLE icdev,__int16 zone,__int16 len,unsigned char *data_buffer)
{
	prac_1608 rac_1608_copy = NULL;
	rac_1608_copy = (prac_1608)GetProcAddress(hModule,"rac_1608");
	return rac_1608_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wac_1608(HANDLE icdev,__int16 zone,__int16 len,unsigned char *data_buffer)
{
	pwac_1608 wac_1608_copy = NULL;
	wac_1608_copy = (pwac_1608)GetProcAddress(hModule,"wac_1608");
	return wac_1608_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_1608(HANDLE icdev)
{
	pchk_1608 chk_1608_copy = NULL;
	chk_1608_copy = (pchk_1608)GetProcAddress(hModule,"chk_1608");
	return chk_1608_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srdconfig_1608(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	psrdconfig_1608 srdconfig_1608_copy = NULL;
	srdconfig_1608_copy = (psrdconfig_1608)GetProcAddress(hModule,"srdconfig_1608");
	return srdconfig_1608_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swrconfig_1608(HANDLE icdev,__int16 offset,__int16 len,unsigned char *data_buffer)
{
	pswrconfig_1608 swrconfig_1608_copy = NULL;
	swrconfig_1608_copy = (pswrconfig_1608)GetProcAddress(hModule,"swrconfig_1608");
	return swrconfig_1608_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) void __stdcall Authenticate(unsigned char *q1,unsigned char *q2)
{
	pAuthenticate Authenticate_copy = NULL;
	Authenticate_copy = (pAuthenticate)GetProcAddress(hModule,"Authenticate");
	Authenticate_copy(q1, q2);
}

extern "C" __declspec(dllexport) void __stdcall SetInit(unsigned char *crypto, unsigned char *graine,unsigned char *host)
{
	pSetInit SetInit_copy = NULL;
	SetInit_copy = (pSetInit)GetProcAddress(hModule,"SetInit");
	SetInit_copy(crypto, graine, host);
}

extern "C" __declspec(dllexport) __int16 __stdcall reset_153(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	preset_153 reset_153_copy = NULL;
	reset_153_copy = (preset_153)GetProcAddress(hModule,"reset_153");
	return reset_153_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall init_auth_153(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	pinit_auth_153 init_auth_153_copy = NULL;
	init_auth_153_copy = (pinit_auth_153)GetProcAddress(hModule,"init_auth_153");
	return init_auth_153_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_153(HANDLE icdev)
{
	pchk_153 chk_153_copy = NULL;
	chk_153_copy = (pchk_153)GetProcAddress(hModule,"chk_153");
	return chk_153_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_153(HANDLE icdev, __int16 zone, __int16 offset, __int16 len, unsigned char *data_buffer)
{
	psrd_153 srd_153_copy = NULL;
	srd_153_copy = (psrd_153)GetProcAddress(hModule,"srd_153");
	return srd_153_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_153(HANDLE icdev, __int16 zone, __int16 offset, __int16 len, unsigned char *data_buffer)
{
	pswr_153 swr_153_copy = NULL;
	swr_153_copy = (pswr_153)GetProcAddress(hModule,"swr_153");
	return swr_153_copy(icdev, zone, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srdconfig_153(HANDLE icdev, __int16 offset, __int16 len, unsigned char *data_buffer)
{
	psrdconfig_153 srdconfig_153_copy = NULL;
	srdconfig_153_copy = (psrdconfig_153)GetProcAddress(hModule,"srdconfig_153");
	return srdconfig_153_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swrconfig_153(HANDLE icdev, __int16 offset, __int16 len, unsigned char *data_buffer)
{
	pswrconfig_153 swrconfig_153_copy = NULL;
	swrconfig_153_copy = (pswrconfig_153)GetProcAddress(hModule,"swrconfig_153");
	return swrconfig_153_copy(icdev, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rac_153(HANDLE icdev, __int16 zone, __int16 len, unsigned char *data_buffer)
{
	prac_153 rac_153_copy = NULL;
	rac_153_copy = (prac_153)GetProcAddress(hModule,"rac_153");
	return rac_153_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wac_153(HANDLE icdev, __int16 zone, __int16 len, unsigned char *data_buffer)
{
	pwac_153 wac_153_copy = NULL;
	wac_153_copy = (pwac_153)GetProcAddress(hModule,"wac_153");
	return wac_153_copy(icdev, zone, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rautht_153(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	prautht_153 rautht_153_copy = NULL;
	rautht_153_copy = (prautht_153)GetProcAddress(hModule,"rautht_153");
	return rautht_153_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rdcr_153(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	prdcr_153 rdcr_153_copy = NULL;
	rdcr_153_copy = (prdcr_153)GetProcAddress(hModule,"rdcr_153");
	return rdcr_153_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wdcr_153(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	pwdcr_153 wdcr_153_copy = NULL;
	wdcr_153_copy = (pwdcr_153)GetProcAddress(hModule,"wdcr_153");
	return wdcr_153_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall csc_153(HANDLE icdev, __int16 psw_set, __int16 rw, __int16 len, unsigned char *data_buffer)
{
	pcsc_153 csc_153_copy = NULL;
	csc_153_copy = (pcsc_153)GetProcAddress(hModule,"csc_153");
	return csc_153_copy(icdev, psw_set, rw, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsc_153(HANDLE icdev, __int16 psw_set, __int16 rw, __int16 len, unsigned char *data_buffer)
{
	prsc_153 rsc_153_copy = NULL;
	rsc_153_copy = (prsc_153)GetProcAddress(hModule,"rsc_153");
	return rsc_153_copy(icdev, psw_set, rw, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall wsc_153(HANDLE icdev, __int16 psw_set, __int16 rw, __int16 len, unsigned char *data_buffer)
{
	pwsc_153 wsc_153_copy = NULL;
	wsc_153_copy = (pwsc_153)GetProcAddress(hModule,"wsc_153");
	return wsc_153_copy(icdev, psw_set, rw, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall rsct_153(HANDLE icdev, __int16 psw_set, __int16 rw, __int16 len, unsigned char *data_buffer)
{
	prsct_153 rsct_153_copy = NULL;
	rsct_153_copy = (prsct_153)GetProcAddress(hModule,"rsct_153");
	return rsct_153_copy(icdev, psw_set, rw, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srfus_153(HANDLE icdev, __int16 len, unsigned char *data_buffer)
{
	psrfus_153 srfus_153_copy = NULL;
	srfus_153_copy = (psrfus_153)GetProcAddress(hModule,"srfus_153");
	return srfus_153_copy(icdev, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swfus_153(HANDLE icdev, unsigned char data_fus)
{
	pswfus_153 swfus_153_copy = NULL;
	swfus_153_copy = (pswfus_153)GetProcAddress(hModule,"swfus_153");
	return swfus_153_copy(icdev, data_fus);
}

extern "C" __declspec(dllexport) __int16 __stdcall psnl_153(HANDLE icdev)
{
	ppsnl_153 psnl_153_copy = NULL;
	psnl_153_copy = (ppsnl_153)GetProcAddress(hModule,"psnl_153");
	return psnl_153_copy(icdev);
}

extern "C" __declspec(dllexport) unsigned char __stdcall xor(unsigned char char1,unsigned char char2)
{
	pxor xor_copy = NULL;
	xor_copy = (pxor)GetProcAddress(hModule,"xor");
	return xor_copy(char1, char2);
}

extern "C" __declspec(dllexport) unsigned char __stdcall ck_bcc(__int16 len, unsigned char *bcc_buffer)
{
	pck_bcc ck_bcc_copy = NULL;
	ck_bcc_copy = (pck_bcc)GetProcAddress(hModule,"ck_bcc");
	return ck_bcc_copy(len, bcc_buffer);
}

extern "C" __declspec(dllexport) unsigned char __stdcall cr_bcc(__int16 len, unsigned char *bcc_buffer)
{
	pcr_bcc cr_bcc_copy = NULL;
	cr_bcc_copy = (pcr_bcc)GetProcAddress(hModule,"cr_bcc");
	return cr_bcc_copy(len, bcc_buffer);
}

extern "C" __declspec(dllexport) unsigned char __stdcall comm_bcc(__int16 len, unsigned char *bcc_buffer)
{
	pcomm_bcc comm_bcc_copy = NULL;
	comm_bcc_copy = (pcomm_bcc)GetProcAddress(hModule,"comm_bcc");
	return comm_bcc_copy(len, bcc_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall srd_ssf1101(HANDLE icdev,__int16 page,__int16 offset,long len, unsigned char *data_buffer)
{
	psrd_ssf1101 srd_ssf1101_copy = NULL;
	srd_ssf1101_copy = (psrd_ssf1101)GetProcAddress(hModule,"srd_ssf1101");
	return srd_ssf1101_copy(icdev, page, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall swr_ssf1101(HANDLE icdev,__int16 page,__int16 offset,long len, unsigned char *data_buffer)
{
	pswr_ssf1101 swr_ssf1101_copy = NULL;
	swr_ssf1101_copy = (pswr_ssf1101)GetProcAddress(hModule,"swr_ssf1101");
	return swr_ssf1101_copy(icdev, page, offset, len, data_buffer);
}

extern "C" __declspec(dllexport) __int16 __stdcall chk_ssf1101(HANDLE icdev)
{
	pchk_ssf1101 chk_ssf1101_copy = NULL;
	chk_ssf1101_copy = (pchk_ssf1101)GetProcAddress(hModule,"chk_ssf1101");
	return chk_ssf1101_copy(icdev);
}

extern "C" __declspec(dllexport) __int16 __stdcall eral_ssf1101(HANDLE icdev)
{
	peral_ssf1101 eral_ssf1101_copy = NULL;
	eral_ssf1101_copy = (peral_ssf1101)GetProcAddress(hModule,"eral_ssf1101");
	return eral_ssf1101_copy(icdev);
}

void timestamp(FILE * fp)
{
    time_t ltime; /* calendar time */
    ltime=time(NULL); /* get current cal time */
 	struct tm *Tm;
	ltime=time(NULL); /* get current cal time */
	Tm=localtime(&ltime);
    fprintf(fp, "%d-%02d-%02d %02d:%02d:%02d ",  Tm->tm_year+1900, Tm->tm_mon+1, Tm->tm_mday, Tm->tm_hour, Tm->tm_min, Tm->tm_sec);
}

char fn[255];
char * getFileName()
{
    time_t ltime; /* calendar time */
	struct tm *Tm;
	ltime=time(NULL); /* get current cal time */
	Tm=localtime(&ltime);
	sprintf(fn, "c:\\WCSLogs\\%d-%02d-%02d-mw.txt", Tm->tm_year+1900, Tm->tm_mon+1, Tm->tm_mday);
	return fn;
}