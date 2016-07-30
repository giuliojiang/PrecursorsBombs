package net.precursorsbombs.testsuite;

import net.precursorsbombs.utils.Pair;
import net.precursorsbombs.utils.PasswordHasher;

import static net.precursorsbombs.testsuite.TestUtils.*;

public class PasswordTests
{

    public static void run()
    {
        TestUtils.print("Password Tests");

        String aPass = "helloWorlld";
        Pair<String, String> result = PasswordHasher.hashNewPassword(aPass);

        String hashed = result.a;
        String salt = result.b;

        assertTrue(PasswordHasher.checkPassword("helloWorlld", hashed, salt));
        assertTrue(!PasswordHasher.checkPassword("helloWorlld ", hashed, salt));
        assertTrue(!PasswordHasher.checkPassword("faser", hashed, salt));
        assertTrue(!PasswordHasher.checkPassword("", hashed, salt));
        assertTrue(!PasswordHasher.checkPassword("helloWorlld", hashed, "fser884"));
        assertTrue(!PasswordHasher.checkPassword("helloWorlld", hashed, ""));

    }

}
