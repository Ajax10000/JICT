package frames;

import apps.IctApp;

import core.MemImage;
import core.SceneList;
import core.ScnFileParser;
import core.Shape3d;

import dialogs.AboutDlg;
import dialogs.ImageView;
import dialogs.MakeTextureDlg;
import dialogs.MorphDlg;
import dialogs.MotionBlurDlg;
import dialogs.QuadMeshDlg;
import dialogs.ScenePreviewDlg;
import dialogs.WarpParamDlg;

import fileUtils.BMPFileFilter;
import fileUtils.FileUtils;
import fileUtils.SCNFileFilter;
import fileUtils.WRLFileFilter;

import globals.Globals;
import globals.JICTConstants;
import globals.Preference;
import globals.VRML;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javax.swing.border.BevelBorder;

import math.TMatrix;

import structs.Point3d;

public class MainFrame extends JFrame implements ActionListener {

/*
class CMainFrame : public CMDIFrameWnd
{
	DECLARE_DYNAMIC(CMainFrame)
public:
	CMainFrame();

// Attributes
public:
  int isDirty;                         // 1 if client window needs erasing
  float warpRotateX,warpRotateY,warpRotateZ;
  float warpScaleX,warpScaleY,warpScaleZ;
  float warpTranslateX,warpTranslateY,warpTranslateZ;
  float viewRotateX,viewRotateY,viewRotateZ;
  float viewTranslateX,viewTranslateY,viewTranslateZ;
  char sceneName[MAXPATH], sceneFileName[MAXPATH];
  int effectType, mode, colorMode;
  int outputRows, outputColumns;
  int cutoutEnabled;         // Menu control variables
  int previewSceneEnabled;
  int previewSequenceEnabled;
  int renderSceneEnabled;
  int renderSequenceEnabled;
  int removeSampleColorsEnabled;
  int depthSortingEnabled;
  int zBufferEnabled;
  int imageSamplingEnabled;
  int motionBlurEnabled;
  int hazeFogEnabled;
  int antiAliasEnabled;
  int previewingScene;      // 1 if the scene is being previewed
  int previewingSequence;   // 1 if sequence is being previewed
  int changeViewPoint;      // 1 if the ViewPoint is being previewed
  sceneList *mySceneList;     // Linked List containing scene description
  tMatrix *modelMatrix;       // Contains a model transformation
  tMatrix *viewMatrix;        // Contains viewpoint transformation
  imageView *previewWindowHandle;	 // the image window into which the scene preview display is drawn
// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
//{{AFX_VIRTUAL(CMainFrame)
//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CMainFrame();
//	afx_msg void OnPaint();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:  // control bar embedded members
	CStatusBar  m_wndStatusBar;

// Generated message map functions
protected:
    void warpImage (); - not found in MAINFRM.H nor in MAINFRM.CPP
    void getViewMatrix(tMatrix *viewMatrix); - implemented
    void closeAllChildren(); - implemented

	//{{AFX_MSG(CMainFrame)
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct); - commented out
	afx_msg void OnPreviewScene(); - renamed to onPreviewStillScene
	afx_msg void OnPreviewSequence(); - implemented
	afx_msg void OnRenderDepthsorting(); - implemented
	afx_msg void OnRenderScene(); - renamed to onRenderStillScene
	afx_msg void OnRenderSequence(); - implemented
	afx_msg void OnToolsCreatealphaimage(); - implemented
	afx_msg void OnToolsCreateascenelist(); - implemented
	afx_msg void OnToolsCreatecutout(); - implemented
	afx_msg void OnToolsMorphSequence(); - implemented
	afx_msg void OnToolsWarpimage(); - implemented
	afx_msg void OnUpdateToolsCreatecutout(CCmdUI* pCmdUI); - commented out
	afx_msg void OnUpdatePreviewScene(CCmdUI* pCmdUI); - renamed to onUpdatePreviewStillScene, then commented out
	afx_msg void OnUpdatePreviewSequence(CCmdUI* pCmdUI); - commented out
	afx_msg void OnUpdateRenderScene(CCmdUI* pCmdUI); - renamed to onUpdateRenderStillScene, then commented out
	afx_msg void OnUpdateRenderSequence(CCmdUI* pCmdUI); - commented out
	afx_msg void OnUpdateRenderDepthsorting(CCmdUI* pCmdUI); - commented out
	afx_msg BOOL OnEraseBkgnd(CDC* pDC); - commented out
	afx_msg void OnToolsTest(); - implemented
	afx_msg void OnRenderZbuffer(); - implemented
	afx_msg void OnUpdateRenderZbuffer(CCmdUI* pCmdUI); - commented out
	afx_msg void OnUpdateToolsSampleimage(CCmdUI* pCmdUI); - commented out
	afx_msg void OnToolsSampleimage(); - implemented
	afx_msg void OnToolsRemoveSampleColors(); - implemented
	afx_msg void OnUpdateToolsRemoveSampleColors(CCmdUI* pCmdUI); - commented out
	afx_msg void OnToolsCreatemesh(); - implemented
	afx_msg void OnToolsCreatetextureimage(); - implemented
	afx_msg void OnUpdateRenderHazefog(CCmdUI* pCmdUI); - commented out
	afx_msg void OnUpdateRenderAntialias(CCmdUI* pCmdUI); - commented out
	afx_msg void OnToolsMotionblur(); - implemented
	afx_msg void OnRenderHazefog(); - implemented
	afx_msg void OnRenderAntialias(); - implemented
	afx_msg void OnToolsRenderVrmlFile(); - implemented
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
*/
    private IctApp ictApp;

    // 1 if client window needs erasing
    // I changed this from int to boolean
    // Modified in:
    //     onPreviewSequenceScene
    //     onPreviewStillScene
    //     onRenderSequence
    //     onRenderStillScene
    //     onToolsCreateAlphaImage
    //     onToolsCreateASceneList
    //     onToolsCreateCutout
    public boolean mbIsDirty;


    // Read in: 
    //     onWarpParamDlgClosed
    //     ScenePreviewDlg.setTextBoxesWithModelTransform (passed as parameter to DecimalFormat.format)
    //     ScenePreviewDlg.onCmdPlus (passed as parameters to MathUtils.fPolar and SceneList.setCurrentModelTransform)
    // Modified in: 
    //     the constructor, when it calls initFields
    //     onWarpParamDlgClosed. Here they are passed as parameters to Globals.iwarpz
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onCmdPlus
    //     ScenePreviewDlg.onCmdReset
    //     ScenePreviewDlg.onSelChangeCmbModels
    public Float mWarpRotateX, mWarpRotateY, mWarpRotateZ;

    // Read in: 
    //     onWarpParamDlgClosed Here they are passed as parameters to Globals.iwarpz
    //     ScenePreviewDlg.setTextBoxesWithModelTransform
    // Modified in: 
    //     constructor, when it calls initFields
    //     onWarpParamDlgClosed 
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onCmdReset
    //     ScenePreviewDlg.onSelChangeCmbModels
    public float mWarpScaleX, mWarpScaleY, mWarpScaleZ;

    // Read in: 
    //     ScenePreviewDlg.onCmdPlus (here they are passed as parameters to SceneList.setCurrentModelTransorm)
    //     ScenePreviewDlg.setTextBoxesWithModelTransform (passed pas parameters to DecimalFormt.format)
    // Modified in: 
    //     constructor, when it calls initFields
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onCmdPlus
    //     ScenePreviewDlg.onCmdReset
    //     ScenePreviewDlg.onSelChangeCmbModels
    public float mWarpTranslateX, mWarpTranslateY, mWarpTranslateZ;

    // Read in: 
    //     getViewMatrix - converted to radians before being passed to TMatrix.rotate
    //     ScenePreviewDlg.setTextBoxesWithViewTransform
    //     ScenePreviewDlg.onOK
    // Modified in:
    //     onToolsCreateASceneList - passed as an output parameter to SceneList.getViewTransform
    //     ScenePreviewDlg.onCmdReset
    public float mViewRotateX, mViewRotateY, mViewRotateZ;

    // Read in: 
    //     getViewMatrix - passed as a parameter to TMatrix.translate
    //     ScenePreviewDlg.setTextBoxesWithViewTransform
    //     ScenePreviewDlg.onOK
    // Modified in:
    //     ScenePreviewDlg.onCmdPlus
    //     ScenePreviewDlg.onCmdReset
    public float mViewTranslateX, mViewTranslateY, mViewTranslateZ;

    // Set in:
    //     onToolsCreateASceneList (when SceneList.getSceneInfo is called)
    public String msSceneName;

    // Set in:
    //     onToolsCreateASceneList
    // Read in: 
    //     ScenePreviewDlg.onOk
    public String msSceneFileName;

    // Read in:
    //     onToolsCreateASceneList
    // Set in:
    //     onToolsCreateASceneList (set when method SceneList.getSceneInfo is called)
    public Integer mIEffectType;

    // Set in:
    //     onToolsCreateASceneList (set when method SceneList.getSceneInfo is called)
    public Integer mIColorMode;
    public int miMode;

