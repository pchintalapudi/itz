/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial;

import java.util.Collection;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author prem
 */
public final class Tutorials {

    public static void registerKeyListeners(Scene scene, Collection<EventHandler<KeyEvent>> handlers) {
        handlers.forEach(h -> scene.addEventHandler(KeyEvent.KEY_PRESSED, h));
    }

    public static void unregisterKeyListeners(Scene scene, Collection<EventHandler<KeyEvent>> handlers) {
        handlers.forEach(h -> scene.removeEventHandler(KeyEvent.KEY_PRESSED, h));
    }
}
