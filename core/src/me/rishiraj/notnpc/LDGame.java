package me.rishiraj.notnpc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.rishiraj.notnpc.screens.GameScreen;
import me.rishiraj.notnpc.screens.MenuScreen;


public class LDGame extends Game implements ScreenChangeCommunicator {
    SpriteBatch batch;
    Texture img;

    @Override
    public void create() {
        AssetManager assetManager = new AssetManager();
        assetManager.load("human.png", Texture.class);
        assetManager.load("bg.png", Texture.class);
        setScreen(new MenuScreen(this));
    }


    @Override
    public void changeScreenTo(final Screen screen) {
        final Runnable newScreen = new Runnable() {
            @Override
            public void run() {
                setScreen(screen);
            }
        };
        Gdx.app.postRunnable(newScreen);
    }
}
