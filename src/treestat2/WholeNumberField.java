package treestat2;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class WholeNumberField extends JTextField
        implements FocusListener, DocumentListener {

    private static final long serialVersionUID = 1L;

    protected static char MINUS_CHAR = '-';
    protected EventListenerList changeListeners = new EventListenerList();
    protected long min;
    protected long max;
    protected boolean range_check = false;
    protected boolean range_checked = false;

    public WholeNumberField() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public WholeNumberField(int min, int max) {
        super();
        this.min = min;
        this.max = max;
        range_check = true;
        this.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent evt) {
    }

    @Override
    public void focusLost(FocusEvent evt) {
        if (range_check && !range_checked) {
            range_checked = true;

            String text = getText().trim();
            if (text.isEmpty()) {
                setText("0"); // Set a default value if the field is empty
                return;
            }

            try {
                long value = Long.valueOf(text);
                if (value < min || value > max) {
                    errorMsg();
                }
            } catch (NumberFormatException e) {
                errorMsg();
            }
        }
    }

    protected void errorMsg() {
        JOptionPane.showMessageDialog(this,
                "Illegal entry\nValue must be between " + min + " and " +
                        max + " inclusive", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setValue(int value) {
        if (range_check) {
            if (value < min || value > max) {
                System.out.println("WholeNumberField.setValue");
                errorMsg();
                return;
            }
        }
        setText(Integer.toString(value));
    }

    public void setValue(long value) {
        if (range_check) {
            if (value < min || value > max) {
                System.out.println("WholeNumberField.setValue");
                errorMsg();
                return;
            }
        }
        setText(Long.toString(value));
    }

    public Integer getValue() {
        try {
            return Integer.valueOf(getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected Document createDefaultModel() {
        Document doc = new WholeNumberFieldDocument();
        doc.addDocumentListener(this);
        return doc;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        range_checked = false;
        fireChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        range_checked = false;
        fireChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        range_checked = false;
        fireChanged();
    }

    static char[] numberSet = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    class WholeNumberFieldDocument extends PlainDocument {
        private static final long serialVersionUID = 1L;

        @Override
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {

            if (str == null) return;
            str = str.trim();

            String buf = getText(0, offs) + str;
            char[] array = buf.toCharArray();

            if (array.length > 0) {
                if (array[0] != MINUS_CHAR && !member(array[0], numberSet)) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }

            for (int i = 1; i < array.length; i++) {
                if (!member(array[i], numberSet)) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            super.insertString(offs, str, a);
        }
    }

    static boolean member(char item, char[] array) {
        for (int i = 0; i < array.length; i++)
            if (array[i] == item) return true;
        return false;
    }
    //------------------------------------------------------------------------
    // Event Methods
    //------------------------------------------------------------------------

    protected void fireChanged() {
        ChangeEvent c = new ChangeEvent(this);
        Object[] listeners = changeListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ChangeListener cl = (ChangeListener) listeners[i + 1];
                cl.stateChanged(c);
            }
        }
    }
}
