package net.precursorsbombs.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher
{
    private static String hashWithSalt(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    
    private static byte[] stringToBytes(String s)
    {
        byte[] result = new byte[s.length()];
        
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            result[i] = charToByte(chars[i]);
        }
        
        return result;
    }
    
    private static String bytesToString(byte[] bs)
    {
        char[] chars = new char[bs.length];
        
        for (int i = 0; i < bs.length; i++)
        {
            chars[i] = byteToChar(bs[i]);
        }
        return new String(chars);
    }

    private static byte charToByte(char c)
    {
        if (c >= '0' && c <= '9')
        {
            return (byte) c;
        } else
        {
            return 0;
        }
    }
    
    private static char byteToChar(byte b)
    {
        char c = (char) b;
        if (c >= '0' && c <= '9')
        {
            return c;
        } else
        {
            return '0';
        }
    }

    private static String getSalt()
    {
        String s = new Double(Math.random()).toString();
        byte[] b = stringToBytes(s);
        return bytesToString(b);
    }
    
    public static Pair<String, String> hashNewPassword(String password)
    {
        String salt = getSalt();
        
        byte[] saltBytes = stringToBytes(salt);
        
        String hashed = hashWithSalt(password, saltBytes);
        
        return new Pair<String, String>(hashed, salt);
    }
    
    public static boolean checkPassword(String password, String hashed, String salt)
    {
        byte[] saltBytes = stringToBytes(salt);
        String newlyHashed = hashWithSalt(password, saltBytes);
        
        return newlyHashed.equals(hashed);
    }
}