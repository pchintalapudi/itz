/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

/**
 * An enumeration representing the different modes of control afforded by the
 * field. The field may assign various qualities to each mode (no driver control
 * in {@link ControlMode#AUTON} or {@link ControlMode#PROGRAMMING_SKILLS}, for
 * example).
 *
 * @author Prem Chintalapudi 5776E
 */
public enum ControlMode {

    /**
     * The control mode representing driver control, with a length of 105
     * seconds (1:45).
     */
    DRIVER_CONTROL(105),
    /**
     * The control mode representing the autonomous period, with a length of 15
     * seconds.
     */
    AUTON(15),
    /**
     * The control mode representing the driver skills period, with a length of
     * 60 seconds.
     */
    DRIVER_SKILLS(60),
    /**
     * The control mode representing the programming skills period, with a
     * length of 60 seconds.
     */
    PROGRAMMING_SKILLS(60),
    /**
     * The control mode representing an untimed game, with the idea of just pure
     * practice.
     */
    FREE_PLAY(0);

    private final int seconds;

    private ControlMode(int seconds) {
        this.seconds = seconds;
    }

    /**
     * Gets the alloted time for this Control Mode.
     *
     * @return the time alloted for this mode (will return 0 for free play)
     */
    public int getTime() {
        return seconds;
    }
}
