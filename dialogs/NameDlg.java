package dialogs;

import javax.swing.JDialog;
import javax.swing.JFrame;

// This dialog is displayed when the user double-clicks the left mouse button on an ImageView.
// See method onLButtonDblClk of the ImageView class.
public class NameDlg extends JDialog {
    String	m_Name;

/* 
class CNameDialog : public CDialog
{
// Construction
public:
	CNameDialog(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CNameDialog)
	enum { IDD = IDD_NAMEDIALOG };
	CString	m_Name;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CNameDialog)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CNameDialog)
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
*/

    // This constructor came from NAMEDLG.CPP
    // I added the modal parameter
    public NameDlg(JFrame pParent, boolean pModal) {
        super(pParent, pModal);
        m_Name = "";
    } // NameDlg ctor


    // This method came from NAMEDLG.CPP
    void DoDataExchange(CDataExchange pDX) {
        CDialog.DoDataExchange(pDX);

        //{{AFX_DATA_MAP(CNameDialog)
        DDX_Text(pDX, IDC_Name, m_Name);
        DDV_MaxChars(pDX, m_Name, 31);
        //}}AFX_DATA_MAP
    }


    /////////////////////////////////////////////////////////////////////////////
    // CNameDialog message handlers

    // This method came from NAMEDLG.CPP
    void onOK() {
        // TODO: Add extra validation here
        
        CDialog.OnOK();
    } // onOK
} // class NameDlg