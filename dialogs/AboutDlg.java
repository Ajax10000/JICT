package dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutDlg extends JDialog implements ActionListener {
	JLabel lblAppName;
	JLabel lblByName1;
	JLabel lblByName2;
	JButton btnOk;

/*
class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	//}}AFX_DATA

// Implementation
protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//{{AFX_MSG(CAboutDlg)
		// No message handlers
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
	//{{AFX_DATA_INIT(CAboutDlg)
	//}}AFX_DATA_INIT
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAboutDlg)
	//}}AFX_DATA_MAP
}
 */


	// This constructor came from ICT20.CPP
	// But I modified it to take 2 parameters.
    public AboutDlg(JFrame pParent, boolean pModal) {
		super(pParent, pModal);

		setTitle("About JICT 1.2");

		lblAppName = new JLabel("Java Image Composition Toolkit, Version 1.2");
		lblByName1 = new JLabel("Original C++ code by TIm Wittenburg");
		lblByName2 = new JLabel("Java version by David de Leon");

		add(lblAppName);
		add(lblByName1);
		add(lblByName2);

		btnOk = new JButton("OK");
		add(btnOk);
    }

	// Called when btnOk is clicked on
	public void actionPerformed(ActionEvent ae) {

	}
}