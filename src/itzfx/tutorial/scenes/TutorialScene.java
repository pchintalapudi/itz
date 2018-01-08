/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial.scenes;

import javafx.scene.Parent;


/**
 *
 * @author prem
 */
public interface TutorialScene {
    
    public void nextScene();
    
    public Parent getRoot();
}
