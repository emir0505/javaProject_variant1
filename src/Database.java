import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private final Connection connection;

    public Database(String dbPath, String csvPath)
            throws ClassNotFoundException, SQLException, IOException, CsvValidationException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        if (csvPath == null)
            return;
        var data = Parser.parse(csvPath);
        var statement = connection.createStatement();
        statement.setQueryTimeout(30);
        statement.executeUpdate("DROP TABLE IF EXISTS country");
        statement.executeUpdate("""
                CREATE TABLE country (
                    name TEXT PRIMARY KEY NOT NULL,
                    region TEXT,
                    happiness_rank INTEGER,
                    happiness_score REAL,
                    standard_error REAL,
                    economy REAL,
                    family REAL,
                    health REAL,
                    freedom REAL,
                    trust REAL,
                    generosity REAL,
                    dystopia_residual REAL
                )""");
        var preparedStatement = connection.prepareStatement("""
                INSERT INTO country 
   (name, region, happiness_rank, happiness_score, standard_error, economy, family, health, freedom, trust, generosity, dystopia_residual)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");
        for (var row : data) {
            preparedStatement.setString(1, row[0]);
            preparedStatement.setString(2, row[1]);
            preparedStatement.setInt(3, Integer.parseInt(row[2]));
            preparedStatement.setDouble(4, Double.parseDouble(row[3]));
            preparedStatement.setDouble(5, Double.parseDouble(row[4]));
            preparedStatement.setDouble(6, Double.parseDouble(row[5]));
            preparedStatement.setDouble(7, Double.parseDouble(row[6]));
            preparedStatement.setDouble(8, Double.parseDouble(row[7]));
            preparedStatement.setDouble(9, Double.parseDouble(row[8]));
            preparedStatement.setDouble(10, Double.parseDouble(row[9]));
            preparedStatement.setDouble(11, Double.parseDouble(row[10]));
            preparedStatement.setDouble(12, Double.parseDouble(row[11]));
            preparedStatement.executeUpdate();
        }
        preparedStatement.close();
        statement.close();
    }

    public ResultSet doQuery(String query) throws SQLException {
        var statement = connection.createStatement();
        statement.setQueryTimeout(30);
        return statement.executeQuery(query);
    }
}
