package me.rishabhvenu.aseplugin.database.options;

import lombok.Getter;

public class ConnectionOptions {
    @Getter private final String host;
    @Getter private final int port;
    @Getter private final String database;
    @Getter private final String username;
    @Getter private final String password;

    public ConnectionOptions(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
}
