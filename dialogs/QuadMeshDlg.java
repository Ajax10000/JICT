package dialogs;

import fileUtils.BMPFileFilter;

import globals.Globals;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

// This dialog is displayed when the user selects the 
// "Create a Mesh Model..." menu item from the Tools menu.
// See method onToolsCreateMesh of the MainFrame class.
// To see what it should look like, see Figure D.6 on p 278 of 
// Visual Special Effects Toolkit in C++, by Tim Wittenburg
public class QuadMeshDlg extends JDialog {
    // DDX_Control(pDX, IDC_COMBO1, m_QMeshType);
	JList<String>	m_QMeshType;

    // DDX_Control(pDX, IDC_EDITTEXTURE, m_TextureImage);
	JTextField	m_TextureImage;

    // DDX_Control(pDX, IDC_EDITDIRECTORY, m_ModelDirectory);
	JTextField	m_ModelDirectory;

	JButton	    mBtnLocateTextureImg;
    JButton     mBtnLocateDestDir;

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
        setSize(530, 270);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();

        Box topBox = addTopSection();
        Box botBox = addBotSection();

        row01Box.add(topBox);
        row02Box.add(botBox);

        panel.add(row01Box);
        panel.add(row02Box);
        this.add(panel);

        setVisible(true);
    } // QuadMeshDlg ctor


    // Called from:
    //     Constructor
    private Box addTopSection() {
        Dimension spacerSize = new Dimension(80, 25);
        Dimension btnSize = new Dimension(80, 25);
        Dimension txtFldSize = new Dimension(400, 25);
        // A btnFldSpacer rigid area will be used to separate each locate button 
        // from its corresponding text field.
        Dimension btnFldSpacerDim = new Dimension(10, 25);

        Box vertBox  = Box.createVerticalBox();
        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();
        Box row04Box = Box.createHorizontalBox();
        Box row05Box = Box.createHorizontalBox();

        // Create components for row01Box
        Component spacer01 = Box.createRigidArea(spacerSize);
        JLabel lblSourceImage = new JLabel("Source Image");

        // Populate row01Box
        row01Box.add(spacer01);
        row01Box.add(lblSourceImage);

        // Create components for row02Box
        // DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
        m_TextureImage = new JTextField(30);
        m_TextureImage.setSize(txtFldSize);
        m_TextureImage.setMaximumSize(txtFldSize);
        m_TextureImage.setPreferredSize(txtFldSize);

        // DDX_Control(pDX, IDC_LOCATEDESTDIR2, m_locateOutDirectory);
        mBtnLocateTextureImg = new JButton("Locate");
        mBtnLocateTextureImg.setSize(btnSize);
        mBtnLocateTextureImg.setPreferredSize(btnSize);
        mBtnLocateTextureImg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateTexture();
            }
        });
        Component btnFldSpacer01 = Box.createRigidArea(btnFldSpacerDim);

        // Populate row02Box
        row02Box.add(mBtnLocateTextureImg);
        row02Box.add(btnFldSpacer01);
        row02Box.add(m_TextureImage);

        // Create components for row03Box
        Component spacer02 = Box.createRigidArea(spacerSize);
        JLabel lblDestDir = new JLabel("Destination Directory (Select a file to choose its directory)");

        // Populate row03Box
        row03Box.add(spacer02);
        row03Box.add(lblDestDir);

        // Create components for row04Box
        // DDX_Control(pDX, IDC_EDITDIRECTORY, m_outDirectory);
        m_ModelDirectory = new JTextField(30);
        m_ModelDirectory.setSize(txtFldSize);
        m_ModelDirectory.setMaximumSize(txtFldSize);
        m_ModelDirectory.setPreferredSize(txtFldSize);

        // DDX_Control(pDX, IDC_LOCATEDESTDIR, m_locateInDirectory);
        mBtnLocateDestDir = new JButton("Locate");
        mBtnLocateDestDir.setSize(btnSize);
        mBtnLocateDestDir.setPreferredSize(btnSize);
        mBtnLocateDestDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir();
            }
        });
        Component btnFldSpacer02 = Box.createRigidArea(btnFldSpacerDim);

        // Populate row04Box
        row04Box.add(mBtnLocateDestDir);
        row04Box.add(btnFldSpacer02);
        row04Box.add(m_ModelDirectory);

        // Create component for row05Box
        Component panelSpacer = Box.createRigidArea(new Dimension(500, 10));
        
        // Populate row05Box
        row05Box.add(panelSpacer);

        // Populate vertBox
        vertBox.add(row01Box);
        vertBox.add(row02Box);
        vertBox.add(row03Box);
        vertBox.add(row04Box);
        vertBox.add(row05Box);

        return vertBox;
    } // addTopSection


    // Called from:
    //     Constructor
    private Box addBotSection() {
        Dimension btnSize = new Dimension(80, 25);

        Box horizBox = Box.createHorizontalBox();
        Box vertBox01 = Box.createVerticalBox();
        Box vertBox02 = Box.createVerticalBox();
        Box vertBox03 = Box.createVerticalBox();
        Box vertBox04 = Box.createVerticalBox();

        // Create component for vertBox01
        Component rectSpacer01 = Box.createRigidArea(new Dimension(70, 80));

        // Populate vertBox01
        vertBox01.add(rectSpacer01);

        // Create components for vertBox02
        JLabel lblMeshModelType = new JLabel("Mesh Model Type");
        Dimension lblSize = new Dimension(100, 25);
        lblMeshModelType.setSize(lblSize);
        lblMeshModelType.setPreferredSize(lblSize);
        lblMeshModelType.setAlignmentX(SwingConstants.LEFT);

        m_QMeshType = new JList<String>();
        String[] listData = {
            "Sphere",
            "Planar",
            "Sine1D", 
            "Sine2D",
            "Checkerboard",
            "White Noise"
        };
        m_QMeshType.setListData(listData);
        Dimension scrollPaneSize = new Dimension(120, 80);
        JScrollPane jScrlPane = new JScrollPane(m_QMeshType);
        jScrlPane.setSize(scrollPaneSize);
        jScrlPane.setMaximumSize(scrollPaneSize);
        jScrlPane.setPreferredSize(scrollPaneSize);
        jScrlPane.setAlignmentX(SwingConstants.LEFT);

        // Populate vertBox02
        vertBox02.add(lblMeshModelType);
        vertBox02.add(jScrlPane);

        // Create component for vertBox03
        Component rectSpacer02 = Box.createRigidArea(new Dimension(125, 80));

        // Populate vertBox03
        vertBox03.add(rectSpacer02);

        // Create components for vertBox04
        Component topSpacer = Box.createRigidArea(new Dimension(90, 25));

        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });
        btnOK.setSize(btnSize);
        btnOK.setMinimumSize(btnSize);
        btnOK.setMaximumSize(btnSize);
        btnOK.setPreferredSize(btnSize);
        
        Component btnSpacer = Box.createRigidArea(new Dimension(90, 5));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onCancel();
            }
        });
        btnCancel.setSize(btnSize);
        btnCancel.setMinimumSize(btnSize);
        btnCancel.setMaximumSize(btnSize);
        btnCancel.setPreferredSize(btnSize);

        // Populate vertBox04
        vertBox04.add(topSpacer);
        vertBox04.add(btnOK);
        vertBox04.add(btnSpacer);
        vertBox04.add(btnCancel);

        //Populate horizBox
        horizBox.add(vertBox01);
        horizBox.add(vertBox02);
        horizBox.add(vertBox03);
        horizBox.add(vertBox04);

        return horizBox;
    } // addBotSection
    
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
        String sOutDirectory = "";

        // TODO: Replace the 2 following statements
        //_splitpath(sDestinationDir, sDdrive, sDdir, sDfile, sDext);
        //_makepath(sOutDirectory, sDdrive, sDdir, "", "");
        
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


    // Called when the Cancel button is clicked
    public void onCancel() {
        this.dispose();
    }


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
