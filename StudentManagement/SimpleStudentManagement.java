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
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root","password");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addStudent(String id, String name, float grade, String major, String gender, String birthdate, String address, String department, String username, String password) {
        try {
            Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            String hashedPassword = PasswordMD5.hashPassword(password);
            studentmanage.executeUpdate("INSERT INTO students (id, name, grade, major, gender, birthdate, address, department, username, password) "
                    + "VALUES ('" + id + "', '" + name + "', " + grade + ", '" + major + "', '" + gender + "', '" + birthdate
                    + "', '" + address + "', '" + department + "', '" + username + "', '" + hashedPassword + "')");
            studentmanage.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStudent(String id, String name, float grade, String major, String gender, String birthdate, String address, String department, String username, String password) {
        try {
        	Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            String hashedPassword = PasswordMD5.hashPassword(password);
            studentmanage.executeUpdate("UPDATE students SET name='" + name + "', grade=" + grade + 
                    ", major='" + major + "', gender='" + gender + "', birthdate='" + birthdate + "', address='" + address + 
                    "', department='" + department + "', username='" + username + "', password='" + hashedPassword + "' WHERE id='" + id + "'");
            studentmanage.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteStudent(String id) {
        try {
        	Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            studentmanage.executeUpdate("DELETE FROM students WHERE id='" + id + "'");
            studentmanage.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewStudents() {
        try {
        	Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            ResultSet result = studentmanage.executeQuery("SELECT * FROM students ORDER BY name");
            while (result.next()) {
                System.out.println(result.getString("id") 
                		+ " | " + result.getString("name") 
                		+ " | " + result.getString("username")
                		+ " | " + result.getFloat("grade") 
                		+ " | " + result.getString("major")
                		+ " | " + result.getString("gender") 
                		+ " | " + result.getString("birthdate") 
                		+ " | " + result.getString("address")
                		+ " | " + result.getString("department")
                		);
            }
            result.close();
            studentmanage.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void searchStudent(String name) {
        try {
        	Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            ResultSet result = studentmanage.executeQuery("SELECT * FROM students WHERE name LIKE '%" + name + "%'");
            while (result.next()) {
                System.out.println(result.getString("id") 
                		+ " | " + result.getString("name") 
                		+ " | " + result.getString("username")
                		+ " | " + result.getFloat("grade") 
                		+ " | " + result.getString("major")
                		+ " | " + result.getString("gender") 
                		+ " | " + result.getString("birthdate") 
                		+ " | " + result.getString("address")
                		+ " | " + result.getString("department")
                		);
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
            System.out.println("2. Update Student");
            System.out.println("3. Remove Student");
            System.out.println("4. View Students (Sorted by Name)");
            System.out.println("5. Search Student (Search by Name)");
            System.out.println("6. Exit");
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
                    System.out.print("Grade: ");
                    float grade = scanner.nextFloat();
                    scanner.nextLine();
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
                    addStudent(id, name, grade, major, gender, birthdate, address, department, username, password);
                    break;
                case 2:
                    System.out.print("ID to update: ");
                    id = scanner.nextLine();
                    System.out.print("New Name: ");
                    name = scanner.nextLine();
                    System.out.print("New Username: ");
                    username = scanner.nextLine();
                    System.out.print("New Password: ");
                    password = scanner.nextLine();
                    System.out.print("New Grade: ");
                    grade = scanner.nextFloat();
                    scanner.nextLine();
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
                    updateStudent(id, name, grade, major, gender, birthdate, address, department, username, password);
                    break;
                case 3:
                    System.out.print("ID to delete: ");
                    id = scanner.nextLine();
                    deleteStudent(id);
                    break;
                case 4:
                    viewStudents();
                    break;
                case 5:
                    System.out.print("Search by Name: ");
                    name = scanner.nextLine();
                    searchStudent(name);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
