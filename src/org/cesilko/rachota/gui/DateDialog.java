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
 * Created on 18. srpen 2006  22:14
 * DateDialog.java
 */

package org.cesilko.rachota.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.SpinnerDateModel;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.Translator;

/** Dialog for user friendly selecting date to be displayed in Day View.
 * @author Jiri Kovalsky
 */
public class DateDialog extends javax.swing.JDialog {
    
    /** Identifies dialog for switching current day view. */
    public static final int TYPE_SWITCH_DATE = 0;
    /** Identifies dialog for copying task to different day. */
    public static final int TYPE_COPY_TASK = 1;
    /** Type of dialog. */
    private int type;
    
    /** Creates new form DateDialog */
    public DateDialog(java.awt.Frame parent, Date date, int type) {
        initComponents();
        this.type = type;
        switch (type) {
            case TYPE_SWITCH_DATE:
                lbSelectDate.setText(Translator.getTranslation("DATEDIALOG.SELECT_DATE"));
                break;
            default:
                lbSelectDate.setText(Translator.getTranslation("DATEDIALOG.COPY_TASK"));
        }
        setLocationRelativeTo(parent);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMMMMMMMMMMMMMM");
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH, i);
            cmbMonth.addItem(sdf.format(calendar.getTime()));
        }
        calendar.setTime(date);
        spYear.setValue(date);
        spYear.setEditor(new javax.swing.JSpinner.DateEditor(spYear, "yyyy"));
        cmbMonth.setSelectedIndex(calendar.get(Calendar.MONTH));
        this.date = date;
        updateDays();
        pack();
    }

    private void updateDays() {
        pnDays.removeAll();
        String dateFormat = Translator.getTranslation("FORMAT.DATE");
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);
        int numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Font font = getFont();
        JLabel specimen = new JLabel("31");
        specimen.setFont(font);
        specimen.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Dimension size = specimen.getPreferredSize();
        size.setSize(size.getWidth() + 2, size.getHeight() + 2);
        for (int i = 0; i < numberOfDays; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i+1);
            JLabel day = new JLabel(new Integer(i+1).toString());
            day.setPreferredSize(size);
            day.setHorizontalAlignment(JLabel.CENTER);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if ((dayOfWeek == Calendar.SATURDAY) | (dayOfWeek == Calendar.SUNDAY)) day.setFont(font.deriveFont(Font.BOLD));
            else day.setFont(font);
            if (dayNumber == i+1) {
                day.setForeground(Color.BLUE);
                day.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                day.setFont(font.deriveFont(Font.BOLD));
            }
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints.gridx = i % 7;
            gridBagConstraints.gridy = i / 7;
            day.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    lbDayMouseClicked(evt);
                }
            });
            pnDays.add(day, gridBagConstraints);
        }
        previousYear = (Date) spYear.getValue();
        repaint();
        pack();
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
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbSelectDate = new javax.swing.JLabel();
        cmbMonth = new javax.swing.JComboBox();
        spYear = new javax.swing.JSpinner();
        pnDays = new javax.swing.JPanel();
        txtDate = new javax.swing.JTextField();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Translator.getTranslation("DATEDIALOG.TITLE"));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lbSelectDate.setFont(getFont());
        lbSelectDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbSelectDate.setLabelFor(cmbMonth);
        lbSelectDate.setText(Translator.getTranslation("DATEDIALOG.SELECT_DATE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lbSelectDate, gridBagConstraints);

        cmbMonth.setFont(getFont());
        cmbMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMonthActionPerformed(evt);
            }
        });
        cmbMonth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMonthKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cmbMonth, gridBagConstraints);

        spYear.setFont(getFont());
        spYear.setModel(new SpinnerDateModel());
        spYear.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spYearStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(spYear, gridBagConstraints);

        pnDays.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnDays.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pnDaysKeyPressed(evt);
            }
        });
        pnDays.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(pnDays, gridBagConstraints);

        txtDate.setEditable(false);
        txtDate.setFont(getFont());
        txtDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDate.setToolTipText(Translator.getTranslation("DATEDIALOG.TXT_DATE_TOOLTIP"));
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(txtDate, gridBagConstraints);

        btOK.setFont(getFont());
        btOK.setMnemonic(Translator.getMnemonic("DATEDIALOG.BT_OK"));
        btOK.setText(Translator.getTranslation("DATEDIALOG.BT_OK"));
        btOK.setToolTipText(Translator.getTranslation("DATEDIALOG.BT_OK_TOOLTIP"));
        btOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOKActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btOK, gridBagConstraints);

        btCancel.setFont(getFont());
        btCancel.setMnemonic(Translator.getMnemonic("DATEDIALOG.BT_CANCEL"));
        btCancel.setText(Translator.getTranslation("DATEDIALOG.BT_CANCEL"));
        btCancel.setToolTipText(Translator.getTranslation("DATEDIALOG.BT_CANCEL_TOOLTIP"));
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btCancel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void pnDaysKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnDaysKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_pnDaysKeyPressed

private void cmbMonthKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMonthKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btOKActionPerformed(null);
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        btCancelActionPerformed(null);
}//GEN-LAST:event_cmbMonthKeyPressed

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        spYear.setValue(new Date());
        cmbMonth.setSelectedIndex(calendar.get(Calendar.MONTH));
        date = new Date();
        updateDays();
    }//GEN-LAST:event_txtDateMouseClicked

    private void lbDayMouseClicked(java.awt.event.MouseEvent evt) {
        JLabel selectedDay = (JLabel) evt.getComponent();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDay.getText()));
        if (type==TYPE_COPY_TASK) {
            Plan plan = Plan.getDefault();
            Day day = plan.getDay(calendar.getTime());
            boolean today = plan.isToday(day);
            boolean future = plan.isFuture(day);
            if (!(today | future)) return;
        }
        date = calendar.getTime();
        updateDays();
    }

    private void spYearStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spYearStateChanged
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) spYear.getValue());
        int year = calendar.get(Calendar.YEAR);
        if (year < 1970) spYear.setValue(previousYear);
        else {
            calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, cmbMonth.getSelectedIndex());
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
            updateDays();
        }
    }//GEN-LAST:event_spYearStateChanged

    private void cmbMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMonthActionPerformed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) spYear.getValue());
        calendar.set(Calendar.MONTH, cmbMonth.getSelectedIndex());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        updateDays();
    }//GEN-LAST:event_cmbMonthActionPerformed

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btCancelActionPerformed

    private void btOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOKActionPerformed
        switch (type) {
            case TYPE_SWITCH_DATE:
                firePropertyChange("date_selected_switch", null, date);
                break;
            default:
                firePropertyChange("date_selected_copy_task", null, date);
        }
        setVisible(false);
    }//GEN-LAST:event_btOKActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOK;
    private javax.swing.JComboBox cmbMonth;
    private javax.swing.JLabel lbSelectDate;
    private javax.swing.JPanel pnDays;
    private javax.swing.JSpinner spYear;
    private javax.swing.JTextField txtDate;
    // End of variables declaration//GEN-END:variables
    private Date date = new Date();
    private Date previousYear = new Date();
}
