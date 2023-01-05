# JICT

This project is a Java conversion of the application ICT (Image Compositing Toolkit) that is presented in the book Visual Special Effects Toolkit in C++, by Tim Wittenburg. As you can tell by the title of the book, this application was written in C++, Visual C++ to be precise, and it used some MFC classes. As it is a conversion of ICT to Java, I've named this repository JICT.

The book states on page xxiii:
I think it should be mentioned that this software is not intended to be an example of perfect programming technique. It is also not intended to be the ultimate application of object-oriented design. The focus was placed instead on creating a reasonably well-designed and documented body of code that could serve as a starting point for other efforts, and without too much difficulty, be ported to other environments. The software provided in this book is therefore offered humbly as a leg up to anyone interested in experimenting with the methods, expanding on them, and otherwise extending their knowledge of this fascinating subject.

So far my strategy has been:

-   Convert subclasses of CDialog to subclasses of JDialog.
-   Converted subclasses of CFrame to subclasses of JFrame.
-   Converted uses of CFile to JFileChooser.
-   Converted uses of MessageBox to JOptionPane.
-   Converted uses of mathematical functions (mostly cos, sin, sqrt and abs) to usage of the Math class in Java.

My strategy for handling data types has been:

-   int's that are being used as booleans were changed to boolean
-   other ints stayed as ints
-   float stayed as float. However for float literals I had to follow the float literal with an 'f', as otherwise the compiler would complain cannot convert double to float. Hence, for example,
    float r = 0.0;
    became
    float r = 0.0f;
-   BYTE was translated as byte. However, this has been problematic. In C/C++, a byte is 8-bits and an hold a value from 0 to 255. In Java, a byte is 8-bits and can hold a value from -128 to 127. This will require changes in the future. I imagine I will change BYTE to Java shorts.
-   character pointers became Strings
-   character arrays became Strings
-   instances of RBQUAD became instances of java.awt.Color

In subclasses of CDialog:

-   instances of CEdit became instances of JTextField
-   instances of CBUTTON became instances of JButton
-   instances of CCOMBOBOX became instances of JComboBox

*   instances of CTEXT became instances of JLabel

The menu for ICT was defined in ICT20.RC. Of course, to implement the menu in Java I used the JMenuBar, JMenu and JMenuItem classes.

## Handling parameters to methods

-   Handling object parameters to methods has been easy. In C++, the object type is often followed by an asterisk, indicating it is a pointer to an object of that type. In converting, I just removed the '\*'.
-   For integer and float parameters that are being modified in a method (so that the new value is seen by the calling method), I substitued Integer and Float parameters.

I was able to convert many of the sprintf statements to simple String concatenations in Java. I believe others will be converted using DecimalFormat.

There are still a lot of conversion to be done. Some changes I have planned are:

-   Usage of the strtok function will be changed to usage of the StringTokenizer class.
-   Usage of the RGBTRIPLE will be changed to usage of the java.awt.Color class.
-   Usage of SETCURSOR and LOADCURSOR will be changed to usage of the java.awt.Component class's setCursor and getCursor methods. But there is a glitch, in that the SETCURSOR and LOADCURSOR are used in MEMIMAGE.CPP, which does not extend CFrame nor CDialog.
-   The message handlers will have to be converted to event handlers.

I started the conversion on Dec. 21, 2022, so I'm still the early stages of converting.
