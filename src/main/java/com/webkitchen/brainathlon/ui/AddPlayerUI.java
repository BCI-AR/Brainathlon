package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.ui.elements.MapComboBox;
import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;


/**
 * Shows the UI for adding a new player
 *
 * @author Amy Palke
 */
public class AddPlayerUI extends JFrame implements IAddPlayerView
{
    private JDialog dialog;
    private JTextField playerNameText;
    private JComboBox channelList;
    private MapComboBox soundList;
    private JButton addButton;
    private String description;

    public void buildView(String description)
    {
        this.description = description;
        buildDialog();
    }

    public void packAndShow()
    {
        /* setLocationRelativeTo() must be called after pack() because
        placement is based on size.  Since this frame isn't visible,
        setLocationRelativeTo() will cause the dialog to be centered
        on screen. */
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true); // blocks until dialog is disposed
    }

    public void closeView()
    {
        dialog.dispose();
    }

    public String getPlayerName()
    {
        return playerNameText.getText();
    }

    public void setPlayerName(final String playerName)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                playerNameText.setText(playerName);
            }
        });
    }

    public Integer getChannel()
    {
        return (Integer) channelList.getSelectedItem();
    }

    public void setChannel(final Integer channel)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                channelList.setSelectedItem(channel);
            }
        });
    }

    public Integer getInstrument()
    {
        return (Integer) soundList.getSelectedValue();
    }

    public void setInstrument(Integer instrument)
    {
        soundList.setSelectedItem(instrument);
    }

    public void setInstrumentMap(final Map instrumentMap)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                soundList.setMap(instrumentMap);
            }
        });
    }

    public void addInstrumentListener(ActionListener listener)
    {
        soundList.addActionListener(listener);
    }

    public void addButtonListener(ActionListener listener)
    {
        addButton.addActionListener(listener);
    }

    private void buildDialog()
    {
        dialog = new JDialog(this, description, true);
        Container dialogPane = dialog.getContentPane();
        JPanel addPlayerPanel = createAddPlayerPanel();
        dialogPane.add(addPlayerPanel, BorderLayout.CENTER);
    }

    private JPanel createAddPlayerPanel()
    {
        JPanel mainPanel = new TitledPanel(description);
        mainPanel.setLayout(new SpringLayout());

        // Name row
        JLabel nameLabel = new JLabel("Name:");
        playerNameText = new JTextField();
        mainPanel.add(nameLabel);
        mainPanel.add(playerNameText);
        nameLabel.setLabelFor(playerNameText);

        // Channel row
        JLabel channelLabel = new JLabel("Channel:");
        channelList = new JComboBox(Configuration.getChannels());
        mainPanel.add(channelLabel);
        mainPanel.add(channelList);
        nameLabel.setLabelFor(channelList);

        // Sound pick row
        JLabel soundLabel = new JLabel("Feedback sound:");
        soundList = new MapComboBox();
        mainPanel.add(soundLabel);
        mainPanel.add(soundList);
        nameLabel.setLabelFor(soundList);

        // Button row
        JLabel blankLabel = new JLabel("");
        addButton = new JButton(description);
        mainPanel.add(blankLabel);
        mainPanel.add(addButton);
        dialog.getRootPane().setDefaultButton(addButton);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(mainPanel,
                                        4, 2, //rows, cols
                                        6, 6, //initX, initY
                                        6, 6); //xPad, yPad

        return mainPanel;
    }
}
