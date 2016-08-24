package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

public class Util {
	//yeah.... this is a mess from when I tried to do raytracing on my own. We have bullet now so don't worry about this.
	
	public static Vec3 ScreenPosToWorldRay(
			int mouseX, int mouseY,             // Mouse position, in pixels, from bottom-left corner of the window
			int screenWidth, int screenHeight,  // Window size, in pixels
			Mat4 ViewMatrix,               // Camera position and orientation
			Mat4 ProjectionMatrix,         // Camera parameters (ratio, field of view, near and far planes)
			Vec3 out_origin              // Ouput : Origin of the ray. /!\ Starts at the near plane, so if you want the ray to start at the camera's position instead, ignore this.
		){

			// The ray Start and End positions, in Normalized Device Coordinates (Have you read Tutorial 4 ?)
			Vec4 lRayStart_NDC = new Vec4(
				((float)mouseX/(float)screenWidth  - 0.5f) * 2.0f, // [0,1024] -> [-1,1]
				((float)mouseY/(float)screenHeight - 0.5f) * 2.0f, // [0, 768] -> [-1,1]
				-1.0f, // The near plane maps to Z=-1 in Normalized Device Coordinates
				1.0f
			);
			Vec4 lRayEnd_NDC = new Vec4(
				((float)mouseX/(float)screenWidth  - 0.5f) * 2.0f,
				((float)mouseY/(float)screenHeight - 0.5f) * 2.0f,
				0.0f,
				1.0f
			);


			// The Projection matrix goes from Camera Space to NDC.
			// So inverse(ProjectionMatrix) goes from NDC to Camera Space.
			Mat4 InverseProjectionMatrix = ProjectionMatrix.inverse();
			
			// The View Matrix goes from World Space to Camera Space.
			// So inverse(ViewMatrix) goes from Camera Space to World Space.
			Mat4 InverseViewMatrix = ViewMatrix.inverse();
			
			Vec4 lRayStart_camera = InverseProjectionMatrix.multiply(lRayStart_NDC);    lRayStart_camera = lRayStart_camera.divide(lRayStart_camera.w);
			Vec4 lRayStart_world  = InverseViewMatrix.multiply(lRayStart_camera);     lRayStart_world = lRayStart_world.divide(lRayStart_world.w);
			Vec4 lRayEnd_camera   = InverseProjectionMatrix.multiply(lRayEnd_NDC);      lRayEnd_camera = lRayEnd_camera.divide(lRayEnd_camera.w);
			Vec4 lRayEnd_world    = InverseViewMatrix.multiply(lRayEnd_camera);   lRayEnd_world = lRayEnd_world.divide(lRayEnd_world.w);


			// Faster way (just one inverse)
			//glm::mat4 M = glm::inverse(ProjectionMatrix * ViewMatrix);
			//glm::vec4 lRayStart_world = M * lRayStart_NDC; lRayStart_world/=lRayStart_world.w;
			//glm::vec4 lRayEnd_world   = M * lRayEnd_NDC  ; lRayEnd_world  /=lRayEnd_world.w;


			Vec4 lRayDir_world = lRayEnd_world.subtract(lRayStart_world);
			lRayDir_world = lRayDir_world.normalize();


			out_origin = new Vec3(lRayStart_world);
			
			return new Vec3(lRayDir_world.normalize());
		}
}
