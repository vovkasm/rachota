/*
 * CompletionWindow.java
 *
 * Created on September 8, 2006, 6:57 PM
 */

package org.cesilko.rachota.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;
import org.cesilko.rachota.core.Translator;

/** Window containing list of all suitable completion items.
 * @author Jiri Kovalsky
 */
public class CompletionWindow extends javax.swing.JDialog {
    
    /** Text component that invoked this completion window. */
    private JTextComponent textComponent;
    /** List of all possible completion items. */
    private Vector allCompletionItems;
    
    /** Creates new form CompletionWindow
     * @param textComponent Text component that invoked this completion window.
     * @param allCompletionItems List of all possible completion items.
     */
    public CompletionWindow(JTextComponent textComponent, Vector allCompletionItems) {
        this.textComponent = textComponent;
        this.allCompletionItems = allCompletionItems;
        initComponents();
        jlCompletion.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlCompletion.setModel(new CompletionListModel());
        setCompletionItems(getCompletionItems(getIncompleteWord()));
        jlCompletion.setSelectedIndex(0);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        spCompletion = new javax.swing.JScrollPane();
        jlCompletion = new javax.swing.JList();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setModal(true);
        setUndecorated(true);
        spCompletion.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jlCompletion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlCompletionMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jlCompletionMouseExited(evt);
            }
        });
        jlCompletion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jlCompletionFocusLost(evt);
            }
        });
        jlCompletion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jlCompletionKeyPressed(evt);
            }
        });

        spCompletion.setViewportView(jlCompletion);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(spCompletion, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jlCompletionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlCompletionMouseClicked
        if (evt.getClickCount() == 2) completeItem((String) jlCompletion.getSelectedValue());
    }//GEN-LAST:event_jlCompletionMouseClicked

    private void jlCompletionMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlCompletionMouseExited
        setVisible(false);
    }//GEN-LAST:event_jlCompletionMouseExited

    private void jlCompletionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jlCompletionFocusLost
        setVisible(false);
    }//GEN-LAST:event_jlCompletionFocusLost
    
    private void jlCompletionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jlCompletionKeyPressed
        int keyCode = evt.getKeyCode();
        int keyChar = (int) evt.getKeyChar();
        if ((keyChar == 32) | (keyChar == 127)) return;
        if (((keyChar > 30) & (keyChar < 256)) | (keyCode == 8)) reduceCompletionBy(evt.getKeyChar());
        if (keyCode == 10) {
            String value = (String) jlCompletion.getSelectedValue();
            if (value.equals(Translator.getTranslation("COMPLETION.NONE"))) return;
            completeItem(value);
        }
        if ((evt.getKeyCode() == 38) & (jlCompletion.getSelectedIndex() == 0)) {
            jlCompletion.setSelectedValue(jlCompletion.getModel().getElementAt(jlCompletion.getModel().getSize() - 1), true);
            evt.consume();
        }
        if ((evt.getKeyCode() == 40) & (jlCompletion.getSelectedIndex() == jlCompletion.getModel().getSize() - 1)) {
            jlCompletion.setSelectedValue(jlCompletion.getModel().getElementAt(0), true);
            evt.consume();
        }
        if ((keyCode == 27) | (keyCode == 10)) setVisible(false);
    }//GEN-LAST:event_jlCompletionKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jlCompletion;
    private javax.swing.JScrollPane spCompletion;
    // End of variables declaration//GEN-END:variables
    
    /** Sets list of completion items to given vector.
     * @param completionItems New list of completion items.
     */
    public void setCompletionItems(Vector completionItems) {
        CompletionListModel model = (CompletionListModel) jlCompletion.getModel();
        model.setItems(completionItems);
        String prefix = textComponent.getText().substring(0, textComponent.getCaretPosition());
        Point position = textComponent.getLocationOnScreen();
        int maxHeight = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - position.getY()) - 50;
        position.setLocation(position.x + getFontMetrics(getFont()).stringWidth(prefix) + 3, position.y);
        setLocation(position);
        int height = jlCompletion.getPreferredSize().height + 4;
        if (height > maxHeight) height = maxHeight;
        setSize(new Dimension(jlCompletion.getPreferredSize().width + 4, height));
        repaint();
    }
    
    /** Returns list of completion items that start with given prefix.
     * @param prefix Prefix to be used for finding suitable completion items.
     * @return List of all completion items that begin with given prefix.
     */
    private Vector getCompletionItems(String prefix) {
        Vector filteredItems = new Vector();
        if (prefix.equals("")) filteredItems = (Vector) allCompletionItems.clone();
        else {
            Iterator iterator = allCompletionItems.iterator();
            while(iterator.hasNext()) {
                String item = (String) iterator.next();
                if (item.startsWith(prefix)) filteredItems.add(item);
            }
        }
        Vector alreadyUsedItems = getUsedItems();
        Iterator iterator = alreadyUsedItems.iterator();
        while (iterator.hasNext()) {
            String item = (String) iterator.next();
            if (filteredItems.contains(item)) filteredItems.remove(item);
        }
        if (filteredItems.size() == 0) filteredItems.add(Translator.getTranslation("COMPLETION.NONE"));
        return filteredItems;
    }
    
    /** Reduces list of completion items by given character.
     * @param key New character that user typed to extend prefix.
     */
    private void reduceCompletionBy(char key) {
        String word = getIncompleteWord();
        if (key == 8) {
            if (word.length() > 0) word = word.substring(0, word.length() - 1);
        } else word = word + key;
        int caretPosition = textComponent.getCaretPosition();
        textComponent.setText(getPrefix() + word + getSuffix());
        caretPosition = caretPosition + (key == 8 ? -1 : +1);
        if (caretPosition == -1) caretPosition = 0;
        textComponent.setCaretPosition(caretPosition);
        Vector newItems = getCompletionItems(word);
        setCompletionItems(newItems);
        if (newItems.get(0).equals(Translator.getTranslation("COMPLETION.NONE"))) jlCompletion.setSelectedIndex(0);
    }
    
    /** Completes text in text component with selected completion item.
     * @param value Selected completion item to be added to text component.
     */
    private void completeItem(String value) {
        String prefix = getPrefix();
        String suffix = getSuffix();
        if (suffix.equals("")) suffix = " ";
        textComponent.setText(prefix + value + suffix);
        setVisible(false);
    }
    
    /** Returns part of word that user typed to narrow list of all completion items.
     * i.e. "meeting int|" returns "int".
     * @return Incomplete word that user typed to narrow list of all completion items.
     */
    private String getIncompleteWord() {
        String text = textComponent.getText();
        String prefix = text.substring(0, textComponent.getCaretPosition());
        int spaceIndex = prefix.lastIndexOf(" ");
        if (spaceIndex == -1) return prefix;
        return text.substring(spaceIndex + 1, textComponent.getCaretPosition());
    }
    
    /** Returns text that is before currently incomplete word.
     * i.e. "meeting int|" returns "meeting ".
     * @return Text that is before currently incomplete word.
     */
    private String getPrefix() {
        String text = textComponent.getText();
        String prefix = text.substring(0, textComponent.getCaretPosition());
        int spaceIndex = prefix.lastIndexOf(" ");
        if (spaceIndex == -1) return "";
        return text.substring(0, spaceIndex + 1);
    }
    
    /** Returns text that is after currently incomplete word.
     * i.e. "meeting int| hello" returns " hello".
     * @return Text that is after currently incomplete word.
     */
    private String getSuffix() {
        return textComponent.getText().substring(textComponent.getCaretPosition());
    }

    /** Returns list of items that were already used in the text component.
     * i.e. "meeting int| hello" returns "meeting" and "hello".
     * @return Items that were already used in the text component.
     */
    private Vector getUsedItems() {
        Vector alreadyUsedItems = new Vector();
        String prefix = getPrefix() + " " + getSuffix();
        StringTokenizer tokenizer = new StringTokenizer(prefix);
        while (tokenizer.hasMoreTokens()) {
            String item = (String) tokenizer.nextToken();
            alreadyUsedItems.add(item);
        }
        return alreadyUsedItems;
    }
}