/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.scoring;

/**
 *
 * @author prem
 */
public enum ScoreType {

    ZONE_NONE(0), ZONE_20(20), ZONE_10(10), ZONE_5(5), STAT_GOAL(0), PARKING(2);

    private final int score;
    
    ScoreType(int score) {
        this.score = score;
    }
    
    public int getScore() {
        return score;
    }
    
    public static boolean isMobileGoal(ScoreType st) {
        return st != STAT_GOAL && st != PARKING;
    }
}
