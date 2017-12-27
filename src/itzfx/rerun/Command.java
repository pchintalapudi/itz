/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.rerun;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public enum Command {
    FORWARD, BACKWARD, LEFT_TURN, RIGHT_TURN, MOGO, AUTOSTACK, CONE, STATSTACK, LOAD, NONE,//Everything after here is just for itzfx.rerun.translate.Translate purposes only.
    FL, FR, BL, BR;

    public static List<String> encode(Queue<List<Command>> commands) {
        return commands.stream().map(pulse -> pulse.stream().map(Objects::toString).collect(Collectors.joining(" "))).collect(Collectors.toList());
    }

    public static Queue<List<Command>> decode(List<String> commands) {
        return commands.stream().map(s -> s.split(" ")).map(Arrays::asList).map(l -> l.stream().map(Command::valueOf).collect(Collectors.toList())).collect(Collectors.toCollection(LinkedList::new));
    }
}
