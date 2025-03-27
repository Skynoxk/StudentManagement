package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import application.ViewStudentController.Database;
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
import java.sql.PreparedStatement;

public class AddcourseController implements Initializable {

    @FXML
    private TextField idField, courseNameField, courseGradeField;
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
    
    public static Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Luchh@2006");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void addCourseGrade(ActionEvent event) {
        String id = idField.getText();
        String courseName = courseNameField.getText();
        String courseGradeString = courseGradeField.getText().trim();

        if (id.isEmpty() || courseName.isEmpty() || courseGradeString.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }
        
       if (!isStudentAndCourseExists(id)) {
          showAlert("Error", "Student ID not found in the system.");
           return;
       }
      
        try {
            float courseGrade = Float.parseFloat(courseGradeString); 
            
            if (courseGrade < 0 || courseGrade > 100) {
                showAlert("Error", "Course grade must be between 0 and 100.");
                return;
            }
            
            addCourseGradeToDatabase(id, courseName, courseGrade);
            System.out.println("Adding grade for student: " + id + " in course: " + courseName);
            
            showAlert("Success", "Added Course Grade successfully!");
            idField.clear();
            courseNameField.clear();
            courseGradeField.clear();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid grade (numeric value).");
        }
    }
    
    private boolean isStudentAndCourseExists(String id) {
        String query = "SELECT 1 FROM students WHERE id = ?";
        
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet result = stmt.executeQuery();
            
            return result.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void addCourseGradeToDatabase(String id, String courseName, float courseGrade) {
        String insertQuery = "INSERT INTO course_grades (id, course_name, course_grade) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE students SET grade = ? WHERE id = ?";

        try (Connection conn = connection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
        	
            insertStmt.setString(1, id);
            insertStmt.setString(2, courseName);
            insertStmt.setFloat(3, courseGrade);
            insertStmt.executeUpdate();

            float newAverage = calculateAverageGrade(id);

            updateStmt.setFloat(1, newAverage);
            updateStmt.setString(2, id);
            updateStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static float calculateAverageGrade(String id) {
    	float averageGrade = 0;
        String query = "SELECT AVG(new_course_grade) AS avg_grade FROM course_grades WHERE id = ?";
        
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                averageGrade = result.getFloat("avg_grade");
            }
            result.close();
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
}
