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
import java.util.List;
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

    public static void readRobot(Robot r, File f) {
        try {
            Robot.fillRobot(r, Files.readAllLines(f.toPath()).get(0));
        } catch (IOException ex) {
            Logger.getLogger(Retrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeRobot(Robot r, File f) {
        writeToFile(Arrays.asList(r.fileData()), f);
    }

    public static KeyControl readKeyControl(File f) {
        try {
            return KeyControl.getKeyControl(Files.readAllLines(f.toPath()).get(0));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void writeKeyControl(KeyControl kc, File f) {
        writeToFile(Arrays.asList(kc.fileData()), f);
    }
    
    public static void readRerun(Robot r, File f) {
        try {
            r.setAuton(Files.readAllLines(f.toPath()));
        } catch (IOException ex) {
            Logger.getLogger(Retrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeRerun(List<String> commands, File f) {
        writeToFile(commands, f);
    }

    private static void writeToFile(Iterable<? extends CharSequence> lines, File f) {
        try (PrintWriter p = new PrintWriter(f)) {
        } catch (FileNotFoundException fnfex) {
            throw new RuntimeException(fnfex);
        }
        try {
            Files.write(f.toPath(), lines);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
