package io.piotrjastrzebski.ld35.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.piotrjastrzebski.ld35.ShapeShiftGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("LD35 - ShapeShift");
		config.setWindowedMode(1280, 720);
		new Lwjgl3Application(new ShapeShiftGame(), config);
	}
}
