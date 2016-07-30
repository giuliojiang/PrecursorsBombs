package net.precursorsbombs.database;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerWins implements Comparable<PlayerWins>
{

    public String username;
    public int wins;
    
    public PlayerWins(String username, int wins)
    {
        this.username = username;
        this.wins = wins;
    }
    
    @Override
    public int compareTo(PlayerWins other)
    {
        return other.wins - this.wins;
    }
    
    @Override
    public String toString()
    {
        return "" + wins;
    }
    
    // quick test
    public static void main(String[] args)
    {
        ArrayList<PlayerWins> list = new ArrayList<>();
        
        list.add(new PlayerWins("a",2));
        list.add(new PlayerWins("b",5));
        list.add(new PlayerWins("c",3));
        list.add(new PlayerWins("d",1));
        
        System.out.println(list);
        Collections.sort(list);
        System.out.println(list);
    }
    
}
