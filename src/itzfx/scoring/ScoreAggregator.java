/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.scoring;

import itzfx.fxml.FXMLController;
import itzfx.fxml.ScoreSheetController;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * This class tallies up scores from registered
 * {@link ScoreReport score reports} to give final red and blue match/autonomous
 * scores. Skills scoring will be added in a future release. A new skills score
 * user interface will be added to support skills scoring as well.
 *
 * @author Prem Chintalapudi 5776E
 */
public class ScoreAggregator {

    private final List<ScoreReport> reports;

    private int red20;
    private int blue20;
    private int red10;
    private int blue10;
    private int red5;
    private int blue5;
    private int redCones;
    private int blueCones;
    private int redStacks;
    private int blueStacks;
    private int auton;
    private int redPark;
    private int bluePark;

    private Boolean autonomous;

    /**
     * Constructs a new ScoreAggregator.
     */
    public ScoreAggregator() {
        reports = new LinkedList<>();
    }

    /**
     * Registeres a {@link ScoreReport score report} with this aggregator.
     * Registering a report means that this report will be scored, and its score
     * will be added to its corresponding alliance's score.
     *
     * @param sr the score report to register
     */
    public void registerReport(ScoreReport sr) {
        reports.add(sr);
    }

    /**
     * Calculates match score for the currently registered score reports.
     *
     * @return the calculated match scores in the order {red, blue}
     *
     * @see ScoreAggregator#calculate()
     */
    public int[] calculateMatch() {
        AtomicInteger[] initial = calculate();
        highStack(initial[0], initial[1]);
        if (autonomous != null) {
            if (autonomous) {
                initial[0].addAndGet(10);
            } else {
                initial[1].addAndGet(10);
            }
        }
        return new int[]{initial[0].get(), initial[1].get()};
    }

    private AtomicInteger[] calculate() {
        AtomicInteger aiR = new AtomicInteger();
        AtomicInteger aiB = new AtomicInteger();
        reports.forEach(sr -> {
            if (sr.getOwner().isRed()) {
                aiR.addAndGet(sr.getOwner().score());
                aiR.addAndGet(sr.getType().getScore());
            } else {
                aiB.addAndGet(sr.getOwner().score());
                aiB.addAndGet(sr.getType().getScore());
            }
        });
        return new AtomicInteger[]{aiR, aiB};
    }

    /**
     * Calculates autonomous score (no parking bonus) for the currently
     * registered score reports.
     *
     * @return the calculated autonomous scores in the order {red, blue}
     *
     */
    public int[] calculateAuton() {
        AtomicInteger aiR = new AtomicInteger();
        AtomicInteger aiB = new AtomicInteger();
        reports.stream().peek(sr -> {
            if (sr.getOwner().isRed()) {
                aiR.addAndGet(sr.getOwner().score());
            } else {
                aiB.addAndGet(sr.getOwner().score());
            }
        }).filter(sr -> sr.getType() != ScoreType.PARKING)
                .forEach(sr -> {
                    if (sr.getOwner().isRed()) {
                        aiR.addAndGet(sr.getType().getScore());
                    } else {
                        aiB.addAndGet(sr.getType().getScore());
                    }
                });
        highStack(aiR, aiB);
        return new int[]{aiR.get(), aiB.get()};
    }

    private Boolean[] highStack(AtomicInteger aiR, AtomicInteger aiB) {
        MaxableInt r20 = new MaxableInt();
        MaxableInt b20 = new MaxableInt();
        MaxableInt r10 = new MaxableInt();
        MaxableInt b10 = new MaxableInt();
        MaxableInt r5 = new MaxableInt();
        MaxableInt b5 = new MaxableInt();
        MaxableInt redStat = new MaxableInt();
        MaxableInt blueStat = new MaxableInt();
        reports.forEach(sr -> {
            switch (sr.getType()) {
                case ZONE_20:
                    if (sr.getOwner().isRed()) {
                        r20.max(sr.getOwner().score());
                    } else {
                        b20.max(sr.getOwner().score());
                    }
                    break;
                case ZONE_10:
                    if (sr.getOwner().isRed()) {
                        r10.max(sr.getOwner().score());
                    } else {
                        b10.max(sr.getOwner().score());
                    }
                    break;
                case ZONE_5:
                    if (sr.getOwner().isRed()) {
                        r5.max(sr.getOwner().score());
                    } else {
                        b5.max(sr.getOwner().score());
                    }
                    break;
                case STAT_GOAL:
                    if (sr.getOwner().isRed()) {
                        redStat.max(sr.getOwner().score());
                    } else {
                        blueStat.max(sr.getOwner().score());
                    }
            }
        });
        return new Boolean[]{add(r20, b20, aiR, aiB), add(r10, b10, aiR, aiB), add(r5, b5, aiR, aiB), add(redStat, blueStat, aiR, aiB)};
    }

