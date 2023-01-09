package dialogs;

import globals.Globals;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class QuadMeshDlg extends JDialog {
	JComboBox<String>	m_QMeshType;
	JTextField	m_TextureImage;
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
    public QuadMeshDlg(JFrame pParent /*=NULL*/, boolean pModal) {
        super(pParent, pModal);
        //{{AFX_DATA_INIT(CQuadMeshDlg)
        //}}AFX_DATA_INIT
    } // QuadMeshDlg ctor

    
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


    // This method came from QUADMESHDLG.CPP
    void onOK() {
        String texturePath,destinationDir;
        String aTexture, aDirectory;

        // The mesh type is assumed to be one more than the  
        // order in which its name appears in the combo box.
        int meshType = m_QMeshType.getSelectedIndex() + 1; 
        texturePath = m_TextureImage.getText();
        destinationDir = m_ModelDirectory.getText();

        String ddrive, ddir, dfile, dext;
        String outDirectory;

        _splitpath(destinationDir, ddrive, ddir, dfile, dext);
        _makepath(outDirectory, ddrive, ddir, "", "");
        
        Globals.statusPrint("Generating Quad Mesh Model."); 
        String msgText = "Mesh Type: " + meshType + " Model Path: " + texturePath;
        Globals.statusPrint(msgText);
        
        int aStatus = Globals.createQMeshModel(texturePath, outDirectory, meshType);
        if(aStatus == -1) {
            Globals.statusPrint("Quad Mesh Model could not be created. See log for more information.");
        } else {
            msgText = "Models Placed in directory: " + outDirectory;
            Globals.statusPrint(msgText);
        }

        // This method came from QUADMESHDLG.CPP
        CDialog.OnOK();
    } // onOK


    // This method came from QUADMESHDLG.CPP
    void onLocateTexture() {
        // TODO: Replace with JFileChooser
        // CFileDialog dlg = new CFileDialog(true, "bmp", "*.bmp");	//find a bmp file
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = dlg.showDialog(this, "Select BMP file");

        if (result == JFileChooser.APPROVE_OPTION) {
            m_TextureImage.setText(dlg.getSelectedFile().getName());
        }	
    } // onLocateTexture


    // This method came from QUADMESHDLG.CPP
    void onLocateDestDir() {
        // TODO: Replace with JFileChooser
        // CFileDialog dlg = new CFileDialog(true, "*", "*.*");	//find a destination directory
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = dlg.showDialog(this, "Select directory");

        if (result == JFileChooser.APPROVE_OPTION) {
            m_ModelDirectory.setText(dlg.getSelectedFile().getName());	
        }
    } // onLocateDestDir
} // class QuadMeshDlg
