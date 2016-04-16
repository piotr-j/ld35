package io.piotrjastrzebski.ld35.generic;

import com.artemis.PooledComponent;

/**
 * Created by EvilEntity on 16/04/2016.
 */
public class Player extends PooledComponent {
	public float dashTimer;
	public float dashChainMargin = 0.1f;
	public float dashDelay = 1;
	public boolean dashing;
	public int id = -1;
	public float dashStopSpeed;

	public enum State {DPS, TANK}
	public State state;
	public float speed;

	@Override protected void reset () {
		id = -1;
		state = State.DPS;
		speed = 0;
		dashTimer = 0;
		dashChainMargin = .1f;
		dashDelay = 1;
		dashing = true;
	}
}
