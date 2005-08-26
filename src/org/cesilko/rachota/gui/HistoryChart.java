/*
 * HistoryChart.java
 *
 * Created on August 7, 2005, 8:45 PM
 */

package org.cesilko.rachota.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.AbstractTaskFilter;

/** Chart showing either from/to or total times in given
 * period scale.
 * @author Jiri Kovalsky
 */
public class HistoryChart extends JPanel {
    
    /** Days whose data should be drawn. */
    private Vector days;
    /** Filter for highlighting tasks. */
    private AbstractTaskFilter taskFilter;
    /** Type of history chart to be drawn. */
    private int chartType;
    /** Maximum value on Y axis. */
    int maxValueY;
    /** Minimum value on X axis. */
    int minValueY;
    /** Distance between two values on X axis. */
    private double xStep;
    /** Distance between two values on Y axis. */
    private double yStep;
    /** Chart displaying total times for days. */
    public static final int TYPE_TOTAL = 0;
    /** Chart displaying from/to times for days. */
    public static final int TYPE_FROM_TO = 1;
    /** Left space before chart e.g. distance between left edge and X axis. */
    private static final int INSET_LEFT = 20;
    /** Right space behind chart. */
    private static final int INSET_RIGHT = 10;
    /** Top space above chart. */
    private static final int INSET_TOP = 10;
    /** Bottom space behind chart. */
    private static final int INSET_BOTTOM = 30;
    /** Light green color for chart axes and grid. */
    private static final Color LIGHT_GREEN = new Color(0, 153, 153);
    
    /** Creates a new history chart.
     * @param days Days that should be used to draw history chart.
     * @param taskFilter Filter for highlighting tasks when drawing chart.
     * @param chartType Type of history chart to be drawn.
     */
    public HistoryChart(Vector days, AbstractTaskFilter taskFilter, int chartType) {
        super();
        setDays(days);
        setHighlightingFilter(taskFilter);
        setChartType(chartType);
    }
    
    /** Sets which days should be used to draw history chart.
     * @param days Days that should be used to draw history chart.
     */
    public void setDays(Vector days) {
        this.days = days;
        repaint();
    }
    
    /** Sets task filter for highlighting tasks when drawing chart.
     * @param taskFilter Filter that will be used to select tasks that
     * will be highlighted when drawing total times chart.
     */
    public void setHighlightingFilter(AbstractTaskFilter taskFilter) {
        this.taskFilter = taskFilter;
        repaint();
    }
    
    /** Sets type of history chart to be drawn.
     * @param chartType Type of history chart to be drawn.
     */
    public void setChartType(int chartType) {
        this.chartType = chartType;
        repaint();
    }
    
    /** Draws the chart of specified type from given days.
     * @param graphics Graphics area where the chart should be drawn.
     */
    public void paint(Graphics graphics) {
        graphics.clearRect(0, 0, getBounds().width, getBounds().height);
        computeSteps();
        drawAxes(graphics, true);
        drawHours(graphics);
        drawReferenceLines(graphics, true, true);
    }
    
    /** Computes both X and Y steps based on days and type of chart. */
    private void computeSteps() {
        computeMaxMinY();
        xStep = (getBounds().width - INSET_LEFT - INSET_RIGHT) / (double) days.size();
        yStep = (getBounds().height - INSET_TOP - INSET_BOTTOM) / (double) (maxValueY - minValueY);
    }
    
    /** Computes maximum and minimum values for Y axis. */
    private void computeMaxMinY() {
        minValueY = 24;
        maxValueY = 0;
        Iterator iterator = days.iterator();
        while (iterator.hasNext()) {
            Day day = (Day) iterator.next();
            if (chartType == TYPE_TOTAL) {
                int hours = (int) day.getTotalTime()/(1000*60*60);
                if (hours > maxValueY) maxValueY = hours;
            } else {
                if (day.getStartTime() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(day.getStartTime());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (hour < minValueY) minValueY = hour;
                    calendar.setTime(day.getFinishTime());
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (hour > maxValueY) maxValueY = hour;
                }
            }
        }
        if (minValueY == 24) minValueY = 0;
        if (maxValueY != 0) maxValueY = maxValueY + 2;
        else maxValueY = 25;
    }
    
