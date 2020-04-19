package me.rishiraj.notnpc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.rishiraj.notnpc.DisplayConstants;
import me.rishiraj.notnpc.Interpolator;
import me.rishiraj.notnpc.LevelSerialiser;
import me.rishiraj.notnpc.Line;
import me.rishiraj.notnpc.LineEndListener;
import me.rishiraj.notnpc.LoopCreatedException;
import me.rishiraj.notnpc.Popup;
import me.rishiraj.notnpc.ScreenChangeCommunicator;
import me.rishiraj.notnpc.entity.GameCharacter;
import me.rishiraj.notnpc.entity.Pair;
import me.rishiraj.notnpc.entity.Person;
import me.rishiraj.notnpc.utils.CollisionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static me.rishiraj.notnpc.utils.CollisionUtils.getAngle;
import static me.rishiraj.notnpc.utils.CollisionUtils.isPointInsidePolygon;
import static me.rishiraj.notnpc.utils.CollisionUtils.totalArea;

public class GameScreen implements Screen, InputProcessor, LineEndListener {
    private List<Person> personList;
    private Texture texture, bgTexture;
    private SpriteBatch batch;
    private Line currentLine;
    private ShapeRenderer shapeRenderer;
    private int lives = 3;
    private boolean penDown = false;
    private float[][] allLoops;
    private int loopCount = 0;
    private int level = 0;
    private boolean gameOver = false;
    private float totalArea;
    private static int INFECTION_CHANCE = 40;
    private BitmapFont font = new BitmapFont(Gdx.files.internal("40.fnt"), false);
    boolean gameLost = false;
    private float[] visibleLoop;
    private float visibleDuration;
    private int rescuedLives = 0;
    private ScreenChangeCommunicator screenChangeCommunicator;
    private List<Popup> popups;
    private Music chalkMusic;

    public GameScreen(ScreenChangeCommunicator screenChangeCommunicator) {
        this(screenChangeCommunicator, 0);
    }

