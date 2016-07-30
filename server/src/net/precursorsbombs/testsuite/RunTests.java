package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.printTestResults;

public class RunTests
{
    private static final boolean RUN_DATABASE = false;

    public static void main(String[] args)
    {
        System.out.println("Running backend tests...");

        PlayerTests.run();
        CellTests.run();
        EmptyMapObjectTests.run();
        BombTests.run();
        EventsTests.run();
        MatchTests.run();
        BasicMapTests.run();
        PasswordTests.run();
        StatisticsDbTests.run();

        if (RUN_DATABASE)
        {
            DatabaseConnectionTests.run();
            MysqlDatabaseTests.run();
        }

        System.out.println("All tests run");
        System.exit(printTestResults());
    }

}
