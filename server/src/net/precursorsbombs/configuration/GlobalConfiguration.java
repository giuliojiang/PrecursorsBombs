package net.precursorsbombs.configuration;

public class GlobalConfiguration {

	private boolean useRealMysqlServer = false;

	public boolean isUseRealMysqlServer() {
		return useRealMysqlServer;
	}

	public void parseCommandLine(String[] args)
	{
		for (String s : args)
		{
			if (s.equals("sql"))
			{
				useRealMysqlServer = true;
			}
		}
	}
	
	
}
