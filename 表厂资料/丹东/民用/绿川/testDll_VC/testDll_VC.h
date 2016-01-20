// testDll_VC.h : main header file for the TESTDLL_VC application
//

#if !defined(AFX_TESTDLL_VC_H__EC338407_0ADE_48FE_BB5D_F41F96795C89__INCLUDED_)
#define AFX_TESTDLL_VC_H__EC338407_0ADE_48FE_BB5D_F41F96795C89__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CTestDll_VCApp:
// See testDll_VC.cpp for the implementation of this class
//

class CTestDll_VCApp : public CWinApp
{
public:
	CTestDll_VCApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CTestDll_VCApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CTestDll_VCApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_TESTDLL_VC_H__EC338407_0ADE_48FE_BB5D_F41F96795C89__INCLUDED_)
