/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * The Original Software is Rachota.
 * The Initial Developer of the Original Software is Jiri Kovalsky
 * Portions created by Jiri Kovalsky are Copyright (C) 2006
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on 09 August 2005  20:37
 * HistoryView.java
 */

package org.cesilko.rachota.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.*;

/** Panel providing history view on tasks from the past.
 * @author Jiri Kovalsky
 */
public class HistoryView extends javax.swing.JPanel implements PropertyChangeListener {
    
    /** Creates new HistoryView panel charts and table. */
    public HistoryView() {
        initComponents();
        txtName.setFont(txtName.getFont().deriveFont(java.awt.Font.BOLD));
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        PieChart pieChart = new PieChart(0);
        pnShare.add(pieChart, gridBagConstraints);
        addPropertyChangeListener(pieChart);
        final ProjectsTreeModel model = new ProjectsTreeModel(getDays());
        jtProjects.setModel(model);
        jtProjects.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jtProjects.addTreeSelectionListener(new ProjectsTreeModel.ProjectsTreeListener() {
            public void valueChanged(TreeSelectionEvent event) {
                TreePath treePath = event.getPath();
                int nodeType = model.getSelectedNodeType(treePath);
                switch(nodeType) {
                case ProjectsTreeModel.NODE_TYPE_TASK:
                    DefaultMutableTreeNode selectedTaskNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    ProjectsTreeModel.TaskNode taskNode = (ProjectsTreeModel.TaskNode) selectedTaskNode.getUserObject();
                    txtName.setText(taskNode.getDescription());
                    txtName.setCaretPosition(0);
                    txtName.setFont(txtName.getFont().deriveFont(java.awt.Font.BOLD));
                    txtTime.setText(Tools.getTime(taskNode.getTotalTime()));
                    txtTasks.setText("" + taskNode.getTasks().size());
                    DefaultMutableTreeNode selectedCategoryNode = (DefaultMutableTreeNode) selectedTaskNode.getParent();
                    ProjectsTreeModel.CategoryNode category = (ProjectsTreeModel.CategoryNode) selectedCategoryNode.getUserObject();
                    float shareTask = Math.round(((float) taskNode.getTotalTime()/(float) category.getTotalTime())*100);
                    txtPercentage.setText("" + shareTask + "%");
                    txtPriority.setText(Task.getPriority(taskNode.getAverageValue(ProjectsTreeModel.TaskNode.PROPERTY_PRIORITY)));
                    txtState.setText(Task.getState(taskNode.getAverageValue(ProjectsTreeModel.TaskNode.PROPERTY_STATE)));
                    PieChart taskPieChart = (PieChart) pnShare.getComponent(0);
                    taskPieChart.setShare(shareTask);
                    break;
                case ProjectsTreeModel.NODE_TYPE_CATEGORY:
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    ProjectsTreeModel.CategoryNode categoryNode = (ProjectsTreeModel.CategoryNode) selectedNode.getUserObject();
                    txtName.setText(categoryNode.getName());
                    txtTime.setText(Tools.getTime(categoryNode.getTotalTime()));
                    txtTasks.setText("" + categoryNode.getTaskNodes().size());
                    float shareCategory = Math.round(((float) categoryNode.getTotalTime()/(float) getTotalTime())*100);
                    txtPercentage.setText("" + shareCategory + "%");
                    txtPriority.setText(Task.getPriority(categoryNode.getAverageValue(ProjectsTreeModel.CategoryNode.PROPERTY_PRIORITY)));
                    txtState.setText(Task.getState(categoryNode.getAverageValue(ProjectsTreeModel.CategoryNode.PROPERTY_STATE)));
                    PieChart categoryPieChart = (PieChart) pnShare.getComponent(0);
                    categoryPieChart.setShare(shareCategory);
                    break;
                default:
                    txtName.setText("");
                    txtTime.setText("");
                    txtTasks.setText("");
                    txtPercentage.setText("");
                    txtPriority.setText("");
                    txtState.setText("");
                    PieChart emptyPieChart = (PieChart) pnShare.getComponent(0);
                    emptyPieChart.setShare(0);
                }
            }
        });
        tbFilters.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbTasks.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cmbPeriod.addItem(Translator.getTranslation("HISTORYVIEW.PERIOD_" + SCALE_DAY));
        cmbPeriod.addItem(Translator.getTranslation("HISTORYVIEW.PERIOD_" + SCALE_WEEK));
        cmbPeriod.addItem(Translator.getTranslation("HISTORYVIEW.PERIOD_" + SCALE_MONTH));
        cmbPeriod.addItem(Translator.getTranslation("HISTORYVIEW.PERIOD_" + SCALE_YEAR));
        historyChart = new HistoryChart(getDays(), null, HistoryChart.TYPE_TOTAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(historyChart, gridBagConstraints);
        historyChart.setToolTipText(Translator.getTranslation("HISTORYVIEW.CHART_TOOLTIP"));
        historyChart.addMouseListener(new MouseAdapter() {
            Date clickedWhen = new Date();
            Point clickedWhere = new Point();
            public void mouseClicked(MouseEvent e) {
                Date now = new Date();
                long delay = now.getTime() - clickedWhen.getTime();
                boolean samePoint = clickedWhere.equals(e.getPoint());
                if (samePoint & (delay < 250)) {
                    Day day = historyChart.getDayAt(e.getPoint());
                    if (day != null) firePropertyChange("day", null, day);
                }
                clickedWhere = e.getPoint();
                clickedWhen = now;
            }
        });
        cmbPeriod.setSelectedIndex(SCALE_WEEK);
        checkButtons();
        filterTasks();
        tbFilters.getTableHeader().setForeground(java.awt.Color.BLUE);
        tbFilters.getTableHeader().setBackground(java.awt.Color.LIGHT_GRAY);
        tbFilters.getTableHeader().setFont(getFont());
        tbFilters.setFont(getFont());
        tbFilters.setRowHeight(getFont().getSize() + 2);
        tbTasks.getTableHeader().setForeground(java.awt.Color.BLUE);
        tbTasks.getTableHeader().setBackground(java.awt.Color.LIGHT_GRAY);
        tbTasks.getTableHeader().setFont(getFont());
        tbTasks.setFont(getFont());
        tbTasks.setRowHeight(getFont().getSize() + 2);
        cmbFilterName.addItem(new DescriptionFilter().toString());
        cmbFilterName.addItem(new KeywordFilter().toString());
        cmbFilterName.addItem(new DurationFilter().toString());
        cmbFilterName.addItem(new PriorityFilter().toString());
        cmbFilterName.addItem(new StateFilter().toString());
        cmbFilterName.addItem(new PrivateFilter().toString());
        loadSetup();
        tbTasks.getColumn(Translator.getTranslation("TASKS.DESCRIPTION")).setPreferredWidth(280);
        tbTasks.getColumn(Translator.getTranslation("TASKS.DURATION_DAYS")).setPreferredWidth(50);
        tbTasks.setRowSelectionAllowed(false);
        tbTasks.getTableHeader().addMouseListener(new MouseAdapter() {
            Point pressedPoint;
            public void mousePressed(MouseEvent e) {
                pressedPoint = e.getPoint();
            }
            public void mouseReleased(MouseEvent e) {
                if (!e.getPoint().equals(pressedPoint)) return;
                int column = tbTasks.getTableHeader().columnAtPoint(e.getPoint());
                FilteredTasksTableModel filteredTasksTableModel = (FilteredTasksTableModel) tbTasks.getModel();
                filteredTasksTableModel.sortTable(column, true);
                int columns = tbTasks.getColumnCount();
                for (int i=0; i<columns; i++)
                    tbTasks.getColumnModel().getColumn(i).setHeaderValue(filteredTasksTableModel.getColumnName(i));
            }
        });
    }
    
    /** Returns font that should be used for all widgets in this component
     * based on the language preferences specified by user.
     * @return Font to be used in this component.
     */
    public java.awt.Font getFont() {
        return new java.awt.Font((String) Settings.getDefault().getSetting("fontName"), java.awt.Font.PLAIN, Integer.parseInt((String) Settings.getDefault().getSetting("fontSize")));
    }

    private long getTotalTime() {
        long totalTime = 0;
        Iterator iterator = getDays().iterator();
        while (iterator.hasNext()) {
            Day day = (Day) iterator.next();
            totalTime = totalTime + day.getTotalTime();
            Task idleTask = day.getIdleTask();
            if (idleTask != null) totalTime = totalTime + idleTask.getDuration();
        }
        return totalTime;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblPeriod = new javax.swing.JLabel();
        cmbPeriod = new javax.swing.JComboBox();
        btReport = new javax.swing.JButton();
        pnPeriod = new javax.swing.JPanel();
        spMinus = new javax.swing.JSpinner();
        btBackward = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();
        btForward = new javax.swing.JButton();
        spPlus = new javax.swing.JSpinner();
        tpViews = new javax.swing.JTabbedPane();
        pnTimes = new javax.swing.JPanel();
        lblChartType = new javax.swing.JLabel();
        rbTotal = new javax.swing.JRadioButton();
        rbFromTo = new javax.swing.JRadioButton();
        rbTimeUsage = new javax.swing.JRadioButton();
        chbHighlightTasks = new javax.swing.JCheckBox();
        cmbFilterName = new javax.swing.JComboBox();
        cmbContentRule = new javax.swing.JComboBox();
        cmbContent = new javax.swing.JComboBox();
        txtContent = new javax.swing.JTextField();
        lblTotalTime = new javax.swing.JLabel();
        txtTotalTime = new javax.swing.JTextField();
        pnTasks = new javax.swing.JPanel();
        lblFilters = new javax.swing.JLabel();
        spFilters = new javax.swing.JScrollPane();
        tbFilters = new javax.swing.JTable();
        pnButtons = new javax.swing.JPanel();
        btAddFilter = new javax.swing.JButton();
        btEditFilter = new javax.swing.JButton();
        btRemoveFilter = new javax.swing.JButton();
        lblTasks = new javax.swing.JLabel();
        spTasks = new javax.swing.JScrollPane();
        tbTasks = new javax.swing.JTable();
        chbGroupTasks = new javax.swing.JCheckBox();
        pnTotalTime = new javax.swing.JPanel();
        lblFilteredTime = new javax.swing.JLabel();
        txtFilteredTime = new javax.swing.JTextField();
        pnProjects = new javax.swing.JPanel();
        spProjects = new javax.swing.JScrollPane();
        jtProjects = new javax.swing.JTree();
        pnDetails = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblTime = new javax.swing.JLabel();
        txtTime = new javax.swing.JTextField();
        lblPercentage = new javax.swing.JLabel();
        txtPercentage = new javax.swing.JTextField();
        lbTasks = new javax.swing.JLabel();
        txtTasks = new javax.swing.JTextField();
        lblPriority = new javax.swing.JLabel();
        txtPriority = new javax.swing.JTextField();
        lblState = new javax.swing.JLabel();
        txtState = new javax.swing.JTextField();
        pnShare = new javax.swing.JPanel();

        setName(Translator.getTranslation("HISTORYVIEW.TB_NAME"));
        setLayout(new java.awt.GridBagLayout());

        lblPeriod.setDisplayedMnemonic(Translator.getMnemonic("HISTORYVIEW.LBL_PERIOD"));
        lblPeriod.setFont(getFont());
        lblPeriod.setLabelFor(cmbPeriod);
        lblPeriod.setText(Translator.getTranslation("HISTORYVIEW.LBL_PERIOD"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblPeriod, gridBagConstraints);

        cmbPeriod.setFont(getFont());
        cmbPeriod.setToolTipText(Translator.getTranslation("HISTORYVIEW.PERIOD_TOOLTIP"));
        cmbPeriod.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPeriodItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cmbPeriod, gridBagConstraints);

        btReport.setFont(getFont());
        btReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/report.png"))); // NOI18N
        btReport.setMnemonic(Translator.getMnemonic("HISTORYVIEW.BT_REPORT"));
        btReport.setText(Translator.getTranslation("HISTORYVIEW.BT_REPORT"));
        btReport.setToolTipText(Translator.getTranslation("HISTORYVIEW.BT_REPORT_TOOLTIP"));
        btReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btReportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btReport, gridBagConstraints);

        spMinus.setFont(getFont());
        spMinus.setToolTipText(Translator.getTranslation("HISTORYVIEW.SP_MINUS_TOOLTIP"));
        spMinus.setPreferredSize(new java.awt.Dimension(60, 23));
        spMinus.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spMinusStateChanged(evt);
            }
        });
        pnPeriod.add(spMinus);

        btBackward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/previous.png"))); // NOI18N
        btBackward.setToolTipText(Translator.getTranslation("HISTORYVIEW.BT_BACKWARD_TOOLTIP"));
        btBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBackwardActionPerformed(evt);
            }
        });
        pnPeriod.add(btBackward);

        txtDate.setEditable(false);
        txtDate.setFont(getFont());
        txtDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDate.setToolTipText(Translator.getTranslation("HISTORYVIEW.TXT_DATE_TOOLTIP"));
        txtDate.setPreferredSize(new java.awt.Dimension(150, 23));
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });
        pnPeriod.add(txtDate);

        btForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/next.png"))); // NOI18N
        btForward.setToolTipText(Translator.getTranslation("HISTORYVIEW.BT_FORWARD_TOOLTIP"));
        btForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btForwardActionPerformed(evt);
            }
        });
        pnPeriod.add(btForward);

        spPlus.setFont(getFont());
        spPlus.setToolTipText(Translator.getTranslation("HISTORYVIEW.SP_PLUS_TOOLTIP"));
        spPlus.setPreferredSize(new java.awt.Dimension(60, 23));
        spPlus.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spPlusStateChanged(evt);
            }
        });
        pnPeriod.add(spPlus);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnPeriod, gridBagConstraints);

        tpViews.setFont(getFont());

        pnTimes.setFont(getFont());
        pnTimes.setLayout(new java.awt.GridBagLayout());

        lblChartType.setDisplayedMnemonic(Translator.getMnemonic("HISTORYVIEW.LBL_CHART_TYPE"));
        lblChartType.setFont(getFont());
        lblChartType.setLabelFor(rbFromTo);
        lblChartType.setText(Translator.getTranslation("HISTORYVIEW.LBL_CHART_TYPE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(lblChartType, gridBagConstraints);

        rbTotal.setFont(getFont());
        rbTotal.setSelected(true);
        rbTotal.setText(Translator.getTranslation("HISTORYVIEW.TYPE_TOTAL"));
        rbTotal.setToolTipText(Translator.getTranslation("HISTORYVIEW.TYPE_TOTAL_TOOLTIP"));
        rbTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbTotalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(rbTotal, gridBagConstraints);

        rbFromTo.setFont(getFont());
        rbFromTo.setText(Translator.getTranslation("HISTORYVIEW.TYPE_FROM_TO"));
        rbFromTo.setToolTipText(Translator.getTranslation("HISTORYVIEW.TYPE_FROM_TO_TOOLTIP"));
        rbFromTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbFromToActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnTimes.add(rbFromTo, gridBagConstraints);

        rbTimeUsage.setFont(getFont());
        rbTimeUsage.setText(Translator.getTranslation("HISTORYVIEW.TYPE_TIME_USAGE"));
        rbTimeUsage.setToolTipText(Translator.getTranslation("HISTORYVIEW.TYPE_TIME_USAGE_TOOLTIP"));
        rbTimeUsage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbTimeUsageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnTimes.add(rbTimeUsage, gridBagConstraints);

        chbHighlightTasks.setFont(getFont());
        chbHighlightTasks.setMnemonic(Translator.getMnemonic("HISTORYVIEW.LBL_HIGHLIGHT_TASKS"));
        chbHighlightTasks.setText(Translator.getTranslation("HISTORYVIEW.LBL_HIGHLIGHT_TASKS"));
        chbHighlightTasks.setToolTipText(Translator.getTranslation("HISTORYVIEW.LBL_HIGHLIGHT_TASKS_TOOLTIP"));
        chbHighlightTasks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbHighlightTasksActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(chbHighlightTasks, gridBagConstraints);

        cmbFilterName.setFont(getFont());
        cmbFilterName.setToolTipText(Translator.getTranslation("FILTERDIALOG.NAME_TOOLTIP"));
        cmbFilterName.setEnabled(false);
        cmbFilterName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbFilterNameItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(cmbFilterName, gridBagConstraints);

        cmbContentRule.setFont(getFont());
        cmbContentRule.setToolTipText(Translator.getTranslation("FILTERDIALOG.CONTENT_RULE_TOOLTIP"));
        cmbContentRule.setEnabled(false);
        cmbContentRule.setPreferredSize(new java.awt.Dimension(100, 22));
        cmbContentRule.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbContentRuleItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(cmbContentRule, gridBagConstraints);

        cmbContent.setFont(getFont());
        cmbContent.setToolTipText(Translator.getTranslation("FILTERDIALOG.CONTENT_TOOLTIP"));
        cmbContent.setEnabled(false);
        cmbContent.setPreferredSize(new java.awt.Dimension(100, 22));
        cmbContent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbContentItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(cmbContent, gridBagConstraints);

        txtContent.setFont(getFont());
        txtContent.setToolTipText(Translator.getTranslation("FILTERDIALOG.CONTENT_TOOLTIP"));
        txtContent.setEnabled(false);
        txtContent.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtContentKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(txtContent, gridBagConstraints);

        lblTotalTime.setFont(getFont());
        lblTotalTime.setLabelFor(txtTotalTime);
        lblTotalTime.setText(Translator.getTranslation("HISTORYVIEW.LBL_TOTAL_TIME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(lblTotalTime, gridBagConstraints);

        txtTotalTime.setEditable(false);
        txtTotalTime.setFont(getFont());
        txtTotalTime.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalTime.setToolTipText(Translator.getTranslation("HISTORYVIEW.TXT_TOTAL_TIME_TOOLTIP"));
        txtTotalTime.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimes.add(txtTotalTime, gridBagConstraints);

        tpViews.addTab(Translator.getTranslation("HISTORYVIEW.TIMES_TAB_NAME"), pnTimes);

        pnTasks.setFont(getFont());
        pnTasks.setLayout(new java.awt.GridBagLayout());

        lblFilters.setDisplayedMnemonic(Translator.getMnemonic("HISTORYVIEW.LBL_FILTERS"));
        lblFilters.setFont(getFont());
        lblFilters.setLabelFor(tbFilters);
        lblFilters.setText(Translator.getTranslation("HISTORYVIEW.LBL_FILTERS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTasks.add(lblFilters, gridBagConstraints);

        spFilters.setMinimumSize(new java.awt.Dimension(453, 80));
        spFilters.setPreferredSize(new java.awt.Dimension(453, 80));

        tbFilters.setFont(getFont());
        tbFilters.setModel(new FiltersTableModel());
        tbFilters.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbFiltersMouseClicked(evt);
            }
        });
        tbFilters.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbFiltersKeyReleased(evt);
            }
        });
        spFilters.setViewportView(tbFilters);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTasks.add(spFilters, gridBagConstraints);

        pnButtons.setLayout(new java.awt.GridBagLayout());

        btAddFilter.setFont(getFont());
        btAddFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/new_filter.png"))); // NOI18N
        btAddFilter.setMnemonic(Translator.getMnemonic("HISTORYVIEW.BT_ADD_FILTER"));
        btAddFilter.setText(Translator.getTranslation("HISTORYVIEW.BT_ADD_FILTER"));
        btAddFilter.setToolTipText(Translator.getTranslation("HISTORYVIEW.BT_ADD_FILTER_TOOLTIP"));
        btAddFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddFilterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnButtons.add(btAddFilter, gridBagConstraints);

        btEditFilter.setFont(getFont());
        btEditFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/edit_filter.png"))); // NOI18N
        btEditFilter.setMnemonic(Translator.getMnemonic("HISTORYVIEW.BT_EDIT_FILTER"));
        btEditFilter.setText(Translator.getTranslation("HISTORYVIEW.BT_EDIT_FILTER"));
        btEditFilter.setToolTipText(Translator.getTranslation("HISTORYVIEW.BT_EDIT_FILTER_TOOLTIP"));
        btEditFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEditFilterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnButtons.add(btEditFilter, gridBagConstraints);

        btRemoveFilter.setFont(getFont());
        btRemoveFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/remove_filter.png"))); // NOI18N
        btRemoveFilter.setMnemonic(Translator.getMnemonic("HISTORYVIEW.BT_REMOVE_FILTER"));
        btRemoveFilter.setText(Translator.getTranslation("HISTORYVIEW.BT_REMOVE_FILTER"));
        btRemoveFilter.setToolTipText(Translator.getTranslation("HISTORYVIEW.BT_REMOVE_FILTER_TOOLTIP"));
        btRemoveFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRemoveFilterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnButtons.add(btRemoveFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTasks.add(pnButtons, gridBagConstraints);

        lblTasks.setDisplayedMnemonic(Translator.getMnemonic("HISTORYVIEW.LBL_TASKS"));
        lblTasks.setFont(getFont());
        lblTasks.setLabelFor(tbTasks);
        lblTasks.setText(Translator.getTranslation("HISTORYVIEW.LBL_TASKS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTasks.add(lblTasks, gridBagConstraints);

        spTasks.setPreferredSize(new java.awt.Dimension(453, 100));

        tbTasks.setModel(new FilteredTasksTableModel());
        spTasks.setViewportView(tbTasks);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTasks.add(spTasks, gridBagConstraints);

        chbGroupTasks.setFont(getFont());
        chbGroupTasks.setMnemonic(Translator.getMnemonic("HISTORYVIEW.CHB_GROUP_TASKS"));
        chbGroupTasks.setSelected(true);
        chbGroupTasks.setText(Translator.getTranslation("HISTORYVIEW.CHB_GROUP_TASKS"));
        chbGroupTasks.setToolTipText(Translator.getTranslation("HISTORYVIEW.CHB_GROUP_TASKS_TOOLTIP"));
        chbGroupTasks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbGroupTasksActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTasks.add(chbGroupTasks, gridBagConstraints);

        pnTotalTime.setLayout(new java.awt.GridBagLayout());

        lblFilteredTime.setFont(getFont());
        lblFilteredTime.setLabelFor(txtFilteredTime);
        lblFilteredTime.setText(Translator.getTranslation("HISTORYVIEW.LBL_FILTERED_TIME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTotalTime.add(lblFilteredTime, gridBagConstraints);

        txtFilteredTime.setEditable(false);
        txtFilteredTime.setFont(getFont());
        txtFilteredTime.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFilteredTime.setToolTipText(Translator.getTranslation("HISTORYVIEW.TXT_FILTERED_TIME_TOOLTIP"));
        txtFilteredTime.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTotalTime.add(txtFilteredTime, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTasks.add(pnTotalTime, gridBagConstraints);

        tpViews.addTab(Translator.getTranslation("HISTORYVIEW.TASKS_TAB_NAME"), pnTasks);

        pnProjects.setFont(getFont());
        pnProjects.setLayout(new java.awt.GridBagLayout());

        spProjects.setPreferredSize(new java.awt.Dimension(81, 100));
        spProjects.setViewportView(jtProjects);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnProjects.add(spProjects, gridBagConstraints);

        pnDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Translator.getTranslation("HISTORYVIEW.PN_DETAILS"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnDetails.setLayout(new java.awt.GridBagLayout());

        lblName.setFont(getFont());
        lblName.setText(Translator.getTranslation("HISTORYVIEW.LBL_DETAILS_NAME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(lblName, gridBagConstraints);

        txtName.setEditable(false);
        txtName.setFont(getFont());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(txtName, gridBagConstraints);

        lblTime.setFont(getFont());
        lblTime.setText(Translator.getTranslation("HISTORYVIEW.LBL_DETAILS_TIME"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(lblTime, gridBagConstraints);

        txtTime.setEditable(false);
        txtTime.setFont(getFont());
        txtTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTime.setMinimumSize(new java.awt.Dimension(70, 20));
        txtTime.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(txtTime, gridBagConstraints);

        lblPercentage.setFont(getFont());
        lblPercentage.setText(Translator.getTranslation("HISTORYVIEW.LBL_DETAILS_PERCENTAGE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(lblPercentage, gridBagConstraints);

        txtPercentage.setEditable(false);
        txtPercentage.setFont(getFont());
        txtPercentage.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPercentage.setMinimumSize(new java.awt.Dimension(80, 20));
        txtPercentage.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(txtPercentage, gridBagConstraints);

        lbTasks.setFont(getFont());
        lbTasks.setText(Translator.getTranslation("HISTORYVIEW.LBL_DETAILS_TASKS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(lbTasks, gridBagConstraints);

        txtTasks.setEditable(false);
        txtTasks.setFont(getFont());
        txtTasks.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(txtTasks, gridBagConstraints);

        lblPriority.setFont(getFont());
        lblPriority.setText(Translator.getTranslation("HISTORYVIEW.LBL_DETAILS_PRIORITY"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(lblPriority, gridBagConstraints);

        txtPriority.setEditable(false);
        txtPriority.setFont(getFont());
        txtPriority.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(txtPriority, gridBagConstraints);

        lblState.setFont(getFont());
        lblState.setText(Translator.getTranslation("HISTORYVIEW.LBL_DETAILS_STATE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(lblState, gridBagConstraints);

        txtState.setEditable(false);
        txtState.setFont(getFont());
        txtState.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(txtState, gridBagConstraints);

        pnShare.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnShare.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDetails.add(pnShare, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnProjects.add(pnDetails, gridBagConstraints);

        tpViews.addTab(Translator.getTranslation("HISTORYVIEW.PROJECTS_TAB_NAME"), pnProjects);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tpViews, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void rbTimeUsageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTimeUsageActionPerformed
        rbTotal.setSelected(false);
        rbFromTo.setSelected(false);
        rbTimeUsage.setSelected(true);
        chbHighlightTasks.setEnabled(false);
        historyChart.setChartType(HistoryChart.TYPE_TIME_USAGE);
        setComponents();
}//GEN-LAST:event_rbTimeUsageActionPerformed

    private void tbFiltersKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbFiltersKeyReleased
        checkButtons();
    }//GEN-LAST:event_tbFiltersKeyReleased
    
    /** Method called when generate report button was clicked.
     * @param evt Event that invoked the action.
     */
    private void btReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btReportActionPerformed
        createReport();
    }//GEN-LAST:event_btReportActionPerformed
    
    /** Method called when highlight tasks checkbox is un/checked.
     * @param evt Event that invoked the action.
     */
    private void chbHighlightTasksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbHighlightTasksActionPerformed
        setComponents();
        historyChart.setHighlightingFilter(getFilter());
    }//GEN-LAST:event_chbHighlightTasksActionPerformed
    
    /** Method called when from/to chart type is required.
     * @param evt Event that invoked the action.
     */
    private void rbFromToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFromToActionPerformed
        rbTotal.setSelected(false);
        rbTimeUsage.setSelected(false);
        rbFromTo.setSelected(true);
        chbHighlightTasks.setEnabled(false);
        historyChart.setChartType(HistoryChart.TYPE_FROM_TO);
        setComponents();
    }//GEN-LAST:event_rbFromToActionPerformed
    
    /** Method called when total times chart type is required.
     * @param evt Event that invoked the action.
     */
    private void rbTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTotalActionPerformed
        rbTotal.setSelected(true);
        rbFromTo.setSelected(false);
        rbTimeUsage.setSelected(false);
        chbHighlightTasks.setEnabled(true);
        historyChart.setChartType(HistoryChart.TYPE_TOTAL);
        setComponents();
        historyChart.setHighlightingFilter(getFilter());
    }//GEN-LAST:event_rbTotalActionPerformed
    
    /** Method called when any key is typed in content textfield.
     * @param evt Event that invoked the action.
     */
    private void txtContentKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContentKeyTyped
        if (evt.getKeyChar() == evt.VK_ENTER) historyChart.setHighlightingFilter(getFilter());
    }//GEN-LAST:event_txtContentKeyTyped
    
    /** Method called when selection of content item has changed.
     * @param evt Event that invoked the action.
     */
    private void cmbContentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbContentItemStateChanged
        historyChart.setHighlightingFilter(getFilter());
    }//GEN-LAST:event_cmbContentItemStateChanged
    
    /** Method called when selection of content rule item has changed.
     * @param evt Event that invoked the action.
     */
    private void cmbContentRuleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbContentRuleItemStateChanged
        historyChart.setHighlightingFilter(getFilter());
    }//GEN-LAST:event_cmbContentRuleItemStateChanged
    
    /** Method called when selection of filter item has changed.
     * @param evt Event that invoked the action.
     */
    private void cmbFilterNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbFilterNameItemStateChanged
        setComponents();
        historyChart.setHighlightingFilter(getFilter());
    }//GEN-LAST:event_cmbFilterNameItemStateChanged
    
    /** Method called when checkbox "Group tasks with same name" is un/checked.
     * @param evt Event that invoked this action.
     */
    private void chbGroupTasksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbGroupTasksActionPerformed
        FilteredTasksTableModel filteredTasksTableModel = (FilteredTasksTableModel) tbTasks.getModel();
        filteredTasksTableModel.setGroupSameTasks(chbGroupTasks.isSelected());
    }//GEN-LAST:event_chbGroupTasksActionPerformed
    
    /** Method called when user clicked into tables of filters.
     * @param evt Event that invoked this action.
     */
    private void tbFiltersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbFiltersMouseClicked
        checkButtons();
    }//GEN-LAST:event_tbFiltersMouseClicked
    
    /** Method called when Edit Filter button was pressed.
     * @param evt Event that invoked this action.
     */
    private void btEditFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditFilterActionPerformed
        int row = tbFilters.getSelectedRow();
        FiltersTableModel tableModel = (FiltersTableModel) tbFilters.getModel();
        AbstractTaskFilter taskFilter = tableModel.getFilter(row);
        new FilterDialog(tableModel, taskFilter).setVisible(true);
        checkButtons();
        filterTasks();
    }//GEN-LAST:event_btEditFilterActionPerformed
    
    /** Method called when Remove Filter button was pressed.
     * @param evt Event that invoked this action.
     */
    private void btRemoveFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoveFilterActionPerformed
        int row = tbFilters.getSelectedRow();
        FiltersTableModel tableModel = (FiltersTableModel) tbFilters.getModel();
        AbstractTaskFilter taskFilter = tableModel.getFilter(row);
        tableModel.removeFilter(taskFilter);
        checkButtons();
        filterTasks();
    }//GEN-LAST:event_btRemoveFilterActionPerformed
    
    /** Method called when Add Filter button was pressed.
     * @param evt Event that invoked this action.
     */
    private void btAddFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddFilterActionPerformed
        FiltersTableModel tableModel = (FiltersTableModel) tbFilters.getModel();
        new FilterDialog(tableModel).setVisible(true);
        checkButtons();
        filterTasks();
    }//GEN-LAST:event_btAddFilterActionPerformed
    
    /** Method called when date textfield was clicked to select actual day/week/month/year.
     * @param evt Event that invoked the action.
     */
    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        period = new Date();
        cmbPeriodItemStateChanged(null);
    }//GEN-LAST:event_txtDateMouseClicked
    
    /** Method called when period scale should be changed.
     * @param evt Event that invoked the action.
     */
    private void cmbPeriodItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPeriodItemStateChanged
        int scale = cmbPeriod.getSelectedIndex();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        String format;
        switch (scale) {
            case (SCALE_YEAR):
                format = Translator.getTranslation("FORMAT.YEAR");
                sdf.applyPattern(format);
                txtDate.setText(Tools.replaceAll(sdf.format(period), "|", (String) cmbPeriod.getSelectedItem()));
                break;
            case (SCALE_MONTH):
                format = Translator.getTranslation("FORMAT.MONTH");
                sdf.applyPattern(format);
                txtDate.setText(sdf.format(period));
                break;
            case (SCALE_WEEK):
                format = Translator.getTranslation("FORMAT.WEEK");
                sdf.applyPattern(format);
                txtDate.setText(Tools.replaceAll(sdf.format(period), "|", (String) cmbPeriod.getSelectedItem()));
                break;
            default:
                format = Translator.getTranslation("FORMAT.DAY");
                sdf.applyPattern(format);
                txtDate.setText(sdf.format(period));
                break;
        }
        if (historyChart != null) historyChart.setDays(getDays());
        filterTasks();
        updateTotalTime();
        ProjectsTreeModel model = (ProjectsTreeModel) jtProjects.getModel();
        model.setDays(getDays());
        jtProjects.setSelectionRow(0);
    }//GEN-LAST:event_cmbPeriodItemStateChanged
    
    /** Method called when forward button was pressed.
     * @param evt Event that invoked the action.
     */
    private void btForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btForwardActionPerformed
        period = shiftPeriod(1);
        cmbPeriodItemStateChanged(null);
        updateTotalTime();
        ProjectsTreeModel model = (ProjectsTreeModel) jtProjects.getModel();
        model.setDays(getDays());
        jtProjects.setSelectionRow(0);
    }//GEN-LAST:event_btForwardActionPerformed
    
    /** Method called when backward button was pressed.
     * @param evt Event that invoked the action.
     */
    private void btBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBackwardActionPerformed
        period = shiftPeriod(-1);
        cmbPeriodItemStateChanged(null);
        updateTotalTime();
        ProjectsTreeModel model = (ProjectsTreeModel) jtProjects.getModel();
        model.setDays(getDays());
        jtProjects.setSelectionRow(0);
    }//GEN-LAST:event_btBackwardActionPerformed
    
    /** Method called when plus spinner was pressed.
     * @param evt Event that invoked the action.
     */
    private void spPlusStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spPlusStateChanged
        Integer plus = (Integer) spPlus.getValue();
        int value = plus.intValue();
        if (value < 0) spPlus.setValue(previousPlus);
        else previousPlus = plus;
        historyChart.setDays(getDays());
        filterTasks();
        updateTotalTime();
        ProjectsTreeModel model = (ProjectsTreeModel) jtProjects.getModel();
        model.setDays(getDays());
        jtProjects.setSelectionRow(0);
    }//GEN-LAST:event_spPlusStateChanged
    
    /** Method called when minus spinner was pressed.
     * @param evt Event that invoked the action.
     */
    private void spMinusStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spMinusStateChanged
        Integer minus = (Integer) spMinus.getValue();
        int value = minus.intValue();
        if (value > 0) spMinus.setValue(previousMinus);
        else previousMinus = minus;
        historyChart.setDays(getDays());
        filterTasks();
        updateTotalTime();
        ProjectsTreeModel model = (ProjectsTreeModel) jtProjects.getModel();
        model.setDays(getDays());
        jtProjects.setSelectionRow(0);
    }//GEN-LAST:event_spMinusStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddFilter;
    private javax.swing.JButton btBackward;
    private javax.swing.JButton btEditFilter;
    private javax.swing.JButton btForward;
    private javax.swing.JButton btRemoveFilter;
    private javax.swing.JButton btReport;
    private javax.swing.JCheckBox chbGroupTasks;
    private javax.swing.JCheckBox chbHighlightTasks;
    private javax.swing.JComboBox cmbContent;
    private javax.swing.JComboBox cmbContentRule;
    private javax.swing.JComboBox cmbFilterName;
    private javax.swing.JComboBox cmbPeriod;
    private javax.swing.JTree jtProjects;
    private javax.swing.JLabel lbTasks;
    private javax.swing.JLabel lblChartType;
    private javax.swing.JLabel lblFilteredTime;
    private javax.swing.JLabel lblFilters;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPercentage;
    private javax.swing.JLabel lblPeriod;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblState;
    private javax.swing.JLabel lblTasks;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTotalTime;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JPanel pnDetails;
    private javax.swing.JPanel pnPeriod;
    private javax.swing.JPanel pnProjects;
    private javax.swing.JPanel pnShare;
    private javax.swing.JPanel pnTasks;
    private javax.swing.JPanel pnTimes;
    private javax.swing.JPanel pnTotalTime;
    private javax.swing.JRadioButton rbFromTo;
    private javax.swing.JRadioButton rbTimeUsage;
    private javax.swing.JRadioButton rbTotal;
    private javax.swing.JScrollPane spFilters;
    private javax.swing.JSpinner spMinus;
    private javax.swing.JSpinner spPlus;
    private javax.swing.JScrollPane spProjects;
    private javax.swing.JScrollPane spTasks;
    private javax.swing.JTable tbFilters;
    private javax.swing.JTable tbTasks;
    private javax.swing.JTabbedPane tpViews;
    private javax.swing.JTextField txtContent;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtFilteredTime;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPercentage;
    private javax.swing.JTextField txtPriority;
    private javax.swing.JTextField txtState;
    private javax.swing.JTextField txtTasks;
    private javax.swing.JTextField txtTime;
    private javax.swing.JTextField txtTotalTime;
    // End of variables declaration//GEN-END:variables
    
    /** Index of day time scale */
    private static final int SCALE_DAY = 0;
    /** Index of week time scale */
    private static final int SCALE_WEEK = 1;
    /** Index of month time scale */
    private static final int SCALE_MONTH = 2;
    /** Index of year time scale */
    private static final int SCALE_YEAR = 3;
    /** Last correct value of spPlus setting specified by user. */
    private Integer previousPlus = new Integer(0);
    /** Last correct value of spMinus setting specified by user. */
    private Integer previousMinus = new Integer(0);
    /** History chart painter. */
    private HistoryChart historyChart;
    /** Currently selected time period. */
    private Date period = new Date();
    
    /** Shifts selected period for given steps based on currently
     * selected time scale. For example if "day" is selected and
     * step is -1, it returns date before selected one.
     * @param step Number of units to be added/substracted.
     * @return Date determined as period + step * scale.
     */
    private Date shiftPeriod(int step) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(period);
        int scale = cmbPeriod.getSelectedIndex();
        switch (scale) {
            case (SCALE_YEAR):
                calendar.add(Calendar.YEAR, step);
                break;
            case (SCALE_MONTH):
                calendar.add(Calendar.MONTH, step);
                break;
            case (SCALE_WEEK):
                calendar.add(Calendar.WEEK_OF_YEAR, step);
                break;
            default:
                calendar.add(Calendar.DAY_OF_YEAR, step);
                break;
        }
        return calendar.getTime();
    }
    
    /** Identification of the first date within selected preiod. */
    private static final boolean FIRST_DATE = true;
    /** Identification of the last date within selected preiod. */
    private static final boolean LAST_DATE = false;
    
    /** Returns either first or last date of selected period interval
     * whose data should be processed in the history view.
     * @param date If true, first date of period interval will be
     * returned or last interval date if false.
     * @return First or last date of period interval.
     */
    private Date getDate(boolean date) {
        Calendar calendar = Calendar.getInstance();
        int offset = date == FIRST_DATE ? previousMinus.intValue() : previousPlus.intValue();
        calendar.setTime(shiftPeriod(offset));
        int scale = cmbPeriod.getSelectedIndex();
        offset = 1;
        switch (scale) {
            case (SCALE_YEAR):
                if (date == LAST_DATE) offset = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
                calendar.set(Calendar.DAY_OF_YEAR, offset);
                break;
            case (SCALE_MONTH):
                if (date == LAST_DATE) offset = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.DAY_OF_MONTH, offset);
                break;
            case (SCALE_WEEK):
                offset = calendar.getFirstDayOfWeek();
                if (date == LAST_DATE) offset = offset + 6;
                calendar.set(Calendar.DAY_OF_WEEK, offset);
        }
        return calendar.getTime();
    }
    
    /** Returns all days in selected period interval whose tasks will be processed.
     * @return All days in selected period interval whose tasks will be processed.
     */
    private Vector getDays() {
        Vector days = new Vector();
        Calendar calendar = Calendar.getInstance();
        Plan plan = Plan.getDefault();
        
        Date firstDate = getDate(FIRST_DATE);
        Date lastDate = getDate(LAST_DATE);
        
        calendar.setTime(lastDate);
        String lastDayID = plan.getDayID(calendar);
        
        Day day = plan.getDay(firstDate);
        days.add(day);
        while (true) {
            calendar.setTime(day.getDate());
            if (plan.getDayID(calendar).equals(lastDayID)) break;
            day = plan.getDayAfter(day);
            days.add(day);
        }
        return days;
    }
    
    /** Checks whether edit and remove filter buttons should be enabled.
     */
    private void checkButtons() {
        int row = tbFilters.getSelectedRow();
        btRemoveFilter.setEnabled(row != -1);
        btEditFilter.setEnabled(row != -1);
    }
    
    /** Takes all tasks from days in the selected period and filters them
     * using all defined filters.
     */
    private void filterTasks() {
        Vector filteredTasks = new Vector();
        Iterator iterator = getDays().iterator();
        while (iterator.hasNext()) {
            Day day = (Day) iterator.next();
            filteredTasks.addAll(day.getTasks());
        }
        FiltersTableModel filtersTableModel = (FiltersTableModel) tbFilters.getModel();
        Vector filters = (Vector) filtersTableModel.getFilters().clone();
        filters.add(new IdleFilter(IdleFilter.RULE_EQUALS_NOT, new Boolean(true)));
        iterator = filters.iterator();
        while (iterator.hasNext()) {
            AbstractTaskFilter abstractTaskFilter = (AbstractTaskFilter) iterator.next();
            filteredTasks = abstractTaskFilter.filterTasks(filteredTasks);
        }
        FilteredTasksTableModel filteredTasksTableModel = (FilteredTasksTableModel) tbTasks.getModel();
        filteredTasksTableModel.setTasks(filteredTasks);
        txtFilteredTime.setText(Tools.getTime(filteredTasksTableModel.getTotalTime()));
    }
    
    /** Sets content rules and values according to saved user customizations. */
    private void loadSetup() {
        Settings settings = Settings.getDefault();
        if (settings.getSetting("history.period") == null) return;
        cmbPeriod.setSelectedIndex(Integer.parseInt((String) settings.getSetting("history.period")));
        chbGroupTasks.setSelected(settings.getSetting("history.group").toString().equals("true"));
        int filtersCount = Integer.parseInt((String) settings.getSetting("history.filters"));
        for (int i = 0; i < filtersCount; i++) {
            String filterData = (String) settings.getSetting("history.filter." + i);
            String filterName = filterData.substring(1, filterData.indexOf(","));
            String filterContentRule = filterData.substring(filterData.indexOf(",")+1, filterData.lastIndexOf(","));
            String filterContent = filterData.substring(filterData.lastIndexOf(",")+1, filterData.length()-1);
            try {
                AbstractTaskFilter filter = (AbstractTaskFilter) Class.forName(filterName).newInstance();
                filter.setContentRule(Integer.parseInt(filterContentRule));
                filter.setContent(filterContent);
                FiltersTableModel filtersModel = (FiltersTableModel) tbFilters.getModel();
                filtersModel.addFilter(filter);
            } catch (Exception exception) {
                System.out.println("Error: Cannot load this filter: " + filterData);
                exception.printStackTrace();
            }
        }
        filterTasks();
        rbTotal.setSelected(false);
        rbFromTo.setSelected(false);
        rbTimeUsage.setSelected(false);
        chbHighlightTasks.setEnabled(false);
        String chartType = (String) settings.getSetting("history.chart");
        if (chartType.equals("time")) {
            rbTotal.setSelected(true);
            chbHighlightTasks.setEnabled(true);
            historyChart.setChartType(HistoryChart.TYPE_TOTAL);
            chbHighlightTasks.setSelected(settings.getSetting("history.highlight").toString().equals("true"));
        }
        if (chartType.equals("from/to")) {
            rbFromTo.setSelected(true);
            historyChart.setChartType(HistoryChart.TYPE_FROM_TO);
        }
        if (chartType.equals("usage")) {
            rbTimeUsage.setSelected(true);
            historyChart.setChartType(HistoryChart.TYPE_TIME_USAGE);
        }
        
        if (!chbHighlightTasks.isSelected() || !chbHighlightTasks.isEnabled()) {
            cmbFilterName.setEnabled(false);
            cmbContentRule.setEnabled(false);
            txtContent.setEnabled(false);
            cmbContent.setEnabled(false);
            return;
        }
        
        cmbFilterName.setEnabled(true);
        cmbContentRule.setEnabled(true);
        cmbFilterName.setSelectedIndex(Integer.parseInt((String) settings.getSetting("history.filter")));
        AbstractTaskFilter taskFilter = getFilter();
        Vector contentRules = taskFilter.getContentRules();
        int length = contentRules.size();
        cmbContentRule.removeAllItems();
        for (int i=0; i<length; i++)
            cmbContentRule.addItem(contentRules.get(i));
        cmbContentRule.setSelectedIndex(Integer.parseInt((String) settings.getSetting("history.rule")));
        
        String content = (String) settings.getSetting("history.content");
        Vector contentValues = taskFilter.getContentValues();
        cmbContent.removeAllItems();
        if (contentValues != null) {
            length = contentValues.size();
            for (int i=0; i<length; i++)
                cmbContent.addItem(contentValues.get(i));
            cmbContent.setSelectedIndex(Integer.parseInt(content));
        } else txtContent.setText(content);
        cmbContent.setEnabled(contentValues != null);
        txtContent.setEnabled(contentValues == null);
        historyChart.setHighlightingFilter(getFilter());
    }
    
    /** Sets content rules and values according to currently selected task filter.
     */
    private void setComponents() {
        if (!chbHighlightTasks.isSelected() || !chbHighlightTasks.isEnabled()) {
            cmbFilterName.setEnabled(false);
            cmbContentRule.setEnabled(false);
            txtContent.setEnabled(false);
            cmbContent.setEnabled(false);
            return;
        }
        
        cmbFilterName.setEnabled(true);
        cmbContentRule.setEnabled(true);
        AbstractTaskFilter taskFilter = getFilter();
        Vector contentRules = taskFilter.getContentRules();
        int length = contentRules.size();
        cmbContentRule.removeAllItems();
        for (int i=0; i<length; i++)
            cmbContentRule.addItem(contentRules.get(i));
        cmbContentRule.setSelectedIndex(0);
        
        Vector contentValues = taskFilter.getContentValues();
        cmbContent.removeAllItems();
        if (contentValues != null) {
            length = contentValues.size();
            for (int i=0; i<length; i++)
                cmbContent.addItem(contentValues.get(i));
            cmbContent.setSelectedIndex(0);
        } else txtContent.setText("");
        if (taskFilter instanceof DurationFilter) txtContent.setText(Tools.getTime(0));
        cmbContent.setEnabled(contentValues != null);
        txtContent.setEnabled(contentValues == null);
    }
    
    /** Returns task filter object based on currently selected options.
     * @return Task filter object based on currently selected options.
     */
    private AbstractTaskFilter getFilter() {
        if (!chbHighlightTasks.isSelected()) return null;
        String filterName = (String) cmbFilterName.getSelectedItem();
        AbstractTaskFilter taskFilter = null;
        if (new DescriptionFilter().toString().equals(filterName)) {
            taskFilter = new DescriptionFilter();
            taskFilter.setContent(txtContent.getText());
        }
        if (new KeywordFilter().toString().equals(filterName)) {
            taskFilter = new KeywordFilter();
            taskFilter.setContent(txtContent.getText());
        }
        if (new DurationFilter().toString().equals(filterName)) {
            taskFilter = new DurationFilter();
            taskFilter.setContent(Tools.getTime(0));
            try {
                String text = txtContent.getText();
                if (text.equals("")) {
                    text = Tools.getTime(0);
                    txtContent.setText(Tools.getTime(0));
                }
                if (text.length() != 8) throw new NumberFormatException("Error: invalid task duration specified: " + text);
                else taskFilter.setContent(Tools.getTime(Tools.getTime(text)));
            } catch (NumberFormatException e) {
                txtContent.setText(Tools.getTime(0));
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.INVALID_DURATION"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            }
        }
        if (new PriorityFilter().toString().equals(filterName)) {
            taskFilter = new PriorityFilter();
            taskFilter.setContent("" + cmbContent.getSelectedIndex());
        }
        if (new StateFilter().toString().equals(filterName)) {
            taskFilter = new StateFilter();
            taskFilter.setContent("" + cmbContent.getSelectedIndex());
        }
        if (new PrivateFilter().toString().equals(filterName)) {
            taskFilter = new PrivateFilter();
            taskFilter.setContent(Boolean.toString(cmbContent.getSelectedIndex() == 0));
        }
        taskFilter.setContentRule(cmbContentRule.getSelectedIndex());
        return taskFilter;
    }
    
    /** Generates HTML file with work statistics for selected period.
     */
    private void createReport() {
        String location = (String) Settings.getDefault().getSetting("reportDir");
        if (location == null) location = (String) Settings.getDefault().getSetting("userDir");
        JFileChooser fileChooser = new JFileChooser(location);
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory() || f.isFile() && f.getName().endsWith(".html"));
            }
            public String getDescription() {
                return "All HTML Files";
            }
        });
        fileChooser.setApproveButtonText(Translator.getTranslation("HISTORYVIEW.BT_SELECT"));
        fileChooser.setApproveButtonMnemonic(Translator.getMnemonic("HISTORYVIEW.BT_SELECT"));
        fileChooser.setApproveButtonToolTipText(Translator.getTranslation("HISTORYVIEW.BT_SELECT_TOOLTIP"));
        int decision = fileChooser.showOpenDialog(this);
        if (decision != JFileChooser.APPROVE_OPTION) return;
        File file = fileChooser.getSelectedFile();
        String fileName = file.getAbsolutePath();
        if (!fileName.endsWith(".html")) file = new File(fileName + ".html");
        Settings.getDefault().setSetting("reportDir", file.getParent());
        fileName = file.getAbsolutePath();
        fileName = fileName.substring(0, fileName.indexOf("."));
        if (file.exists()) {
            String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
            decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.OVERWRITE_FILE", new String[] {file.getName()}), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
            if (decision != JOptionPane.YES_OPTION) return;
        }
        String description = JOptionPane.showInputDialog(this, Translator.getTranslation("QUESTION.REPORT_DESCRIPTION"), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.QUESTION_MESSAGE);
        if (description == null) return;
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), (String) Settings.getDefault().getSetting("systemEncoding"));
            writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            writer.write("\n");
            writer.write("<!--");
            writer.write("\n");
            writer.write("    Rachota 2.0 report file");
            writer.write("\n");
            writer.write("    Generated: " + new Date());
            writer.write("\n");
            writer.write("-->");
            writer.write("\n");
            writer.write("<html lang=\"" + Locale.getDefault().getLanguage() + "\">");
            writer.write("\n");
            writer.write("  <head>");
            writer.write("\n");
            writer.write("    <title>" + Translator.getTranslation("REPORT.TITLE") + "</title>");
            writer.write("\n");
            writer.write("    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=" + (String) Settings.getDefault().getSetting("systemEncoding") + "\">");
            writer.write("\n");
            writer.write("  </head>");
            writer.write("\n");
            writer.write("  <body>");
            writer.write("\n");
            writer.write("    <h1>" + Translator.getTranslation("REPORT.TITLE") + "</h1>");
            writer.write("\n");
            writer.write("    <table>");
            writer.write("\n");
            writer.write("      <tr><td><u>" + Translator.getTranslation("QUESTION.REPORT_DESCRIPTION") + "</u></td><td width=\"20\"/><td>" + description + "</td></tr>");
            writer.write("\n");
            Vector days = getDays();
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            Day firstDay = (Day) days.get(0);
            Day lastDay = (Day) days.get(days.size() - 1);
            int numberOfWorkingDays = 0;
            Iterator iterator = days.iterator();
            while(iterator.hasNext()) {
                Day day = (Day) iterator.next();
                if (day.getTotalTime() != 0) numberOfWorkingDays++;
            }
            writer.write("      <tr><td><u>" + Translator.getTranslation("REPORT.PERIOD") + "</u></td><td width=\"20\"/><td>" + df.format(firstDay.getDate()) + " - " + df.format(lastDay.getDate()) + "</td></tr>");
            writer.write("\n");
            writer.write("      <tr><td><u>" + Translator.getTranslation("REPORT.NUMBER_OF_DAYS") + "</u></td><td width=\"20\"/><td>" + days.size() + "</td></tr>");
            writer.write("\n");
            writer.write("      <tr><td><u>" + Translator.getTranslation("REPORT.NUMBER_OF_WORK_DAYS") + "</u></td><td width=\"20\"/><td>" + numberOfWorkingDays + "</td></tr>");
            writer.write("\n");
            writer.write("    </table><br/>");
            writer.write("\n");
            writer.write("    <img src=\"" + file.getName().substring(0, file.getName().indexOf(".")) + "_chart.png\" border=\"1\"/><br/><br/>");
            writer.write("\n");
            AbstractTaskFilter filter = getFilter();
            if (filter != null) {
                writer.write("    <u>" + Translator.getTranslation("HISTORYVIEW.LBL_HIGHLIGHT_TASKS") + "</u>");
                writer.write("\n");
                writer.write("    <ul><li>" + filter.toString() + " " + filter.getContentRules().get(filter.getContentRule()) + " <b>" + filter.getContent() + "</b></li></ul>");
                writer.write("\n");
            }
            writer.write("    <u>" + Translator.getTranslation("REPORT.APPLIED_FILTERS") + "</u>");
            writer.write("\n");
            writer.write("    <ul>");
            writer.write("\n");
            FiltersTableModel filtersTableModel = (FiltersTableModel) tbFilters.getModel();
            int count = filtersTableModel.getRowCount();
            for (int i=0; i<count; i++) {
                writer.write("      <li>" + filtersTableModel.getValueAt(i, FiltersTableModel.FILTER_NAME) + " ");
                writer.write(filtersTableModel.getValueAt(i, FiltersTableModel.FILTER_CONTENT_RULE) + " ");
                writer.write("<b>" + filtersTableModel.getValueAt(i, FiltersTableModel.FILTER_CONTENT) + "</b></li>");
                writer.write("\n");
            }
            writer.write("    </ul>");
            writer.write("\n");
            writer.write("    <u>" + Translator.getTranslation("REPORT.TASKS") + "</u><br/><br/>");
            writer.write("\n");
            writer.write("    <table border=\"1\">");
            writer.write("\n");
            writer.write("      <tr bgcolor=\"CCCCCC\">");
            writer.write("\n");
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DESCRIPTION") + "</b></td>");
            writer.write("\n");
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DURATION_TIME") + "</b></td>");
            writer.write("\n");
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DURATION_DAYS") + "</b></td>");
            writer.write("\n");
            writer.write("      </tr>");
            writer.write("\n");
            FilteredTasksTableModel filteredTasksTableModel = (FilteredTasksTableModel) tbTasks.getModel();
            count = filteredTasksTableModel.getRowCount();
            for (int i=0; i<count; i++) {
                writer.write("      <tr>");
                writer.write("\n");
                writer.write("        <td>" + filteredTasksTableModel.getValueAt(i, FilteredTasksTableModel.DESCRIPTION) + "</td>");
                writer.write("\n");
                writer.write("        <td>" + filteredTasksTableModel.getValueAt(i, FilteredTasksTableModel.DURATION_TIME) + "</td>");
                writer.write("\n");
                writer.write("        <td align=\"right\">" + filteredTasksTableModel.getValueAt(i, FilteredTasksTableModel.DURATION_DAYS) + "</td>");
                writer.write("\n");
                writer.write("      </tr>");
                writer.write("\n");
            }
            writer.write("    </table><br/>");
            writer.write("\n");
            writer.write("    <u>" + Translator.getTranslation("REPORT.TOTAL_FILTERED_TIME") + "</u> " + Tools.getTime(filteredTasksTableModel.getTotalTime()) + "<br/>");
            writer.write("\n");
            writer.write("    <u>" + Translator.getTranslation("REPORT.TOTAL_TIME") + "</u><b> " + Tools.getTime(getTotalTime()) + "</b><br/><br/>");
            writer.write("\n");
            writer.write("    <hr/><u>" + Translator.getTranslation("REPORT.GENERATED_BY") + "</u> <a href=\"http://rachota.sourceforge.net/\">" + Tools.title + "</a> " + "(build " + Tools.build + ")<br/>");
            writer.write("\n");
            writer.write("    " + new Date());
            writer.write("\n");
            writer.write("  </body>");
            writer.write("\n");
            writer.write("</html>");
            writer.write("\n");
            writer.close();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName + "_chart.png"));
            PNGImageWriter imageWriter = new PNGImageWriter();
            BufferedImage image = new BufferedImage(historyChart.getBounds().width, historyChart.getBounds().height, BufferedImage.TYPE_INT_RGB);
            historyChart.paint(image.getGraphics());
            imageWriter.write(image, out);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {location}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(this, Translator.getTranslation("INFORMATION.REPORT_CREATED"), Translator.getTranslation("INFORMATION.INFORMATION_TITLE"), JOptionPane.INFORMATION_MESSAGE);
    }
    
    /** Updates information about total time and returns the time. */
    private void updateTotalTime() {
        long totalTime = getTotalTime();
        txtTotalTime.setText(Tools.getTime(totalTime));
    }
    
    /** Saves setup customized by user e.g. time scale, highlighted tasks etc. */
    public void saveSetup() {
        Settings settings = Settings.getDefault();
        settings.setSetting("history.period", new Integer(cmbPeriod.getSelectedIndex()));
        if (rbFromTo.isSelected()) settings.setSetting("history.chart", "from/to");
        if (rbTotal.isSelected()) settings.setSetting("history.chart", "time");
        if (rbTimeUsage.isSelected()) settings.setSetting("history.chart", "usage");
        settings.setSetting("history.highlight", new Boolean(chbHighlightTasks.isSelected()));
        settings.setSetting("history.filter", new Integer(cmbFilterName.getSelectedIndex()));
        settings.setSetting("history.rule", new Integer(cmbContentRule.getSelectedIndex()));
        if (cmbContent.isEnabled()) settings.setSetting("history.content", new Integer(cmbContent.getSelectedIndex()));
        else settings.setSetting("history.content", txtContent.getText());
        settings.setSetting("history.group", new Boolean(chbGroupTasks.isSelected()));
        FiltersTableModel filtersModel = (FiltersTableModel) tbFilters.getModel();
        settings.setSetting("history.filters", new Integer(filtersModel.getRowCount()));
        Vector filters = filtersModel.getFilters();
        int count = filters.size();
        for (int i = 0; i < count; i++) {
            AbstractTaskFilter filter = (AbstractTaskFilter) filters.get(i);
            Vector contentValues = filter.getContentValues();
            String content = filter.getContent();
            settings.setSetting("history.filter." + i,
                    "[" + filter.getClass().getName() +
                    "," + filter.getContentRule() +
                    "," + (contentValues == null ? content : "" + contentValues.indexOf(content)) + "]");
        }
        FilteredTasksTableModel model = (FilteredTasksTableModel) tbTasks.getModel();
        String columnWidths = "";
        for (int i = 0; i < 3; i++) {
            int size = tbTasks.getColumnModel().getColumn(i).getWidth();
            columnWidths = columnWidths + "[" + size + "]";
        }
        settings.setSetting("history.columns", columnWidths);
        settings.setSetting("history.sortedColumn", "" + model.getSortedColumn() + model.getSortedOrder());
        settings.setSetting("history.range", "[" + spMinus.getValue() + "," + spPlus.getValue() + "]");
    }
    
    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        FilteredTasksTableModel filteredTasksTableModel = (FilteredTasksTableModel) tbTasks.getModel();
        filteredTasksTableModel.fireTableDataChanged();
        txtFilteredTime.setText(Tools.getTime(filteredTasksTableModel.getTotalTime()));
        historyChart.setDays(getDays());
        updateTotalTime();
    }
}