/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * FXML Controller class. Controls the "ScoreSheet.fxml" file. This updates the
 * fields on the score sheet so that it accurately displays the results of a
 * match.
 *
 * @author Prem Chintalapudi 5776E
 */
public class SkillsScoreSheetController {

    @FXML
    private Text red20;
    @FXML
    private Text red10;
    @FXML
    private Text red5;
    @FXML
    private Text redCones;
    @FXML
    private Text redPark;
    @FXML
    private Text redTotal;

    public void update(int[] temp) {
        int[] vals = new int[5];
        if (temp != null) {
            System.arraycopy(temp, 0, vals, 0, temp.length > vals.length ? temp.length : vals.length);
        }
        red20.setText(String.valueOf(vals[0]));
        red10.setText(String.valueOf(vals[1]));
        red5.setText(String.valueOf(vals[2]));
        redCones.setText(String.valueOf(vals[3]));
        redPark.setText(String.valueOf(vals[4]));
        redTotal.setText(String.valueOf(vals[0] * 20 + vals[1] * 10 + vals[2] * 5 + vals[3] * 2 + vals[4] * 2));
    }
}
