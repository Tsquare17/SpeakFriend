package com.tsquare.speakfriend.database.model;

import com.tsquare.speakfriend.database.connection.SqliteConnection;

import java.sql.*;
import java.util.HashMap;

abstract public class Model {
    Connection connection = SqliteConnection.getConnection();

    public Model() throws SQLException {}

    abstract String getTableName();

    public Connection getConnection() {
        return connection;
    }

    protected ResultSet get() throws SQLException {
        String sql = "select * from " + getTableName();

        Statement statement = connection.createStatement();

        return statement.executeQuery(sql);
    }

    protected ResultSet get(String column, String value) throws SQLException {
        String sql = "select * from " + getTableName() + " where " + column + " = ?";

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, value);

        return statement.executeQuery();
    };

    protected ResultSet get(String column, int value) throws SQLException {
        String sql = "select * from " + getTableName() + " where " + column + " = ?";

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, value);

        return statement.executeQuery();
    };

    protected ResultSet get(HashMap<String, Object> columnValueMap) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from " + getTableName() + " where 1=1 ");
        for (var entry : columnValueMap.entrySet()) {
            sql.append("and ").append(entry.getKey()).append(" = ? ");
        }

        PreparedStatement statement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            columnValueMap
        );

        return statement.executeQuery();
    }

    protected void insert(HashMap<String, Object> columnValueMap) throws SQLException {
        StringBuilder sql = new StringBuilder("insert into " + getTableName() + " (");

        int counter = 0;
        int columnLength = columnValueMap.size();
        for (var entry : columnValueMap.entrySet()) {
            counter++;

            sql.append(entry.getKey());

            if (counter != columnLength) {
                sql.append(", ");
            }
        }

        sql.append(") values (");

        counter = 0;
        for (var entry : columnValueMap.entrySet()) {
            counter++;

            sql.append("?");

            if (counter != columnLength) {
                sql.append(", ");
            }
        }

        sql.append(")");

        PreparedStatement statement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            columnValueMap
        );

        statement.executeUpdate();

        statement.close();
    }

    protected void update(
        HashMap<String, Object> updateColumnValueMap,
        HashMap<String, Object> whereColumnValueMap
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("update " + getTableName() + " set ");

        int counter = 0;
        int columnLength = updateColumnValueMap.size();
        for (var entry : updateColumnValueMap.entrySet()) {
            counter++;

            sql.append(entry.getKey()).append(" = ?");

            if (counter != columnLength) {
                sql.append(", ");
            }
        }

        sql.append(" where 1=1 and ");

        counter = 0;
        columnLength = whereColumnValueMap.size();
        for (var entry : whereColumnValueMap.entrySet()) {
            counter++;

            sql.append(entry.getKey()).append(" = ? ");

            if (counter != columnLength) {
                sql.append(" and ");
            }
        }

        PreparedStatement statement = initUpdateStatement(
            connection.prepareStatement(sql.toString()),
            updateColumnValueMap,
            whereColumnValueMap
        );

        statement.executeUpdate();

        statement.close();
    }

    protected void delete(int id) throws SQLException {
        String sql = "delete from " + getTableName() + " where id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, id);

        statement.executeUpdate();

        statement.close();;
    }

    protected void delete(HashMap<String, Object> columnValueMap) throws SQLException {
        StringBuilder sql = new StringBuilder("delete from " + getTableName() + " where ");

        int counter = 0;
        int columnLength = columnValueMap.size();
        for (var entry : columnValueMap.entrySet()) {
            counter++;

            String keyValue = entry.getKey() + " = ?";

            sql.append(keyValue);

            if (counter != columnLength) {
                sql.append(" and ");
            }
        }

        PreparedStatement statement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            columnValueMap
        );

        statement.executeUpdate();

        statement.close();
    }

    private PreparedStatement initPreparedStatement(
        PreparedStatement statement,
        HashMap<String, Object> columnValueMap
    ) throws SQLException {
        int counter = 0;
        for (var entry : columnValueMap.entrySet()) {
            counter++;

            if (entry.getValue() instanceof Integer) {
                statement.setInt(counter, (Integer) entry.getValue());
            } else if (entry.getValue() instanceof String) {
                statement.setString(counter, (String) entry.getValue());
            }
        }

        return statement;
    }

    private PreparedStatement initUpdateStatement(
        PreparedStatement statement,
        HashMap<String, Object> updateMap,
        HashMap<String, Object> whereMap
    ) throws SQLException {
        int counter = 0;
        for (var entry : updateMap.entrySet()) {
            counter++;

            if (entry.getValue() instanceof Integer) {
                statement.setInt(counter, (Integer) entry.getValue());
            } else if (entry.getValue() instanceof String) {
                statement.setString(counter, (String) entry.getValue());
            }
        }

        for (var entry : whereMap.entrySet()) {
            counter++;

            if (entry.getValue() instanceof Integer) {
                statement.setInt(counter, (Integer) entry.getValue());
            } else if (entry.getValue() instanceof String) {
                statement.setString(counter, (String) entry.getValue());
            }
        }

        return statement;
    }
}
