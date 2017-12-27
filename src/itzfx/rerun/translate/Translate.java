/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.rerun.translate;

import itzfx.Robot;
import itzfx.data.FileUI;
import itzfx.data.Retrieval;
import itzfx.fxml.FXMLController;
import itzfx.rerun.Command;
import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Window;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public final class Translate {

    private static Deque<Segment> translate(Queue<List<Command>> commands) {
        Deque<Segment> graduated = new LinkedList<>();
        reduceToggles(commands);
        reduceDrives(commands);
        List<Command> singular = commands.stream().filter(l -> !l.isEmpty()).map(l -> l.get(0)).collect(Collectors.toList());
        for (int i = 0; i < singular.size(); i++) {
            Command command = singular.get(i);
            if (graduated.size() > 0 && graduated.peekLast().c == command) {
                graduated.peekLast().length++;
            } else {
                graduated.add(new Segment(command, i));
            }
        }
        return graduated;
    }

    public static List<String> translateTime(Queue<List<Command>> commands) {
        return translate(commands).stream().filter(Translate::filterZeroes).map(Translate::to10Millis).collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<String> translateDistance(Queue<List<Command>> commands, Robot r) {
        return translate(commands).stream().filter(Translate::filterZeroes).map(s -> toDistance(s, r)).collect(Collectors.toCollection(LinkedList::new));
    }

    private static boolean filterZeroes(Segment s) {
        Command c = s.c;
        switch (c) {
            case FORWARD:
            case BACKWARD:
            case LEFT_TURN:
            case RIGHT_TURN:
            case FR:
            case BR:
            case FL:
            case BL:
                if (s.length == 0) {
                    return false;
                }
        }
        return true;
    }

    private static String to10Millis(Segment s) {
        switch (s.c) {
            case FORWARD:
                return "drive(127, 127, " + s.length * 100 + ");";
            case LEFT_TURN:
                return "drive(-127, 127, " + s.length * 100 + ");";
            case BACKWARD:
                return "drive(-127, -127, " + s.length * 100 + ");";
            case RIGHT_TURN:
                return "drive(127, -127, " + s.length * 100 + ");";
            case FR:
                return "drive(127, 0, " + s.length * 100 + ");";
            case BR:
                return "drive(0, -127, " + s.length * 100 + ");";
            case FL:
                return "drive(0, 127, " + s.length * 100 + ");";
            case BL:
                return "drive(-127, 0, " + s.length * 100 + ");";
            case MOGO:
                return "startTask(mogoTask);";
            case CONE:
                return "startTask(intakeTask);";
            case AUTOSTACK:
                return "startTask(autostackTask);";
            case STATSTACK:
                return "startTask(statTask);";
            case NONE:
                return "wait10Msec(" + s.length * 10 + ");";
        }
        return null;
    }

    private static String toDistance(Segment s, Robot r) {
        switch (s.c) {
            case FORWARD:
                return "drive(127, 127, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case LEFT_TURN:
                return "drive(-127, 127, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case BACKWARD:
                return "drive(-127, -127, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case RIGHT_TURN:
                return "drive(127, -127, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case FR:
                return "drive(127, 0, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case BR:
                return "drive(0, -127, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case FL:
                return "drive(0, 127, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case BL:
                return "drive(-127, 0, " + truncate(s.length / 10.0 * r.getSpeed()) + ");";
            case MOGO:
                return "startTask(mogoTask);";
            case CONE:
                return "startTask(intakeTask);";
            case AUTOSTACK:
                return "startTask(autostackTask);";
            case STATSTACK:
                return "startTask(statTask);";
            case NONE:
                return "wait10Msec(" + s.length * 10 + ");";
        }
        return null;
    }

    private static double truncate(double in) {
        return new BigDecimal(String.valueOf(in)).movePointRight(3).intValue() / 1000.0;
    }

    private static void reduceToggles(Queue<List<Command>> commands) {
        MutableBoolean[] bools = new MutableBoolean[4];
        for (int i = 0; i < 4; i++) {
            bools[i] = new MutableBoolean();
        }
        commands.forEach(l -> l.removeIf(c -> c == Command.LOAD));
        commands.stream().peek(l -> validate(bools[0], Command.MOGO, l)).peek(l -> validate(bools[1], Command.CONE, l))
                .peek(l -> validate(bools[2], Command.AUTOSTACK, l)).forEach(l -> validate(bools[3], Command.STATSTACK, l));
    }

    private static void validate(MutableBoolean had, Command command, List<Command> verify) {
        if (had.b) {
            if (verify.contains(command)) {
                verify.remove(command);
            } else {
                had.b = false;
            }
        } else {
            if (verify.contains(command)) {
                had.b = true;
                verify.clear();
                verify.add(command);
            }
        }
    }

    private static class MutableBoolean {

        private boolean b;
    }

    private static void reduceDrives(Queue<List<Command>> commands) {
        commands.stream().filter(l -> l.size() > 1).forEach(Translate::reduceDrives);
    }

    private static void reduceDrives(List<Command> commands) {
        boolean f = commands.contains(Command.FORWARD);
        boolean b = commands.contains(Command.BACKWARD);
        boolean l = commands.contains(Command.LEFT_TURN);
        boolean r = commands.contains(Command.RIGHT_TURN);
        commands.clear();
        if (f) {
            if (b) {
                if (l) {
                    commands.add(r ? Command.NONE : Command.LEFT_TURN);
                } else {
                    commands.add(r ? Command.RIGHT_TURN : Command.NONE);
                }
            } else {
                if (l) {
                    commands.add(r ? Command.FORWARD : Command.FL);
                } else {
                    commands.add(Command.FR);
                }
            }
        } else {
            if (b) {
                if (l) {
                    commands.add(r ? Command.BACKWARD : Command.BL);
                } else {
                    commands.add(Command.BR);
                }
            } else {
                commands.add(Command.NONE);
            }
        }
    }

    private static class Segment {

        private final Command c;
        private final long start;
        private long length;

        public Segment(Command c, long start) {
            this.c = c;
            this.start = start;
            length = 1;
        }
    }

    public static void userTranslateToTime(Window owner) {
        FileUI.load("Autonomous", "*.rrn", owner, f -> {
            String text = (translateTime(Command.decode(Retrieval.read(f))).stream().collect(Collectors.joining("\n")));
            Alert show = new Alert(Alert.AlertType.CONFIRMATION, "", new ButtonType("Copy", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
            ScrollPane s = new ScrollPane(new Label(text));
            s.setPrefViewportHeight(300);
            show.getDialogPane().setContent(s);
            show.getDialogPane().getChildren().stream().forEach(n -> n.setStyle("-fx-background-color:#ffffff"));
            show.getButtonTypes().get(0);
            show.showAndWait().filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .ifPresent(bt -> FXMLController.copy(text));
        });
    }

    public static void userTranslateToDistance(Window owner, Robot r) {
        FileUI.load("Autonomous", "*.rrn", owner, f -> {
            String text = (translateDistance(Command.decode(Retrieval.read(f)), r).stream().collect(Collectors.joining("\n")));
            Alert show = new Alert(Alert.AlertType.CONFIRMATION, "", new ButtonType("Copy", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
            ScrollPane s = new ScrollPane(new Label(text));
            s.setPrefViewportHeight(300);
            show.getDialogPane().setContent(s);
            show.getDialogPane().getChildren().stream().forEach(n -> n.setStyle("-fx-background-color:#ffffff"));
            show.getButtonTypes().get(0);
            show.showAndWait().filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .ifPresent(bt -> FXMLController.copy(text));
        });
    }
}
