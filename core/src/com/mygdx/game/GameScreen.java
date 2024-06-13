package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import java.util.Random;

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
    private Texture doorTexture;
    private Rectangle doorBounds;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private float playerX, playerY;
    private float playerSpeed;
    private boolean isMoving;
    private boolean facingRight;
    private ArrayList<GameObject> objects;
    private ArrayList<GameObject> inventory;
    private float savedPlayerX, savedPlayerY;
    private boolean savedIsMoving, savedFacingRight;
    private final int MAX_OBJECTS = 5;
    private Rectangle playerBounds;
    private boolean showMessage = false;
    private float messageTimer = 0f;
    private static final float MESSAGE_DISPLAY_TIME = 3f;
    private String messageText = "";
    private BitmapFont font; // Declarar variable BitmapFont

    public GameScreen(final MyLibGDXGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        touchpadBackground = new Texture(Gdx.files.internal("cuadrado.png"));
        touchpadKnob = new Texture(Gdx.files.internal("circulo.png"));

        touchpad = new Touchpad(10, skin);
        touchpad.setBounds(15, 15, 200, 200);

        stage.addActor(touchpad);

        playerTexture = new Texture(Gdx.files.internal("spritesheet.png"));
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        doorTexture = new Texture(Gdx.files.internal("Puerta.png"));
        doorBounds = new Rectangle(Gdx.graphics.getWidth() - doorTexture.getWidth() / 4, Gdx.graphics.getHeight() - doorTexture.getHeight() / 4, doorTexture.getWidth() / 4, doorTexture.getHeight() / 4);

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
        playerBounds = new Rectangle(playerX, playerY, walkFrames[0].getRegionWidth(), walkFrames[0].getRegionHeight());

        // Crear el botón de inventario
        inventoryButton = new TextButton("Inventario", skin);
        inventoryButton.setSize(100, 50);
        inventoryButton.setPosition(Gdx.graphics.getWidth() - inventoryButton.getWidth() - 20, 20);

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openInventory();
                saveGameState();
            }
        });

        stage.addActor(inventoryButton);

        objects = new ArrayList<>();
        inventory = new ArrayList<>();
        generateRandomObjects();

        font = new BitmapFont(); // Inicializar BitmapFont
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

        for (GameObject object : objects) {
            game.batch.draw(object.getTexture(), object.getX(), object.getY(), object.getWidth(), object.getHeight());
        }

        game.batch.draw(doorTexture, doorBounds.getX(), doorBounds.getY(), doorBounds.getWidth(), doorBounds.getHeight());

        if (showMessage) {
            font.draw(game.batch, messageText, 10, Gdx.graphics.getHeight() - 10); // Dibujar el mensaje en la parte superior izquierda
        }

        game.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // Actualizar la posición de la hitbox del personaje
        playerBounds.setPosition(playerX, playerY);

        checkObjectCollision();
        checkDoorCollision();

        // Mostrar el mensaje si es necesario
        if (showMessage) {
            messageTimer += delta;
            if (messageTimer >= MESSAGE_DISPLAY_TIME) {
                showMessage = false;
                messageTimer = 0f;
            }
        }
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
        doorTexture.dispose();
        for (GameObject object : objects) {
            object.dispose();
        }
        font.dispose(); // Liberar recursos de BitmapFont
    }

    private void checkScreenBounds() {
        if (playerX < 0) {
            playerX = 0;
        } else if (playerX > Gdx.graphics.getWidth() - 130) {
            playerX = Gdx.graphics.getWidth() - 130;
        } else if (playerY < 0) {
            playerY = 0;
        } else if (playerY > Gdx.graphics.getHeight() - 130) {
            playerY = Gdx.graphics.getHeight() - 130;
        }
    }

    private void openInventory() {
        game.setScreen(new InventoryScreen(game, this, inventory));
    }

    private void checkObjectCollision() {
        for (int i = 0; i < objects.size(); i++) {
            GameObject object = objects.get(i);
            Rectangle objectBounds = object.getBounds();
            if (playerBounds.overlaps(objectBounds)) {
                objects.remove(i);
                inventory.add(object);
                i--;
                showMessage = true;
                messageText = "Quedan " + (MAX_OBJECTS - inventory.size()) + " objetos por recoger.";
            }
        }
    }

    private void checkDoorCollision() {
        if (playerBounds.overlaps(doorBounds)) {
            if (inventory.size() == MAX_OBJECTS) {
                gameOver();
            } else {
                showMessage = true;
                messageText = "Faltan " + (MAX_OBJECTS - inventory.size()) + " objetos para completar.";
            }
        }
    }

    private void gameOver() {
        game.setScreen(new GameOverScreen(game));
    }

    private void saveGameState() {
        savedPlayerX = playerX;
        savedPlayerY = playerY;
        savedIsMoving = isMoving;
        savedFacingRight = facingRight;
    }

    public void loadGameState() {
        playerX = savedPlayerX;
        playerY = savedPlayerY;
        isMoving = savedIsMoving;
        facingRight = savedFacingRight;
    }

    private void generateRandomObjects() {
        Random random = new Random();
        Texture objectTexture = new Texture(Gdx.files.internal("Banana.png"));

        Rectangle touchpadBounds = new Rectangle(15, 15, 200, 200);
        Rectangle inventoryButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() - inventoryButton.getWidth() - 20,
                20,
                inventoryButton.getWidth(),
                inventoryButton.getHeight()
        );
        Rectangle doorBounds = new Rectangle(
                Gdx.graphics.getWidth() - doorTexture.getWidth() / 4,
                Gdx.graphics.getHeight() - doorTexture.getHeight() / 4,
                doorTexture.getWidth() / 4,
                doorTexture.getHeight() / 4
        );

        for (int i = 0; i < MAX_OBJECTS; i++) {
            float x, y;
            Rectangle objectBounds;
            boolean intersects;

            do {
                x = random.nextFloat() * (Gdx.graphics.getWidth() - 32);
                y = random.nextFloat() * (Gdx.graphics.getHeight() - 32);
                objectBounds = new Rectangle(x, y, 32, 32);

                intersects = objectBounds.overlaps(touchpadBounds) || objectBounds.overlaps(inventoryButtonBounds) || objectBounds.overlaps(doorBounds);
            } while (intersects);

            objects.add(new GameObject(objectTexture, x, y, 32, 32));
        }
    }
}
