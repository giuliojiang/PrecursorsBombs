package net.precursorsbombs.timer;

import net.precursorsbombs.match.BombermanMatch;
import net.precursorsbombs.serverlogic.Map;

public class BombermanTimer implements Runnable
{

    private final static int INTERVAL = 30;
    private Map map;
    private BombermanMatch match;

    public BombermanTimer(Map objects, BombermanMatch match)
    {
        this.map = objects;
        this.match = match;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(INTERVAL);
                
                // updateAll returns true while the game is in progress
                if (!map.updateAll())
                {
                    match.reset();
                    return;
                }
            } catch (InterruptedException e)
            {
            }

        }
    }

}
