/*
 * SettingsDialog.java
 *
 * Created on May 26, 2005, 8:48 PM
 */

package org.cesilko.rachota.gui;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.RegularTask;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Dialog with system settings.
 * @author Jiri Kovalsky
 */
public class SettingsDialog extends javax.swing.JDialog implements PropertyChangeListener {
    
    /** Creates new dialog with system settings.
     * @param parent Parent component of this dialog i.e. main window.
     */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, true);
        regularTasks = (Vector) Plan.getDefault().getRegularTasks().clone();
        initComponents();
        setLocationRelativeTo(parent);
        txtHours.setText((String) Settings.getDefault().getSetting("dayWorkHours"));
        chbHoursNotReached.setSelected(((Boolean) Settings.getDefault().getSetting("warnHoursNotReached")).booleanValue());
        chbHoursExceeded.setSelected(((Boolean) Settings.getDefault().getSetting("warnHoursExceeded")).booleanValue());
        chbMoveUnfinished.setSelected(((Boolean) Settings.getDefault().getSetting("moveUnfinished")).booleanValue());
        chbArchiveNotStarted.setSelected(((Boolean) Settings.getDefault().getSetting("archiveNotStarted")).booleanValue());
        chbCheckPriority.setSelected(((Boolean) Settings.getDefault().getSetting("checkPriority")).booleanValue());
        chbCountPrivate.setSelected(((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue());
        tbRegularTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbRegularTasks.getColumn(Translator.getTranslation("TASK_DESCRIPTION")).setPreferredWidth(250);
        tbRegularTasks.getColumn(Translator.getTranslation("TASK_REGULAR")).setPreferredWidth(100);
        tbRegularTasks.getTableHeader().setForeground(java.awt.Color.BLUE);
        tbRegularTasks.getTableHeader().setBackground(java.awt.Color.LIGHT_GRAY);
        tbRegularTasks.getTableHeader().setFont(getFont());
        tbRegularTasks.setFont(getFont());
        tbRegularTasks.setRowHeight(getFont().getSize() + 2);
    }
    
    /** Returns font that should be used for all widgets in this component
     * based on the language preferences specified by user.
     * @return Font to be used in this component.
     */
    public java.awt.Font getFont() {
        return new java.awt.Font((String) Settings.getDefault().getSetting("fontName"), java.awt.Font.PLAIN, Integer.parseInt((String) Settings.getDefault().getSetting("fontSize")));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnGeneral = new javax.swing.JPanel();
        lblWorkingHours = new javax.swing.JLabel();
        txtHours = new javax.swing.JTextField();
        lblHours = new javax.swing.JLabel();
        lblWarn = new javax.swing.JLabel();
        chbHoursNotReached = new javax.swing.JCheckBox();
        chbHoursExceeded = new javax.swing.JCheckBox();
        chbMoveUnfinished = new javax.swing.JCheckBox();
        chbArchiveNotStarted = new javax.swing.JCheckBox();
        chbCheckPriority = new javax.swing.JCheckBox();
        chbCountPrivate = new javax.swing.JCheckBox();
        pnRegularTasks = new javax.swing.JPanel();
        spRegularTasks = new javax.swing.JScrollPane();
        tbRegularTasks = new javax.swing.JTable();
        pnButtons = new javax.swing.JPanel();
        btAdd = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Translator.getTranslation("SETTINGSDIALOG.TITLE"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnGeneral.setLayout(new java.awt.GridBagLayout());

        pnGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), Translator.getTranslation("SETTINGSDIALOG.BORDER_GENERAL"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnGeneral.setFont(getFont());
        lblWorkingHours.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_WORKING_HOURS"));
        lblWorkingHours.setFont(getFont());
        lblWorkingHours.setLabelFor(txtHours);
        lblWorkingHours.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_WORKING_HOURS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblWorkingHours, gridBagConstraints);

        txtHours.setText("8.0");
        txtHours.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_HOURS_TOOLTIP"));
        txtHours.setPreferredSize(new java.awt.Dimension(30, 19));
        txtHours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHoursFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(txtHours, gridBagConstraints);

        lblHours.setFont(getFont());
        lblHours.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_HOURS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblHours, gridBagConstraints);

        lblWarn.setFont(getFont());
        lblWarn.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_WARN"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblWarn, gridBagConstraints);

        chbHoursNotReached.setFont(getFont());
        chbHoursNotReached.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_HOURS_NOT_REACHED"));
        chbHoursNotReached.setSelected(true);
        chbHoursNotReached.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_HOURS_NOT_REACHED"));
        chbHoursNotReached.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_HOURS_NOT_REACHED_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbHoursNotReached, gridBagConstraints);

        chbHoursExceeded.setFont(getFont());
        chbHoursExceeded.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_HOURS_EXCEEDED"));
        chbHoursExceeded.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_HOURS_EXCEEDED"));
        chbHoursExceeded.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_HOURS_EXCEEDED_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbHoursExceeded, gridBagConstraints);

        chbMoveUnfinished.setFont(getFont());
        chbMoveUnfinished.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_MOVE_UNFINISHED"));
        chbMoveUnfinished.setSelected(true);
        chbMoveUnfinished.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_MOVE_UNFINISHED"));
        chbMoveUnfinished.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_MOVE_UNFINISHED_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbMoveUnfinished, gridBagConstraints);

        chbArchiveNotStarted.setFont(getFont());
        chbArchiveNotStarted.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_ARCHIVE_NOT_STARTED"));
        chbArchiveNotStarted.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_ARCHIVE_NOT_STARTED"));
        chbArchiveNotStarted.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_ARCHIVE_NOT_STARTED_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbArchiveNotStarted, gridBagConstraints);

        chbCheckPriority.setFont(getFont());
        chbCheckPriority.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_CHECK_PRIORITY"));
        chbCheckPriority.setSelected(true);
        chbCheckPriority.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_CHECK_PRIORITY"));
        chbCheckPriority.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_CHECK_PRIORITY_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbCheckPriority, gridBagConstraints);

        chbCountPrivate.setFont(getFont());
        chbCountPrivate.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_COUNT_PRIVATE"));
        chbCountPrivate.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_COUNT_PRIVATE"));
        chbCountPrivate.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_COUNT_PRIVATE_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbCountPrivate, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(pnGeneral, gridBagConstraints);

        pnRegularTasks.setLayout(new java.awt.GridBagLayout());

        pnRegularTasks.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), Translator.getTranslation("SETTINGSDIALOG.BORDER_REGULAR_TASKS"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnRegularTasks.setFont(getFont());
        spRegularTasks.setPreferredSize(new java.awt.Dimension(350, 150));
        tbRegularTasks.setFont(getFont());
        tbRegularTasks.setModel(new RegularTasksTableModel(regularTasks));
        tbRegularTasks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbRegularTasksKeyReleased(evt);
            }
        });
        tbRegularTasks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRegularTasksMouseClicked(evt);
            }
        });

        spRegularTasks.setViewportView(tbRegularTasks);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnRegularTasks.add(spRegularTasks, gridBagConstraints);

        btAdd.setFont(getFont());
        btAdd.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.BT_ADD"));
        btAdd.setText(Translator.getTranslation("SETTINGSDIALOG.BT_ADD"));
        btAdd.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.BT_ADD_TOOLTIP"));
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        pnButtons.add(btAdd);

        btEdit.setFont(getFont());
        btEdit.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.BT_EDIT"));
        btEdit.setText(Translator.getTranslation("SETTINGSDIALOG.BT_EDIT"));
        btEdit.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.BT_EDIT_TOOLTIP"));
        btEdit.setEnabled(false);
        btEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEditActionPerformed(evt);
            }
        });

        pnButtons.add(btEdit);

        btRemove.setFont(getFont());
        btRemove.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.BT_REMOVE"));
        btRemove.setText(Translator.getTranslation("SETTINGSDIALOG.BT_REMOVE"));
        btRemove.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.BT_REMOVE_TOOLTIP"));
        btRemove.setEnabled(false);
        btRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRemoveActionPerformed(evt);
            }
        });

        pnButtons.add(btRemove);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnRegularTasks.add(pnButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(pnRegularTasks, gridBagConstraints);

        btOK.setFont(getFont());
        btOK.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.BT_OK"));
        btOK.setText(Translator.getTranslation("SETTINGSDIALOG.BT_OK"));
        btOK.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.BT_OK_TOOLTIP"));
        btOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOKActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btOK, gridBagConstraints);

        btCancel.setFont(getFont());
        btCancel.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.BT_CANCEL"));
        btCancel.setText(Translator.getTranslation("SETTINGSDIALOG.BT_CANCEL"));
        btCancel.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.BT_CANCEL_TOOLTIP"));
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btCancel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /** Method called when any key was released while table with regular tasks had focus.
     * @param evt Event that invoked this method call.
     */
    private void tbRegularTasksKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbRegularTasksKeyReleased
        checkButtons();
    }//GEN-LAST:event_tbRegularTasksKeyReleased
    
    /** Method called when textfield with working hours lost its focus.
     * @param evt Event that invoked this method call.
     */
    private void txtHoursFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHoursFocusLost
        if (evt.getOppositeComponent() == null) return; // In case of closing window via X button, do not check validity now
        if (evt.getOppositeComponent() instanceof javax.swing.JButton) return; // In case of any button, do not check validity either
        workHoursValid();
    }//GEN-LAST:event_txtHoursFocusLost
    
    /** Method called when remove button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoveActionPerformed
        if (!workHoursValid()) return;
        int row = tbRegularTasks.getSelectedRow();
        RegularTask regularTask = (RegularTask) regularTasks.get(row);
        String description = regularTask.getDescription();String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
        int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.REMOVE_REGULAR_TASK", new String[] {description}), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if (decision == JOptionPane.NO_OPTION) return;
        regularTasks.remove(regularTask);
        RegularTasksTableModel regularTasksTableModel = (RegularTasksTableModel) tbRegularTasks.getModel();
        regularTasksTableModel.fireTableDataChanged();
        checkButtons();
    }//GEN-LAST:event_btRemoveActionPerformed
    
    /** Method called when edit button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditActionPerformed
        if (!workHoursValid()) return;
        int row = tbRegularTasks.getSelectedRow();
        RegularTask regularTask = (RegularTask) regularTasks.get(row);
        TaskDialog taskDialog = new TaskDialog(regularTask);
        taskDialog.addPropertyChangeListener(this);
        taskDialog.setVisible(true);
    }//GEN-LAST:event_btEditActionPerformed
    
    /** Method called when add button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        if (!workHoursValid()) return;
        TaskDialog taskDialog = new TaskDialog();
        taskDialog.addPropertyChangeListener(this);
        taskDialog.setVisible(true);
    }//GEN-LAST:event_btAddActionPerformed
    
    /** Method called when user clicked into table with regular tasks.
     * @param evt Event that invoked this method call.
     */
    private void tbRegularTasksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRegularTasksMouseClicked
        checkButtons();
    }//GEN-LAST:event_tbRegularTasksMouseClicked
    
    /** Method called when cancel button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_btCancelActionPerformed
    
    /** Method called when ok button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOKActionPerformed
        if (!workHoursValid()) return;
        formWindowClosing(null);
        Settings.getDefault().setSetting("dayWorkHours", txtHours.getText());
        Settings.getDefault().setSetting("warnHoursNotReached", new Boolean(chbHoursNotReached.isSelected()));
        Settings.getDefault().setSetting("warnHoursExceeded", new Boolean(chbHoursExceeded.isSelected()));
        Settings.getDefault().setSetting("moveUnfinished", new Boolean(chbMoveUnfinished.isSelected()));
        Settings.getDefault().setSetting("archiveNotStarted", new Boolean(chbArchiveNotStarted.isSelected()));
        Settings.getDefault().setSetting("checkPriority", new Boolean(chbCheckPriority.isSelected()));
        Settings.getDefault().setSetting("countPrivateTasks", new Boolean(chbCountPrivate.isSelected()));
        Plan.getDefault().getRegularTasks().clear();
        Iterator iterator = regularTasks.iterator();
        while (iterator.hasNext()) {
            RegularTask regularTask = (RegularTask) iterator.next();
            Plan.getDefault().addRegularTask(regularTask);
        }
        firePropertyChange("settings", null, Settings.getDefault());
        Plan.saveRegularTasks();
        Settings.saveSettings();
    }//GEN-LAST:event_btOKActionPerformed
    
    /** Method called when this dialog is being closed.
     * @param evt Event that invoked this method call.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btEdit;
    private javax.swing.JButton btOK;
    private javax.swing.JButton btRemove;
    private javax.swing.JCheckBox chbArchiveNotStarted;
    private javax.swing.JCheckBox chbCheckPriority;
    private javax.swing.JCheckBox chbCountPrivate;
    private javax.swing.JCheckBox chbHoursExceeded;
    private javax.swing.JCheckBox chbHoursNotReached;
    private javax.swing.JCheckBox chbMoveUnfinished;
    private javax.swing.JLabel lblHours;
    private javax.swing.JLabel lblWarn;
    private javax.swing.JLabel lblWorkingHours;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JPanel pnGeneral;
    private javax.swing.JPanel pnRegularTasks;
    private javax.swing.JScrollPane spRegularTasks;
    private javax.swing.JTable tbRegularTasks;
    private javax.swing.JTextField txtHours;
    // End of variables declaration//GEN-END:variables
    
    /** Vector of currently planned regular tasks. */
    private Vector regularTasks;
    
    /** If any regular task is selected in the table enable both edit and remove buttons.
     */
    private void checkButtons() {
        int row = tbRegularTasks.getSelectedRow();
        btEdit.setEnabled(row != -1);
        btRemove.setEnabled(row != -1);
    }
    
    /** Check validity of number provided as working hours and warn user if it is invalid.
     * @return True if number provided as working hours is valid double number e.g. 8.5 or false otherwise.
     */
    private boolean workHoursValid() {
        boolean valid = true;
        try {
            Double.parseDouble(txtHours.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.INVALID_WORKING_HOURS"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            txtHours.setText((String) Settings.getDefault().getSetting("dayWorkHours"));
            valid = false;
        }
        return valid;
    }

    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().startsWith("task_")) {
            TaskDialog taskDialog = (TaskDialog) evt.getSource();
            taskDialog.removePropertyChangeListener(this);
            checkButtons();
        }
        if (evt.getPropertyName().equals("task_created")) {
            Task task = (Task) evt.getNewValue();
            regularTasks.add(task);
        }
        RegularTasksTableModel regularTasksTableModel = (RegularTasksTableModel) tbRegularTasks.getModel();
        regularTasksTableModel.fireTableDataChanged();
    }
}