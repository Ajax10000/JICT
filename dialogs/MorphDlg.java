package dialogs;

import core.MemImage;

import fileUtils.BMPFileFilter;
import fileUtils.FileUtils;

import globals.Globals;
import globals.JICTConstants;

import java.io.File;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import math.TMatrix;

import structs.Point3d;

// This dialog is displayed  when the user selects the 
// "Create a Morph Sequence..." menu item from the Tools menu.
// See method onToolsMorphSequence of the MainFrame class.
// To see what it should look like, see Figure D.11 on p 283 of the book.
public class MorphDlg extends JDialog {
    int m_morphType;
    int m_numFrames;
    String m_morph1Path;
    String m_morph2Path;
    String m_firstOutputPath;

    // EDITTEXT        IDC_NumBlurFrames,43,116,36,13,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_NumBlurFrames, m_preNumFrames);
	JTextField	m_preNumFrames;

    // EDITTEXT        IDC_EDITDIRECTORY,43,80,194,14,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_EDITDIRECTORY, m_firstOutPath);
	JTextField	m_firstOutPath;

    // EDITTEXT        IDC_EDITFIRSTIMAGE2,43,50,194,14,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_EDITFIRSTIMAGE2, m_secondImage);
	JTextField	m_secondImage;

    // EDITTEXT        IDC_EDITFIRSTIMAGE,42,17,194,14,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
	JTextField	m_firstImage;

    // CONTROL         "2D",IDC_2D,"Button",BS_AUTORADIOBUTTON,140,117,25,10
    JRadioButton rbn2D;

    // CONTROL         "3D",IDC_3D,"Button",BS_AUTORADIOBUTTON,140,128,25,10
    JRadioButton rbn3D;

    // GROUPBOX        "Type",IDC_Type,132,104,41,37
    JPanel pnlGroupBox;

    // DEFPUSHBUTTON   "OK",IDOK,206,110,29,14
    JButton btnOK;

    // PUSHBUTTON      "Cancel",IDCANCEL,206,126,29,14
    JButton btnCancel;

    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR,5,17,34,14
    JButton btnLocateFirstImgToMorph;

    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR2,6,80,34,14
    JButton btnLocateSecImgToMorph;

    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR3,6,50,34,14
    JButton btnLocateCompletePath;

    // LTEXT           "First Image to Morph",-1,43,6,81,11
    JLabel lblFirstImg;

    // LTEXT           "Second Image to Morph",-1,44,39,81,11
    JLabel lblSecImg;

    // LTEXT           "Complete pathname of first image in output sequence.",
    //                 IDC_STATIC2,45,68,181,11
    JLabel lblComplPath;

    // LTEXT           "#Frames in Sequence",-1,43,106,73,10
    JLabel lblNumFrames;

/* 
class CMorphDialog : public CDialog
{
// Construction
public:
	CMorphDialog(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CMorphDialog)
	enum { IDD = IDD_Morph };
	CEdit	m_preNumFrames;
	CEdit	m_firstOutPath;
	CEdit	m_secondImage;
	CEdit	m_firstImage;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMorphDialog)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
     int m_morphType;
     int m_numFrames;
     char m_morph1Path[80];
     char m_morph2Path[80];
     char m_firstOutputPath[80];
	// Generated message map functions
	//{{AFX_MSG(CMorphDialog)
	afx_msg void OnLocatedestdir();
	afx_msg void OnLocatedestdir2();
	afx_msg void OnLocatedestdir3();
	afx_msg void On2d();
	afx_msg void On3d();
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
*/

    // This constructor came from MORPHDIALOG.CPP
    public MorphDlg(JFrame pParent, boolean pModal) {
        super(pParent, pModal);

        setTitle("Create Morph Sequence");
        setSize(520, 320);
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();

        Box topBox = addTopSection();
        row01Box.add(topBox);

        Box botBox = addBotSection();
        row02Box.add(botBox);

        panel.add(row01Box);
        panel.add(row02Box);
        this.add(panel);

        setVisible(true);
    } // MorphDlg ctor


