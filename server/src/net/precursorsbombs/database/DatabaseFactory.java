package net.precursorsbombs.database;

import net.precursorsbombs.configuration.GlobalConfiguration;

public class DatabaseFactory {

	public static BombermanDatabase makeDatabase(GlobalConfiguration conf)
	{
		if (conf.isUseRealMysqlServer())
		{
			return new MysqlDatabase();
		} else
		{
			return new LocalDatabase();
		}
	}
	
}
