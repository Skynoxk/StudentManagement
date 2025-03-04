/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package studentfx;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author skynoxk
 */
public class AdminDashboardController  {

    /**
     * Initializes the controller class.
     */
    @FXML
    Label nameLabel;
    public void display(String username){
        nameLabel.setText("HEllo" + username);
    }
    
}
