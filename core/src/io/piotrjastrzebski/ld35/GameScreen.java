package io.piotrjastrzebski.ld35;

import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.piotrjastrzebski.jam.ecs.ArtemisUtils;
import io.piotrjastrzebski.jam.ecs.GlobalSettings;
import io.piotrjastrzebski.jam.ecs.components.Transform;
import io.piotrjastrzebski.jam.ecs.components.gameplay.CameraFollow;
import io.piotrjastrzebski.jam.ecs.components.physics.BodyDef;
import io.piotrjastrzebski.jam.ecs.processors.gameplay.CameraFollower;
import io.piotrjastrzebski.jam.ecs.processors.gameplay.CursorProcessor;
import io.piotrjastrzebski.jam.ecs.processors.physics.Physics;
import io.piotrjastrzebski.jam.ecs.processors.rendering.*;
import io.piotrjastrzebski.ld35.generic.Player;
import io.piotrjastrzebski.ld35.input.PlayerController;
import io.piotrjastrzebski.ld35.physics.TransformUpdate;

/**
 * Created by PiotrJ on 16/04/16.
 */
public class GameScreen extends ScreenAdapter {
	private final World world;
	private final ExtendViewport gameViewport;
	private final ScreenViewport guiViewport;
	private final ShapeRenderer shapes;

	public GameScreen (ShapeShiftGame game) {
		GlobalSettings.init(32, 1280, 720);

		gameViewport = new ExtendViewport(GlobalSettings.WIDTH, GlobalSettings.HEIGHT);
		guiViewport = new ScreenViewport();
		shapes = new ShapeRenderer();

		WorldConfiguration config = new WorldConfiguration();
		config.register(game.batch);
		config.register(shapes);
		config.register(GlobalSettings.WIRE_GAME_VP, gameViewport);
		config.register(GlobalSettings.WIRE_GAME_CAM, gameViewport.getCamera());
		config.register(GlobalSettings.WIRE_GUI_VP, guiViewport);
		config.register(GlobalSettings.WIRE_GUI_CAM, guiViewport.getCamera());

		config.setSystem(CursorProcessor.class);
		config.setSystem(PlayerController.class);
		config.setSystem(Physics.class);
		config.setSystem(TransformUpdate.class);
		config.setSystem(CameraFollower.class);
		config.setSystem(ViewBounds.class);
		config.setSystem(new DebugGridRenderer(1, 1, 1, 1, .25f));

		config.setSystem(DebugBox2dRenderer.class);
		config.setSystem(DebugShapeRenderer.class);
		config.setSystem(Stage.class);

		world = new World(config);
		Gdx.input.setInputProcessor(ArtemisUtils.registerInput(world));

		EntityEdit edit = world.createEntity().edit();
		Transform tm = edit.create(Transform.class);
		tm.size(1, 1);
		tm.xy(-.5f, -.5f);
		Player player = edit.create(Player.class);
		player.state = Player.State.DPS;
		player.id = 0;
		player.speed = 128;
		player.dashDelay = 2;
		player.dashChainMargin = .25f;
		edit.create(CameraFollow.class);
//		edit.create(DebugShape.class).addCircle(ShapeRenderer.ShapeType.Filled, 1f).color(Color.GREEN).segments(16).centre(true);

		// TODO this is crap
		BodyDef bd = edit.create(BodyDef.class);
		bd.def.type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
		bd.def.linearDamping = 16;
		bd.def.fixedRotation = true;
		FixtureDef fd = new FixtureDef();
		CircleShape cs = new CircleShape();
		cs.setRadius(.5f);
		fd.shape = cs;
		fd.density = 0;
		fd.restitution = 0;
		fd.friction = .2f;
		bd.fixtureDefs.add(fd);

		player.dashStopSpeed = ((player.speed * player.speed) / (bd.def.linearDamping * bd.def.linearDamping)) * 1.25f;

	}

	@Override public void render (float delta) {
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// 15fps max delta
		world.delta = Math.min(delta, 1f/15f);
		world.process();
		shapes.begin(ShapeRenderer.ShapeType.Line);
		shapes.setColor(Color.CYAN);
		shapes.line(-1.5f, 0, 1.5f, 0);
		shapes.line(0, -1.5f, 0, 1.5f);
		shapes.end();
	}

	@Override public void resize (int width, int height) {
		gameViewport.update(width, height, true);
		guiViewport.update(width, height);
	}

	@Override public void dispose () {
		world.dispose();
		shapes.dispose();
		Gdx.input.setInputProcessor(null);
	}
}