    /** Draws axes and values including chart grid if needed.
     * @param graphics Graphics area where the axes should be drawn.
     * @param drawGrid Whether chart grid should be drawn or not.
     */
    private void drawAxes(Graphics graphics, boolean drawGrid) {
        int width = getBounds().width;
        int height = getBounds().height;
        graphics.setColor(LIGHT_GREEN);
        graphics.drawRect(INSET_LEFT, INSET_TOP, width - INSET_LEFT - INSET_RIGHT, height - INSET_TOP -INSET_BOTTOM);
        int lastValueX = 0;
        int lastLineX = 0;
        int columns = days.size();
        for (int i=0; i<columns; i++) {
            int xi = (int) (INSET_LEFT + xStep * (i + 1));
            if ((xi - lastLineX) > 5) {
                graphics.setColor(LIGHT_GREEN);
                graphics.drawLine(xi, height - INSET_BOTTOM, xi, height - INSET_BOTTOM - 5);
                lastLineX = xi;
            }
            if ((xi - lastValueX) > 50) {
                String value = days.get(i).toString();
                int correction = value.length() * 3;
                graphics.setColor(Color.DARK_GRAY);
                graphics.drawString(days.get(i).toString(), (int) (INSET_LEFT + xStep * (i + 0.5) - correction), height - INSET_BOTTOM/2);
                lastValueX = xi;
            }
        }
        int lastValueY = height - INSET_BOTTOM;
        int lastLineY = height - INSET_BOTTOM;
        int gridStep = (maxValueY - minValueY) / 5;
        int nextGrid = gridStep - 1;
        for (int i=0; i<(maxValueY - minValueY); i++) {
            int yi = (int) (height - INSET_BOTTOM - yStep * (i + 1));
            if ((lastLineY - yi) > 5) {
                graphics.setColor(LIGHT_GREEN);
                graphics.drawLine(INSET_LEFT, yi, INSET_LEFT + 5, yi);
                lastLineY = yi;
            }
            if ((drawGrid) && (i == nextGrid)) {
                graphics.setColor(LIGHT_GREEN);
                graphics.drawLine(INSET_LEFT, yi, width - INSET_RIGHT, yi);
                nextGrid = nextGrid + gridStep;
            }
            if ((lastValueY - yi) > 50) {
                String valueY = "" + (minValueY + i);
                graphics.setColor(Color.DARK_GRAY);
                graphics.drawString(valueY, (minValueY + i > 9 ? 2 : 6), (int) (height - INSET_BOTTOM - yStep * i + 3));
                lastValueY = yi;
            }
        }
    }
    
    /** Draws actual data i.e. hour columns.
     * @param graphics Graphics area where the axes should be drawn.
     */
    private void drawHours(Graphics graphics) {
        int height = getBounds().height;
        int width = getBounds().width;
        int count = days.size();
        for (int i=0; i<count; i++) {
            Day day = (Day) days.get(i);
            if (chartType == TYPE_TOTAL) {
                double hours = day.getTotalTime() / (double) (1000*60*60);
                int columnHeight = (int) ((height - INSET_BOTTOM - INSET_TOP) * (hours / maxValueY));
                int x = INSET_LEFT + (int) (xStep * i) + (xStep > 4 ? 2 : 0);
                if (columnHeight != 0) {
                    graphics.setColor(Color.LIGHT_GRAY);
                    graphics.fillRect(x, height - INSET_BOTTOM - columnHeight, (int) (xStep > 4 ? xStep - 3 : xStep), columnHeight);
                    if (taskFilter != null) {
                        Vector filteredTasks = taskFilter.filterTasks(day.getTasks());
                        if (filteredTasks.size() != 0) {
                            Iterator iterator = filteredTasks.iterator();
                            long totalTime = 0;
                            while (iterator.hasNext()) {
                                Task task = (Task) iterator.next();
                                if (!task.privateTask()) totalTime = totalTime + task.getDuration();
                            }
                            double filtered = totalTime / (double) (1000*60*60);
                            int filteredTasksHeight = (int) ((height - INSET_BOTTOM - INSET_TOP) * (filtered / maxValueY));
                            graphics.setColor(Color.CYAN);
                            graphics.fillRect(x, height - INSET_BOTTOM - filteredTasksHeight, (int) (xStep > 4 ? xStep - 3 : xStep), filteredTasksHeight);
                        }
                    }
                    graphics.setColor(Color.DARK_GRAY);
                    graphics.drawRect(x, height - INSET_BOTTOM - columnHeight, (int) (xStep > 4 ? xStep - 3 : xStep), columnHeight);
                    String value = Tools.getTime(day.getTotalTime());
                    value = value.substring(0, value.lastIndexOf(":"));
                    if (hours < 10) value = value.substring(1);
                    int widths[] = graphics.getFontMetrics().getWidths();
                    int correction = 0;
                    for (int j=0; j<value.length(); j++) correction = correction + widths[value.charAt(j)];
                    graphics.setColor(Color.DARK_GRAY);
                    if (correction <= xStep + 2)
                        graphics.drawString(value, (int) (x + xStep/2 - correction/2 - 1), height - INSET_BOTTOM - columnHeight - 10);
                }
            } else { // TYPE_FROM_TO chart type was selected.
                if (day.getStartTime() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(day.getStartTime());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    double startTime = hour + minute / (double) 60;
                    calendar.setTime(day.getFinishTime());
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                    double finishTime = hour + minute / (double) 60;
                    int x = INSET_LEFT + (int) (xStep * i) + (xStep > 4 ? 2 : 0);
                    int y1 = (int) (height - INSET_BOTTOM - yStep * (finishTime - minValueY));
                    int y2 = (int) (yStep * (finishTime - startTime));
                    graphics.setColor(Color.LIGHT_GRAY);
                    graphics.fillRect(x, y1, (int) (xStep > 4 ? xStep - 3 : xStep), y2);
                    graphics.setColor(Color.DARK_GRAY);
                    graphics.drawRect(x, y1, (int) (xStep > 4 ? xStep - 3 : xStep), y2);
                    String start = Tools.getTime(day.getStartTime());
                    if (start.charAt(0) == '0') start = start.substring(1);
                    String finish = Tools.getTime(day.getFinishTime());
                    if (finish.charAt(0) == '0') finish = finish.substring(1);
                    int widths[] = graphics.getFontMetrics().getWidths();
                    int widthStart = 0;
                    int widthFinish = 0;
                    for (int j=0; j<start.length(); j++) widthStart = widthStart + widths[start.charAt(j)];
                    for (int j=0; j<finish.length(); j++) widthFinish = widthFinish + widths[finish.charAt(j)];
                    graphics.setColor(Color.DARK_GRAY);
                    if ((!start.equals("0:00")) && (widthStart <= xStep + 2))
                        graphics.drawString(start, (int) (x + xStep/2 - widthStart/2 - 1), y1 + y2 - 10);
                    if ((!finish.equals("0:00"))  && (widthFinish <= xStep + 2))
                        graphics.drawString(finish, (int) (x + xStep/2 - widthFinish/2 - 1), y1 - 10);
                }
            }
        }
    }
    
