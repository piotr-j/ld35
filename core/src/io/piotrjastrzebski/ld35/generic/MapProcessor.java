package io.piotrjastrzebski.ld35.generic;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

	OrthoCachedTiledMapRenderer mapRenderer;
	@Override protected void initialize () {
		mapRenderer = new OrthoCachedTiledMapRenderer(map, GlobalSettings.INV_SCALE, 5460);

		BodyDef bd = new BodyDef();
		bd.type = BodyDef.BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(.5f, .5f);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;

		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		for (int x = 0; x < layer.getWidth(); x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
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
				}break;
				}
			}
		}
	}

	@Override protected void processSystem () {
		mapRenderer.setView(camera);
		mapRenderer.render();
	}
}
