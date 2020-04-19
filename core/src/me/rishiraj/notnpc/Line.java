package me.rishiraj.notnpc;

import com.badlogic.gdx.Gdx;
import me.rishiraj.notnpc.entity.Pair;
import me.rishiraj.notnpc.utils.CollisionUtils;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private int radius;
    private List<Pair<Integer>> linePoints;
    private LineEndListener lineEndListener;

    public Line(LineEndListener lineEndListener, int radius) {
        linePoints = new ArrayList<>();
        this.radius = radius;
        this.lineEndListener = lineEndListener;
    }

    public void addPoint(int x, int y) throws LoopCreatedException {
        if(x < 0 || x > Gdx.graphics.getWidth() || y < 0 || y > Gdx.graphics.getHeight()) {
            lineEndListener.lineEndedPrematurely(this);
        }
        Pair<Integer> point = getLastPoint();
        int prevX = point.getFirst();
        int prevY = point.getSecond();
        double distance = CollisionUtils.distance(x, y, prevX, prevY);
        int circlesToBePlaced = (int) (distance / (1.5f * radius));
        for (int i = 1; i <= circlesToBePlaced; ++i) {
            int placeX = prevX + (x - prevX) * i / (circlesToBePlaced + 1);
            int placeY = prevY + (y - prevY) * i / (circlesToBePlaced + 1);
            addRealPoint(placeX, placeY);
        }
        addRealPoint(x, y);
    }

    private void addRealPoint(int x, int y) throws LoopCreatedException {
        checkForLoops(x, y);
        linePoints.add(new Pair<>(x, y));
    }

    private void checkForLoops(int x, int y) throws LoopCreatedException {
        boolean previousCollides = false;
        for (int i = 0; i < linePoints.size(); ++i) {
            int x2 = linePoints.get(i).getFirst();
            int y2 = linePoints.get(i).getSecond();
            boolean collides = CollisionUtils.circlesOverlap(x, y, radius, x2, y2, radius);
            if (previousCollides && !collides) {
                for (int j = i; j < linePoints.size(); ++j) {
                    int x3 = linePoints.get(j).getFirst();
                    int y3 = linePoints.get(j).getSecond();
                    if (!CollisionUtils.circlesOverlap(x3, y3, radius * 3, x, y, radius * 3)) {
                        List<Pair<Integer>> loopPoints = linePoints.subList(j, linePoints.size() - 1);
                        loopPoints.add(new Pair<Integer>(x, y));
                        throw new LoopCreatedException(loopPoints);
                    }
                }
            }
            previousCollides = collides;
        }
    }

    public List<Pair<Integer>> getLinePoints() {
        return linePoints;
    }

    public void resetLine(int x, int y) {
        linePoints = new ArrayList<>();
        linePoints.add(new Pair<Integer>(x, y));
    }

    public boolean isLastPoint(int screenX, int screenY) {
        Pair<Integer> lastPoint = getLastPoint();
        return lastPoint.getFirst() == screenX && lastPoint.getSecond() == screenY;
    }

    private Pair<Integer> getLastPoint() {
        return linePoints.get(linePoints.size() - 1);
    }

    public int getRadius() {
        return radius;
    }
}
