/*
 * FilterDialog.java
 *
 * Created on 20 August 2005, 22:35
 */

package org.cesilko.rachota.gui;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.*;

/** Dialog allowing to create new or edit existing task filter.
 * @author  Jiri Kovalsky
 */
public class FilterDialog extends javax.swing.JDialog {
    
    /** Filters table model holding all task filters. */
    FiltersTableModel filtersTableModel;
    /** Task filter to be edited. */
    AbstractTaskFilter taskFilter;
    
    /** Creates new form filter dialog for editing provided task filter.
     * @param filtersTableModel Table model with all selected task filters.
     * @param taskFilter Task filter to be edited.
     */
    public FilterDialog(FiltersTableModel filtersTableModel, AbstractTaskFilter taskFilter) {
        this.filtersTableModel = filtersTableModel;
        this.taskFilter = taskFilter;
        initComponents();
        cmbFilterName.addItem(new DescriptionFilter());
        cmbFilterName.addItem(new KeywordFilter());
        cmbFilterName.addItem(new DurationFilter());
        cmbFilterName.addItem(new PriorityFilter());
        cmbFilterName.addItem(new StateFilter());
        cmbFilterName.setSelectedIndex(0);
        if (taskFilter instanceof KeywordFilter) cmbFilterName.setSelectedIndex(1);
        if (taskFilter instanceof DurationFilter) cmbFilterName.setSelectedIndex(2);
        if (taskFilter instanceof PriorityFilter) cmbFilterName.setSelectedIndex(3);
        if (taskFilter instanceof StateFilter) cmbFilterName.setSelectedIndex(4);
        setComponents(taskFilter == null ? new DescriptionFilter() : taskFilter);
    }
    
