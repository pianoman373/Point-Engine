package com.team.engine;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public class PhysicsObject2D implements GameObject2D {
	public Body body;
	public boolean isDynamic = false;
	public boolean isBullet = false;

	@Override
	public void init(Scene scene) {
		BodyDef def = new BodyDef();
		if (isDynamic) {
			def.type = BodyType.DYNAMIC;
		}
		else {
			def.type = BodyType.STATIC;
		}
		
		body = scene.world2D.createBody(def);
		body.setUserData(this);
		
		if (isBullet) {
			body.setBullet(true);
		}
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render(Scene scene, Camera cam) {}

	@Override
	public void onContact(Fixture f) {}
	
	@Override
	public void endContact(Fixture f) {}
}
