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

public class TeacherDashboardController {

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
            stage.setTitle("Teacher Dashboard");
        } else {
            System.out.println("Warning: Scene is not set yet!");
        }
    }
    
    @FXML
    private void showAddStudentForm(ActionEvent event) {
        loadFXML("AddStudent.fxml");
    }

    @FXML
    private void showAddCourseGradeForm(ActionEvent event) {  
        loadFXML("AddCourseGrade.fxml");
    }
    
    @FXML
    private void showRemoveCourseGradeForm(ActionEvent event) {  
        loadFXML("DeleteCourseGrade.fxml");
    }
    
    @FXML
    private void showUpdateStudentForm(ActionEvent event) {  
        loadFXML("UpdateStudent.fxml");
    }
    
    @FXML
    private void showDeleteStudentForm(ActionEvent event) {  
        loadFXML("DeleteStudent.fxml");
    }
    
    @FXML
    private void showViewStudentForm(ActionEvent event) {  
        loadFXML("ViewStudent.fxml");
    }
    
    @FXML
    private void showSearchStudentForm(ActionEvent event) {  
        loadFXML("SearchStudent.fxml");
    }
    
    @FXML
    private void showViewCourseGradeForm(ActionEvent event) {  
        loadFXML("ViewCourseGrade.fxml");
    }
    
    @FXML
    private void showSearchCourseGradeForm(ActionEvent event) {  
        loadFXML("SearchCourseGrade.fxml");
    }
    
    @FXML
    private void showSummaryReportForm(ActionEvent event) {  
        loadFXML("SummaryReport.fxml");
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
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