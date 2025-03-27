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

public class ViewCourseGradeUser implements Initializable {

    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> colID;
    @FXML
    private TableColumn<Student, String> colCourseName;
    @FXML
    private TableColumn<Student, String> colCourseGrade;

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
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCourseGrade.setCellValueFactory(new PropertyValueFactory<>("courseGrade"));
    }

    private void loadStudentData() {
        Task<ObservableList<Student>> loadTask = new Task<ObservableList<Student>>() {
            @Override
            protected ObservableList<Student> call() throws Exception {
                ObservableList<Student> tempList = FXCollections.observableArrayList();
                String query = "SELECT * FROM course_grades ORDER BY id";
                try (Connection conn = Database.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet result = pstmt.executeQuery()) {
                    while (result.next()) {
                        // Ensure course_grade is stored as a string
                        tempList.add(new Student(
                            result.getString("id"),
                            result.getString("course_name"),
                            result.getString("course_grade") // Now keeping it as String for consistent handling
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
        private String id, courseName, courseGrade;

        public Student(String id, String courseName, String courseGrade) {
            this.id = id;
            this.courseName = courseName;
            this.courseGrade = courseGrade;
        }

        public String getId() { return id; }
        public String getCourseName() { return courseName; }
        public String getCourseGrade() { return courseGrade; }
    }

    // Database class definition
    public static class Database {
        private static final String URL = "jdbc:mysql://localhost:3306/student?useSSL=false&serverTimezone=UTC";
        private static final String USER = "root";
        private static final String PASSWORD = "Luchh@2006";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
}
