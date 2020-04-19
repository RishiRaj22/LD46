package me.rishiraj.notnpc;


import java.util.function.Function;

public class Interpolator {
    private float totalTime, timeElapsed;
    private InterpolatingFunction interpolatingFunction;
    private Function<Float, Void> whereToApply;

    public Interpolator(float totalTime,
                        Function<Float, Void> whereToApply,
                        Interpolator.InterpolatingFunction interpolatingFunction) {
        this.totalTime = totalTime;
        this.whereToApply = whereToApply;
        this.interpolatingFunction = interpolatingFunction;
        this.timeElapsed = 0;
    }

    public boolean isComplete() {
        return timeElapsed >= totalTime;
    }

    public void update(float delta) {
        timeElapsed += delta;
        if (timeElapsed > totalTime) {
            timeElapsed = totalTime;
        }
        float ratio = timeElapsed / totalTime;
        switch (interpolatingFunction) {
            case EASE_OUT_QUAD:
                ratio = 2 * ratio - ratio * ratio;
            case SUPER_FAST_IN:
                ratio = 4 * ratio - 3 * ratio * ratio;
        }
        whereToApply.apply(ratio);
    }

    public enum InterpolatingFunction {
        LINEAR,
        SUPER_FAST_IN,
        EASE_OUT_QUAD
    }
}
