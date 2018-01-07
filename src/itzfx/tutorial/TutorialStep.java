/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial;

import itzfx.KeyControl;
import java.util.Arrays;
import java.util.Iterator;
import javafx.scene.input.KeyCode;

/**
 *
 * @author prem
 */
public enum TutorialStep {
    STEP1, STEP2, STEP3, STEP4, STEP5, STEP6, STEP7, STEP8, STEP9, STEP10;

    public static void setControllers(KeyControl controller) {
        Iterator<TutorialControllers> itr = Arrays.asList(TutorialControllers.values()).iterator();
        Arrays.stream(values()).forEach(ts -> ts.setController(itr.next().getTutorialController(controller)));
    }

    private KeyControl controller;

    private void setController(KeyControl controller) {
        this.controller = controller;
    }

    public KeyControl getController() {
        return controller;
    }

    private enum TutorialControllers {

        F(true), FB(true, false, true), FBLR(true, true, true, true), FBM(true, false, true, false, true),
        FBLRM(true, true, true, true, true), FBA(true, false, true, false, false, true),
        FBLRMA(true, true, true, true, true, true), FBC(true, false, true, false, false, false, true),
        FBCS(true, false, true, false, false, false, true, true), FBLRMACS(true, true, true, true, true, true, true, true);

        private final Boolean[] controls;

        private TutorialControllers(Boolean... booleans) {
            controls = new Boolean[9];
            if (booleans == null || booleans.length == 0) {
                Arrays.fill(controls, true);
            } else {
                System.arraycopy(booleans, 0, controls, 0, booleans.length);
                Arrays.fill(controls, booleans.length, 9, false);
            }
        }

        public KeyControl getTutorialController(KeyControl full) {
            Iterator<Boolean> itr = Arrays.asList(controls).iterator();
            return new KeyControl(Arrays.stream(full.keys()).map(k -> itr.next() ? k : KeyCode.UNDEFINED).toArray(KeyCode[]::new));
        }
    }
}
