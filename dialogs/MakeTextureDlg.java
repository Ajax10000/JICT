package dialogs;

import globals.Globals;
import globals.Texture;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// This dialog is displayed when the user selects 
// the "Create a Texture Image..." menu item from the Tools menu.
// See method onToolsCreateTextureImage of the MainFrame class.
// To see what it should look like, see Figure D.5 on p 277 of the book.
public class MakeTextureDlg extends JDialog {
	JComboBox<String>	m_textureType;
	JTextField	m_numRows;
	JComboBox<String>	m_imageType;
	JTextField	m_foreColor;
	JTextField	m_numColumns;
	JTextField	m_backColor;
	JTextField	m_textureImageName;
	JTextField	m_textureDirectory;
    JButton     btnOk;
    JButton     btnCancel;
    JButton     btnLocate;

    JLabel lblTextureType;
    JLabel lblImageType;
    JLabel lblImageName;
    JLabel lblRows;
    JLabel lblCols;
    JLabel lblForeColor;
    JLabel lblBackColor;
    JLabel lblDestDir;
/*
class MakeTextureDlg : public CDialog
{
// Construction
public:
	MakeTextureDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(MakeTextureDlg)
	enum { IDD = IDD_MakeTexture };
	CComboBox	m_textureType;
	CEdit	m_numRows;
	CComboBox	m_imageType;
	CEdit	m_foreColor;
	CEdit	m_numColumns;
	CEdit	m_backColor;
	CEdit	m_textureImageName;
	CEdit	m_textureDirectory;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(MakeTextureDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(MakeTextureDlg)
	virtual void OnOK();
	afx_msg void OnLocatedestdir();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
 */

    // This constructor came from MAKETEXTUREDLG.CPP
    // This dialog is displayed when the user selects 
    // the "Create a Texture Image..." menu item from the Tools menu
    public MakeTextureDlg(JFrame pParent, boolean pModal) {
        super(pParent, pModal);

        setTitle("Create a Texture Image");
        createFields();
        createLabels();
        
        // For the dialog, try to follow Figure D.5, p 277 of the book
        // We will arrange the fields and labels in 3 panels, 
        // a top panel for the image name, locate button and destination directory
        // a middle panel for the texture tpe, image type, foreground color and background color
        // a bottom panel for the rows, columns and OK and Cancel buttons
        JPanel topPanel = new JPanel();
        JPanel midPanel = new JPanel();
        JPanel botPanel = new JPanel();
    } // MakeTextureDlg ctor


    private void createFields() {
        // EDITTEXT        IDC_EDITTEXTURE,42,17,194,14,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_EDITTEXTURE, m_textureImageName);
        m_textureImageName = new JTextField();
                
        // EDITTEXT        IDC_EDITDIRECTORY,42,45,194,14,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_EDITDIRECTORY, m_textureDirectory);
        m_textureDirectory = new JTextField(); 

        // COMBOBOX        IDC_TextureType,43,75,66,52,CBS_SIMPLE | WS_VSCROLL | WS_TABSTOP
        // DDX_Control(pDX, IDC_TextureType, m_textureType);
        m_textureType = new JComboBox<String>();

        // COMBOBOX        IDC_ImageType,119,75,58,54,CBS_SIMPLE | WS_VSCROLL | WS_TABSTOP
        // DDX_Control(pDX, IDC_ImageType, m_imageType);
        m_imageType = new JComboBox<String>();

        // DEFPUSHBUTTON  "OK",IDOK,209,130,29,14
        btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });

        // PUSHBUTTON      "Cancel",IDCANCEL,209,146,29,14
        btnCancel = new JButton("Cancel"); 

        // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR,5,45,34,14
        btnLocate = new JButton("Locate");
        btnLocate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir();
            }
        });

        // IDC_Rows,44,140,36,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Rows, m_numRows);
        m_numRows = new JTextField(); 

        // IDC_Columns,101,140,36,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Columns, m_numColumns);
        m_numColumns = new JTextField(); 

        // IDC_Background,189,106,48,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Background, m_backColor);
        m_backColor = new JTextField(); 

        // IDC_Foreground,189,78,48,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Foreground, m_foreColor);
        m_foreColor = new JTextField(); 
    } // createFields


    private void createLabels() {
        // "Texture Type",IDC_STATIC,44,65,61,10
        lblTextureType = new JLabel("Texure Type"); 

        // "Image Name",IDC_STATIC,43,6,52,11
        lblImageName = new JLabel("Image Name");

        // "Destination Directory (Select a file to choose its directory)", IDC_STATIC2,43,34,181,11
        lblDestDir = new JLabel("Destination Directory (Select a file to choose its directory)"); 

        // LTEXT           "Image Type",IDC_STATIC,120,65,42,10
        lblImageType = new JLabel("Image Type"); 

        // LTEXT           "Rows (Y)",IDC_STATIC,45,130,33,10
        lblRows = new JLabel("Rows (Y)"); 

        // LTEXT           "Columns (X)",IDC_STATIC,102,130,40,10
        lblCols = new JLabel("Columns (X)"); 

        // LTEXT          "Foreground Color",IDC_STATIC,189,67,61,10
        lblForeColor = new JLabel("Foreground Color"); 

        // LTEXT          "Background Color",IDC_STATIC,189,95,61,10
        lblBackColor = new JLabel("Background Color"); 
    } // createLabels

    
    // This method came from MAKETEXTUREDLG.CPP
    void onOK() {
        String texturePath, destinationDir;
        String aDirectory;

        // The texture type  and image type is assumed to be one more than the  
        // order in which its name appears in the combo box.
        int textureType = m_textureType.getSelectedIndex() + 1; 
        int imageType   = m_imageType.getSelectedIndex() + 1;

        texturePath = m_textureImageName.getText();
        destinationDir = m_textureDirectory.getText();

        String ddrive, ddir, dfile,dext;
        String outDirectory;

        _splitpath(destinationDir, ddrive, ddir, dfile, dext);
        _makepath(outDirectory, ddrive, ddir, "", "");

        String msgText = "Generating Texture. Texture Type: " + imageType + " Image Type: " + textureType;
        Globals.statusPrint(msgText); 

        String aForeColor, aBackColor, aNumRows, aNumColumns;
        aForeColor  = m_foreColor.getText(); 
        aBackColor  = m_backColor.getText(); 
        aNumRows    = m_numRows.getText(); 
        aNumColumns = m_numColumns.getText();

        int foreColor  = Integer.parseInt(aForeColor); 
        int backColor  = Integer.parseInt(aBackColor); 
        int numRows    = Integer.parseInt(aNumRows); 
        int numColumns = Integer.parseInt(aNumColumns);

        int aStatus = Texture.createTexture(texturePath, outDirectory, 
            textureType, imageType, 
            foreColor, backColor, 
            numRows, numColumns);
        if(aStatus == -1) {
            Globals.statusPrint("Texture could not be created. See log for more information.");	
        }

        CDialog.OnOK();
    } // onOK


    // This method came from MAKETEXTUREDLG.CPP
    void onLocateDestDir() {
        // TODO: Replace with JFileChooser, add a FileFilter
        // CFileDialog dlg = new CFileDialog(true, "*", "*.*");	//find a destination directory
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int showDlgResult = dlg.showDialog(null, "Select destination directory");
        
        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            File file = dlg.getSelectedFile();
            String pathName = file.getName();
            m_textureDirectory.setText(pathName);
        }
    } // onLocateDestDir
} // class MakeTextureDlg