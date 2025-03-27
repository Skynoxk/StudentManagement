package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class AddstudentController {

    @FXML
    private TextField idField, nameField, majorField, genderField, birthdateField, addressField, departmentField, usernameField, passwordField, roleField;
    
    public static Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Luchh@2006");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @FXML
    private ImageView welcomeImage;
    
    public void initialize() {
        try {
        	welcomeImage.setImage(new Image(getClass().getResourceAsStream("/images/Hero-Graphic-1.png")));
        } catch (NullPointerException e) {
            System.out.println("Error: Image not found! Check the path.");
            e.printStackTrace();
        }
    }

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
        String role = roleField.getText().trim().toLowerCase();

        if (id.isEmpty() || name.isEmpty() || major.isEmpty() || gender.isEmpty() ||
            birthdate.isEmpty() || address.isEmpty() || department.isEmpty() ||
            username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }
        if (!role.equals("user") && !role.equals("admin")) {
            showAlert("Error", "Role must be either 'user' or 'admin'!");
            return;
        }

        addStudentToDatabase(id, name, major, gender,
    			 birthdate, address, department,
    			 username, password, role);
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
        roleField.clear();
    }
    
    String role = "user";
    private static void addStudentToDatabase(String id, String name, String major, String gender,
			String birthdate, String address, String department,
			String username, String password, String role) {
			String query = "INSERT INTO students (id, name, major, gender, birthdate, address, department, username, password, role) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			try (Connection conn = connection();
			PreparedStatement pstmt = conn.prepareStatement(query)) {
			
			pstmt.setString(1, id.isEmpty() ? null : id);
			pstmt.setString(2, name.isEmpty() ? null : name);
			pstmt.setString(3, major.isEmpty() ? null : major);
			pstmt.setString(4, gender.isEmpty() ? null : gender);
			pstmt.setString(5, birthdate.isEmpty() ? null : birthdate);
			pstmt.setString(6, address.isEmpty() ? null : address);
			pstmt.setString(7, department.isEmpty() ? null : department);
			pstmt.setString(8, username.isEmpty() ? null : username);
			pstmt.setString(9, password.isEmpty() ? null : PasswordMD5.hashPassword(password));
			pstmt.setString(10, role.isEmpty() ? "user" : role); 
			
			pstmt.executeUpdate();
			System.out.println("Added " + role + " successfully ");
			
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
