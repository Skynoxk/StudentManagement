/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package studentfx;
import java.time.LocalDate; 
import java.time.LocalTime; 
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author skynoxk
 */
public class MainController implements Initializable {
    @FXML
    private ImageView itclogo;

    @FXML
    private TextField usernameLogin;
    @FXML
    private PasswordField passwordLogin;
    
    private Scene scene;
    private Stage stage;
    private Parent root;
    private final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "admin123";

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MS = 60_000; // in milliseconds
    
    private static HashMap<String, Integer> loginAttempts = new HashMap<>();
    private static HashMap<String, Long> lockTime = new HashMap<>();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // translate
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(itclogo);
        translate.setDuration(javafx.util.Duration.millis(800));
        translate.setByY(25);
        translate.play();
    }    

    @FXML
    private void login(ActionEvent event) throws IOException {
        
      String username = usernameLogin.getText();
      String password = passwordLogin.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username or Password cannot be empty!");
            return;
        }

        // Check if the user is locked out
        if (lockTime.containsKey(username)) {
            long lockedTime = lockTime.get(username);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lockedTime < LOCK_TIME_MS) {
                showAlert("Error", "Too many failed attempts! Try again after 1 minute.");
                return;
            } else {
                // Reset lock status after time is over
                lockTime.remove(username);
                loginAttempts.put(username, 0);
            }
        }
         String role = validateLogin(username, password);

        if (role == null) {
            // Increase failed attempts
            loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);

            // If max attempts reached, lock the user
            if (loginAttempts.get(username) >= MAX_ATTEMPTS) {
                lockTime.put(username, System.currentTimeMillis());
                showAlert("Error", "Too many failed attempts! You are locked for 1 minute.");
            } else {
                showAlert("Error", "Invalid Username or Password! Attempts left: " + (MAX_ATTEMPTS - loginAttempts.get(username)));
            }
        } else {
            // Reset attempts on successful login
            loginAttempts.remove(username);
            lockTime.remove(username);

            if (role.equals("admin")) {
                loadDashboard(event, "AdminDashboard.fxml");
                
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();
                
                System.out.println("admin login"+ date + " " + time);
            } else {
                loadDashboard(event, "UserDashboard.fxml");
            }
        }
    }
    private String validateLogin(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT role FROM students WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
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
