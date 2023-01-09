package frames;

import apps.IctApp;

import core.MemImage;
import core.SceneList;
import core.Shape3d;

import dialogs.ImageView;
import dialogs.MakeTextureDlg;
import dialogs.MorphDlg;
import dialogs.MotionBlurDlg;
import dialogs.QuadMeshDlg;
import dialogs.ScenePreviewDlg;
import dialogs.WarpParamDlg;

import globals.Globals;
import globals.Preference;
import globals.VRML;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
    void warpImage ();
    void getViewMatrix(tMatrix *viewMatrix);
    void closeAllChildren();

	//{{AFX_MSG(CMainFrame)
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);
	afx_msg void OnPreviewScene();
	afx_msg void OnPreviewSequence();
	afx_msg void OnRenderDepthsorting();
	afx_msg void OnRenderScene();
	afx_msg void OnRenderSequence();
	afx_msg void OnToolsCreatealphaimage();
	afx_msg void OnToolsCreateascenelist();
	afx_msg void OnToolsCreatecutout();
	afx_msg void OnToolsMorphSequence();
	afx_msg void OnToolsWarpimage();
	afx_msg void OnUpdateToolsCreatecutout(CCmdUI* pCmdUI);
	afx_msg void OnUpdatePreviewScene(CCmdUI* pCmdUI);
	afx_msg void OnUpdatePreviewSequence(CCmdUI* pCmdUI);
	afx_msg void OnUpdateRenderScene(CCmdUI* pCmdUI);
	afx_msg void OnUpdateRenderSequence(CCmdUI* pCmdUI);
	afx_msg void OnUpdateRenderDepthsorting(CCmdUI* pCmdUI);
	afx_msg BOOL OnEraseBkgnd(CDC* pDC);
	afx_msg void OnToolsTest();
	afx_msg void OnRenderZbuffer();
	afx_msg void OnUpdateRenderZbuffer(CCmdUI* pCmdUI);
	afx_msg void OnUpdateToolsSampleimage(CCmdUI* pCmdUI);
	afx_msg void OnToolsSampleimage();
	afx_msg void OnToolsRemoveSampleColors();
	afx_msg void OnUpdateToolsRemoveSampleColors(CCmdUI* pCmdUI);
	afx_msg void OnToolsCreatemesh();
	afx_msg void OnToolsCreatetextureimage();
	afx_msg void OnUpdateRenderHazefog(CCmdUI* pCmdUI);
	afx_msg void OnUpdateRenderAntialias(CCmdUI* pCmdUI);
	afx_msg void OnToolsMotionblur();
	afx_msg void OnRenderHazefog();
	afx_msg void OnRenderAntialias();
	afx_msg void OnToolsRenderVrmlFile();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
