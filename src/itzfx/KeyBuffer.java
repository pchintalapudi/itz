/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author prem
 */
public final class KeyBuffer {

    private static final Map<KeyCode, Boolean> KEYBUFFER;

    private static final Map<KeyCode, List<Consumer<KeyCode>>> ONACTION;

    static {
        KEYBUFFER = new HashMap<>();
        Arrays.stream(KeyCode.values()).forEach(k -> KEYBUFFER.put(k, false));
        ONACTION = new HashMap<>();
    }

    public static void initialize(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, k -> {
            if (!locked) {
                KEYBUFFER.put(k.getCode(), true);
            }
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, k -> {
            if (!locked) {
                KEYBUFFER.put(k.getCode(), false);
            }
        });
    }

    public static void register(KeyCode k, Consumer<KeyCode> c) {
        if (ONACTION.containsKey(k)) {
            ONACTION.get(k).add(c);
        } else {
            ONACTION.put(k, new LinkedList<>(Arrays.asList(c)));
        }
    }

    public static void pulse() {
        KEYBUFFER.entrySet().parallelStream().filter(e -> e.getValue()).filter(e -> ONACTION.containsKey(e.getKey()))
                .forEach(e -> ONACTION.get(e.getKey()).forEach(c -> c.accept(e.getKey())));
    }

    public static void remove(KeyCode k, Consumer<KeyCode> c) {
        ONACTION.get(k).remove(c);
    }

    public static boolean isActive(KeyCode k) {
        return KEYBUFFER.get(k);
    }

    private static boolean locked;

    public static void lock() {
        locked = true;
        Arrays.stream(KeyCode.values()).forEach(k -> KEYBUFFER.put(k, false));
    }

    public static void unlock() {
        locked = false;
    }
}
