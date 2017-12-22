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
public class ScoreReport {
    
    private final Scoreable owner;
    private ScoreType st;

    public ScoreReport(Scoreable owner) {
        this.owner = owner;
        st = ScoreType.ZONE_NONE;
    }

    public void setScoreType(ScoreType st) {
        this.st = st;
    }

    public Scoreable getOwner() {
        return owner;
    }

    public ScoreType getType() {
        return st;
    }
}
