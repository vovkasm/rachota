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
import java.util.Dictionary;
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
        chbReportActivity.setSelected(((Boolean) Settings.getDefault().getSetting("reportActivity")).booleanValue());
        chbDetectInactivity.setSelected(((Boolean) Settings.getDefault().getSetting("detectInactivity")).booleanValue());
        cmbInactivityAction.addItem(Translator.getTranslation("SETTINGSDIALOG.INACTIVITY_ACTION_NOTIFY"));
        cmbInactivityAction.addItem(Translator.getTranslation("SETTINGSDIALOG.INACTIVITY_ACTION_STOP"));
        int selectedInactivityAction = Integer.parseInt((String) Settings.getDefault().getSetting("inactivityAction"));
        cmbInactivityAction.setSelectedIndex(selectedInactivityAction);
        txtProxyHost.setText("" + Settings.getDefault().getSetting("proxyHost"));
        txtProxyPort.setText("" + Settings.getDefault().getSetting("proxyPort"));
        txtInactivityTime.setText("" + Settings.getDefault().getSetting("inactivityTime"));
        chbReportActivityActionPerformed(null);
        chbDetectInactivityActionPerformed(null);
        chbLogEvents.setSelected(((Boolean) Settings.getDefault().getSetting("logTaskEvents")).booleanValue());
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
        chbReportActivity = new javax.swing.JCheckBox();
        lblProxyHost = new javax.swing.JLabel();
        txtProxyHost = new javax.swing.JTextField();
        lblProxyPort = new javax.swing.JLabel();
        txtProxyPort = new javax.swing.JTextField();
        chbLogEvents = new javax.swing.JCheckBox();
        chbDetectInactivity = new javax.swing.JCheckBox();
        lblInactivityTime = new javax.swing.JLabel();
        txtInactivityTime = new javax.swing.JTextField();
        lblInactivityAction = new javax.swing.JLabel();
        cmbInactivityAction = new javax.swing.JComboBox();
        pnRegularTasks = new javax.swing.JPanel();
        spRegularTasks = new javax.swing.JScrollPane();
        tbRegularTasks = new javax.swing.JTable();
        pnButtons = new javax.swing.JPanel();
        btAdd = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();
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

        pnGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), Translator.getTranslation("SETTINGSDIALOG.BORDER_GENERAL"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnGeneral.setFont(getFont());
        pnGeneral.setLayout(new java.awt.GridBagLayout());

        lblWorkingHours.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_WORKING_HOURS"));
        lblWorkingHours.setFont(getFont());
        lblWorkingHours.setLabelFor(txtHours);
        lblWorkingHours.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_WORKING_HOURS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblWorkingHours, gridBagConstraints);

        txtHours.setFont(getFont());
        txtHours.setText("8.0");
        txtHours.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_HOURS_TOOLTIP"));
        txtHours.setMinimumSize(new java.awt.Dimension(30, 19));
        txtHours.setPreferredSize(new java.awt.Dimension(30, 19));
        txtHours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHoursFocusLost(evt);
            }
        });
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
        gridBagConstraints.gridwidth = 4;
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
        gridBagConstraints.gridwidth = 4;
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

        chbReportActivity.setFont(getFont());
        chbReportActivity.setMnemonic(Translator.getMnemonic("SETTINGSDIALOG.CHB_REPORT_ACTIVITY"));
        chbReportActivity.setSelected(true);
        chbReportActivity.setText(Translator.getTranslation("SETTINGSDIALOG.CHB_REPORT_ACTIVITY"));
        chbReportActivity.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CHB_REPORT_ACTIVITY_TOOLTIP"));
        chbReportActivity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbReportActivityActionPerformed(evt);
            }
        });
        chbReportActivity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chbReportActivityKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbReportActivity, gridBagConstraints);

        lblProxyHost.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_PROXY_HOST"));
        lblProxyHost.setFont(getFont());
        lblProxyHost.setLabelFor(txtProxyHost);
        lblProxyHost.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_PROXY_HOST"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblProxyHost, gridBagConstraints);

        txtProxyHost.setFont(getFont());
        txtProxyHost.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_PROXY_HOST_TOOLTIP"));
        txtProxyHost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProxyHostKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(txtProxyHost, gridBagConstraints);

        lblProxyPort.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_PROXY_PORT"));
        lblProxyPort.setFont(getFont());
        lblProxyPort.setLabelFor(txtProxyPort);
        lblProxyPort.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_PROXY_PORT"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblProxyPort, gridBagConstraints);

        txtProxyPort.setFont(getFont());
        txtProxyPort.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_PROXY_PORT_TOOLTIP"));
        txtProxyPort.setMinimumSize(new java.awt.Dimension(40, 19));
        txtProxyPort.setPreferredSize(new java.awt.Dimension(40, 19));
        txtProxyPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProxyPortKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(txtProxyPort, gridBagConstraints);

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
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbLogEvents, gridBagConstraints);

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
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(chbDetectInactivity, gridBagConstraints);

        lblInactivityTime.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_INACTIVITY_TIME"));
        lblInactivityTime.setFont(getFont());
        lblInactivityTime.setLabelFor(txtInactivityTime);
        lblInactivityTime.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_INACTIVITY_TIME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblInactivityTime, gridBagConstraints);

        txtInactivityTime.setFont(getFont());
        txtInactivityTime.setText("10");
        txtInactivityTime.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.TXT_INACTIVITY_TIME_TOOLTIP"));
        txtInactivityTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtInactivityTimeKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(txtInactivityTime, gridBagConstraints);

        lblInactivityAction.setDisplayedMnemonic(Translator.getMnemonic("SETTINGSDIALOG.LBL_INACTIVITY_ACTION"));
        lblInactivityAction.setFont(getFont());
        lblInactivityAction.setLabelFor(cmbInactivityAction);
        lblInactivityAction.setText(Translator.getTranslation("SETTINGSDIALOG.LBL_INACTIVITY_ACTION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(lblInactivityAction, gridBagConstraints);

        cmbInactivityAction.setFont(getFont());
        cmbInactivityAction.setToolTipText(Translator.getTranslation("SETTINGSDIALOG.CMB_INACTIVITY_ACTION_TOOLTIP"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnGeneral.add(cmbInactivityAction, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(pnGeneral, gridBagConstraints);

        pnRegularTasks.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), Translator.getTranslation("SETTINGSDIALOG.BORDER_REGULAR_TASKS"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnRegularTasks.setFont(getFont());
        pnRegularTasks.setLayout(new java.awt.GridBagLayout());

        spRegularTasks.setPreferredSize(new java.awt.Dimension(400, 100));
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

private void txtHoursKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHoursKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_txtHoursKeyPressed
    
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
        Settings.getDefault().setSetting("reportActivity", new Boolean(chbReportActivity.isSelected()));
        Settings.getDefault().setSetting("logTaskEvents", new Boolean(chbLogEvents.isSelected()));
        Settings.getDefault().setSetting("detectInactivity", new Boolean(chbDetectInactivity.isSelected()));
        Settings.getDefault().setSetting("inactivityTime", txtInactivityTime.getText());
        Settings.getDefault().setSetting("inactivityAction", "" + cmbInactivityAction.getSelectedIndex());
        String proxyHost = txtProxyHost.getText();
        String proxyPort = txtProxyPort.getText();
        Settings.getDefault().setSetting("proxyHost", proxyHost);
        Settings.getDefault().setSetting("proxyPort", proxyPort);
        if (proxyHost.length() > 0) System.setProperty("http.proxyHost", proxyHost);
        else System.clearProperty("http.proxyHost");
        if (proxyPort.length() > 0) System.setProperty("http.proxyPort", proxyPort);
        else System.clearProperty("http.proxyPort");
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

    private void txtProxyPortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProxyPortKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_txtProxyPortKeyPressed

    private void chbReportActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbReportActivityActionPerformed
        txtProxyHost.setEnabled(chbReportActivity.isSelected());
        txtProxyPort.setEnabled(chbReportActivity.isSelected());
        lblProxyHost.setEnabled(chbReportActivity.isSelected());
        lblProxyPort.setEnabled(chbReportActivity.isSelected());
    }//GEN-LAST:event_chbReportActivityActionPerformed

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
    private javax.swing.JCheckBox chbReportActivity;
    private javax.swing.JComboBox cmbInactivityAction;
    private javax.swing.JLabel lblHours;
    private javax.swing.JLabel lblInactivityAction;
    private javax.swing.JLabel lblInactivityTime;
    private javax.swing.JLabel lblProxyHost;
    private javax.swing.JLabel lblProxyPort;
    private javax.swing.JLabel lblWarn;
    private javax.swing.JLabel lblWorkingHours;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JPanel pnGeneral;
    private javax.swing.JPanel pnRegularTasks;
    private javax.swing.JScrollPane spRegularTasks;
    private javax.swing.JTable tbRegularTasks;
    private javax.swing.JTextField txtHours;
    private javax.swing.JTextField txtInactivityTime;
    private javax.swing.JTextField txtProxyHost;
    private javax.swing.JTextField txtProxyPort;
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