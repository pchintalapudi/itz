/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.data;

import itzfx.KeyControl;
import itzfx.Robot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author prem
 */
public class Retrieval {

    private static final File DATADIR;

    static {
        DATADIR = new File("Data");
        if (!DATADIR.isDirectory()) {
            DATADIR.mkdir();
        }
    }

    private Retrieval() {
    }

    public static File getDataDirectory() {
        return DATADIR;
    }

    public static void readFile(Robot r, File f) {
        try {
            Robot.fillRobot(r, Files.readAllLines(f.toPath()).get(0));
        } catch (IOException ex) {
            Logger.getLogger(Retrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeToFile(Robot r, File f) {
        try (PrintWriter p = new PrintWriter(f)) {
        } catch (FileNotFoundException fnfex) {
            throw new RuntimeException(fnfex);
        }
        try {
            Files.write(f.toPath(), Arrays.asList(r.fileData()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static KeyControl readKeyControlFile(File f) {
        try {
            return KeyControl.getKeyControl(Files.readAllLines(f.toPath()).get(0));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void writeToFile(KeyControl kc, File f) {
        try (PrintWriter p = new PrintWriter(f)) {
        } catch (FileNotFoundException fnfex) {
            throw new RuntimeException(fnfex);
        }
        try {
            Files.write(f.toPath(), Arrays.asList(kc.fileData()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
