package me.rishiraj.notnpc;

import me.rishiraj.notnpc.entity.Pair;

import java.util.List;

public class LoopCreatedException extends Exception {
    private final List<Pair<Integer>> loopPoints;

    public LoopCreatedException(final List<Pair<Integer>> loopPoints) {
        this.loopPoints = loopPoints;
    }

    public List<Pair<Integer>> getLoopPoints() {
        return loopPoints;
    }
}
