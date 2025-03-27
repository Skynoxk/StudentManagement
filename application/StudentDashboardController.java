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
import javafx.scene.Parent;

public class StudentDashboardController {

    @FXML
    private AnchorPane mainContent;
    
    @FXML
    private TextField idField, nameField, usernameField, passwordField, majorField, genderField, birthdateField, addressField, departmentField, courseNameField, courseGradeField;;

    @FXML
    private ImageView logoImageView;
    
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
            stage.setTitle("Student Dashboard");
        } else {
            System.out.println("Warning: Scene is not set yet!");
        }
    }
    
    @FXML
    private void showViewStudentForm(ActionEvent event) {  
        loadFXML("ViewStudentUser.fxml");
    }
    
    @FXML
    private void showSearchStudentForm(ActionEvent event) {  
        loadFXML("SearchStudentUser.fxml");
    }
    
    @FXML
    private void showViewCourseGradeForm(ActionEvent event) {  
        loadFXML("ViewCourseGradeUser.fxml");
    }
    
    @FXML
    private void showSearchCourseGradeForm(ActionEvent event) {  
        loadFXML("SearchCourseGradeUser.fxml");
    }
    
    @FXML
    private void loadViewstudent(ActionEvent event) {
        try {
            AnchorPane addStudentPane = FXMLLoader.load(getClass().getResource("ViewStudentUser.fxml"));
            mainContent.getChildren().setAll(addStudentPane);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Add Student form.");
        }
    }
    
    @FXML
    private void loadSearchstudent(ActionEvent event) {
        try {
            AnchorPane addStudentPane = FXMLLoader.load(getClass().getResource("SearchStudentUser.fxml"));
            mainContent.getChildren().setAll(addStudentPane);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Search Student form.");
        }
    }
    
    @FXML
    private void loadViewCourseGrade(ActionEvent event) {
        try {
            AnchorPane addStudentPane = FXMLLoader.load(getClass().getResource("ViewCourseGradeUser.fxml"));
            mainContent.getChildren().setAll(addStudentPane);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Search Student form.");
        }
    }
    
    @FXML
    private void loadSearchCourseGrade(ActionEvent event) {
        try {
            AnchorPane addStudentPane = FXMLLoader.load(getClass().getResource("SearchCourseGradeUser.fxml"));
            mainContent.getChildren().setAll(addStudentPane);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Search Course Grades form.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
        	Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadFXML(String fileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
            AnchorPane pane = loader.load();
            mainContent.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load " + fileName);
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