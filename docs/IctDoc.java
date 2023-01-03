package docs;

public class IctDoc {
/*
class CIctDoc : public CDocument
{
protected: // create from serialization only
	CIctDoc();
	DECLARE_DYNCREATE(CIctDoc)

// Attributes
public:

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIctDoc)
	public:
	virtual BOOL OnNewDocument();
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CIctDoc();
	virtual void Serialize(CArchive& ar);   // overridden for document i/o
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	//{{AFX_MSG(CIctDoc)
	afx_msg void OnFileOpen();
	afx_msg void OnFileOpenimage();
	afx_msg void OnPreviewScene();
	afx_msg void OnPreviewSequence();
	afx_msg void OnRenderDepthsorting();
	afx_msg void OnRenderScene();
	afx_msg void OnRenderSequence();
	afx_msg void OnToolsCreatealphaimage();
	afx_msg void OnToolsCreateascenelist();
	afx_msg void OnToolsCreatecutout();
	afx_msg void OnToolsSysteminformation();
	afx_msg void OnToolsWarpimage();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
*/

    // This constructor came from ICT20.CPP
    public void IctDoc() {
        // TODO: add one-time construction code here

    }


    // This destructor came from ICT20.CPP
    public void finalize() {
    }


    // This method came from ICT20.CPP
    boolean OnNewDocument() {
        if (!CDocument.OnNewDocument()) {
            return false;
        }

        // TODO: add reinitialization code here
        // (SDI documents will reuse this document)

        return true;
    }

	
    // This method came from ICT20.CPP
    void Serialize(CArchive ar) {
        if (ar.IsStoring()) {
            ((CEditView)m_viewList.GetHead()).SerializeRaw(ar);
        } else {
            ((CEditView)m_viewList.GetHead()).SerializeRaw(ar);
        }
    }
} // class IctDoc