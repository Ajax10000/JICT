package dialogs;

import globals.Globals;
import globals.Texture;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

// This dialog is displayed when the user selects 
// the "Create a Texture Image..." menu item from the Tools menu.
// See method onToolsCreateTextureImage of the MainFrame class.
// To see what it should look like, see Figure D.5 on p 277 of the book.
public class MakeTextureDlg extends JDialog {
	JComboBox<String>	m_textureType;
    private JTextField  mSelectedTextureType;
	private JTextField	m_numRows;
	JComboBox<String>	m_imageType;
    private JTextField  mSelectedImageType;
	private JTextField	m_foreColor;
	private JTextField	m_numColumns;
	private JTextField	m_backColor;
	private JTextField	m_textureImageName;
	private JTextField	m_textureDirectory;
    private JButton     btnOk;
    private JButton     btnCancel;
    private JButton     btnLocate;

    private JLabel lblTextureType;
    private JLabel lblImageType;
    private JLabel lblImageName;
    private JLabel lblRows;
    private JLabel lblCols;
    private JLabel lblForeColor;
    private JLabel lblBackColor;
    private JLabel lblDestDir;

    private int iDlgWidth = 520;
    private int iDlgHeight = 300;
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
        setSize(new Dimension(iDlgWidth, iDlgHeight));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();

        // For the dialog, try to follow Figure D.5, p 277 of the book
        // We will arrange the fields and labels in 3 sections, 
        // a top section for the image name, locate button and destination directory
        // a middle section for the texture type, image type, foreground color and background color
        // a bottom section for the rows, columns and OK and Cancel buttons
        Box topBox = addTopSection();
        Box midBox = addMidSection();
        Box botBox = addBotSection();

        row01Box.add(topBox);
        row02Box.add(midBox);
        row03Box.add(botBox);

        panel.add(row01Box);
        panel.add(row02Box);
        panel.add(row03Box);

        this.add(panel);

