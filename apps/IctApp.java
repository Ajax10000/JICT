package apps;

import dialogs.ImageView;

import docs.ImageDoc;

import frames.MainFrame;

import globals.Globals;

import java.io.File;
import java.time.LocalDateTime;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

public class IctApp {
    private MainFrame mainFrame;
    //public CMultiDocTemplate m_pDocTemplateImage;
    //public CMultiDocTemplate m_pDocTemplateText;

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

    // This constructor originally came from ICT20.CPP
    public IctApp(String[] args) {
        // TODO: add construction code here,
        // Place all significant initialization in InitInstance
        initInstance(args);
    } // IctApp ctor


    // This method originally came from ICT20.CPP
    boolean initInstance(String args[]) {
        String sMsgText;

        // Standard initialization
        // If you are not using these features and wish to reduce the size
        // of your final executable, you should remove from the following
        // the specific initialization routines you do not need.

        /*
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
        */

        // create main MDI Frame window
        // this.mainFrame = new MainFrame(this);
        this.mainFrame = MainFrame.getInstance();
        
        if ((args != null) && (args.length > 0)) {
            // TODO: add command line processing here
        }

        // The main window has been initialized, so show and update it.       
        mainFrame.setVisible(true);
        mainFrame.repaint();

        // Remove the old JICT log and create a new one
        Preferences prefs = Preferences.userNodeForPackage(JICTApp.class);
        String oldLogFilePath = prefs.get("LogPath", "logs/JICT.log");
        File oldLogFile = new File(oldLogFilePath);
        if (oldLogFile.exists()) {
            boolean deleted = oldLogFile.delete();
            if(!deleted) {
                // TODO: Write a message on mainFrame
            }
        }

        sMsgText = "JICT Process Log.  " + LocalDateTime.now();
        Globals.statusPrint(sMsgText);

        return true;
    } // initInstance


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new IctApp(args);
            }
        });
    } // main
} // class IctApp