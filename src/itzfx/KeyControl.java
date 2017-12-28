/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.scene.input.KeyCode;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public final class KeyControl {

    private final KeyCode forward, left, backward, right, mogo, autostack, cone, stat, load;

    private static final KeyControl SINGLE, DUAL_1, DUAL_2, QUAD_3, QUAD_4, BLANK;

    static {
        SINGLE = new KeyControl((KeyCode[]) null);
        DUAL_1 = new KeyControl(KeyCode.W, KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.Q, KeyCode.E, KeyCode.X, KeyCode.C, KeyCode.F);
        DUAL_2 = new KeyControl(KeyCode.NUMPAD8, KeyCode.NUMPAD4, KeyCode.NUMPAD5, KeyCode.NUMPAD6, KeyCode.NUMPAD7, KeyCode.NUMPAD9, KeyCode.NUMPAD1, KeyCode.NUMPAD3, KeyCode.NUMPAD2);
        QUAD_3 = new KeyControl(KeyCode.Y, KeyCode.G, KeyCode.H, KeyCode.J, KeyCode.T, KeyCode.U, KeyCode.B, KeyCode.M, KeyCode.N);
        QUAD_4 = new KeyControl(KeyCode.P, KeyCode.L, KeyCode.SEMICOLON, KeyCode.QUOTE, KeyCode.O, KeyCode.OPEN_BRACKET, KeyCode.COMMA, KeyCode.SLASH, KeyCode.PERIOD);
        BLANK = new KeyControl(KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED);
    }

    /**
     *
     * @param keys
     */
    public KeyControl(KeyCode... keys) {
        if (keys == null) {
            keys = new KeyCode[0];
        }
        KeyCode[] read = new KeyCode[9];
        System.arraycopy(keys, 0, read, 0, keys.length > read.length ? read.length : keys.length);
        forward = read[0] == null ? KeyCode.W : read[0];
        left = read[1] == null ? KeyCode.A : read[1];
        backward = read[2] == null ? KeyCode.S : read[2];
        right = read[3] == null ? KeyCode.D : read[3];
        mogo = read[4] == null ? KeyCode.M : read[4];
        autostack = read[5] == null ? KeyCode.I : read[5];
        cone = read[6] == null ? KeyCode.O : read[6];
        stat = read[7] == null ? KeyCode.K : read[7];
        load = read[8] == null ? KeyCode.L : read[8];
    }

    /**
     *
     * @return
     */
    public KeyCode[] keys() {
        return new KeyCode[]{forward, left, backward, right, mogo, autostack, cone, stat, load};
    }

    /**
     *
     * @param k
     * @return
     */
    public Action interpret(KeyCode k) {
        int index = indexOf(keys(), k);
        if (index == -1) {
            return Action.NONE;
        }
        return Action.values()[index];
    }

    private <T> int indexOf(T[] array, T element) {
        for (int i = 0; i < array.length; i++) {
            if (element == null ? array[i] == null : element.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     *
     */
    public static enum Action {

        /**
         *
         */
        FORWARD,

        /**
         *
         */
        LEFT,

        /**
         *
         */
        BACKWARD,

        /**
         *
         */
        RIGHT,

        /**
         *
         */
        MOGO,

        /**
         *
         */
        AUTOSTACK,

        /**
         *
         */
        CONE,

        /**
         *
         */
        STAT,

        /**
         *
         */
        LOAD,

        /**
         *
         */
        NONE;
    }

    /**
     *
     */
    public static enum Defaults {

        /**
         *
         */
        SINGLE(KeyControl.SINGLE),

        /**
         *
         */
        DUAL_1(KeyControl.DUAL_1),

        /**
         *
         */
        DUAL_2(KeyControl.DUAL_2),

        /**
         *
         */
        QUAD_3(KeyControl.QUAD_3),

        /**
         *
         */
        QUAD_4(KeyControl.QUAD_4),

        /**
         *
         */
        BLANK(KeyControl.BLANK);

        private final KeyControl kc;

        Defaults(KeyControl kc) {
            this.kc = kc;
        }

        /**
         *
         * @return
         */
        public KeyControl getKC() {
            return kc;
        }
    }

    /**
     *
     * @return
     */
    public String fileData() {
        return Arrays.stream(keys()).map(k -> k.getName()).collect(Collectors.joining(" "));
    }

    /**
     *
     * @param fileData
     * @return
     */
    public static KeyControl getKeyControl(String fileData) {
        return new KeyControl(Arrays.stream(fileData.split(" ")).map(KeyCode::valueOf).toArray(KeyCode[]::new));
    }
}
