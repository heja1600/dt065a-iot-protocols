package util;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import java.util.Objects;

import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import model.swing.JTextFieldLimit;

public class SwingUtil {

    public interface SetValue<T> extends EventListener {
        /**
         * Invoked when the target of the listener has changed its state.
         *
         * @param e  a ChangeEvent object
         */
        void setValue(T object, String value);
    }
    
   
    /**
     * Installs a listener to receive notification when the text of any
     * {@code JTextComponent} is changed. Internally, it installs a
     * {@link DocumentListener} on the text component's {@link Document}, and a
     * {@link PropertyChangeListener} on the text component to detect if the
     * {@code Document} itself is replaced.
     * 
     * @param text           any text component, such as a {@link JTextField} or
     *                       {@link JTextArea}
     * @param changeListener a listener to receieve {@link ChangeEvent}s when the
     *                       text is changed; the source object for the events will
     *                       be the text component
     * @throws NullPointerException if either parameter is null
     */
    public static <T> void addChangeListener(JTextComponent text, T object, SetValue<T> valueSetter) {


        Objects.requireNonNull(text);
        Objects.requireNonNull(valueSetter);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        valueSetter.setValue(object, text.getText());
                    }
                });
            }

        };

        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document) e.getOldValue();
            Document d2 = (Document) e.getNewValue();
            if (d1 != null)
                d1.removeDocumentListener(dl);
            if (d2 != null)
                d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null)
            d.addDocumentListener(dl);

    }
    public static JTextField createTextField(String label, String defaultValue,int ... maxAndMin ) {
        JLabel jLabel = new JLabel( "Payload:" );
        JTextField textField = new JTextField(defaultValue);
        if(maxAndMin != null && maxAndMin.length > 0) {
            textField.setDocument(new JTextFieldLimit(maxAndMin[0]));
        }

        return textField;
    }

    public static JTextField createNumberInput(String label, Integer defaultValue) {
        return createNumberInput(label, defaultValue, null);
    }

    public static JTextField createNumberInput(String label, Integer defaultValue, Integer length) {
    
        JLabel jLabel = new JLabel( "Payload:" );
        JTextField textField = new JTextField(defaultValue);
        if(length != null) {
            textField.setDocument(new JTextFieldLimit(length));
        }
   
        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
               if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9' || ke.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                  textField.setEditable(true);
                  jLabel.setText("");
               } else {
                  textField.setEditable(false);
                  jLabel.setText("* Enter only numeric digits(0-9)");
               }
            }
         });
        return textField;
    }

    
    public static JPanel group( Component [] leftArray, Component [] rightArray) {

        // Create a sequential group for the horizontal axis.
        JPanel panel = new JPanel();

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
    
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        {
            ParallelGroup parallelGroup = layout.createParallelGroup();
            for(Component left: leftArray) {
                parallelGroup.addComponent(left);
            }
            hGroup.addGroup(parallelGroup);
        }
        
        {
            ParallelGroup parallelGroup = layout.createParallelGroup();
            for(Component right: rightArray) {
                parallelGroup.addComponent(right);
            }
            hGroup.addGroup(parallelGroup);
        }

        layout.setHorizontalGroup(hGroup);
     
        {
            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            for(var i = 0; i < leftArray.length && i < rightArray.length; i++) {
                ParallelGroup parallelGroup = layout.createParallelGroup(Alignment.BASELINE);
                parallelGroup.addComponent(leftArray[i]).addComponent(rightArray[i]);
                vGroup.addGroup(parallelGroup);
            }
            layout.setVerticalGroup(vGroup);
        }
    
        return panel;
    }


    public static void setJComboBoxReadOnly(JComboBox jcb)
    {
        JTextField jtf = (JTextField)jcb.getEditor().getEditorComponent();
        jtf.setEditable(false);

        MouseListener[] mls = jcb.getMouseListeners();
        for (MouseListener listener : mls)
            jcb.removeMouseListener(listener);

        Component[] comps = jcb.getComponents();
        for (Component c : comps)
        {
            if (c instanceof AbstractButton)
            {
                AbstractButton ab = (AbstractButton)c;
                ab.setEnabled(false);

                MouseListener[] mls2 = ab.getMouseListeners();
                for (MouseListener listener : mls2)
                    ab.removeMouseListener(listener);
            }
        }
    }
}
