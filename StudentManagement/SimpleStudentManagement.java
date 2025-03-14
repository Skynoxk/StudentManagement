package StudentManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SimpleStudentManagement {

    public static Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Daly030105@");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add students function (Passwords will be hashed when input)
    public static void addStudent(String id, String name, String major, String gender,
                                  String birthdate, String address, String department,
                                  String username, String password) {
        String query = "INSERT INTO students (id, name, major, gender, birthdate, address, department, username, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            pstmt.executeUpdate();
            System.out.println("Added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add course and point the calculated average grade from course_grades to grade in students table
    // course_grades table and students table is in the same database called student
    public static void addCourseGrade(String id, String courseName, float courseGrade) {
        String insertQuery = "INSERT INTO course_grades (id, course_name, course_grade) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE students SET grade = ? WHERE id = ?";

        try (Connection conn = connection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Validate ID and course name
            if (id.isEmpty() || courseName.isEmpty()) {
                System.out.println("Error: ID and course name cannot be empty.");
                return;
            }

            // Ensure course grade is within range
            if (courseGrade < 0 || courseGrade > 100) {
                courseGrade = 0;
            }

            // Insert into course_grades table
            insertStmt.setString(1, id);
            insertStmt.setString(2, courseName);
            insertStmt.setFloat(3, courseGrade);
            insertStmt.executeUpdate();

            // Recalculate the new average grade for the student
            float newAverage = calculateAverageGrade(id);

            // Update the student's grade in students table
            updateStmt.setFloat(1, newAverage);
            updateStmt.setString(2, id);
            updateStmt.executeUpdate();

            System.out.println("Course grade added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete course based on ID input and recalculate the new grades
    public static void deleteCourseGrade(String id, String courseName) {
        String deleteQuery = "DELETE FROM course_grades WHERE id = ? AND course_name = ?";
        String updateQuery = "UPDATE students SET grade = ? WHERE id = ?";

        try (Connection conn = connection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Validate ID and course name
            if (id.isEmpty() || courseName.isEmpty()) {
                System.out.println("Error: ID and course name cannot be empty.");
                return;
            }

            // Delete the course grade
            deleteStmt.setString(1, id);
            deleteStmt.setString(2, courseName);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Recalculate the new average grade after deletion
                float newAverage = calculateAverageGrade(id);

                // Update the student's grade in the students table
                updateStmt.setFloat(1, newAverage);
                updateStmt.setString(2, id);
                updateStmt.executeUpdate();

                System.out.println("Course grade deleted successfully.");
            } else {
                System.out.println("No matching course grade found to delete.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update everything including password. Password is still hashed after input
    public static void updateStudent(String id, String name, String major, String gender,
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

    // Delete everything about the student even including course grades
    public static void deleteStudent(String id) {
        if (id == null || id.isEmpty()) {
            System.out.println("Error: Student ID cannot be empty.");
            return;
        }

        String deleteGradesQuery = "DELETE FROM course_grades WHERE id = ?";
        String deleteStudentQuery = "DELETE FROM students WHERE id = ?";

        try (Connection conn = connection();
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

    // View everything except passwords
    public static void viewStudents() {
        String query = "SELECT * FROM students ORDER BY id";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery()) {

            while (result.next()) {
                System.out.println(result.getString("id") + " | " + result.getString("name")
                        + " | " + result.getString("username") + " | " + result.getString("grade")
                        + " | " + result.getString("major") + " | " + result.getString("gender")
                        + " | " + result.getString("birthdate") + " | " + result.getString("address")
                        + " | " + result.getString("department"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View everything
    public static void viewCourseGrades() {
        String query = "SELECT * FROM course_grades ORDER BY id";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery()) {

            while (result.next()) {
                System.out.println(result.getString("id") + " | " + result.getString("course_name")
                        + " | " + result.getFloat("course_grade"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calculate the average of the scores
    public static float calculateAverageGrade(String id) {
        float averageGrade = 0;
        String query = "SELECT AVG(course_grade) AS avg_grade FROM course_grades WHERE id = ?";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);
            try (ResultSet result = pstmt.executeQuery()) {
                if (result.next()) {
                    averageGrade = result.getFloat("avg_grade");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return averageGrade;
    }

    // Search student using name
    public static void searchStudent(String name) {
        String query = "SELECT * FROM students WHERE name LIKE ?";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + name + "%");
            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) {
                    System.out.println(result.getString("id") + " | " + result.getString("name")
                            + " | " + result.getString("username") + " | " + result.getString("grade")
                            + " | " + result.getString("major") + " | " + result.getString("gender")
                            + " | " + result.getString("birthdate") + " | " + result.getString("address")
                            + " | " + result.getString("department"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search course grade of the student
    public static void searchCourseGrade(String courseName) {
        String query = "SELECT * FROM course_grades WHERE course_name LIKE ?";

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + courseName + "%");
            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) {
                    System.out.println(result.getString("id") + " | " + result.getString("course_name")
                            + " | " + result.getFloat("course_grade"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nStudent Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Add Course Grade");
            System.out.println("3. Remove Course Grade");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. View Students (Ascending order by ID)");
            System.out.println("7. View Course Grades (Ascending order by ID)");
            System.out.println("8. Search Student");
            System.out.println("9. Search Course Grade");
            System.out.println("10. Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    // Add Student
                    System.out.println("Enter student details:");
                    System.out.print("ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Major: ");
                    String major = scanner.nextLine();
                    System.out.print("Gender: ");
                    String gender = scanner.nextLine();
                    System.out.print("Birthdate (YYYY-MM-DD): ");
                    String birthdate = scanner.nextLine();
                    System.out.print("Address: ");
                    String address = scanner.nextLine();
                    System.out.print("Department: ");
                    String department = scanner.nextLine();
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    addStudent(id, name, major, gender, birthdate, address, department, username, password);
                    break;

                case 2:
                    // Add Course Grade
                    System.out.print("Enter student ID: ");
                    String studentId = scanner.nextLine();
                    System.out.print("Enter course name: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Enter course grade: ");
                    float courseGrade = scanner.nextFloat();
                    scanner.nextLine();  // Consume the newline
                    addCourseGrade(studentId, courseName, courseGrade);
                    break;

                case 3:
                    // Remove Course Grade
                    System.out.print("Enter student ID: ");
                    String removeId = scanner.nextLine();
                    System.out.print("Enter course name to remove: ");
                    String removeCourse = scanner.nextLine();
                    deleteCourseGrade(removeId, removeCourse);
                    break;

                case 4:
                    // Update Student
                    System.out.print("Enter student ID to update: ");
                    String updateId = scanner.nextLine();
                    System.out.print("Enter new name (leave empty to skip): ");
                    String updateName = scanner.nextLine();
                    System.out.print("Enter new major (leave empty to skip): ");
                    String updateMajor = scanner.nextLine();
                    System.out.print("Enter new gender (leave empty to skip): ");
                    String updateGender = scanner.nextLine();
                    System.out.print("Enter new birthdate (leave empty to skip): ");
                    String updateBirthdate = scanner.nextLine();
                    System.out.print("Enter new address (leave empty to skip): ");
                    String updateAddress = scanner.nextLine();
                    System.out.print("Enter new department (leave empty to skip): ");
                    String updateDepartment = scanner.nextLine();
                    System.out.print("Enter new username (leave empty to skip): ");
                    String updateUsername = scanner.nextLine();
                    System.out.print("Enter new password (leave empty to skip): ");
                    String updatePassword = scanner.nextLine();
                    updateStudent(updateId, updateName, updateMajor, updateGender, updateBirthdate, updateAddress,
                            updateDepartment, updateUsername, updatePassword);
                    break;

                case 5:
                    // Delete Student
                    System.out.print("Enter student ID to delete: ");
                    String deleteId = scanner.nextLine();
                    deleteStudent(deleteId);
                    break;

                case 6:
                    // View Students
                    viewStudents();
                    break;

                case 7:
                    // View Course Grades
                    viewCourseGrades();
                    break;

                case 8:
                    // Search Student
                    System.out.print("Enter student name to search: ");
                    String searchName = scanner.nextLine();
                    searchStudent(searchName);
                    break;

                case 9:
                    // Search Course Grade
                    System.out.print("Enter course name to search: ");
                    String searchCourse = scanner.nextLine();
                    searchCourseGrade(searchCourse);
                    break;

                case 10:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option, try again.");
                    break;
            }
        }
    }
}
