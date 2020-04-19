package me.rishiraj.notnpc.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;

public class CollisionUtils {
    public static boolean circlesOverlap(double x1, double y1, double r1, double x2, double y2, double r2) {
        return distance(x1, y1, x2, y2) <= r1 + r2;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static double getAngle(float dx, float dy) {
        double angle = Math.atan(dy / dx);
        if (dx < 0) {
            angle += Math.PI;
        }
        return angle;
    }

    public static Color interpolateTint(Color initialColor, Color secondColor) {
        float ration = randomComponent(0.9f, 1);
        float newR = (initialColor.r * ration + secondColor.r * (1 - ration)) ;
        float newG = (initialColor.g * ration + secondColor.g * (1 - ration)) ;
        float newB = (initialColor.b * ration + secondColor.b * (1 - ration)) ;
        float newA = (initialColor.a * ration + secondColor.a * (1 - ration)) ;
        return new Color(newR, newG, newB, newA);
    }

    public static boolean isPointInsidePolygon(float[] polygon, float x, float y) {
        Polygon polyObject = new Polygon(polygon);
        return polyObject.contains(x, y);
    }

    public static float totalArea(float[] poly) {
        Polygon polygon = new Polygon(poly);
        float area =  polygon.area();
        Gdx.app.log("ADD", "Area: " + area);
        return area;
    }

    public static float randomComponent(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static float interpolate(float src, float target, float totalRange, float speedComponent, float deltaTime) {
        float speed = Math.signum(target - src);
        speed += speedComponent * (target - src) / (totalRange);
        float actualDelta = lesserMagnitude(target - src, speed * deltaTime);
        return src + actualDelta;
    }

    private static float lesserMagnitude(float v1, float v2) {
        if(v1 < 0) {
            return Math.max(v1,v2);
        }
        return Math.min(v1, v2);
    }
}
