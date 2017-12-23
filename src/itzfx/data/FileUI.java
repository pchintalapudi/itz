/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.data;

import itzfx.Robot;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 *
 * @author prem
 */
public class FileUI {

    public static void saveRobot(Robot r, Window owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Save Robot");
        alert.setContentText("Would you like to save this robot?");
        alert.showAndWait().filter(bt -> bt == ButtonType.YES).ifPresent(bt -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(Retrieval.getDataDirectory());
            fc.getExtensionFilters().add(new ExtensionFilter("Robot", "*.rbt"));
            File f = fc.showSaveDialog(owner);
            if (f != null) {
                Retrieval.writeToFile(r, f);
            }
        });
    }

    public static void fillRobot(Robot r, Window owner) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(Retrieval.getDataDirectory());
        fc.getExtensionFilters().add(new ExtensionFilter("Robot", "*.rbt"));
        File f = fc.showOpenDialog(owner);
        if (f != null) {
            Retrieval.readFile(r, f);
        }
    }
}
