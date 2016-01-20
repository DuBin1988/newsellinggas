
                     ---------------------------------------------------
                      MicroDog(UMI/UMC/PMH/PMI) Device Drivers
                      Installing Guide for Windows 9X/ME/NT/2K/XP/
                      WS2003/Vista/XP64/WS2003x64/Vista64/
                      Windows 7 (x86/x64) and Windows server 2008
                     ---------------------------------------------------

                    Copyright (c) 2010, SafeNet China Ltd.

MicroDog(UMI/UMC/PMH/PMI) device driver for Windows 9x/ME should be installed if your protected application with 
Hardware Dog is running at Windows 9x/ME system. 

MicroDog(UMI/UMC/PMH/PMI) device driver for Windows NT/2K/XP must be installed if your protected application with 
Hardware Dog is running at Windows NT/2K/XP/Server 2003 system. 

This version MicroDogInstdrv.exe call DriverDialog of RCMicroDogsetup.dll to display the interface 
of installing and uninstalling driver. User can use MicroDogInstdrv.exe to install and uninstall 
drivers or make the interface by yourself.  

Run installing program MicroDogInstdrv.exe to install the device drivers and MicroDogInstdrv.exe can 
discern the type of your system, and install corresponding device driver to the system. Please follow
the prompts to complete the installing process.Note: On Windows Vista/WIndows 7/Windows server 2008, 
right click on the installing program and on the popup menu select "Run as administrator" to run the program.

You,the software developer,should not only install the device driver in your system, but also install 
the driver in your end user's system. Therefore you should include MicroDogInstdrv.exe and RCMicroDogSetup.dll
in your release version and execute installing device driver in your application's SETUP process by using 
MicroDogInstdrv.exe directly or refer to the following Appendix A.

If you want to uninstall the drivers of MicorDog,please run the MicroDogInstdrv.exe and click the UninstallDriver 
button.This tool can identify the operating system automatically and uninstall the corresponding drivers. 

The program of MicroDogInstdrv.exe supports the command line parameter,the parameters as follow:
Command line parameter:(Not distinguish capital or lowercase.)
/?display command line help
/i Not display the installation interface
/r Not display uninstall interface
/a Install both USBDog driver and Parallel Dog driver
/u Install or Uninstall USBDog driver only(it should be used with /i or /r)
/p Install or Uninstall Parallel Dog driver only(it should be used with /i or /r)
/s Not display any installation or uninstall information 
For example: MicroDogInstdrv /i/s Not display any information in the process of installation.

-----------
FileList:
-----------
readme-eng.txt			this document
MicroDogInstdrv.exe		The program of driver's installation or uninstallation
RCMicroDogsetup.dll		The dynamic link library of driver's installation
delphi		<dir>		The sample of calling RCMicroDogsetup.dll with Delphi
installshield	<dir>		The sample of calling RCMicroDogsetup.dll with Installshield
pb		<dir>		The sample of calling RCMicroDogsetup.dll with Power Builder
vb		<dir>		The sample of calling RCMicroDogsetup.dll with Visual Basic
vc		<dir>		The sample of calling RCMicroDogsetup.dll with Visual C++

RCMicroDogsetup.dll is a 32-bit Windows DLL, you may use it to develop your own SETUP program in
your application's SETUP process. 

If you want to display the imformation of dog's driver, you can call the function GetDogDriverInfo in the Dll.
  int PASCAL GetDogDriverInfo();
  Parameter:none
  Return value:
  		0 No Driver is installed
  		1 The drivers(Parallel and Usb) with same version have already been installed.
  		2 The USB drivers with same version have already been installed.
  		3 The Parallel drivers with same version have already been installed.
  		4 The driver that you will install is an newer version than the one you are presently using.
  		5 The USB driver that you will install is an newer version than the one you are presently using.
  		6 The Parallel driver that you will install is an newer version than the one you are presently using.
  		7 The driver that you will install is a older version than the one you are presently using.
  		8 The USB driver that you will install is a older version than the one you are presently using.
  		9 The Parallel driver that you will install is a older version than the one you are presently using.
             3008 Not administrator

If you want to install the dog's driver, you can call the function InstDriver in the DLL.

  int PASCAL InstDriver(int iFlag);
  Parameter:
  		1 Install USB dog driver
  		2 Install Parallel dog driver
  		3 Install USB and Parallel dog driver
  Return value should be 0 if success, other return values are error codes.


If you want to uninstall the dog's driver,you can call the function UninstallDriver in the Dll.
  int PASCAL UninstallDriver(int iFlag);
  Parameter
  		1 Uninstall USB dog driver
  		2 Uninstall Parallel dog driver
  		3 Uninstall USB and Parallel dog driver
  Return value should be 0 if success, other return values are error codes.
  
  
If you want to display the interface of driver installing and uninstalling, you can call the function 
DriverDialog in the Dll.   
  void PASCAL DriverDialog();
  Parameter:none
  Return value:none

If you want to control whether display the information "If you have plugged in the USB Hardware, 
please pull out it and plug in it again!" or not in Windows 98/ME,you can call the function 
NotifyPullOutAndPlugInUsbDog in the Dll.   
  void PASCAL NotifyPullOutAndPlugInUsbDog(int iFlag)
  Parameter:
	0  Not dispaly the hint information
	1  display the hint information
  Return value:none

There are demo programs in the subdirectory of delphi/insallshield/vb/vc/pb, which show how to 
call RCMicroDogSetup.DLL to install device drivers. You may refer the samples to write your own 
installing program.  

		
----------------------------------------
Appendix B: Error code for RCMicroDogsetup.dll
----------------------------------------
	
 3001	Open driver service failed
 3002	Start driver service failed
 3003   Copy file failed
 3004   Operate registery failed
 3005	Unknown the operating system
 3006   Stop driver failed
 3007   Uninstall driver failed, none of this type driver is installed
 3008	You are not administrator
 3009	Invalid service
 3010   Error in mapping system library function
 3011	Error in loading system library
 3012	No USB dog device is found
 3013	Update USB dog driver failed 
 3014   Remove file failed 
 3015	Another driver installing process is running
 3016	Another driver uninstalling process is running 

