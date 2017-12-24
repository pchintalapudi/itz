/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.controller;

import itzfx.KeyControl;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class KeyBinder {

    @FXML
    private AnchorPane root;

    @FXML
    private ListView<String> left;
    @FXML
    private AnchorPane center;

    private Keyboard keyboard;

    private final KeyCode[] keys;

    public KeyBinder(KeyControl kc) {
        keys = kc.keys();
    }
    
    @FXML
    private void initialize() {
        this.keyboard = (Keyboard) center.getUserData();
        left.getItems().addAll("Forward  " + keys[0], "Left Turn  " + keys[1], "Backward  " + keys[2], "Right Turn  " + keys[3]);
        left.getItems().addAll("Mobile Goal Intake/Outtake  " + keys[4], "Autostack on Mobile Goal  " + keys[5],
                "Intake/Outtake a Cone  " + keys[6], "Stack on a Stationary Goal  " + keys[7], "Load a Driver Load  " + keys[8]);
        Arrays.stream(keys).peek(keyboard::selected).forEach(k -> keyboard.save());
        left.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
            System.out.println(k);
            int index = left.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                String replace = left.getItems().get(index).split("  ")[0];
                keys[index] = k.getCode();
                left.getItems().set(index, replace + "  " + k.getCode());
                keyboard.selected(k.getCode());
            }
        });
        left.addEventFilter(MouseEvent.MOUSE_RELEASED, m -> {
            keyboard.save();
            keyboard.selected(keys[left.getSelectionModel().getSelectedIndex()]);
        });
    }
    
    public KeyControl getKC() {
        return new KeyControl(keys);
    }
}
