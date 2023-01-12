package dialogs;

import globals.Globals;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

// This dialog is displayed when the user selects the 
// "Motion Blur..." menu item from the Tools menu.
// See method onToolsMotionBlur of the MainFrame class.
// To see what it should look like, see Figure D.10 on p 282 of the book.
public class MotionBlurDlg extends JDialog {
    JTextField	m_firstImage;
	JButton	    m_locateOutDirectory;
	JButton	    m_locateInDirectory;
	JTextField	m_outDirectory;
	JTextField	m_NumBlurFrames;
	JTextField	m_BlurDepth;

/*
class MotionBlurDialog : public CDialog
{
// Construction
public:
	MotionBlurDialog(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(MotionBlurDialog)
	enum { IDD = IDD_MotionBlur };
	CEdit	m_firstImage;
	CButton	m_locateOutDirectory;
	CButton	m_locateInDirectory;
	CEdit	m_outDirectory;
	CEdit	m_NumBlurFrames;
	CEdit	m_BlurDepth;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(MotionBlurDialog)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(MotionBlurDialog)
	afx_msg void OnLocatedestdir();
	afx_msg void OnLocatedestdir2();
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
 */

    // This constructor came from MOTIONBLURDIALOG.CPP
    public MotionBlurDlg(JFrame pParent, boolean pModal) {
        super(pParent, pModal);
        //{{AFX_DATA_INIT(MotionBlurDialog)
        //}}AFX_DATA_INIT
    } // MotionBlurDlg ctor


    // This method came from MOTIONBLURDIALOG.CPP
    void DoDataExchange(CDataExchange pDX) {
        CDialog.DoDataExchange(pDX);
        //{{AFX_DATA_MAP(MotionBlurDialog)
        DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
        DDX_Control(pDX, IDC_LOCATEDESTDIR2, m_locateOutDirectory);
        DDX_Control(pDX, IDC_LOCATEDESTDIR, m_locateInDirectory);
        DDX_Control(pDX, IDC_EDITDIRECTORY, m_outDirectory);
        DDX_Control(pDX, IDC_NumBlurFrames, m_NumBlurFrames);
        DDX_Control(pDX, IDC_BlurDepth, m_BlurDepth);
        //}}AFX_DATA_MAP
    }

    /*
    BEGIN_MESSAGE_MAP(MotionBlurDlg, CDialog)
        //{{AFX_MSG_MAP(MotionBlurDialog)
        ON_BN_CLICKED(IDC_LOCATEDESTDIR, OnLocatedestdir)
        ON_BN_CLICKED(IDC_LOCATEDESTDIR2, OnLocatedestdir2)
        //}}AFX_MSG_MAP
    END_MESSAGE_MAP()
    */

    /////////////////////////////////////////////////////////////////////////////
    // MotionBlurDialog message handlers

    // This method came from MOTIONBLURDIALOG.CPP
    void onLocateDestDir() {
        // TODO: Replace with JFileChooser
        // CFileDialog dlg = new CFileDialog(true, "bmp", "*.bmp");	//find a bmp file
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int showDlgResult = dlg.showDialog(null, "Select bmp file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            m_firstImage.setText(dlg.getSelectedFile().getName());	
        }
    } // onLocateDestDir


    // This method came from MOTIONBLURDIALOG.CPP
    void OnLocateDestDir2() {
        // TODO: Replace with JFileChooser
        // CFileDialog dlg = new CFileDialog(true, "bmp", "*.bmp");	//find a bmp file
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int showDlgResult = dlg.showDialog(null, "Select bmp file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            m_outDirectory.setText(dlg.getSelectedFile().getName());
        }
    } // OnLocateDestDir2


    // This method came from MOTIONBLURDIALOG.CPP
    void onOK() {
        String aFirstImage,aOutDir, aBlurDepth, aNumBlurFrames;
        int blurDepth, numBlurFrames;

        aFirstImage = m_firstImage.getText();
        aOutDir = m_outDirectory.getText();

        aNumBlurFrames = m_NumBlurFrames.getText();
        numBlurFrames = Integer.parseInt(aNumBlurFrames);

        aBlurDepth = m_BlurDepth.getText();
        blurDepth = Integer.parseInt(aBlurDepth);
        
        String msgText = "numBlurFrames " + numBlurFrames + " blurDepth " + blurDepth;
        Globals.statusPrint(msgText);

        // Blur a sequence of images
        int aStatus;
        aStatus = Globals.motionBlur(aFirstImage, aOutDir, numBlurFrames, blurDepth);
        msgText = "motion blur Complete. Status: " + aStatus;
        Globals.statusPrint(msgText);	
        CDialog.OnOK();
    } // onOK
} // class MotionBlurDlg