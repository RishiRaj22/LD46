package me.rishiraj.notnpc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import me.rishiraj.notnpc.DisplayConstants;
import me.rishiraj.notnpc.ScreenChangeCommunicator;

public class MenuScreen implements InputProcessor, Screen {
    private final Sprite healthySprite, infectedSprite;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private ScreenChangeCommunicator screenChangeCommunicator;
    private int level;
    private BitmapFont font = new BitmapFont(Gdx.files.internal("40.fnt"), false);
    private Texture texture,texture2;
    private Sprite pencil;
    private float timeElapsed = 0;
    private boolean touched = false;

    public MenuScreen(ScreenChangeCommunicator screenChangeCommunicator) {
        this.screenChangeCommunicator = screenChangeCommunicator;
        Gdx.input.setInputProcessor(this);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        texture2 = new Texture("cursor.png");
        pencil = new Sprite(texture2);
        texture = new Texture("human.png");
        healthySprite = new Sprite(texture, 0, 0, 32, 32);
        infectedSprite = new Sprite(healthySprite);
        infectedSprite.setColor(1, 0, 0, 1);
        infectedSprite.setX(Gdx.graphics.getWidth()/3f);
        infectedSprite.setY(Gdx.graphics.getWidth()/3.2f);
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            screenChangeCommunicator.changeScreenTo(new GameScreen(screenChangeCommunicator));
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY;
        if(screenY > buttonY && screenY < buttonY + buttonHeight && screenX > buttonX && screenX < (buttonX + buttonWidth)) {
            touched = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY;
        if(screenY > buttonY && screenY < buttonY + buttonHeight && screenX > buttonX && screenX < (buttonX + buttonWidth)) {
            screenChangeCommunicator.changeScreenTo(new GameScreen(screenChangeCommunicator));
        }
        touched = false;
        return true;
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
        font.setColor(Color.WHITE);
    }

    private float buttonX = Gdx.graphics.getWidth()/2-50;
    private float buttonY = DisplayConstants.PADDING * 1.5f - 43;
    private float buttonWidth = 120;
    private float buttonHeight = 50;

    @Override
    public void render(float delta) {

        timeElapsed += delta;
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        if(touched) {
            shapeRenderer.setColor(Color.BLACK);
        }
        shapeRenderer.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(Gdx.graphics.getWidth()/2.7f + 3, Gdx.graphics.getHeight()/2.7f + 32,50);
        shapeRenderer.end();


        spriteBatch.begin();
        font.draw(spriteBatch, "Manage the ongoing situation", 60, DisplayConstants.PADDING + Gdx.graphics.getHeight() / 1.5f);
        font.getData().setScale(0.5f);
        font.draw(spriteBatch, "Enclose the infected people in circle to avoid spread of infection",
                50,
                Gdx.graphics.getHeight() / 3.5f);
        font.draw(spriteBatch, "Save as many people as you can!",
                150,
                Gdx.graphics.getHeight() / 4.5f);
        font.getData().setScale(1);

        if(!touched) {
            font.setColor(Color.BLACK);
        }
        font.draw(spriteBatch, "Play!", Gdx.graphics.getWidth()/2-40, DisplayConstants.PADDING * 1.5f);
        font.setColor(Color.WHITE);
        font.setColor(Color.WHITE);

//        pencil.setX(Gdx.graphics.getWidth()/2.7f);
//        pencil.setY(Gdx.graphics.getHeight()/2.7f);

        pencil.setX(Gdx.graphics.getWidth()/2.7f + (float)Math.cos(timeElapsed*5)*50);
        pencil.setY(Gdx.graphics.getHeight()/2.7f + (float)Math.sin(timeElapsed*5)*50);
        infectedSprite.draw(spriteBatch);

        pencil.draw(spriteBatch);
        spriteBatch.end();


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
        shapeRenderer.dispose();
        spriteBatch.dispose();
        texture.dispose();
        texture2.dispose();
        font.dispose();
    }
}
