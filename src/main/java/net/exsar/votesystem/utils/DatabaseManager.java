package net.exsar.votesystem.utils;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class DatabaseManager {

    private static HikariDataSource connection;
    private static final String prefix = "§8[§c§lMYSQL§8] §7";

    private final String ip;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseManager(String ip, String database, String username, String password) {
        this.ip = password;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /*
     * Methode um mit der Datenbank zu verbinden.
     */

    public void load() {
        try {
            if (isConnected()) {
                Bukkit.getConsoleSender().sendMessage(prefix + "§cVerbindung bereits aufgebaut.");
                return;
            }

            synchronized (this) {
                if(isConnected()) {
                    Bukkit.getConsoleSender().sendMessage(prefix + "§cVerbindung bereits aufgebaut.");
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                connection = new HikariDataSource();
                connection.setJdbcUrl("jdbc:mysql://" + getIp() + ":3306/"+ getDatabase() + "?autoReconnect=true&useUnicode=yes");
                connection.setUsername(getUsername());
                connection.setPassword(getPassword());
                connection.setMaximumPoolSize(20);
                connection.setConnectionTimeout(45000);
                connection.setIdleTimeout(600000);
                connection.setMaxLifetime(1800000);
                onUpdate("CREATE TABLE IF NOT EXISTS players (player VARCHAR(255), tokens INTEGER)");
                Bukkit.getConsoleSender().sendMessage(prefix + "Verbindung aufgebaut!");
            }
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    /*
     * Methode um die Verbindung mit Datenbank zu trennen.
     */
    public static void unload() {
        if (isConnected()) {
            connection.close();
            connection = null;
            Bukkit.getConsoleSender().sendMessage(prefix + "Verbindung unterbrochen!");
        }
    }

    /*
     * Abfrage, ob eine generelle Verbindung zum Datenbank existiert.
     */
    public static boolean isConnected() {
        return connection != null && !connection.isClosed();
    }

    private void onUpdate(String sql) {
        try {
            try (Statement statement = connection.getConnection().createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (NullPointerException | SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
