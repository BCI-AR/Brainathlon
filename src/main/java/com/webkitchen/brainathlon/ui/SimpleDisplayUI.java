package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.brainathlon.gameComponents.ISpectrumPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.gameControl.SimpleDisplayCourse;
import com.webkitchen.brainathlon.ui.elements.SimpleSpectrumDisplayPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Displays the raw EEG signal and all 4 component frequency bands
 *
 * @author Amy Palke
 */
public class SimpleDisplayUI extends AbstractCourseUI
{
    private SimpleDisplayCourse course;

    public SimpleDisplayUI(SimpleDisplayCourse course)
    {
        super(course);
        this.course = course;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI()
    {
        buildView();

        //Display the window.
        pack();
        setVisible(true);
    }

    protected void buildView()
    {
        // Create the player feedback panel(s), and wire them to the monitors
        List<SimpleSpectrumDisplayPanel> playerPanels = new ArrayList<SimpleSpectrumDisplayPanel>();
        PlayerMonitorList<ISpectrumPlayerMonitor> playerMonitorList = course.getPlayerMonitorList();

        for (int i = 0, numPlayers = playerMonitorList.size(); i < numPlayers; i++)
        {
            int playerNumber = i + 1; // players are 1-based, not 0-based
            Player player = playerMonitorList.getPlayer(playerNumber);
            ISpectrumPlayerMonitor monitor = playerMonitorList.getPlayerMonitor(playerNumber);

            SimpleSpectrumDisplayPanel panel = new SimpleSpectrumDisplayPanel(player.getFirstName());
            player.addSampleListener(panel);
            monitor.addSpectrumListener(panel);
            playerPanels.add(panel);
        }

        // Create the main panel, and add it to our content pane
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

        timeDisplay = new JLabel("0:00");
        mainPanel.add(timeDisplay, BorderLayout.NORTH);

        // Create a display panel with a grid that accommodates our player panel(s)
        int columns = playerPanels.size();
        JPanel waveDisplayPanel = new JPanel(new GridLayout(1, columns, 5, 5));
        waveDisplayPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        for (JPanel panel : playerPanels)
        {
            waveDisplayPanel.add(panel);
        }
        mainPanel.add(waveDisplayPanel, BorderLayout.CENTER);

        getContentPane().add(mainPanel);
    }


    public void setTimeDisplay(String time)
    {
        timeDisplay.setText(time);
    }

}
