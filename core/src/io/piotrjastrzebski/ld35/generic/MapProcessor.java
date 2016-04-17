package io.piotrjastrzebski.ld35.generic;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.*;
import io.piotrjastrzebski.jam.ecs.GlobalSettings;
import io.piotrjastrzebski.jam.ecs.processors.physics.Physics;
import io.piotrjastrzebski.ld35.GameScreen;

import static io.piotrjastrzebski.ld35.GameScreen.*;

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
					createTile(x + .5f, y + .5f, bd, fd, CAT_HOLE, CAT_ENEMY | CAT_PLAYER);
					blocked[x + y * width] = true;
				}break;
				case 6: // mountain
				{
					createTile(x + .5f, y + .5f, bd, fd, CAT_WALL, CAT_ENEMY | CAT_PLAYER);
					blocked[x + y * width] = true;
				}break;
				case 7: // lava
				{
					createTile(x + .5f, y + .5f, bd, fd, CAT_HOLE, CAT_ENEMY | CAT_PLAYER);
					blocked[x + y * width] = true;
				}break;
				}
			}
		}

		// create enemies
		MapLayer enemies = map.getLayers().get(1);
		for (MapObject object : enemies.getObjects()) {
			TiledMapTileMapObject to = (TiledMapTileMapObject)object;
			float tx = to.getX() * GlobalSettings.INV_SCALE;
			float ty = to.getY() * GlobalSettings.INV_SCALE;
			createTile(tx + .5f, ty + .5f, bd, fd, CAT_ENEMY, CAT_WALL | CAT_PLAYER);
		}

		// create map boundary
		for (int x = -1; x <= width; x++) {
			createTile(x + .5f, -.5f, bd, fd, CAT_WALL, CAT_ENEMY | CAT_PLAYER);
			createTile(x + .5f, height + .5f, bd, fd, CAT_WALL, CAT_ENEMY | CAT_PLAYER);
		}

		for (int y = -1; y <= height; y++) {
			createTile(-.5f, y + .5f, bd, fd, CAT_WALL, CAT_ENEMY | CAT_PLAYER);
			createTile(width + .5f, y + .5f, bd, fd, CAT_WALL, CAT_ENEMY | CAT_PLAYER);
		}

		shape.dispose();
	}

	private void createTile (float x, float y, BodyDef bd, FixtureDef fd, short catBits, int mask) {
		Body body = physics.b2d.createBody(bd);
		body.setTransform(x, y, 0);
		Fixture fixture = body.createFixture(fd);
		Filter filter = fixture.getFilterData();
		filter.categoryBits = catBits;
		filter.maskBits = (short)mask;
		fixture.setFilterData(filter);
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
