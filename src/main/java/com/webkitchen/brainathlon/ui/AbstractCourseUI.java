package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.gameComponents.MidiFeedback;
import com.webkitchen.brainathlon.gameControl.AbstractCourse;
import com.webkitchen.brainathlon.util.MathUtil;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;

/**
 * Contains the generic logic for all course UIs
 *
 * @author Amy Palke
 */
public abstract class AbstractCourseUI extends JFrame
{
    private boolean soundEnabled;
    private MidiFeedback midiFeedback;
    protected JLabel timeDisplay;

    public AbstractCourseUI(AbstractCourse course)
    {
        super(course.getTitle());
        loadMidi();
    }

    protected abstract void buildView();

    /**
     * Create the GUI and show it.  For thread safety, this method should be invoked from the event-dispatching thread.
     */
    public void createAndShowGUI()
    {
        buildView();
        packAndShow();
    }

    protected void loadMidi()
    {
        midiFeedback = MidiFeedback.getInstance();
        try
        {
            midiFeedback.loadMidi();
            soundEnabled = true;
        }
        catch (MidiUnavailableException e)
        {
            soundEnabled = false;
        }
    }

    protected void playSound(MidiFeedback.SoundType type, int channel, int instrument, MidiFeedback.Volume volume)
    {
        if (soundEnabled && isVisible())
        {
            midiFeedback.playSound(type, channel, instrument, volume);
        }
    }

    protected void packAndShow()
    {
        //Display the window.
        pack();
        setVisible(true);
    }

    public void setTimeDisplay(int time)
    {
        int minutes = time / 60;
        int seconds = time % 60;
        String display = String.valueOf(minutes) + ":" + MathUtil.padInt(seconds, 2);
        timeDisplay.setText(display);
    }
}
