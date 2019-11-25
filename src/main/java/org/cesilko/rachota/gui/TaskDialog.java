/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://rachota.sourceforge.net/license.txt.
 * 
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://rachota.sourceforge.net/license.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * The Original Software is Rachota.
 * The Initial Developer of the Original Software is Jiri Kovalsky
 * Portions created by Jiri Kovalsky are Copyright (C) 2006
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on 11 September 2004  22:00
 * TaskDialog.java
 */

package org.cesilko.rachota.gui;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.RegularTask;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Visualizer of task.
 *
 * @author  Jiri Kovalsky
 */
public class TaskDialog extends javax.swing.JDialog {
    
    /** Task that is being edited. */
    private Task task;
    /** Day which new task should be added to. */
    private Day day;
    /** Flag determining if task can be edited or not. */
    private boolean readOnly;
    
    /** Creates new dialog for editing of given task.
     * @param task Task which is going to be edited.
     * @param day Day which the task belongs to.
     * @param readOnly Flag determining if task can be edited or not.
     */
    public TaskDialog(Task task, Day day, boolean readOnly) {
        this.task = task;
        this.day = day;
        this.readOnly = readOnly;
        initComponents();
        txtDescription.setText(task.getDescription());
        txtCategory.setText(task.getKeyword());
        taNotes.setText(task.getNotes());
        cmbPriority.setSelectedIndex(task.getPriority());
        Date notificationTime = task.getNotificationTime();
        boolean notification = notificationTime != null;
        chbNotification.setSelected(notification);
        spHours.setEnabled(notification);
        lblColon.setEnabled(notification);
        spMinutes.setEnabled(notification);
        if (notification) {
            Calendar time = Calendar.getInstance();
            time.setTime(notificationTime);
            spHours.setValue(new Integer(time.get(Calendar.HOUR_OF_DAY)));
            spMinutes.setValue(new Integer(time.get(Calendar.MINUTE)));
        }
        chbAutoStart.setEnabled(notification);
        chbAutoStart.setSelected(task.automaticStart());
        boolean regular = task instanceof RegularTask;
        chbRegular.setSelected(regular);
        cmbRepetition.setEnabled(false);
        chbRegular.setEnabled(task == null);
        if (regular) {
            RegularTask regularTask = (RegularTask) task;
            cmbRepetition.setSelectedIndex(regularTask.getFrequency());
        }
        chbPrivate.setSelected(task.privateTask());
        if (readOnly) {
            txtDescription.setEditable(false);
            txtCategory.setEditable(false);
            taNotes.setEditable(false);
            chbPrivate.setEnabled(false);
            chbNotification.setEnabled(false);
            chbAutoStart.setEnabled(false);
            cmbPriority.setEnabled(false);
            spHours.setEnabled(false);
            spMinutes.setEnabled(false);
        }
        Dimension size = spHours.getPreferredSize();
        spHours.setPreferredSize(new Dimension((int) (size.getWidth()*1.5), (int) size.getHeight()));
        spMinutes.setPreferredSize(new Dimension((int) (size.getWidth()*1.5), (int) size.getHeight()));
        pack();
    }
    
    /** Creates new dialog for creating new task for given day.
     * @param day Day which new task should be added to.
     */
    public TaskDialog(Day day) {
        this.task = null;
        this.day = day;
        initComponents();
        chbRegular.setEnabled(false);
        cmbRepetition.setEnabled(false);
        chbStartTask.setEnabled(Plan.getDefault().isToday(day));
        chbStartTask.setSelected(((Boolean) Settings.getDefault().getSetting("startTask")).booleanValue());
    }
    
    /** Creates new dialog for editing existing regular task.
     * @param regularTask Regular task that should be edited.
     */
    public TaskDialog(RegularTask regularTask) {
        this(regularTask, null, false);
        chbRegular.setSelected(true);
        chbRegular.setEnabled(false);
        cmbRepetition.setEnabled(true);
    }
    
    /** Creates new dialog for creating new regular task.
     */
    public TaskDialog() {
        this.task = null;
        this.day = null;
        initComponents();
        chbRegular.setSelected(true);
        chbRegular.setEnabled(false);
        cmbRepetition.setEnabled(true);
    }
    
