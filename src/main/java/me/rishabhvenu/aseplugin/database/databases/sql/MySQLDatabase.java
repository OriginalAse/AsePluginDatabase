package me.rishabhvenu.aseplugin.database.databases.sql;

public class MySQLDatabase extends SQLDatabase {
    public MySQLDatabase(String host, int port, String database, String username, String password) {
        super(host, port, database, username, password, "com.mysql.cj.jdbc.Driver", "mysql");
    }
}
