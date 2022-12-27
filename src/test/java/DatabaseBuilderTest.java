import com.tsquare.speakfriend.database.Builder;
import com.tsquare.speakfriend.database.connection.SqliteConnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.*;

@Order(1)
public class DatabaseBuilderTest {
    @Test
    void canBuildUsersTable() throws SQLException {
        Connection connection = SqliteConnection.getConnection(true);
        Builder builder = new Builder(connection);
        builder.createUsersTable();
    }

    @Test
    void canBuildAccountsTable() throws SQLException {
        Connection connection = SqliteConnection.getConnection(true);
        Builder builder = new Builder(connection);
        builder.createAccountsTable();
    }

    @Test
    void canBuildUserSettingsTable() throws SQLException {
        Connection connection = SqliteConnection.getConnection(true);
        Builder builder = new Builder(connection);
        builder.createUserSettingsTable();
    }

    @Test
    void canCreateSystemSettingsTable() throws SQLException {
        Connection connection = SqliteConnection.getConnection(true);
        Builder builder = new Builder(connection);
        builder.createSystemSettingsTable();
    }

    @Test
    void canRenameAndDropTables() throws SQLException {
        Connection connection = SqliteConnection.getConnection(true);

        String sql = """
            create table test_table
            (
                id   INTEGER
                    primary key autoincrement
            );
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();

        Builder builder = new Builder(connection);
        builder.renameTable("test_table", "renamed");

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, "renamed", null);
        resultSet.next();

        Assertions.assertEquals(1, resultSet.getRow());

        resultSet.close();

        builder.dropTable("renamed");
    }
}