    /** Returns font that should be used for all widgets in this component
     * based on the language preferences specified by user.
     * @return Font to be used in this component.
     */
    public java.awt.Font getFont() {
        return Tools.getFont();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblDescription = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        lblCategory = new javax.swing.JLabel();
        txtCategory = new javax.swing.JTextField();
        lblNotes = new javax.swing.JLabel();
        spNotes = new javax.swing.JScrollPane();
        taNotes = new javax.swing.JTextArea();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        chbNotification = new javax.swing.JCheckBox();
        spHours = new javax.swing.JSpinner();
        lblColon = new javax.swing.JLabel();
        spMinutes = new javax.swing.JSpinner();
        chbAutoStart = new javax.swing.JCheckBox();
        chbRegular = new javax.swing.JCheckBox();
        cmbRepetition = new javax.swing.JComboBox();
        chbPrivate = new javax.swing.JCheckBox();
        chbStartTask = new javax.swing.JCheckBox();
        pnButtons = new javax.swing.JPanel();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setTitle(Translator.getTranslation("TASKDIALOG.TITLE"));
        setModal(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblDescription.setDisplayedMnemonic(Translator.getMnemonic("TASKDIALOG.LBL_DESCRIPTION"));
        lblDescription.setFont(getFont());
        lblDescription.setLabelFor(txtDescription);
        lblDescription.setText(Translator.getTranslation("TASKDIALOG.LBL_DESCRIPTION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblDescription, gridBagConstraints);

        txtDescription.setFont(getFont());
        txtDescription.setToolTipText(Translator.getTranslation("TASKDIALOG.TXT_DESCRIPTION_TOOLTIP"));
        txtDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDescriptionKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(txtDescription, gridBagConstraints);

        lblCategory.setDisplayedMnemonic(Translator.getMnemonic("TASKDIALOG.LBL_CATEGORY"));
        lblCategory.setFont(getFont());
        lblCategory.setLabelFor(txtCategory);
        lblCategory.setText(Translator.getTranslation("TASKDIALOG.LBL_CATEGORY"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblCategory, gridBagConstraints);

        txtCategory.setFont(getFont());
        txtCategory.setToolTipText(Translator.getTranslation("TASKDIALOG.TXT_CATEGORY_TOOLTIP"));
        txtCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCategoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCategoryFocusLost(evt);
            }
        });
        txtCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCategoryKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCategoryKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(txtCategory, gridBagConstraints);

        lblNotes.setDisplayedMnemonic(Translator.getMnemonic("TASKDIALOG.LBL_NOTES"));
        lblNotes.setFont(getFont());
        lblNotes.setLabelFor(taNotes);
        lblNotes.setText(Translator.getTranslation("TASKDIALOG.LBL_NOTES"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblNotes, gridBagConstraints);

        taNotes.setFont(getFont());
        taNotes.setRows(3);
        taNotes.setToolTipText(Translator.getTranslation("TASKDIALOG.TXT_NOTES_TOOLTIP"));
        taNotes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                taNotesKeyPressed(evt);
            }
        });
        spNotes.setViewportView(taNotes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(spNotes, gridBagConstraints);

        lblPriority.setDisplayedMnemonic(Translator.getMnemonic("TASKDIALOG.LBL_PRIORITY"));
        lblPriority.setFont(getFont());
        lblPriority.setLabelFor(cmbPriority);
        lblPriority.setText(Translator.getTranslation("TASKDIALOG.LBL_PRIORITY"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblPriority, gridBagConstraints);

        cmbPriority.setFont(getFont());
        for (int i=Task.PRIORITY_HIGH; i<=Task.PRIORITY_LOW; i++)
        cmbPriority.addItem(Task.getPriority(i));
        cmbPriority.setSelectedIndex(Task.PRIORITY_MEDIUM);
        cmbPriority.setToolTipText(Translator.getTranslation("TASKDIALOG.CMB_PRIORITY_TOOLTIP"));
        cmbPriority.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbPriorityKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cmbPriority, gridBagConstraints);

        chbNotification.setFont(getFont());
        chbNotification.setMnemonic(Translator.getMnemonic("TASKDIALOG.CHB_NOTIFICATION"));
        chbNotificationChanged(null);
        chbNotification.setText(Translator.getTranslation("TASKDIALOG.CHB_NOTIFICATION"));
        chbNotification.setToolTipText(Translator.getTranslation("TASKDIALOG.CHB_NOTIFICATION_TOOLTIP"));
        chbNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbNotificationChanged(evt);
            }
        });
        chbNotification.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbNotificationKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(chbNotification, gridBagConstraints);

        spHours.setFont(getFont());
        spHours.setToolTipText(Translator.getTranslation("TASKDIALOG.JSP_HOURS_TOOLTIP"));
        spHours.setMinimumSize(new java.awt.Dimension(40, 20));
        spHours.setPreferredSize(new java.awt.Dimension(40, 20));
        spHours.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spHoursStateChanged(evt);
            }
        });
        spHours.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spHoursKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(spHours, gridBagConstraints);

        lblColon.setText(":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(lblColon, gridBagConstraints);

        spMinutes.setFont(getFont());
        spMinutes.setToolTipText(Translator.getTranslation("TASKDIALOG.JSP_MINUTES_TOOLTIP"));
        spMinutes.setMinimumSize(new java.awt.Dimension(40, 20));
        spMinutes.setPreferredSize(new java.awt.Dimension(40, 20));
        spMinutes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spMinutesStateChanged(evt);
            }
        });
        spMinutes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spMinutesKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(spMinutes, gridBagConstraints);

        chbAutoStart.setFont(getFont());
        chbAutoStart.setMnemonic(Translator.getMnemonic("TASKDIALOG.CHB_AUTOSTART"));
        chbAutoStart.setText(Translator.getTranslation("TASKDIALOG.CHB_AUTOSTART"));
        chbAutoStart.setToolTipText(Translator.getTranslation("TASKDIALOG.CHB_AUTOSTART_TOOLTIP"));
        chbAutoStart.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbAutoStartKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(chbAutoStart, gridBagConstraints);

        chbRegular.setFont(getFont());
        chbRegular.setText(Translator.getTranslation("TASKDIALOG.CHB_REGULAR"));
        chbRegular.setToolTipText(Translator.getTranslation("TASKDIALOG.CHB_REGULAR_TOOLTIP"));
        chbRegular.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbRegularKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(chbRegular, gridBagConstraints);

        cmbRepetition.setFont(getFont());
        for (int i=RegularTask.FREQUENCY_DAILY; i<=RegularTask.FREQUENCY_WEEKEND; i++)
        cmbRepetition.addItem(RegularTask.getFrequency(i));
        cmbRepetition.setSelectedIndex(RegularTask.FREQUENCY_DAILY);
        cmbRepetition.setToolTipText(Translator.getTranslation("TASKDIALOG.CMB_REPETITION_TOOLTIP"));
        cmbRepetition.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbRepetitionKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cmbRepetition, gridBagConstraints);

        chbPrivate.setFont(getFont());
        chbPrivate.setMnemonic(Translator.getMnemonic("TASKDIALOG.CHB_PRIVATE"));
        chbPrivate.setText(Translator.getTranslation("TASKDIALOG.CHB_PRIVATE"));
        chbPrivate.setToolTipText(Translator.getTranslation("TASKDIALOG.CHB_PRIVATE_TOOLTIP"));
        chbPrivate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbPrivateKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(chbPrivate, gridBagConstraints);

        chbStartTask.setFont(getFont());
        chbStartTask.setMnemonic(Translator.getMnemonic("TASKDIALOG.CHB_START_TASK"));
        chbStartTask.setText(Translator.getTranslation("TASKDIALOG.CHB_START_TASK"));
        chbStartTask.setToolTipText(Translator.getTranslation("TASKDIALOG.CHB_START_TASK_TOOLTIP"));
        chbStartTask.setEnabled(false);
        chbStartTask.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbStartTaskKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(chbStartTask, gridBagConstraints);

        btOK.setFont(getFont());
        btOK.setMnemonic(Translator.getMnemonic("TASKDIALOG.BT_OK_NAME"));
        btOK.setText(Translator.getTranslation("TASKDIALOG.BT_OK_NAME"));
        btOK.setToolTipText(Translator.getTranslation("TASKDIALOG.BT_OK_TOOLTIP"));
        btOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOKActionPerformed(evt);
            }
        });
        pnButtons.add(btOK);

        btCancel.setFont(getFont());
        btCancel.setMnemonic(Translator.getMnemonic("TASKDIALOG.BT_CANCEL_NAME"));
        btCancel.setText(Translator.getTranslation("TASKDIALOG.BT_CANCEL_NAME"));
        btCancel.setToolTipText(Translator.getTranslation("TASKDIALOG.BT_CANCEL_TOOLTIP"));
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });
        pnButtons.add(btCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(pnButtons, gridBagConstraints);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-419)/2, (screenSize.height-428)/2, 419, 428);
    }// </editor-fold>//GEN-END:initComponents

