package me.rishiraj.notnpc.entity;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import me.rishiraj.notnpc.DisplayConstants;
import me.rishiraj.notnpc.Interpolator;
import me.rishiraj.notnpc.utils.CollisionUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class GameCharacter implements GameEntity {

    private static int COUNT = 0;

    private double speed;

    private final List<Interpolator> interpolators;

    protected float angle;

    protected float x, y, width, height;

    protected int id;

    public GameCharacter() {
        this.interpolators = new ArrayList<>();
        this.id = COUNT;
        COUNT++;
    }

    protected float speedX, speedY;

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    Sprite sprite;


    public float getX() {
        return x;
    }

    public void setX(float x) {
        sprite.setX(x);
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        sprite.setY(y);
        this.y = y;

    }

    public float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void addInterpolator(Interpolator interpolator) {
        interpolators.add(interpolator);
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float newAngle) {
        rotate(newAngle - this.angle);
        this.angle = newAngle;
    }

    @Override
    public void update(float delta) {
        speed = Math.sqrt(speedX * speedX + speedY * speedY);
        float targetAngle = (float) CollisionUtils.getAngle(speedX, speedY);
        if (targetAngle != angle) {
            float interpolatedAngle = CollisionUtils.interpolate(angle, targetAngle,
                    2 * (float) Math.PI, (float) speed, delta);
            setAngle(interpolatedAngle);
        }

        float maxX = Gdx.graphics.getWidth() - width - DisplayConstants.PADDING;
        float maxY = Gdx.graphics.getHeight() - height - DisplayConstants.PADDING;
        float minX = DisplayConstants.PADDING;
        float minY = DisplayConstants.PADDING;
        x += speedX * delta;
        y += speedY * delta;
        if (x > maxX) {
            x = maxX;
            speedX = -speedX;

        } else if (x < minX) {
            x = minX;
            speedX = -speedX;
        }
        if (y > maxY) {
            y = maxY;
            speedY = -speedY;
        } else if (y < minY) {
            y = minY;
            speedY = -speedY;
        }
        setX(x);
        setY(y);
        List<Interpolator> toRemove = null;
        for(Interpolator interpolator: interpolators) {
            interpolator.update(delta);
            if(interpolator.isComplete()) {
                if(toRemove == null) toRemove = new ArrayList<>();
                toRemove.add(interpolator);
            }
        }
        if(toRemove != null) {
            for(Interpolator interpolator: toRemove) {
                interpolators.remove(interpolator);
            }
        }
    }

    @Override
    public String toString() {
        return "GameCharacter{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", speedX=" + speedX +
                ", speedY=" + speedY +
                ", sprite=" + sprite +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return ((GameCharacter) obj).id == id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void rotate(float angle) {
        sprite.rotate(180f * angle / (float) Math.PI);
    }
}
