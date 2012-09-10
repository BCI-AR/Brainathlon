package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Displays an error message dialog
 *
 * @author Amy Palke
 */
public class ErrorMessageUI extends JFrame
{
    private JDialog dialog;
    private JPanel messagePanel;
    private String message;

    public ErrorMessageUI(String message)
    {
        this.message = message;
    }

    public void createAndShowGUI()
    {
        dialog = new JDialog(this, "Oops.  There's been an error.", true);
        messagePanel = createMessagePanel();
        Container contentPane = dialog.getContentPane();
        contentPane.add(messagePanel, BorderLayout.CENTER);
        centerWindow();
        dialog.pack();
        dialog.setVisible(true);
    }

    public void disposeOfGUI()
    {
        dialog.dispose();
    }

    private JPanel createMessagePanel()
    {
        JPanel mainPanel = new TitledPanel("Error");

        JLabel caption = new JLabel(message, SwingConstants.CENTER);
        caption.setForeground(Color.red);
        caption.setFont(new Font("TimesRoman", Font.ITALIC, 24));
        mainPanel.add(caption, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        mainPanel.add(okButton, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Close the dialog
                dialog.removeAll();
                dialog.dispose();
            }
        });

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

        dialog.setLocation((screenWidth / 2) - (messagePanelWidth / 2),
                           (screenHeight / 2) - (messagePanelHeight / 2));
    }

}
