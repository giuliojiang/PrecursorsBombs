package net.precursorsbombs.testsuite;

import java.util.Deque;
import java.util.LinkedList;

public class TestUtils
{
    private static final boolean VERBOSE = true;
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    private static Deque<String> log = new LinkedList<String>();
    
    public static void print(String s)
    {
        if (VERBOSE)
        {
            System.out.println(s);
        }
    }

    public static void assertEquals(Object actual, Object expected)
    {
        testsRun++;
        if (!(actual.equals(expected)))
        {
            print("\tExpected=" + expected + "Actual=" + actual);
        } else
        {
            testsPassed++;
        }
    }
    
    public static void assertEquals(int actual, int expected)
    {
        testsRun++;
        if (actual != expected)
        {
            print("\tExpected=" + expected + "Actual=" + actual);
        } else
        {
            testsPassed++;
        }
    }
    
    
    public static void assertEqualsApprox(double a, double b)
    {
        testsRun++;
        
        double diff = Math.abs(a-b);
        if (diff > 0.001)
        {
            print("\tExpected=" + a + "Actual=" + b);
        } else
        {
            testsPassed++;
        }
    }
    
    public static void assertTrue(boolean b)
    {
        testsRun++;
        if (!b)
        {
            print("Assertion failed");
            Thread.dumpStack();
        } else
        {
            testsPassed++;
        }
    }
    
    public static int printTestResults()
    {
        System.out.println("Passed " + testsPassed + "/" + testsRun + " tests.");
        return testsPassed == testsRun ? 0 : 1;
    }
    
    public static void log(String s)
    {
        while (log.size() > 10)
        {
            log.removeFirst();
        }
        log.addLast(s);
    }
    
    public static String getLastLoggedMessage()
    {
        if (log.isEmpty())
        {
            return "";
        } else
        {
            return log.getLast();
        }
    }
    
}
