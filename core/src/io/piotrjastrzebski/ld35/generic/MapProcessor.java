package io.piotrjastrzebski.ld35.generic;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.*;
import io.piotrjastrzebski.jam.ecs.GlobalSettings;
import io.piotrjastrzebski.jam.ecs.processors.physics.Physics;
import io.piotrjastrzebski.ld35.GameScreen;

/**
 * Created by EvilEntity on 17/04/2016.
 */
public class MapProcessor extends BaseSystem {
	@Wire(name = GlobalSettings.WIRE_GAME_CAM) OrthographicCamera camera;
	@Wire TiledMap map;
	@Wire Physics physics;
//	@Wire ShapeRenderer shapes;

	OrthoCachedTiledMapRenderer mapRenderer;
	private int width;
	private int height;
	private boolean[] blocked;

	@Override protected void initialize () {
		mapRenderer = new OrthoCachedTiledMapRenderer(map, GlobalSettings.INV_SCALE, 5460);

		BodyDef bd = new BodyDef();
		bd.type = BodyDef.BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(.5f, .5f);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;

		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		width = layer.getWidth();
		height = layer.getHeight();
		blocked = new boolean[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				TiledMapTileLayer.Cell cell = layer.getCell(x, y);
				if (cell == null) {
					throw new AssertionError("Null cell x=" + x + ", y=" + y);
				}
				TiledMapTile tile = cell.getTile();
				if (tile == null) {
					throw new AssertionError("Null tile x=" + x + ", y=" + y);
				}
				int id = tile.getId();
				switch (id) {
				case 1: // water deep
				case 2: // water shallow
				{
					Body body = physics.b2d.createBody(bd);
					body.setTransform(x + .5f, y + .5f, 0);
					Fixture fixture = body.createFixture(fd);
					Filter filter = fixture.getFilterData();
					filter.categoryBits = GameScreen.CAT_HOLE;
					filter.maskBits = GameScreen.CAT_ENEMY | GameScreen.CAT_PLAYER;
					fixture.setFilterData(filter);
					blocked[x + y * width] = true;
				}break;
				case 6: // mountain
				{
					Body body = physics.b2d.createBody(bd);
					body.setTransform(x + .5f, y + .5f, 0);
					Fixture fixture = body.createFixture(fd);
					Filter filter = fixture.getFilterData();
					filter.categoryBits = GameScreen.CAT_WALL;
					filter.maskBits = GameScreen.CAT_ENEMY | GameScreen.CAT_PLAYER;
					fixture.setFilterData(filter);
					blocked[x + y * width] = true;
				}break;
				case 7: // lava
				{
					Body body = physics.b2d.createBody(bd);
					body.setTransform(x + .5f, y + .5f, 0);
					Fixture fixture = body.createFixture(fd);
					Filter filter = fixture.getFilterData();
					filter.categoryBits = GameScreen.CAT_HOLE;
					filter.maskBits = GameScreen.CAT_ENEMY | GameScreen.CAT_PLAYER;
					fixture.setFilterData(filter);
					blocked[x + y * width] = true;
				}break;
				}
			}
		}

		for (int x = -1; x <= width; x++) {
			Body body = physics.b2d.createBody(bd);
			body.setTransform(x + .5f, -.5f, 0);
			Fixture fixture = body.createFixture(fd);
			Filter filter = fixture.getFilterData();
			filter.categoryBits = GameScreen.CAT_WALL;
			filter.maskBits = GameScreen.CAT_ENEMY | GameScreen.CAT_PLAYER;
			fixture.setFilterData(filter);

			body = physics.b2d.createBody(bd);
			body.setTransform(x + .5f, height + .5f, 0);
			fixture = body.createFixture(fd);
			filter = fixture.getFilterData();
			filter.categoryBits = GameScreen.CAT_WALL;
			filter.maskBits = GameScreen.CAT_ENEMY | GameScreen.CAT_PLAYER;
			fixture.setFilterData(filter);
		}

		for (int y = -1; y <= height; y++) {
			Body body = physics.b2d.createBody(bd);
			body.setTransform(-.5f, y + .5f, 0);
			Fixture fixture = body.createFixture(fd);
			Filter filter = fixture.getFilterData();
			filter.categoryBits = GameScreen.CAT_WALL;
			filter.maskBits = GameScreen.CAT_ENEMY | GameScreen.CAT_PLAYER;
			fixture.setFilterData(filter);

			body = physics.b2d.createBody(bd);
			body.setTransform(width + .5f, y + .5f, 0);
			fixture = body.createFixture(fd);
			filter = fixture.getFilterData();
			filter.categoryBits = GameScreen.CAT_WALL;
			filter.maskBits = GameScreen.CAT_ENEMY | GameScreen.CAT_PLAYER;
			fixture.setFilterData(filter);
		}
	}

	public boolean isBlocked (int x, int y) {
		return x < 0 || y < 0 || x > width || y > height || blocked[x + y * width];
	}

	@Override protected void processSystem () {
		mapRenderer.setView(camera);
		mapRenderer.render();

//		Gdx.gl.glEnable(GL20.GL_BLEND);
//		shapes.setProjectionMatrix(camera.combined);
//		shapes.setColor(1, 0, 0, .5f);
//		shapes.begin(ShapeRenderer.ShapeType.Filled);
//
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				if (isBlocked(x, y)) {
//					shapes.rect(x, y, 1, 1);
//				}
//			}
//		}
//		shapes.end();
	}
}
