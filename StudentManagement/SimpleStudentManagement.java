package StudentManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SimpleStudentManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nStudent Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Update Student");
            System.out.println("3. Remove Student");
            System.out.println("4. View Students (Sorted by Name)");
            System.out.println("5. Search Student");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt(); 
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addStudent(scanner);
                    break;
                case 2:
                    updateStudent(scanner);
                    break;
                case 3:
                    deleteStudent(scanner);
                    break;
                case 4:
                    viewStudents();
                    break;
                case 5:
                    searchStudent(scanner);
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

    private static void addStudent(Scanner scanner) {
        System.out.print("ID: "); 
        String id = scanner.nextLine();
        System.out.print("Name: "); 
        String name = scanner.nextLine();
        System.out.print("Grade: "); 
        float grade = scanner.nextFloat();
        scanner.nextLine();
        System.out.print("Major: "); 
        String major = scanner.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Password Here");
            Statement studentmanage = conn.createStatement();
            studentmanage.executeUpdate("INSERT INTO students (id, name, grade, major) VALUES ('" + id + "', '" + name + "', " + grade + ", '" + major + "')");
            studentmanage.close();
            conn.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    private static void updateStudent(Scanner scanner) {
        System.out.print("ID to update: "); 
        String id = scanner.nextLine();
        System.out.print("New Name: "); 
        String name = scanner.nextLine();
        System.out.print("New Grade: "); 
        float grade = scanner.nextFloat(); 
        scanner.nextLine();
        System.out.print("New Major: "); 
        String major = scanner.nextLine();

        try {
        	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Password Here");
            Statement studentmanage = conn.createStatement();
            studentmanage.executeUpdate("UPDATE students SET name='" + name + "', grade=" + grade + ", major='" + major + "' WHERE id='" + id + "'");
            studentmanage.close();
            conn.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    private static void deleteStudent(Scanner scanner) {
        System.out.print("ID to delete: "); 
        String id = scanner.nextLine();
        try {
        	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Password Here");
            Statement studentmanage = conn.createStatement();
            studentmanage.executeUpdate("DELETE FROM students WHERE id='" + id + "'");
            studentmanage.close();
            conn.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    private static void viewStudents() {
        try {
        	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Password Here");
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

    private static void searchStudent(Scanner scanner) {
        System.out.print("Search by Name: "); 
        String name = scanner.nextLine();
        try {
        	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "Password Here");
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
}
