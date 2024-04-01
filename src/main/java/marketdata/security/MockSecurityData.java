package marketdata.security;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MockSecurityData {
    private final String _securityFilePath;
    private final Connection _dbConnection;
    private final String[] SQL_SCRIPTS = {
            "marketdata/db/security_schema.sql",
    };
    private static final String INSERT_SQL = "INSERT INTO securities (ticker, identifier, type, maturity_date, strike_price, current_price, STOCK_VOLATILITY, STOCK_RETURN) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public MockSecurityData(Connection dbConnection_, String securityFilePath_) {
        _dbConnection = dbConnection_;
        _securityFilePath = securityFilePath_;
    }

    public void mockData() {
        try (Connection conn = _dbConnection) {
            //Create table
            executeSqlScripts(conn);
            //Read and load security data in table
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(_securityFilePath);
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

            String[] line;
            // Skip header line if present
            if (reader.readNext() == null) {
                System.out.println("Empty file or invalid format.");
                return;
            }

            // Prepare SQL statement for inserting data into the securities table
            try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_SQL)) {
                // Read each line from the CSV file and insert data into the securities table
                while ((line = reader.readNext()) != null) {
                    // Assuming CSV columns order is: ticker, identifier, type, maturity_date, strike_price, current_price, volatility, return_on_stock
                    preparedStatement.setString(1, line[1]); // ticker
                    preparedStatement.setString(2, line[0]); // identifier
                    preparedStatement.setString(3, line[2]); // type
                    preparedStatement.setDate(4, java.sql.Date.valueOf(line[3])); // maturity_date
                    preparedStatement.setBigDecimal(5, new BigDecimal(line[4])); // strike_price
                    preparedStatement.setBigDecimal(6, new BigDecimal(line[5])); // current_price
                    preparedStatement.setBigDecimal(7, new BigDecimal(line[6])); // volatility
                    preparedStatement.setBigDecimal(8, new BigDecimal(line[7])); // return_on_stock
                    preparedStatement.executeUpdate();
                }
                conn.commit();
            }

            System.out.println("Data inserted successfully.");

        } catch (IOException | SQLException e_) {
            System.out.println("Error inserting data in DB:" + e_);
            throw new RuntimeException(e_);
        } catch (CsvValidationException e_) {
            System.out.println("Error inserting data in DB, Invalid CSV file:" + e_);
            throw new RuntimeException(e_);
        }
    }

    private void executeSqlScripts(Connection connection) throws SQLException, IOException {
        for (String script : SQL_SCRIPTS) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 Statement statement = connection.createStatement()) {
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sql.append(line);
                }
                statement.executeUpdate(sql.toString());
                connection.commit();
            }
        }
    }
}
