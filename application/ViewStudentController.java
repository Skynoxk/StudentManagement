package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewStudentController implements Initializable {

    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> colID;
    @FXML
    private TableColumn<Student, String> colName;
    @FXML
    private TableColumn<Student, String> colUsername;
    @FXML
    private TableColumn<Student, String> colGrade;
    @FXML
    private TableColumn<Student, String> colMajor;
    @FXML
    private TableColumn<Student, String> colGender;
    @FXML
    private TableColumn<Student, String> colBirthdate;
    @FXML
    private TableColumn<Student, String> colAddress;
    @FXML
    private TableColumn<Student, String> colDepartment;
    
    @FXML
    private javafx.scene.control.Button exportButton;

    @FXML
    private javafx.scene.control.Button importButton;
    
    private ObservableList<Student> studentsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadStudentData();
    }

    private void setupTableColumns() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colBirthdate.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
    }

    private void loadStudentData() {
    	Task<ObservableList<Student>> loadTask = new Task<ObservableList<Student>>() {
            @Override
            protected ObservableList<Student> call() throws Exception {
                ObservableList<Student> tempList = FXCollections.observableArrayList();
                String query = "SELECT * FROM students ORDER BY id";
                try (Connection conn = Database.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet result = pstmt.executeQuery()) {
                    while (result.next()) {
                        tempList.add(new Student(
                            result.getString("id"),
                            result.getString("name"),
                            result.getString("username"),
                            result.getString("grade"),
                            result.getString("major"),
                            result.getString("gender"),
                            result.getString("birthdate"),
                            result.getString("address"),
                            result.getString("department")
                        ));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert("Database Error", "Failed to retrieve student data."));
                }
                return tempList;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    studentsList.clear();
                    studentsList.addAll(getValue());
                    studentTable.setItems(studentsList);
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> showAlert("Error", "Failed to load data."));
            }
        };
        new Thread(loadTask).start();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Student {
        private String id, name, username, grade, major, gender, birthdate, address, department;

        public Student(String id, String name, String username, String grade, String major, String gender, String birthdate, String address, String department) {
            this.id = id;
            this.name = name;
            this.username = username;
            this.grade = grade;
            this.major = major;
            this.gender = gender;
            this.birthdate = birthdate;
            this.address = address;
            this.department = department;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getUsername() { return username; }
        public String getGrade() { return grade; }
        public String getMajor() { return major; }
        public String getGender() { return gender; }
        public String getBirthdate() { return birthdate; }
        public String getAddress() { return address; }
        public String getDepartment() { return department; }
    }

    public static class Database {
        private static final String URL = "jdbc:mysql://localhost:3306/student?useSSL=false&serverTimezone=UTC";
        private static final String USER = "root";
        private static final String PASSWORD = "Luchh@2006";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }

        public static ResultSet executeQuery(String query) throws SQLException {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            return pstmt.executeQuery();
        }
    }
    @FXML
    private void exportDataToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            exportToCSV(file.getAbsolutePath());
        }
    }
    
    private void exportToCSV(String filepath) {
    	String query = "SELECT id, name, grade, major, gender, birthdate, address, department, username FROM students";
        String header = "ID,Name,Username,Grade,Major,Gender,Birthdate,Address";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery();
             BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false))) { 

            writer.write(header);
            writer.newLine();

            while (result.next()) {
            	writer.write(result.getString("id") + "," +
                        result.getString("Name") + "," +
                        result.getString("Username") + "," +
                        result.getString("Grade") + "," +
                        result.getString("major") + "," +
                        result.getString("Gender") + "," +
                        result.getString("Birthdate") + "," +
                        result.getString("address"));
                writer.newLine();
            }

            System.out.println("Data exported successfully to " + filepath);
        } catch (IOException | SQLException e) {
            System.out.println("Error during export: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Method to check for duplicate entry in the database
    private boolean isDuplicate(Connection conn, String tableName, String columnName, String value) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, value);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if count > 0, meaning the entry exists
                }
            }
        }
        return false;
    }
    // Method to check for duplicate entry in the table
    private boolean isDuplicate(Connection conn, String tableName, String column1, String value1, String column2, String value2) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + column1 + " = ? AND " + column2 + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, value1);
            pstmt.setString(2, value2);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if count > 0
                }
            }
        }
        return false;
    }


    @FXML
    private void importDataFromCSV(javafx.event.ActionEvent event) {
    	
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File to Import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Show file selection dialog
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (file == null) {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "Please select a valid CSV file from the predefined directory.");
            return;
        }

        List<String> tableOptions = Arrays.asList("students");
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("students", tableOptions);
        choiceDialog.setTitle("Select Table");
        choiceDialog.setHeaderText("Choose the table to import data into:");
        choiceDialog.setContentText("Table:");

        Optional<String> result = choiceDialog.showAndWait();
        if (!result.isPresent()) {
            showAlert(Alert.AlertType.WARNING, "No Table Selected", "Please select a table.");
            return;
        }

        String selectedTable = result.get();
        String query = selectedTable.equals("students")
                ? "INSERT INTO students (id, name, major, gender, birthdate, address, department, username, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                : "INSERT INTO course_grades (id, course_name, course_grade) VALUES (?, ?, ?)";

        Task<Void> importTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (BufferedReader reader = new BufferedReader(new FileReader(file));
                     Connection conn = Database.getConnection()) { // Ensure Database.getConnection() is implemented

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split(",");

                        if (selectedTable.equals("students") && data.length == 9) {
                            if (isDuplicate(conn, "students", "id", data[0].trim())) {
                                updateMessage("Skipping duplicate student ID: " + data[0].trim());
                                continue;
                            }
                            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                                for (int i = 0; i < data.length; i++) {
                                    pstmt.setString(i + 1, data[i].trim().isEmpty() ? null : data[i].trim());
                                }
                                pstmt.executeUpdate();
                            }
                        } else if (selectedTable.equals("course_grades") && data.length == 3) {
                            if (isDuplicate(conn, "course_grades", "id", data[0].trim(), "course_name", data[1].trim())) {
                                updateMessage("Skipping duplicate entry for ID & Course: " + data[0].trim() + ", " + data[1].trim());
                                continue;
                            }
                            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                                pstmt.setString(1, data[0].trim());
                                pstmt.setString(2, data[1].trim());
                                pstmt.setFloat(3, Float.parseFloat(data[2].trim()));
                                pstmt.executeUpdate();
                            }
                        } else {
                            updateMessage("Skipping invalid line: " + line);
                        }
                    }

                } catch (IOException | SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Import Error", "Error during import: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        };
        
        importTask.setOnSucceeded(event2 -> showAlert(Alert.AlertType.INFORMATION, "Import Completed", "Data imported successfully!"));
        new Thread(importTask).start();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
