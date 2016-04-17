package io.piotrjastrzebski.ld35.generic.components;

import com.artemis.PooledComponent;

/**
 * Created by EvilEntity on 16/04/2016.
 */
public class Enemy extends PooledComponent {
	public float dmg;

	@Override protected void reset () {
		dmg = 0;
	}
}
