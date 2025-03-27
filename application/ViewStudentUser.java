package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewStudentUser implements Initializable {

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

    private ObservableList<Student> studentsList = FXCollections.observableArrayList();

    @FXML
    private ImageView welcomeImage;
    
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

    // Student class definition
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

    // Database class definition
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
            return pstmt.executeQuery(); // Caller must close the ResultSet and Connection
        }
    }

}
