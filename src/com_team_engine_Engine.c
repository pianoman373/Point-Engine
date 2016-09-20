#include <stdio.h>
#include "com_team_engine_Engine.h"

JNIEXPORT jint JNICALL Java_com_team_engine_Engine_sayHello(JNIEnv *env, jclass c) {
    printf("hello!");

    return 101;
}
