// demonew.h : main header file for the DEMONEW application
//

#if !defined(AFX_DEMONEW_H__6E98FA13_CD47_431E_8C4D_219E48CB1C5F__INCLUDED_)
#define AFX_DEMONEW_H__6E98FA13_CD47_431E_8C4D_219E48CB1C5F__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CDemonewApp:
// See demonew.cpp for the implementation of this class
//

class CDemonewApp : public CWinApp
{
public:
	CDemonewApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CDemonewApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CDemonewApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_DEMONEW_H__6E98FA13_CD47_431E_8C4D_219E48CB1C5F__INCLUDED_)
