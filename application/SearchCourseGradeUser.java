package application;

import java.sql.Connection;
import javafx.scene.control.TableView;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchCourseGradeUser {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private Button searchButton;
    @FXML
    private TableColumn<Student, String> colID;
    @FXML
    private TableColumn<Student, String> colCourseName;
    @FXML
    private TableColumn<Student, String> colCourseGrade;
    
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    
    public static Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Luchh@2006");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName")); 
        colCourseGrade.setCellValueFactory(new PropertyValueFactory<>("courseGrade")); 

        studentTable.setItems(studentList);
    }

    
    private void searchFromDatabase() {
        String courseName = searchField.getText().trim();
        if (courseName.isEmpty()) {
            showAlert("Error", "Please enter a course name!");
            return;
        }

        studentList.clear();  

        String query = "SELECT * FROM course_grades WHERE course_name LIKE ?";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + courseName + "%");
            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) {
                    // Add each result to studentList
                    studentList.add(new Student(result.getString("id"), 
                                                result.getString("course_name"), 
                                                result.getString("course_grade")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        searchFromDatabase();  // Trigger search when button is clicked
    }

    public class Student {
        private final StringProperty id, courseName, courseGrade;

        public Student(String id, String courseName, String courseGrade) {
            this.id = new SimpleStringProperty(id);
            this.courseName = new SimpleStringProperty(courseName);
            this.courseGrade = new SimpleStringProperty(courseGrade);
        }

        public StringProperty idProperty() { return id; }
        public StringProperty courseNameProperty() { return courseName; } // Fix name
        public StringProperty courseGradeProperty() { return courseGrade; } // Fix name
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
