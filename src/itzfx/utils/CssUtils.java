/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 *
 * @author prem
 */
public class CssUtils {

    private static final ObjectProperty<String> STYLESHEET = new SimpleObjectProperty<>("/itzfx/fxml/css/global.css");
    private static final Map<String, String> STYLESHEETS = new HashMap<>();

    static {
        STYLESHEETS.put("default", "/itzfx/fxml/css/global.css");
        STYLESHEETS.put("dark", "/itzfx/fxml/css/dark-mode.css");
    }
    
    public static void switchStyleSheet(String key) {
        STYLESHEET.set(STYLESHEETS.getOrDefault(key, "/itzfx/fxml/css/global.css"));
    }

    public static void addStyleSheet(Parent p) {
        p.getStylesheets().add(STYLESHEET.get());
        STYLESHEET.addListener((o, b, s) -> {
            p.getStylesheets().remove(b);
            p.getStylesheets().add(s);
        });
    }

    public static void addStyleSheet(Scene scene) {
        scene.getStylesheets().add(STYLESHEET.get());
        STYLESHEET.addListener((o, b, s) -> {
            scene.getStylesheets().remove(b);
            scene.getStylesheets().add(s);
        });
    }

    public static void styleDialog(Dialog d) {
        addStyleSheet(d.getDialogPane());
        d.getDialogPane().getStyleClass().add("default-background");
        Optional.ofNullable(d.getDialogPane().lookupButton(ButtonType.CANCEL)).map(Node::getStyleClass).ifPresent(c -> c.add("cancel-button"));
        Optional.ofNullable(d.getDialogPane().lookupButton(ButtonType.YES)).map(Node::getStyleClass).ifPresent(c -> c.add("yes-button"));
        Optional.ofNullable(d.getDialogPane().lookupButton(ButtonType.NO)).map(Node::getStyleClass).ifPresent(c -> c.add("no-button"));
    }
}
