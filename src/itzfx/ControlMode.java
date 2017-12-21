/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

/**
 *
 * @author prem
 */
public enum ControlMode {
    DRIVER_CONTROL(105), AUTON(15), DRIVER_SKILLS(60), PROGRAMMING_SKILLS(60), FREE_PLAY(0);

    private final int seconds;

    private ControlMode(int seconds) {
        this.seconds = seconds;
    }

    public int getTime() {
        return seconds;
    }
}
