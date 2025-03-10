package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import StudentManagment.PasswordMD5;
import javafx.scene.Parent;

public class TeacherDashboardController {

    @FXML
    private AnchorPane mainContent;
    
    @FXML
	private AnchorPane addStudent;
    
    @FXML
    private TextField idField, nameField, usernameField, passwordField, majorField, genderField, birthdateField, addressField, departmentField;

    @FXML
    private ImageView logoImageView;
    
    @FXML
    private void showAddStudentForm(ActionEvent event) {
        try {
            // Load AddStudent.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddStudent.fxml"));
            AnchorPane addStudentPane = loader.load();

            // Set contentArea to the loaded FXML
            mainContent.getChildren().setAll(addStudentPane);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Add Student form.");
        }
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
    public void initialize() {
        try {
            logoImageView.setImage(new Image(getClass().getResourceAsStream("/images/Logo-ITC.png")));
        } catch (NullPointerException e) {
            System.out.println("Error: Image not found! Check the path.");
            e.printStackTrace();
        }
        javafx.application.Platform.runLater(() -> setWindowTitle());
    }
    
    private void setWindowTitle() {
        if (mainContent.getScene() != null) {
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setTitle("Teacher Dashboard");
        } else {
            System.out.println("Warning: Scene is not set yet!");
        }
    }

    @FXML
    private void loadAddStudent(ActionEvent event) {
        try {
            AnchorPane addStudentPane = FXMLLoader.load(getClass().getResource("AddStudent.fxml"));
            mainContent.getChildren().setAll(addStudentPane);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Add Student form.");
        }
    }


    // Add Student Button Clicked
    @FXML
    private void addStudent(ActionEvent event) {
        String id = idField.getText();
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

        addStudentToDatabase(id, name, major, gender, birthdate, address, department, username, password);
        showAlert("Success", "Student added successfully!");
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

    private static void addStudentToDatabase(String id, String name, String major, String gender, String birthdate, String address, String department, String username, String password) {
        String sql = "INSERT INTO students (id, name, major, gender, birthdate, address, department, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, major);
            pstmt.setString(4, gender);
            pstmt.setString(5, birthdate);
            pstmt.setString(6, address);
            pstmt.setString(7, department);
            pstmt.setString(8, username);
            pstmt.setString(9, PasswordMD5.hashPassword(password));
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddStudent() {
        addStudent.setVisible(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}