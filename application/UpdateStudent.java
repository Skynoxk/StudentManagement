package application;

import java.net.URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import application.ViewStudentController.Database;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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

public class UpdateStudent implements Initializable {
	
	@FXML
    private TextField idField, nameField, majorField, genderField, birthdateField, addressField, departmentField, usernameField, passwordField;
	
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
    private void updateStudent(ActionEvent event) {
        String id = idField.getText().trim();
        String name = nameField.getText();
        String major = majorField.getText();
        String gender = genderField.getText();
        String birthdate = birthdateField.getText();
        String address = addressField.getText();
        String department = departmentField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (id.isEmpty() || name.isEmpty() || major.isEmpty() || gender.isEmpty() ||
                birthdate.isEmpty() || address.isEmpty() || department.isEmpty() ||
                username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "All fields must be filled!");
                return;
        }
        if (!studentExists(id)) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "No student found with ID: " + id);
            return;
        }

        	deleteCourseGradeFromDatabase(id, name, major, gender, birthdate, address, department, username, password);
            showAlert("Success", "Updated student successfully!");
            idField.clear();
            nameField.clear();
            majorField.clear();
            genderField.clear();
            birthdateField.clear();
            addressField.clear();
            departmentField.clear();
            usernameField.clear();
            passwordField.clear();
    }
	private boolean studentExists(String studentID) {
	    String query = "SELECT COUNT(*) FROM students WHERE id = ?";
	    
	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setString(1, studentID);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next() && rs.getInt(1) > 0) {
	            return true; 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; 
	}
	private void showAlert(Alert.AlertType alertType, String title, String message) {
	    Alert alert = new Alert(alertType);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}
	
	private static void deleteCourseGradeFromDatabase(String id, String name, String major, String gender,
            String birthdate, String address, String department,
            String username, String password) {
			if (id == null || id.isEmpty()) {
			System.out.println("Error: Student ID cannot be empty.");
			return;
			}
			
			String query = "UPDATE students SET name = ?, major = ?, gender = ?, birthdate = ?, "
			+ "address = ?, department = ?, username = ?, password = ? WHERE id = ?";
			
			try (Connection conn = connection();
			PreparedStatement pstmt = conn.prepareStatement(query)) {
			
			pstmt.setString(1, name.isEmpty() ? null : name);
			pstmt.setString(2, major.isEmpty() ? null : major);
			pstmt.setString(3, gender.isEmpty() ? null : gender);
			pstmt.setString(4, birthdate.isEmpty() ? null : birthdate);
			pstmt.setString(5, address.isEmpty() ? null : address);
			pstmt.setString(6, department.isEmpty() ? null : department);
			pstmt.setString(7, username.isEmpty() ? null : username);
			pstmt.setString(8, password.isEmpty() ? null : PasswordMD5.hashPassword(password));
			pstmt.setString(9, id);
			
			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
			System.out.println("Student updated successfully.");
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

}
