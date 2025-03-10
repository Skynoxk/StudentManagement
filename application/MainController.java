package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class MainController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField pwdPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private ImageView backgroundImage;
    
    @FXML
    private ImageView logoImageView;

    @FXML
    public void initialize() {
        try {
            backgroundImage.setImage(new Image(getClass().getResourceAsStream("/images/School_Background_Framework.jpg")));
            logoImageView.setImage(new Image(getClass().getResourceAsStream("/images/Logo-ITC.png")));
        } catch (NullPointerException e) {
            System.out.println("Error: Image not found! Check the path.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String usernames = username.getText();
        String password = pwdPassword.getText();

        if (usernames.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in both fields.");
        } else if (usernames.equals("teacher") && password.equals("1234")) {
            openDashboard("TeacherDashboard.fxml");
        } else if (usernames.equals("student") && password.equals("1234")) {
            showAlert("Success", "Student login successful!");
            openDashboard("StudentDashboard.fxml");
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    private void openDashboard(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
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
