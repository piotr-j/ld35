package io.piotrjastrzebski.ld35.input;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.piotrjastrzebski.jam.ecs.components.Transform;
import io.piotrjastrzebski.jam.ecs.components.physics.DynamicBody;
import io.piotrjastrzebski.jam.ecs.processors.gameplay.CursorProcessor;
import io.piotrjastrzebski.ld35.generic.Player;

/**
 * Created by EvilEntity on 16/04/2016.
 */
public class PlayerController extends IteratingSystem {
	private static final String TAG = PlayerController.class.getSimpleName();
	protected ComponentMapper<Transform> mTransform;
	protected ComponentMapper<Player> mPlayer;
	protected ComponentMapper<DynamicBody> mDynamicBody;

	@Wire CursorProcessor cp;

	public PlayerController () {
		super(Aspect.all(Player.class, Transform.class, DynamicBody.class));
	}

	private Vector2 dir = new Vector2();
	@Override protected void process (int entityId) {
		Player player = mPlayer.get(entityId);
		Body body = mDynamicBody.get(entityId).body;
		// up/down
		dir.setZero();
		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
			dir.y = 1;
		} else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			dir.y = -1;
		}
		// left/right
		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			dir.x = -1;
		} else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			dir.x = 1;
		}
		dir.limit2(1);
		if (!dir.isZero(0.001f)) {
			float speed = player.speed * world.delta;
			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
				speed *= 10;
			}
			dir.scl(speed);
			Vector2 wc = body.getWorldCenter();
			body.applyLinearImpulse(dir, wc, true);
		}
		// attack etc
	}
}
