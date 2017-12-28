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
import java.util.TreeMap;
import java.util.function.Consumer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public final class KeyBuffer {

    private static final Map<KeyCode, Boolean> KEYBUFFER;

    private static final Map<KeyCode, List<Consumer<KeyCode>>> ONACTION;
    private static final Map<KeyCode[], List<Consumer<KeyCode[]>>> ONMULTI;

    static {
        KEYBUFFER = new HashMap<>();
        Arrays.stream(KeyCode.values()).forEach(k -> KEYBUFFER.put(k, false));
        ONACTION = new HashMap<>();
        ONMULTI = new TreeMap<>((k1, k2) -> k1.length > k2.length ? 1 : k1.length < k2.length ? -1 : 0);
    }

    /**
     *
     * @param scene
     */
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

    /**
     *
     * @param k
     * @param c
     */
    public static void register(KeyCode k, Consumer<KeyCode> c) {
        if (ONACTION.containsKey(k)) {
            ONACTION.get(k).add(c);
        } else {
            ONACTION.put(k, new LinkedList<>(Arrays.asList(c)));
        }
    }

    /**
     *
     */
    public static void pulse() {
        ONACTION.entrySet().parallelStream().filter(e -> KEYBUFFER.get(e.getKey()))
                .forEach(e -> e.getValue().forEach(c -> c.accept(e.getKey())));
        if (!ONMULTI.isEmpty()) {
            pulseMulti();
        }
    }

    /**
     *
     * @param c
     * @param k
     */
    public static void registerMulti(Consumer<KeyCode[]> c, KeyCode... k) {
        if (ONMULTI.containsKey(k)) {
            ONMULTI.get(k).add(c);
        } else {
            ONMULTI.put(k, new LinkedList<>(Arrays.asList(c)));
        }
    }

    private static void pulseMulti() {
        ONMULTI.entrySet().parallelStream().filter(e -> Arrays.stream(e.getKey()).allMatch(k -> KEYBUFFER.get(k)))
                .forEach(e -> e.getValue().forEach(c -> c.accept(e.getKey())));
    }

    /**
     *
     * @param k
     * @param c
     */
    public static void remove(KeyCode k, Consumer<KeyCode> c) {
        ONACTION.get(k).remove(c);
    }

    /**
     *
     * @param k
     * @return
     */
    public static boolean isActive(KeyCode k) {
        return KEYBUFFER.get(k);
    }

    private static boolean locked;

    /**
     *
     */
    public static void lock() {
        locked = true;
        Arrays.stream(KeyCode.values()).forEach(k -> KEYBUFFER.put(k, false));
    }

    /**
     *
     */
    public static void unlock() {
        locked = false;
    }
}
