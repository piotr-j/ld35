package io.piotrjastrzebski.ld35;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class ShapeShiftGame extends Game {
	public SpriteBatch batch;
	public TiledMap map;

	@Override
	public void create () {
		batch = new SpriteBatch();
		map = new TmxMapLoader().load("map.tmx");
		setScreen(new GameScreen(this));
	}

	@Override public void dispose () {
		super.dispose();
		batch.dispose();
		map.dispose();
	}
}
