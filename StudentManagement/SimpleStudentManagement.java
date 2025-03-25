package StudentManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public static void addStudent(String id, String name, String major, String gender,
            String birthdate, String address, String department,
            String username, String password, String role) {

    	// Validate the role input
    	if (!role.equalsIgnoreCase("user") && !role.equalsIgnoreCase("admin")) {
        	System.out.println("Failed to add student: Invalid role provided. Role must be 'user' or 'admin'.");
        	return; // Exit the method gracefully
    	}

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
        	pstmt.setString(10, role);
        
        	pstmt.executeUpdate();
        	System.out.println("Added " + role + " successfully.");

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
    public static void deleteStudent(String id, Scanner scanner) {  // Pass Scanner as a parameter
        searchStudent(id);  // Show student details before deletion

        System.out.print("Are you sure you want to delete this student? (yes/no): ");
        String confirm = scanner.nextLine();  // Use existing Scanner

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Deletion canceled.");
            return;
        }

        String deleteGradesQuery = "DELETE FROM course_grades WHERE id = ?";
        String deleteStudentQuery = "DELETE FROM students WHERE id = ?";

        try (Connection conn = connection();
             PreparedStatement pstmtGrades = conn.prepareStatement(deleteGradesQuery);
             PreparedStatement pstmtStudent = conn.prepareStatement(deleteStudentQuery)) {

            pstmtGrades.setString(1, id);
            pstmtGrades.executeUpdate();  // Delete associated course grades

            pstmtStudent.setString(1, id);
            int rowsDeleted = pstmtStudent.executeUpdate(); // Delete student

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

            // Print header
            System.out.printf("%-10s | %-20s | %-15s | %-5s | %-15s | %-8s | %-12s | %-25s | %-15s%n", 
                              "ID", "Name", "Username", "Grade", "Major", "Gender", "Birthdate", "Phone address", "Department");
            System.out.println("-----------------------------------------------------------------------------------------------------------");

            // Print each row
            while (result.next()) {
                System.out.printf("%-10s | %-20s | %-15s | %-5s | %-15s | %-8s | %-12s | %-25s | %-15s%n", 
                                  result.getString("id"), 
                                  result.getString("name"), 
                                  result.getString("username"), 
                                  result.getString("grade"), 
                                  result.getString("major"), 
                                  result.getString("gender"), 
                                  result.getString("birthdate"), 
                                  result.getString("address"), 
                                  result.getString("department"));
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

            // Print header
            System.out.printf("%-10s | %-20s | %-10s%n", "ID", "Course Name", "Grade");
            System.out.println("------------------------------------------------------");

            // Print each row
            while (result.next()) {
                System.out.printf("%-10s | %-20s | %-10.2f%n", 
                                  result.getString("id"), 
                                  result.getString("course_name"), 
                                  result.getFloat("course_grade"));
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
    public static void searchStudent(String keyword) {
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
                    System.out.println(result.getString("id") + " | " + result.getString("name")
                            + " | " + result.getString("username") + " | " + result.getString("grade")
                            + " | " + result.getString("major") + " | " + result.getString("gender")
                            + " | " + result.getString("birthdate") + " | " + result.getString("address")
                            + " | " + result.getString("department"));
                }
                if (!found) {
                    System.out.println("No matching students found.");
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
    
    public static void summaryReport() {
        String totalQuery = "SELECT COUNT(*) AS total FROM students";
        String majorQuery = "SELECT major, COUNT(*) AS count FROM students GROUP BY major";
        String genderQuery = "SELECT gender, COUNT(*) AS count FROM students GROUP BY gender";

        try (Connection conn = connection();
             PreparedStatement totalStmt = conn.prepareStatement(totalQuery);
             PreparedStatement majorStmt = conn.prepareStatement(majorQuery);
             PreparedStatement genderStmt = conn.prepareStatement(genderQuery)) {

            // Get the total number of students
            int totalStudents = 0;
            try (ResultSet totalResult = totalStmt.executeQuery()) {
                if (totalResult.next()) {
                    totalStudents = totalResult.getInt("total");
                }
            }

            System.out.println("\nSummary Report:");

         // Execute and process major query
            try (ResultSet majorResult = majorStmt.executeQuery()) {
                System.out.println("\nStudents per Major:");
                while (majorResult.next()) {
                    String major = majorResult.getString("major");
                    int count = majorResult.getInt("count");
                    double percentage = (count / (double) totalStudents) * 100;
                    String output = major + ": " + count + " students (" + ((int)(percentage * 100) / 100.0) + "%)";
                    System.out.println(output);
                }
            }

            // Execute and process gender query
            try (ResultSet genderResult = genderStmt.executeQuery()) {
                System.out.println("\nStudents per Gender:");
                while (genderResult.next()) {
                    String gender = genderResult.getString("gender");
                    int count = genderResult.getInt("count");
                    double percentage = (count / (double) totalStudents) * 100;
                    String output = gender + ": " + count + " students (" + ((int)(percentage * 100) / 100.0) + "%)";
                    System.out.println(output);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  
    public static void exportToCSV(String filepath, Scanner scanner) {
        // List available tables
        System.out.println("Available tables to export:");
        System.out.println("1. students");
        System.out.println("2. course_grades");

        // Ask user for choice
        int tableChoice = -1;
        while (tableChoice != 1 && tableChoice != 2) {
            System.out.print("Enter the number of the table you want to export (1 or 2): ");
            if (scanner.hasNextInt()) {
                tableChoice = scanner.nextInt();
                if (tableChoice != 1 && tableChoice != 2) {
                    System.out.println("Invalid choice! Please select 1 or 2.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        }
        scanner.nextLine(); // Consume newline character after the integer input

        String query = "";
        String header = "";
        if (tableChoice == 1) {
            query = "SELECT id, name, major, gender, birthdate, address, department, username, role FROM students";
            header = "ID,Name,Major,Gender,Birthdate,Address,Department,Username,Role";
        } else if (tableChoice == 2) {
            query = "SELECT id, course_name, course_grade FROM course_grades";
            header = "ID,Course Name,Course Grade";
        }

        // Ensure filename ends with .csv
        if (!filepath.toLowerCase().endsWith(".csv")) {
            filepath += ".csv";
        }

        // Export selected table to CSV (overwrite mode)
        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery();
             BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false))) { // false = overwrite

            // Write the header once
            writer.write(header);
            writer.newLine();

            // Write each record from the table to the CSV file
            while (result.next()) {
                if (tableChoice == 1) {
                    writer.write(result.getString("id") + "," +
                            result.getString("name") + "," +
                            result.getString("major") + "," +
                            result.getString("gender") + "," +
                            result.getString("birthdate") + "," +
                            result.getString("address") + "," +
                            result.getString("department") + "," +
                            result.getString("username") + "," +
                            result.getString("role"));
                } else {
                    writer.write(result.getString("id") + "," +
                            result.getString("course_name") + "," +
                            result.getFloat("course_grade"));
                }
                writer.newLine();
            }

            System.out.println("Data exported successfully to " + filepath);

        } catch (IOException | SQLException e) {
            System.out.println("Error during export: " + e.getMessage());
            e.printStackTrace();
        }
    }
  
    public static void importFromCSV(String filePath) {
        Scanner scanner = new Scanner(System.in);

        // List available tables
        System.out.println("Available tables to import into:");
        System.out.println("1. students");
        System.out.println("2. course_grades");

        // Ask user for choice
        System.out.print("Enter the number of the table you want to import into (1 or 2): ");
        int tableChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        String query = "";
        if (tableChoice == 1) {
            query = "INSERT INTO students (id, name, major, gender, birthdate, address, department, username, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else if (tableChoice == 2) {
            query = "INSERT INTO course_grades (id, course_name, course_grade) VALUES (?, ?, ?)";
        } else {
            System.out.println("Invalid choice. Exiting.");
            return;
        }

        // Check if the file exists
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        System.out.println("File path: " + filePath);

        // Import selected table from CSV
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             Connection conn = connection()) {

            String line;
            System.out.println("Importing data from CSV...");

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(","); // Split the CSV line into columns

                if (tableChoice == 1 && data.length == 9) { // Students table
                    // Manually trim fields
                    String id = data[0].trim();
                    String name = data[1].trim();
                    String major = data[2].trim();
                    String gender = data[3].trim();
                    String birthdate = data[4].trim();
                    String address = data[5].trim();
                    String department = data[6].trim();
                    String username = data[7].trim();
                    String role = data[8].trim();

                    // Check for duplicate entry before insertion
                    String checkQuery = "SELECT COUNT(*) FROM students WHERE id = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                        checkStmt.setString(1, id);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                System.out.println("Duplicate entry for ID: " + id);
                                
                                // Validation: If ID equals "ID", delete it
                                if ("ID".equals(id)) {
                                    String deleteQuery = "DELETE FROM students WHERE id = ?";
                                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                                        deleteStmt.setString(1, id);
                                        int deletedRows = deleteStmt.executeUpdate();
                                        if (deletedRows > 0) {
                                            System.out.println("Deleted ID: " + id);
                                        }
                                    }
                                }

                                continue; // Skip this row
                            }
                        }
                    }

                    // Insert into students table
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, id.isEmpty() ? null : id);
                        pstmt.setString(2, name.isEmpty() ? null : name);
                        pstmt.setString(3, major.isEmpty() ? null : major);
                        pstmt.setString(4, gender.isEmpty() ? null : gender);
                        pstmt.setString(5, birthdate.isEmpty() ? null : birthdate);
                        pstmt.setString(6, address.isEmpty() ? null : address);
                        pstmt.setString(7, department.isEmpty() ? null : department);
                        pstmt.setString(8, username.isEmpty() ? null : username);
                        pstmt.setString(9, role.isEmpty() ? null : role);
                        pstmt.executeUpdate();
                        System.out.println("Successfully inserted: ID=" + id);
                    }
                } else if (tableChoice == 2 && data.length == 3) { // course_grades table
                    // Manually trim fields
                    String studentId = data[0].trim();
                    String courseName = data[1].trim();
                    String courseGradeStr = data[2].trim();

                    float courseGrade = 0;
                    if (!courseGradeStr.isEmpty()) {
                        try {
                            courseGrade = Float.parseFloat(courseGradeStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid course grade for student ID " + studentId + ". Skipping this row.");
                            continue;
                        }
                    }

                    // Check for duplicate entry before insertion
                    String checkDuplicateQuery = "SELECT COUNT(*) FROM course_grades WHERE id = ? AND course_name = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkDuplicateQuery)) {
                        checkStmt.setString(1, studentId);
                        checkStmt.setString(2, courseName);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                System.out.println("Duplicate entry found for ID and Course Name: " + studentId + ", " + courseName);
                                continue; // Skip this row
                            }
                        }
                    }

                    // Insert into course_grades table
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, studentId);
                        pstmt.setString(2, courseName);
                        pstmt.setFloat(3, courseGrade);
                        pstmt.executeUpdate();
                    }
                } else {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
            }

            System.out.println("Data imported successfully from " + filePath);

        } catch (IOException | SQLException e) {
            System.out.println("Error during import: " + e.getMessage());
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
            System.out.println("6. View Students");
            System.out.println("7. View Course Grades");
            System.out.println("8. Search Student");
            System.out.println("9. Search Course Grade");
            System.out.println("10. Summary Report");
            System.out.println("11. Export Data to CSV");
            System.out.println("12. Import Data from CSV");
            System.out.println("13. Exit");

            System.out.print("Select an option: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();  // Clear invalid input
                continue;
            }
            
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    // Add Student
                	viewStudents();
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
                    System.out.print("Phone Address: ");
                    String address = scanner.nextLine();
                    System.out.print("Department: ");
                    String department = scanner.nextLine();
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    System.out.print("Role (admin/user): ");
                    String role = scanner.nextLine();
                    addStudent(id, name, major, gender, birthdate, address, department, username, password, role);
                    break;

                case 2:
                    // Add Course Grade
                	viewCourseGrades();
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
                	viewCourseGrades();
                    System.out.print("Enter student ID: ");
                    String removeId = scanner.nextLine();
                    System.out.print("Enter course name to remove: ");
                    String removeCourse = scanner.nextLine();
                    deleteCourseGrade(removeId, removeCourse);
                    break;

                case 4:
                    // Update Student
                	viewStudents();
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
                    System.out.print("Enter new Phone address (leave empty to skip): ");
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
                	viewStudents();
                    System.out.print("Enter student ID to delete: ");
                    String deleteId = scanner.nextLine();
                    deleteStudent(deleteId, scanner);
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
                    System.out.print("Enter student name/major/department/phone address/id to search: ");
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
                    // Generate Summary Report
                    summaryReport();
                    break;

                case 11:
                    // Export Data to CSV
                    System.out.print("Enter filename to export: ");
                    String exportFile = scanner.nextLine();
                    exportToCSV(exportFile, scanner);
                    break;

                case 12:
                    // Import Data from CSV
                    System.out.print("Enter filename to import: ");
                    String importFile = scanner.nextLine();
                    importFromCSV(importFile);
                    break;

                case 13:
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

//- More bug but I can't find it yet
//- Address in database is now treat as phone number.
