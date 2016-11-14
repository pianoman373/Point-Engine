package com.team.engine.gameobject;

import static com.team.engine.Globals.*;

import com.team.engine.Camera;
import com.team.engine.Engine;
import com.team.engine.Scene;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * Sprite contains more helper functions that build on top of PhysicsObject2D that are focused on
 * automatically rendering your object with no work done on your end.
 * 
 * Notice this class is not abstract. It is primarily meant to be extended by another class, but it
 * works perfectly fine when constructed on it's own too.
 */
public class Sprite extends PhysicsObject2D {
	private String image;
	private Vec2 halfExtents;
	public Vec3 overlayColor = vec3(1, 1, 1);
	
	/**
	 * If you're extending this class, then call super to this.
	 * 
	 * image is the string that this sprite will look for from Engine to bind. Make sure that it is loaded.
	 * image may be null to render a placeholder texture, which is useful for prototyping.
	 * 
	 * halfExtents is the dimensions of the sprite starting from the center of the object,
	 * NOT the width and height (width = halfExtents.x * 2, height = halfExtents.y * 2).
	 * Make sure the ratio of x to y is the same as your image or you'll get stretching.
	 * 
	 * refer to PhysicsObject2D for isDynamic and isBullet since they are just passed through.
	 */
	public Sprite(String image, Vec2 halfExtents, boolean isDynamic, boolean isBullet) {
		super(isDynamic, isBullet);
		this.image = image;
		this.halfExtents = halfExtents;
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void render(Scene scene, Camera cam) {
		if (image != null) {
			Shader s = getShader("sprite");
			s.bind();
			s.uniformMat4("model", mat4().translate(vec3(body.getPosition().x, body.getPosition().y, 1)).rotate(vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(body.getAngle()))).scale(vec3(halfExtents.x * 2, halfExtents.y * 2, 1.0f)));
			s.uniformVec3("overlayColor", overlayColor);
			getTexture(image).bind();
			Engine.spriteMesh.draw();
		}
		else {
			Shader s = getShader("color");
			s.bind();
			s.uniformMat4("model", mat4().translate(vec3(body.getPosition().x, body.getPosition().y, 1)).rotate(vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(body.getAngle()))).scale(vec3(halfExtents.x * 2, halfExtents.y * 2, 1.0f)));
			s.uniformVec3("color", vec3(0.1f, 0.1f, 0.5f));
			Engine.spriteMesh.draw();
			
			s.uniformMat4("model", mat4().translate(vec3(body.getPosition().x, body.getPosition().y, 1.1)).rotate(vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(body.getAngle()))).scale(vec3(halfExtents.x * 2 * 0.9f, halfExtents.y * 2 * 0.9f, 1.0f)));
			s.uniformVec3("color", vec3(0.5f, 0.5f, 1f));
			Engine.spriteMesh.draw();
		}
	}
}