    // Initialized in the constructor when it calls initFields
    public Integer mIOutputRows = 0, mIOutputColumns = 0;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Modified in:
    //     onToolsCreateCutout - flips the value
    //     onToolsSampleImage - set to false
    // Read in:
    //     ImageView.onInitialUpdate - where it sets ImageView.mbCutoutEnabled
    //     ImageView.onLButtonDown
    public boolean mbCutoutEnabled;         // Menu control variables

    // Changed from int to boolean
    // Initialized to false in initFields
    // Determines whether the Preview|Still menu item is enabled or not
    //
    // Modified in:
    //     onToolsCreateASceneList - set to false at end of method
    public boolean mbPreviewSceneEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Determines whether the Preview|Sequence menu item is enabled or not.
    //
    // Modified in:
    //     onToolsCreateASceneList - set to true or false depending on value of effectType
    public boolean mbPreviewSequenceEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Determines whether the Render|Still menu item is enabled or not.
    //
    // Modified in:
    //     onPreviewStillScene - set to true at end of method
    //     onRenderStillScene - set to false at end of method
    //     onToolsCreateASceneList - set to false at end of method
    public boolean mbRenderSceneEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Determines whether the Render|Sequence menu item is enabled or not.
    //
    // Modified in:
    //     onPreviewSequenceEnabled - set to true at end of method
    //     onRenderSequence - set to false at end of method
    //     onToolsCreateASceneList - set to false at end of method
    public boolean mbRenderSequenceEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Read in:
    //     ImageView.onLButtonDown
    //     ImageView.onRButtonDown
    // Modified in:
    //     onToolsRemoveSampleColors - toggled from true to false or false to true
    public boolean mbRemoveSampleColorsEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // This field controls whether SceneList.depthSort will call Globals.insertionSort2
    // or not. This happens when either MainFrame.onRenderScene or MainFrame.onRenderSequence
    // call SceneList.render, which in turn calls SceneList.depthSort.
    //
    // Read in:
    //     onRenderStillScene - passed as a parameter to SceneList.render
    //     onRenderSequence - passed as a parameter to SceneList.render
    // Modified in:
    //     onRenderDepthSorting - toggled from true to false or false to true
    public boolean mbDepthSortingEnabled;

    // Changed from int to boolean
    // Initialized to true in initFields
    // Read in:
    //     onRenderStillScene - passed as a parameter to SceneList.render
    //     onRenderSequence - passed as a parameter to SceneList.render
    //     onRenderHazeFog - controls toggling of hazeFogEnabled
    // Modified in:
    //     onRenderZBuffer - toggled from true to false or false to true
    public boolean mbZBufferEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Read in:
    //     ImageView.onLButtonDown
    // Modified in:
    //     onToolsSampleImage - toggled from true to false or false to true
    public boolean mbImageSamplingEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    public boolean mbMotionBlurEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Read in:
    //     onRenderStillScene - passed as parameter to SceneList.render
    //     onRenderSequence - passed as parameter to SceneList.render
    // Modified in:
    //     onRenderHazeFog - toggled from true to false or false to true
    public boolean mbHazeFogEnabled;

    // Changed from int to boolean
    // Initialized to false in initFields
    // Read in:
    //     onRenderStillScene - passed as a parameter to SceneList.render
    //     onRenderSequence - passed as a parameter to SceneList.render
    //     onWarpParamDlgClosed - controls whether to call Global.antiAlias() or not
    // Modified in:
    //     onRenderAntiAlias - flips the value of the variable (from true to false or false to true)
    public boolean mbAntiAliasEnabled;

    // 1 if the scene is being previewed
    // Changed from int to boolean
    // Initialized to false in the constructor when it calls method initFields.
    // Modified in: 
    //     onPreviewStillScene (set to true at top of method, and to false at bottom of method)
    // Read in:
    //     ImageView.onDraw
    public boolean mbPreviewingScene;

    // 1 if sequence is being previewed
    // Changed from int to boolean
    // Initialized to false in the constructor when it calls method initFields.
    // Modified in: 
    //     onPreviewSequenceScene (set to true at the beginning of the method, and false at the end of the method)
    //     onPreviewStillScene (set to true t the beginning of the method, and false at the end of the method)
    public boolean mbPreviewingSequence;

    // 1 if the ViewPoint is being previewed
    // Changed from int to boolean
    // Initialized to false in initFields
    // Modified in: 
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onInitDialog
    //     ScenePreviewDlg.onChkMoveViewPoint
    // Read in:
    //     ScenePreviewDlg.onCmdPlus
    //     ScenePreviewDlg.onCmdReset
    public boolean mbChangeViewPoint;

    // Linked List containing scene description
    // Initialized in method initFields
    // Read in: 
    //     onRenderSequence - used to call method SceneList.render
    //     onRenderStillSequence - used to call method SceneList.render
    //     onToolsCreateASceneList - used to call SceneList methods listLength(), clear(), getSceneInfo() and getViewTransform(). Also passed as parameter to ScnFileParser ctor
    //     ImageView.onDraw - used to call SceneList methods previewStill() and preview()
    //     ScenePreviewDlg.chooseModel - used to call SceneList methods setCurrentModel() and getCurrentModelTransform()
    //     ScenePreviewDlg.onCmdPlus - used to call SceneList method setCurrentModelTransform
    //     ScenePreviewDlg.onInitDialog - used to call SceneList.showModels()
    //     ScenePreviewDlg.onOK - used to call SceneList methods setViewTransform() and writeList()
    //     ScenePreviewDlg.onSelChangeCmbModels - used to call SceneList methods setCurrentModel(),  getCurrentModelTransform() 
    //     Shape3d constructor, the one that takes 2 parameters, a String and an int
    public SceneList mSceneList;

    // Contains a model transformation
    // Initialized in method initFields
    // Read in:
    //     ImageView.onDraw, passed as parameter to SceneList.previewStill and SceneList.preview
    public TMatrix mModelMatrix;

    // Contains viewpoint transformation
    // Initialized in the constructor when it calls initFields
    // Used in method getViewMatrix
    public TMatrix mViewMatrix;

    // The image window into which the scene preview display is drawn
    // Initialized to null in method initFields
    // Modified in:
    //     onPreviewSequence
    //     onPreviewStillScene
    //     onRenderSequence
    //     onRenderStillScene
    // Read in:
    //     ScenePreviewDlg.onCmdPlus
    public ImageView mPreviewWindowHandle;	 

    // These two Preview menu items are fields because
    // they can be enabled or disabled, via which type of SceneList
    // the user opens
    private JMenuItem prvwMimStill;
    private JMenuItem prvwMimSequence;

    // These two Render menu items are fields because
    // they can be enabled or disabled, via which type of SceneList
    // the user has previewed.
    private JMenuItem rndrMimStill;
    private JMenuItem rndrMimSequence;

    public MainFrame(IctApp ictApp) {
        this.ictApp = ictApp;

        initFields();

        // Initialize the gPipe object for VRML rendering
        // TODO: Uncomment the following line when GPipe is completed
        // Globals.aGraphicPipe.initialize();

        setLayout(new BorderLayout());

        // Create the status bar panel and shove it down the bottom of the frame
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        JLabel statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        // Pass statusLabel to the Globals class so that Globals.statusPrint will work
        Globals.setLblStatus(statusLabel);

        JMenuBar menuBar = new JMenuBar();
        createMenu(menuBar);
        setJMenuBar(menuBar);

        setVisible(true);
    } // MainFrame ctor


    // Called from:
    //     Constructor
    private void initFields() {
        this.mWarpRotateX = 0.0f; 
        this.mWarpRotateY = 0.0f; 
        this.mWarpRotateZ = 0.0f;

        this.mWarpTranslateX = 0.0f; 
        this.mWarpTranslateY = 0.0f; 
        this.mWarpTranslateZ = 0.0f;

        this.mWarpScaleX = 1.0f; 
        this.mWarpScaleY = 1.0f; 
        this.mWarpScaleZ = 1.0f;

        this.mViewRotateX = 0.0f; 
        this.mViewRotateY = 0.0f; 
        this.mViewRotateZ = 0.0f;

        this.mViewTranslateX = 0.0f; 
        this.mViewTranslateY = 0.0f; 
        this.mViewTranslateZ = 0.0f;

        this.mbIsDirty = false;
        this.mbCutoutEnabled = false;
        this.mbPreviewSceneEnabled = false;    // Preview|Still menu item is disabled
        this.mbPreviewSequenceEnabled = false; // Preview|Sequene menu item is disabled
        this.mbRenderSceneEnabled = false;     // Render|Still menu item is disabled
        this.mbRenderSequenceEnabled = false;  // Render|Sequence menu item is disabled
        this.mbRemoveSampleColorsEnabled = false;
        this.mbDepthSortingEnabled = false;
        this.mbMotionBlurEnabled = false;
        this.mbHazeFogEnabled = false;
        this.mbAntiAliasEnabled = false;
        this.mbZBufferEnabled = true;
        this.mbImageSamplingEnabled = false;
        this.mbPreviewingScene = false;
        this.mbPreviewingSequence = false;
        this.mIOutputRows = 250;  // Set these in case a SceneList is not read in
        this.mIOutputColumns = 250;
        this.mbChangeViewPoint = false;
        // TODO: Uncomment the following line when SceneList is completed
        //this.mSceneList = new SceneList();
        this.mViewMatrix = new TMatrix();
        this.mModelMatrix = new TMatrix();
        this.mPreviewWindowHandle = null;  // The scene preview window handle
    } // initFields


