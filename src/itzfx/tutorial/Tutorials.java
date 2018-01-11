/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial;

import itzfx.tutorial.scenes.TutorialScene;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author prem
 */
public final class Tutorials {

    public static Parent load(String url, TutorialScene controller) {
        FXMLLoader loader = new FXMLLoader(Tutorials.class.getResource(url));
        loader.setController(controller);
        try {
            return loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Tutorials.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
