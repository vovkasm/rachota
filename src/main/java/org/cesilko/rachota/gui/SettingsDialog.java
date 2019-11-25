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
 * Created on May 26, 2005  8:48 PM
 * SettingsDialog.java
 */

package org.cesilko.rachota.gui;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger log = Logger.getLogger(SettingsDialog.class.getName());
    
    /** Creates new dialog with system settings.
     * @param parent Parent component of this dialog i.e. main window.
     */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, true);
        regularTasks = (Vector) Plan.getDefault().getRegularTasks().clone();
        initComponents();
        txtHours.setValue(Settings.getDefault().getWorkingHours());
        chbHoursNotReached.setSelected(((Boolean) Settings.getDefault().getSetting("warnHoursNotReached")).booleanValue());
        chbHoursExceeded.setSelected(((Boolean) Settings.getDefault().getSetting("warnHoursExceeded")).booleanValue());
        chbMoveUnfinished.setSelected(((Boolean) Settings.getDefault().getSetting("moveUnfinished")).booleanValue());
        chbArchiveNotStarted.setSelected(((Boolean) Settings.getDefault().getSetting("archiveNotStarted")).booleanValue());
        chbCheckPriority.setSelected(((Boolean) Settings.getDefault().getSetting("checkPriority")).booleanValue());
        chbCountPrivate.setSelected(((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue());
        chbReportActivity.setSelected(((Boolean) Settings.getDefault().getSetting("reportActivity")).booleanValue());
        chbDetectInactivity.setSelected(((Boolean) Settings.getDefault().getSetting("detectInactivity")).booleanValue());
        chbPopupGroupByKeyword.setSelected(((Boolean) Settings.getDefault().getSetting("popupGroupByKeyword")).booleanValue());
        cmbInactivityAction.addItem(Translator.getTranslation("SETTINGSDIALOG.INACTIVITY_ACTION_NOTIFY"));
        cmbInactivityAction.addItem(Translator.getTranslation("SETTINGSDIALOG.INACTIVITY_ACTION_ASK"));
        cmbInactivityAction.addItem(Translator.getTranslation("SETTINGSDIALOG.INACTIVITY_ACTION_STOP"));
        int selectedInactivityAction = Integer.parseInt((String) Settings.getDefault().getSetting("inactivityAction"));
        cmbInactivityAction.setSelectedIndex(selectedInactivityAction);
        String[] hibernationActionKeys = new String[] {"SETTINGSDIALOG.HIBERNATION_ACTION_IGNORE", "SETTINGSDIALOG.HIBERNATION_ACTION_INCLUDE", "SETTINGSDIALOG.HIBERNATION_ACTION_ASK"};
        for (int i = 0; i < hibernationActionKeys.length; i++) {
            String item = Translator.getTranslation(hibernationActionKeys[i]);
            cmbHibernationAction.addItem(item);
        }
        int selectedHibernationAction = Integer.parseInt((String) Settings.getDefault().getSetting("hibernationAction"));
        cmbHibernationAction.setSelectedIndex(selectedHibernationAction);
        cmbOnExitAction.addItem(Translator.getTranslation("SETTINGSDIALOG.ON_EXIT_ACTION_ASK_USER"));
        cmbOnExitAction.addItem(Translator.getTranslation("SETTINGSDIALOG.ON_EXIT_ACTION_STOP"));
        int selectedOnExitAction = Integer.parseInt((String) Settings.getDefault().getSetting("onExitAction"));
        cmbOnExitAction.setSelectedIndex(selectedOnExitAction);
        txtProxyHost.setText("" + Settings.getDefault().getSetting("proxyHost"));
        txtProxyPort.setText("" + Settings.getDefault().getSetting("proxyPort"));
        txtInactivityTime.setValue(Integer.valueOf(Settings.getDefault().getSetting("inactivityTime").toString()));
        chbDetectInactivityActionPerformed(null);
        txtHibernationTime.setValue(Integer.valueOf(Settings.getDefault().getSetting("hibernationTime").toString()));
        chbLogEvents.setSelected(((Boolean) Settings.getDefault().getSetting("logTaskEvents")).booleanValue());
        String uploadDiary = (String) Settings.getDefault().getSetting("uploadDiary");
        chbUploadDiary.setSelected(uploadDiary.equals(Settings.UPLOAD_ON));
        txtUploadURL.setText((String) Settings.getDefault().getSetting("uploadDiaryURL"));
        txtUploadUsername.setText((String) Settings.getDefault().getSetting("uploadDiaryUsername"));
        chbUploadDiaryActionPerformed(null);
        tbRegularTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbRegularTasks.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbRegularTasks.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbRegularTasks.getTableHeader().setForeground(java.awt.Color.BLUE);
        tbRegularTasks.getTableHeader().setBackground(java.awt.Color.LIGHT_GRAY);
        tbRegularTasks.getTableHeader().setFont(getFont());
        tbRegularTasks.setFont(getFont());
        tbRegularTasks.setRowHeight(getFont().getSize() + 2);
        tbRegularTasks.getTableHeader().addMouseListener(new MouseAdapter() {
            Point pressedPoint;
            public void mousePressed(MouseEvent e) {
                pressedPoint = e.getPoint();
            }
            public void mouseReleased(MouseEvent e) {
                if (!e.getPoint().equals(pressedPoint)) return;
                int column = tbRegularTasks.getTableHeader().columnAtPoint(e.getPoint());
                RegularTasksTableModel regularTasksTableModel = (RegularTasksTableModel) tbRegularTasks.getModel();
                regularTasks = regularTasksTableModel.sortTable(column);
                int columns = tbRegularTasks.getColumnCount();
                for (int i=0; i<columns; i++)
                    tbRegularTasks.getColumnModel().getColumn(i).setHeaderValue(regularTasksTableModel.getColumnName(i));
            }
        });

        getRootPane().setDefaultButton(btOK);
        pack();
        setLocationRelativeTo(parent);
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

        tpPanels = new javax.swing.JTabbedPane();
        pnGeneral = new javax.swing.JPanel();
        lblWorkingHours = new javax.swing.JLabel();
        txtHours = new javax.swing.JFormattedTextField();
        lblHours = new javax.swing.JLabel();
        lblWarn = new javax.swing.JLabel();
        chbHoursNotReached = new javax.swing.JCheckBox();
        chbHoursExceeded = new javax.swing.JCheckBox();
        chbMoveUnfinished = new javax.swing.JCheckBox();
        chbArchiveNotStarted = new javax.swing.JCheckBox();
        chbCheckPriority = new javax.swing.JCheckBox();
        chbCountPrivate = new javax.swing.JCheckBox();
        chbLogEvents = new javax.swing.JCheckBox();
        chbPopupGroupByKeyword = new javax.swing.JCheckBox();
        pnRegularTasks = new javax.swing.JPanel();
        spRegularTasks = new javax.swing.JScrollPane();
        tbRegularTasks = new javax.swing.JTable();
        pnButtons = new javax.swing.JPanel();
        btAdd = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();
        pnInteraction = new javax.swing.JPanel();
        chbDetectInactivity = new javax.swing.JCheckBox();
        lblInactivityTime = new javax.swing.JLabel();
        txtInactivityTime = new javax.swing.JFormattedTextField();
        lblInactivityAction = new javax.swing.JLabel();
        cmbInactivityAction = new javax.swing.JComboBox();
        lblHibernationTime = new javax.swing.JLabel();
        txtHibernationTime = new javax.swing.JFormattedTextField();
        lblHibernationAction = new javax.swing.JLabel();
        cmbHibernationAction = new javax.swing.JComboBox();
        lbOnExit = new javax.swing.JLabel();
        lbOnExit.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_ON_EXIT"));
        lbOnExit.setFont(getFont());
        cmbOnExitAction = new javax.swing.JComboBox();
        pnNetwork = new javax.swing.JPanel();
        chbReportActivity = new javax.swing.JCheckBox();
        lblProxyHost = new javax.swing.JLabel();
        txtProxyHost = new javax.swing.JTextField();
        lblProxyPort = new javax.swing.JLabel();
        txtProxyPort = new javax.swing.JFormattedTextField();
        chbUploadDiary = new javax.swing.JCheckBox();
        lblUploadUsername = new javax.swing.JLabel();
        txtUploadUsername = new javax.swing.JTextField();
        lblUploadURL = new javax.swing.JLabel();
        txtUploadURL = new javax.swing.JTextField();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Translator.getTranslation("SETTINGSDIALOG.TITLE"));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tpPanels.setFont(getFont());

        pnGeneral.setLayout(new java.awt.GridBagLayout());

        lblWorkingHours.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_WORKING_HOURS"));
        lblWorkingHours.setFont(getFont());
        lblWorkingHours.setLabelFor(txtHours);
        lblWorkingHours.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_WORKING_HOURS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblWorkingHours, gridBagConstraints);

        txtHours.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        txtHours.setText("8.5");
        txtHours.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_HOURS_TOOLTIP"));
        txtHours.setFont(getFont());
        txtHours.setMinimumSize(new java.awt.Dimension(30, 20));
        txtHours.setPreferredSize(new java.awt.Dimension(30, 20));
        txtHours.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtHoursKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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
        chbHoursNotReached.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbHoursNotReachedKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbHoursNotReached, gridBagConstraints);

        chbHoursExceeded.setFont(getFont());
        chbHoursExceeded.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_HOURS_EXCEEDED"));
        chbHoursExceeded.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_HOURS_EXCEEDED"));
        chbHoursExceeded.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_HOURS_EXCEEDED_TOOLTIP"));
        chbHoursExceeded.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbHoursExceededKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbHoursExceeded, gridBagConstraints);

        chbMoveUnfinished.setFont(getFont());
        chbMoveUnfinished.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_MOVE_UNFINISHED"));
        chbMoveUnfinished.setSelected(true);
        chbMoveUnfinished.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_MOVE_UNFINISHED"));
        chbMoveUnfinished.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_MOVE_UNFINISHED_TOOLTIP"));
        chbMoveUnfinished.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbMoveUnfinishedKeyPressed(evt);
            }
        });
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
        chbArchiveNotStarted.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbArchiveNotStartedKeyPressed(evt);
            }
        });
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
        chbCheckPriority.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbCheckPriorityKeyPressed(evt);
            }
        });
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
        chbCountPrivate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbCountPrivateKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbCountPrivate, gridBagConstraints);

        chbLogEvents.setFont(getFont());
        chbLogEvents.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_LOG_EVENTS"));
        chbLogEvents.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_LOG_EVENTS"));
        chbLogEvents.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_LOG_EVENTS_TOOLTIP"));
        chbLogEvents.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbLogEventsKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbLogEvents, gridBagConstraints);

        chbPopupGroupByKeyword.setFont(getFont());
        chbPopupGroupByKeyword.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_POPUP_GROUPBY_KEYWORD"));
        chbPopupGroupByKeyword.setSelected(((Boolean) Settings.getDefault().getSetting("popupGroupByKeyword")).booleanValue());
        chbPopupGroupByKeyword.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_POPUP_GROUPBY_KEYWORD"));
        chbPopupGroupByKeyword.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_POPUP_GROUPBY_KEYWORD_TOOLTIP"));
        chbPopupGroupByKeyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbPopupGroupByKeywordKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbPopupGroupByKeyword, gridBagConstraints);

        tpPanels.addTab(Translator.getTranslation("SETTINGSDIALOG.BORDER_GENERAL"), pnGeneral);

        pnRegularTasks.setLayout(new java.awt.GridBagLayout());

        spRegularTasks.setPreferredSize(new java.awt.Dimension(300, 100));
        spRegularTasks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spRegularTasksKeyPressed(evt);
            }
        });

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

        tpPanels.addTab(Translator.getTranslation("SETTINGSDIALOG.BORDER_REGULAR_TASKS"), pnRegularTasks);

        pnInteraction.setLayout(new java.awt.GridBagLayout());

        chbDetectInactivity.setFont(getFont());
        chbDetectInactivity.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_DETECT_INACTIVITY"));
        chbDetectInactivity.setSelected(true);
        chbDetectInactivity.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_DETECT_INACTIVITY"));
        chbDetectInactivity.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_DETECT_INACTIVITY_TOOLTIP"));
        chbDetectInactivity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbDetectInactivityActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(chbDetectInactivity, gridBagConstraints);

        lblInactivityTime.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_INACTIVITY_TIME"));
        lblInactivityTime.setFont(getFont());
        lblInactivityTime.setLabelFor(txtInactivityTime);
        lblInactivityTime.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_INACTIVITY_TIME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(lblInactivityTime, gridBagConstraints);

        txtInactivityTime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtInactivityTime.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_INACTIVITY_TIME_TOOLTIP"));
        txtInactivityTime.setFont(getFont());
        txtInactivityTime.setMinimumSize(new java.awt.Dimension(40, 20));
        txtInactivityTime.setPreferredSize(new java.awt.Dimension(40, 20));
        txtInactivityTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtInactivityTimeKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(txtInactivityTime, gridBagConstraints);

        lblInactivityAction.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_INACTIVITY_ACTION"));
        lblInactivityAction.setFont(getFont());
        lblInactivityAction.setLabelFor(cmbInactivityAction);
        lblInactivityAction.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_INACTIVITY_ACTION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(lblInactivityAction, gridBagConstraints);

        cmbInactivityAction.setFont(getFont());
        cmbInactivityAction.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CMB_INACTIVITY_ACTION_TOOLTIP"));
        cmbInactivityAction.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbInactivityActionKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(cmbInactivityAction, gridBagConstraints);

        lblHibernationTime.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_HIBERNATION_TIME"));
        lblHibernationTime.setFont(getFont());
        lblHibernationTime.setLabelFor(txtHibernationTime);
        lblHibernationTime.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_HIBERNATION_TIME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(lblHibernationTime, gridBagConstraints);

        txtHibernationTime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtHibernationTime.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_HIBERNATION_TIME_TOOLTIP"));
        txtHibernationTime.setFont(getFont());
        txtHibernationTime.setMinimumSize(new java.awt.Dimension(40, 20));
        txtHibernationTime.setPreferredSize(new java.awt.Dimension(40, 20));
        txtHibernationTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtHibernationTimeKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(txtHibernationTime, gridBagConstraints);

        lblHibernationAction.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_HIBERNATION_ACTION"));
        lblHibernationAction.setFont(getFont());
        lblHibernationAction.setLabelFor(cmbHibernationAction);
        lblHibernationAction.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_HIBERNATION_ACTION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(lblHibernationAction, gridBagConstraints);

        cmbHibernationAction.setFont(getFont());
        cmbHibernationAction.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CMB_HIBERNATION_ACTION_TOOLTIP"));
        cmbHibernationAction.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbHibernationActionKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(cmbHibernationAction, gridBagConstraints);

        lbOnExit.setFont(getFont());
        lbOnExit.setLabelFor(cmbOnExitAction);
        lbOnExit.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_ON_EXIT"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(lbOnExit, gridBagConstraints);

        cmbOnExitAction.setFont(getFont());
        cmbOnExitAction.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CMB_ON_EXIT_ACTION_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnInteraction.add(cmbOnExitAction, gridBagConstraints);

        tpPanels.addTab(Translator.getTranslation("SETTINGSDIALOG.BORDER_INTERACTION"), pnInteraction);

        pnNetwork.setLayout(new java.awt.GridBagLayout());

        chbReportActivity.setFont(getFont());
        chbReportActivity.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_REPORT_ACTIVITY"));
        chbReportActivity.setSelected(true);
        chbReportActivity.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_REPORT_ACTIVITY"));
        chbReportActivity.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_REPORT_ACTIVITY_TOOLTIP"));
        chbReportActivity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbReportActivityKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(chbReportActivity, gridBagConstraints);

        lblProxyHost.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_PROXY_HOST"));
        lblProxyHost.setFont(getFont());
        lblProxyHost.setLabelFor(txtProxyHost);
        lblProxyHost.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_PROXY_HOST"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(lblProxyHost, gridBagConstraints);

        txtProxyHost.setFont(getFont());
        txtProxyHost.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_PROXY_HOST_TOOLTIP"));
        txtProxyHost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProxyHostKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(txtProxyHost, gridBagConstraints);

        lblProxyPort.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_PROXY_PORT"));
        lblProxyPort.setFont(getFont());
        lblProxyPort.setLabelFor(txtProxyPort);
        lblProxyPort.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_PROXY_PORT"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(lblProxyPort, gridBagConstraints);

        txtProxyPort.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtProxyPort.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_PROXY_PORT_TOOLTIP"));
        txtProxyPort.setFont(getFont());
        txtProxyPort.setMinimumSize(new java.awt.Dimension(40, 20));
        txtProxyPort.setPreferredSize(new java.awt.Dimension(40, 20));
        txtProxyPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProxyPortKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(txtProxyPort, gridBagConstraints);

        chbUploadDiary.setFont(getFont());
        chbUploadDiary.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_UPLOAD_DIARIES"));
        chbUploadDiary.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_UPLOAD_DIARIES"));
        chbUploadDiary.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_UPLOAD_DIARIES_TOOLTIP"));
        chbUploadDiary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbUploadDiaryActionPerformed(evt);
            }
        });
        chbUploadDiary.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbUploadDiaryKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(chbUploadDiary, gridBagConstraints);

        lblUploadUsername.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_USERNAME"));
        lblUploadUsername.setFont(getFont());
        lblUploadUsername.setLabelFor(txtUploadUsername);
        lblUploadUsername.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_USERNAME"));
        lblUploadUsername.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(lblUploadUsername, gridBagConstraints);

        txtUploadUsername.setFont(getFont());
        txtUploadUsername.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.LBL_USERNAME_TOOLTIP"));
        txtUploadUsername.setEnabled(false);
        txtUploadUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUploadUsernameKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(txtUploadUsername, gridBagConstraints);

        lblUploadURL.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_UPLOAD_URL"));
        lblUploadURL.setFont(getFont());
        lblUploadURL.setLabelFor(txtUploadURL);
        lblUploadURL.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_UPLOAD_URL"));
        lblUploadURL.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(lblUploadURL, gridBagConstraints);

        txtUploadURL.setFont(getFont());
        txtUploadURL.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.LBL_UPLOAD_URL_TOOLTIP"));
        txtUploadURL.setEnabled(false);
        txtUploadURL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUploadURLKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnNetwork.add(txtUploadURL, gridBagConstraints);

        tpPanels.addTab(Translator.getTranslation("SETTINGSDIALOG.BORDER_NETWORK"), pnNetwork);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(tpPanels, gridBagConstraints);

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
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btCancel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void spRegularTasksKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spRegularTasksKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_spRegularTasksKeyPressed

