package me.rishiraj.notnpc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.rishiraj.notnpc.ScreenChangeCommunicator;
import me.rishiraj.notnpc.screens.GameScreen;
import me.rishiraj.notnpc.utils.CollisionUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.Input.Keys.NUM_0;

public class DesignerScreen implements InputProcessor, Screen {
    private int balls;
    private int speed = 100;
    private Writer writer;
    private int touchX, touchY;
    private ShapeRenderer shapeRenderer;
    private List<List<Integer>> values;
    private boolean infected = false;
    private ScreenChangeCommunicator screenChangeCommunicator;
    private int level;


    public DesignerScreen(ScreenChangeCommunicator screenChangeCommunicator) {
        this(screenChangeCommunicator, 0);
    }

    public DesignerScreen(ScreenChangeCommunicator screenChangeCommunicator, int level) {
        this.level = level;
        this.screenChangeCommunicator = screenChangeCommunicator;
        Gdx.input.setInputProcessor(this);
        values = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        try {
            writer = Gdx.files.absolute("/Users/rshiraj/personal/game/core/assets/level" + this.level).writer(false);
        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.I) {
            infected = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.P) {
            screenChangeCommunicator.changeScreenTo(new GameScreen(screenChangeCommunicator, level));
        }
        int neWLevel = (keycode - NUM_0);
        if (neWLevel < 10) {
            try {
                writer.flush();
                values = new ArrayList<>();
                this.level = neWLevel;
                writer = Gdx.files.absolute("/Users/rshiraj/personal/game/core/assets/level" + neWLevel)
                        .writer(false);
                Gdx.app.log("SWITCH", "Switched to level " + this.level);
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
        if (keycode == Input.Keys.I) {
            infected = false;
        }
        if (keycode == Input.Keys.SPACE) {
            try {
                Gdx.app.log("SAVE","Saved level " + level);
                writer.write(values.size() + " " + speed + "\n");
                for (List<Integer> value : values) {
                    writer.write(value.get(0) + " " + value.get(1) + " " + value.get(2) + " " + value.get(3) + "\n");
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.touchY = Gdx.graphics.getHeight() - screenY;
        this.touchX = screenX;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY;
        float dy = screenY - touchY;
        float dx = screenX - touchX;
        double angle = CollisionUtils.getAngle(dx, dy) * 180f / Math.PI;
        List<Integer> valueList = Arrays.asList(infected ? 1 : 0, touchX, touchY, (int) angle);
        values.add(valueList);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        for (List<Integer> value : values) {
            if (value.get(0) == 1) {
                shapeRenderer.setColor(Color.GREEN);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }
            float angle = (float) (((float) value.get(3)) * Math.PI / 180f);
            float x = value.get(1);
            float y = value.get(2);
            float extendedX = x + (float) Math.cos(angle) * 60;
            float extendedY = y + (float) Math.sin(angle) * 60;
            shapeRenderer.line(x, y, extendedX, extendedY);
            shapeRenderer.circle(x, y, 16);
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
