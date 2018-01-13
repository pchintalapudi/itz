/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial.scenes;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author prem
 */
public class TutorialBase {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private void close() {
        root.getScene().getWindow().hide();
    }
}
