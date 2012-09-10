package com.webkitchen.brainathlon.ui.elements;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Amy Palke
 */
public class RawSignalPanelOld extends JPanel
{
    private int baseLine;
    private int xLoc;
    private int yLoc;

    private double scale;

    protected List points = new ArrayList();
    protected int vectorsPainted;

    private Color waveColor = Color.RED;
    private Color backgroundColor = this.getBackground();

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public RawSignalPanelOld(int preferredPanelHeight, double scale)
    {
        this.scale = scale;
        baseLine = preferredPanelHeight / 2;
        xLoc = 0;
        yLoc = baseLine;
        initPoints();
        points.add(new Point(xLoc, yLoc));
    }

    public void setCurrentValue(double currentValue)
    {
        int yNew = (int) currentValue;
        yNew = scaleValue(yNew) + getBaseLine();
        int xNew = incrementX(xLoc);

        // Check if we are wrapping back to the start
        if (xNew == 0)
        {
            initPoints();
        }
        points.add(new Point(xNew, yNew));
        xLoc = xNew;
        yLoc = yNew;

        // This method will be invoked from the reader thread, so
        // schedule a job for the event-dispatching thread to update our values
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                drawSignal();
            }
        });
    }

    private void initPoints()
    {
        vectorsPainted = 0;
        points.clear();
    }

    /**
     * Calls the UI delegate's paint method, if the UI delegate
     * is non-<code>null</code>.  We pass the delegate a copy of the
     * <code>Graphics</code> object to protect the rest of the
     * paint code from irrevocable changes
     * (for example, <code>Graphics.translate</code>).
     * <p/>
     * If you override this in a subclass you should not make permanent
     * changes to the passed in <code>Graphics</code>. For example, you
     * should not alter the clip <code>Rectangle</code> or modify the
     * transform. If you need to do these operations you may find it
     * easier to create a new <code>Graphics</code> from the passed in
     * <code>Graphics</code> and manipulate it. Further, if you do not
     * invoker super's implementation you must honor the opaque property,
     * that is
     * if this component is opaque, you must completely fill in the background
     * in a non-opaque color. If you do not honor the opaque property you
     * will likely see visual artifacts.
     *
     * @param g the <code>Graphics</code> object to protect
     * @see #paint
     */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // reset points and xLoc to start drawing from the beginning
        //   in case window was resized
        initPoints();
        baseLine = getHeight() / 2;
        yLoc = baseLine;
        xLoc = getWidth();
    }

    private void drawSignal()
    {
        if (vectorsPainted < points.size())
        {
            Graphics g = getGraphics();
            drawBaseline(g);
            drawWave(g);
        }
    }

    private void drawBaseline(Graphics g)
    {
        int currentBaseLine = getBaseLine();
        g.drawLine(0, currentBaseLine, getWidth(), currentBaseLine);
    }

    private void drawWave(Graphics g)
    {
        Color currentColor = g.getColor();

        // If we are wrapping back to start, repaint screen
        // otherwise just draw the line
        if (points.size() > 0 && ((Point) points.get(vectorsPainted)).x == 0)
        {
            paintBackdrop(g);
        }
        g.setColor(waveColor);
        // only paint new vectors since last update call
        for (int i = vectorsPainted; i < points.size() - 1; i++)
        {
            paintVector(g, i);
        }
        g.setColor(currentColor);
    }

    private void paintBackdrop(Graphics g)
    {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void paintVector(Graphics g, int start)
    {
        Point p1 = (Point) points.get(start);
        Point p2 = (Point) points.get(start + 1);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
        vectorsPainted++;
    }


    private int incrementX(int x)
    {
        // wrap value
        int result = x + 1;
        if (result >= getWidth())
        {
            result = 0;
        }
        return result;
    }

    private int scaleValue(int value)
    {
        int result = (int) (value * scale);
        return result;
    }

    private int getBaseLine()
    {
        baseLine = getHeight() / 2;
        return baseLine;
    }

}
