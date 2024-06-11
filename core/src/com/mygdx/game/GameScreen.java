package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final MyLibGDXGame game;
    private Stage stage;
    private Skin skin;
    private TextButton inventoryButton;
    private Touchpad touchpad;
    private Texture touchpadBackground;
    private Texture touchpadKnob;
    private Texture playerTexture;
    private Texture backgroundTexture;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private float playerX, playerY;
    private float playerSpeed;
    private boolean isMoving;
    private boolean facingRight;
    private ArrayList<GameObject> objects;
    private float savedPlayerX, savedPlayerY;
    private boolean savedIsMoving, savedFacingRight;

    public GameScreen(final MyLibGDXGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        touchpadBackground = new Texture(Gdx.files.internal("touchpad.png"));
        touchpadKnob = new Texture(Gdx.files.internal("touchpad.png"));

        touchpad = new Touchpad(10, skin);
        touchpad.setBounds(15, 15, 200, 200);

        stage.addActor(touchpad);

        playerTexture = new Texture(Gdx.files.internal("spritesheet.png"));
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));

        TextureRegion[][] tmpFrames = TextureRegion.split(playerTexture, playerTexture.getWidth() / 2, playerTexture.getHeight() / 2);
        TextureRegion[] walkFrames = new TextureRegion[4];
        walkFrames[0] = tmpFrames[0][0];
        walkFrames[1] = tmpFrames[0][1];
        walkFrames[2] = tmpFrames[1][0];
        walkFrames[3] = tmpFrames[1][1];
        walkAnimation = new Animation<TextureRegion>(0.25f, walkFrames);
        stateTime = 0f;
        playerX = Gdx.graphics.getWidth() / 2 - walkFrames[0].getRegionWidth() / 2;
        playerY = Gdx.graphics.getHeight() / 2 - walkFrames[0].getRegionHeight() / 2;
        playerSpeed = 100f;
        isMoving = false;
        facingRight = true;

        objects = new ArrayList<>();
        objects.add(new GameObject(new Texture(Gdx.files.internal("banana.png")), 200, 200, 32, 32));

        // Crear el botón de inventario
        inventoryButton = new TextButton("Inventario", skin);
        inventoryButton.setSize(100, 50);
        inventoryButton.setPosition(Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 70);

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openInventory();
                saveGameState();
            }
        });

        stage.addActor(inventoryButton);
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

        if (moveX != 0 || moveY != 0) {
            isMoving = true;
            playerX += moveX;
            playerY += moveY;
            if (moveX > 0 && !facingRight) {
                facingRight = true;
            } else if (moveX < 0 && facingRight) {
                facingRight = false;
            }
        } else {
            isMoving = false;
        }

        checkScreenBounds();

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        TextureRegion currentFrame;
        if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = walkAnimation.getKeyFrame(0);
        }

        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        game.batch.draw(currentFrame, playerX, playerY);
        game.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        checkObjectCollision();
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
        backgroundTexture.dispose();
        for (GameObject object : objects) {
            object.dispose();
        }
    }

    private void checkScreenBounds() {
        if (playerX < 0) {
            // Cambiar a la pantalla izquierda
        } else if (playerX > Gdx.graphics.getWidth() - 130) {
            // Cambiar a la pantalla derecha
        } else if (playerY < 0) {
            // Cambiar a la pantalla inferior
        } else if (playerY > Gdx.graphics.getHeight() - 130) {
            // Cambiar a la pantalla superior
        }
    }

    private void openInventory() {
        game.setScreen(new InventoryScreen(game));
    }

    private void checkObjectCollision() {
        Rectangle playerBounds = new Rectangle(playerX, playerY, playerTexture.getWidth(), playerTexture.getHeight());
        for (int i = 0; i < objects.size(); i++) {
            GameObject object = objects.get(i);
            Rectangle objectBounds = object.getBounds();
            if (playerBounds.overlaps(objectBounds)) {
                objects.remove(i);
                // Lógica para agregar el objeto al inventario
            }
        }
    }

    private void saveGameState() {
        // Guarda el estado actual del juego
        savedPlayerX = playerX;
        savedPlayerY = playerY;
        savedIsMoving = isMoving;

    }

    private void loadGameState() {
        // Carga el estado guardado del juego
        playerX = savedPlayerX;
        playerY = savedPlayerY;
        isMoving = savedIsMoving;
        facingRight = savedFacingRight;
    }
}