    // Called from:
    //     MainFrame constructor
    private void createMenu(JMenuBar pMenuBar) {
        JMenu mnuFile    = new JMenu("File");    // POPUP "&File"
        JMenu mnuEdit    = new JMenu("Edit");    // POPUP "&Edit"
        JMenu mnuSearch  = new JMenu("Search");  // POPUP "Search"
        JMenu mnuTools   = new JMenu("Tools");   // POPUP "Tools"
        JMenu mnuPreview = new JMenu("Preview"); // POPUP "Preview"
        JMenu mnuRender  = new JMenu("Render");  // POPUP "Render"
        JMenu mnuView    = new JMenu("View");    // POPUP "&View"
        JMenu mnuWindow  = new JMenu("Window");  // POPUP "&Window"
        JMenu mnuHelp    = new JMenu("Help");    // POPUP "&Help"

        createFileMenu(mnuFile);
        createEditMenu(mnuEdit);
        createSearchMenu(mnuSearch);
        createToolsMenu(mnuTools);
        createPreviewMenu(mnuPreview);
        createRenderMenu(mnuRender);
        createViewMenu(mnuView);
        createWindowMenu(mnuWindow);
        createHelpMenu(mnuHelp);

        pMenuBar.add(mnuFile);
        pMenuBar.add(mnuEdit);
        pMenuBar.add(mnuSearch);
        pMenuBar.add(mnuTools);
        pMenuBar.add(mnuPreview);
        pMenuBar.add(mnuRender);
        pMenuBar.add(mnuView);
        pMenuBar.add(mnuWindow);
        pMenuBar.add(mnuHelp);
    } // createMenu


    // Called from:
    //     createMenu
    private void createFileMenu(JMenu pFileMenu) {
/*
POPUP "&File"
    BEGIN
        MENUITEM "&New Scene\tCtrl+N",          ID_FILE_NEW
        MENUITEM "&Open Scene...\tCtrl+O",      ID_FILE_OPEN
        MENUITEM "&Close Scene",                ID_FILE_CLOSE
        MENUITEM "&Save Scene\tCtrl+S",         ID_FILE_SAVE
        MENUITEM "Save Scene &As...",           ID_FILE_SAVE_AS
        MENUITEM SEPARATOR
        MENUITEM "Open Image",                  ID_FILE_OPENIMAGE
        MENUITEM SEPARATOR
        MENUITEM "Open ICT Log",                ID_FILE_OPENICTLOG
        MENUITEM SEPARATOR
        MENUITEM "Recent File",                 ID_FILE_MRU_FILE1, GRAYED
        MENUITEM SEPARATOR
        MENUITEM "E&xit",                       ID_APP_EXIT
    END
 */
        JMenuItem mimNew         = new JMenuItem("New Scene",        KeyEvent.VK_N);  // "&New Scene\tCtrl+N"
        JMenuItem mimOpenScene   = new JMenuItem("Open Scene...",    KeyEvent.VK_O);  // "&Open Scene...\tCtrl+O"
        JMenuItem mimCloseScene  = new JMenuItem("Close Scene",      KeyEvent.VK_C);  // "&Close Scene"
        JMenuItem mimSaveScene   = new JMenuItem("Save Scene",       KeyEvent.VK_S);  // "&Save Scene\tCtrl+S"
        JMenuItem mimSaveSceneAs = new JMenuItem("Save Scene As...", KeyEvent.VK_A);  // "Save Scene &As..."
        // MENUITEM SEPARATOR
        JMenuItem mimOpenImage   = new JMenuItem("Open Image");
        // MENUITEM SEPARATOR
        JMenuItem mimOpenIctLog  = new JMenuItem("Open ICT Log");
        // MENUITEM SEPARATOR
        JMenuItem mimRecentFile  = new JMenuItem("Recent File");
        // MENUITEM SEPARATOR
        JMenuItem mimExit        = new JMenuItem("Exit", KeyEvent.VK_X); // "E&xit"

        pFileMenu.add(mimNew);
        pFileMenu.add(mimOpenScene);
        pFileMenu.add(mimCloseScene);
        pFileMenu.add(mimSaveScene);
        pFileMenu.add(mimSaveSceneAs);
        pFileMenu.addSeparator();
        pFileMenu.add(mimOpenImage);
        pFileMenu.addSeparator();
        pFileMenu.add(mimOpenIctLog);
        pFileMenu.addSeparator();
        pFileMenu.add(mimRecentFile);
        pFileMenu.addSeparator();
        pFileMenu.add(mimExit);
    } // createFileMenu


    // Called from:
    //     createMenu
    private void createEditMenu(JMenu pEditMenu) {
        /*
        POPUP "&Edit"
        BEGIN
            MENUITEM "&Undo\tCtrl+Z",               ID_EDIT_UNDO
            MENUITEM SEPARATOR
            MENUITEM "Cu&t\tCtrl+X",                ID_EDIT_CUT
            MENUITEM "&Copy\tCtrl+C",               ID_EDIT_COPY
            MENUITEM "&Paste\tCtrl+V",              ID_EDIT_PASTE
        END
        */

        JMenuItem mimUndo  = new JMenuItem("Undo",  KeyEvent.VK_U);  // "&Undo\tCtrl+Z"
        // MENUITEM SEPARATOR
        JMenuItem mimCut   = new JMenuItem("Cut",   KeyEvent.VK_T);  // "Cu&t\tCtrl+X"
        JMenuItem mimCopy  = new JMenuItem("Copy",  KeyEvent.VK_C);  // "&Copy\tCtrl+C"
        JMenuItem mimPaste = new JMenuItem("Paste", KeyEvent.VK_P);  // "&Paste\tCtrl+V"

        pEditMenu.add(mimUndo);
        pEditMenu.addSeparator();
        pEditMenu.add(mimCut);
        pEditMenu.add(mimCopy);
        pEditMenu.add(mimPaste);
    } // createEditMenu


    // Called from:
    //     createMenu
    private void createSearchMenu(JMenu pSearchMenu) {
/* 
    POPUP "Search"
    BEGIN
        MENUITEM "Find...",                     ID_SEARCH_FIND
        MENUITEM "Replace...",                  ID_SEARCH_REPLACE
        MENUITEM "Next",                        ID_SEARCH_NEXT
    END
*/

        JMenuItem mimFind    = new JMenuItem("Find...");
        JMenuItem mimReplace = new JMenuItem("Replace...");
        JMenuItem mimNext    = new JMenuItem("Next");

        pSearchMenu.add(mimFind);
        pSearchMenu.add(mimReplace);
        pSearchMenu.add(mimNext);
    } // createSearchMenu


