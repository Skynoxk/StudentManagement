package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

public class DeleteCourseGradeController implements Initializable {
	
	@FXML
    private TextField idField, courseNameField;
	
	@FXML
    private ImageView welcomeImage;
	
	@FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> colID;
    @FXML
    private TableColumn<Student, String> colCourseName;
    @FXML
    private TableColumn<Student, String> colCourseGrade;
	
    private ObservableList<Student> studentsList = FXCollections.observableArrayList();
    
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
                String query = "SELECT * FROM course_grades ORDER BY id, course_grade";
                try (Connection conn = Database.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet result = pstmt.executeQuery()) {
                    while (result.next()) {
                        tempList.add(new Student(
                            result.getString("id"),
                            result.getString("course_name"),
                            result.getString("course_grade") 
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
    
	public static Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Luchh@2006");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	@FXML
    private void deleteCourseGrade(ActionEvent event) {
        String id = idField.getText();
        String courseName = courseNameField.getText();

        if (id.isEmpty() || courseName.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }
        

        deleteCourseGradeFromDatabase(id, courseName);
        showAlert("Success", "Student deleted successfully!");
        idField.clear();
        courseNameField.clear();
    }
	
	private static void deleteCourseGradeFromDatabase(String id, String courseName) {
		try {
            Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            
            studentmanage.executeUpdate("DELETE FROM course_grades WHERE id='" + id 
            		+ "' AND course_name='" + courseName + "'");

            float newAverage = calculateAverageGrade(id);
            studentmanage.executeUpdate("UPDATE students SET grade = " + newAverage + " WHERE id = '" + id + "'");

            studentmanage.close();
            conn.close();
            System.out.println("Course grade deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public static float calculateAverageGrade(String id) {
        float averageGrade = 0;
        try {
            Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            ResultSet result = studentmanage.executeQuery("SELECT AVG(course_grade) "
            		+ "AS avg_grade FROM course_grades WHERE id='" + id + "'");
            if (result.next()) {
                averageGrade = result.getFloat("avg_grade");
            }
            result.close();
            studentmanage.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return averageGrade;
    }
	
	private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

}
