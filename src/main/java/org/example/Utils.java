package org.example;

import java.io.FileInputStream;
import java.util.Properties;

public class Utils {

    public static String getSecureToken(String keyName) {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            return prop.getProperty(keyName);
        } catch (Exception ex) {
            System.out.println("שגיאה בקריאת הקובץ: " + ex.getMessage());
            return null;
        }
    }
}