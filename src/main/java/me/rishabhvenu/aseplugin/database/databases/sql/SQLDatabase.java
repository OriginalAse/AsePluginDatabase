package me.rishabhvenu.aseplugin.database.databases.sql;

import me.rishabhvenu.aseplugin.AsePlugin;
import me.rishabhvenu.aseplugin.database.options.ConnectionOptions;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SQLDatabase {
    private final AsePlugin plugin;
    private Connection connection;

    public SQLDatabase(AsePlugin plugin, ConnectionOptions options, String driver, String connectionname) {
        this.plugin = plugin;
        try {
            Class.forName(driver);
            this.connection = DriverManager.getConnection("jdbc:" + connectionname + "://"  + options.getHost() + ":" +
                    options.getPort() + "/" + options.getDatabase() + "?autoReconnect=true", options.getUsername(), options.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean tableExists(String table) {
        AtomicBoolean exists = new AtomicBoolean(false);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                DatabaseMetaData dbm = connection.getMetaData();
                ResultSet rs = dbm.getTables(null, null, table, null);
                exists.set(rs.next());
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return exists.get();
    }

    public Table createTable(String table) {
        return new Table(table);
    }

    public void createTable(Table table) {
        StringBuilder query = new StringBuilder("CREATE TABLE " + table.getName() + "(");
        Map<String, Map<DataType, String[]>> columns = table.getColumns();
        boolean comma = false;
        for (String key : columns.keySet()) {
            if (comma) {
                query.append(", ");
            }
            comma = true;
            Map<DataType, String[]> column = columns.get(key);
            query.append(key).append(" ").append(column.keySet().toArray()[0]);
            String[] params = (String[]) column.values().toArray()[0];
            if (params.length > 0)
                bracketQueryBuilder(params, query);
        }
        execute(query.append(")").toString());
    }

    public void execute(String query) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public ResultSet select(String table, String select, String column, String value) {
        return query("SELECT " + select + " FROM " + table + " WHERE " + column + "=\"" + value + "\"");
    }

    public void update(String table, String column, String val, String where, String value) {
        execute("UPDATE " + table + " SET " + column + " = " + val + " WHERE " + where  + " = \"" + value + "\"");
    }

    public ResultSet query(String query) {
        AtomicReference<ResultSet> rs = new AtomicReference<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Statement statement = connection.createStatement();
                rs.set(statement.executeQuery(query));
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return rs.get();
    }

    private void bracketQueryBuilder(String[] columns, StringBuilder sb) {
        sb.append("(");
        boolean comma = false;
        for (String column : columns) {
            if (comma) {
                sb.append(", ").append(column);
            } else {
                sb.append(column);
            }
            comma = true;
        }
        sb.append(")");
    }
}