private void chbPrivateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbPrivateKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbPrivateKeyPressed

private void cmbRepetitionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbRepetitionKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_cmbRepetitionKeyPressed

private void chbRegularKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbRegularKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbRegularKeyPressed

private void chbAutoStartKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbAutoStartKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbAutoStartKeyPressed

private void spMinutesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spMinutesKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_spMinutesKeyPressed

private void spHoursKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spHoursKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_spHoursKeyPressed

private void chbNotificationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbNotificationKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbNotificationKeyPressed

private void cmbPriorityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbPriorityKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_cmbPriorityKeyPressed

private void taNotesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_taNotesKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.isControlDown())
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_taNotesKeyPressed

private void txtCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_txtCategoryKeyPressed

private void txtDescriptionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescriptionKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_txtDescriptionKeyPressed

    private void txtCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCategoryFocusLost
        txtCategory.setToolTipText(Translator.getTranslation("TASKDIALOG.TXT_CATEGORY_TOOLTIP"));
    }//GEN-LAST:event_txtCategoryFocusLost

    private void txtCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCategoryFocusGained
        if (txtCategory.getText().equals("")) {
            int modifier = System.getProperty("os.name").indexOf("Windows") != -1 ? KeyEvent.CTRL_MASK : KeyEvent.SHIFT_MASK;
            txtCategory.setToolTipText(Translator.getTranslation("TASKDIALOG.COMPLETION_HINT", new String[]{KeyEvent.getKeyModifiersText(modifier)}));
            ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(txtCategory, 0, 0, 0, 0, 0, 0, false));
        }
    }//GEN-LAST:event_txtCategoryFocusGained
    
    private void txtCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyTyped
        int modifier = System.getProperty("os.name").indexOf("Windows") != -1 ? evt.CTRL_MASK : evt.SHIFT_MASK;
        if ((evt.getModifiers() == modifier) & (evt.getKeyChar() == ' ')) {
            evt.consume();
            new CompletionWindow(txtCategory, Plan.getDefault().getCategories(), this).setVisible(true);
        }
    }//GEN-LAST:event_txtCategoryKeyTyped
    
    /** Method called when ok button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOKActionPerformed
        if (readOnly) closeDialog(null);
        String description = txtDescription.getText();
        /* Generates error dialog box when no description is provided for the new task */
        if (description.equalsIgnoreCase("")) { // Is there a description for the task ?
            JOptionPane.showMessageDialog(this, Translator.getTranslation("ERROR.MISSING_DESCRIPTION"), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (day != null) {
            boolean error = false;
            if (day.getTask(description) != null) // Is there a task in day's plan with same description ?
                if (task == null) // Are we creating new task ?
                    error = true;
                else // We are editing some task
                    if (!task.getDescription().equals(description)) // Was its description changed ?
                        error = true;
            if (error) {
                JOptionPane.showMessageDialog(this, Translator.getTranslation("ERROR.TASK_EXISTS"), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String keyword = txtCategory.getText();
        String notes = taNotes.getText();
        int priority = cmbPriority.getSelectedIndex();
        int state = Task.STATE_NEW;
        long duration = 0;
        Date notificationTime = null;
        boolean automaticStart = false;
        boolean privateTask = chbPrivate.isSelected();
        if (chbNotification.isSelected()) {
            Integer hours = (Integer) spHours.getValue();
            Integer minutes = (Integer) spMinutes.getValue();
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, hours.intValue());
            time.set(Calendar.MINUTE, minutes.intValue());
            notificationTime = time.getTime();
            automaticStart = chbAutoStart.isSelected();
        }
        if (task != null) { // Are we editing some task ?
            task.setDescription(description);
            task.setKeyword(keyword);
            task.setNotes(notes);
            task.setPriority(priority);
            task.setNotificationTime(notificationTime);
            task.setAutomaticStart(automaticStart);
            task.setPrivateTask(privateTask);
            if (task instanceof RegularTask) {
                RegularTask regularTask = (RegularTask) task;
                regularTask.setFrequency(cmbRepetition.getSelectedIndex());
            }
            firePropertyChange("task_changed", null, task);
        } else {
            if (chbRegular.isSelected()) {
                RegularTask regularTask = new RegularTask(description, keyword, notes, priority, state, duration, notificationTime, automaticStart, privateTask, cmbRepetition.getSelectedIndex());
                task = regularTask;
            } else {
                task = new Task(description, keyword, notes, priority, state, duration, notificationTime, automaticStart, privateTask);
                Boolean logTaskEvents = (Boolean) Settings.getDefault().getSetting("logTaskEvents");
                if (logTaskEvents.booleanValue()) {
                    if (Plan.getDefault().isToday(day)) task.addNote("created", true);
                    else task.setNotes("[" + Plan.getDefault().getDay(new Date()).toString() + " " + Tools.getTime(new Date()) + "] created");
                }
                if (Plan.getDefault().isToday(day)) {
                    System.setProperty("startTaskNow", "" + chbStartTask.isSelected());
                    Settings.getDefault().setSetting("startTask", Boolean.valueOf(chbStartTask.isSelected()));
                }
                day.addTask(task);
            }
            firePropertyChange("task_created", null, task);
        }
        closeDialog(null);
    }//GEN-LAST:event_btOKActionPerformed
    
    /** Method called when cancel button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        firePropertyChange("task_unchanged", task, null);
        closeDialog(null);
    }//GEN-LAST:event_btCancelActionPerformed
    
    /** Method called when minutes of notification time were changed.
     * @param evt Event that invoked this method call.
     */
    private void spMinutesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spMinutesStateChanged
        Integer value = (Integer) spMinutes.getValue();
        if (value.intValue() > 59) spMinutes.setValue(new Integer(59));
        if (value.intValue() < 0) spMinutes.setValue(new Integer(0));
    }//GEN-LAST:event_spMinutesStateChanged
    
    /** Method called when hours of notification time were changed.
     * @param evt Event that invoked this method call.
     */
    private void spHoursStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spHoursStateChanged
        Integer value = (Integer) spHours.getValue();
        if (value.intValue() > 23) spHours.setValue(new Integer(23));
        if (value.intValue() < 0) spHours.setValue(new Integer(0));
    }//GEN-LAST:event_spHoursStateChanged
    
    /** Method called when notification was turned on/off.
     * @param evt Event that invoked this method call.
     */
    private void chbNotificationChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbNotificationChanged
        boolean value = chbNotification.isSelected();
        spHours.setEnabled(value);
        lblColon.setEnabled(value);
        spMinutes.setEnabled(value);
        chbAutoStart.setEnabled(value);
    }//GEN-LAST:event_chbNotificationChanged
    
    /** Method called when dialog should be closed.
     * @param evt Event that invoked this method call.
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
    }//GEN-LAST:event_closeDialog

    private void chbStartTaskKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbStartTaskKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbStartTaskKeyPressed

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        Tools.recordActivity();
    }//GEN-LAST:event_formMouseEntered
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOK;
    private javax.swing.JCheckBox chbAutoStart;
    private javax.swing.JCheckBox chbNotification;
    private javax.swing.JCheckBox chbPrivate;
    private javax.swing.JCheckBox chbRegular;
    private javax.swing.JCheckBox chbStartTask;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbRepetition;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblColon;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblNotes;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JSpinner spHours;
    private javax.swing.JSpinner spMinutes;
    private javax.swing.JScrollPane spNotes;
    private javax.swing.JTextArea taNotes;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtDescription;
    // End of variables declaration//GEN-END:variables
    
    /** Return task that was edited or created by this dialog.
     * @return Task that was edited or created by this dialog.
     */
    public Task getTask() {
        return task;
    }

    public void requestFocus() {
        super.requestFocus();
        txtCategory.requestFocusInWindow();
    }
}