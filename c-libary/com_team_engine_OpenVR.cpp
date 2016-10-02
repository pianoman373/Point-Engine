#include <cstdio>
#include <string>
#include <cstdlib>
#include <openvr.h>

#include "com_team_engine_OpenVR.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_team_engine_OpenVR_setup(JNIEnv *env, jclass c) {
	//vprintf("hello world from native land!");

	// Loading the SteamVR Runtime
	vr::EVRInitError eError = vr::VRInitError_None;
	vr::IVRSystem *m_pHMD = vr::VR_Init( &eError, vr::VRApplication_Scene );
}

#ifdef __cplusplus
}
#endif
