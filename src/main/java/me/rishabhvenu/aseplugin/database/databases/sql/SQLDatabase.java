package me.rishabhvenu.aseplugin.database.databases.sql;

import java.sql.*;
import java.util.Map;

public abstract class SQLDatabase {
    private Connection connection;

    public SQLDatabase(String host, int port, String database, String username, String password, String driver,
                       String connectionname) {
        try {
            Class.forName(driver);
            this.connection = DriverManager.getConnection("jdbc:" + connectionname + "://"  + host + ":" +
                    port + "/" + database + "?autoReconnect=true", username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean tableExists(String table) {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet rs = dbm.getTables(null, null, table, null);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet select(String table, String select, String column, String value) {
        return query("SELECT " + select + " FROM " + table + " WHERE " + column + "=\"" + value + "\"");
    }

    public void update(String table, String column, String val, String where, String value) {
        execute("UPDATE " + table + " SET " + column + " = " + val + " WHERE " + where  + " = \"" + value + "\"");
    }

    public ResultSet query(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
