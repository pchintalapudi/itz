/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial.scenes;

import itzfx.rerun.Command;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author prem
 */
public interface TutorialScene {

    public static final ObjectProperty<TutorialScene> FOCUSED = new SimpleObjectProperty<>();

    public static void setFocusedScene(TutorialScene focused) {
        FOCUSED.set(focused);
    }

    public static TutorialScene getFocusedScene() {
        return FOCUSED.get();
    }

    public void nextScene();

    public void init();

    default public void interact(Command command) {
    }
}
