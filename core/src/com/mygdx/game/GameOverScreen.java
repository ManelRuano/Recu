package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {
    private final MyLibGDXGame game;
    private Stage stage;
    private Skin skin;

    public GameOverScreen(final MyLibGDXGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear la imagen de pantalla de Game Over
        Texture gameOverTexture = new Texture(Gdx.files.internal("game_over.png"));
        Image gameOverImage = new Image(gameOverTexture);
        gameOverImage.setPosition(
                Gdx.graphics.getWidth() / 2 - gameOverImage.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - gameOverImage.getHeight() / 2
        );

        // Agregar la imagen al stage
        stage.addActor(gameOverImage);

        // Crear el botón de reiniciar
        TextButton restartButton = new TextButton("Reiniciar", skin);
        restartButton.setPosition(
                Gdx.graphics.getWidth() / 2 - restartButton.getWidth() / 2,
                Gdx.graphics.getHeight() / 4
        );

        // Agregar el botón al stage
        stage.addActor(restartButton);

        // Manejar la acción del botón
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Reiniciar el juego
                game.setScreen(new GameScreen(game));
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1); // Establecer el color de fondo a blanco (RGBA: 1, 1, 1, 1)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
    }
}
