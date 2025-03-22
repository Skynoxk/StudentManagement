package studentfx;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainController implements Initializable {
    @FXML
    private ImageView itclogo;
    @FXML
    private Button loginBT;
    @FXML
    private TextField usernameLogin;
    @FXML
    private PasswordField passwordLogin;
    @FXML
    private Canvas gridCanvas;

    private final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "admin123";

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MS = 60_000;

    private static HashMap<String, Integer> loginAttempts = new HashMap<>();
    private static HashMap<String, Long> lockTime = new HashMap<>();

    private Random random = new Random();
    private Color[][] currentColors;
    private Color[][] targetColors;
    private long lastColorUpdateTime = 0;
    private static final long COLOR_UPDATE_INTERVAL = 100; // Update colors every 100 ms

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Logo animation
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(itclogo);
        translate.setDuration(javafx.util.Duration.millis(800));
        translate.setByY(25);
        translate.play();

        // Initialize the color arrays
        currentColors = new Color[10][10];  // Grid size (you can adjust this)
        targetColors = new Color[10][10];   // Target colors for slow transition

        // Fill the grid with initial random colors
        for (int i = 0; i < currentColors.length; i++) {
            for (int j = 0; j < currentColors[i].length; j++) {
                currentColors[i][j] = randomColor();
                targetColors[i][j] = randomColor(); // Target color for smooth transition
            }
        }

        // Grid animation with random colors for boxes and smooth color transitions
        if (gridCanvas != null) {
            GraphicsContext gc = gridCanvas.getGraphicsContext2D();
            new AnimationTimer() {
                private double offset = 0;
                private long lastUpdateTime = 0;

                @Override
                public void handle(long now) {
                    offset += 0.5;
                    long currentTime = now / 1000000;  // Convert nanoseconds to milliseconds

                    // Update colors every 100ms
                    if (currentTime - lastColorUpdateTime >= COLOR_UPDATE_INTERVAL) {
                        updateColors();  // Smoothly change colors
                        lastColorUpdateTime = currentTime;
                    }

                    // Draw the grid with random colors
                    drawGrid(gc, offset);
                }
            }.start();
        }
    }

    private void drawGrid(GraphicsContext gc, double offset) {
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        // Box size
        double boxSize = 60; // Size of each box
        double padding = 20; // Space between boxes

        // Adjust the line width for bigger boxes
        gc.setLineWidth(2);

        // Use efficient color updating (no glow effect for optimization)
        for (double x = -offset; x < gridCanvas.getWidth(); x += boxSize + padding) {
            for (double y = -offset; y < gridCanvas.getHeight(); y += boxSize + padding) {
                int gridX = (int)((x + offset) / (boxSize + padding));
                int gridY = (int)((y + offset) / (boxSize + padding));
                Color color = currentColors[gridX % currentColors.length][gridY % currentColors[0].length];
                gc.setFill(color); // Draw the box with color
                gc.fillRect(x, y, boxSize, boxSize);
            }
        }
    }

    private void updateColors() {
        // Interpolate the current color toward the target color for each box
        for (int i = 0; i < currentColors.length; i++) {
            for (int j = 0; j < currentColors[i].length; j++) {
                Color current = currentColors[i][j];
                Color target = targetColors[i][j];

                // Interpolate between current and target color components
                double r = interpolate(current.getRed(), target.getRed());
                double g = interpolate(current.getGreen(), target.getGreen());
                double b = interpolate(current.getBlue(), target.getBlue());

                currentColors[i][j] = Color.color(r, g, b);

                // Randomly generate a new target color after a smooth transition
                if (Math.abs(current.getRed() - target.getRed()) < 0.01 &&
                    Math.abs(current.getGreen() - target.getGreen()) < 0.01 &&
                    Math.abs(current.getBlue() - target.getBlue()) < 0.01) {
                    targetColors[i][j] = randomColor();
                }
            }
        }
    }

    // Smooth interpolation function
    private double interpolate(double start, double end) {
        double step = 0.02;  // How fast the color changes
        if (start < end) {
            return Math.min(start + step, end);
        } else {
            return Math.max(start - step, end);
        }
    }

    private Color randomColor() {
        // Generate a random color with RGB values
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @FXML
     private void login(ActionEvent event) throws IOException {
        
      String username = usernameLogin.getText();
      String password = passwordLogin.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username or Password cannot be empty!");
            return;
        }

        // Check if the user is locked out
        if (lockTime.containsKey(username)) {
            long lockedTime = lockTime.get(username);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lockedTime < LOCK_TIME_MS) {
                showAlert("Error", "Too many failed attempts! Try again after 1 minute.");
                return;
            } else {
                // Reset lock status after time is over
                lockTime.remove(username);
                loginAttempts.put(username, 0);
            }
        }
         String role = validateLogin(username, password);

        if (role == null) {
            // Increase failed attempts
            loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);

            // If max attempts reached, lock the user
            if (loginAttempts.get(username) >= MAX_ATTEMPTS) {
                lockTime.put(username, System.currentTimeMillis());
                showAlert("Error", "Too many failed attempts! You are locked for 1 minute.");
            } else {
                showAlert("Error", "Invalid Username or Password! Attempts left: " + (MAX_ATTEMPTS - loginAttempts.get(username)));
            }
        } else {
            // Reset attempts on successful login
            loginAttempts.remove(username);
            lockTime.remove(username);

            if (role.equals("admin")) {
                loadDashboard("AdminDashboard.fxml");
                
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();
                
                System.out.println("admin login"+ date + " " + time);
            } else {
                loadDashboard("UserDashboard.fxml");
            }
        }
    }
    private String validateLogin(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT role FROM students WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }


    private void loadDashboard(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Stage stage = (Stage)usernameLogin.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String hashPassword(String password) {
        try {
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public void StoreLog(String log) {
        try (FileWriter writer = new FileWriter("Log.txt", true)) {
            writer.write("\n" + log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
