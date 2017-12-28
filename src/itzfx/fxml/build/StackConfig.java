/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.build;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Prem Chintalapudi 5776E
 */
public class StackConfig {

    @FXML
    private Pane root;

    @FXML
    private TextField mogoStack;

    @FXML
    private TextField statStack;

    /**
     *
     */
    public void initialize() {
        mogoStack.setTextFormatter(new IntFormatter());
        statStack.setTextFormatter(new IntFormatter());
        root.setUserData(this);
    }

    /**
     *
     * @return
     */
    public Integer getMogoStack() {
        if (mogoStack.getText().isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(mogoStack.getText());
        }
    }

    /**
     *
     * @return
     */
    public Integer getStatStack() {
        if (statStack.getText().isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(statStack.getText());
        }
    }

    private static class IntFormatter extends TextFormatter<Integer> {

        public IntFormatter() {
            super(c -> {
                c.setText(c.getText().replaceAll("[^0-9]", ""));
                return c;
            });
        }
    }
}
