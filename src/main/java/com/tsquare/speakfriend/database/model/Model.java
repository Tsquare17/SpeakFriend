package com.tsquare.speakfriend.database.model;

import com.tsquare.speakfriend.database.connection.SqliteConnection;
import com.tsquare.speakfriend.database.model.relationship.JoinTable;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

abstract public class Model {
    Connection connection = SqliteConnection.getConnection();

    Statement statement = null;

    PreparedStatement preparedStatement = null;

    String select = "select * from " + this.getTableName() + " ";

    String groupBy = null;

    public void setSelect(String select) {
        this.select = select;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public Model() throws SQLException {}

    abstract String getTableName();

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }

        if (preparedStatement != null && !preparedStatement.isClosed()) {
            preparedStatement.close();
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void reset() throws SQLException {
        this.groupBy = null;

        if (statement != null && !statement.isClosed()) {
            statement.close();
        }

        if (preparedStatement != null && !preparedStatement.isClosed()) {
            preparedStatement.close();
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();

            connection = SqliteConnection.getConnection();
        }
    }

    protected ResultSet get() throws SQLException {
        String sql = "select * from " + getTableName();

        statement = connection.createStatement();

        return statement.executeQuery(sql);
    }

    protected ResultSet get(String column, String value) throws SQLException {
        String sql = select + "where " + column + " = ?";

        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, value);

        return preparedStatement.executeQuery();
    };

    protected ResultSet get(String column, int value) throws SQLException {
        String sql = select + "where " + column + " = ?";

        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, value);

        return preparedStatement.executeQuery();
    };

    protected ResultSet get(HashMap<String, Object> columnValueMap) throws SQLException {
        StringBuilder sql = new StringBuilder(select + "where 1=1 ");
        for (var entry : columnValueMap.entrySet()) {
            sql.append("and ").append(entry.getKey()).append(" = ? ");
        }

        preparedStatement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            columnValueMap
        );

        return preparedStatement.executeQuery();
    }

    protected ResultSet getJoin(HashMap<String, Object> columnValueMap, List<JoinTable> joinTables) throws SQLException {
        StringBuilder sql = new StringBuilder(select + "");

        for (var joinTable : joinTables) {
            sql.append("left join ").append(joinTable.getTable()).append(" on ")
                .append(joinTable.getKey()).append("=")
                .append(joinTable.getRelatedKey()).append(" ");
        }

        sql.append("where 1=1 ");

        for (var entry : columnValueMap.entrySet()) {
            sql.append("and ").append(entry.getKey()).append(" = ? ");
        }

        if (this.groupBy != null) {
            sql.append("group by ").append(this.groupBy);
        }

        preparedStatement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            columnValueMap
        );

        return preparedStatement.executeQuery();
    }

    protected ResultSet getIn(String column, List<Integer> values) throws SQLException {
        StringBuilder sql = new StringBuilder(select + "where " + column + " in (");

        HashMap<String, Object> hashMap = new HashMap<>();
        int length = values.size();
        int counter = 0;
        for (Integer id : values) {
            counter++;

            sql.append("?");

            if (counter != length) {
                sql.append(", ");
            }

            hashMap.put(id.toString(), id);
        }

        sql.append(")");

        preparedStatement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            hashMap
        );

        return preparedStatement.executeQuery();
    }

    protected int insert(HashMap<String, Object> columnValueMap) throws SQLException {
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

        preparedStatement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            columnValueMap
        );

        int rows = preparedStatement.executeUpdate();

        if (rows == 0) {
            return 0;
        }

        int key = 0;
        try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
            if (keys.next()) {
                key = keys.getInt(1);
            }
        }

        return key;
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

        preparedStatement = initUpdateStatement(
            connection.prepareStatement(sql.toString()),
            updateColumnValueMap,
            whereColumnValueMap
        );

        preparedStatement.executeUpdate();
    }

    protected void delete(int id) throws SQLException {
        String sql = "delete from " + getTableName() + " where id = ?";

        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, id);

        preparedStatement.executeUpdate();
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

        preparedStatement = initPreparedStatement(
            connection.prepareStatement(sql.toString()),
            columnValueMap
        );

        preparedStatement.executeUpdate();
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
