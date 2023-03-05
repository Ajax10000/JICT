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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// This dialog is displayed when the user selects the 
// "Motion Blur..." menu item from the Tools menu.
// See method onToolsMotionBlur of the MainFrame class.
// To see what it should look like, see Figure D.10 on p 282 of the book
// Visual Special Effects Toolkit in C++, by Tim Wittenburg
public class MotionBlurDlg extends JDialog {
    // DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
    JTextField	m_firstImage;

    // DDX_Control(pDX, IDC_LOCATEDESTDIR2, m_locateOutDirectory);
	JButton	    m_locateOutDirectory;

    // DDX_Control(pDX, IDC_LOCATEDESTDIR, m_locateInDirectory);
	JButton	    m_locateInDirectory;

    // DDX_Control(pDX, IDC_EDITDIRECTORY, m_outDirectory);
	JTextField	m_outDirectory;

    // DDX_Control(pDX, IDC_NumBlurFrames, m_NumBlurFrames);
	JTextField	m_NumBlurFrames;

    // DDX_Control(pDX, IDC_BlurDepth, m_BlurDepth);
	JTextField	m_BlurDepth;

    private JButton btnOK;
    private JButton btnCancel;

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
        setTitle("Motion Blur");
        setSize(500, 250);
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();

        Box topBox = addTopSection();
        row01Box.add(topBox);

        Box botBox = addBottomSection();
        row02Box.add(botBox);