        setVisible(true);
    } // MakeTextureDlg ctor


    // Called from:
    //     constructor
    private Box addTopSection() {
        Dimension shortSpacerSize = new Dimension(90, 25);
        Dimension txtFieldSize = new Dimension(120, 25);
        Dimension locateBtnSize = new Dimension(90, 25);

        Box vertBox = Box.createVerticalBox();
        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();
        Box row04Box = Box.createHorizontalBox();

        // Create components for row01Box
        Component shortSpacer01 = Box.createRigidArea(shortSpacerSize);
        // "Image Name",IDC_STATIC,43,6,52,11
        lblImageName = new JLabel("Image Name");

        // Populate row01Box
        row01Box.add(shortSpacer01);
        row01Box.add(lblImageName);

        // Create components for row02Box
        Component shortSpacer02 = Box.createRigidArea(shortSpacerSize);

        // EDITTEXT        IDC_EDITTEXTURE,42,17,194,14,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_EDITTEXTURE, m_textureImageName);
        m_textureImageName = new JTextField();
        m_textureImageName.setSize(txtFieldSize);
        m_textureImageName.setPreferredSize(txtFieldSize);

        // Populate row02Box
        row02Box.add(shortSpacer02);
        row02Box.add(m_textureImageName);

        // Create components for row03Box
        Component shortSpacer03 = Box.createRigidArea(shortSpacerSize);

        // "Destination Directory (Select a file to choose its directory)", IDC_STATIC2,43,34,181,11
        lblDestDir = new JLabel("Destination Directory (Select a file to choose its directory)"); 

        // Populate row03Box
        row03Box.add(shortSpacer03);
        row03Box.add(lblDestDir);

        // Create components for row04Box
        // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR,5,45,34,14
        btnLocate = new JButton("Locate");
        btnLocate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir();
            }
        });
        btnLocate.setSize(locateBtnSize);
        btnLocate.setPreferredSize(locateBtnSize);

        m_textureDirectory = new JTextField();
        m_textureDirectory.setSize(txtFieldSize);
        m_textureDirectory.setPreferredSize(txtFieldSize);

        // Populate row04Box
        row04Box.add(btnLocate);
        row04Box.add(m_textureDirectory);

        // Populate vertBox
        vertBox.add(row01Box);
        vertBox.add(row02Box);
        vertBox.add(row03Box);
        vertBox.add(row04Box);

        return vertBox;
    } // addTopPanel


    // Called from:
    //     constructor
    public Box addMidSection() {
        Dimension lblSize = new Dimension(90, 25);
        Dimension shortTxtFieldSize = new Dimension(90, 25);
        Dimension cboFieldSize = new Dimension(90, 25);

        Box horizBox = Box.createHorizontalBox();
        Box midVertBox01 = Box.createVerticalBox();
        Box midVertBox02 = Box.createVerticalBox();
        Box midVertBox03a = Box.createVerticalBox();
        Box midVertBox03 = Box.createVerticalBox();
        Box midVertBox04a = Box.createVerticalBox();
        Box midVertBox04 = Box.createVerticalBox();

        // Create components for midVertBox01
        Component spacer01 = Box.createRigidArea(new Dimension(90, 90));
        spacer01.setBackground(Color.GREEN);

        // Populate midVertBox01
        midVertBox01.add(spacer01);

        // Create components for midVertBox02
        // "Texture Type",IDC_STATIC,44,65,61,10
        lblTextureType = new JLabel("Texture Type"); 
        lblTextureType.setSize(lblSize);
        lblTextureType.setMinimumSize(lblSize);
        lblTextureType.setMaximumSize(lblSize);
        lblTextureType.setPreferredSize(lblSize);
        lblTextureType.setAlignmentX(SwingConstants.LEFT);
        
        mSelectedTextureType = new JTextField(); 
        mSelectedTextureType.setSize(shortTxtFieldSize);
        mSelectedTextureType.setPreferredSize(shortTxtFieldSize);

        // COMBOBOX        IDC_TextureType,43,75,66,52,CBS_SIMPLE | WS_VSCROLL | WS_TABSTOP
        // DDX_Control(pDX, IDC_TextureType, m_textureType);
        m_textureType = new JComboBox<String>();
        m_textureType.setSize(cboFieldSize);
        m_textureType.setPreferredSize(cboFieldSize);
        m_textureType.addItem("Constant");
        m_textureType.addItem("Checkerboard");
        m_textureType.addItem("Horizontal Ramp");
        m_textureType.addItem("Vertical Ramp");
        m_textureType.addItem("Plasma");

        // Populate midVertBox02
        midVertBox02.add(lblTextureType);
        midVertBox02.add(mSelectedTextureType);
        midVertBox02.add(m_textureType);

        Component spacer02 = Box.createRigidArea(new Dimension(10, 90));
        midVertBox03a.add(spacer02);

        // Create components for midVertBox03
        // LTEXT           "Image Type",IDC_STATIC,120,65,42,10
        lblImageType = new JLabel("Image Type"); 
        lblImageType.setSize(lblSize);
        lblImageType.setPreferredSize(lblSize);
        lblImageType.setAlignmentX(SwingConstants.LEFT);

        mSelectedImageType = new JTextField();
        mSelectedImageType.setSize(shortTxtFieldSize);
        mSelectedImageType.setPreferredSize(shortTxtFieldSize);

        // COMBOBOX        IDC_ImageType,119,75,58,54,CBS_SIMPLE | WS_VSCROLL | WS_TABSTOP
        // DDX_Control(pDX, IDC_ImageType, m_imageType);
        m_imageType = new JComboBox<String>();
        m_imageType.setSize(cboFieldSize);
        m_imageType.setPreferredSize(cboFieldSize);
        m_imageType.addItem("Monochrome");
        m_imageType.addItem("RGBColor");
        m_imageType.addItem("Floating Point");

        // Populate midVertBox03
        midVertBox03.add(lblImageType);
        midVertBox03.add(mSelectedImageType);
        midVertBox03.add(m_imageType);

        Component spacer03 = Box.createRigidArea(new Dimension(10, 90));
        midVertBox04a.add(spacer03);

        // Create components for midVertBox04
        // LTEXT          "Foreground Color",IDC_STATIC,189,67,61,10
        lblForeColor = new JLabel("Foreground Color"); 
        lblForeColor.setSize(lblSize);
        lblForeColor.setPreferredSize(lblSize);

        // IDC_Foreground,189,78,48,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Foreground, m_foreColor);
        m_foreColor = new JTextField(); 
        m_foreColor.setSize(shortTxtFieldSize);
        m_foreColor.setPreferredSize(shortTxtFieldSize);

        // LTEXT          "Background Color",IDC_STATIC,189,95,61,10
        lblBackColor = new JLabel("Background Color"); 
        lblBackColor.setSize(lblSize);
        lblBackColor.setPreferredSize(lblSize);

        // IDC_Background,189,106,48,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Background, m_backColor);
        m_backColor = new JTextField(); 
        m_backColor.setSize(shortTxtFieldSize);
        m_backColor.setPreferredSize(shortTxtFieldSize);

        // Populate midVertBox04
        midVertBox04.add(lblForeColor);
        midVertBox04.add(m_foreColor);
        midVertBox04.add(lblBackColor);
        midVertBox04.add(m_backColor);

        horizBox.add(midVertBox01);
        horizBox.add(midVertBox02);
        horizBox.add(midVertBox03a);
        horizBox.add(midVertBox03);
        horizBox.add(midVertBox04a);
        horizBox.add(midVertBox04);

        return horizBox;
    } // addMiddlePanel


    // Called from:
    //     constructor
    private Box addBotSection() {
        Dimension btnSize = new Dimension(73, 25);
        Dimension btnVertSpacerSize = new Dimension(73, 5);

        Box botHorizBox = Box.createHorizontalBox();
        Box botVertBox01 = Box.createVerticalBox();
        Box botVertBox02 = Box.createVerticalBox();
        Box botVertBox03 = Box.createVerticalBox();
        Box botVertBox04 = Box.createVerticalBox();
        Box botVertBox05 = Box.createVerticalBox();
        Box botVertBox06 = Box.createVerticalBox();

        // Create components for botVertBox01
        Component spacer01 = Box.createRigidArea(new Dimension(90, 50));

        // Populate botVertBox01
        botVertBox01.add(spacer01);

        // Create components for botVertBox02
        // LTEXT           "Rows (Y)",IDC_STATIC,45,130,33,10
        lblRows = new JLabel("Rows (Y)"); 
        lblRows.setSize(new Dimension(35, 25));
        lblRows.setPreferredSize(new Dimension(35, 25));
        lblRows.setAlignmentX(SwingConstants.LEFT);

        // IDC_Rows,44,140,36,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Rows, m_numRows);
        m_numRows = new JTextField(); 

        // Populate botVertBox02
        botVertBox02.add(lblRows);
        botVertBox02.add(m_numRows);

        // Create components for botVertBox03
        Component spacer02 = Box.createRigidArea(new Dimension(30, 50));

        // Populate botVertBox03
        botVertBox03.add(spacer02);

        // Create components for botVertBox04
        // LTEXT           "Columns (X)",IDC_STATIC,102,130,40,10
        lblCols = new JLabel("Columns (X)"); 
        lblCols.setSize(new Dimension(35, 25));
        lblCols.setPreferredSize(new Dimension(35, 25));
        lblCols.setAlignmentX(SwingConstants.LEFT);

        // IDC_Columns,101,140,36,13,ES_AUTOHSCROLL
        // DDX_Control(pDX, IDC_Columns, m_numColumns);
        m_numColumns = new JTextField(); 

        // Populate botVertBox04
        botVertBox04.add(lblCols);
        botVertBox04.add(m_numColumns);

        // Create component for botVertBox05
        Component spacer03 = Box.createRigidArea(new Dimension(90, 50));
        
        // Populate botVertBox05
        botVertBox05.add(spacer03);

        // Create components for botVertBox06
        // DEFPUSHBUTTON  "OK",IDOK,209,130,29,14
        btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });
        btnOk.setSize(btnSize);
        btnOk.setMinimumSize(btnSize);
        btnOk.setMaximumSize(btnSize);
        btnOk.setPreferredSize(btnSize);
        btnOk.setAlignmentX(SwingConstants.CENTER);

        Component vertBtnSpacer = Box.createRigidArea(btnVertSpacerSize);

        // PUSHBUTTON      "Cancel",IDCANCEL,209,146,29,14
        btnCancel = new JButton("Cancel"); 
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onCancel();
            }
        });
        btnCancel.setSize(btnSize);
        btnCancel.setMinimumSize(btnSize);
        btnCancel.setMaximumSize(btnSize);
        btnCancel.setPreferredSize(btnSize);
        btnCancel.setAlignmentX(SwingConstants.CENTER);

        // Populate botVertBox06
        botVertBox06.add(btnOk);
        botVertBox06.add(vertBtnSpacer);
        botVertBox06.add(btnCancel);

        // Populate botHorizBox
        botHorizBox.add(botVertBox01);
        botHorizBox.add(botVertBox02);
        botHorizBox.add(botVertBox03);
        botHorizBox.add(botVertBox04);
        botHorizBox.add(botVertBox05);
        botHorizBox.add(botVertBox06);

        return botHorizBox;
    } // addBotPanel

    
    // This method originally came from MAKETEXTUREDLG.CPP
    //
    // Called when the user clicks on the OK button.
    void onOK() {
        String sTexturePath, sDestinationDir;
        // String aDirectory; // not used

        // The texture type  and image type is assumed to be one more than the  
        // order in which its name appears in the combo box.
        int iTextureType = m_textureType.getSelectedIndex() + 1; 
        int iImageType   = m_imageType.getSelectedIndex() + 1;

        sTexturePath = m_textureImageName.getText();
        sDestinationDir = m_textureDirectory.getText();

        // String ddrive, ddir, dfile, dext; // These variables are no longer used
        String sOutDirectory = "";

        // Set outDirectory. It will later be used as a parameter to Texture.createTexture
        // TODO: Replace the following 2 statements
        //_splitpath(destinationDir, ddrive, ddir, dfile, dext);
        //_makepath(outDirectory, ddrive, ddir, "", "");

        String sMsgText = "Generating Texture. Texture Type: " + iTextureType + " Image Type: " + iImageType;
        Globals.statusPrint(sMsgText); 

        String sForeColor, sBackColor, sNumRows, sNumColumns;
        sForeColor  = m_foreColor.getText(); 
        sBackColor  = m_backColor.getText(); 
        sNumRows    = m_numRows.getText(); 
        sNumColumns = m_numColumns.getText();

        Integer iParsedInteger = 0;
        int iForeColor  = 0; 
        if (getIntegerValue(sForeColor, iParsedInteger) == null) {
            iForeColor = iParsedInteger.intValue();
        } else {
            JOptionPane.showMessageDialog(this, "The value entered in Foreground Color is not a valid integer.");
            m_foreColor.requestFocusInWindow();
            return;
        }

        int iBackColor  = 0; 
        if (getIntegerValue(sBackColor, iParsedInteger) == null) {
            iBackColor = iParsedInteger.intValue();
        } else {
            JOptionPane.showMessageDialog(this, "The value entered in Background Color is not a valid integer.");
            m_backColor.requestFocusInWindow();
            return;
        }

        int iNumRows    = 0; 
        if (getIntegerValue(sNumRows, iParsedInteger) == null) {
            iNumRows = iParsedInteger.intValue();
        } else {
            JOptionPane.showMessageDialog(this, "The value entered in Rows is not a valid integer.");
            m_numRows.requestFocusInWindow();
            return;
        }

        int iNumColumns = 0;
        if (getIntegerValue(sNumColumns, iParsedInteger) == null) {
            iNumColumns = iParsedInteger.intValue();
        } else {
            JOptionPane.showMessageDialog(this, "The value entered in Columns is not a valid integer.");
            m_numColumns.requestFocusInWindow();
            return;
        }

        int iStatus = Texture.createTexture(sTexturePath, sOutDirectory, 
            iTextureType, iImageType, 
            iForeColor, iBackColor, 
            iNumRows, iNumColumns);

        if(iStatus == -1) {
            Globals.statusPrint("Texture could not be created. See log for more information.");	
        }
    } // onOK


    // Called from:
    //     onOK
    public NumberFormatException getIntegerValue(String psIntAsString, Integer pIParsedInt) {
        try {
            pIParsedInt = Integer.parseInt(psIntAsString);
            return null;
        } catch (NumberFormatException nfe) {
            return nfe;
        }
    }


    // Called when the user clicks on the Cancel button.
    public void onCancel() {
        this.dispose();
    }


    // This method originally came from MAKETEXTUREDLG.CPP
    //
    // Called when the user clicks on the Locate button
    void onLocateDestDir() {
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