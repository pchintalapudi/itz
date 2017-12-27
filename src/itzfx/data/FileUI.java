/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.data;

import itzfx.KeyControl;
import itzfx.Robot;
import java.io.File;
import java.util.function.Consumer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public class FileUI {

    public static void saveRobot(Robot r, Window owner) {
        save("Robot", "*.rbt", owner, f -> Retrieval.writeRobot(r, f));
    }

    public static void fillRobot(Robot r, Window owner) {
        load("Robot", "*.rbt", owner, f -> Retrieval.readRobot(r, f));
    }

    public static void saveKeyControl(KeyControl kc, Window owner) {
        save("Controller", "*.kcl", owner, f -> Retrieval.writeKeyControl(kc, f));
    }

    public static void getKeyControl(Robot r, Window owner) {
        load("Controller", "*.kcl", owner, f -> r.setController(Retrieval.readKeyControl(f)));
    }
    
    public static void saveRerun(Robot r, Window owner) {
        save("Rerun", "*.rrn", owner, f -> Retrieval.writeRerun(r.saveRecording(), f));
    }

    public static void getRerun(Robot r, Window owner) {
        load("Autonomous", "*.rrn", owner, f -> Retrieval.readRerun(r, f));
    }

    public static void save(String descriptor, String extension, Window owner, Consumer<File> action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Save " + descriptor);
        alert.setContentText("Would you like to save this " + descriptor.toLowerCase() + "?");
        alert.showAndWait().filter(bt -> bt == ButtonType.YES).ifPresent(bt -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(Retrieval.getDataDirectory());
            fc.getExtensionFilters().add(new ExtensionFilter(descriptor, extension));
            File f = fc.showSaveDialog(owner);
            if (f != null) {
                action.accept(f);
            }
        });
    }

    public static void load(String descriptor, String extension, Window owner, Consumer<File> action) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(Retrieval.getDataDirectory());
        fc.getExtensionFilters().add(new ExtensionFilter(descriptor, extension));
        File f = fc.showOpenDialog(owner);
        if (f != null) {
            action.accept(f);
        }
    }
}
