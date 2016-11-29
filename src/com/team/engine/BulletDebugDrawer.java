/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.team.engine;

import com.bulletphysics.linearmath.IDebugDraw;
import com.team.engine.rendering.Shader;

import javax.vecmath.Vector3f;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author jezek2
 */
public class BulletDebugDrawer extends IDebugDraw {
	private int debugMode;
	
	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		Shader s = getShader("debug");
		s.bind();
		s.uniformVec3("color", vec3(1f, 0.3f, 0.3f));
		s.uniformMat4("model", mat4());
		Tessellator ts = Engine.tessellator;
		ts.begin(GL_LINES);
		ts.vertex(from.x, from.y, from.z);
		ts.vertex(to.x, to.y, to.z);
		ts.end();
		
		print("drawing line");
	}
	
	public void drawTriangle(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f color, float alpha) {
		Shader s = getShader("debug");
		s.bind();
		s.uniformVec3("color", vec3(1f, 0.3f, 0.3f));
		s.uniformMat4("model", mat4());
		Tessellator ts = Engine.tessellator;
		ts.begin(GL_TRIANGLES);
		ts.vertex(v1.x, v1.y, v1.z);
		ts.vertex(v2.x, v2.y, v2.z);
		ts.vertex(v3.x, v3.y, v3.z);
		ts.end();
		
		print("drawing triangle");
	}
	
	

	@Override
	public void drawAabb(Vector3f arg0, Vector3f arg1, Vector3f arg2) {
		print("drawing AABB");
	}

	@Override
	public void drawTriangle(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f n0, Vector3f n1, Vector3f n2, Vector3f color, float alpha) {
		print("drawing triangle");
	}

	@Override
	public void setDebugMode(int debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public void draw3dText(Vector3f location, String textString) {
			
	}

	@Override
	public void reportErrorWarning(String warningString) {
		System.err.println(warningString);
	}

	@Override
	public void drawContactPoint(Vector3f pointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {
		print("drawing contact point");
	}

	@Override
	public int getDebugMode() {
		return debugMode;
	}

}
