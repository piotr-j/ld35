package io.piotrjastrzebski.ld35.generic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.piotrjastrzebski.jam.ecs.components.Transform;
import io.piotrjastrzebski.jam.ecs.components.gameplay.CameraFollow;
import io.piotrjastrzebski.jam.ecs.components.physics.BodyDef;
import io.piotrjastrzebski.jam.ecs.processors.physics.Physics;
import io.piotrjastrzebski.ld35.generic.components.Player;

/**
 * Created by EvilEntity on 16/04/2016.
 */
public class PlayerUpdater extends IteratingSystem {
	private static final String TAG = PlayerUpdater.class.getSimpleName();
	protected ComponentMapper<Transform> mTransform;
	protected ComponentMapper<Player> mPlayer;
	@Wire MapProcessor map;
	@Wire Physics physics;

	public PlayerUpdater () {
		super(Aspect.all(Player.class, Transform.class));
	}

	@Override protected void initialize () {
		spawnPlayer = true;
	}

	boolean spawnPlayer;
	float timer;

	@Override protected void begin () {
		timer -= world.delta;
		if (timer < 0 && spawnPlayer) {
			spawnPlayer = false;
			EntityEdit edit = world.createEntity().edit();
			Transform tm = edit.create(Transform.class);
			tm.size(.8f, .8f);
			tm.xy(32 -.5f, 32 -.5f);
			Player player = edit.create(Player.class);
			player.state = Player.State.DPS;
			player.id = 0;
			player.speed = 128;
			player.dashDelay = 0;
			player.dashChainMargin = .25f;
			edit.create(CameraFollow.class);
//		edit.create(DebugShape.class).addCircle(ShapeRenderer.ShapeType.Filled, 1f).color(Color.GREEN).segments(16).centre(true);

			// TODO api this is crap
			BodyDef bd = edit.create(BodyDef.class);
			bd.def.type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
			bd.def.linearDamping = 16;
			bd.def.fixedRotation = true;
			FixtureDef fd = new FixtureDef();
			CircleShape cs = new CircleShape();
			cs.setRadius(.4f);
			fd.shape = cs;
			fd.density = 0;
			fd.restitution = 0;
			fd.friction = .2f;
			bd.fixtureDefs.add(fd);

			fd = new FixtureDef();
			cs = new CircleShape();
			cs.setRadius(.9f);
			fd.shape = cs;
			fd.density = 0;
			fd.restitution = 0;
			fd.friction = .2f;
			bd.fixtureDefs.add(fd);

			player.dashStopSpeed = ((player.speed * player.speed) / (bd.def.linearDamping * bd.def.linearDamping)) * 1.25f;
		}
	}

	private Rectangle tmp = new Rectangle();
	private float margin = .2f;
	@Override protected void process (int entityId) {
		Transform tm = mTransform.get(entityId);
		Player player = mPlayer.get(entityId);
		// simple check for player dashing into a hole
		if (!player.dashing && player.dashTimer < player.dashDelay - player.dashChainMargin) {
			int cx = (int)tm.cx;
			int cy = (int)tm.cy;
			tmp.set(cx + margin, cy + margin, 1 - margin * margin, 1 - margin * margin);
			if (map.isBlocked(cx, cy) && tmp.contains(tm.cx, tm.cy)) {
				world.delete(entityId);
			}
		}
	}

	@Override protected void inserted (int entityId) {
	}

	@Override protected void removed (int entityId) {
		timer = 1;
		spawnPlayer = true;
	}
}