    /**
     * Sets the autonomous winner of this ScoreAggregator based on which
     * alliance has more points currently. That alliance gets a 10 point bonus.
     */
    public void determineAutonWinner() {
        int[] temp = calculateAuton();
        autonomous = temp[0] > temp[1] ? true : temp[1] > temp[0] ? false : null;
    }

    /**
     * Clears the autonomous winner, in preparation for a new match.
     */
    public void clearAuton() {
        autonomous = null;
    }

    private static Boolean add(MaxableInt red, MaxableInt blue, AtomicInteger aiR, AtomicInteger aiB) {
        if (red.get() > blue.get()) {
            aiR.addAndGet(5);
            return true;
        } else if (red.get() < blue.get()) {
            aiB.addAndGet(5);
            return false;
        } else {
            return null;
        }
    }

    /**
     * Calculates the skills score. This method is currently unimplemented
     * correctly, and will be fixed in a future update.
     *
     * @return the calculated skills score
     */
    public int calculateSkills() {
        AtomicInteger[] temp = calculate();
        return temp[0].get() + temp[1].get();
    }

    private void updateReport() {
        red20 = 0;
        blue20 = 0;
        red10 = 0;
        blue10 = 0;
        red5 = 0;
        blue5 = 0;
        redCones = 0;
        blueCones = 0;
        redStacks = 0;
        blueStacks = 0;
        auton = 0;
        redPark = 0;
        bluePark = 0;
        reports.forEach(r -> {
            if (r.getOwner().isRed()) {
                redCones += r.getOwner().score() / 2;
                switch (r.getType()) {
                    case ZONE_20:
                        red20++;
                        break;
                    case ZONE_10:
                        red10++;
                        break;
                    case ZONE_5:
                        red5++;
                        break;
                    case PARKING:
                        redPark++;
                        break;
                    case ZONE_NONE:
                    case STAT_GOAL:
                    default:
                        break;
                }
            } else {
                blueCones += r.getOwner().score() / 2;
                switch (r.getType()) {
                    case ZONE_20:
                        blue20++;
                        break;
                    case ZONE_10:
                        blue10++;
                        break;
                    case ZONE_5:
                        blue5++;
                        break;
                    case PARKING:
                        bluePark++;
                        break;
                    case ZONE_NONE:
                    case STAT_GOAL:
                    default:
                        break;
                }
            }
        });
        Boolean[] stacks = highStack(new AtomicInteger(), new AtomicInteger());
        for (Boolean b : stacks) {
            if (b != null) {
                if (b) {
                    redStacks++;
                } else {
                    blueStacks++;
                }
            }
        }
        if (autonomous != null) {
            auton = autonomous ? 1 : -1;
        }
    }

    /**
     * Shows a {@link ScoreSheet score sheet} in a {@link Dialog}. This is
     * mostly for visual looks and UX enhancement.
     *
     * @see ScoreSheetController
     */
    public void showReport() {
        updateReport();
        FXMLLoader loader = new FXMLLoader(ScoreAggregator.class.getResource("/itzfx/fxml/ScoreSheet.fxml"));
        try {
            Pane load = (loader.load());
            load.getStylesheets().add("/itzfx/fxml/Resources.css");
            StackPane report = new StackPane(load);
            report.setPadding(new Insets(10));
            ScoreSheetController ssc = loader.getController();
            ssc.update(new int[]{red20, blue20, red10, blue10, red5, blue5, redCones, blueCones, redStacks, blueStacks, auton, redPark, bluePark});
            Alert show = new Alert(Alert.AlertType.CONFIRMATION, "", new ButtonType("Copy", ButtonData.OK_DONE), ButtonType.CANCEL);
            System.out.println(show.getDialogPane().getChildren());
            show.getDialogPane().setContent(report);
            show.getDialogPane().getChildren().stream().peek(n -> n.setStyle("-fx-background-color:#ffffff")).filter(n -> n instanceof ButtonBar).map(n -> (ButtonBar) n)
                    .flatMap(bb -> bb.getButtons().stream())
                    .filter(n -> n instanceof Button).map(n -> (Button) n).peek(b -> b.getStyleClass().clear())
                    .peek(b -> b.getStyleClass().add("button")).peek(b -> b.getStylesheets().add("itzfx/fxml/Resources.css"))
                    .filter(b -> b.getText().equals("Cancel")).forEach(b -> b.getStyleClass().add("cancel-button"));
            show.getButtonTypes().get(0);
            show.showAndWait().filter(bt -> bt.getButtonData() == ButtonData.OK_DONE)
                    .ifPresent(bt -> FXMLController.copy(FXMLController.takeScreenshot(report)));
        } catch (IOException ex) {
            Logger.getLogger(ScoreAggregator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class MaxableInt {

        private int integer;

        public int get() {
            return integer;
        }

        public int max(int compare) {
            return integer = integer > compare ? integer : compare;
        }
    }
}
