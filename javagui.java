import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnectionDemo extends JFrame {

    private JTextField urlField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton connectButton;

    public DatabaseConnectionDemo() {
        setTitle("Database Connection Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new GridLayout(4, 2));

        JLabel urlLabel = new JLabel("Database URL:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        urlField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        connectButton = new JButton("Connect to Database");

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (connectToDatabase(url, username, password)) {
                    // Database connection successful, insert records here
                    insertRecords();
                }
            }
        });

        add(urlLabel);
        add(urlField);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Empty cell for spacing
        add(connectButton);

        setVisible(true);
    }

    private boolean connectToDatabase(String url, String username, String password) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                System.out.println("Connected to the database.");
                return true;
            } else {
                System.out.println("Failed to connect to the database.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void insertRecords() {
        String url = urlField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false);

            long startTimeWithoutBatch = System.currentTimeMillis();
            insertRecordsWithoutBatch(connection);
            long endTimeWithoutBatch = System.currentTimeMillis();
            connection.commit();
            connection.setAutoCommit(true);

            connection.setAutoCommit(false);
            long startTimeWithBatch = System.currentTimeMillis();
            insertRecordsWithBatch(connection);
            long endTimeWithBatch = System.currentTimeMillis();
            connection.commit();
            connection.setAutoCommit(true);

            long timeWithoutBatch = endTimeWithoutBatch - startTimeWithoutBatch;
            long timeWithBatch = endTimeWithBatch - startTimeWithBatch;

            System.out.println("Time taken without batch: " + timeWithoutBatch + " milliseconds");
            System.out.println("Time taken with batch: " + timeWithBatch + " milliseconds");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertRecordsWithoutBatch(Connection connection) throws SQLException {
        String insertSql = "INSERT INTO Temp (num1, num2, num3) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSql);

        for (int i = 0; i < 1000; i++) {
            double num1 = Math.random();
            double num2 = Math.random();
            double num3 = Math.random();

            preparedStatement.setDouble(1, num1);
            preparedStatement.setDouble(2, num2);
            preparedStatement.setDouble(3, num3);

            preparedStatement.executeUpdate();
        }
    }

    private void insertRecordsWithBatch(Connection connection) throws SQLException {
        String insertSql = "INSERT INTO Temp (num1, num2, num3) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSql);

        for (int i = 0; i < 1000; i++) {
            double num1 = Math.random();
            double num2 = Math.random();
            double num3 = Math.random();

            preparedStatement.setDouble(1, num1);
            preparedStatement.setDouble(2, num2);
            preparedStatement.setDouble(3, num3);

            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DatabaseConnectionDemo();
            }
        });
    }
}
