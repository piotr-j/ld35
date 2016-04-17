package io.piotrjastrzebski.ld35.input;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import io.piotrjastrzebski.jam.ecs.components.Transform;
import io.piotrjastrzebski.jam.ecs.components.physics.DynamicBody;
import io.piotrjastrzebski.jam.ecs.processors.gameplay.CursorProcessor;
import io.piotrjastrzebski.ld35.GameScreen;
import io.piotrjastrzebski.ld35.generic.components.Player;

/**
 * Created by EvilEntity on 16/04/2016.
 */
public class PlayerController extends IteratingSystem {
	private static final String TAG = PlayerController.class.getSimpleName();
	protected ComponentMapper<Transform> mTransform;
	protected ComponentMapper<Player> mPlayer;
	protected ComponentMapper<DynamicBody> mDynamicBody;

	public static final int CAT_NONE = 0;
	public static final int CAT_ALL = GameScreen.CAT_PLAYER | GameScreen.CAT_ENEMY | GameScreen.CAT_WALL | GameScreen.CAT_HOLE;
	public static final int CAT_NO_HOLE = GameScreen.CAT_PLAYER | GameScreen.CAT_ENEMY | GameScreen.CAT_WALL;

	@Wire CursorProcessor cp;

	public PlayerController () {
		super(Aspect.all(Player.class, Transform.class, DynamicBody.class));
	}

	@Override protected void inserted (int entityId) {
		Body body = mDynamicBody.get(entityId).body;
		setMasks(body, CAT_ALL, CAT_NONE);
	}

	private void setMasks(Body body, int smallMask, int largeMask) {
		Array<Fixture> fl = body.getFixtureList();
		Fixture fixture = fl.get(0);
		Filter filter = fixture.getFilterData();
		filter.categoryBits = GameScreen.CAT_PLAYER;
		filter.maskBits = (short)smallMask;
		fixture.setFilterData(filter);
		fixture = fl.get(1);
		filter = fixture.getFilterData();
		filter.categoryBits = GameScreen.CAT_PLAYER;
		filter.maskBits = (short)largeMask;
		fixture.setFilterData(filter);
	}

	private Vector2 dir = new Vector2();
	private Vector2 move = new Vector2();
	@Override protected void process (int entityId) {
		Player player = mPlayer.get(entityId);
		Body body = mDynamicBody.get(entityId).body;
		Vector2 vel = body.getLinearVelocity();
		float velMag2 = vel.len2();
		Vector2 bp = body.getPosition();

		// switch state
		if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
			switch (player.state) {
			case DPS:
				player.state = Player.State.TANK;
				setMasks(body, CAT_NONE, CAT_ALL);
				break;
			case TANK:
				player.state = Player.State.DPS;
				setMasks(body, CAT_ALL, CAT_NONE);
				break;
			}
			Gdx.app.log(TAG, "State="+player.state);
		}

		// up/down
		move.setZero();
		if (player.dashing) {
			if (velMag2 <= player.dashStopSpeed) {
				player.dashing = false;
				setMasks(body, CAT_ALL, CAT_NONE);
			}
		} else {
			if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
				move.y = 1;
			} else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				move.y = -1;
			}
			// left/right
			if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				move.x = -1;
			} else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				move.x = 1;
			}
			move.limit2(1);
			player.dashTimer -= world.delta;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			switch (player.state) {
			case DPS:
				// we need to know if we are dashing
				if (!player.dashing && (player.dashTimer <= 0 || player.dashTimer >= player.dashDelay - player.dashChainMargin)) {
					dir.set(cp.x, cp.y).sub(bp).nor().scl(50);
					move.add(dir);
					player.dashTimer = player.dashDelay;
					player.dashing = true;
					setMasks(body, CAT_NO_HOLE, CAT_NONE);
				}
				break;
			case TANK:
				// we need to know if we are dashing
				if (player.dashTimer <= 0 || player.dashTimer >= player.dashDelay - player.dashChainMargin) {
					dir.set(cp.x, cp.y).sub(bp).nor().scl(80);
					move.add(dir);
					player.dashTimer = player.dashDelay;
				}
				break;
			}
		}

		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			switch (player.state) {
			case DPS: {

			}break;
			case TANK: {

			}break;
			}
		}

		switch (player.state) {
		case DPS:
			if (!move.isZero(0.001f)) {
				float speed = player.speed * world.delta;
				if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					speed *= 10;
				}
				move.scl(speed);
			}
			break;
		case TANK:
			if (!move.isZero(0.001f)) {
				float speed = player.speed * world.delta * 0.33f;
				if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					speed *= 10;
				}
				move.scl(speed);
			}
			break;
		}
		Vector2 wc = body.getWorldCenter();
		body.applyLinearImpulse(move, wc, true);

		// attack etc
	}
}
