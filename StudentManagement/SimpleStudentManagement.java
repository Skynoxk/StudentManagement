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
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root","Password");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addStudent(String id, String name, float grade, String major) {
        try {
            Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            studentmanage.executeUpdate("INSERT INTO students (id, name, grade, major) VALUES ('" + id + "', '" + name + "', " + grade + ", '" + major + "')");
            studentmanage.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStudent(String id, String name, float grade, String major) {
        try {
        	Connection conn = connection();
            Statement studentmanage = conn.createStatement();
            studentmanage.executeUpdate("UPDATE students SET name='" + name + "', grade=" + grade + ", major='" + major + "' WHERE id='" + id + "'");
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
                System.out.println(result.getString("id") + " | " + result.getString("name") + " | " + result.getFloat("grade") + " | " + result.getString("major"));
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
                System.out.println(result.getString("id") + " | " + result.getString("name") + " | " + result.getFloat("grade") + " | " + result.getString("major"));
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
            System.out.println("5. Search Student (Search by Name");
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
                    System.out.print("Grade: ");
                    float grade = scanner.nextFloat();
                    scanner.nextLine();
                    System.out.print("Major: ");
                    String major = scanner.nextLine();
                    addStudent(id, name, grade, major);
                    break;
                case 2:
                    System.out.print("ID to update: ");
                    id = scanner.nextLine();
                    System.out.print("New Name: ");
                    name = scanner.nextLine();
                    System.out.print("New Grade: ");
                    grade = scanner.nextFloat();
                    scanner.nextLine();
                    System.out.print("New Major: ");
                    major = scanner.nextLine();
                    updateStudent(id, name, grade, major);
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
