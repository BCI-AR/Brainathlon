package com.webkitchen.brainathlon.gameComponents;


/**
 * Interface for monitoring players and generating scores and reward events
 *
 * @author Amy Palke
 */
public interface IPlayerMonitor extends IScoreGenerator, IRewardGenerator
{
    public int getFinalScore();
}
