package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;


/**
 * Displays a "please wait" dialog
 *
 * @author Amy Palke
 */
public class PleaseWaitForFilterSetupUI
{
    private JWindow window;
    private JPanel messagePanel;

    public void createAndShowGUI()
    {
        messagePanel = createMessagePanel();
        window = new JWindow();
        Container contentPane = window.getContentPane();
        contentPane.add(messagePanel, BorderLayout.CENTER);
        centerWindow();
        window.pack();
        window.setVisible(true);
    }

    public void disposeOfGUI()
    {
        window.dispose();
    }

    private JPanel createMessagePanel()
    {
        JPanel mainPanel = new TitledPanel("Filter setup");

        JLabel caption = new JLabel("Please wait while filters are being set up...", SwingConstants.CENTER);
        caption.setForeground(Color.red);
        caption.setFont(new Font("TimesRoman", Font.ITALIC, 24));
        mainPanel.add(caption, BorderLayout.CENTER);

        return mainPanel;
    }

    private void centerWindow()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        Dimension messagePanelSize = messagePanel.getPreferredSize();
        int messagePanelWidth = messagePanelSize.width;
        int messagePanelHeight = messagePanelSize.height;

        window.setLocation((screenWidth / 2) - (messagePanelWidth / 2),
                           (screenHeight / 2) - (messagePanelHeight / 2));
    }

}
