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
 * Portions created by Jiri Kovalsky are Copyright (C) 2007
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on 05 October 2007  20:35
 * ComparisonChart.java
 */
package org.cesilko.rachota.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import org.cesilko.rachota.core.Translator;

/** Chart showing comparison of current user's usage times with
 * other Rachota users.
 * @author Jiri Kovalsky
 */
public class ComparisonChart extends JPanel {

    /** Total working time of current user. */
    private long totalTimeUser;
    /** Total working time of all users. */
    private long totalTimeAll;
    /** Total idle time of current user. */
    private long idleTimeUser;
    /** Total idle time of all users. */
    private long idleTimeAll;
    /** Total private time of current user. */
    private long privateTimeUser;
    /** Total private time of all users. */
    private long privateTimeAll;
    /** Error message to be displayed instead of chart in case of problems. */
    private String message = "";
    /** Hint message to help user understand/resolve possible problems. */
    private String hint = "";
    /** Top inset */
    private static int INSET_TOP = 10;
    /** Bottom inset */
    private static int INSET_BOTTOM = 15;
    /** Left inset */
    private static int INSET_LEFT = 10;
    /** Right inset */
    private static int INSET_RIGHT = 10;

    /** Creates a new comparison chart. */
    public ComparisonChart() {
        super();
        setTimes(0, 0, 0, 0, 0, 0);
    }

