package dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
		setSize(300, 145);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

		Box vertBox = Box.createVerticalBox();
        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
		Box row03Box = Box.createHorizontalBox();
		Box row04Box = Box.createHorizontalBox();
		Box row05Box = Box.createHorizontalBox();

		lblAppName = new JLabel("Java Image Composition Toolkit, Version 1.2");
		row01Box.add(lblAppName);

		lblByName1 = new JLabel("Original C++ code by TIm Wittenburg");
		row02Box.add(lblByName1);

		lblByName2 = new JLabel("Java version by David de Leon");
		row03Box.add(lblByName2);

		Component rowSpacer = Box.createRigidArea(new Dimension(180, 10));
		row04Box.add(rowSpacer);

		btnOk = new JButton("OK");
		btnOk.addActionListener(this);
		row05Box.add(btnOk);

		vertBox.add(row01Box);
		vertBox.add(row02Box);
		vertBox.add(row03Box);
		vertBox.add(row04Box);
		vertBox.add(row05Box);

		panel.add(vertBox);
		add(panel);
		setVisible(true);
    }


	// Called when the OK button is clicked on
	public void actionPerformed(ActionEvent ae) {
		dispose();
	}
}