package me.rishiraj.notnpc.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;

public interface GameEntity {
    void update(float delta);
    Sprite getSprite();
}
