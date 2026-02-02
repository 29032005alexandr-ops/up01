
package ru.up01.app;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Simple connection factory for MySQL. */
public final class Db {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Db.class.getResourceAsStream("/db.properties")) {
            if (in == null) throw new IllegalStateException("db.properties not found in resources");
            PROPS.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Db() {}

    public static Connection getConnection() throws SQLException {
        String url = PROPS.getProperty("url");
        String user = PROPS.getProperty("user");
        String password = PROPS.getProperty("password");
        return DriverManager.getConnection(url, user, password);
    }

    public static String getUrl() { return PROPS.getProperty("url"); }
    public static String getUser() { return PROPS.getProperty("user"); }
}