private void chbCountPrivateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbCountPrivateKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbCountPrivateKeyPressed

private void chbCheckPriorityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbCheckPriorityKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbCheckPriorityKeyPressed

private void chbArchiveNotStartedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbArchiveNotStartedKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbArchiveNotStartedKeyPressed

private void chbMoveUnfinishedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbMoveUnfinishedKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbMoveUnfinishedKeyPressed

private void chbHoursExceededKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbHoursExceededKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbHoursExceededKeyPressed

private void chbHoursNotReachedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbHoursNotReachedKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbHoursNotReachedKeyPressed
    
    /** Method called when any key was released while table with regular tasks had focus.
     * @param evt Event that invoked this method call.
     */
    private void tbRegularTasksKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbRegularTasksKeyReleased
        checkButtons();
    }//GEN-LAST:event_tbRegularTasksKeyReleased
    
    /** Method called when textfield with working hours lost its focus.
     * @param evt Event that invoked this method call.
     */    
    /** Method called when remove button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoveActionPerformed
        int row = tbRegularTasks.getSelectedRow();
        RegularTask regularTask = (RegularTask) regularTasks.get(row);
        String description = regularTask.getDescription();String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
        int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.REMOVE_REGULAR_TASK", new String[] {description}), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if (decision != JOptionPane.YES_OPTION) return;
        regularTasks.remove(regularTask);
        RegularTasksTableModel regularTasksTableModel = (RegularTasksTableModel) tbRegularTasks.getModel();
        regularTasksTableModel.fireTableDataChanged();
        checkButtons();
    }//GEN-LAST:event_btRemoveActionPerformed
    
    /** Method called when edit button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditActionPerformed
        int row = tbRegularTasks.getSelectedRow();
        RegularTask regularTask = (RegularTask) regularTasks.get(row);
        TaskDialog taskDialog = new TaskDialog(regularTask);
        taskDialog.addPropertyChangeListener(this);
        taskDialog.setLocationRelativeTo(this);
        taskDialog.setVisible(true);
    }//GEN-LAST:event_btEditActionPerformed
    
    /** Method called when add button was pressed.
     * @param evt Event that invoked this method call.
     */
    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        TaskDialog taskDialog = new TaskDialog();
        taskDialog.addPropertyChangeListener(this);
        taskDialog.setLocationRelativeTo(this);
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
        formWindowClosing(null);
        try {
            txtHours.commitEdit();
            Settings.getDefault().setWorkingHours(((Number) txtHours.getValue()).doubleValue());
        } catch (ParseException ex) {
            log.log(Level.WARNING, "Unable to read the value of the working hours field as a double value, resetting value to currently saved.", ex);
            txtHours.setValue(Settings.getDefault().getWorkingHours());
        }
        Settings.getDefault().setSetting("warnHoursNotReached", new Boolean(chbHoursNotReached.isSelected()));
        Settings.getDefault().setSetting("warnHoursExceeded", new Boolean(chbHoursExceeded.isSelected()));
        Settings.getDefault().setSetting("moveUnfinished", new Boolean(chbMoveUnfinished.isSelected()));
        Settings.getDefault().setSetting("archiveNotStarted", new Boolean(chbArchiveNotStarted.isSelected()));
        Settings.getDefault().setSetting("checkPriority", new Boolean(chbCheckPriority.isSelected()));
        Settings.getDefault().setSetting("countPrivateTasks", new Boolean(chbCountPrivate.isSelected()));
        Settings.getDefault().setSetting("reportActivity", new Boolean(chbReportActivity.isSelected()));
        Settings.getDefault().setSetting("logTaskEvents", new Boolean(chbLogEvents.isSelected()));
        Settings.getDefault().setSetting("detectInactivity", new Boolean(chbDetectInactivity.isSelected()));
        try {
            txtInactivityTime.commitEdit();
        } catch (ParseException ex) {
            log.log(Level.WARNING, "Unable to parse the text in the inactivity time field, will use last valid value.", ex);
        }
        Settings.getDefault().setSetting("inactivityTime", Integer.toString(((Number) txtInactivityTime.getValue()).intValue()));
        Settings.getDefault().setSetting("inactivityAction", "" + cmbInactivityAction.getSelectedIndex());
        try {
            txtHibernationTime.commitEdit();
        } catch (ParseException ex) {
            log.log(Level.WARNING, "Unable to parse the text in the hibernation time field, will use last valid value.", ex);
        }
        Settings.getDefault().setSetting("hibernationTime", Integer.toString(((Number) txtHibernationTime.getValue()).intValue()));
        Settings.getDefault().setSetting("hibernationAction", "" + cmbHibernationAction.getSelectedIndex());
        Settings.getDefault().setSetting("onExitAction", "" + cmbOnExitAction.getSelectedIndex());
        Settings.getDefault().setSetting("popupGroupByKeyword", new Boolean(chbPopupGroupByKeyword.isSelected()));
        String proxyHost = txtProxyHost.getText();
        String proxyPort = txtProxyPort.getText();
        Settings.getDefault().setSetting("proxyHost", proxyHost);
        Settings.getDefault().setSetting("proxyPort", proxyPort);
        if (proxyHost.length() > 0) System.setProperty("http.proxyHost", proxyHost);
        else System.clearProperty("http.proxyHost");
        if (proxyPort.length() > 0) System.setProperty("http.proxyPort", proxyPort);
        else System.clearProperty("http.proxyPort");
        Settings.getDefault().setSetting("uploadDiary", chbUploadDiary.isSelected()?Settings.UPLOAD_ON:Settings.UPLOAD_OFF);
        Settings.getDefault().setSetting("uploadDiaryUsername", txtUploadUsername.getText());
        Settings.getDefault().setSetting("uploadDiaryURL", txtUploadURL.getText());
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

    private void chbReportActivityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbReportActivityKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_chbReportActivityKeyPressed

    private void txtProxyHostKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProxyHostKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_txtProxyHostKeyPressed

    private void chbLogEventsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbLogEventsKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
    }//GEN-LAST:event_chbLogEventsKeyPressed

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        Tools.recordActivity();
    }//GEN-LAST:event_formMouseEntered

    private void txtInactivityTimeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInactivityTimeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_txtInactivityTimeKeyPressed

    private void chbDetectInactivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbDetectInactivityActionPerformed
        lblInactivityTime.setEnabled(chbDetectInactivity.isSelected());
        txtInactivityTime.setEnabled(chbDetectInactivity.isSelected());
        lblInactivityAction.setEnabled(chbDetectInactivity.isSelected());
        cmbInactivityAction.setEnabled(chbDetectInactivity.isSelected());
    }//GEN-LAST:event_chbDetectInactivityActionPerformed

    private void txtHibernationTimeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHibernationTimeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_txtHibernationTimeKeyPressed

    private void cmbInactivityActionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbInactivityActionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_cmbInactivityActionKeyPressed

    private void cmbHibernationActionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbHibernationActionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_cmbHibernationActionKeyPressed

    private void chbPopupGroupByKeywordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbPopupGroupByKeywordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_chbPopupGroupByKeywordKeyPressed

    private void txtHoursKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHoursKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_txtHoursKeyPressed

    private void txtProxyPortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProxyPortKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_txtProxyPortKeyPressed

    private void chbUploadDiaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbUploadDiaryActionPerformed
        boolean uploadDiaries = chbUploadDiary.isSelected();
        lblUploadURL.setEnabled(uploadDiaries);
        txtUploadURL.setEnabled(uploadDiaries);
        lblUploadUsername.setEnabled(uploadDiaries);
        txtUploadUsername.setEnabled(uploadDiaries);
    }//GEN-LAST:event_chbUploadDiaryActionPerformed

    private void chbUploadDiaryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chbUploadDiaryKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_chbUploadDiaryKeyPressed

    private void txtUploadUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUploadUsernameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_txtUploadUsernameKeyPressed

    private void txtUploadURLKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUploadURLKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btOKActionPerformed(null);
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            btCancelActionPerformed(null);
    }//GEN-LAST:event_txtUploadURLKeyPressed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btEdit;
    private javax.swing.JButton btOK;
    private javax.swing.JButton btRemove;
    private javax.swing.JCheckBox chbArchiveNotStarted;
    private javax.swing.JCheckBox chbCheckPriority;
    private javax.swing.JCheckBox chbCountPrivate;
    private javax.swing.JCheckBox chbDetectInactivity;
    private javax.swing.JCheckBox chbHoursExceeded;
    private javax.swing.JCheckBox chbHoursNotReached;
    private javax.swing.JCheckBox chbLogEvents;
    private javax.swing.JCheckBox chbMoveUnfinished;
    private javax.swing.JCheckBox chbPopupGroupByKeyword;
    private javax.swing.JCheckBox chbReportActivity;
    private javax.swing.JCheckBox chbUploadDiary;
    private javax.swing.JComboBox cmbHibernationAction;
    private javax.swing.JComboBox cmbInactivityAction;
    private javax.swing.JComboBox cmbOnExitAction;
    private javax.swing.JLabel lbOnExit;
    private javax.swing.JLabel lblHibernationAction;
    private javax.swing.JLabel lblHibernationTime;
    private javax.swing.JLabel lblHours;
    private javax.swing.JLabel lblInactivityAction;
    private javax.swing.JLabel lblInactivityTime;
    private javax.swing.JLabel lblProxyHost;
    private javax.swing.JLabel lblProxyPort;
    private javax.swing.JLabel lblUploadURL;
    private javax.swing.JLabel lblUploadUsername;
    private javax.swing.JLabel lblWarn;
    private javax.swing.JLabel lblWorkingHours;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JPanel pnGeneral;
    private javax.swing.JPanel pnInteraction;
    private javax.swing.JPanel pnNetwork;
    private javax.swing.JPanel pnRegularTasks;
    private javax.swing.JScrollPane spRegularTasks;
    private javax.swing.JTable tbRegularTasks;
    private javax.swing.JTabbedPane tpPanels;
    private javax.swing.JFormattedTextField txtHibernationTime;
    private javax.swing.JFormattedTextField txtHours;
    private javax.swing.JFormattedTextField txtInactivityTime;
    private javax.swing.JTextField txtProxyHost;
    private javax.swing.JFormattedTextField txtProxyPort;
    private javax.swing.JTextField txtUploadURL;
    private javax.swing.JTextField txtUploadUsername;
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
