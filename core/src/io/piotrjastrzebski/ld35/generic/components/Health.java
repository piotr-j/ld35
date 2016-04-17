package io.piotrjastrzebski.ld35.generic.components;

import com.artemis.PooledComponent;

/**
 * Created by EvilEntity on 16/04/2016.
 */
public class Health extends PooledComponent {
	public float max;
	public float value;

	@Override protected void reset () {
		value = max = 0;
	}
}
