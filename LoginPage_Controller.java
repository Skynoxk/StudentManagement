import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FXMLController {
    @FXML
    private TextField usernameLogin;
    
    @FXML
    private PasswordField passwordLogin;

    private final String DB_URL = "jdbc:mysql://localhost:3306/student?useSSL=false&serverTimezone=UTC";
    private final String DB_USER = "root";  
    private final String DB_PASSWORD = "admin123"; 

    @FXML
    public void login(ActionEvent event) {
        String username = usernameLogin.getText();
        String password = passwordLogin.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username or Password cannot be empty!");
            return;
        }

        String role = validateLogin(username, password);
        if (role == null) {
            showAlert("Error", "Invalid Username or Password!");
        } else if (role.equals("admin")) {
            loadDashboard(event, "AdminDashboard.fxml");  
        } else {
            loadDashboard(event, "UserDashboard.fxml");
        }
    }

    private String validateLogin(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // ✅ Load MySQL Driver
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT role FROM users WHERE username=? AND password=?"; 
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");  // Return user role
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }

    private void loadDashboard(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) usernameLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
