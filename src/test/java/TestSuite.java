import com.tsquare.speakfriend.database.connection.SqliteConnection;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class TestSuite {
    static File dbFile;

    static void setup() {
        try {
            dbFile = File.createTempFile("speakfriend-testing", null);

            SqliteConnection.getConnection(dbFile.getAbsolutePath());
        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void tearDown() {
        dbFile.delete();
    }
}