    /** Creates new form filter dialog for creating new task filter.
     * @param filtersTableModel Table model with all selected task filters.
     */
    public FilterDialog(FiltersTableModel filtersTableModel) {
        this(filtersTableModel, null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbFilterName = new javax.swing.JLabel();
        cmbFilterName = new javax.swing.JComboBox();
        lblContentRule = new javax.swing.JLabel();
        cmbContentRule = new javax.swing.JComboBox();
        lblContent = new javax.swing.JLabel();
        cmbContent = new javax.swing.JComboBox();
        txtContent = new javax.swing.JTextField();
        pnButtons = new javax.swing.JPanel();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle(Translator.getTranslation("FILTERDIALOG.TITLE"));
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lbFilterName.setDisplayedMnemonic(Translator.getMnemonic("FILTERDIALOG.NAME"));
        lbFilterName.setLabelFor(cmbFilterName);
        lbFilterName.setText(Translator.getTranslation("FILTERDIALOG.NAME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lbFilterName, gridBagConstraints);

        cmbFilterName.setToolTipText(Translator.getTranslation("FILTERDIALOG.NAME_TOOLTIP"));
        cmbFilterName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbFilterNameItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cmbFilterName, gridBagConstraints);

        lblContentRule.setDisplayedMnemonic(Translator.getMnemonic("FILTERDIALOG.CONTENT_RULE"));
        lblContentRule.setLabelFor(cmbContentRule);
        lblContentRule.setText(Translator.getTranslation("FILTERDIALOG.CONTENT_RULE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblContentRule, gridBagConstraints);

        cmbContentRule.setToolTipText(Translator.getTranslation("FILTERDIALOG.CONTENT_RULE_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cmbContentRule, gridBagConstraints);

        lblContent.setDisplayedMnemonic(Translator.getMnemonic("FILTERDIALOG.CONTENT"));
        lblContent.setLabelFor(txtContent);
        lblContent.setText(Translator.getTranslation("FILTERDIALOG.CONTENT"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblContent, gridBagConstraints);

        cmbContent.setToolTipText(Translator.getTranslation("FILTERDIALOG.CONTENT_TOOLTIP"));
        cmbContent.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cmbContent, gridBagConstraints);

        txtContent.setToolTipText(Translator.getTranslation("FILTERDIALOG.CONTENT_TOOLTIP"));
        txtContent.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(txtContent, gridBagConstraints);

        pnButtons.setLayout(new java.awt.GridBagLayout());

        btOK.setMnemonic(Translator.getMnemonic("FILTERDIALOG.BT_OK"));
        btOK.setText(Translator.getTranslation("FILTERDIALOG.BT_OK"));
        btOK.setToolTipText(Translator.getTranslation("FILTERDIALOG.BT_OK_TOOLTIP"));
        btOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOKActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnButtons.add(btOK, gridBagConstraints);

        btCancel.setMnemonic(Translator.getMnemonic("FILTERDIALOG.BT_CANCEL"));
        btCancel.setText(Translator.getTranslation("FILTERDIALOG.BT_CANCEL"));
        btCancel.setToolTipText(Translator.getTranslation("FILTERDIALOG.BT_CANCEL_TOOLTIP"));
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnButtons.add(btCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(pnButtons, gridBagConstraints);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-265)/2, (screenSize.height-240)/2, 265, 240);
    }
    // </editor-fold>//GEN-END:initComponents
    
    /** Method called when any filter is selected.
     * @param evt Event that invoked this action.
     */
    private void cmbFilterNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbFilterNameItemStateChanged
        AbstractTaskFilter taskFilter = (AbstractTaskFilter) cmbFilterName.getSelectedItem();
        setComponents(taskFilter);
    }//GEN-LAST:event_cmbFilterNameItemStateChanged
    
    /** Method called when dialog is being closed.
     * @param evt Event that invoked this action.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing
    
    /** Method called when Cancel button was pressed.
     * @param evt Event that invoked this action.
     */
    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_btCancelActionPerformed
    
    /** Method called when OK button was pressed.
     * @param evt Event that invoked this action.
     */
    private void btOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOKActionPerformed
        AbstractTaskFilter newTaskFilter = (AbstractTaskFilter) cmbFilterName.getItemAt(cmbFilterName.getSelectedIndex());
        newTaskFilter.setContentRule(cmbContentRule.getSelectedIndex());
        String content = null;
        if (newTaskFilter instanceof DescriptionFilter) content = txtContent.getText();
        if (newTaskFilter instanceof KeywordFilter) content = txtContent.getText();
        if (newTaskFilter instanceof DurationFilter) {
            String text = txtContent.getText();
            long duration;
            try {
                if (text.length() != 8) throw new NumberFormatException("Error: invalid task duration specified: " + text);
                duration = Tools.getTime(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.INVALID_DURATION"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
                txtContent.setText(Tools.getTime(0));
                return;
            }
            content = Tools.getTime(duration);
        }
        if (newTaskFilter instanceof PriorityFilter) content = "" + cmbContent.getSelectedIndex();
        if (newTaskFilter instanceof StateFilter) content = "" + cmbContent.getSelectedIndex();
        newTaskFilter.setContent(content);
        if (taskFilter == null) filtersTableModel.addFilter(newTaskFilter);
        else filtersTableModel.replaceFilter(taskFilter, newTaskFilter);
        formWindowClosing(null);
    }//GEN-LAST:event_btOKActionPerformed
        // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOK;
    private javax.swing.JComboBox cmbContent;
    private javax.swing.JComboBox cmbContentRule;
    private javax.swing.JComboBox cmbFilterName;
    private javax.swing.JLabel lbFilterName;
    private javax.swing.JLabel lblContent;
    private javax.swing.JLabel lblContentRule;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JTextField txtContent;
    // End of variables declaration//GEN-END:variables
    
    /** Sets content rules and values according to given task filter.
     * @param taskFilter Task filter to be used for setting content
     * rules and values.
     */
    private void setComponents(AbstractTaskFilter taskFilter) {
        cmbContentRule.removeAllItems();
        Vector contentRules = taskFilter.getContentRules();
        int length = contentRules.size();
        for (int i=0; i<length; i++)
            cmbContentRule.addItem(contentRules.get(i));
        cmbContentRule.setSelectedIndex(taskFilter.getContentRule());
        Vector contentValues = taskFilter.getContentValues();
        cmbContent.setEnabled(contentValues != null);
        txtContent.setEnabled(contentValues == null);
        cmbContent.removeAllItems();
        if (contentValues != null) {
            length = contentValues.size();
            String value = taskFilter.getContent();
            for (int i=0; i<length; i++) {
                cmbContent.addItem(contentValues.get(i));
                if (contentValues.get(i).equals(value))
                    cmbContent.setSelectedIndex(i);
            }
        } else txtContent.setText(taskFilter.getContent());
    }
}