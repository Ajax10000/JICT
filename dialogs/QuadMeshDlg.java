package dialogs;

import fileUtils.BMPFileFilter;

import globals.Globals;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

// This dialog is displayed when the user selects the 
// "Create a Mesh Model..." menu item from the Tools menu.
// See method onToolsCreateMesh of the MainFrame class.
// To see what it should look like, see Figure D.6 on p 278 of 
// Visual Special Effects Toolkit in C++, by Tim Wittenburg
public class QuadMeshDlg extends JDialog {
    // DDX_Control(pDX, IDC_COMBO1, m_QMeshType);
	JComboBox<String>	m_QMeshType;

    // DDX_Control(pDX, IDC_EDITTEXTURE, m_TextureImage);
	JTextField	m_TextureImage;

    // DDX_Control(pDX, IDC_EDITDIRECTORY, m_ModelDirectory);
	JTextField	m_ModelDirectory;

	JButton	    m_Locate;
	JComboBox<String>	m_MeshType;

/*
class CQuadMeshDlg : public CDialog
{
// Construction
public:
	CQuadMeshDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CQuadMeshDlg)
	enum { IDD = IDD_MeshModel };
	CComboBox	m_QMeshType;
	CEdit	m_TextureImage;
	CEdit	m_ModelDirectory;
	CButton	m_Locate;
	CComboBox	m_MeshType;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CQuadMeshDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
 
	// Generated message map functions
	//{{AFX_MSG(CQuadMeshDlg)
	virtual void OnOK();
	afx_msg void OnLocatetexture();
	afx_msg void OnLocatedestdir();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
*/

    // Instantiated by MainFrame.onToolsCreatemesh
    public QuadMeshDlg(JFrame pParent, boolean pbModal) {
        super(pParent, pbModal);
        setTitle("Generate a Quadrilateral Mesh Model");

    } // QuadMeshDlg ctor

    
    /*
    // This method came from QUADMESHDLG.CPP
    void DoDataExchange(CDataExchange pDX)
    {
        CDialog.DoDataExchange(pDX);

        //{{AFX_DATA_MAP(CQuadMeshDlg)
        DDX_Control(pDX, IDC_COMBO1, m_QMeshType);
        DDX_Control(pDX, IDC_EDITTEXTURE, m_TextureImage);
        DDX_Control(pDX, IDC_EDITDIRECTORY, m_ModelDirectory);
        //}}AFX_DATA_MAP
    }
    */

    // This method came from QUADMESHDLG.CPP
    void onOK() {
        String sTexturePath, sDestinationDir;
        String sTexture, sDirectory;

        // The mesh type is assumed to be one more than the  
        // order in which its name appears in the combo box.
        int iMeshType = m_QMeshType.getSelectedIndex() + 1; 
        sTexturePath = m_TextureImage.getText();
        sDestinationDir = m_ModelDirectory.getText();

        String sDdrive, sDdir, sDfile, sDext;
        String sOutDirectory;

        _splitpath(sDestinationDir, sDdrive, sDdir, sDfile, sDext);
        _makepath(sOutDirectory, sDdrive, sDdir, "", "");
        
        Globals.statusPrint("Generating Quad Mesh Model."); 
        String sMsgText = "Mesh Type: " + iMeshType + " Model Path: " + sTexturePath;
        Globals.statusPrint(sMsgText);
        
        int iStatus = Globals.createQMeshModel(sTexturePath, sOutDirectory, iMeshType);
        if(iStatus == -1) {
            Globals.statusPrint("Quad Mesh Model could not be created. See log for more information.");
        } else {
            sMsgText = "Models Placed in directory: " + sOutDirectory;
            Globals.statusPrint(sMsgText);
        }
    } // onOK


    // This method came from QUADMESHDLG.CPP
    void onLocateTexture() {
        // Find a bmp file
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dlg.setFileFilter(new BMPFileFilter());
        int iResult = dlg.showDialog(this, "Select BMP file");

        if (iResult == JFileChooser.APPROVE_OPTION) {
            m_TextureImage.setText(dlg.getSelectedFile().getName());
        }	
    } // onLocateTexture


    // This method came from QUADMESHDLG.CPP
    void onLocateDestDir() {
        // Find a destination directory
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int iResult = dlg.showDialog(this, "Select directory");

        if (iResult == JFileChooser.APPROVE_OPTION) {
            m_ModelDirectory.setText(dlg.getSelectedFile().getName());	
        }
    } // onLocateDestDir
} // class QuadMeshDlg
