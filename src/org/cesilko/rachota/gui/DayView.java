/*
 * DayView.java
 *
 * Created on February 24, 2005, 7:45 AM
 */

package org.cesilko.rachota.gui;

import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.cesilko.rachota.core.Clock;
import org.cesilko.rachota.core.ClockListener;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Panel showing tasks planned for one day and manipulation with them.
 * @author Jiri Kovalsky
 */
public class DayView extends javax.swing.JPanel implements ClockListener, PropertyChangeListener {
    
    /** Creates new DayView panel containing tasks planned for given day.
     */
    public DayView() {
        day = Plan.getDefault().getDay(new Date());
        Plan.getDefault().addRegularTasks(day);
        task = null;
        day.addPropertyChangeListener(this);
        Clock.getDefault().addListener(this);
        initComponents();
        tbPlan.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Boolean displayFinishedTasks = (Boolean) Settings.getDefault().getSetting("displayFinishedTasks");
        chbShowFinished.setSelected(displayFinishedTasks.booleanValue());
        ImageIcon icon = new ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/table.png"));
        final JButton corner = new JButton(icon);
        corner.setToolTipText(Translator.getTranslation("DAYVIEW.TABLE_HEADER_HINT"));
        spPlan.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        MouseListener listener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                final JPopupMenu menu = new JPopupMenu();
                final DayTableModel tableModel = (DayTableModel) tbPlan.getModel();
                String[] columnNames = tableModel.getAllColumnNames();
                final int count = columnNames.length;
                for (int i=0; i<count; i++) {
                    final JCheckBoxMenuItem item = new JCheckBoxMenuItem(columnNames[i]);
                    item.setSelected(tableModel.isSelectedColumn(i));
                    final int columnID = i;
                    item.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            tableModel.setSelectedColumn(columnID, item.isSelected());
                            tableModel.fireTableStructureChanged();
                            if (tableModel.getSelectedColumnsCount() == 1) {
                                JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) menu.getComponent(tableModel.getColumnID(0));
                                menuItem.setEnabled(false);
                            } else for (int j=0; j<count; j++) {
                                JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) menu.getComponent(j);
                                menuItem.setEnabled(true);
                            }
                        }
                    });
                    menu.add(item);
                }
                if (tableModel.getSelectedColumnsCount() == 1) {
                    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) menu.getComponent(tableModel.getColumnID(0));
                    menuItem.setEnabled(false);
                }
                Point point = corner.getLocationOnScreen();
                menu.setLocation(point);
                menu.setVisible(true);
                MouseAdapter listener = new MouseAdapter() {
                    public void mouseExited(MouseEvent e) {
                        menu.setVisible(false);
                        menu.removeMouseListener(this);
                    }
                };
                menu.addMouseListener(listener);
            }
        };
        spPlan.getCorner(JScrollPane.UPPER_RIGHT_CORNER).addMouseListener(listener);
        tbPlan.getTableHeader().addMouseListener(new MouseAdapter() {
            Point pressedPoint;
            public void mousePressed(MouseEvent e) {
                pressedPoint = e.getPoint();
            }
            public void mouseReleased(MouseEvent e) {
                if (!e.getPoint().equals(pressedPoint)) return;
                int column = tbPlan.getTableHeader().columnAtPoint(e.getPoint());
                DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
                dayTableModel.setSortedColumn(column, true);
                int columns = tbPlan.getColumnCount();
                for (int i=0; i<columns; i++)
                    tbPlan.getColumnModel().getColumn(i).setHeaderValue(dayTableModel.getColumnName(i));
            }
        });
        DayTableModel tableModel = (DayTableModel) tbPlan.getModel();
        tableModel.setSortedColumn(DayTableModel.TASK_PRIORITY, false);
        updateInformation(false);
        loadRunningTask();
        checkButtons();
        tbPlan.getColumn(Translator.getTranslation("TASK_DESCRIPTION")).setPreferredWidth(240);
        tbPlan.getColumn(Translator.getTranslation("TASK_STATE")).setPreferredWidth(100);
        tbPlan.getTableHeader().setForeground(java.awt.Color.BLUE);
        tbPlan.getTableHeader().setBackground(java.awt.Color.LIGHT_GRAY);
        tbPlan.getTableHeader().setFont(getFont());
        tbPlan.setFont(getFont());
        tbPlan.setRowHeight(getFont().getSize() + 2);
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

        btPrevious = new javax.swing.JButton();
        btPreviousWeek = new javax.swing.JButton();
        btPreviousMonth = new javax.swing.JButton();
        lblDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        lblWeek = new javax.swing.JLabel();
        txtWeek = new javax.swing.JTextField();
        btNext = new javax.swing.JButton();
        btNextMonth = new javax.swing.JButton();
        btNextWeek = new javax.swing.JButton();
        pnDayView = new javax.swing.JPanel();
        lblStart = new javax.swing.JLabel();
        txtStart = new javax.swing.JTextField();
        lblEnd = new javax.swing.JLabel();
        txtEnd = new javax.swing.JTextField();
        lblProgress = new javax.swing.JLabel();
        pbProgress = new javax.swing.JProgressBar();
        lblTask = new javax.swing.JLabel();
        txtTask = new javax.swing.JTextField();
        pnWorkButtons = new javax.swing.JPanel();
        btWork = new javax.swing.JButton();
        btRelax = new javax.swing.JButton();
        btDone = new javax.swing.JButton();
        lblPlan = new javax.swing.JLabel();
        spPlan = new javax.swing.JScrollPane();
        tbPlan = new javax.swing.JTable();
        chbShowFinished = new javax.swing.JCheckBox();
        pnButtons = new javax.swing.JPanel();
        btSelect = new javax.swing.JButton();
        btAdd = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setName(Translator.getTranslation("DAYVIEW.TB_NAME"));
        btPrevious.setFont(getFont());
        btPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/previous.png")));
        btPrevious.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_PREVIOUS"));
        btPrevious.setText(Translator.getTranslation("DAYVIEW.BT_PREVIOUS"));
        btPrevious.setToolTipText(Translator.getTranslation("DAYVIEW.BT_PREVIOUS_TOOLTIP"));
        btPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPreviousActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        add(btPrevious, gridBagConstraints);

        btPreviousWeek.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/previous_week.png")));
        btPreviousWeek.setToolTipText(Translator.getTranslation("DAYVIEW.BT_PREVIOUS_WEEK_TOOLTIP"));
        btPreviousWeek.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btPreviousWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPreviousWeekActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(btPreviousWeek, gridBagConstraints);

        btPreviousMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/previous_month.png")));
        btPreviousMonth.setToolTipText(Translator.getTranslation("DAYVIEW.BT_PREVIOUS_MONTH_TOOLTIP"));
        btPreviousMonth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btPreviousMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPreviousMonthActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btPreviousMonth, gridBagConstraints);

        lblDate.setFont(getFont());
        lblDate.setText(Translator.getTranslation("DAYVIEW.LBL_DATE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDate, gridBagConstraints);

        txtDate.setEditable(false);
        txtDate.setFont(getFont());
        txtDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDate.setToolTipText(Translator.getTranslation("DAYVIEW.TXT_DATE_TOOLTIP"));
        txtDate.setDoubleBuffered(true);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtDate, gridBagConstraints);

        lblWeek.setFont(getFont());
        lblWeek.setText(Translator.getTranslation("DAYVIEW.LBL_WEEK"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblWeek, gridBagConstraints);

        txtWeek.setEditable(false);
        txtWeek.setFont(getFont());
        txtWeek.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtWeek.setDoubleBuffered(true);
        txtWeek.setMinimumSize(new java.awt.Dimension(40, 19));
        txtWeek.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtWeek, gridBagConstraints);

        btNext.setFont(getFont());
        btNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/next.png")));
        btNext.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_NEXT"));
        btNext.setText(Translator.getTranslation("DAYVIEW.BT_NEXT"));
        btNext.setToolTipText(Translator.getTranslation("DAYVIEW.BT_NEXT_TOOLTIP"));
        btNext.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNextActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        add(btNext, gridBagConstraints);

        btNextMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/next_month.png")));
        btNextMonth.setToolTipText(Translator.getTranslation("DAYVIEW.BT_NEXT_MONTH_TOOLTIP"));
        btNextMonth.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btNextMonth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btNextMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNextMonthActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btNextMonth, gridBagConstraints);

        btNextWeek.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/next_week.png")));
        btNextWeek.setToolTipText(Translator.getTranslation("DAYVIEW.BT_NEXT_WEEK_TOOLTIP"));
        btNextWeek.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btNextWeek.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btNextWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNextWeekActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(btNextWeek, gridBagConstraints);

        pnDayView.setLayout(new java.awt.GridBagLayout());

        lblStart.setFont(getFont());
        lblStart.setText(Translator.getTranslation("DAYVIEW.LBL_START"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(lblStart, gridBagConstraints);

        txtStart.setColumns(5);
        txtStart.setEditable(false);
        txtStart.setFont(getFont());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(txtStart, gridBagConstraints);

        lblEnd.setFont(getFont());
        lblEnd.setText(Translator.getTranslation("DAYVIEW.LBL_END"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(lblEnd, gridBagConstraints);

        txtEnd.setColumns(5);
        txtEnd.setEditable(false);
        txtEnd.setFont(getFont());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(txtEnd, gridBagConstraints);

        lblProgress.setFont(getFont());
        lblProgress.setText(Translator.getTranslation("DAYVIEW.LBL_PROGRESS"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(lblProgress, gridBagConstraints);

        pbProgress.setFont(getFont().deriveFont(java.awt.Font.BOLD));
        pbProgress.setPreferredSize(new java.awt.Dimension(148, 19));
        pbProgress.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(pbProgress, gridBagConstraints);

        lblTask.setFont(getFont());
        lblTask.setText(Translator.getTranslation("DAYVIEW.LBL_TASK"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(lblTask, gridBagConstraints);

        txtTask.setEditable(false);
        txtTask.setFont(getFont());
        txtTask.setDoubleBuffered(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(txtTask, gridBagConstraints);

        btWork.setFont(getFont());
        btWork.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/work.png")));
        btWork.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_WORK"));
        btWork.setText(Translator.getTranslation("DAYVIEW.BT_WORK"));
        btWork.setToolTipText(Translator.getTranslation("DAYVIEW.BT_WORK_TOOLTIP"));
        btWork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btWorkActionPerformed(evt);
            }
        });

        pnWorkButtons.add(btWork);

        btRelax.setFont(getFont());
        btRelax.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/paint.png")));
        btRelax.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_RELAX"));
        btRelax.setText(Translator.getTranslation("DAYVIEW.BT_RELAX"));
        btRelax.setToolTipText(Translator.getTranslation("DAYVIEW.BT_RELAX_TOOLTIP"));
        btRelax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRelaxActionPerformed(evt);
            }
        });

        pnWorkButtons.add(btRelax);

        btDone.setFont(getFont());
        btDone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/done.png")));
        btDone.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_DONE"));
        btDone.setText(Translator.getTranslation("DAYVIEW.BT_DONE"));
        btDone.setToolTipText(Translator.getTranslation("DAYVIEW.BT_DONE_TOOLTIP"));
        btDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDoneActionPerformed(evt);
            }
        });

        pnWorkButtons.add(btDone);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(pnWorkButtons, gridBagConstraints);

        lblPlan.setDisplayedMnemonic(Translator.getMnemonic("DAYVIEW.LBL_PLAN"));
        lblPlan.setFont(getFont());
        lblPlan.setLabelFor(tbPlan);
        lblPlan.setText(Translator.getTranslation("DAYVIEW.LBL_PLAN"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(lblPlan, gridBagConstraints);

        spPlan.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        spPlan.setFont(getFont());
        spPlan.setPreferredSize(new java.awt.Dimension(400, 200));
        tbPlan.setModel(new DayTableModel(day));
        tbPlan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbPlanKeyReleased(evt);
            }
        });
        tbPlan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPlanMouseClicked(evt);
            }
        });

        spPlan.setViewportView(tbPlan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnDayView.add(spPlan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(pnDayView, gridBagConstraints);

        chbShowFinished.setFont(getFont());
        chbShowFinished.setMnemonic(Translator.getMnemonic("DAYVIEW.CHB_SHOWFINISHED"));
        chbShowFinished.setText(Translator.getTranslation("DAYVIEW.CHB_SHOWFINISHED"));
        chbShowFinished.setToolTipText(Translator.getTranslation("DAYVIEW.CHB_SHOWFINISHED_TOOLTIP"));
        chbShowFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbShowFinishedActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(chbShowFinished, gridBagConstraints);

        btSelect.setFont(getFont());
        btSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/select.png")));
        btSelect.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_SELECT"));
        btSelect.setText(Translator.getTranslation("DAYVIEW.BT_SELECT"));
        btSelect.setToolTipText(Translator.getTranslation("DAYVIEW.BT_SELECT_TOOLTIP"));
        btSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSelectActionPerformed(evt);
            }
        });

        pnButtons.add(btSelect);

        btAdd.setFont(getFont());
        btAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/add.png")));
        btAdd.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_ADD"));
        btAdd.setText(Translator.getTranslation("DAYVIEW.BT_ADD"));
        btAdd.setToolTipText(Translator.getTranslation("DAYVIEW.BT_ADD_TOOLTIP"));
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        pnButtons.add(btAdd);

        btEdit.setFont(getFont());
        btEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/edit.png")));
        btEdit.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_EDIT"));
        btEdit.setText(Translator.getTranslation("DAYVIEW.BT_EDIT"));
        btEdit.setToolTipText(Translator.getTranslation("DAYVIEW.BT_EDIT_TOOLTIP"));
        btEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEditActionPerformed(evt);
            }
        });

        pnButtons.add(btEdit);

        btRemove.setFont(getFont());
        btRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/delete.png")));
        btRemove.setMnemonic(Translator.getMnemonic("DAYVIEW.BT_REMOVE"));
        btRemove.setText(Translator.getTranslation("DAYVIEW.BT_REMOVE"));
        btRemove.setToolTipText(Translator.getTranslation("DAYVIEW.BT_REMOVE_TOOLTIP"));
        btRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRemoveActionPerformed(evt);
            }
        });

        pnButtons.add(btRemove);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        add(pnButtons, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void btNextWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNextWeekActionPerformed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        setDay(Plan.getDefault().getDay(calendar.getTime()));
    }//GEN-LAST:event_btNextWeekActionPerformed

    private void btNextMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNextMonthActionPerformed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        calendar.add(Calendar.MONTH, 1);
        setDay(Plan.getDefault().getDay(calendar.getTime()));
    }//GEN-LAST:event_btNextMonthActionPerformed

    private void btPreviousMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPreviousMonthActionPerformed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        calendar.add(Calendar.MONTH, -1);
        setDay(Plan.getDefault().getDay(calendar.getTime()));
    }//GEN-LAST:event_btPreviousMonthActionPerformed

    private void btPreviousWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPreviousWeekActionPerformed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        setDay(Plan.getDefault().getDay(calendar.getTime()));
    }//GEN-LAST:event_btPreviousWeekActionPerformed
    
    /** Method called when date textfield is clicked.
     * @param evt Event that invoked the action.
     */
    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        setDay(Plan.getDefault().getDay(new Date()));
    }//GEN-LAST:event_txtDateMouseClicked
    
    /** Method called when remove button is pressed.
     * @param evt Event that invoked the action.
     */
    private void btRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoveActionPerformed
        int row = tbPlan.getSelectedRow();
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        Task selectedTask = dayTableModel.getTask(row);
        String description = selectedTask.getDescription();
        String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
        int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.REMOVE_TASK", new String[] {description}), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
        if (decision == JOptionPane.YES_OPTION) {
            if (task == selectedTask)
                btDoneActionPerformed(null);
            day.removeTask(selectedTask);
            checkButtons();
        }
    }//GEN-LAST:event_btRemoveActionPerformed
    
    /** Method called when edit button is pressed.
     * @param evt Event that invoked the action.
     */
    private void btEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditActionPerformed
        int row = tbPlan.getSelectedRow();
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        Task selectedTask = dayTableModel.getTask(row);
        boolean readOnly = btEdit.getText().equals(Translator.getTranslation("DAYVIEW.BT_VIEW"));
        TaskDialog dialog = new TaskDialog(selectedTask, day, readOnly);
        dialog.addPropertyChangeListener(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_btEditActionPerformed
    
    /** Method called when add button is pressed.
     * @param evt Event that invoked the action.
     */
    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        new TaskDialog(day).setVisible(true);
    }//GEN-LAST:event_btAddActionPerformed
    
    /** Method called when select button is pressed.
     * @param evt Event that invoked the action.
     */
    private void btSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectActionPerformed
        int row = tbPlan.getSelectedRow();
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        Task selectedTask = dayTableModel.getTask(row);
        Boolean checkPriorities = (Boolean) Settings.getDefault().getSetting("checkPriority");
        if (checkPriorities.booleanValue() & day.existsMorePriorityTask(selectedTask.getPriority())) {
            String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
            int ignorePriority = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.IGNORE_PRIORITY"), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
            if (ignorePriority != JOptionPane.YES_OPTION) return;
        }
        setTask(selectedTask, false);
        checkButtons();
    }//GEN-LAST:event_btSelectActionPerformed
    
    /** Method called when visibility of finished tasks should be changed.
     * @param evt Event that invoked the action.
     */
    private void chbShowFinishedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbShowFinishedActionPerformed
        Settings.getDefault().setSetting("displayFinishedTasks", new Boolean(chbShowFinished.isSelected()));
        updateInformation(false);
    }//GEN-LAST:event_chbShowFinishedActionPerformed
    
    /** Method called when next button is pressed.
     * @param evt Event that invoked the action.
     */
    private void btNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNextActionPerformed
        setDay(Plan.getDefault().getDayAfter(day));
    }//GEN-LAST:event_btNextActionPerformed
    
    /** Method called when previous button is pressed.
     * @param evt Event that invoked the action.
     */
    private void btPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPreviousActionPerformed
        setDay(Plan.getDefault().getDayBefore(day));
    }//GEN-LAST:event_btPreviousActionPerformed
    
    /** Method called when any key is released while table of planned tasks has focus.
     * @param evt Event that invoked this method call.
     */
    private void tbPlanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPlanKeyReleased
        checkButtons();
    }//GEN-LAST:event_tbPlanKeyReleased
    
    /** Method called when Done button is pressed.
     * @param evt Event that invoked this action.
     */
    private void btDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDoneActionPerformed
        task.removePropertyChangeListener(this);
        task.workDone();
        Settings.getDefault().setSetting("runningTask", null);
        Settings.saveSettings();
        txtTask.setText("");
        double dayWorkHours = Double.parseDouble((String) Settings.getDefault().getSetting("dayWorkHours"));
        double totalTime = (double) day.getTotalTime()/(60 * 60 * 1000);
        int progress = (int) (totalTime * 100 / dayWorkHours);
        pbProgress.setValue(progress);
        pbProgress.setString(Tools.getTime(day.getTotalTime()));
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        dayTableModel.fireTableDataChanged();
        firePropertyChange("task_done", task, null);
        task = null;
        checkButtons();
    }//GEN-LAST:event_btDoneActionPerformed
    
    /** Method called when Relax button is pressed.
     * @param evt Event that invoked this action.
     */
    private void btRelaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRelaxActionPerformed
        task.suspendWork();
        Settings.getDefault().setSetting("runningTask", null);
        Settings.saveSettings();
        task.removePropertyChangeListener(this);
        firePropertyChange("task_suspended", task, null);
        checkButtons();
    }//GEN-LAST:event_btRelaxActionPerformed
    
    /** Method called when Work button is pressed.
     * @param evt Event that invoked this action.
     */
    private void btWorkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btWorkActionPerformed
        task.startWork();
        task.addPropertyChangeListener(this);
        firePropertyChange("task_resumed", null, task);
        checkButtons();
    }//GEN-LAST:event_btWorkActionPerformed
    
    /** Method called when user clicks to table with tasks plan.
     * @param evt Event that invoked this action.
     */
    private void tbPlanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPlanMouseClicked
        checkButtons();
        firePropertyChange("plan_clicked", null, null);
        if (selectButtonEnabled) { // Selected task can be started
            Date now = new Date();
            long delay = 1000;
            delay = now.getTime() - clickedWhen.getTime();
            boolean samePoint = clickedWhere.equals(evt.getPoint());
            if (samePoint & (delay < 250)) {
                int row = tbPlan.getSelectedRow();
                DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
                Task selectedTask = dayTableModel.getTask(row);
                Boolean checkPriorities = (Boolean) Settings.getDefault().getSetting("checkPriority");
                if (checkPriorities.booleanValue() & day.existsMorePriorityTask(selectedTask.getPriority())) {
                    String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
                    int ignorePriority = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.IGNORE_PRIORITY"), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
                    if (ignorePriority != JOptionPane.YES_OPTION) return;
                }
                setTask(selectedTask, false);
                checkButtons();
                btWorkActionPerformed(null);
            } else {
                clickedWhere = evt.getPoint();
                clickedWhen = now;
            }
        }
    }//GEN-LAST:event_tbPlanMouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btDone;
    private javax.swing.JButton btEdit;
    private javax.swing.JButton btNext;
    private javax.swing.JButton btNextMonth;
    private javax.swing.JButton btNextWeek;
    private javax.swing.JButton btPrevious;
    private javax.swing.JButton btPreviousMonth;
    private javax.swing.JButton btPreviousWeek;
    private javax.swing.JButton btRelax;
    private javax.swing.JButton btRemove;
    private javax.swing.JButton btSelect;
    private javax.swing.JButton btWork;
    private javax.swing.JCheckBox chbShowFinished;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblEnd;
    private javax.swing.JLabel lblPlan;
    private javax.swing.JLabel lblProgress;
    private javax.swing.JLabel lblStart;
    private javax.swing.JLabel lblTask;
    private javax.swing.JLabel lblWeek;
    private javax.swing.JProgressBar pbProgress;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JPanel pnDayView;
    private javax.swing.JPanel pnWorkButtons;
    private javax.swing.JScrollPane spPlan;
    private javax.swing.JTable tbPlan;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtEnd;
    private javax.swing.JTextField txtStart;
    private javax.swing.JTextField txtTask;
    private javax.swing.JTextField txtWeek;
    // End of variables declaration//GEN-END:variables
    
    /** Day that is actually being displayed. */
    private Day day = null;
    /** Task that is currently selected and may be started. */
    private Task task = null;
    /** Point in the plan table when user clicked last time. */
    private Point clickedWhere = new Point();
    /** Time when user clicked in the plan table last time. */
    private Date clickedWhen = new Date();
    /** Whether Select button is currently enabled or not. */
    private boolean selectButtonEnabled = false;
    /** Whether user already confirmed warning about exceeded working hours or not. */
    private boolean warningConfirmed = false;
    
    /** Method called when some information needs to be updated. This means that
     * start and finish times will be updated or information about selected task.
     * @param taskDurationChanged If true, only task row will be updated and working
     * hours will be checked. Otherwise whole table gets updated.
     */
    private void updateInformation(boolean taskDurationChanged) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        String dayFormat = Translator.getTranslation("FORMAT.DATE");
        sdf.applyPattern(dayFormat);
        txtDate.setText(sdf.format(day.getDate()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        txtWeek.setText("" + calendar.get(Calendar.WEEK_OF_YEAR) + ".");
        Date startTime = day.getStartTime();
        if (startTime != null) {
            txtStart.setText(Tools.getTime(startTime));
            txtEnd.setText(Tools.getTime(day.getFinishTime()));
            double dayWorkHours = Double.parseDouble((String) Settings.getDefault().getSetting("dayWorkHours"));
            double totalTime = (double) day.getTotalTime()/(60 * 60 * 1000);
            int progress = (int) (totalTime * 100 / dayWorkHours);
            pbProgress.setValue(progress);
            pbProgress.setStringPainted(true);
            pbProgress.setString(Tools.getTime(day.getTotalTime()));
        } else {
            txtStart.setText("");
            txtEnd.setText("");
            pbProgress.setValue(0);
            pbProgress.setStringPainted(false);
        }
        int row = tbPlan.getSelectedRow();
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        if (task != null)
            txtTask.setText(task.getDescription());
        boolean today = Plan.getDefault().isToday(day);
        if (taskDurationChanged) {
            if (today) {
                row = dayTableModel.getRow(task);
                if (row != -1) dayTableModel.fireTableRowsUpdated(row, row);
            }
            boolean warnHoursExceeded = ((Boolean) Settings.getDefault().getSetting("warnHoursExceeded")).booleanValue();
            if (warnHoursExceeded && !warningConfirmed) {
                double dayWorkHours = Double.parseDouble((String) Settings.getDefault().getSetting("dayWorkHours"));
                double totalTime = (double) Plan.getDefault().getDay(new Date()).getTotalTime()/(60 * 60 * 1000);
                if (totalTime > dayWorkHours) {
                    warningConfirmed = true;
                    new Thread() {
                        public void run() {
                            JOptionPane.showMessageDialog(null, Translator.getTranslation("WARNING.HOURS_EXCEEDED"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
                        }
                    }.start();
                }
            }
        } else dayTableModel.fireTableDataChanged();
        firePropertyChange("view_updated", null, null);
    }
    
    /** Check availability of buttons according to current state of view.
     */
    private void checkButtons() {
        boolean today = Plan.getDefault().isToday(day);
        boolean futureDay = Plan.getDefault().isFuture(day) | today;
        int row = tbPlan.getSelectedRow();
        boolean taskSelected = row != -1;
        boolean taskFinished = false;
        boolean taskAlreadySelected = false;
        if (taskSelected) {
            DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
            Task selectedTask = dayTableModel.getTask(row);
            taskFinished = selectedTask.getState() == Task.STATE_DONE;
            taskAlreadySelected = task == selectedTask;
        }
        btSelect.setEnabled(today & taskSelected & !taskAlreadySelected & !taskFinished);
        selectButtonEnabled = btSelect.isEnabled();
        btAdd.setEnabled(futureDay);
        btRemove.setEnabled(futureDay & taskSelected);
        btEdit.setEnabled(taskSelected);
        btEdit.setText(Translator.getTranslation("DAYVIEW.BT_VIEW"));
        btEdit.setToolTipText(Translator.getTranslation("DAYVIEW.BT_VIEW_TOOLTIP"));
        if (futureDay & taskSelected & !taskFinished) {
            btEdit.setText(Translator.getTranslation("DAYVIEW.BT_EDIT"));
            btEdit.setToolTipText(Translator.getTranslation("DAYVIEW.BT_EDIT_TOOLTIP"));
        }
        taskSelected = task != null;
        boolean taskRunning = taskSelected && task.isRunning();
        taskFinished = taskSelected && task.getState() == Task.STATE_DONE;
        btWork.setEnabled(taskSelected & !taskFinished & !taskRunning);
        btRelax.setEnabled(taskSelected & !taskFinished & taskRunning);
        btDone.setEnabled(taskSelected & !taskFinished);
    }
    
    /** Method called when switch date action is required.
     */
    public void switchDate() {
        DateDialog dateDialog = new DateDialog(day.getDate());
        dateDialog.addPropertyChangeListener(this);
        dateDialog.setVisible(true);
    }
    
    /** Method called when move time action is required.
     */
    public void moveTime() {
        if (!Plan.getDefault().isToday(day)) {
            JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.ONLY_TODAY"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = tbPlan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.NO_TASK_SELECTED"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        Task selectedTask = dayTableModel.getTask(row);
        if (selectedTask.getDuration() == 0) {
            JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.NO_TIME"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedTask.getState() == Task.STATE_DONE) {
            JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.TASK_DONE"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean existsTargetTask = false;
        Iterator iterator = day.getTasks().iterator();
        while (iterator.hasNext()) {
            Task anyTask = (Task) iterator.next();
            if ((anyTask.getState() != Task.STATE_DONE) && (anyTask != selectedTask)) existsTargetTask = true;
        }
        if (!existsTargetTask) {
            JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.NO_TARGET_TASK"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        MoveTimeDialog moveTimeDialog = new MoveTimeDialog(selectedTask);
        moveTimeDialog.addPropertyChangeListener(this);
        moveTimeDialog.setVisible(true);
    }
    
    /** Method called when copy task action is required.
     */
    public void copyTask() {
        int row = tbPlan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, Translator.getTranslation("WARNING.NO_TASK_SELECTED"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        Task selectedTask = dayTableModel.getTask(row);
        Plan plan = Plan.getDefault();
        boolean futureDay = plan.isFuture(plan.getDayAfter(day));
        String bundleKey = futureDay ? "QUESTION.MOVE_TASK_NEXT" : "QUESTION.MOVE_TASK_TODAY";
        String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
        String question = Translator.getTranslation(bundleKey, new String[] {selectedTask.getDescription()});
        int decision = JOptionPane.showOptionDialog(this, question, Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if (decision == JOptionPane.YES_OPTION) {
            Day targetDay = futureDay ? plan.getDayAfter(day) : plan.getDay(new Date());
            if (targetDay.getTask(selectedTask.getDescription()) == null) {
                targetDay.addTask(selectedTask.cloneTask());
                plan.addDay(targetDay);
            }
            if ((futureDay) && (selectedTask.getState() == Task.STATE_NEW))
                day.removeTask(selectedTask);
        }
    }
    
    /** Set whether finished tasks should be displayed or not.
     * @param visibility Should be finished tasks displayed or not ?
     */
    public void setFinishedTasksVisibility(boolean visibility) {
        Settings.getDefault().setSetting("displayFinishedTasks", new Boolean(visibility));
        updateInformation(false);
    }
    
    /** Get suffix to be appended to title of application.
     * @return Title suffix: name of current task and/or total time.
     */
    public String getTitleSuffix() {
        String suffix = "-";
        if ((task != null) && (task.isRunning()))
            suffix = suffix + " " + task.getDescription();
        Day today = Plan.getDefault().getDay(new Date());
        suffix = suffix + " [" + Tools.getTime(today.getTotalTime()) + "]";
        return suffix;
    }
    
    /** Tries to load restarting task and run it.
     **/
    private void loadRunningTask() {
        String runningTask = (String) Settings.getDefault().getSetting("runningTask");
        if ((runningTask == null) || runningTask.equals("null")) return;
        int index = runningTask.indexOf("[");
        String description = runningTask.substring(0, index);
        Task task = Plan.getDefault().getDay(new Date()).getTask(description);
        if (task == null) {
            System.out.println("Error: Task to which restart time should be added does not exist in plan.");
            Settings.getDefault().setSetting("runningTask", null);
            Settings.saveSettings();
            return;
        }
        long restartTime;
        try { restartTime = Long.parseLong(runningTask.substring(index + 1, runningTask.length() - 1)); } catch (NumberFormatException e) {
            System.out.println("Error: Restart time stamp of Rachota was not successfully read.");
            Settings.getDefault().setSetting("runningTask", null);
            Settings.saveSettings();
            return;
        }
        long duration = new Date().getTime() - restartTime;
        String time = Tools.getTime(duration);
        String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
        int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.ADD_RUNNING_TASK", new String[] {time, description}), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if (decision != JOptionPane.YES_OPTION) {
            Settings.getDefault().setSetting("runningTask", null);
            Settings.saveSettings();
            return;
        }
        task.addDuration(duration);
        setTask(task, true);
    }
    
    /** Method called when one clock tick is over.
     */
    public void tick() {
        /* Day today = Plan.getDefault().getDay(new Date());
        if ((today != day) && !requiredDay) {
            if (task != null) btRelaxActionPerformed(null);
            if (day.isModified()) Plan.getDefault().addDay(day);
            ChangeHandler.getDefault().removeChangeEventListener(this, day);
            day = today;
            Plan.getDefault().addRegularTasks(day);
            Boolean moveUnfinishedTasks = (Boolean) Settings.getDefault().getSetting("moveUnfinished");
            if (moveUnfinishedTasks) Plan.getDefault().copyUnfinishedTasks();
            ChangeHandler.getDefault().addChangeEventListener(this, day);
            DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
            dayTableModel.setDay(day);
            dayTableModel.fireTableDataChanged();
            updateInformation(false);
            requiredDay = true;
        } */
        Iterator iterator = day.getTasks().iterator();
        while (iterator.hasNext()) {
            final Task task = (Task) iterator.next();
            if (task.getState() == Task.STATE_DONE) continue;
            if ((task == this.task) && (task.isRunning())) continue;
            Date notificationTime = task.getNotificationTime();
            if (notificationTime == null) continue;
            String notification = Tools.getTime(notificationTime);
            String now = Tools.getTime(new Date());
            if (notification.equals(now)) { // Dvakrat upozorneni na tu samou ulohu nefunguje
                task.setNotificationTime(null);
                final String description = task.getDescription();
                if (task.automaticStart()) {
                    new Thread() {
                        public void run() {
                            setTask(task, true);
                            Tools.beep(Tools.BEEP_NOTIFICATION);
                            JOptionPane.showMessageDialog(null, Translator.getTranslation("INFORMATION.AUTOMATIC_START", new String[] {description}), Translator.getTranslation("INFORMATION.INFORMATION_TITLE"), JOptionPane.INFORMATION_MESSAGE);
                        }
                    }.start();
                    return;
                }
                new Thread() {
                    public void run() {
                        Tools.beep(Tools.BEEP_WARNING);
                        String message = Translator.getTranslation("QUESTION.SWITCH_TASK", new String[] {description});
                        String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
                        int decision = JOptionPane.showOptionDialog(null, message, Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
                        if (decision == JOptionPane.YES_OPTION)
                            setTask(task, true);
                    }
                }.start();
            }
        }
    }
    
    /** Set description of selected task and start it if required.
     * @param task Task to be selected.
     * @param startTask Whether the task should be started.
     */
    private void setTask(Task task, boolean startTask) {
        if ((this.task != null) && (this.task.isRunning())) btRelaxActionPerformed(null);
        this.task = task;
        txtTask.setText(task.getDescription());
        updateInformation(false);
        if (startTask) btWorkActionPerformed(null);
    }
    
    /** Sets day view to given day.
     * @param newDay New day to be displayed by day view.
     */
    private void setDay(Day newDay) {
        if (day.isModified()) Plan.getDefault().addDay(day);
        day.removePropertyChangeListener(this);
        day = newDay;
        Plan.getDefault().addRegularTasks(day);
        day.addPropertyChangeListener(this);
        DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
        dayTableModel.setDay(day);
        updateInformation(false);
        // requiredDay = (Plan.getDefault().getDay(new Date()) != day);
        checkButtons();
    }
    
    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        boolean taskDurationChanged = false;
        if (evt.getPropertyName().startsWith("task_")) {
            TaskDialog taskDialog = (TaskDialog) evt.getSource();
            taskDialog.removePropertyChangeListener(this);
            checkButtons();
        }
        if (evt.getPropertyName().equals("day")) {
            Day day = (Day) evt.getNewValue();
            setDay(day);
            firePropertyChange("day", null, day);
        }
        if (evt.getPropertyName().equals("date_selected")) {
            Date date = (Date) evt.getNewValue();
            setDay(Plan.getDefault().getDay(date));
        }
        if (evt.getPropertyName().equals("settings")) Plan.getDefault().addRegularTasks(day);
        if (evt.getPropertyName().equals("duration")) taskDurationChanged = true;
        else if (evt.getPropertyName().equals("tasks")) {
            Plan.getDefault().addDay(day);
            DayTableModel dayTableModel = (DayTableModel) tbPlan.getModel();
            dayTableModel.resortRows();
        }
        updateInformation(taskDurationChanged);
    }
}
