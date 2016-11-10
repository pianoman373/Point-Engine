package com.team.engine.gameobject;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import com.team.engine.Scene;
import com.team.engine.vecmath.Vec2;

/**
 * PhysicsObject2D is meant to simplify gameobjects affected by physics a bit more.
 * If your GameObject2D implementation needs physics and/or collision detection, have it
 * extend PhysicsObject2D rather than GameObject2D.
 */
public abstract class PhysicsObject2D extends GameObject2D {
	private boolean isDynamic = false;
	private boolean isBullet = false;
	
	/**
	 * Default constructor. As always, call super in your constructor to call this.
	 * 
	 * isDynamic specifies whether this object should move from forces.
	 * 
	 * isBullet will tell box2D that this object will move fast, and try to eliminate
	 * bugs (and also use more performance). Use wisely.
	 * 
	 * This constructor doesn't really do anything, all the meat is in init.
	 */
	public PhysicsObject2D(boolean isDynamic, boolean isBullet) {
		this.isDynamic = isDynamic;
		this.isBullet = isBullet;
	}

	
	/**
	 * Sets up the actual physics part of this helper class.
	 * 
	 * As always call super or else this definition has no use and you get crashing.
	 */
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
	
	/**
	 * Basically teleports this object. Using this for player movement is a bad idea.
	 */
	public void setPosition(Vec2 pos) {
		this.body.setTransform(new Vector2(pos.x, pos.y), this.body.getAngle());
	}
	
	public Vec2 getPosition() {
		return new Vec2(this.body.getPosition());
	}
	
	public void setVelocity(Vec2 vec) {
		this.body.setLinearVelocity(new Vector2(vec.x, vec.y));
	}
	
	public Vec2 getVelocity() {
		return new Vec2(this.body.getLinearVelocity());
	}
	
	/**
	 * Adds a sphere Fixture to this object. You don't have to do anything with
	 * the returned Fixture unless you need it for something.
	 * 
	 * Position is relative to the center of this object.
	 * 
	 * Radius is simple enough.
	 * 
	 * Friction is also pretty straightforward. Goes above 1.
	 */
	public Fixture addSphere(Vec2 pos, float radius, float friction) {
		CircleShape shape2 = new CircleShape();
		shape2.m_p.set(new Vector2(pos.x, pos.y));
		shape2.setRadius(radius);
		Fixture fix = body.createFixture(shape2, 1);
		fix.setFriction(friction);
		
		return fix;
	}
	
	/**
	 * Adds a cube Fixture to this object. You don't have to do anything with
	 * the returned Fixture unless you need it for something.
	 * 
	 * Position is relative to the center of this object.
	 * 
	 * halfExtents is the dimensions of this cube starting from the center of the object,
	 * NOT the width and height (width = halfExtents.x * 2, height = halfExtents.y * 2).
	 */
	public Fixture addCube(Vec2 pos, Vec2 halfExtents, float friction, float restitution, boolean sensor) {
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(halfExtents.x, halfExtents.y, new Vector2(pos.x, pos.y), 0f);
		Fixture fix = body.createFixture(poly, 1);
		fix.setFriction(friction);
		fix.setRestitution(restitution);
		fix.setSensor(sensor);
		
		return fix;
	}
	
	public Fixture addCube(Vec2 pos, Vec2 halfExtents, float friction, boolean sensor) {
		return this.addCube(pos, halfExtents, friction, 0, sensor);
	}
}
