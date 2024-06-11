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

public class InventoryScreen implements Screen {
    private final MyLibGDXGame game;
    private Stage stage;
    private Skin skin;

    public InventoryScreen(final MyLibGDXGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear la imagen de la mochila
        Texture mochilaTexture = new Texture(Gdx.files.internal("Mochila.jpeg"));
        Image mochilaImage = new Image(mochilaTexture);
        mochilaImage.setPosition(
                Gdx.graphics.getWidth() / 2 - mochilaImage.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - mochilaImage.getHeight() / 2
        );

        // Agregar la imagen al stage
        stage.addActor(mochilaImage);

        // Crear el botón de regreso
        TextButton backButton = new TextButton("Volver al juego", skin);
        backButton.setPosition(20, 20); // Ajusta la posición del botón según tus necesidades

        // Agregar el botón al stage
        stage.addActor(backButton);

        // Manejar la acción del botón
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla del juego
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
