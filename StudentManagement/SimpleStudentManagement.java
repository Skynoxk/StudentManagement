package StudentManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SimpleStudentManagement {

	    public static Connection connection() {
	        try {
	            return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root","Daly030105@");
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    // Add students function (Passwords will be hashed when input)
	    public static void addStudent(String id, String name, String major, String gender, 
	            String birthdate, String address, String department, String username, String password) {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();

	            // Check if the fields are empty and handle NULL values
	            id = id.isEmpty() ? "NULL" : "'" + id + "'";
	            name = name.isEmpty() ? "NULL" : "'" + name + "'";
	            major = major.isEmpty() ? "NULL" : "'" + major + "'";
	            gender = gender.isEmpty() ? "NULL" : "'" + gender + "'";
	            birthdate = birthdate.isEmpty() ? "NULL" : "'" + birthdate + "'";
	            address = address.isEmpty() ? "NULL" : "'" + address + "'";
	            department = department.isEmpty() ? "NULL" : "'" + department + "'";
	            username = username.isEmpty() ? "NULL" : "'" + username + "'";
	            password = password.isEmpty() ? "NULL" : "'" + PasswordMD5.hashPassword(password) + "'";

	            String query = "INSERT INTO students (id, name, major, gender, birthdate, address, department, username, password) "
	                    + "VALUES (" + id + ", " + name + ", " + major + ", " + gender + ", " + birthdate + ", "
	                    + address + ", " + department + ", " + username + ", " + password + ")";
	            
	            studentmanage.executeUpdate(query);
	            studentmanage.close();
	            conn.close();
	            System.out.println("Add successfully");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Add course and point the calculated average grade from course_grades to grade in students table
	    // course_grades table and students table is in the same database called student
	    public static void addCourseGrade(String id, String courseName, float courseGrade) {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();

	            id = id.isEmpty() ? "NULL" : "'" + id + "'";
	            courseName = courseName.isEmpty() ? "NULL" : "'" + courseName + "'";

	            if (courseGrade < 0 || courseGrade > 100) {
	                courseGrade = 0;
	            }
	            
	            String query = "INSERT INTO course_grades (id, course_name, course_grade) "
	                    + "VALUES (" + id + ", " + courseName + ", " + courseGrade + ")";
	            
	            studentmanage.executeUpdate(query);

	            float newAverage = calculateAverageGrade(id);
	            studentmanage.executeUpdate("UPDATE students SET grade = " + newAverage + " WHERE id = '" + id + "'");

	            studentmanage.close();
	            conn.close();
	            System.out.println("Add successfully");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Delete course based on ID input and recalculate the new grades
	    public static void deleteCourseGrade(String id, String courseName) {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();

	            // Delete the course grade
	            studentmanage.executeUpdate("DELETE FROM course_grades WHERE id='" + id 
	                    + "' AND course_name='" + courseName + "'");

	            // Recalculate the new average grade after deletion
	            float newAverage = calculateAverageGrade(id);
	            studentmanage.executeUpdate("UPDATE students SET grade = " + newAverage + " WHERE id = '" + id + "'");

	            studentmanage.close();
	            conn.close();
	            System.out.println("Course grade deleted successfully.");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Update everything including password. Password is still hashed after input
	    public static void updateStudent(String id, String name, String major, String gender, 
	            String birthdate, String address, String department, String username, String password) {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();

	            name = name.isEmpty() ? "NULL" : "'" + name + "'";
	            major = major.isEmpty() ? "NULL" : "'" + major + "'";
	            gender = gender.isEmpty() ? "NULL" : "'" + gender + "'";
	            birthdate = birthdate.isEmpty() ? "NULL" : "'" + birthdate + "'";
	            address = address.isEmpty() ? "NULL" : "'" + address + "'";
	            department = department.isEmpty() ? "NULL" : "'" + department + "'";
	            username = username.isEmpty() ? "NULL" : "'" + username + "'";
	            password = password.isEmpty() ? "NULL" : "'" + PasswordMD5.hashPassword(password) + "'";

	            String query = "UPDATE students SET name=" + name + ", major=" + major + ", gender=" + gender 
	                    + ", birthdate=" + birthdate + ", address=" + address + ", department=" + department 
	                    + ", username=" + username + ", password=" + password + " WHERE id=" + "'" + id + "'";
	            
	            studentmanage.executeUpdate(query);
	            studentmanage.close();
	            conn.close();
	            System.out.println("Update successfully");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Delete everything about the student even including course grades
	    // Have to delete course grades first before you can delete students table due to primary key constraint
	    public static void deleteStudent(String id) {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();

	            // First, delete related course grades
	            studentmanage.executeUpdate("DELETE FROM course_grades WHERE id='" + id + "'");

	            // Now, delete the student
	            studentmanage.executeUpdate("DELETE FROM students WHERE id='" + id + "'");

	            studentmanage.close();
	            conn.close();
	            System.out.println("Student deleted successfully.");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // View everything except passwords
	    public static void viewStudents() {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();
	            ResultSet result = studentmanage.executeQuery("SELECT * FROM students ORDER BY name");
	            while (result.next()) {
	                System.out.println(result.getString("id") + " | " + result.getString("name") 
	                        + " | " + result.getString("username") + " | " + result.getString("grade") 
	                        + " | " + result.getString("major") + " | " + result.getString("gender") 
	                        + " | " + result.getString("birthdate") + " | " + result.getString("address") 
	                        + " | " + result.getString("department"));
	            }
	            result.close();
	            studentmanage.close();
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // View everything
	    public static void viewCourseGrades() {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();
	            ResultSet result = studentmanage.executeQuery("SELECT * FROM course_grades ORDER BY id, course_name");

	            while (result.next()) {
	                System.out.println(result.getString("id") + " | " + result.getString("course_name") 
	                        + " | " + result.getFloat("course_grade"));
	            }

	            result.close();
	            studentmanage.close();
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Calculate the average of the scores
	    public static float calculateAverageGrade(String id) {
	        float averageGrade = 0;
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();
	            ResultSet result = studentmanage.executeQuery("SELECT AVG(course_grade) "
	                    + "AS avg_grade FROM course_grades WHERE id='" + id + "'");
	            if (result.next()) {
	                averageGrade = result.getFloat("avg_grade");
	            }
	            result.close();
	            studentmanage.close();
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return averageGrade;
	    }

	    // Search student using name
	    public static void searchStudent(String name) {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();
	            ResultSet result = studentmanage.executeQuery("SELECT * FROM students WHERE name LIKE '%" + name + "%'");
	            while (result.next()) {
	                System.out.println(result.getString("id") + " | " + result.getString("name") 
	                        + " | " + result.getString("username") + " | " + result.getString("grade") 
	                        + " | " + result.getString("major") + " | " + result.getString("gender") 
	                        + " | " + result.getString("birthdate") + " | " + result.getString("address") 
	                        + " | " + result.getString("department"));
	            }
	            result.close();
	            studentmanage.close();
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Search course grade of the student
	    public static void searchCourseGrade(String courseName) {
	        try {
	            Connection conn = connection();
	            Statement studentmanage = conn.createStatement();
	            ResultSet result = studentmanage.executeQuery("SELECT * FROM course_grades WHERE course_name LIKE '%" + courseName + "%'");
	            while (result.next()) {
	                System.out.println(result.getString("id") + " | " + result.getString("course_name") 
	                        + " | " + result.getFloat("course_grade"));
	            }
	            result.close();
	            studentmanage.close();
	            conn.close();
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
            System.out.println("5. Remove Student");
            System.out.println("6. View Students (Sorted by Name)");
            System.out.println("7. Search Student (Search by Name)");
            System.out.println("8. View Course Grades (Sorted by Student ID)");
            System.out.println("9. Search Course Grade (Search by Course Name)");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    System.out.print("Major: ");
                    String major = scanner.nextLine();
                    System.out.print("Gender: ");
                    String gender = scanner.nextLine();
                    System.out.print("Birth Date: ");
                    String birthdate = scanner.nextLine();
                    System.out.print("Address: ");
                    String address = scanner.nextLine();
                    System.out.print("Department: ");
                    String department = scanner.nextLine();
                    addStudent(id, name, major, gender, birthdate, address, department, username, password);
                    break;
                case 2:
                    System.out.print("ID: ");
                    id = scanner.nextLine();
                    System.out.print("Course Name: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Course Grade: ");
                    float courseGrade = scanner.nextFloat();
                    scanner.nextLine();
                    addCourseGrade(id, courseName, courseGrade);
                    break;
                case 3:
                    System.out.print("ID: ");
                    id = scanner.nextLine();
                    System.out.print("Course Name: ");
                    courseName = scanner.nextLine();
                    deleteCourseGrade(id, courseName);
                    break;
                case 4:
                    System.out.print("ID to update: ");
                    id = scanner.nextLine();
                    System.out.print("New Name: ");
                    name = scanner.nextLine();
                    System.out.print("New Username: ");
                    username = scanner.nextLine();
                    System.out.print("New Password: ");
                    password = scanner.nextLine();
                    System.out.print("New Major: ");
                    major = scanner.nextLine();
                    System.out.print("New Gender: ");
                    gender = scanner.nextLine();
                    System.out.print("New Birth date: ");
                    birthdate = scanner.nextLine();
                    System.out.print("New Address: ");
                    address = scanner.nextLine();
                    System.out.print("New Department: ");
                    department = scanner.nextLine();
                    updateStudent(id, name, major, gender, birthdate, address, department, username, password);
                    break;
                case 5:
                    System.out.print("ID to delete: ");
                    id = scanner.nextLine();
                    deleteStudent(id);
                    break;
                case 6:
                    viewStudents();
                    break;
                case 7:
                    System.out.print("Search by Name: ");
                    name = scanner.nextLine();
                    searchStudent(name);
                    break;
                case 8:
                    viewCourseGrades();
                    break;
                case 9:
                    System.out.print("Enter Course Name: ");
                    courseName = scanner.nextLine();
                    searchCourseGrade(courseName); 
                    break;
                case 10:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}

