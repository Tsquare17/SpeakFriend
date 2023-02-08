import com.tsquare.speakfriend.database.schema.Builder;
import com.tsquare.speakfriend.database.connection.SqliteConnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.*;

@Order(1)
public class DatabaseBuilderTest {
    @BeforeAll
    static void setup() {
        TestSuite.setup();
    }

    @Test
    void canBuildUsersTable() throws SQLException {
        Builder builder = new Builder();
        builder.createUsersTable();
    }

    @Test
    void canBuildAccountsTable() throws SQLException {
        Builder builder = new Builder();
        builder.createAccountsTable();
    }

    @Test
    void canBuildUserSettingsTable() throws SQLException {
        Builder builder = new Builder();
        builder.createUserSettingsTable();
    }

    @Test
    void canCreateSystemSettingsTable() throws SQLException {
        Builder builder = new Builder();
        builder.createSystemSettingsTable();
    }

    @Test
    void canRenameAndDropTables() throws SQLException {
        Connection connection = SqliteConnection.getConnection();

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

        Builder builder = new Builder();
        builder.renameTable("test_table", "renamed");

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, "renamed", null);
        resultSet.next();

        Assertions.assertEquals(1, resultSet.getRow());

        resultSet.close();

        builder.dropTable("renamed");
        builder.close();
    }
}