    /** Draws reference lines in chart if required.
     * @param graphics Graphics area where the axes should be drawn.
     * @param drawAverageHours If true, average hours for all and working days will be drawn.
     * @param drawGivenHours If true, given working hours will be drawn.
     */
    private void drawReferenceLines(Graphics graphics, boolean drawAverageHours, boolean drawGivenHours) {
        if (chartType != TYPE_TOTAL) return;
        Iterator iterator = days.iterator();
        double totalHours = 0;
        int workDays = 0;
        while (iterator.hasNext()) {
            Day day = (Day) iterator.next();
            totalHours = totalHours + day.getTotalTime() / (double) (1000*60*60);
            if (day.getTotalTime() != 0) workDays++;
        }
        double averageAll = totalHours / days.size();
        double averageWork = (workDays == 0 ? 0 : totalHours / workDays);
        double given = Double.parseDouble((String) Settings.getDefault().getSetting("dayWorkHours"));
        int width = getBounds().width;
        int height = getBounds().height;
        Font originalFont = getFont();
        graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
        if (drawAverageHours) {
            graphics.setColor(Color.BLUE);
            int y = (int) (height - INSET_BOTTOM - yStep * averageWork);
            graphics.drawLine(INSET_LEFT, y, width - INSET_RIGHT, y);
            String legend = Translator.getTranslation("HISTORYCHART.AVERAGE_WORK");
            int widths[] = graphics.getFontMetrics().getWidths();
            int correction = 0;
            for (int i=0; i<legend.length(); i++) correction = correction + widths[legend.charAt(i)];
            graphics.drawString(legend, width - INSET_RIGHT - correction, y - 5);
            graphics.setColor(Color.MAGENTA);
            y = (int) (height - INSET_BOTTOM - yStep * averageAll);
            graphics.drawLine(INSET_LEFT, y, width - INSET_RIGHT, y);
            graphics.drawString(Translator.getTranslation("HISTORYCHART.AVERAGE_ALL"), INSET_LEFT + 5, y - 5);
        }
        if (drawGivenHours) {
            graphics.setColor(Color.RED);
            int y = (int) (height - INSET_BOTTOM - yStep * given);
            graphics.drawLine(INSET_LEFT, y, width - INSET_RIGHT, y);
            graphics.drawString(Translator.getTranslation("HISTORYCHART.GIVEN_HOURS"), INSET_LEFT + 5, y - 5);
        }
        graphics.setFont(originalFont);
    }
}