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
import java.util.Vector;
import javax.swing.JPanel;
import org.cesilko.rachota.core.Translator;

/** Pie chart showing a circle with a highlighted arc representing preset share.
 * @author Jiri Kovalsky
 */
public class PieChart extends JPanel implements PropertyChangeListener {

    /** Names of portions that should be highlighted. */
    private Vector names = new Vector();
    /** Shares of portions that should be highlighted. */
    private Vector shares = new Vector();

    /** Creates a new pie chart. */
    public PieChart() {
        super();
    }
    
    /** Draws the pie chart given set share.
     * @param graphics Graphics area where the chart should be drawn.
     */
    public void paint(Graphics graphics) {
        int width = getWidth();
        int height = getHeight();
        graphics.clearRect(0, 0, width, height);
        int count = names.size();
        if (count == 0) return;
        int min = Math.min(width - 15, height - names.size()*15 - 5);
        int dia = (int) (min - min/4)/2;
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillOval(width/2 - dia, 20 + count*15, dia*2, dia*2);
        Color color = Color.CYAN;
        float previousAngle = 0;
        for (int i = 0; i < count; i++) {
            String name = (String) names.get(i);
            Float share = (Float) shares.get(i);
            graphics.setColor(color);
            graphics.fillArc(getWidth()/2 - dia, 20 + count*15, dia*2, dia*2, 90 - (int) (3.6*previousAngle), (int) (-3.6*share));
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawOval(width/2 - dia, 20 + count*15, dia*2, dia*2);
            int y = 5 + i*15;
            graphics.setColor(color);
            graphics.fillRect(5, y, 10, 10);
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawRect(5, y, 10, 10);
            graphics.drawString(name + " " + share + "%", 20, y + 10);
            color = changeColor(color);
            previousAngle = share.floatValue() + previousAngle;
        }
        int x = 5;
        int y = 5 + count*15;
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(x, y, 10, 10);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(x, y, 10, 10);
        graphics.drawString(Translator.getTranslation("HISTORYVIEW.PROJECTS_OTHERS"), x + 15, y + 10);
    }
    
    
    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    /** Sets names and shares that should be highlighted in the pie chart.
     * @param names Descriptions of portions to be highlighted in the pie chart.
     * @param shares Percentages of portions to be highlighted in the pie chart.
     */
    public void setShares(Vector names, Vector shares) {
        this.names = names;
        this.shares = shares;
        repaint();
    }

    /** Returns new color visibly different from the given one.
     * @param color Color to be derivated.
     * @return New color with R+50, G-50 and B+100;
     */
    private Color changeColor(Color color) {
        int red = color.getRed() + 50;
        if (red > 255) red = red - 255;
        int green = color.getGreen() - 50;
        if (green < 0) green = green + 255;
        int blue = color.getBlue() + 100;
        if (blue > 255) blue = blue - 255;
        return new Color(red, green, blue);
    }
}