    /** Draws the pie chart given set share.
     * @param graphics Graphics area where the chart should be drawn.
     */
    public void paint(Graphics graphics) {
        int width = getBounds().width;
        int height = getBounds().height;
        graphics.clearRect(0, 0, width, height);
        graphics.setColor(Color.RED);
        int positionY = height/3;
        if (width < graphics.getFontMetrics().stringWidth(message)) {
            int index = message.substring(message.length()/2).indexOf(" ") + message.length()/2;
            String partOne = message.substring(0, index);
            String partTwo = message.substring(index);
            graphics.drawString(partOne, width/2 - graphics.getFontMetrics().stringWidth(partOne)/2, positionY);
            positionY = positionY + 15;
            graphics.drawString(partTwo, width/2 - graphics.getFontMetrics().stringWidth(partTwo)/2, positionY);
        } else graphics.drawString(message, width/2 - graphics.getFontMetrics().stringWidth(message)/2, positionY);
        positionY = positionY + 15;
        graphics.setColor(Color.BLACK);
        if (width < graphics.getFontMetrics().stringWidth(hint)) {
            int index = hint.substring(hint.length()/2).indexOf(" ") + hint.length()/2;
            String partOne = hint.substring(0, index);
            String partTwo = hint.substring(index);
            graphics.drawString(partOne, width/2 - graphics.getFontMetrics().stringWidth(partOne)/2, positionY);
            positionY = positionY + 15;
            graphics.drawString(partTwo, width/2 - graphics.getFontMetrics().stringWidth(partTwo)/2, positionY);
        } else graphics.drawString(hint, width/2 - graphics.getFontMetrics().stringWidth(hint)/2, positionY);
        long times = totalTimeAll + totalTimeUser + privateTimeAll + privateTimeUser + idleTimeAll + idleTimeUser;
        if (times == 0) return;
        graphics.clearRect(0, 0, width, height);
        long maxTimeValue = Math.max(totalTimeUser, Math.max(totalTimeAll, Math.max(privateTimeUser, Math.max(privateTimeAll, Math.max(idleTimeUser, idleTimeAll)))));
        int columnWidth = (width - INSET_RIGHT - INSET_LEFT - width / 6) / 6;
        String you = Translator.getTranslation("ANALYTICSVIEW.YOU");
        String others = Translator.getTranslation("ANALYTICSVIEW.OTHERS");

        // Drawing total times columns
        int columnHeight = (int) ((height - INSET_BOTTOM - INSET_TOP - 15) * totalTimeUser / maxTimeValue);
        drawColumn(INSET_LEFT, height - INSET_BOTTOM - columnHeight, columnWidth, columnHeight, Color.LIGHT_GRAY, you, Tools.getTimeShort(totalTimeUser), graphics);
        columnHeight = (int) ((height - INSET_BOTTOM - INSET_TOP - 15) * totalTimeAll / maxTimeValue);
        drawColumn(INSET_LEFT + columnWidth + 2, height - INSET_BOTTOM - columnHeight, columnWidth, columnHeight, Color.GRAY, others, Tools.getTimeShort(totalTimeAll), graphics);

        // Drawing idle times columns
        columnHeight = (int) ((height - INSET_BOTTOM - INSET_TOP - 15) * idleTimeUser / maxTimeValue);
        drawColumn(INSET_LEFT + columnWidth * 2 + width / 12, height - INSET_BOTTOM - columnHeight, columnWidth, columnHeight, Color.CYAN, you, Tools.getTimeShort(idleTimeUser), graphics);
        columnHeight = (int) ((height - INSET_BOTTOM - INSET_TOP - 15) * idleTimeAll / maxTimeValue);
        drawColumn(INSET_LEFT + columnWidth * 3 + 2 + width / 12, height - INSET_BOTTOM - columnHeight, columnWidth, columnHeight, Color.GRAY, others, Tools.getTimeShort(idleTimeAll), graphics);

        // Drawing private times columns
        columnHeight = (int) ((height - INSET_BOTTOM - INSET_TOP - 15) * privateTimeUser / maxTimeValue);
        drawColumn(INSET_LEFT + columnWidth * 4 + width / 6, height - INSET_BOTTOM - columnHeight, columnWidth, columnHeight, Color.BLUE, you, Tools.getTimeShort(privateTimeUser), graphics);
        columnHeight = (int) ((height - INSET_BOTTOM - INSET_TOP - 15) * privateTimeAll / maxTimeValue);
        drawColumn(INSET_LEFT + columnWidth * 5 + 2 + width / 6, height - INSET_BOTTOM - columnHeight, columnWidth, columnHeight, Color.GRAY, others, Tools.getTimeShort(privateTimeAll), graphics);

        graphics.setColor(Color.BLACK);
        String working_Time = Translator.getTranslation("HISTORYCHART.WORKING_TIME");
        String private_Time = Translator.getTranslation("HISTORYCHART.PRIVATE_TIME");
        String idle_Time = Translator.getTranslation("HISTORYCHART.IDLE_TIME");
        int correction = graphics.getFontMetrics().stringWidth(working_Time);
        int maxCorrection = correction;
        correction = graphics.getFontMetrics().stringWidth(private_Time);
        if (correction > maxCorrection)
            maxCorrection = correction;
        correction = graphics.getFontMetrics().stringWidth(idle_Time);
        if (correction > maxCorrection)
            maxCorrection = correction;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(width - maxCorrection - 25, 5, maxCorrection + 20, 50);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(width - maxCorrection - 25, 5, maxCorrection + 20, 50);
        graphics.drawString(working_Time, width - maxCorrection - 6, 20);
        graphics.drawString(idle_Time, width - maxCorrection - 6, 35);
        graphics.drawString(private_Time, width - maxCorrection - 6, 50);
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(width - 20 - maxCorrection, 10, 10, 10);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(width - 20 - maxCorrection, 10, 10, 10);
        graphics.setColor(Color.CYAN);
        graphics.fillRect(width - 20 - maxCorrection, 25, 10, 10);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(width - 20 - maxCorrection, 25, 10, 10);
        graphics.setColor(Color.BLUE);
        graphics.fillRect(width - 20 - maxCorrection, 40, 10, 10);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(width - 20 - maxCorrection, 40, 10, 10);
    }

    public void setTimes(long totalTimeUser, long totalTimeAll, long idleTimeUser, long idleTimeAll, long privateTimeUser, long privateTimeAll) {
        this.totalTimeAll = totalTimeAll;
        this.totalTimeUser = totalTimeUser;
        this.idleTimeAll = idleTimeAll;
        this.idleTimeUser = idleTimeUser;
        this.privateTimeAll = privateTimeAll;
        this.privateTimeUser = privateTimeUser;
        repaint();
    }

    void setMessage(String message, String hint) {
        this.message = message;
        this.hint = hint;
        setTimes(0, 0, 0, 0, 0, 0);
    }

    private void drawColumn(int x, int y, int columnWidth, int columnHeight, Color color, String textBelow, String textAbove, Graphics graphics) {
        graphics.setColor(color);
        graphics.fillRect(x, y, columnWidth, columnHeight);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(x, y, columnWidth, columnHeight);
        graphics.drawString(textBelow, x + columnWidth / 2 - graphics.getFontMetrics().stringWidth(textBelow) / 2, y + columnHeight + INSET_BOTTOM);
        graphics.drawString(textAbove, x + columnWidth / 2 - graphics.getFontMetrics().stringWidth(textAbove) / 2, y - 10);
    }
}