#include <cstdio>
#include <string>
#include <cstdlib>

#include <openvr.h>

#define DYNAMIC_LIB_EXT	".so"
#define PLATSUBDIR	"linux64"


int main() {
	printf("hello world from native land!");

	// Loading the SteamVR Runtime
	vr::EVRInitError eError = vr::VRInitError_None;
	vr::IVRSystem *m_pHMD = vr::VR_Init( &eError, vr::VRApplication_Scene );

	return 0;
}