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

    JButton btnOK;
    JButton btnCancel;

    JPanel topPanel;
    JPanel botLeftPanel;
    JPanel botRightPanel;

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

        Box topBox = addTopPanel();
        row01Box.add(topBox);

        Box botLeftBox = addBotLeftPanel();
        Box botRightBox = addBotRightPanel();
        row02Box.add(botLeftBox);
        row02Box.add(botRightBox);

        panel.add(row01Box);
        panel.add(row02Box);
        this.add(panel);
        setVisible(true);
    } // MotionBlurDlg ctor


    // Called from:
    //     constructor
    private Box addTopPanel() {
        Dimension spacerDim = new Dimension(80, 25);
        Dimension btnDim = new Dimension(80, 25);
        Dimension btnFldSpacerDim = new Dimension(10, 25);

        Box vertBox  = Box.createVerticalBox();
        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();
        Box row04Box = Box.createHorizontalBox();

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

        // Populate vertBox
        vertBox.add(row01Box);
        vertBox.add(row02Box);
        vertBox.add(row03Box);
        vertBox.add(row04Box);

        return vertBox;
    } // addTopPanel


    // Called from:
    //     constructor
    private Box addBotLeftPanel() {
        Dimension spacerDim = new Dimension(200, 25);
        Dimension lblDim = new Dimension(100, 25);
        Dimension txtFldDim = new Dimension(100, 25);

        Box vertBox  = Box.createVerticalBox();
        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();

        // Create components for row01Box (a label and a text field)
        JLabel lblNumFramesToBlur = new JLabel("#Frames to Blur ");
        lblNumFramesToBlur.setPreferredSize(lblDim);
        m_NumBlurFrames = new JTextField(7);
        m_NumBlurFrames.setSize(txtFldDim);
        m_NumBlurFrames.setPreferredSize(txtFldDim);

        // Populate row01Box
        row01Box.add(lblNumFramesToBlur);
        row01Box.add(m_NumBlurFrames);

        // Create components for row02Box (a label and a text field)
        JLabel lblBlurDepth = new JLabel("Blur Depth");
        lblBlurDepth.setPreferredSize(lblDim);
        m_BlurDepth = new JTextField(7);
        m_BlurDepth.setSize(txtFldDim);
        m_BlurDepth.setPreferredSize(txtFldDim);

        // Populate row02Box
        row02Box.add(lblBlurDepth);
        row02Box.add(m_BlurDepth);

        // Create components for row03Box (a blank row, so we fill it with a spacer)
        Component spacer = Box.createRigidArea(spacerDim);

        // Populate row03Box
        row03Box.add(spacer);

        // Populate vertBox
        vertBox.add(row01Box);
        vertBox.add(row02Box);
        vertBox.add(row03Box);

        return vertBox;
    } // addBotLeftPanel


    // Called from:
    //     constructor
    private Box addBotRightPanel() {
        Dimension longSpacerDim = new Dimension(160, 25);
        Dimension shortSpacerDim = new Dimension(20, 25);
        Dimension btnDim = new Dimension(80, 25);

        Box vertBox = Box.createVerticalBox();
        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();

        // Create components for row01Box. Row 1 is a blank line, so we will fill it
        // with 2 spacers.
        Component lngSpacer01 = Box.createRigidArea(longSpacerDim);
        Component shrtSpacer01 = Box.createRigidArea(shortSpacerDim);

        // Populate row01Box
        row01Box.add(lngSpacer01);
        row01Box.add(shrtSpacer01);

        // Create components for row02Box (a spacer and a button)
        Component lngSpacer02 = Box.createRigidArea(longSpacerDim);
        btnOK = new JButton("OK");
        btnOK.setPreferredSize(btnDim);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });

        // Populate row02Box
        row02Box.add(lngSpacer02);
        row02Box.add(btnOK);

        // Create components for row03Box (a spacer and a button)
        Component lngSpacer03 = Box.createRigidArea(longSpacerDim);
        btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(btnDim);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onCancel();
            }
        });

        // Populate row03Box
        row03Box.add(lngSpacer03);
        row03Box.add(btnCancel);

        // Populate vertBox
        vertBox.add(row01Box);
        vertBox.add(row02Box);
        vertBox.add(row03Box);

        return vertBox;
    } // addBotRightPanel


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

    /////////////////////////////////////////////////////////////////////////////
    // MotionBlurDialog message handlers

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
        String aFirstImage, aOutDir, aBlurDepth, aNumBlurFrames;
        int blurDepth, numBlurFrames;

        aFirstImage = m_firstImage.getText();
        aOutDir = m_outDirectory.getText();

        aNumBlurFrames = m_NumBlurFrames.getText();
        numBlurFrames = Integer.parseInt(aNumBlurFrames);

        aBlurDepth = m_BlurDepth.getText();
        blurDepth = Integer.parseInt(aBlurDepth);
        
        String msgText = "numBlurFrames " + numBlurFrames + " blurDepth " + blurDepth;
        // TODO: In future, uncomment the following line
        //Globals.statusPrint(msgText);

        // Blur a sequence of images
        int iStatus = 0;
        // TODO: In future, uncomment the following line
        //iStatus = Globals.motionBlur(aFirstImage, aOutDir, numBlurFrames, blurDepth);

        msgText = "motion blur Complete. Status: " + iStatus;
        // TODO: In future, uncomment the following line
        //Globals.statusPrint(msgText);
    } // onOK


    // Called when the user clicks on the Cancel button
    public void onCancel() {
        dispose();
    } // onCancel
} // class MotionBlurDlg