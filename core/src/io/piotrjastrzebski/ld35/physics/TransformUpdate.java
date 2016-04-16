package io.piotrjastrzebski.ld35.physics;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.piotrjastrzebski.jam.ecs.components.Transform;
import io.piotrjastrzebski.jam.ecs.components.physics.DynamicBody;
import io.piotrjastrzebski.jam.ecs.components.physics.KinematicBody;

/**
 * Created by EvilEntity on 16/04/2016.
 */
public class TransformUpdate extends IteratingSystem {
	protected ComponentMapper<Transform> mTransform;
	protected ComponentMapper<DynamicBody> mDynamicBody;
	protected ComponentMapper<KinematicBody> mKinematicBody;

	public TransformUpdate () {
		super(Aspect.all(Transform.class).one(DynamicBody.class, KinematicBody.class));
	}

	@Override protected void process (int entityId) {
		Transform transform = mTransform.get(entityId);
		if (mDynamicBody.has(entityId)) {
			DynamicBody body = mDynamicBody.get(entityId);
			Vector2 pos = body.body.getPosition();
			transform.xy(pos.x, pos.y);
			transform.rotation(body.body.getAngle() * MathUtils.radiansToDegrees);
		}
		if (mKinematicBody.has(entityId)) {
			KinematicBody body = mKinematicBody.get(entityId);
			Vector2 pos = body.body.getPosition();
			transform.xy(pos.x, pos.y);
			transform.rotation(body.body.getAngle() * MathUtils.radiansToDegrees);
		}
	}
}
