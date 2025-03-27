package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class DeleteStudentController implements Initializable{
	
	@FXML
    private TextField idField;
	
	@FXML
    private ImageView welcomeImage;
	
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
                        // Ensure course_grade is stored as a string
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
    
	public static Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Luchh@2006");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	@FXML
    private void deleteStudent(ActionEvent event) {
        String id = idField.getText();
        
        if (id.isEmpty()) {
                showAlert("Error", "All fields must be filled!");
                return;
            }

        deleteStudentFromDatabase(id);
            showAlert("Success", "Student added successfully!");
            idField.clear();
    }
	
	private static void deleteStudentFromDatabase(String id) {
        if (id == null || id.isEmpty()) {
            System.out.println("Error: Student ID cannot be empty.");
            return;
        }

        String deleteGradesQuery = "DELETE FROM course_grades WHERE id = ?";
        String deleteStudentQuery = "DELETE FROM students WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmtGrades = conn.prepareStatement(deleteGradesQuery);
             PreparedStatement pstmtStudent = conn.prepareStatement(deleteStudentQuery)) {

            // Delete related course grades first
            pstmtGrades.setString(1, id);
            pstmtGrades.executeUpdate();

            // Delete student record
            pstmtStudent.setString(1, id);
            int rowsDeleted = pstmtStudent.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("No student found with the given ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            return pstmt.executeQuery(); // Caller must close the ResultSet and Connection
        }
    }

}
