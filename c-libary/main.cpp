#include <cstdio>
#include <string>
#include <cstdlib>
#include <openvr.h>


int main() {
	//vprintf("hello world from native land!");

	// Loading the SteamVR Runtime
	vr::EVRInitError eError = vr::VRInitError_None;
	vr::IVRSystem *m_pHMD = vr::VR_Init( &eError, vr::VRApplication_Scene );

	return 0;
}