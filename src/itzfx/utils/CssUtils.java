/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.utils;

import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 *
 * @author prem
 */
public class CssUtils {
    
    public static void addStyleSheet(Parent p) {
        p.getStylesheets().add("/itzfx/fxml/css/global.css");
    }
    
    public static void styleDialog(Dialog d) {
        addStyleSheet(d.getDialogPane());
        Optional.ofNullable(d.getDialogPane().lookupButton(ButtonType.CANCEL)).map(Node::getStyleClass).ifPresent(c -> c.add("cancel-button"));
        Optional.ofNullable(d.getDialogPane().lookupButton(ButtonType.YES)).map(Node::getStyleClass).ifPresent(c -> c.add("yes-button"));
        Optional.ofNullable(d.getDialogPane().lookupButton(ButtonType.NO)).map(Node::getStyleClass).ifPresent(c -> c.add("no-button"));
    }
}
