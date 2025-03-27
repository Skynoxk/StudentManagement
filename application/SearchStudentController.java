package application;

import java.sql.Connection;

import javafx.scene.control.TableView;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class SearchStudentController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private Button searchButton;
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
    
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    
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
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colBirthdate.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        
        studentTable.setItems(studentList);
        loadAllCourseGrades();
    }
    
    private void loadAllCourseGrades() {
        String query = "SELECT * FROM students";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery()) {

            studentList.clear(); 

            while (result.next()) {
                studentList.add(new Student(result.getString("id"), 
                                            result.getString("name"), 
                                            result.getString("username"),
                                            result.getString("grade"),
                                            result.getString("major"),
                                            result.getString("gender"),
                                            result.getString("birthdate"),
                                            result.getString("address"),
                                            result.getString("department")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void searchStudentFromDatabase() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Error", "Please enter a search keyword!");
            return;
        }

        studentList.clear(); 

        String query = "SELECT * FROM students WHERE id LIKE ? OR name LIKE ? OR major LIKE ? OR department LIKE ? OR address LIKE ? ORDER BY id";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, "%" + keyword + "%");
            }

            try (ResultSet result = pstmt.executeQuery()) {
                boolean found = false;
                while (result.next()) {
                    found = true;
                    studentList.add(new Student(
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
                studentTable.setItems(studentList);

                if (!found) {
                    showAlert("Info", "No matching students found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error occurred!");
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        searchStudentFromDatabase();
    }

    // Student class definition
    public class Student {
        private final StringProperty id, name, username, grade, major, gender, birthdate, address, department;

        public Student(String id, String name, String username, String grade, String major, String gender, String birthdate, String address, String department) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.username = new SimpleStringProperty(username);
            this.grade = new SimpleStringProperty(grade);
            this.major = new SimpleStringProperty(major);
            this.gender = new SimpleStringProperty(gender);
            this.birthdate = new SimpleStringProperty(birthdate);
            this.address = new SimpleStringProperty(address);
            this.department = new SimpleStringProperty(department);
        }

        public StringProperty idProperty() { return id; }
        public StringProperty nameProperty() { return name; }
        public StringProperty usernameProperty() { return username; }
        public StringProperty gradeProperty() { return grade; }
        public StringProperty majorProperty() { return major; }
        public StringProperty genderProperty() { return gender; }
        public StringProperty birthdateProperty() { return birthdate; }
        public StringProperty addressProperty() { return address; }
        public StringProperty departmentProperty() { return department; }
        
        public String getId() { return id.get(); }
        public String getname() { return name.get(); }
        public String getusername() { return username.get(); }
        public String getgrade() { return grade.get(); }
        public String getmajor() { return major.get(); }
        public String getgender() { return gender.get(); }
        public String getbirthdate() { return birthdate.get(); }
        public String getaddress() { return address.get(); }
        public String getdepartment() { return department.get(); }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
