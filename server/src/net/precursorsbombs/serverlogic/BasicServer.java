package net.precursorsbombs.serverlogic;

import java.net.UnknownHostException;

import net.precursorsbombs.configuration.GlobalConfiguration;
import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.DatabaseFactory;
import net.precursorsbombs.utils.JVMStatistics;

public class BasicServer {
	
	private static final int serverPort = 1080;
	
	public static void main(String[] args) throws UnknownHostException
	{
	    Thread jvmStatisticsThread = new Thread(new JVMStatistics());
	    jvmStatisticsThread.start();
	    
		GlobalConfiguration conf = new GlobalConfiguration();
		conf.parseCommandLine(args);
		
		BombermanDatabase db = DatabaseFactory.makeDatabase(conf);
		
	    PlayerManagerInterface playerManager = new PlayerManager(db);
	    EventsManagerInterface eventsManagerInterface = new EventsManager(playerManager);
	    
		BombermanServerCommunication communicator = new BombermanServerCommunication(serverPort, eventsManagerInterface);
		System.out.println("Starting server on port " + serverPort);

		communicator.start();
	}

}
