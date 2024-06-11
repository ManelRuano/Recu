package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.MyLibGDXGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("My GDX Game");
		config.setWindowedMode(800, 600);
		config.useVsync(true);
		config.setForegroundFPS(60);
		new Lwjgl3Application(new MyLibGDXGame(), config);
	}
}
