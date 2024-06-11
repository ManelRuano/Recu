package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyLibGDXGame;

public class ObjectScreen implements Screen {
    private final MyLibGDXGame game;
    private Stage stage;
    private Skin skin;
    private Texture objectTexture;
    private Image objectImage;

    public ObjectScreen(final MyLibGDXGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        objectTexture = new Texture(Gdx.files.internal("assets/Banana.png"));
        objectImage = new Image(objectTexture);
        objectImage.setPosition(Gdx.graphics.getWidth() / 2 - objectTexture.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - objectTexture.getHeight() / 2);

        stage.addActor(objectImage);
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
        objectTexture.dispose();
    }
}

