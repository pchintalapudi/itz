/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.scoring;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public class ScoreReport {
    
    private final Scoreable owner;
    private ScoreType st;

    /**
     *
     * @param owner
     */
    public ScoreReport(Scoreable owner) {
        this.owner = owner;
        st = ScoreType.ZONE_NONE;
    }

    /**
     *
     * @param st
     */
    public void setScoreType(ScoreType st) {
        this.st = st;
    }

    /**
     *
     * @return
     */
    public Scoreable getOwner() {
        return owner;
    }

    /**
     *
     * @return
     */
    public ScoreType getType() {
        return st;
    }
}
