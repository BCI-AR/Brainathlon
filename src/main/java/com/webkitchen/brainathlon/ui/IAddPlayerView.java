package com.webkitchen.brainathlon.ui;

import java.awt.event.ActionListener;
import java.util.Map;

/**
 * @author Amy Palke
 */
public interface IAddPlayerView
{
    public void buildView(String description);

    public void packAndShow();

    public String getPlayerName();

    public void setPlayerName(String playerName);

    public Integer getChannel();

    public void setChannel(Integer channel);

    public Integer getInstrument();

    public void setInstrument(Integer instrument);

    public void setInstrumentMap(Map instrumentMap);

    public void addInstrumentListener(ActionListener listener);

    public void addButtonListener(ActionListener listener);

    public void closeView();
}
