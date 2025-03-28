package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SummaryReportController {

    @FXML
    private PieChart pieChartMajor; 
    
    @FXML
    private PieChart pieChartGender; 

    public void initialize() {
        loadData();
    }

    @FXML
    private void loadData() {
        String totalQuery = "SELECT COUNT(*) AS total FROM students";
        String majorQuery = "SELECT major, COUNT(*) AS count FROM students GROUP BY major";
        String genderQuery = "SELECT gender, COUNT(*) AS count FROM students GROUP BY gender";

        ObservableList<PieChart.Data> pieDataMajor = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> pieDataGender = FXCollections.observableArrayList();

        try (Connection conn = getConnection();
             PreparedStatement totalStmt = conn.prepareStatement(totalQuery);
             PreparedStatement majorStmt = conn.prepareStatement(majorQuery);
             PreparedStatement genderStmt = conn.prepareStatement(genderQuery)) {

            int totalStudents = 0;
            try (ResultSet totalResult = totalStmt.executeQuery()) {
                if (totalResult.next()) {
                    totalStudents = totalResult.getInt("total");
                }
            }

            try (ResultSet majorResult = majorStmt.executeQuery()) {
                while (majorResult.next()) {
                    String major = majorResult.getString("major");
                    int count = majorResult.getInt("count");
                    double percentage = (count / (double) totalStudents) * 100;
                    pieDataMajor.add(new PieChart.Data(major + " (" + count + " - " + String.format("%.2f", percentage) + "%)", count));
                }
            }

            try (ResultSet genderResult = genderStmt.executeQuery()) {
                while (genderResult.next()) {
                    String gender = genderResult.getString("gender");
                    int count = genderResult.getInt("count");
                    double percentage = (count / (double) totalStudents) * 100;
                    pieDataGender.add(new PieChart.Data(gender + " (" + count + " - " + String.format("%.2f", percentage) + "%)", count));
                }
            }

            pieChartMajor.setData(pieDataMajor);
            pieChartGender.setData(pieDataGender);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/student";
        String user = "root";
        String password = "Luchh@2006"; 
        return DriverManager.getConnection(url, user, password);
    }
}
