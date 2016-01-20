// goldcarddlltest.h : main header file for the GOLDCARDDLLTEST application
//

#if !defined(AFX_GOLDCARDDLLTEST_H__B7FBDF89_5E31_4255_849F_1096EDC058CA__INCLUDED_)
#define AFX_GOLDCARDDLLTEST_H__B7FBDF89_5E31_4255_849F_1096EDC058CA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CGoldcarddlltestApp:
// See goldcarddlltest.cpp for the implementation of this class
//

class CGoldcarddlltestApp : public CWinApp
{
public:
	CGoldcarddlltestApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CGoldcarddlltestApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CGoldcarddlltestApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_GOLDCARDDLLTEST_H__B7FBDF89_5E31_4255_849F_1096EDC058CA__INCLUDED_)
