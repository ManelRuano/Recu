package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyLibGDXGame;

public class GameScreen implements Screen {
    private final MyLibGDXGame game;
    private Stage stage;
    private Skin skin;
    private Touchpad touchpad;
    private Texture touchpadBackground;
    private Texture touchpadKnob;
    private Texture playerTexture;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private float playerX, playerY;
    private float playerSpeed;

    public GameScreen(final MyLibGDXGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        touchpadBackground = new Texture(Gdx.files.internal("assets/background.jpg"));
        touchpadKnob = new Texture(Gdx.files.internal("assets/touchpad.png"));

        touchpad = new Touchpad(10, skin);
        touchpad.setBounds(15, 15, 200, 200);

        stage.addActor(touchpad);

        playerTexture = new Texture(Gdx.files.internal("assets/spritesheet.png"));
        TextureRegion[][] tmpFrames = TextureRegion.split(playerTexture, playerTexture.getWidth() / 4, playerTexture.getHeight());
        TextureRegion[] walkFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            walkFrames[i] = tmpFrames[0][i];
        }
        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);
        stateTime = 0f;
        playerX = Gdx.graphics.getWidth() / 2 - playerTexture.getWidth() / 8;
        playerY = Gdx.graphics.getHeight() / 2 - playerTexture.getHeight() / 2;
        playerSpeed = 100f;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        float moveX = touchpad.getKnobPercentX() * playerSpeed * delta;
        float moveY = touchpad.getKnobPercentY() * playerSpeed * delta;

        playerX += moveX;
        playerY += moveY;

        game.batch.begin();
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        game.batch.draw(currentFrame, playerX, playerY);
        game.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        touchpadBackground.dispose();
        touchpadKnob.dispose();
        playerTexture.dispose();
    }

    private void checkScreenBounds() {
        if (playerX < 0) {
            // Cambiar a la pantalla izquierda
        } else if (playerX > Gdx.graphics.getWidth() - playerTexture.getWidth() / 4) {
            // Cambiar a la pantalla derecha
        } else if (playerY < 0) {
            // Cambiar a la pantalla inferior
        } else if (playerY > Gdx.graphics.getHeight() - playerTexture.getHeight()) {
            // Cambiar a la pantalla superior
        }
    }

}