        panel.add(row01Box);
        panel.add(row02Box);
        this.add(panel);
        setVisible(true);
    } // MotionBlurDlg ctor


    // Called from:
    //     constructor
    private Box addTopSection() {
        Dimension spacerDim = new Dimension(80, 25);
        Dimension btnDim = new Dimension(80, 25);
        Dimension btnFldSpacerDim = new Dimension(10, 25);

        Box vertBox  = Box.createVerticalBox();
        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();
        Box row04Box = Box.createHorizontalBox();
        Box row05Box = Box.createHorizontalBox();

        // Create components for row01Box
        Component spacer01 = Box.createRigidArea(spacerDim);
        JLabel lblFirstImg = new JLabel("First Image to Blur");

        // Populate row01Box
        row01Box.add(spacer01);
        row01Box.add(lblFirstImg);

        // Create components for row02Box
        // DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
        m_firstImage = new JTextField(30);

        // DDX_Control(pDX, IDC_LOCATEDESTDIR2, m_locateOutDirectory);
        m_locateOutDirectory = new JButton("Locate");
        m_locateOutDirectory.setPreferredSize(btnDim);
        m_locateOutDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir();
            }
        });
        Component btnFldSpacer01 = Box.createRigidArea(btnFldSpacerDim);

        // Populate row02Box
        row02Box.add(m_locateOutDirectory);
        row02Box.add(btnFldSpacer01);
        row02Box.add(m_firstImage);

        // Create components for row03Box
        Component spacer02 = Box.createRigidArea(spacerDim);
        JLabel lblDestDir = new JLabel("Destination Directory (Select a file to choose its directory)");

        // Populate row03Box
        row03Box.add(spacer02);
        row03Box.add(lblDestDir);

        // Create components for row04Box
        // DDX_Control(pDX, IDC_EDITDIRECTORY, m_outDirectory);
        m_outDirectory = new JTextField(30);

        // DDX_Control(pDX, IDC_LOCATEDESTDIR, m_locateInDirectory);
        m_locateInDirectory = new JButton("Locate");
        m_locateInDirectory.setPreferredSize(btnDim);
        m_locateInDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir2();
            }
        });
        Component btnFldSpacer02 = Box.createRigidArea(btnFldSpacerDim);

        // Populate row04Box
        row04Box.add(m_locateInDirectory);
        row04Box.add(btnFldSpacer02);
        row04Box.add(m_outDirectory);

        // Create component for row05Box
        // row05Box will contain a long, thin blank section that will separate the 
        // top section from the bottom section
        Component sectionSpacer = Box.createRigidArea(new Dimension(250, 10));

        // Populate row05Box
        row05Box.add(sectionSpacer);

        // Populate vertBox
        vertBox.add(row01Box);
        vertBox.add(row02Box);
        vertBox.add(row03Box);
        vertBox.add(row04Box);
        vertBox.add(row05Box);

        return vertBox;
    } // addTopPanel


    // Called from:
    //     constructor
    private Box addBottomSection() {
        Dimension spacerDim = new Dimension(200, 25);
        final int iLblWidth = 100;
        Dimension lblDim = new Dimension(iLblWidth, 25);
        final int iTxtFldWidth = 100;
        Dimension txtFldDim = new Dimension(iTxtFldWidth, 25);

        // horizBox will contain 3 vertical boxes, vert01Box, vert02Box and vert03Box
        Box horizBox = Box.createHorizontalBox();

        // Here we create the bottom left section ===============================
        // Create and populate vert01Box
        Box vert01Box  = Box.createVerticalBox();
        Box row01aBox = Box.createHorizontalBox();
        Box row02aBox = Box.createHorizontalBox();
        Box row03aBox = Box.createHorizontalBox();
        Box row04aBox = Box.createHorizontalBox();

        // Create components for row01aBox (a label and a text field)
        JLabel lblNumFramesToBlur = new JLabel("#Frames to Blur ");
        lblNumFramesToBlur.setPreferredSize(lblDim);
        m_NumBlurFrames = new JTextField(7);
        m_NumBlurFrames.setSize(txtFldDim);
        m_NumBlurFrames.setPreferredSize(txtFldDim);

        // Populate row01aBox
        row01aBox.add(lblNumFramesToBlur);
        row01aBox.add(m_NumBlurFrames);

        // Create component for row02aBox
        // This is a spacer between the "#Frames to Blur" text field and the "Blur Depth" text field
        Component rowSpacer = Box.createRigidArea(new Dimension(iLblWidth + iTxtFldWidth - 5, 5));

        // Populate row02aBox
        row02aBox.add(rowSpacer);

        // Create components for row03aBox (a label and a text field)
        JLabel lblBlurDepth = new JLabel("Blur Depth");
        lblBlurDepth.setPreferredSize(lblDim);
        m_BlurDepth = new JTextField(7);
        m_BlurDepth.setSize(txtFldDim);
        m_BlurDepth.setPreferredSize(txtFldDim);

        // Populate row03aBox
        row03aBox.add(lblBlurDepth);
        row03aBox.add(m_BlurDepth);

        // Create components for row04aBox (a blank row, so we fill it with a spacer)
        Component spacer = Box.createRigidArea(spacerDim);

        // Populate row03aBox
        row04aBox.add(spacer);

        // Populate vert01Box
        vert01Box.add(row01aBox);
        vert01Box.add(row02aBox);
        vert01Box.add(row03aBox);
        vert01Box.add(row04aBox);

        // Now we create the bottom middle section, a blank area ================
        // Create and populate vert02Box
        Box vert02Box = Box.createVerticalBox();
        Component spacer02 = Box.createRigidArea(new Dimension(180, 75));
        vert02Box.add(spacer02);

        // Now we create the bottom right area, which contains 2 buttons ========
        Dimension shortSpacerDim = new Dimension(20, 25);
        Dimension btnDim = new Dimension(80, 25);

        // Create and populate vert03Box
        Box vert03Box = Box.createVerticalBox();
        Box row01cBox = Box.createHorizontalBox();
        Box row02cBox = Box.createHorizontalBox();
        Box row03cBox = Box.createHorizontalBox();
        Box row04cBox = Box.createHorizontalBox();

        // Create components for row01cBox. Row 1 is a blank line, so we will fill it
        // with a spacer.
        Component shrtSpacer01 = Box.createRigidArea(shortSpacerDim);

        // Populate row01cBox
        row01cBox.add(shrtSpacer01);

        // Create components for row02cBox (a spacer and a button)
        btnOK = new JButton("OK");
        btnOK.setSize(btnDim);
        btnOK.setMinimumSize(btnDim);
        btnOK.setMaximumSize(btnDim);
        btnOK.setPreferredSize(btnDim);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });

        // Populate row02cBox
        row02cBox.add(btnOK);

        // Create component for row03cBox
        Component btnSpacer = Box.createRigidArea(new Dimension(80, 8));

        // Populate row03cBox
        // This is a spacer between the OK and Cancel buttons
        row03cBox.add(btnSpacer);

        // Create components for row04cBox (a spacer and a button)
        btnCancel = new JButton("Cancel");
        btnCancel.setSize(btnDim);
        btnCancel.setMinimumSize(btnDim);
        btnCancel.setMaximumSize(btnDim);
        btnCancel.setPreferredSize(btnDim);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onCancel();
            }
        });

        // Populate row04cBox
        row04cBox.add(btnCancel);

        // Populate vert03Box
        vert03Box.add(row01cBox);
        vert03Box.add(row02cBox);
        vert03Box.add(row03cBox);
        vert03Box.add(row04cBox);

        horizBox.add(vert01Box);
        horizBox.add(vert02Box);
        horizBox.add(vert03Box);

        return horizBox;
    } // addBotLeftPanel


    /*
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
    */

    /*
    BEGIN_MESSAGE_MAP(MotionBlurDlg, CDialog)
        //{{AFX_MSG_MAP(MotionBlurDialog)
        ON_BN_CLICKED(IDC_LOCATEDESTDIR, OnLocatedestdir)
        ON_BN_CLICKED(IDC_LOCATEDESTDIR2, OnLocatedestdir2)
        //}}AFX_MSG_MAP
    END_MESSAGE_MAP()
    */

    // This method came from MOTIONBLURDIALOG.CPP
    void onLocateDestDir() {
        // Find a bmp file
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dlg.setFileFilter(new BMPFileFilter());
        int showDlgResult = dlg.showDialog(null, "Select bmp file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            m_firstImage.setText(dlg.getSelectedFile().getName());	
        }
    } // onLocateDestDir


    // This method came from MOTIONBLURDIALOG.CPP
    void onLocateDestDir2() {
        // Find a bmp file
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dlg.setFileFilter(new BMPFileFilter());
        int showDlgResult = dlg.showDialog(null, "Select bmp file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            m_outDirectory.setText(dlg.getSelectedFile().getName());
        }
    } // onLocateDestDir2


    // This method came from MOTIONBLURDIALOG.CPP
    // Called when the user clicks on the OK button
    void onOK() {
        String sFirstImage, sOutDir, sBlurDepth, sNumBlurFrames;
        int iBlurDepth = 0, iNumBlurFrames = 0;
        Integer parsedValue = 0;

        sFirstImage = m_firstImage.getText();
        sOutDir = m_outDirectory.getText();

        sNumBlurFrames = m_NumBlurFrames.getText();
        // iNumBlurFrames = Integer.parseInt(sNumBlurFrames);
        if (getIntegerValue(sNumBlurFrames, parsedValue) == null) {
            iNumBlurFrames = parsedValue.intValue();
        } else {
            JOptionPane.showMessageDialog(this, "# Frames to blur value entered is not a valid number.");
            m_NumBlurFrames.requestFocusInWindow();
            return;
        }

        sBlurDepth = m_BlurDepth.getText();
        // iBlurDepth = Integer.parseInt(sBlurDepth);
        if (getIntegerValue(sBlurDepth, parsedValue) == null) {
            iBlurDepth = parsedValue.intValue();
        } else {
            JOptionPane.showMessageDialog(this, "Blur depth value entered is not a valid number.");
            m_BlurDepth.requestFocusInWindow();
            return;
        }

        String sMsgText = "numBlurFrames " + iNumBlurFrames + " blurDepth " + iBlurDepth;
        Globals.statusPrint(sMsgText);

        // Blur a sequence of images
        int iStatus = 0;
        // TODO: In future, uncomment the following line
        iStatus = Globals.motionBlur(sFirstImage, sOutDir, iNumBlurFrames, iBlurDepth);

        sMsgText = "motion blur Complete. Status: " + iStatus;
        Globals.statusPrint(sMsgText);
    } // onOK


    private NumberFormatException getIntegerValue(String psIntAsString, Integer pIValue) {
        try {
            pIValue = Integer.parseInt(psIntAsString);
        } catch (NumberFormatException nfe) {
            return nfe;
        }
        return null;
    }

    
    // Called when the user clicks on the Cancel button
    public void onCancel() {
        dispose();
    } // onCancel
} // class MotionBlurDlg