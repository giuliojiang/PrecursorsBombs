package net.precursorsbombs.utils;

public class JVMStatistics implements Runnable
{
    private long mb = 1024*1024;
    Runtime runtime = Runtime.getRuntime();
    private long sleep_interval = 10000; // update interval in ms

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(sleep_interval);
                long used_memory = (runtime.totalMemory() - runtime.freeMemory()) / mb;
                long total_memory = runtime.totalMemory() / mb;
                System.out.println("Used memory: " + used_memory + "/" + total_memory + "MB");
            } catch (InterruptedException e)
            {
                return;
            }
        }

    }

}