*/

    // 1 if client window needs erasing
    // I changed this from int to boolean
    public boolean isDirty;


    // Read in: 
    //     ScenePreviewDlg.setTextBoxesWithModelTransform
    // Modified in: 
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onCmdReset
    public float warpRotateX, warpRotateY, warpRotateZ;

    // Read in: 
    //     ScenePreviewDlg.setTextBoxesWithModelTransform
    // Modified in: 
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onCmdReset
    public float warpScaleX, warpScaleY, warpScaleZ;

    // Modified in: 
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onCmdReset
    // Read in: 
    //     ScenePreviewDlg.setTextBoxesWithModelTransform
    public float warpTranslateX, warpTranslateY, warpTranslateZ;

    // Read in: 
    //     ScenePreviewDlg.setTextBoxesWithViewTransform
    //     ScenePreviewDlg.onOK
    public float viewRotateX, viewRotateY, viewRotateZ;

    // Read in: 
    //     ScenePreviewDlg.setTextBoxesWithViewTransform
    //     ScenePreviewDlg.onOK
    // Modified in:
    //     ScenePreviewDlg.onCmdReset
    public float viewTranslateX, viewTranslateY, viewTranslateZ;
    public String sceneName;

    // Read in ScenePreviewDlg.onOk
    public String sceneFileName;
    public int effectType, mode, colorMode;
    public int outputRows, outputColumns;

    // Changed from int to boolean
    public boolean cutoutEnabled;         // Menu control variables

    // Changed from int to boolean
    public boolean previewSceneEnabled;

    // Chaned from int to boolean
    public boolean previewSequenceEnabled;

    // Changed from int to boolean
    public boolean renderSceneEnabled;

    // Changed from int to boolean
    public boolean renderSequenceEnabled;

    // Changed from int to boolean
    // Toggled in method onToolsRemoveSampleColors
    // Affects ImageView.onLButtonDown
    // Affects ImageView.onRButtonDown
    public boolean removeSampleColorsEnabled;

    // Changed from int to boolean
    public boolean depthSortingEnabled;

    // Changed from int to boolean
    public boolean zBufferEnabled;

    // Chanaged from int to boolean
    public boolean imageSamplingEnabled;

    // Changed from int to boolean
    public boolean motionBlurEnabled;

    // Changed from int to boolean
    public boolean hazeFogEnabled;

    // Changed from int to boolean
    public boolean antiAliasEnabled;

    // 1 if the scene is being previewed
    // Changed from int to boolean
    public boolean previewingScene;

    // 1 if sequence is being previewed
    // Changed from int to boolean
    public boolean previewingSequence;

    // 1 if the ViewPoint is being previewed
    // Changed from int to boolean
    // Modified in ScenePreviewDlg.chooseModel
    public boolean changeViewPoint;
    public SceneList mySceneList;     // Linked List containing scene description
    public TMatrix modelMatrix;       // Contains a model transformation
    public TMatrix viewMatrix;        // Contains viewpoint transformation

    // The image window into which the scene preview display is drawn
    public ImageView previewWindowHandle;	 

    // SEQUENTIAL and RANDOM were defined in MEMIMAGE.H
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM     = 0;

    // Effect Types
    public static final int STILL    = 1;
    public static final int SEQUENCE = 2;
    public static final int MORPH    = 3;

    // These were defined in MEMIMAGE.H
    public static final int REDCOLOR = 1;
    public static final int GREENCOLOR = 2;
    public static final int BLUECOLOR = 3;
    public static final int EIGHTBITMONOCHROME = 2;
    public static final int A32BIT = 4;
    public static final int RGBCOLOR = 5;
    public static final int ONEBITMONOCHROME = 6;

    public static final int MONOCHROME = 1;
    public static final int COLOR      = 2;

    // This value came from ICT20.H
    public static final float F_DTR = 3.1415926f/180.0f;


    public MainFrame() {
        this.warpRotateX = 0.0f; 
        this.warpRotateY = 0.0f; 
        this.warpRotateZ = 0.0f;

        this.warpTranslateX = 0.0f; 
        this.warpTranslateY = 0.0f; 
        this.warpTranslateZ = 0.0f;

        this.warpScaleX = 1.0f; 
        this.warpScaleY = 1.0f; 
        this.warpScaleZ = 1.0f;

        this.viewRotateX = 0.0f; 
        this.viewRotateY = 0.0f; 
        this.viewRotateZ = 0.0f;

        this.viewTranslateX = 0.0f; 
        this.viewTranslateY = 0.0f; 
        this.viewTranslateZ = 0.0f;

        this.isDirty = false;
        this.cutoutEnabled = false;
        this.previewSceneEnabled = false;
        this.previewSequenceEnabled = false;
        this.renderSceneEnabled = false;
        this.renderSequenceEnabled = false;
        this.removeSampleColorsEnabled = false;
        this.depthSortingEnabled = false;
        this.motionBlurEnabled = false;
        this.hazeFogEnabled = false;
        this.antiAliasEnabled = false;
        this.zBufferEnabled = true;
        this.imageSamplingEnabled = false;
        this.previewingScene = false;
        this.previewingSequence = false;
        this.outputRows = 250;  // Set these in case a SceneList is not read in
        this.outputColumns = 250;
        this.changeViewPoint = false;
        this.mySceneList = new SceneList();
        this.viewMatrix = new TMatrix();
        this.modelMatrix = new TMatrix();
        this.previewWindowHandle = null;  // The scene preview window handle

        // Initialize the gPipe object for VRML rendering
        Globals.aGraphicPipe.initialize();

        JMenuBar menuBar = new JMenuBar();
        createMenu(menuBar);
        setJMenuBar(menuBar);
    }


    // Called from:
    //     MainFrame constructor
    private void createMenu(JMenuBar pMenuBar) {
        JMenu mnuFile = new JMenu("File");       // POPUP "&File"
        JMenu mnuEdit = new JMenu("Edit");       // POPUP "&Edit"
        JMenu mnuSearch = new JMenu("Search");   // POPUP "Search"
        JMenu mnuTools = new JMenu("Tools");     // POPUP "Tools"
        JMenu mnuPreview = new JMenu("Preview"); // POPUP "Preview"
        JMenu mnuRender = new JMenu("Render");   // POPUP "Render"
        JMenu mnuView = new JMenu("View");       // POPUP "&View"
        JMenu mnuWindow = new JMenu("Window");   // POPUP "&Window"
        JMenu mnuHelp = new JMenu("Help");       // POPUP "&Help"

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
        JMenuItem mimCrtCutout         = new JMenuItem("Create Cutout");
        JMenuItem mimCrtAlphaImage     = new JMenuItem("Create Alpha Image...");
        // MENUITEM SEPARATOR
        JMenuItem mimWarpImage         = new JMenuItem("Warp Image...");
        // MENUITEM SEPARATOR
        JMenuItem mimSampleImage       = new JMenuItem("Sample Image");
        JMenuItem mimRemoveSampledColors = new JMenuItem("Remove Sampled Colors");
        // MENUITEM SEPARATOR
        JMenuItem mimMotionBlur        = new JMenuItem("Motion Blur...");
        // MENUITEM SEPARATOR
        JMenuItem mimCrtAMorphSequence = new JMenuItem("Create a Morph Sequence...");
        // MENUITEM SEPARATOR
        JMenuItem mimRenderVRMLFile    = new JMenuItem("Render VRML File...");
        // MENUITEM SEPARATOR
        JMenuItem mimTest              = new JMenuItem("Test");

        mimCrtASceneList.addActionListener(this);
        mimCrtTextureImage.addActionListener(this);
        mimCrtAMeshModel.addActionListener(this);
        mimCrtCutout.addActionListener(this);
        mimCrtAlphaImage.addActionListener(this);
        mimWarpImage.addActionListener(this);
        mimSampleImage.addActionListener(this);
        mimRemoveSampledColors.addActionListener(this);
        mimMotionBlur.addActionListener(this);
        mimCrtAMorphSequence.addActionListener(this);
        mimRenderVRMLFile.addActionListener(this);
        mimTest.addActionListener(this);
        mimCrtASceneList.addActionListener(this);
        mimCrtTextureImage.addActionListener(this);
        mimCrtAMeshModel.addActionListener(this);
        mimCrtCutout.addActionListener(this);
        mimCrtAlphaImage.addActionListener(this);
        mimWarpImage.addActionListener(this);
        mimSampleImage.addActionListener(this);
        mimRemoveSampledColors.addActionListener(this);
        mimMotionBlur.addActionListener(this);
        mimCrtAMorphSequence.addActionListener(this);
        mimRenderVRMLFile.addActionListener(this);
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

        JMenuItem mimStill    = new JMenuItem("Still");
        JMenuItem mimSequence = new JMenuItem("Sequence");

        mimStill.addActionListener(this);
        mimSequence.addActionListener(this);

        pPreviewMenu.add(mimStill);
        pPreviewMenu.add(mimSequence);
    } // createPreviewMenu


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

        JMenuItem mimStill        = new JMenuItem("Still");
        JMenuItem mimSequence     = new JMenuItem("Sequence");
        // MENUITEM SEPARATOR
        JMenuItem mimZBuffer      = new JMenuItem("Z Buffer");
        JMenuItem mimDepthSorting = new JMenuItem("Depth Sorting");
        // MENUITEM SEPARATOR
        JMenuItem mimAntiAlias    = new JMenuItem("Anti-Alias");

        pRenderMenu.add(mimStill);
        pRenderMenu.add(mimSequence);
        pRenderMenu.addSeparator();
        pRenderMenu.add(mimZBuffer);
        pRenderMenu.add(mimDepthSorting);
        pRenderMenu.addSeparator();
        pRenderMenu.add(mimAntiAlias);
    } // createRenderMenu


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

        JMenuItem mimAbout = new JMenuItem("About ICT 2.0...", KeyEvent.VK_A); // "&About ICT 2.0..."

        mimAbout.addActionListener(this);

        pHelpMenu.add(mimAbout);
    }


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

        // Was it an Edit menu item?

        // Was it a Tools menu item?
        if (sActionCmd.equals("Create a Scene List...")) {
            onToolsCreateASceneList();
            return;
        } else if (sActionCmd.equals("Create Texture Image...")) {
            onToolsCreateTextureImage();
            return;
        } else if (sActionCmd.equals("Create a Mesh Model...")) {
            onToolsCreateMesh();
            return;
        } else if (sActionCmd.equals("Create Cutout")) {
            onToolsCreateCutout();
        } else if (sActionCmd.equals("Create Alpha Image...")) {
            onToolsCreateAlphaImage();
            return;
        } else if (sActionCmd.equals("Warp Image...")) {
            onToolsWarpImage();
            return;
        } else if (sActionCmd.equals("Sample Image")) {
            onToolsSampleImage();
            return;
        } else if (sActionCmd.equals("Remove Sampled Colors")) {
            onToolsRemoveSampleColors();
            return;
        } else if (sActionCmd.equals("Motion Blur...")) {
            onToolsMotionBlur();
            return;
        } else if (sActionCmd.equals("Create a Morph Sequence...")) {
            onToolsMorphSequence();
            return;
        } else if (sActionCmd.equals("Render VRML File...")) {
            onToolsRenderVrmlFile();
            return;
        } else if (sActionCmd.equals("Test")) {
            onToolsTest();
            return;
        }

        
        // Was it a Preview menu item?
        if (sActionCmd.equals("Still")) {
            onPreviewStillScene();
            return;
        } else if (sActionCmd.equals("Sequence")) {
            onPreviewSequenceScene();
            return;
        }

        // Was it a Render menu item?
        if (sActionCmd.equals("Still")) {
            onRenderStillScene();
            return;
        } else if (sActionCmd.equals("Sequence")) {
            onRenderSequence();
            return;
        } else if (sActionCmd.equals("Z Buffer")) {
            onRenderZBuffer();
            return;
        } else if (sActionCmd.equals("Depth Sorting")) {
            onRenderDepthSorting();
            return;
        } else if (sActionCmd.equals("Anti-Alias")) {
            onRenderAntiAlias();
            return;
        }

        // Was it a View menu item?

        // Was it a Help menu item?
        if (sActionCmd.equals("About ICT 2.0...")) {

        }

    }
    // ############################################################################################
    // Event handlers for Tools menu

    // Called when the user selects the "Create a Scene List..." menu item from the Tools menu.
    // MENUITEM "Create a Scene List...",      ID_TOOLS_CREATEASCENELIST
    // ON_COMMAND(ID_TOOLS_CREATEASCENELIST, OnToolsCreateascenelist)
    public void onToolsCreateASceneList() {
        String aFileName;
        String msgText;
        int myStatus;
        
        closeAllChildren();
        if(isDirty) {         // If the client window has been drawn on, erase it
            isDirty = false;
            repaint();
        }

        if(mySceneList.listLength() > 0) {
            Globals.statusPrint("Clearing Previous Scene List...");
            mySceneList.clear();
        }

        // Display standard Open dialog box to select a file name.
        // First, change to the default scene file directory (indicated in the ICT preference object)
        _chdir(Globals.ictPreference.getPath(Preference.SceneFileDirectory)); 
        // TODO: Replace with JFileChooser
        //CFileDialog dlg = new CFileDialog(true, "scn", "*.scn");
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int showDlgResult = dlg.showDialog(this, "Select scn file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            aFileName = dlg.getSelectedFile().getName();
        }

        msgText = "Reading Scene List: " + aFileName;
        Globals.statusPrint(msgText);

        myStatus = mySceneList.readList(msgText, aFileName);
        Globals.statusPrint(msgText);

        if(myStatus != 0) {
            mySceneList.clear();
            return;
        }

        sceneFileName = dlg.getSelectedFile().getName();  // save the file name
        // Load the scene information into the client object
        mySceneList.getSceneInfo(sceneName, effectType, colorMode, 
            outputRows, outputColumns);
        mySceneList.getViewTransform(viewTranslateX, viewTranslateY, viewTranslateZ,
            viewRotateX, viewRotateY, viewRotateZ);
        getViewMatrix(viewMatrix);

        if(this.effectType == SEQUENCE || this.effectType == MORPH) {
            this.previewSequenceEnabled = true;
            this.previewSceneEnabled    = false;
            this.renderSceneEnabled     = false;
            this.renderSequenceEnabled  = false;
        } else {
            this.previewSceneEnabled    = true;
            this.previewSequenceEnabled = false;
            this.renderSceneEnabled     = false;
            this.renderSequenceEnabled  = false;
        }
    } // onToolsCreateASceneList


    // Called when the user selects the "Create Texture Image..." menu item from the Tools menu.
    // MENUITEM "Create Texture Image...",     ID_TOOLS_CREATETEXTUREIMAGE
    // ON_COMMAND(ID_TOOLS_CREATETEXTUREIMAGE, OnToolsCreatetextureimage)
    public void onToolsCreateTextureImage() {
        MakeTextureDlg dlg = new MakeTextureDlg(this, true);
        dlg.setVisible(true);	
    } // onToolsCreateTextureImage


    // Called when the user selects the "Crease a Mesh Model..." menu item from the Tools menu.
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
        if(isDirty) {         // If the client window has been drawn on, erase it
            isDirty = false;
            repaint();
        }
        
        cutoutEnabled = !cutoutEnabled;
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
        String msgText;

        Globals.statusPrint("Creating an Alpha-Channel Image");
        if(isDirty) {         // If the client window has been drawn on, erase it
            isDirty = false;
            repaint();
        }

        closeAllChildren();
        String aFileName;
        // TODO: Replace with JFileChooser
        // CFileDialog dlg = new CFileDialog(true, "bmp", "*.bmp");
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int showDlgResult = dlg.showDialog(this, "Select bmp file");

        int aStatus, imHeight, imWidth, bitsPerPixel;
        String centeredName;
        MemImage alphaImage, inImage;

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            aFileName = dlg.getSelectedFile().getName();

            // Center the input image by removing any chromacolor border 
            Globals.constructPathName(centeredName, aFileName, 'q');
            if(centeredName.equals(aFileName)) {
                Globals.beep(10, 10);
                Globals.statusPrint("onToolsCreateAlphaImage: Centered image name cannot equal original image name");
                return;
            }

            aStatus = Globals.readBMPHeader(aFileName, imHeight, imWidth, bitsPerPixel);
            if(bitsPerPixel == 8) {
                inImage = new MemImage(aFileName, 0, 0, RANDOM, 'R', MONOCHROME);
            }
            if(bitsPerPixel == 24) {
                inImage = new MemImage(aFileName, 0, 0, RANDOM, 'R', RGBCOLOR);
            }

            // Create an alpha image
            String alphaName;
            Globals.constructPathName(alphaName, aFileName, 'a');
            if(alphaName.equals(aFileName)) {
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

                String shapeDir, shapeFile, shapeName;
                shapeFile = aFileName;
                strncpy(shapeFile + shapeFile.length() - 4, ".shp", 4);

                // Generate a shape file from the binary image
                int aStatus = Globals.shapeFromImage(alphaImage, aShape);

                if(aStatus == 0) {
                    aShape.writeShape(shapeFile);
                    msgText = "Saved a shape file: " + shapeFile + "  numVertices: " + aShape.getNumVertices();
                    Globals.statusPrint(msgText);
                }

                Globals.constructPathName(alphaName, aFileName, 'a');
                if(alphaName.equals(aFileName)) {
                    Globals.statusPrint("Alpha Image Name cannot equal the original file name");
                    return;
                }

                alphaImage.writeBMP(alphaName);
                msgText = "Saved an alpha image: " + alphaName;
                Globals.statusPrint(msgText);
            }
        }
    } // onToolsCreateAlphaImage


    // Called when the user selects the "Warp Image..." menu item from the Tools menu.
    // MENUITEM "Warp Image...",               ID_TOOLS_WARPIMAGE
    // ON_COMMAND(ID_TOOLS_WARPIMAGE, OnToolsWarpimage)
    public void onToolsWarpImage() {
        WarpParamDlg dlg = new WarpParamDlg(this, true);
        dlg.setVisible(true);

        // May have to move this code to WarpParamDlg.java
        if(dlg.DoModal() != IDOK) {
            return;
        }

        this.warpRotateX = Float.parseFloat(dlg.m_rx);
        this.warpRotateY = Float.parseFloat(dlg.m_ry);
        this.warpRotateZ = Float.parseFloat(dlg.m_rz);
        this.warpScaleX  = Float.parseFloat(dlg.m_sx);
        this.warpScaleY  = Float.parseFloat(dlg.m_sy);

        ImageView orgView, outView;
        closeAllChildren();
        
        // Setup Output Image
        IctApp pApp = AfxGetApp();
        pApp.m_pDocTemplateImage.OpenDocumentFile(null);
        outView = ImageView.getView();
        outView.setCaption("Warped Image");

        pApp.m_pDocTemplateImage.OpenDocumentFile(null);
        orgView = ImageView.getView();
        int inHeight, inWidth, bitsPerPixel;
        MemImage inImage;
        int aStatus = Globals.readBMPHeader(Globals.ictPreference.getPath(Preference.WarpTestPath), inHeight, inWidth, bitsPerPixel);
        if(bitsPerPixel == 8) {
            inImage = new MemImage(Globals.ictPreference.getPath(Preference.WarpTestPath), 0, 0, RANDOM, 'R', MONOCHROME);
        }
        if(bitsPerPixel == 24) {
            inImage = new MemImage(Globals.ictPreference.getPath(Preference.WarpTestPath), 0, 0, RANDOM, 'R', RGBCOLOR);
        }

        int outHeight = 350;
        int outWidth  = 350;
        if(!inImage.isValid()) {
            Globals.statusPrint("Unable to open warp test image");
            return;
        }
        orgView.setCaption("Original Image");
        orgView.associateMemImage(inImage);

        MDITile(MDITILE_VERTICAL);

        MemImage outImage   = new MemImage(outHeight, outWidth, bitsPerPixel);
        MemImage aliasImage = new MemImage(outHeight, outWidth, bitsPerPixel);
        int xOffset, yOffset;
        TMatrix dummyMatrix = new TMatrix();

        // Translate test image to center of output image
        float xAngle = 0.0f, yAngle = 0.0f, zAngle = 0.0f;

        Globals.iwarpz(inImage, outImage, null, 
            warpRotateX, warpRotateY, warpRotateZ,
            warpScaleX, warpScaleY, warpScaleZ, 
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            dummyMatrix,
            0.0f, 0.0f, 0.0f);
        outImage.writeBMP("d:\\ict20\\output\\testwarp.bmp");

        if(antiAliasEnabled) {
            Globals.antiAlias(outImage, aliasImage);
            outView.associateMemImage(aliasImage);
        } else {
            outView.associateMemImage(outImage);
        }

        xAngle += warpRotateX;
        yAngle += warpRotateY;
        zAngle += warpRotateZ;
    } // onToolsWarpImage


    // Called when the user selects the "Sample Image" menu item from the Tools menu.
    // MENUITEM "Sample Image",                ID_TOOLS_SAMPLEIMAGE
    // ON_COMMAND(ID_TOOLS_SAMPLEIMAGE, OnToolsSampleimage)
    public void onToolsSampleImage() {
        imageSamplingEnabled = !imageSamplingEnabled;		
        cutoutEnabled = false;
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
        removeSampleColorsEnabled = !removeSampleColorsEnabled;
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
        _chdir(Globals.ictPreference.getPath(Preference.VRMLDirectory)); 

        // TODO: Replace with JFileChooser, maybe add FileFilter
        // CFileDialog dlg = new CFileDialog(TRUE, "wrl", "*.wrl");
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
        float angleIncrement = 360.0f / (float)(numImages - 1) * F_DTR;
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
            Globals.aGraphicPipe.setPenXRotation(-360.0f * F_DTR); 
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
        if(isDirty) {         //if the client window has been drawn on, erase it
            isDirty = false;
            repaint();
        }
        previewingScene = true;

        // Create an imageWindow object to draw into
        IctApp pApp = AfxGetApp();
        ImageView preView = new ImageView();
        pApp.m_pDocTemplateImage.OpenDocumentFile(null);
        preView = ImageView.getView();
        preView.setCaption("Scene Preview");
        previewWindowHandle = preView; //save the imageView window so scene preview dialog can use it
        MDITile(MDITILE_VERTICAL);	 //This maximizes the imageWindow

        ScenePreviewDlg dlg = new ScenePreviewDlg(this, true);
        dlg.setVisible(true);

        renderSceneEnabled = true;
        previewingScene = false;
        isDirty = true;
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
        if(isDirty) { // If the client window has been drawn on, erase it
            isDirty = false;
            repaint();
        }
        previewingSequence = true;

        // Create an imageWindow object to draw into
        IctApp pApp = AfxGetApp();
        ImageView preView = new ImageView();
        pApp.m_pDocTemplateImage.OpenDocumentFile(null);
        preView = ImageView.getView();
        preView.setCaption("Sequence Preview");
        previewWindowHandle = preView; // Save the imageView window so scene preview dialog can use it
        MDITile(MDITILE_VERTICAL);	   // This maximizes the imageWindow

        ScenePreviewDlg dlg = new ScenePreviewDlg(this, true);
        dlg.setVisible(true);

        renderSequenceEnabled = true;
        previewingSequence = false;
        isDirty = true;
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
        CMDIChildWnd activeChildWindow = MDIGetActive(); // returns NULL if no child exists
        while(activeChildWindow != null) {
            activeChildWindow.SendMessage(WM_CLOSE);
            activeChildWindow = MDIGetActive();
        } 
    } // closeAllChildren


    // ############################################################################################
    // Event handlers for Render menu

    // Called when the user selects the "Still" menu item from the Render menu
    // MENUITEM "Still",                       ID_RENDER_SCENE, GRAYED
    // ON_COMMAND(ID_RENDER_SCENE, OnRenderScene)
    public void onRenderStillScene() {
        closeAllChildren();
        if(isDirty) {         //if the client window has been drawn on, erase it
            isDirty = false;
            repaint();
        }

        // Create an imageWindow object to draw into
        IctApp pApp = AfxGetApp();
        ImageView renderView;
        pApp.m_pDocTemplateImage.OpenDocumentFile(null);
        renderView = ImageView.getView();
        renderView.setCaption("Scene Render");
        previewWindowHandle = renderView; //save the imageView window so scene preview dialog can use it
        MDITile(MDITILE_VERTICAL);	 //This maximizes the imageWindow

        getViewMatrix(viewMatrix);
        mySceneList.render(renderView, viewMatrix, depthSortingEnabled, zBufferEnabled, 
            antiAliasEnabled, hazeFogEnabled);
        renderSceneEnabled = false;

        isDirty = true;	
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
        if(isDirty) {         //if the client window has been drawn on, erase it
            isDirty = false;
            repaint();
        }

        IctApp pApp = AfxGetApp();
        ImageView renderView;
        pApp.m_pDocTemplateImage.OpenDocumentFile(null);
        renderView = ImageView.getView();
        renderView.setCaption("Sequence Render");
        previewWindowHandle = renderView; // Save the imageView window so 
                                        // the scene preview dialog can use it
        MDITile(MDITILE_VERTICAL);	      //Maximize the imageWindow

        getViewMatrix(viewMatrix);
        mySceneList.render(renderView, viewMatrix, depthSortingEnabled, 
            zBufferEnabled, antiAliasEnabled, hazeFogEnabled);
        renderSequenceEnabled = false;
        isDirty = true;
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
        zBufferEnabled = !zBufferEnabled;	
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
        this.depthSortingEnabled = !this.depthSortingEnabled;
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
        if(zBufferEnabled) {
            // Field hazeFogEnabled is passed as a parameter to method 
            // SceneList.render in methods onRenderScene and onRenderSequence
            hazeFogEnabled = !hazeFogEnabled;
        } else {
            Globals.statusPrint("mainfrm: Haze can only be used if the Z-Buffer is enabled");
        }
    } // onRenderHazeFog


    // Called when the user selects the "Anti-Alias" menu item from the Render menu
    // MENUITEM "Anti-Alias",                  ID_RENDER_ANTIALIAS
    // ON_COMMAND(ID_RENDER_ANTIALIAS, OnRenderAntialias)
    public void onRenderAntiAlias() {
        antiAliasEnabled = !antiAliasEnabled;
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
    // Miscellaneous methods

    // Called from:
    //     onRenderScene
    //     onRenderSequence
    //     onToolsCreateASceneList
    public void getViewMatrix(TMatrix viewMatrix) {
        viewMatrix.setIdentity();
        float xRadians = this.viewRotateX * F_DTR;
        float yRadians = this.viewRotateY * F_DTR;
        float zRadians = this.viewRotateZ * F_DTR;

        viewMatrix.rotate(-xRadians, -yRadians, -zRadians);
        viewMatrix.translate(-viewTranslateX, -viewTranslateY, -viewTranslateZ);
    } // getViewMatrix

/*
    // ON_WM_ERASEBKGND()
    public boolean onEraseBkgnd(CDC pDC) {
        return CMDIFrameWnd.OnEraseBkgnd(pDC);
    } // onEraseBkgnd
*/
} // class MainFrame