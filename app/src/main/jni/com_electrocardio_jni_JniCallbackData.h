/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_electrocardio_jni_JniCallbackData */

#ifndef _Included_com_electrocardio_jni_JniCallbackData
#define _Included_com_electrocardio_jni_JniCallbackData
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_electrocardio_jni_JniCallbackData
 * Method:    getFloatFromJni
 * Signature: ([I)[F
 */
JNIEXPORT jfloatArray JNICALL Java_com_electrocardio_jni_JniCallbackData_getFloatFromJni
  (JNIEnv *, jobject, jintArray);

/*
 * Class:     com_electrocardio_jni_JniCallbackData
 * Method:    getaverageFromHr
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_electrocardio_jni_JniCallbackData_getaverageFromHr
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_electrocardio_jni_JniCallbackData
 * Method:    getinitFromJni
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_electrocardio_jni_JniCallbackData_getinitFromJni
  (JNIEnv *, jobject);

/*
 * Class:     com_electrocardio_jni_JniCallbackData
 * Method:    getAlgorithmFrom
 * Signature: ([I)Lcom/electrocardio/jni/OutObjectResultPacket;
 */
JNIEXPORT jobject JNICALL Java_com_electrocardio_jni_JniCallbackData_getAlgorithmFrom
  (JNIEnv *, jobject, jintArray);

#ifdef __cplusplus
}
#endif
#endif
