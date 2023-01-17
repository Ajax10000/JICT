package apps;

import dialogs.AboutDlg;
import dialogs.ImageView;

import docs.ImageDoc;

import frames.MainFrame;

import globals.Globals;
import globals.Preference;

import javax.swing.JFileChooser;

public class IctApp {
    public CMultiDocTemplate m_pDocTemplateImage;
    public CMultiDocTemplate m_pDocTemplateText;

/*
/////////////////////////////////////////////////////////////////////////////
// CIctApp

BEGIN_MESSAGE_MAP(CIctApp, CWinApp)
	//{{AFX_MSG_MAP(CIctApp)
	ON_COMMAND(ID_APP_ABOUT, OnAppAbout)
	ON_COMMAND(ID_FILE_OPENIMAGE, OnFileOpenimage)
	ON_COMMAND(ID_FILE_OPENICTLOG, OnFileOpenictlog)
	//}}AFX_MSG_MAP
	// Standard file based document commands
	ON_COMMAND(ID_FILE_NEW, CWinApp::OnFileNew)
	ON_COMMAND(ID_FILE_OPEN, OnFileOpen)
//	ON_COMMAND(ID_FILE_OPEN, CWinApp::OnFileOpen)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CIctApp construction

CIctApp::CIctApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CIctApp object

CIctApp theApp;
 */

    // This constructor came from ICT20.CPP
    public IctApp() {
        // TODO: add construction code here,
        // Place all significant initialization in InitInstance
    } // IctApp ctor


    // This method came from ICT20.CPP
    boolean initInstance() {
        String msgText;

        // Standard initialization
        // If you are not using these features and wish to reduce the size
        // of your final executable, you should remove from the following
        // the specific initialization routines you do not need.

        Enable3dControls();

        LoadStdProfileSettings();  // Load standard INI file options (including MRU)

        // Register the application's document templates.  Document templates
        // serve as the connection between documents, frame windows and views.

        m_pDocTemplateImage = new CMultiDocTemplate(
            IDR_ICTTYPE,
            RUNTIME_CLASS(ImageDoc),
            RUNTIME_CLASS(CICTMDIChildWnd),          // standard MDI child frame
            RUNTIME_CLASS(ImageView));

        m_pDocTemplateText = new CMultiDocTemplate(
            IDR_TEXTTYPE,
            RUNTIME_CLASS(IctDoc),
            RUNTIME_CLASS(CMDIChildWnd),          // standard MDI child frame
            RUNTIME_CLASS(IctView));

        AddDocTemplate(m_pDocTemplateText);
        AddDocTemplate(m_pDocTemplateImage);

        // create main MDI Frame window
        MainFrame pMainFrame = new MainFrame();
        if (!pMainFrame.LoadFrame(IDR_MAINFRAME)) {
            return false;
        }
        m_pMainWnd = pMainFrame;

        if (m_lpCmdLine[0] != '\0') {
            // TODO: add command line processing here
        }

        // The main window has been initialized, so show and update it.
        m_nCmdShow = SW_SHOWMAXIMIZED;            
        pMainFrame.ShowWindow(m_nCmdShow);
        pMainFrame.UpdateWindow();

        Globals.ictPreference = new Preference();

        // Remove the old ict log and create a new one
        remove(Globals.ictPreference.getPath(Preference.ProcessLog));
        time_t theTime;
        time(theTime);
        msgText = "ICT Process Log.  " + ctime(theTime);
        Globals.statusPrint(msgText);

        return true;
    } // initInstance


    // Called when the user clicks on the Help menu
    // MENUITEM "&About ICT 2.0...",           ID_APP_ABOUT
    // ON_COMMAND(ID_APP_ABOUT, OnAppAbout)
    public void onAppAbout() {
        AboutDlg aboutDlg = new AboutDlg(null, true);
        aboutDlg.setVisible(true);
    } // onAppAbout

    /////////////////////////////////////////////////////////////////////////////
    // CIctApp commands

    // Called when the user selects the Open Image menu item from the File menu
    // MENUITEM "Open Image",                  ID_FILE_OPENIMAGE
    // ON_COMMAND(ID_FILE_OPENIMAGE, OnFileOpenimage)
    public void onFileOpenImage() {
        // change to the default output file directory (indicated in the ICT preference object)
        _chdir(Globals.ictPreference.getPath(Preference.OutputImageDirectory)); 

        // TODO: Replace with JFileChooser
        // CFileDialog dlg = new CFileDialog(true, "bmp", "*.bmp");
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int showDlgResult = dlg.showDialog(null, "Select image name");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            m_pDocTemplateImage.OpenDocumentFile(dlg.getSelectedFile().getName());
        }
    } // onFileOpenImage


    // Called when the user selects the Open ICT Log menu item from the File menu
    // MENUITEM "Open ICT Log",                ID_FILE_OPENICTLOG
    // ON_COMMAND(ID_FILE_OPENICTLOG, OnFileOpenictlog)
    public void onFileOpenIctLog() {
        String processLogPath;

        processLogPath = Globals.ictPreference.getPath(Preference.ProcessLog);
        m_pDocTemplateText.OpenDocumentFile(processLogPath);
    } // onFileOpenIctLog


    // Called when the user selects the Open Scene menu item from the File menu
    // MENUITEM "&Open Scene...\tCtrl+O",      ID_FILE_OPEN
    // ON_COMMAND(ID_FILE_OPEN, OnFileOpen)
    public void onFileOpen() {
        // Change to the default scene file directory (indicated in the ICT preference object)
        _chdir(Globals.ictPreference.getPath(Preference.SceneFileDirectory));

        // TODO: Replace with JFileChooser
        // CFileDialog dlg = new CFileDialog(true, "scn", "*.scn");
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int showDlgResult = dlg.showDialog(null, "Select scene file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            m_pDocTemplateText.OpenDocumentFile(dlg.getSelectedFile().getName());
        }
    } // onFileOpen
} // class IctApp