    // Called from:
    //     createMenu
    private void createToolsMenu(JMenu pToolsMenu) {
/*
POPUP "Tools"
    BEGIN
        MENUITEM "Create a Scene List...",      ID_TOOLS_CREATEASCENELIST
        MENUITEM SEPARATOR
        MENUITEM "Create Texture Image...",     ID_TOOLS_CREATETEXTUREIMAGE
        , HELP
        MENUITEM "Create a Mesh Model...",      ID_TOOLS_CREATEMESH
        MENUITEM SEPARATOR
        MENUITEM "Create Cutout",               ID_TOOLS_CREATECUTOUT
        MENUITEM "Create Alpha Image...",       ID_TOOLS_CREATEALPHAIMAGE
        MENUITEM SEPARATOR
        MENUITEM "Warp Image...",               ID_TOOLS_WARPIMAGE
        MENUITEM SEPARATOR
        MENUITEM "Sample Image",                ID_TOOLS_SAMPLEIMAGE
        , INACTIVE
        MENUITEM "Remove Sampled Colors",       ID_TOOLS_REMOVESAMPLEDCOLORS
        MENUITEM SEPARATOR
        MENUITEM "Motion Blur...",              ID_TOOLS_MOTIONBLUR
        MENUITEM SEPARATOR
        MENUITEM "Create a Morph Sequence...",  ID_TOOLS_MORPHSEQUENCE
        MENUITEM SEPARATOR
        MENUITEM "Render VRML File...",         ID_TOOLS_RENDERVRMLFILE
        MENUITEM SEPARATOR
        MENUITEM "Test",                        ID_TOOLS_TEST
    END
 */

        JMenuItem mimCrtASceneList     = new JMenuItem("Create a Scene List...");
        // MENUITEM SEPARATOR
        JMenuItem mimCrtTextureImage   = new JMenuItem("Create Texture Image...");
        JMenuItem mimCrtAMeshModel     = new JMenuItem("Create a Mesh Model...");
        // MENUITEM SEPARATOR
        JCheckBoxMenuItem mimCrtCutout = new JCheckBoxMenuItem("Create Cutout");
        JMenuItem mimCrtAlphaImage     = new JMenuItem("Create Alpha Image...");
        // MENUITEM SEPARATOR
        JMenuItem mimWarpImage         = new JMenuItem("Warp Image...");
        // MENUITEM SEPARATOR
        JCheckBoxMenuItem mimSampleImage = new JCheckBoxMenuItem("Sample Image");
        JCheckBoxMenuItem mimRemoveSampledColors = new JCheckBoxMenuItem("Remove Sampled Colors");
        // MENUITEM SEPARATOR
        JMenuItem mimMotionBlur        = new JMenuItem("Motion Blur...");
        // MENUITEM SEPARATOR
        JMenuItem mimCrtAMorphSequence = new JMenuItem("Create a Morph Sequence...");
        // MENUITEM SEPARATOR
        JMenuItem mimRenderVRMLFile    = new JMenuItem("Render VRML File...");
        // MENUITEM SEPARATOR
        JMenuItem mimTest              = new JMenuItem("Test");

        mimCrtASceneList.setActionCommand("Tools|Create a Scene List...");
        mimCrtASceneList.addActionListener(this);
        
        mimCrtTextureImage.setActionCommand("Tools|Create Texture Image...");
        mimCrtTextureImage.addActionListener(this);

        mimCrtAMeshModel.setActionCommand("Tools|Create a Mesh Model...");
        mimCrtAMeshModel.addActionListener(this);

        mimCrtCutout.setActionCommand("Tools|Create Cutout");
        mimCrtCutout.addActionListener(this);

        mimCrtAlphaImage.setActionCommand("Tools|Create Alpha Image...");
        mimCrtAlphaImage.addActionListener(this);

        mimWarpImage.setActionCommand("Tools|Warp Image...");
        mimWarpImage.addActionListener(this);

        mimSampleImage.setActionCommand("Tools|Sample Image");
        mimSampleImage.addActionListener(this);

        mimRemoveSampledColors.setActionCommand("Tools|Remove Sampled Colors");
        mimRemoveSampledColors.addActionListener(this);

        mimMotionBlur.setActionCommand("Tools|Motion Blur...");
        mimMotionBlur.addActionListener(this);

        mimCrtAMorphSequence.setActionCommand("Tools|Create a Morph Sequence...");
        mimCrtAMorphSequence.addActionListener(this);

        mimRenderVRMLFile.setActionCommand("Tools|Render VRML File...");
        mimRenderVRMLFile.addActionListener(this);

        mimTest.setActionCommand("Tools|Test");
        mimTest.addActionListener(this);

        pToolsMenu.add(mimCrtASceneList);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimCrtTextureImage);
        pToolsMenu.add(mimCrtAMeshModel);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimCrtCutout);
        pToolsMenu.add(mimCrtAlphaImage);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimWarpImage);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimSampleImage);
        pToolsMenu.add(mimRemoveSampledColors);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimMotionBlur);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimCrtAMorphSequence);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimRenderVRMLFile);
        pToolsMenu.addSeparator();
        pToolsMenu.add(mimTest);
    }  // createToolsMenu


    // Called from:
    //     createMenu
    private void createPreviewMenu(JMenu pPreviewMenu) {
/*
    POPUP "Preview"
    BEGIN
        MENUITEM "Still",                       ID_PREVIEW_SCENE, GRAYED
        MENUITEM "Sequence",                    ID_PREVIEW_SEQUENCE, GRAYED
    END
 */

        prvwMimStill    = new JMenuItem("Still");
        prvwMimStill.setEnabled(mbPreviewSceneEnabled);

        prvwMimSequence = new JMenuItem("Sequence");
        prvwMimSequence.setEnabled(mbPreviewSequenceEnabled);

        prvwMimStill.setActionCommand("Preview|Still");
        prvwMimStill.addActionListener(this);

        prvwMimSequence.setActionCommand("Preview|Sequence");
        prvwMimSequence.addActionListener(this);

        pPreviewMenu.add(prvwMimStill);
        pPreviewMenu.add(prvwMimSequence);
    } // createPreviewMenu


    private void updatePreviewMenu() {
        prvwMimStill.setEnabled(mbPreviewSceneEnabled);
        prvwMimSequence.setEnabled(mbPreviewSequenceEnabled);
    }

    // Called from:
    //     createMenu
    private void createRenderMenu(JMenu pRenderMenu) {
/*
    POPUP "Render"
    BEGIN
        MENUITEM "Still",                       ID_RENDER_SCENE, GRAYED
        MENUITEM "Sequence",                    ID_RENDER_SEQUENCE, GRAYED
        MENUITEM SEPARATOR
        MENUITEM "Z Buffer",                    ID_RENDER_ZBUFFER, CHECKED
        MENUITEM "Depth Sorting",               ID_RENDER_DEPTHSORTING
        MENUITEM SEPARATOR
        MENUITEM "Anti-Alias",                  ID_RENDER_ANTIALIAS
    END
 */

        rndrMimStill                      = new JMenuItem("Still");
        rndrMimStill.setEnabled(mbRenderSceneEnabled);

        rndrMimSequence                   = new JMenuItem("Sequence");
        rndrMimSequence.setEnabled(mbRenderSequenceEnabled);

        // MENUITEM SEPARATOR
        JCheckBoxMenuItem mimZBuffer      = new JCheckBoxMenuItem("Z Buffer");
        JCheckBoxMenuItem mimDepthSorting = new JCheckBoxMenuItem("Depth Sorting");
        // MENUITEM SEPARATOR
        JCheckBoxMenuItem mimAntiAlias    = new JCheckBoxMenuItem("Anti-Alias");

        rndrMimStill.setActionCommand("Render|Still");
        rndrMimStill.addActionListener(this);

        rndrMimSequence.setActionCommand("Render|Sequence");
        rndrMimSequence.addActionListener(this);

        mimZBuffer.setActionCommand("Render|Z Buffer");
        mimZBuffer.addActionListener(this);

        mimDepthSorting.setActionCommand("Render|Depth Sorting");
        mimDepthSorting.addActionListener(this);

        mimAntiAlias.setActionCommand("Render|Anti-Alias");
        mimAntiAlias.addActionListener(this);

        pRenderMenu.add(rndrMimStill);
        pRenderMenu.add(rndrMimSequence);
        pRenderMenu.addSeparator();
        pRenderMenu.add(mimZBuffer);
        pRenderMenu.add(mimDepthSorting);
        pRenderMenu.addSeparator();
        pRenderMenu.add(mimAntiAlias);
    } // createRenderMenu

    private void updateRenderMenu() {
        rndrMimStill.setEnabled(mbPreviewSceneEnabled);
        rndrMimSequence.setEnabled(mbPreviewSequenceEnabled);
    }

    // Called from:
    //     createMenu
    private void createViewMenu(JMenu pViewMenu) {
/*
    POPUP "&View"
    BEGIN
        MENUITEM "&Status Bar",                 ID_VIEW_STATUS_BAR
    END
 */
        JMenuItem mimStatusBar = new JMenuItem("Status Bar", KeyEvent.VK_S); // "&Status Bar"

        mimStatusBar.addActionListener(this);

        pViewMenu.add(mimStatusBar);
    } // createViewMenu


    // Called from:
    //     createMenu
    private void createWindowMenu(JMenu pWindowMenu) {
/*
    POPUP "&Window"
    BEGIN
        MENUITEM "&New Window",                 ID_WINDOW_NEW
        MENUITEM "&Cascade",                    ID_WINDOW_CASCADE
        MENUITEM "&Tile",                       ID_WINDOW_TILE_HORZ
        MENUITEM "&Arrange Icons",              ID_WINDOW_ARRANGE
    END
 */

        JMenuItem mimNewWindow    = new JMenuItem("New Window",    KeyEvent.VK_N);  // "&New Window"
        JMenuItem mimCascade      = new JMenuItem("Cascade",       KeyEvent.VK_C);  // "&Cascade"
        JMenuItem mimTile         = new JMenuItem("Tile",          KeyEvent.VK_T);  // "&Tile"
        JMenuItem mimArrangeIcons = new JMenuItem("Arrange Icons", KeyEvent.VK_A);  // "&Arrange Icons"

        mimNewWindow.addActionListener(this);
        mimCascade.addActionListener(this);
        mimTile.addActionListener(this);
        mimArrangeIcons.addActionListener(this);

        pWindowMenu.add(mimNewWindow);
        pWindowMenu.add(mimCascade);
        pWindowMenu.add(mimTile);
        pWindowMenu.add(mimArrangeIcons);
    } // createWindowMenu


    // Called from:
    //     createMenu
    private void createHelpMenu(JMenu pHelpMenu) {
/*
    POPUP "&Help"
    BEGIN
        MENUITEM "&About ICT 2.0...",           ID_APP_ABOUT
    END
 */

        JMenuItem mimAbout = new JMenuItem("About JICT 2.0...", KeyEvent.VK_A); // "&About ICT 2.0..."

        mimAbout.setActionCommand("Help|About JICT 2.0...");
        mimAbout.addActionListener(this);

        pHelpMenu.add(mimAbout);
    } // createHelpMenu


    public void finalize() {

    } // finalize


