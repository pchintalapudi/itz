/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class ScoreSheetController {

    @FXML
    private Text red20;
    @FXML
    private Text blue20;
    @FXML
    private Text red10;
    @FXML
    private Text blue10;
    @FXML
    private Text red5;
    @FXML
    private Text blue5;
    @FXML
    private Text redCones;
    @FXML
    private Text blueCones;
    @FXML
    private Text redStacks;
    @FXML
    private Text blueStacks;
    @FXML
    private Text redAuton;
    @FXML
    private Text blueAuton;
    @FXML
    private Text redPark;
    @FXML
    private Text bluePark;

    public void update(int[] temp) {
        int[] vals = new int[13];
        if (temp != null) {
            System.arraycopy(temp, 0, vals, 0, temp.length > vals.length ? temp.length : vals.length);
        }
        red20.setText(String.valueOf(vals[0]));
        blue20.setText(String.valueOf(vals[1]));
        red10.setText(String.valueOf(vals[2]));
        blue10.setText(String.valueOf(vals[3]));
        red5.setText(String.valueOf(vals[4]));
        blue5.setText(String.valueOf(vals[5]));
        redCones.setText(String.valueOf(vals[6]));
        blueCones.setText(String.valueOf(vals[7]));
        redStacks.setText(String.valueOf(vals[8]));
        blueStacks.setText(String.valueOf(vals[9]));
        redAuton.setVisible(vals[10] > 0);
        blueAuton.setVisible(vals[10] < 0);
        redPark.setText(String.valueOf(vals[11]));
        bluePark.setText(String.valueOf(vals[12]));
    }
}