    public GameScreen(ScreenChangeCommunicator screenChangeCommunicator, int level) {
        chalkMusic = Gdx.audio.newMusic(Gdx.files.internal("chalk.ogg"));
        Pixmap pm = new Pixmap(Gdx.files.internal("cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();

        popups = new ArrayList<>();
        this.screenChangeCommunicator = screenChangeCommunicator;
        this.level = level;
        currentLine = new Line(this, 3);
        Gdx.input.setInputProcessor(this);
        allLoops = new float[200][];
        loadCurrentLevel();
    }

    private void loadCurrentLevel() {
        try {
            personList = LevelSerialiser.getLevel(level).getInitialPersons();
            initPersonSprites();
            gameLost = false;
            gameOver = false;
        } catch (Exception ex) {
            gameOver = true;
        }
    }

    private void initPersonSprites() {
//        texture = new Texture("person.png");
        texture = new Texture("human.png");
        Sprite healthySprite = new Sprite(texture, 0, 0, 32, 32);
        Sprite infectedSprite = new Sprite(healthySprite);
        infectedSprite.setColor(1, 0, 0, 1);
        for (Person person : personList) {
            Sprite requiredSprite = healthySprite;
            if (person.isInfected()) {
                requiredSprite = infectedSprite;
            }
            requiredSprite.setOrigin(requiredSprite.getWidth() / 2, requiredSprite.getHeight() / 2);
            person.setSprite(new Sprite(requiredSprite));
            person.getSprite().setScale(person.getWidth() / requiredSprite.getWidth());
            person.rotate(person.getAngle());
        }
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        bgTexture = new Texture("bg.png");
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        font.setColor(Color.WHITE);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        drawBG();
        drawPersons();
        drawInfo();
        drawPopups();
        if (gameOver) {
            drawGameOverText();
        }
        batch.end();
        drawLineAndLoops();
        update(delta);
    }

    private void drawGameOverText() {
        if (!gameLost) {
            font.draw(batch, "You won and saved " + rescuedLives + " people!", 50, Gdx.graphics.getHeight() / 2);
        } else {
            font.draw(batch, "Game over. You saved " + rescuedLives + " people!", 50, Gdx.graphics.getHeight() / 2);
        }
        font.draw(batch, "Press R to restart!", 150, Gdx.graphics.getHeight() / 3);
    }

    private void drawInfo() {
        if (!gameOver) {
            font.draw(batch, "Rescued people: " + rescuedLives, 200, DisplayConstants.PADDING - 10);
        }
        font.draw(batch, "Level: " + level, 300, Gdx.graphics.getHeight() - 10);
    }

    private void drawPopups() {
        for (Popup popup : popups) {
            font.draw(batch, popup.getText(), popup.getX(), popup.getY());
        }
    }

    private void update(float delta) {
        boolean survivorStillLeft = false;
        for (Person person : personList) {
            if (!person.isInfected()) {
                survivorStillLeft = true;
            }
        }
        if (!survivorStillLeft) {
            gameLost = true;
            gameOver = true;
        }

        updatePopups(delta);
        visibleDuration -= delta;
        for (Person person : personList) {
            person.update(delta);
            checkCollisionBetweenBalls(delta);
        }
        if (penDown) {
            checkCollisionBetweenPenAndBall(delta);
        }
    }

    private void updatePopups(float delta) {
        List<Popup> toRemoved = null;
        for (Popup popup : popups) {
            try {
                popup.update(delta);
            } catch (Popup.PopupExpiredException e) {
                if (toRemoved == null) toRemoved = new ArrayList<>();
                toRemoved.add(popup);
            }
        }
        if (toRemoved != null) {
            for (Popup popup : toRemoved) {
                popups.remove(popup);
            }
        }
    }

    private void checkCollisionBetweenPenAndBall(float delta) {
        for (Person person : personList) {
            for (Pair<Integer> linePoint : currentLine.getLinePoints()) {
                double offset = currentLine.getRadius() / 2;
                if (CollisionUtils.circlesOverlap(
                        linePoint.getFirst(), linePoint.getSecond(), 0,
                        centerX(person), centerY(person), person.getHeight() / 2)) {
                    lineEndedByCollision(currentLine);
                    return;
                }
            }
        }
    }

    private void addInfectionAnimation(final Person person) {
        person.addInterpolator(
                new Interpolator(2,
                        new Function<Float, Void>() {
                            @Override
                            public Void apply(Float ratio) {
                                person.getSprite().setColor(1, 1 - ratio, 1 - ratio, 1);
                                return null;
                            }
                        },
                        Interpolator.InterpolatingFunction.SUPER_FAST_IN));
    }


    private void checkCollisionBetweenBalls(float delta) {
        for (int i = 0; i < personList.size(); ++i) {
            final Person character1 = personList.get(i);
            for (int j = i + 1; j < personList.size(); ++j) {
                final Person character2 = personList.get(j);
                if (character1 == character2) continue;
                double distance = Math.sqrt(
                        Math.pow(centerX(character1) - centerX(character2), 2) +
                                Math.pow(centerY(character1) - centerY(character2), 2));
                double distanceInside = character1.getWidth() / 2 + character2.getWidth() / 2 - distance;
                if (distanceInside > 0) {
                    if ((character1.isInfected() || character2.isInfected()) && Math.random() * 100 < INFECTION_CHANCE) {
                        if (!character1.isInfected()) {
                            addInfectionAnimation(character1);
                        }
                        if (!character2.isInfected()) {
                            addInfectionAnimation(character2);
                        }
                        character1.setInfected(true);
                        character2.setInfected(true);
                    }
                    float dx = centerX(character1) - centerX(character2);
                    float dy = centerY(character1) - centerY(character2);
                    double angle = getAngle(dx, dy);

                    double speedAlongCollisionLineForCharacter1 =
                            -Math.cos(angle) * character1.getSpeedX()
                                    - Math.sin(angle) * character1.getSpeedY();
                    double speedAlongCollisionLineForCharacter2 =
                            Math.cos(angle) * character2.getSpeedX()
                                    + Math.sin(angle) * character2.getSpeedY();

                    double perpendicularSpeedForCharacter1 =
                            Math.cos(angle) * character1.getSpeedY()
                                    + Math.cos(Math.PI / 2 + angle) * character1.getSpeedX();

                    double perpendicularSpeedForCharacter2 =
                            Math.cos(angle + Math.PI / 2) * character2.getSpeedX()
                                    + Math.cos(angle) * character2.getSpeedY();

                    float newSpeedXForCharacter1 = (float) (Math.cos(angle) * speedAlongCollisionLineForCharacter2 +
                            Math.cos(Math.PI / 2 + angle) * perpendicularSpeedForCharacter1);

                    float newSpeedXForCharacter2 = (float) -(Math.cos(angle) * speedAlongCollisionLineForCharacter1 +
                            Math.cos(Math.PI / 2 + angle) * perpendicularSpeedForCharacter2);

                    float newSpeedYForCharacter1 = (float) (Math.sin(angle) * speedAlongCollisionLineForCharacter2 +
                            Math.cos(angle) * perpendicularSpeedForCharacter1);

                    float newSpeedYForCharacter2 = (float) -(Math.sin(angle) * speedAlongCollisionLineForCharacter1 +
                            Math.cos(angle) * perpendicularSpeedForCharacter2);


                    character1.setSpeedX(newSpeedXForCharacter1);
                    character1.setSpeedY(newSpeedYForCharacter1);
                    character2.setSpeedX(newSpeedXForCharacter2);
                    character2.setSpeedY(newSpeedYForCharacter2);

                    character1.setX(character1.getX() + (float) (distanceInside / 2 * Math.cos(angle)));
                    character1.setY(character1.getY() + (float) (distanceInside / 2 * Math.sin(angle)));

                    character2.setX(character2.getX() - (float) (distanceInside / 2 * Math.cos(angle)));
                    character2.setY(character2.getY() - (float) (distanceInside / 2 * Math.sin(angle)));

                }
            }
        }
    }


    private float centerX(GameCharacter gameCharacter) {
        return gameCharacter.getX() + gameCharacter.getWidth() / 2;
    }

    private float centerY(GameCharacter gameCharacter) {
        return gameCharacter.getY() + gameCharacter.getHeight() / 2;
    }

    private void drawLineAndLoops() {
        Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        if (penDown) {
            List<Pair<Integer>> linePoints = currentLine.getLinePoints();
            for (int i = 0; i < linePoints.size(); ++i) {
                Pair<Integer> point = linePoints.get(i);
                shapeRenderer.circle(point.getFirst(), point.getSecond(), currentLine.getRadius());
            }
        }
        for (float[] loop : allLoops) {
            if (loop == null) break;
            if (gameOver) {
                shapeRenderer.polygon(loop);
            }
        }
        if (visibleDuration > 0) {
            shapeRenderer.polygon(visibleLoop);
        }
        shapeRenderer.end();
    }

    private void drawBG() {
        batch.draw(bgTexture, DisplayConstants.PADDING, DisplayConstants.PADDING);
    }

    private void drawPersons() {
        for (Person person : personList) {
            person.getSprite().draw(batch);
        }
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
        batch.dispose();
        texture.dispose();
        bgTexture.dispose();
        batch.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.R) {
            Gdx.app.log("RESTART", "Restart");
            level = 0;
            lives = 3;
            rescuedLives = 0;
            loadCurrentLevel();
            return true;
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
        penDown();
        currentLine.resetLine(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (penDown) {
            lineEndedPrematurely(currentLine);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        screenY = Gdx.graphics.getHeight() - screenY;
        if (penDown && !currentLine.isLastPoint(screenX, screenY)) {
            try {
                currentLine.addPoint(screenX, screenY);
            } catch (LoopCreatedException e) {
                lineEnded(currentLine);
                addLoop(e.getLoopPoints());
            }
        }
        return true;
    }

    private void addLoop(List<Pair<Integer>> loopPoints) {
        allLoops[loopCount] = new float[loopPoints.size() * 2];
        for (int i = 0; i < loopPoints.size(); ++i) {
            allLoops[loopCount][2 * i] = loopPoints.get(i).getFirst();
            allLoops[loopCount][2 * i + 1] = loopPoints.get(i).getSecond();
        }
        visibleLoop = allLoops[loopCount];
        visibleDuration = 1;
        totalArea += Math.sqrt(totalArea(allLoops[loopCount])) / 5;
        List<Person> toRemove = new ArrayList<>();
        for (Person person : personList) {
            if (isPointInsidePolygon(allLoops[loopCount], centerX(person), centerY(person))) {
                toRemove.add(person);
            }
        }
        boolean nonInfectedPersonQuarantined = false;
        for (Person person : toRemove) {
            nonInfectedPersonQuarantined = nonInfectedPersonQuarantined || !person.isInfected();
            // Gdx.app.log("PERSON", "Person eaten");
            personList.remove(person);
        }
        if (nonInfectedPersonQuarantined) {
            showPopupText("Oops", Gdx.input.getX(), Gdx.input.getY());
        }
        if (toRemove.size() > 1) {
            showPopupText("Combo x" + toRemove.size(), Gdx.input.getX(), Gdx.input.getY());
        }
        boolean levelCompleted = true;
        for (Person person : personList) {
            if (person.isInfected()) {
                levelCompleted = false;
            }
        }
        if (levelCompleted) {
            rescuedLives += personList.size();
            level++;
//            lives++;
            loadCurrentLevel();
        }

        loopCount++;
    }

    private void showPopupText(String oops, int cursorX, int cursorY) {
        popups.add(new Popup(oops, cursorX, Gdx.graphics.getHeight() - cursorY));
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
//
//        screenY = Gdx.graphics.getHeight() - screenY;
//        if(penDown) {
//            if (!currentLine.isLastPoint(screenX, screenY)) {
//                Gdx.app.log("MOVED", "Screen x: " + screenX + " y: " + screenY);
//                currentLine.addPoint(screenX, screenY);
//                return true;
//            }
//        } else {
//            currentLine.resetLine(screenX, screenY);
//            penDown = true;
//        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private void penUp() {
        penDown = false;
        chalkMusic.stop();
    }

    private void penDown() {
        penDown = true;
        chalkMusic.setLooping(true);
        chalkMusic.play();
    }

    @Override
    public void lineEnded(Line line) {
        Gdx.app.log("LINE", "Line ended");

        penUp();
        //TODO: Implement logic to check if person lies inside line
//        for(Person person: personList) {
//
//        }
    }

    @Override
    public void lineEndedPrematurely(Line line) {
        Gdx.app.log("LINE", "Line ended prematurely");
        penUp();
    }

    @Override
    public void lineEndedByCollision(Line line) {
//        lives--;
//        showPopupText("Life lost",Gdx.input.getX(), Gdx.input.getY());
//        if (lives < 0) {
//            gameOver = true;
//            gameLost = true;
//        }
        Gdx.app.log("LINE", "Line ended by collision. Lives set to " + lives);
        penUp();
    }
}