/*
    int onCreate(LPCREATESTRUCT lpCreateStruct) {
        if (CMDIFrameWnd.OnCreate(lpCreateStruct) == -1) {
            return -1;
        }

        return 0;
    } // onCreate
*/

    public void actionPerformed(ActionEvent ae) {
        String sActionCmd = ae.getActionCommand();

        // Was it a File menu item?
/*
        MENUITEM "&New Scene\tCtrl+N",          ID_FILE_NEW
        MENUITEM "&Open Scene...\tCtrl+O",      ID_FILE_OPEN
        MENUITEM "&Close Scene",                ID_FILE_CLOSE
        MENUITEM "&Save Scene\tCtrl+S",         ID_FILE_SAVE
        MENUITEM "Save Scene &As...",           ID_FILE_SAVE_AS
        MENUITEM "Open Image",                  ID_FILE_OPENIMAGE
        MENUITEM "Open ICT Log",                ID_FILE_OPENICTLOG
        MENUITEM "Recent File",                 ID_FILE_MRU_FILE1, GRAYED
        MENUITEM "E&xit",                       ID_APP_EXIT
 */
        // Open Image menu item from the File menu
        // onFileOpenImage

        // Open ICT Log menu item from the File menu
        // onFileOpenIctLog

        // Open Scene menu item from the File menu
        // onFileOpen
        
        // Was it an Edit menu item?

        // Was it a Tools menu item?
        if (sActionCmd.startsWith("Tools|")) {
            if (sActionCmd.equals("Tools|Create a Scene List...")) {
                onToolsCreateASceneList();
                return;
            } else if (sActionCmd.equals("Tools|Create Texture Image...")) {
                onToolsCreateTextureImage();
                return;
            } else if (sActionCmd.equals("Tools|Create a Mesh Model...")) {
                onToolsCreateMesh();
                return;
            } else if (sActionCmd.equals("Tools|Create Cutout")) {
                onToolsCreateCutout();
            } else if (sActionCmd.equals("Tools|Create Alpha Image...")) {
                onToolsCreateAlphaImage();
                return;
            } else if (sActionCmd.equals("Tools|Warp Image...")) {
                onToolsWarpImage();
                return;
            } else if (sActionCmd.equals("Tools|Sample Image")) {
                onToolsSampleImage();
                return;
            } else if (sActionCmd.equals("Tools|Remove Sampled Colors")) {
                onToolsRemoveSampleColors();
                return;
            } else if (sActionCmd.equals("Tools|Motion Blur...")) {
                onToolsMotionBlur();
                return;
            } else if (sActionCmd.equals("Tools|Create a Morph Sequence...")) {
                onToolsMorphSequence();
                return;
            } else if (sActionCmd.equals("Tools|Render VRML File...")) {
                onToolsRenderVrmlFile();
                return;
            } else if (sActionCmd.equals("Tools|Test")) {
                onToolsTest();
                return;
            }
        } else if (sActionCmd.startsWith("Preview|")) { // Was it a Preview menu item?
            if (sActionCmd.equals("Preview|Still")) {
                onPreviewStillScene();
                return;
            } else if (sActionCmd.equals("Preview|Sequence")) {
                onPreviewSequenceScene();
                return;
            }
        } else if (sActionCmd.startsWith("Render|")) { // Was it a Render menu item?
            if (sActionCmd.equals("Render|Still")) {
                onRenderStillScene();
                return;
            } else if (sActionCmd.equals("Render|Sequence")) {
                onRenderSequence();
                return;
            } else if (sActionCmd.equals("Render|Z Buffer")) {
                onRenderZBuffer();
                return;
            } else if (sActionCmd.equals("Render|Depth Sorting")) {
                onRenderDepthSorting();
                return;
            } else if (sActionCmd.equals("Render|Anti-Alias")) {
                onRenderAntiAlias();
                return;
            }
        }

        // Was it a View menu item?

        // Was it a Help menu item?
        if (sActionCmd.equals("Help|About ICT 2.0...")) {
            onAppAbout();
            return;
        }
    } // actionPerformed

    // ############################################################################################
    // Event handlers for File menu

    // Called when the user selects the Open Image menu item from the File menu
    // MENUITEM "Open Image",                  ID_FILE_OPENIMAGE
    // ON_COMMAND(ID_FILE_OPENIMAGE, OnFileOpenimage)
    public void onFileOpenImage() {
        // Get the Output Image Directory
        String sOutputImageDirectory = Globals.ictPreference.getPath(Preference.OutputImageDirectory);
        File currDir = new File(sOutputImageDirectory);

        JFileChooser dlg = new JFileChooser();
        // Change to the default output image directory (indicated in the ICT preference object)
        dlg.setCurrentDirectory(currDir);
        // The user can only choose a file (no directories)
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // The user can only choose files with the '.bmp' extension
        dlg.setFileFilter(new BMPFileFilter());
        
        // Allow the user to select a '.bmp' file
        int showDlgResult = dlg.showDialog(null, "Select image name");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            // m_pDocTemplateImage.OpenDocumentFile(dlg.getSelectedFile().getName());
        }
    } // onFileOpenImage


    // Called when the user selects the Open ICT Log menu item from the File menu
    // MENUITEM "Open ICT Log",                ID_FILE_OPENICTLOG
    // ON_COMMAND(ID_FILE_OPENICTLOG, OnFileOpenictlog)
    public void onFileOpenIctLog() {
        String processLogPath;

        processLogPath = Globals.ictPreference.getPath(Preference.ProcessLog);
        // m_pDocTemplateText.OpenDocumentFile(processLogPath);
    } // onFileOpenIctLog


    // Called when the user selects the Open Scene menu item from the File menu
    // MENUITEM "&Open Scene...\tCtrl+O",      ID_FILE_OPEN
    // ON_COMMAND(ID_FILE_OPEN, OnFileOpen)
    public void onFileOpen() {
        // Get the Scene File Directory
        String sScnFileDirectory = Globals.ictPreference.getPath(Preference.SceneFileDirectory);
        File currDir = new File(sScnFileDirectory);

        JFileChooser dlg = new JFileChooser();
        // Change to the default scene file directory (indicated in the ICT preference object)
        dlg.setCurrentDirectory(currDir);
        // The user can only choose a file (no directories)
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // The user can only choose files with the '.scn' extension
        dlg.setFileFilter(new SCNFileFilter());

        // Allow the user to choose a '.scn' file
        int showDlgResult = dlg.showDialog(null, "Select scene file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            // m_pDocTemplateText.OpenDocumentFile(dlg.getSelectedFile().getName());
        }
    } // onFileOpen

    // ############################################################################################
    // Event handlers for Tools menu

    // Called when the user selects the "Create a Scene List..." menu item from the Tools menu.
    // MENUITEM "Create a Scene List...",      ID_TOOLS_CREATEASCENELIST
    // ON_COMMAND(ID_TOOLS_CREATEASCENELIST, OnToolsCreateascenelist)
    public void onToolsCreateASceneList() {
        String aFileName = "";
        String msgText;
        int myStatus;
        
        closeAllChildren();
        if(mbIsDirty) {         // If the client window has been drawn on, erase it
            mbIsDirty = false;
            repaint();
        }

        if(mSceneList.listLength() > 0) {
            Globals.statusPrint("Clearing Previous Scene List...");
            mSceneList.clear();
        }

        // Display standard Open dialog box to select a file name.
        // Get the Scene File Directory
        String sSceneFileDirectory = Globals.ictPreference.getPath(Preference.SceneFileDirectory);
        File currDir = new File(sSceneFileDirectory);

        JFileChooser dlg = new JFileChooser();
        // Change to the default scene file directory (indicated in the ICT preference object)
        dlg.setCurrentDirectory(currDir);
        // The user can only choose a file (no directories)
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // The user can only choose files with the '.scn' extension
        dlg.setFileFilter(new SCNFileFilter());

        // Allow the user to choose a '.scn' file
        int showDlgResult = dlg.showDialog(this, "Select scn file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            aFileName = dlg.getSelectedFile().getName();
        }

        msgText = "Reading Scene List: " + aFileName;
        Globals.statusPrint(msgText);

        // Create an instance of a class that can parse .scn files
        ScnFileParser parser = new ScnFileParser(mSceneList);

        // Have it parse the .scn file aFileName
        myStatus = parser.readList(msgText, aFileName);
        Globals.statusPrint(msgText);

        if(myStatus != 0) {
            mSceneList.clear();
            return;
        }

        msSceneFileName = dlg.getSelectedFile().getName();  // save the file name

        // Load the scene information into the client object
        // The following method sets fields msSceneName, mIEffectType, mIColorMode, 
        // mIOutputRows and mIOutputColumns
        // Class SceneList stored the information after ScnFileParser parsed file aFileName
        mSceneList.getSceneInfo(msSceneName, mIEffectType, mIColorMode, 
            mIOutputRows, mIOutputColumns);

        // The following method sets fields mViewTranslateX, mViewTranslateY, mViewTranslateZ, 
        // mViewRotateX, mViewRotateY, and mViewRotateZ
        mSceneList.getViewTransform(mViewTranslateX, mViewTranslateY, mViewTranslateZ,
            mViewRotateX, mViewRotateY, mViewRotateZ);
        getViewMatrix();

        if((this.mIEffectType == JICTConstants.I_SEQUENCE) || (this.mIEffectType == JICTConstants.I_MORPH)) {
            this.mbPreviewSequenceEnabled = true;
            this.mbPreviewSceneEnabled    = false;
            this.mbRenderSceneEnabled     = false;
            this.mbRenderSequenceEnabled  = false;
        } else {
            this.mbPreviewSceneEnabled    = true;
            this.mbPreviewSequenceEnabled = false;
            this.mbRenderSceneEnabled     = false;
            this.mbRenderSequenceEnabled  = false;
        }
    } // onToolsCreateASceneList


    // Called when the user selects the "Create Texture Image..." menu item from the Tools menu.
    // MENUITEM "Create Texture Image...",     ID_TOOLS_CREATETEXTUREIMAGE
    // ON_COMMAND(ID_TOOLS_CREATETEXTUREIMAGE, OnToolsCreatetextureimage)
    public void onToolsCreateTextureImage() {
        MakeTextureDlg dlg = new MakeTextureDlg(this, true);
        dlg.setVisible(true);	
    } // onToolsCreateTextureImage


    // Called when the user selects the "Create a Mesh Model..." menu item from the Tools menu.
    // MENUITEM "Create a Mesh Model...",      ID_TOOLS_CREATEMESH
    // ON_COMMAND(ID_TOOLS_CREATEMESH, OnToolsCreatemesh)
    public void onToolsCreateMesh() { 
        // Quad mesh model creation occurs in the dialog box
        // member functions.
        QuadMeshDlg dlg = new QuadMeshDlg(this, true);
        dlg.setVisible(true);
        // dlg.DoModal();	
    } // onToolsCreateMesh


    // Called when the user selects the "Create Cutout" menu item from the Tools menu.
    // MENUITEM "Create Cutout",               ID_TOOLS_CREATECUTOUT
    // ON_COMMAND(ID_TOOLS_CREATECUTOUT, OnToolsCreatecutout)
    public void onToolsCreateCutout() {
        closeAllChildren();
        if(mbIsDirty) {         // If the client window has been drawn on, erase it
            mbIsDirty = false;
            repaint();
        }
        
        mbCutoutEnabled = !mbCutoutEnabled;
    } // onToolsCreateCutout

