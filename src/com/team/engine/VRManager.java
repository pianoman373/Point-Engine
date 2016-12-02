package com.team.engine;

import static com.team.engine.Globals.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import vr.IVRCompositor_FnTable;
import vr.IVRSystem;
import vr.Texture_t;
import vr.TrackedDevicePose_t;
import vr.VR;
import vr.VREvent_t;

/**
 * This is kind of a plugin for Engine that delegates VR when the isVR option is enabled in Engine.start().
 * Basically there are just a bunch of function hooks that Engine calls to keep the file size of Engine down.
 */
public class VRManager {
	private static IVRCompositor_FnTable compositor;
	
	public static vr.HmdMatrix44_t lEyeProj;
	public static vr.HmdMatrix44_t rEyeProj;
	
	public static vr.HmdMatrix34_t lEyeView;
	public static vr.HmdMatrix34_t rEyeView;
	
	public static IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
	
	public static TrackedDevicePose_t.ByReference trackedDevicePosesReference = new TrackedDevicePose_t.ByReference();
	public TrackedDevicePose_t[] trackedDevicePose = (TrackedDevicePose_t[]) trackedDevicePosesReference.toArray(VR.k_unMaxTrackedDeviceCount);
	
	public static IVRSystem hmd;
	
	public static void setupVR() {
		// Loading the SteamVR Runtime
		print(errorBuffer.isDirect());
		
        hmd = VR.VR_Init(errorBuffer, VR.EVRApplicationType.VRApplication_Scene);

        if (errorBuffer.get(0) != VR.EVRInitError.VRInitError_None) {
            hmd = null;
            String s = "Unable to init VR runtime: " + VR.VR_GetVRInitErrorAsEnglishDescription(errorBuffer.get(0));
            throw new Error("VR_Init Failed, " + s);
        }
        
        IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1);
        
        hmd.GetRecommendedRenderTargetSize.apply(width, height);
        
        print("width: " + width.get(0) + ", height: " + height.get(0));
        
        
        compositor = new IVRCompositor_FnTable(VR.VR_GetGenericInterface(VR.IVRCompositor_Version, errorBuffer));

        if (compositor == null || errorBuffer.get(0) != VR.EVRInitError.VRInitError_None) {
            System.err.println("Compositor initialization failed. See log file for details");
        }
	}
	
	public static void update() {
		if (hmd == null) {
        	print("HMD IS NULL!?!?!?!?!");
        	return;
        }
		
		// Process SteamVR events
		VREvent_t event = new VREvent_t();
		while (hmd.PollNextEvent.apply(event, event.size()) != 0) {
    	
		}
		
		lEyeProj = hmd.GetProjectionMatrix.apply(0, 0.1f, 10000000.0f, VR.EGraphicsAPIConvention.API_OpenGL);
		rEyeProj = hmd.GetProjectionMatrix.apply(1, 0.1f, 10000000.0f, VR.EGraphicsAPIConvention.API_OpenGL);
	
		lEyeView = hmd.GetEyeToHeadTransform.apply(0);
		rEyeView = hmd.GetEyeToHeadTransform.apply(1);
	}
	
	public static void postRender() {
		compositor.Submit.apply(0, new Texture_t(Engine.fbuffer.tex[0].id, VR.EGraphicsAPIConvention.API_OpenGL, VR.EColorSpace.ColorSpace_Gamma), null, VR.EVRSubmitFlags.Submit_Default);
		
		compositor.Submit.apply(1, new Texture_t(Engine.fbuffer.tex[1].id, VR.EGraphicsAPIConvention.API_OpenGL, VR.EColorSpace.ColorSpace_Gamma), null, VR.EVRSubmitFlags.Submit_Default);
		
		compositor.WaitGetPoses.apply(trackedDevicePosesReference, VR.k_unMaxTrackedDeviceCount, null, 0);
	}
	
	public static void delete() {
		if (hmd != null) {
            VR.VR_Shutdown();
            hmd = null;
        }
	}
}
