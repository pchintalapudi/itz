/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.rerun;

import itzfx.Robot;
import itzfx.Start;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This class is a container to perform reruns. It is mainly used in the
 * {@link Robot} class,
 *
 * @author Prem Chintalapudi 5776E
 */
public final class Rerun {

    private final Robot r;

    private final Queue<List<Command>> commands;

    /**
     * Creates a new Rerun object with the specified {@link Robot} and queue of
     * {@link Command} pulses. The list will be executed on the robot when the
     * {@link Rerun#readBack()} method is called.
     *
     * @param r the robot to execute rerun commands on
     * @param commands the commands to execute on the robot
     */
    public Rerun(Robot r, List<String> commands) {
        this.r = r;
        this.commands = Command.decode(commands);
    }

    private Future<?> readBackTask;

    /**
     * Begins executing this rerun's {@link Command command} list on this
     * rerun's {@link Robot}. This method also acts as a resume method if
     * {@link Rerun#pause()} is called.
     */
    public void readBack() {
        if (commands != null && !commands.isEmpty()) {
            readBackTask = Start.PULSER.scheduleAtFixedRate(() -> interpret(commands), 0, 10, TimeUnit.MILLISECONDS);
        }
    }

    private void interpret(Queue<List<Command>> commands) {
        try {
            commands.poll().forEach(this::translate);
        } catch (Exception ex) {
            readBackTask.cancel(true);
        }
    }

    private void translate(Command command) {
        switch (command) {
            case FORWARD:
                r.forward();
                break;
            case LEFT_TURN:
                r.leftTurn();
                break;
            case BACKWARD:
                r.backward();
                break;
            case RIGHT_TURN:
                r.rightTurn();
                break;
            case MOGO:
                r.mogo();
                break;
            case AUTOSTACK:
                r.autostack();
                break;
            case CONE:
                r.cone();
                break;
            case STATSTACK:
                r.statStack();
                break;
            case LOAD:
                r.load();
                break;
        }
    }

    /**
     * Determines if the rerun has finished/is cancelled.
     *
     * @return true if this rerun is not executing.
     */
    public boolean isDone() {
        return readBackTask != null ? readBackTask.isDone() : true;
    }

    /**
     * Stops the rerun task immediately.
     */
    public void stop() {
        if (readBackTask != null) {
            readBackTask.cancel(true);
        }
    }

    /**
     * Pauses the rerun task. If a long-running command is executing, this may
     * allow that command to finish before pausing.
     */
    public void pause() {
        if (readBackTask != null) {
            readBackTask.cancel(false);
        }
    }
}
