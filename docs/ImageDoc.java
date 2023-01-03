package docs;

import core.MemImage;

import globals.Globals;

import java.awt.Dimension;

public class ImageDoc { // : public CDocument {
	boolean ictdebug = false;
	protected Dimension m_sizeDoc;
    protected MemImage m_theImage;
    protected String m_pathName;

    // These were defined in MEMIMAGE.H
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM = 0;

/*
protected: // create from serialization only
	DECLARE_DYNCREATE(imageDoc)

// Attributes
public:
	imageDoc();
	virtual ~imageDoc();
	virtual BOOL OnSaveDocument(LPCTSTR lpszPathName);
	virtual BOOL OnOpenDocument(LPCTSTR lpszPathName);
    CSize GetDocSize();
    memImage *GetImagePointer();
    CString GetPathName();
    void SetImagePointer(memImage *theImage);
    static imageDoc * GetDoc();
   

// Implementation
protected:
	CSize m_sizeDoc;
    memImage *m_theImage;
    CString m_pathName;

#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif
protected:
	virtual BOOL    OnNewDocument();

// Generated message map functions
protected:
	//{{AFX_MSG(imageDoc)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
}
*/

	public ImageDoc() {
		m_sizeDoc = CSize(1, 1);     // dummy value to make CScrollView happy
		if(ictdebug) {
			Globals.statusPrint("ImageDoc Constructor");
		}
	} // ImageDoc ctor


	public Dimension getDocSize() {   
		int imHeight = m_theImage.getHeight();
		int imWidth  = m_theImage.getWidth();
		m_sizeDoc = new Dimension(imWidth, imHeight);
		return m_sizeDoc;     
	} // getDocSize

	
	public MemImage getImagePointer() {
		return m_theImage;
	} // getImagePointer


	public String getPathName() {
		return m_pathName;
	} // getPathName


	public void setImagePointer(MemImage theImage) {
		m_theImage = theImage;
		return ;
	} // setImagePointer


	public void finalize() {
		if(ictdebug) {
			Globals.statusPrint("ImageDoc Destructor");
		}
	} // finalize


	protected boolean onNewDocument() {
		if (!CDocument.OnNewDocument()) {
			return false;
		}
		m_theImage = null;
		return true;
	} // onNewDocument


	public boolean onOpenDocument(String lpszPathName) {
		m_theImage = new MemImage(lpszPathName, 0, 0, RANDOM, 'R', 0);
		if(!m_theImage.isValid()) {
			String msgText = "OnOpenDocument: Cannot open image: " + lpszPathName;
			Globals.statusPrint(msgText);
			return false;
		}

		String msgText = "Opened: " + lpszPathName + "  " + m_theImage.getHeight() + " rows  " + m_theImage.getWidth() + " cols.";
		Globals.statusPrint(msgText);
		m_pathName = new String(lpszPathName);
		return true;
	} // onOpenDocument


	public boolean onSaveDocument(String lpszPathName) {
		m_theImage.writeBMP(lpszPathName);
		return true;
	} // onSaveDocument


	public ImageDoc getDoc() {
		CMDIChildWnd pChild = ((CMDIFrameWnd)(AfxGetApp().m_pMainWnd)).MDIGetActive();

		if (!pChild) {
			return null;
		}
		
		CDocument pDoc = pChild.GetActiveDocument();

		if (!pDoc) {
			return null;
		}

		// Fail if doc is the wrong kind
		if(!pDoc.IsKindOf(RUNTIME_CLASS(ImageDoc))) {
			return null;
		}

		return (ImageDoc)pDoc;
	} // getDoc
} // class ImageDoc