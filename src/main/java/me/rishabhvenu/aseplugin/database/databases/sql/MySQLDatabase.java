package me.rishabhvenu.aseplugin.database.databases.sql;

import me.rishabhvenu.aseplugin.AsePlugin;
import me.rishabhvenu.aseplugin.database.options.ConnectionOptions;

public class MySQLDatabase extends SQLDatabase {
    public MySQLDatabase(AsePlugin plugin, ConnectionOptions options) {
        super(plugin, options, "com.mysql.cj.jdbc.Driver", "mysql");
    }
}
