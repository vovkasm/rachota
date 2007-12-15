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
 * Created on September 27, 2005  9:04 PM
 * PieChart.java
 */

package org.cesilko.rachota.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/** Pie chart showing a circle with a highlighted arc representing preset share.
 * @author Jiri Kovalsky
 */
public class PieChart extends JPanel implements PropertyChangeListener {

    /** Percentage that should be highlighted. */
    private float share;

    /** Creates a new pie chart.
     * @param share Percentage that should be highlighted.
     */
    public PieChart(float share) {
        super();
        setShare(share);
    }
    
    /** Sets share that should be highlighted in the pie chart.
     * @param share Percentage that should be highlighted in the pie chart.
     */
    public void setShare(float share) {
        this.share = share;
        repaint();
    }
    
    /** Draws the pie chart given set share.
     * @param graphics Graphics area where the chart should be drawn.
     */
    public void paint(Graphics graphics) {
        int width = getWidth();
        int height = getHeight();
        graphics.clearRect(0, 0, width, height);
        if (share == 0) return;
        int min = Math.min(width, height);
        int dia = (int) (min - min/4)/2;
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillOval(width/2 - dia, height/2 - dia*3/4, dia*2, dia*2);
        graphics.setColor(Color.CYAN);
        graphics.fillArc(width/2 - dia, height/2 - dia*3/4, dia*2, dia*2, 90, (int) (-3.6*share));
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawOval(width/2 - dia, height/2 - dia*3/4, dia*2, dia*2);
        int shareWidth = graphics.getFontMetrics().stringWidth("" + share + "%");
        int x = width/2 - (shareWidth + 15)/2;
        int y = (height - dia)/6 - 5;
        graphics.setColor(Color.CYAN);
        graphics.fillRect(x, y, 10, 10);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(x, y, 10, 10);
        graphics.drawString("" + share + "%", x + 15, y + 10);
    }
    
    
    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }
}
