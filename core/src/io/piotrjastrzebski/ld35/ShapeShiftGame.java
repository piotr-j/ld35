package io.piotrjastrzebski.ld35;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ShapeShiftGame extends Game {
	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new GameScreen(this));
	}

	@Override public void dispose () {
		super.dispose();
		batch.dispose();
	}
}
