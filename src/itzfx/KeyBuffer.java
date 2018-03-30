/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The class that records which keys are down and which are up at any given
 * pulse. This is used to enable multi-key presses during gameplay (moving a
 * robot both forward and turning left at the same time, for example).
 *
 * @author Prem Chintalapudi 5776E
 */
public final class KeyBuffer {

    private static final Set<KeyCode> KEYBUFFER;

    private static final Map<KeyCode, List<Consumer<KeyCode>>> ONACTION;

    static {
        KEYBUFFER = EnumSet.noneOf(KeyCode.class);
        ONACTION = new EnumMap<>(KeyCode.class);
    }

    private static boolean locked;

    private static Scene scene;

    //It's a lambda; it's not called immediately
    @SuppressWarnings("StaticNonFinalUsedInInitialization")
    private static final EventHandler<KeyEvent> PRESS = k -> {
        if (!locked && !isModified(k)) {
            KEYBUFFER.add(k.getCode());
        }
    };

    //It's a lambda; it's not called immediately
    @SuppressWarnings("StaticNonFinalUsedInInitialization")
    private static final EventHandler<KeyEvent> RELEASE = k -> {
        if (!locked && !isModified(k)) {
            KEYBUFFER.remove(k.getCode());
        }
    };
    
    private static boolean isModified(KeyEvent k) {
        return k.isAltDown() || k.isShiftDown() || k.isShortcutDown();
    }

    /**
     * Registers the listeners for key events on the specified scene. Also
     * removes previous event handlers from the previous scene.
     *
     * @param scene the scene to register key events on
     */
    public static void initialize(Scene scene) {
        if (KeyBuffer.scene != null) {
            KeyBuffer.scene.removeEventHandler(KeyEvent.KEY_PRESSED, PRESS);
            KeyBuffer.scene.removeEventHandler(KeyEvent.KEY_PRESSED, RELEASE);
        }
        scene.addEventHandler(KeyEvent.KEY_PRESSED, PRESS);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, RELEASE);
        KeyBuffer.scene = scene;
    }

    /**
     * Registers a new action to run while a key is pressed.
     *
     * @param k the {@link KeyCode} to check for pressing
     * @param c the action to perform when the key is pressed
     */
    public static void register(KeyCode k, Consumer<KeyCode> c) {
        if (ONACTION.containsKey(k)) {
            ONACTION.get(k).add(c);
        } else {
            ONACTION.put(k, new ArrayList<>(Arrays.asList(c)));
        }
    }

    /**
     * Runs through the keys and performs all actions registered to the key.
     * @return true if any keys were pressed
     */
    public static boolean pulse() {
        boolean actionOccurred = KEYBUFFER.size() > 0;
        KEYBUFFER.forEach(k -> ONACTION.get(k).forEach(c -> c.accept(k)));
        return actionOccurred;
    }

    /**
     * Removes an action registered to the specified key.
     *
     * @param k the {@link KeyCode} to remove the action from
     * @param c the action to be removed
     */
    public static void remove(KeyCode k, Consumer<KeyCode> c) {
        ONACTION.get(k).remove(c);
    }

    /**
     * Determines whether the given key is being held down.
     *
     * @param k the {@link KeyCode} representing the key to check
     * @return true if the key is being held down
     */
    public static boolean isActive(KeyCode k) {
        return KEYBUFFER.contains(k);
    }

    /**
     * Clears the buffer and prevents any additional key presses from being
     * recorded. If this method is called twice without a call to
     * {@link KeyBuffer#unlock()}, nothing will happen.
     */
    public static void lock() {
        locked = true;
        KEYBUFFER.clear();
    }

    /**
     * Allows additional key presses to be recorded. If this method is called
     * twice without a call to {@link KeyBuffer#lock()}, nothing will happen.
     */
    public static void unlock() {
        locked = false;
    }
}
