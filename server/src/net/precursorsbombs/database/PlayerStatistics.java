package net.precursorsbombs.database;

public class PlayerStatistics
{

    private int experience;
    // TODO date/time?
    private int wins;
    private int totalgames;

    public PlayerStatistics(int experience, int wins, int totalgames)
    {
        this.experience = experience;
        this.wins = wins;
        this.totalgames = totalgames;
    }

    public int getExperience()
    {
        return experience;
    }

    public void setExperience(int experience)
    {
        this.experience = experience;
    }

    public int getWins()
    {
        return wins;
    }

    public void setWins(int wins)
    {
        this.wins = wins;
    }

    public int getTotalgames()
    {
        return totalgames;
    }

    public void setTotalgames(int totalgames)
    {
        this.totalgames = totalgames;
    }

}