    // Called from:
    //     constructor
    private Box addTopSection() {
        Dimension txtFldSize = new Dimension(250, 25);
        Dimension locateBtnSize = new Dimension(90, 25);
        Dimension btnTxtFldSpacerSize = new Dimension(8, 25);
        Dimension largeSpacerSize = new Dimension(98, 25);
        Dimension rowSpacerSize = new Dimension(400, 25);

        Box horiz01Box = Box.createHorizontalBox();
        Box horiz02Box = Box.createHorizontalBox();
        Box horiz03Box = Box.createHorizontalBox();
        Box horiz04Box = Box.createHorizontalBox();
        Box horiz05Box = Box.createHorizontalBox();
        Box horiz06Box = Box.createHorizontalBox();
        Box horiz07Box = Box.createHorizontalBox();

        // Create components for horiz01Box (Label "First Image to Morph")
        Component spacerForFirstLbl = Box.createRigidArea(largeSpacerSize);
        JLabel lblFirstImage = new JLabel("First Image to Morph");

        // Populate horiz01Box
        horiz01Box.add(spacerForFirstLbl);
        horiz01Box.add(lblFirstImage);

        // Create components for horiz02Box
        JButton btnLocateFirstImgToMorph = new JButton("Locate");
        btnLocateFirstImgToMorph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir();
            }
        });
        btnLocateFirstImgToMorph.setSize(locateBtnSize);
        btnLocateFirstImgToMorph.setPreferredSize(locateBtnSize);

        Component firstBtnTxtFldSpacer = Box.createRigidArea(btnTxtFldSpacerSize);

        m_firstImage = new JTextField();
        m_firstImage.setSize(txtFldSize);
        m_firstImage.setMinimumSize(txtFldSize);
        m_firstImage.setMaximumSize(txtFldSize);
        m_firstImage.setPreferredSize(txtFldSize);

        // Populate horiz02Box
        horiz02Box.add(btnLocateFirstImgToMorph);
        horiz02Box.add(firstBtnTxtFldSpacer);
        horiz02Box.add(m_firstImage);

        // Create components for horiz03Box (Label "Second Image to morph")
        Component spacerForSecondLbl = Box.createRigidArea(largeSpacerSize);
        JLabel lblSecondImage = new JLabel("Second Image to Morph");
        //lblSecondImage.setSize();
        //lblSecondImage.setPreferredSize();

        // Populate horiz03Box
        horiz03Box.add(spacerForSecondLbl);
        horiz03Box.add(lblSecondImage);
        
        // Create components for horiz04Box
        JButton btnMidLocate = new JButton("Locate");
        btnMidLocate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir3();
            }
        });
        btnMidLocate.setSize(locateBtnSize);
        btnMidLocate.setPreferredSize(locateBtnSize);

        Component secondBtnTxtFldSpacer = Box.createRigidArea(btnTxtFldSpacerSize);

        m_secondImage = new JTextField();
        m_secondImage.setSize(txtFldSize);
        m_secondImage.setMinimumSize(txtFldSize);
        m_secondImage.setMaximumSize(txtFldSize);
        m_secondImage.setPreferredSize(txtFldSize);

        // Populate horiz04Box
        horiz04Box.add(btnMidLocate);
        horiz04Box.add(secondBtnTxtFldSpacer);
        horiz04Box.add(m_secondImage);

        // Create component for horiz05Box (Label "Complete pathname of first image in output sequence")
        Component spacerForThirdLbl = Box.createRigidArea(largeSpacerSize);
        JLabel lblCompletePathname = new JLabel("Complete pathname of first image in output sequence");

        // Populate horiz05Box
        horiz05Box.add(spacerForThirdLbl);
        horiz05Box.add(lblCompletePathname);

        // Create component for horiz06Box
        JButton btnBotLocate = new JButton("Locate");
        btnBotLocate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onLocateDestDir2();
            }
        });
        btnBotLocate.setSize(locateBtnSize);
        btnBotLocate.setPreferredSize(locateBtnSize);

        Component thirdBtnTxtFldSpacer = Box.createRigidArea(btnTxtFldSpacerSize);

        m_firstOutPath = new JTextField();
        m_firstOutPath.setSize(txtFldSize);
        m_firstOutPath.setMinimumSize(txtFldSize);
        m_firstOutPath.setMaximumSize(txtFldSize);
        m_firstOutPath.setPreferredSize(txtFldSize);

        // Populate horiz06Box
        horiz06Box.add(btnBotLocate);
        horiz06Box.add(thirdBtnTxtFldSpacer);
        horiz06Box.add(m_firstOutPath);
        

        Box vertBox = Box.createVerticalBox();
        vertBox.add(horiz01Box);
        vertBox.add(horiz02Box);
        vertBox.add(horiz03Box);
        vertBox.add(horiz04Box);
        vertBox.add(horiz05Box);
        vertBox.add(horiz06Box);

        return vertBox;
    } // addTopSection


    // Called from:
    //     constructor
    private Box addBotSection() {
        final int iPnlWidth = 90;
        final int iPnlHeight = 80;
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Dimension firstSpacerSize = new Dimension(80, 80);
        Dimension secondSpacerSize = new Dimension(45, 80);
        Dimension numFramesLblSize = new Dimension(70, 25);
        Dimension numFramesFldSize = new Dimension(50, 25);

        Box vert01Box = Box.createVerticalBox();
        Box vert02Box = Box.createVerticalBox();
        Box vert03Box = Box.createVerticalBox();
        Box vert04Box = Box.createVerticalBox();
        Box vert05Box = Box.createVerticalBox();
        Box vert06Box = Box.createVerticalBox();

        // Create components for vert01Box (a spacer)
        Component firstSpacer = Box.createRigidArea(firstSpacerSize);

        // Populate vert01Box
        vert01Box.add(firstSpacer);

        // Create components for vert02Box
        Component vert01Spacer = Box.createRigidArea(new Dimension(50, 10));
        JLabel lblNumFramesInSeq = new JLabel("#Frames in Sequence");
        lblNumFramesInSeq.setSize(numFramesLblSize);
        lblNumFramesInSeq.setMinimumSize(numFramesLblSize);
        lblNumFramesInSeq.setPreferredSize(numFramesLblSize);
        lblNumFramesInSeq.setAlignmentX(0.0f);

        m_preNumFrames = new JTextField();
        m_preNumFrames.setSize(numFramesFldSize);
        m_preNumFrames.setMinimumSize(numFramesFldSize);
        m_preNumFrames.setMaximumSize(numFramesFldSize);
        m_preNumFrames.setPreferredSize(numFramesFldSize);
        m_preNumFrames.setAlignmentX(0.0f);

        // Populate vert02Box
        vert02Box.add(vert01Spacer);
        vert02Box.add(lblNumFramesInSeq);
        vert02Box.add(m_preNumFrames);

        // Create components for vert03Box (a spacer)
        Component secondSpacer = Box.createRigidArea(secondSpacerSize);

        // Populate vert03Box
        vert03Box.add(secondSpacer);

        // Create components for vert04Box (Type radio buttons)
        rbn2D = new JRadioButton("2D");
        rbn2D.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                on2d();
            }
        });

        rbn3D = new JRadioButton("3D");
        rbn3D.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                on3d();
            }
        });

        Dimension pnlSize = new Dimension(iPnlWidth, iPnlHeight);
        JPanel pnlType = new JPanel();
        pnlType.setSize(pnlSize);
        pnlType.setMaximumSize(pnlSize);
        pnlType.setPreferredSize(pnlSize);
        BoxLayout boxLayout = new BoxLayout(pnlType, BoxLayout.Y_AXIS);
        pnlType.setLayout(boxLayout);
        pnlType.setBorder(BorderFactory.createTitledBorder(loweredetched, "Type"));
        pnlType.setAlignmentX(SwingConstants.LEFT);
        pnlType.add(rbn2D);
        pnlType.add(rbn3D);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbn2D);
        bg.add(rbn3D);

        // Populate vert04Box
        vert04Box.add(pnlType);

        // Create components for vert05Box 
        // Populate vert05Box

        // Create components for vert06Box (OK and Cancel buttons)
        Component btnSpacer = Box.createRigidArea(new Dimension(50, 30));
        Dimension btnSize = new Dimension(80, 25);
        btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });
        btnOK.setSize(btnSize);
        btnOK.setMinimumSize(btnSize);
        btnOK.setMaximumSize(btnSize);
        btnOK.setPreferredSize(btnSize);

        Component smallVertSpacer = Box.createRigidArea(new Dimension(80, 5));

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

        // Populate vert06Box
        vert06Box.add(btnSpacer);
        vert06Box.add(btnOK);
        vert06Box.add(smallVertSpacer);
        vert06Box.add(btnCancel);

        Box horizBox = Box.createHorizontalBox();
        horizBox.add(vert01Box);
        horizBox.add(vert02Box);
        horizBox.add(vert03Box);
        horizBox.add(vert04Box);
        horizBox.add(vert05Box);
        horizBox.add(vert06Box);

        return horizBox;
    } // addBotSection


    /*
    // This method came from MORPHDIALOG.CPP
    void DoDataExchange(CDataExchange pDX) {
        CDialog.DoDataExchange(pDX);
        
        //{{AFX_DATA_MAP(CMorphDialog)
        DDX_Control(pDX, IDC_NumBlurFrames, m_preNumFrames);
        DDX_Control(pDX, IDC_EDITDIRECTORY, m_firstOutPath);
        DDX_Control(pDX, IDC_EDITFIRSTIMAGE2, m_secondImage);
        DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
        //}}AFX_DATA_MAP
    }
    */

    /////////////////////////////////////////////////////////////////////////////
    // CMorphDialog message handlers

    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the top LOCATE button
    // Note the (x, y) coordinates for this button are (5, 17)
    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR,5,17,34,14
    private void onLocateDestDir() {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dlg.setFileFilter(new BMPFileFilter());
        int showDlgResult = dlg.showDialog(null, "Select morph image");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            File file = dlg.getSelectedFile();
            String sFileName = file.getName();
            m_firstImage.setText(sFileName);
        }
    } // onLocateDestDir


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the bottom locate button 
    // Note the (x, y) coordinates for this button are are (6, 80)
    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR2,6,80,34,14
    private void onLocateDestDir2() {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dlg.setFileFilter(new BMPFileFilter());
        int showDlgResult = dlg.showDialog(null, "Select morph image");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            File file = dlg.getSelectedFile();
            String sFileName = file.getName();
            m_firstOutPath.setText(sFileName);
        }
    } // onLocateDestDir2


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the middle Locate button
    // Note the (x, y) coordinates for this button are (6, 50)
    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR3,6,50,34,14
    private void onLocateDestDir3() {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int showDlgResult = dlg.showDialog(this, "Select scn file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            File file = dlg.getSelectedFile();
            String sFileName = file.getName();
            m_secondImage.setText(sFileName);	
        }
    } // onLocateDestDir3


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the 2D radio button
    // CONTROL         "2D",IDC_2D,"Button",BS_AUTORADIOBUTTON,140,117,25,10
    private void on2d() {
        m_morphType = JICTConstants.I_TWOD;
    } // on2d


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the 3D radio button
    // CONTROL         "3D",IDC_3D,"Button",BS_AUTORADIOBUTTON,140,128,25,10
    private void on3d() {
        m_morphType = JICTConstants.I_THREED;
    } // on3d


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the OK button
    // DEFPUSHBUTTON   "OK",IDOK,206,110,29,14
    private void onOK() {
        String msgText;
        MemImage inImageA, inImageB;
        float aFraction, increment;
        String imagePath, shapePath;
        int aStatus, i, outFrameNum = 0;
        String outPath, aNumFrames, firstImage, secondImage;
        String directory = "", fileName = "", prefix = "", suffix = "";
        // String cNumFrames, cPathName; // These variables are not being used.

        aNumFrames  = m_preNumFrames.getText();
        m_numFrames = Integer.parseInt(aNumFrames);

        firstImage  = m_firstImage.getText();
        secondImage = m_secondImage.getText();
        outPath     = m_firstOutPath.getText();

        FileUtils.getPathPieces(outPath, directory, fileName,
            prefix, outFrameNum, suffix);

        switch(m_morphType) {
        case JICTConstants.I_TWOD:
            inImageA = new MemImage(firstImage, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
            if(!inImageA.isValid()) {
                Globals.statusPrint("CMorphDialog: Unable to open firstImage.");
                return;
            }
            inImageA.adjustImageBorder(firstImage);

            inImageB = new MemImage(secondImage, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
            if(!inImageB.isValid()) {
                Globals.statusPrint("CMorphDialog: Unable to open secondImage.");
                return;
            }

            inImageB.adjustImageBorder(secondImage);

            inImageB = new MemImage(firstImage, 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
            inImageA = new MemImage(secondImage, 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
            aFraction = 0.0f;
            increment = 1.0f/(float)(m_numFrames - 1.0f);

            for(i = 1; i <= m_numFrames; i++) {
                imagePath = directory + prefix + String.format("%04o", i) + suffix + ".bmp";
                Globals.statusPrint(imagePath);

                shapePath = directory + prefix + String.format("%04o", i) + suffix + ".shp";
                aStatus = Globals.tweenImage(aFraction, inImageA, inImageB, imagePath, shapePath);
                aFraction += increment;
            }
            break;

        case JICTConstants.I_THREED:
            // Given two quadmeshes a and b, create a sequence of images where the
            // result mesh transitions from a to b in numFrames frames.
            float fractionIncrement;
            Integer aHeight = 0, aWidth = 0, bpp = 0;

            String aTexPath;
            StringBuffer sbAXPath = new StringBuffer("");
            StringBuffer sbAYPath = new StringBuffer("");
            StringBuffer sbAZPath = new StringBuffer("");
            String bTexPath;
            StringBuffer sbBXPath = new StringBuffer("");
            StringBuffer sbBYPath = new StringBuffer("");
            StringBuffer sbBZPath = new StringBuffer("");
            MemImage aTexture, aX, aY, aZ;
            MemImage bTexture, bX, bY, bZ; 
            MemImage oTexture, oX, oY, oZ;
            
            aTexPath = new String(firstImage);
            FileUtils.constructPathName(sbAXPath, aTexPath, 'x');
            FileUtils.constructPathName(sbAYPath, aTexPath, 'y');
            FileUtils.constructPathName(sbAZPath, aTexPath, 'z');
            aStatus = Globals.readBMPHeader(aTexPath, aHeight, aWidth, bpp);
            if(aStatus != 0) {
                Globals.statusPrint("MorphDlg.onOK(): Unable to open image header.");
                return;
            }

            bpp = 8;
            switch (bpp) {
            case 8:
                aTexture = new MemImage(aTexPath, 0, 0, 
                    JICTConstants.I_RANDOM, 'R', JICTConstants.I_GREENCOLOR);
                break;

            case 24:
                aTexture = new MemImage(aTexPath, 0, 0, 
                    JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
                break;

            default:
                Globals.statusPrint ("MorphDlg.onOK(): Image a must have 8 or 24 bit pixels");
                return;
            } // switch

            aX = new MemImage(sbAXPath.toString(), 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_A32BIT);
            aY = new MemImage(sbAYPath.toString(), 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_A32BIT);
            aZ = new MemImage(sbAZPath.toString(), 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_A32BIT);

            bTexPath = new String(secondImage);
            FileUtils.constructPathName(sbBXPath, bTexPath, 'x');
            FileUtils.constructPathName(sbBYPath, bTexPath, 'y');
            FileUtils.constructPathName(sbBZPath, bTexPath, 'z');
        
            switch (bpp) {
            case 8:
                bTexture = new MemImage(aTexPath, 0, 0, 
                    JICTConstants.I_RANDOM, 'R', JICTConstants.I_GREENCOLOR);
                break;

            case 24:
                bTexture = new MemImage(aTexPath, 0, 0, 
                    JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
                break;

            default:
                Globals.statusPrint("MorphDlg.onOK(): Image b must have 8 or 24 bit pixels");
                return;
            } // switch

            bX = new MemImage(sbBXPath.toString(), 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_A32BIT);
            bY = new MemImage(sbBYPath.toString(), 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_A32BIT);
            bZ = new MemImage(sbBZPath.toString(), 0, 0, 
                JICTConstants.I_RANDOM, 'R', JICTConstants.I_A32BIT);

            int imHeight = aTexture.getHeight();
            int imWidth  = aTexture.getWidth();

            oTexture = new MemImage(imHeight, imWidth, bpp);
            oX = new MemImage(imHeight, imWidth, 32);
            oY = new MemImage(imHeight, imWidth, 32);
            oZ = new MemImage(imHeight, imWidth, 32);
            aFraction = 1.0f;
            fractionIncrement = 1.0f/((float)m_numFrames - 1.0f);
            
            TMatrix aMatrix = new TMatrix();
            Point3d centroid = new Point3d();

            for (i = 1; i <= m_numFrames; i++) {
                msgText = "tweenMesh. Frame " + i + "  aFraction: " + aFraction;
                Globals.statusPrint(msgText);
                aStatus = Globals.tweenMesh(aFraction, 
                    aTexture, aX, aY, aZ,
                    bTexture, bX, bY, bZ, 
                    oTexture, oX, oY, oZ);

                // Move the mesh to the center of the view screen
                aStatus = Globals.getMeshCentroid(oX, oY, oZ,
                    centroid);

                aStatus = Globals.translateMesh(oX, oY, oZ,
                    -centroid.fX, -centroid.fY, -centroid.fZ);

                // sprintf(imagePath, "%s%s%#04d%c.bmp", directory, prefix, i, suffix);
                imagePath = directory + prefix + String.format("%04o", i) + suffix + ".shp";
                Globals.statusPrint(imagePath);
                aStatus = Globals.renderMesh(imagePath, oTexture, oX, oY, oZ, aMatrix);

                aFraction -= fractionIncrement;
                oTexture.clear();
                oX.clear();
                oY.clear();
                oZ.clear();
            }

            Globals.statusPrint("3D morph sequence complete.");
            break;
        } // switch

        this.setVisible(false);
    } // onOK


    // Called when the user clicks on the Cancel button
    private void onCancel() {
        this.dispose();
    } // onCancel
} // class MorphDlg