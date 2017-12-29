/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.GameObjects;

import itzfx.Mobile;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * A class that adds drag functionality to some FXML loads. Specifically, this
 * class is known to be the controller class for {@link Cone Cones} and
 * {@link MobileGoal Mobile Goals}.
 *
 * @author Prem Chintalapudi 5776E
 */
public class MobileController {

    @FXML
    private Pane mobile;

    private final java.awt.Point.Double mouse = new java.awt.Point.Double();

    @FXML
    private void onPress(MouseEvent m) {
        if (m.isPrimaryButtonDown()) {
            mouse.x = m.getX();
            mouse.y = m.getY();
        }
    }

    @FXML
    private void onDragged(MouseEvent m) {
        if (m.isPrimaryButtonDown()) {
            ((Mobile) mobile.getParent().getUserData()).shiftCenter(m.getX() - mouse.x, m.getY() - mouse.y);
        }
    }
}