/*
    // ON_UPDATE_COMMAND_UI(ID_TOOLS_CREATECUTOUT, OnUpdateToolsCreatecutout)
    public void onUpdateToolsCreateCutout(CCmdUI pCmdUI) {
        pCmdUI.SetCheck(cutoutEnabled); 
    } // onUpdateToolsCreateCutout
*/

    // Called when the user selects the "Create Alpha Image..." menu item from the Tools menu.
    // MENUITEM "Create Alpha Image...",       ID_TOOLS_CREATEALPHAIMAGE
    // ON_COMMAND(ID_TOOLS_CREATEALPHAIMAGE, OnToolsCreatealphaimage)
    public void onToolsCreateAlphaImage() {
        String sMsgText;

        Globals.statusPrint("Creating an Alpha-Channel Image");
        if(mbIsDirty) {         // If the client window has been drawn on, erase it
            mbIsDirty = false;
            repaint();
        }

        closeAllChildren();
        String sFileName;

        JFileChooser dlg = new JFileChooser();
        // The user can only choose a file (no directories)
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // The user can only choose files with the '.bmp' extension
        dlg.setFileFilter(new BMPFileFilter());

        // Allow the user to select a '.bmp' file
        int iShowDlgResult = dlg.showDialog(this, "Select bmp file");

        int iStatus;
        Integer imHeight = 0, imWidth = 0, bitsPerPixel = 0;
        String centeredName = "";
        MemImage alphaImage = new MemImage(1, 1);
        MemImage inImage = new MemImage(1, 1);

        if (iShowDlgResult == JFileChooser.APPROVE_OPTION) {
            sFileName = dlg.getSelectedFile().getName();

            // Center the input image by removing any chromacolor border 
            FileUtils.constructPathName(centeredName, sFileName, 'q');
            if(centeredName.equals(sFileName)) {
                Globals.beep(10, 10);
                Globals.statusPrint("onToolsCreateAlphaImage: Centered image name cannot equal original image name");
                return;
            }

            // The following method sets parameters imHeight, imWidth and bitsPerPixel
            iStatus = Globals.readBMPHeader(sFileName, imHeight, imWidth, bitsPerPixel);
            if(bitsPerPixel == 8) {
                inImage = new MemImage(sFileName, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_MONOCHROME);
            }
            if(bitsPerPixel == 24) {
                inImage = new MemImage(sFileName, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
            }

            // Create an alpha image
            String sAlphaName = "";
            FileUtils.constructPathName(sAlphaName, sFileName, 'a');
            if(sAlphaName.equals(sFileName)) {
                Globals.beep(10, 10);
                Globals.statusPrint("onToolsCreateAlphaImage: Alpha image name cannot equal original image name");
                return;
            }
            
            if(inImage.isValid()) {
                alphaImage = new MemImage(inImage.getHeight(), inImage.getWidth());
                inImage.createAlphaImage(alphaImage);
                Globals.statusPrint("Smoothing the Mask Image");
                alphaImage.alphaSmooth5();

                Shape3d aShape = new Shape3d(8192);
                if(!aShape.isValid()) {
                    Globals.statusPrint("Unable to create new shape");
                    return;
                }

                String sShapeFile;
                // String shapeDir, shapeName;
                sShapeFile = sFileName;
                int fileNameLength = sShapeFile.length();
                // Chop off the extension from the shape file name.
                // The extension should be ".shp"
                sShapeFile = sShapeFile.substring(fileNameLength - 4);

                // Generate a shape file from the binary image
                iStatus = Globals.shapeFromImage(alphaImage, aShape);

                if(iStatus == 0) {
                    aShape.writeShape(sShapeFile);
                    sMsgText = "Saved a shape file: " + sShapeFile + "  numVertices: " + aShape.getNumVertices();
                    Globals.statusPrint(sMsgText);
                }

                FileUtils.constructPathName(sAlphaName, sFileName, 'a');
                if(sAlphaName.equals(sFileName)) {
                    Globals.statusPrint("Alpha Image Name cannot equal the original file name");
                    return;
                }

                alphaImage.writeBMP(sAlphaName);
                sMsgText = "Saved an alpha image: " + sAlphaName;
                Globals.statusPrint(sMsgText);
            }
        }
    } // onToolsCreateAlphaImage


    // Called when the user selects the "Warp Image..." menu item from the Tools menu.
    // MENUITEM "Warp Image...",               ID_TOOLS_WARPIMAGE
    // ON_COMMAND(ID_TOOLS_WARPIMAGE, OnToolsWarpimage)
    public void onToolsWarpImage() {
        // The WarpParamDlg dialog displays 5 text boxes where the user can enter 
        // rotation angles (in degrees) for x, y, and z-axes, and 
        // scale factors for x and y axes.
        WarpParamDlg dlg = new WarpParamDlg(this, true);
        dlg.setVisible(true);
    } // onToolsWarpImage


    // Called from:
    //     WarpParamDlg.onOk
    public void onWarpParamDlgClosed() {
        ImageView orgView, outView;
        closeAllChildren();
        
        // Setup Output Image
        //ictApp.m_pDocTemplateImage.OpenDocumentFile(null);
        outView = ImageView.getView();
        outView.setCaption("Warped Image");

        //ictApp.m_pDocTemplateImage.OpenDocumentFile(null);
        orgView = ImageView.getView();
        Integer inHeight = 0, inWidth = 0, bitsPerPixel = 0;
        MemImage inImage = new MemImage(0, 0);

        // The following method sets parameters inHeight, inWidth, and bitsPerPixel
        int iStatus = Globals.readBMPHeader(Globals.ictPreference.getPath(Preference.WarpTestPath), inHeight, inWidth, bitsPerPixel);
        if(bitsPerPixel == 8) {
            inImage = new MemImage(Globals.ictPreference.getPath(Preference.WarpTestPath), 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_MONOCHROME);
        }
        if(bitsPerPixel == 24) {
            inImage = new MemImage(Globals.ictPreference.getPath(Preference.WarpTestPath), 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
        }

        int outHeight = 350;
        int outWidth  = 350;
        if(!inImage.isValid()) {
            Globals.statusPrint("Unable to open warp test image");
            return;
        }
        orgView.setCaption("Original Image");
        orgView.associateMemImage(inImage);

        // MDITile(MDITILE_VERTICAL);

        MemImage outImage   = new MemImage(outHeight, outWidth, bitsPerPixel);
        MemImage aliasImage = new MemImage(outHeight, outWidth, bitsPerPixel);
        // int xOffset, yOffset;
        TMatrix dummyMatrix = new TMatrix();

        // Translate test image to center of output image
        // float xAngle = 0.0f, yAngle = 0.0f, zAngle = 0.0f;

        Globals.iwarpz(inImage, outImage, null, 
            mWarpRotateX, mWarpRotateY, mWarpRotateZ,
            mWarpScaleX,  mWarpScaleY,  mWarpScaleZ, 
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            dummyMatrix,
            0.0f, 0.0f, 0.0f);
        outImage.writeBMP("d:\\ict20\\output\\testwarp.bmp");

        if(mbAntiAliasEnabled) {
            Globals.antiAlias(outImage, aliasImage);
            outView.associateMemImage(aliasImage);
        } else {
            outView.associateMemImage(outImage);
        }
    } // onWarpParamDlgClosed


    // Called when the user selects the "Sample Image" menu item from the Tools menu.
    // MENUITEM "Sample Image",                ID_TOOLS_SAMPLEIMAGE
    // ON_COMMAND(ID_TOOLS_SAMPLEIMAGE, OnToolsSampleimage)
    public void onToolsSampleImage() {
        mbImageSamplingEnabled = !mbImageSamplingEnabled;		
        mbCutoutEnabled = false;
    } // onToolsSampleImage

/*
    // ON_UPDATE_COMMAND_UI(ID_TOOLS_SAMPLEIMAGE, OnUpdateToolsSampleimage)
    public void onUpdateToolsSampleImage(CCmdUI pCmdUI) {
        pCmdUI.SetCheck(imageSamplingEnabled); 
    } // onUpdateToolsSampleImage
*/

    // Called when the user selects the "Remove Sampled Colors" menu item from the Tools menu.
    // MENUITEM "Remove Sampled Colors",       ID_TOOLS_REMOVESAMPLEDCOLORS
    // ON_COMMAND(ID_TOOLS_REMOVESAMPLEDCOLORS, OnToolsRemoveSampleColors)
    public void onToolsRemoveSampleColors() {
        mbRemoveSampleColorsEnabled = !mbRemoveSampleColorsEnabled;
    } // onToolsRemoveSampleColors

/*
    // ON_UPDATE_COMMAND_UI(ID_TOOLS_REMOVESAMPLEDCOLORS, OnUpdateToolsRemoveSampleColors)
    public void onUpdateToolsRemoveSampleColors(CCmdUI pCmdUI) {
        pCmdUI.SetCheck(removeSampleColorsEnabled); 
    } // onUpdateToolsRemoveSampleColors
*/

    // Called when the user selects the "Motion Blur..." menu item from the Tools menu.
    // MENUITEM "Motion Blur...",              ID_TOOLS_MOTIONBLUR
    // ON_COMMAND(ID_TOOLS_MOTIONBLUR, OnToolsMotionblur)
    public void onToolsMotionBlur() {
        MotionBlurDlg dlg = new MotionBlurDlg(this, true);
        dlg.setVisible(true);
    } // onToolsMotionBlur


    // Called when the user selects the "Create a Morph Sequence..." menu item from the Tools menu.
    // MENUITEM "Create a Morph Sequence...",  ID_TOOLS_MORPHSEQUENCE
    // ON_COMMAND(ID_TOOLS_MORPHSEQUENCE, OnToolsMorphSequence)
    public void onToolsMorphSequence() {
        MorphDlg dlg = new MorphDlg(this, true);
        dlg.setVisible(true);
    } // onToolsMorphSequence


    // Called when the user selects the "Render VRML File..." menu item from the Tools menu.
    // MENUITEM "Render VRML File...",         ID_TOOLS_RENDERVRMLFILE
    // ON_COMMAND(ID_TOOLS_RENDERVRMLFILE, OnToolsRenderVrmlFile)
    public void onToolsRenderVrmlFile() {
        String inPath, outPath;

        // Get the VRML File Directory
        String sVRMLDirectory = Globals.ictPreference.getPath(Preference.VRMLDirectory);
        File currDir = new File(sVRMLDirectory);

        JFileChooser dlg = new JFileChooser();
        // Change to the default VRML file directory (indicated in the ICT preference object)
        dlg.setCurrentDirectory(currDir);
        // The user can only choose a file (no directories)
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // The user can only choose files with the '.scn' extension
        dlg.setFileFilter(new WRLFileFilter());

        // Allow the user to choose a '.wrl' file
        int showDlgResult = dlg.showDialog(this, "Select WRL file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            inPath = dlg.getSelectedFile().getName();
            String msgText = "Reading VRML file: " + inPath;
            Globals.statusPrint(msgText);

            outPath = "D:\\ict20\\output\\vrmlImage.bmp";
            VRML.renderVRML(inPath, outPath);
        }
    } // onToolsRenderVrmlFile


    // Called when the user selects the "Test" menu item from the Tools menu.
    // MENUITEM "Test",                        ID_TOOLS_TEST
    // ON_COMMAND(ID_TOOLS_TEST, OnToolsTest)
    public void onToolsTest() {
        String msgText = "";
        Globals.aGraphicPipe.initialize();
        Globals.aGraphicPipe.setZBuffer(true);
        Globals.aGraphicPipe.setLighting(true);

        Point3d aLight = new Point3d();
        aLight.x =    0.0f;
        aLight.y =    0.0f;
        aLight.z = -100.0f;
        Globals.aGraphicPipe.setLightSource(aLight);
        
        int x, y, translationX, translationY;
        int numImages = 10;
        float angleIncrement = 360.0f / (float)(numImages - 1) * JICTConstants.F_DTR;
        int translationIncrement = 500 / numImages;
        translationX = -230;
        translationY = -230;
        Globals.aGraphicPipe.setPenTranslation((float)translationX, (float)translationY, 0.0f);

        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d(); 
        Point3d p4 = new Point3d();
        float fHalfFace = 10.0f;
        p1.x = -fHalfFace;
        p1.y = -fHalfFace;
        p1.z = 0.0f;
        
        p2.x =  fHalfFace;
        p2.y = -fHalfFace;
        p2.z = 0.0f;
        
        p3.x = fHalfFace;
        p3.y = fHalfFace;
        p3.z = 0.0f;
        
        p4.x = -fHalfFace;
        p4.y =  fHalfFace;
        p4.z = 0.0f;

        for(y = 0; y < 360; y += (360/numImages)) {
            for(x = 0; x < 360; x += (360/numImages)) {
                msgText = "Calling addFace.  tx: " + translationX + "  ty: " + translationY +
                    "  rx: " + x + "  ry: " + y;
                Globals.statusPrint(msgText);
                Globals.aGraphicPipe.addFace(p1, p2, p3, p4);
                Globals.aGraphicPipe.setPenTranslation((float)translationIncrement, 0.0f, 0.0f);
                Globals.aGraphicPipe.setPenXRotation(angleIncrement); 
            } // for x

            Globals.aGraphicPipe.setPenTranslation(-500.0f, (float)translationIncrement, 0.0f);
            Globals.aGraphicPipe.setPenYRotation(angleIncrement); 
            Globals.aGraphicPipe.setPenXRotation(-360.0f * JICTConstants.F_DTR); 
        } // for y

        Globals.aGraphicPipe.saveZBuffer("d:\\ict20\\output\\gPipeZBuffer8.bmp");
        Globals.aGraphicPipe.saveOutputImage("d:\\ict20\\output\\VRMLImage.bmp");
        Globals.statusPrint(msgText);
    } // onToolsTest


    // ############################################################################################
    // Event handlers for Preview menu

    // Called when the user selects the "Still" menu item from the Preview menu
    // MENUITEM "Still",                       ID_PREVIEW_SCENE, GRAYED
    // ON_COMMAND(ID_PREVIEW_SCENE, OnPreviewScene)
    public void onPreviewStillScene() {
        closeAllChildren();
        if(mbIsDirty) {         // If the client window has been drawn on, erase it
            mbIsDirty = false;
            repaint();
        }
        mbPreviewingScene = true;

        // Create an imageWindow object to draw into
        ImageView preView = new ImageView(this);
        // ictApp.m_pDocTemplateImage.OpenDocumentFile(null);
        preView = ImageView.getView();
        preView.setCaption("Scene Preview");
        mPreviewWindowHandle = preView; // Save the imageView window so scene preview dialog can use it
        // MDITile(MDITILE_VERTICAL);	   // This maximizes the imageWindow

        ScenePreviewDlg dlg = new ScenePreviewDlg(this, true);
        dlg.setVisible(true);

        mbRenderSceneEnabled = true;
        mbPreviewingScene = false;
        mbIsDirty = true;
        closeAllChildren();
    } // onPreviewStillScene

/*
    // ON_UPDATE_COMMAND_UI(ID_PREVIEW_SCENE, OnUpdatePreviewScene)
    public void onUpdatePreviewStillScene(CCmdUI pCmdUI) {
        pCmdUI.Enable(previewSceneEnabled);
    } // onUpdatePreviewStillScene
*/

    // Called when the user selects the "Sequence" menu item from the Preview menu
    // MENUITEM "Sequence",                    ID_PREVIEW_SEQUENCE, GRAYED
    // ON_COMMAND(ID_PREVIEW_SEQUENCE, OnPreviewSequence)
    public void onPreviewSequenceScene() {
        closeAllChildren();
        if(mbIsDirty) { // If the client window has been drawn on, erase it
            mbIsDirty = false;
            repaint();
        }
        mbPreviewingSequence = true;

        // Create an imageWindow object to draw into
        ImageView preView = new ImageView(this);
        // ictApp.m_pDocTemplateImage.OpenDocumentFile(null);
        preView = ImageView.getView();
        preView.setCaption("Sequence Preview");
        mPreviewWindowHandle = preView; // Save the imageView window so scene preview dialog can use it
        // MDITile(MDITILE_VERTICAL);	   // This maximizes the imageWindow

        ScenePreviewDlg dlg = new ScenePreviewDlg(this, true);
        dlg.setVisible(true);

        mbRenderSequenceEnabled = true;
        mbPreviewingSequence = false;
        mbIsDirty = true;
        closeAllChildren();
    } // onPreviewSequenceScene

/*
    // ON_UPDATE_COMMAND_UI(ID_PREVIEW_SEQUENCE, OnUpdatePreviewSequence)
    public void onUpdatePreviewSequence(CCmdUI pCmdUI) {
        pCmdUI.Enable(previewSequenceEnabled);
    } // onUpdatePreviewSequence
*/

    // Called from:
    //     onPreviewStillScene
    //     onPreviewSequenceScene
    //     onToolsCreateASceneList
    public void closeAllChildren() {
        return;
        /*
        CMDIChildWnd activeChildWindow = MDIGetActive(); // returns NULL if no child exists
        while(activeChildWindow != null) {
            activeChildWindow.SendMessage(WM_CLOSE);
            activeChildWindow = MDIGetActive();
        } 
        */
    } // closeAllChildren


    // ############################################################################################
    // Event handlers for Render menu

    // Called when the user selects the "Still" menu item from the Render menu
    // MENUITEM "Still",                       ID_RENDER_SCENE, GRAYED
    // ON_COMMAND(ID_RENDER_SCENE, OnRenderScene)
    public void onRenderStillScene() {
        closeAllChildren();
        if(mbIsDirty) {         // If the client window has been drawn on, erase it
            mbIsDirty = false;
            repaint();
        }

        // Create an imageWindow object to draw into
        ImageView renderView;
        //ictApp.m_pDocTemplateImage.OpenDocumentFile(null);
        renderView = ImageView.getView();
        renderView.setCaption("Scene Render");
        mPreviewWindowHandle = renderView; //save the imageView window so scene preview dialog can use it
        // MDITile(MDITILE_VERTICAL);	 //This maximizes the imageWindow

        getViewMatrix();
        mSceneList.render(renderView, mViewMatrix, mbDepthSortingEnabled, mbZBufferEnabled, 
            mbAntiAliasEnabled, mbHazeFogEnabled);
        mbRenderSceneEnabled = false;

        mbIsDirty = true;	
    } // onRenderStillScene

/*
    // ON_UPDATE_COMMAND_UI(ID_RENDER_SCENE, OnUpdateRenderScene)
    public void onUpdateRenderStillScene(CCmdUI pCmdUI) {
        pCmdUI.Enable(renderSceneEnabled);
    } // onUpdateRenderStillScene
*/

    // Called when the user selects the "Sequence" menu item from the Render menu
    // MENUITEM "Sequence",                    ID_RENDER_SEQUENCE, GRAYED
    // ON_COMMAND(ID_RENDER_SEQUENCE, OnRenderSequence)
    public void onRenderSequence() {
        closeAllChildren();
        if(mbIsDirty) {         //if the client window has been drawn on, erase it
            mbIsDirty = false;
            repaint();
        }

        ImageView renderView;
        //ictApp.m_pDocTemplateImage.OpenDocumentFile(null);
        renderView = ImageView.getView();
        renderView.setCaption("Sequence Render");
        mPreviewWindowHandle = renderView; // Save the imageView window so 
                                        // the scene preview dialog can use it
        // MDITile(MDITILE_VERTICAL);	      //Maximize the imageWindow

        getViewMatrix();
        mSceneList.render(renderView, mViewMatrix, mbDepthSortingEnabled, 
            mbZBufferEnabled, mbAntiAliasEnabled, mbHazeFogEnabled);
        mbRenderSequenceEnabled = false;
        mbIsDirty = true;
    } // onRenderSequence

/*
    // ON_UPDATE_COMMAND_UI(ID_RENDER_SEQUENCE, OnUpdateRenderSequence)
    public void onUpdateRenderSequence(CCmdUI pCmdUI) {
        pCmdUI.Enable(renderSequenceEnabled);
    } // onUpdateRenderSequence
*/

    // Called when the user selects the "Z Buffer" menu item from the Render menu
    // MENUITEM "Z Buffer",                    ID_RENDER_ZBUFFER, CHECKED
    // ON_COMMAND(ID_RENDER_ZBUFFER, OnRenderZbuffer)
    public void onRenderZBuffer() {
        mbZBufferEnabled = !mbZBufferEnabled;	
    } // onRenderZBuffer

/*
    // ON_UPDATE_COMMAND_UI(ID_RENDER_ZBUFFER, OnUpdateRenderZbuffer)
    public void onUpdateRenderZBuffer(CCmdUI pCmdUI) {
        pCmdUI.SetCheck(zBufferEnabled); 
    } // onUpdateRenderZBuffer
*/

    // Called when the user selects the "Depth Sorting" menu item from the Render menu
    // MENUITEM "Depth Sorting",               ID_RENDER_DEPTHSORTING
    // ON_COMMAND(ID_RENDER_DEPTHSORTING, OnRenderDepthsorting)
    public void onRenderDepthSorting() {
        this.mbDepthSortingEnabled = !this.mbDepthSortingEnabled;
    } // onRenderDepthSorting

/*
    // ON_UPDATE_COMMAND_UI(ID_RENDER_DEPTHSORTING, OnUpdateRenderDepthsorting)
    public void onUpdateRenderDepthSorting(CCmdUI pCmdUI) {
        pCmdUI.SetCheck(depthSortingEnabled); 
    } // onUpdateRenderDepthSorting


    // ON_UPDATE_COMMAND_UI(ID_RENDER_HAZEFOG, OnUpdateRenderHazefog)
    public void onUpdateRenderHazeFog(CCmdUI pCmdUI) {
        // Field hazeFogEnabled is passed as a parameter to method 
        // SceneList.render in methods onRenderScene and onRenderSequence
        pCmdUI.SetCheck(hazeFogEnabled); 
    } // onUpdateRenderHazeFog
*/

    // ON_COMMAND(ID_RENDER_HAZEFOG, OnRenderHazefog)
    public void onRenderHazeFog() {
        if(mbZBufferEnabled) {
            // Field hazeFogEnabled is passed as a parameter to method 
            // SceneList.render in methods onRenderScene and onRenderSequence
            mbHazeFogEnabled = !mbHazeFogEnabled;
        } else {
            Globals.statusPrint("mainfrm: Haze can only be used if the Z-Buffer is enabled");
        }
    } // onRenderHazeFog


    // Called when the user selects the "Anti-Alias" menu item from the Render menu
    // MENUITEM "Anti-Alias",                  ID_RENDER_ANTIALIAS
    // ON_COMMAND(ID_RENDER_ANTIALIAS, OnRenderAntialias)
    public void onRenderAntiAlias() {
        mbAntiAliasEnabled = !mbAntiAliasEnabled;
    } // onRenderAntiAlias

/*
    // ON_UPDATE_COMMAND_UI(ID_RENDER_ANTIALIAS, OnUpdateRenderAntialias)
    public void onUpdateRenderAntiAlias(CCmdUI pCmdUI) {
        pCmdUI.SetCheck(antiAliasEnabled); 
    } // onUpdateRenderAntiAlias
*/

    // ############################################################################################
    // Event handlers for View menu

    // ############################################################################################
    // Called when the user clicks on the Help menu
    // MENUITEM "&About ICT 2.0...",           ID_APP_ABOUT
    // ON_COMMAND(ID_APP_ABOUT, OnAppAbout)
    public void onAppAbout() {
        AboutDlg aboutDlg = new AboutDlg(null, true);
        aboutDlg.setVisible(true);
    } // onAppAbout

    // ############################################################################################
    // Miscellaneous methods

    // SceneList also has a getViewMatrix method, but it takes 3 parameters: 
    // a TMatrix, an int, and Scene.
    // Called from:
    //     onRenderScene
    //     onRenderSequence
    //     onToolsCreateASceneList
    private void getViewMatrix() {
        mViewMatrix.setIdentity();
        float xRadians = this.mViewRotateX * JICTConstants.F_DTR;
        float yRadians = this.mViewRotateY * JICTConstants.F_DTR;
        float zRadians = this.mViewRotateZ * JICTConstants.F_DTR;

        mViewMatrix.rotate(-xRadians, -yRadians, -zRadians);
        mViewMatrix.translate(-mViewTranslateX, -mViewTranslateY, -mViewTranslateZ);
    } // getViewMatrix

/*
    // ON_WM_ERASEBKGND()
    public boolean onEraseBkgnd(CDC pDC) {
        return CMDIFrameWnd.OnEraseBkgnd(pDC);
    } // onEraseBkgnd
*/
} // class MainFrame