//
// Created by Administrator on 2016/2/14.
//

#include <jni.h>
#include "stdio.h"
#include "stdlib.h"
#include "string.h"
#include "AnalysisWave.h"
#include "EcgDiagnosis.h"
#include "Ecg_Interface.h"
#include "ProcessShowWave.h"

#include <android/log.h>

#define  LOG_TAG    "native-dev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

/**
 * 获取心率波形数据
 */
JNIEXPORT jobject JNICALL Java_com_electrocardio_jni_JniCallbackData_AnalysisECGWave
        (JNIEnv *env, jobject obj, jintArray array, jint bLeadOff) {
    // 存储原始数据
    int arynData[10];
    // 存储ECG波形数据
    float aryfDataShow[13] = {0};
    // 实时每s节律事件个数
    int nRhythmNum = 0;
    // 实时每s节律事件编号
    int arynRhythmMark[RHYTHM_SORT] = {0};
    // 是否过载
    int bOverLoad = 0;

    int len = (*env)->GetArrayLength(env, array);
    jint *p = (*env)->GetIntArrayElements(env, array, 0);

    int i = 0;
    for (i; i < len; i++) {
        arynData[i] = *(p + i);
    }
    AnalysisECGWave(arynData, bLeadOff, aryfDataShow, &nRhythmNum, arynRhythmMark, &bOverLoad);
    jclass objectClass = (*env)->FindClass(env, "com/electrocardio/jni/ECGResultBean");
    jfieldID DataShow = (*env)->GetFieldID(env, objectClass, "aryfDataShow", "[F");
    jfieldID RhythmNum = (*env)->GetFieldID(env, objectClass, "nRhythmNum", "I");
    jfieldID RhythmMark = (*env)->GetFieldID(env, objectClass, "arynRhythmMark", "[I");
    jfieldID OverLoad = (*env)->GetFieldID(env, objectClass, "bOverLoad", "I");
    jobject result = (*env)->AllocObject(env, objectClass);
    jfloatArray floatArr = (*env)->NewFloatArray(env, 13);
    (*env)->SetFloatArrayRegion(env, floatArr, 0, 13, aryfDataShow);
    (*env)->SetObjectField(env, result, DataShow, floatArr);
    (*env)->SetIntField(env, result, RhythmNum, nRhythmNum);
    jintArray intArr = (*env)->NewIntArray(env, 15);
    (*env)->SetIntArrayRegion(env, intArr, 0, 15, arynRhythmMark);
    (*env)->SetObjectField(env, result, RhythmMark, intArr);
    (*env)->SetIntField(env, result, OverLoad, bOverLoad);

    (*env)->ReleaseIntArrayElements(env, array, p, 0);
    (*env)->DeleteLocalRef(env, intArr);
    (*env)->DeleteLocalRef(env, floatArr);
    return result;
};

/**
 * 获取心率
 */
JNIEXPORT jint JNICALL Java_com_electrocardio_jni_JniCallbackData_getHeartRate
        (JNIEnv *env, jobject obj) {
    jint nHR;
    nHR = Ecg_GetHeartRate();
    return nHR;
}

/**
 * 调用算法初始化
 */
JNIEXPORT void JNICALL Java_com_electrocardio_jni_JniCallbackData_getInitFromJni
        (JNIEnv *env, jobject thiz) {
    Ecg_Reset();
}

/**
 * ECG连续测量完成后获得结果数据
 */
JNIEXPORT jobject JNICALL Java_com_electrocardio_jni_JniCallbackData_EcgGetStatisitic
        (JNIEnv *env, jobject obj) {
    int nRNum, nHRAvg, nHRMax, nHRMin;
    int arydbHRPercent[5] = {0};
    jclass objectClass = (*env)->FindClass(env, "com/electrocardio/jni/CompleteECGResultBean");
    jfieldID RNum = (*env)->GetFieldID(env, objectClass, "nRNum", "I");
    jfieldID HRAvg = (*env)->GetFieldID(env, objectClass, "nHRAvg", "I");
    jfieldID HRMax = (*env)->GetFieldID(env, objectClass, "nHRMax", "I");
    jfieldID HRMin = (*env)->GetFieldID(env, objectClass, "nHRMin", "I");
    jfieldID HRPercent = (*env)->GetFieldID(env, objectClass, "arynHRPercent", "[I");
    Ecg_GetStatisitic(&nRNum, &nHRAvg, &nHRMax, &nHRMin, arydbHRPercent);
    jobject result = (*env)->AllocObject(env, objectClass);
    (*env)->SetIntField(env, result, RNum, nRNum);
    (*env)->SetIntField(env, result, HRAvg, nHRAvg);
    (*env)->SetIntField(env, result, HRMax, nHRMax);
    (*env)->SetIntField(env, result, HRMin, nHRMin);
    jintArray intArr = (*env)->NewIntArray(env, 5);
    (*env)->SetIntArrayRegion(env, intArr, 0, 5, arydbHRPercent);
    (*env)->SetObjectField(env, result, HRPercent, intArr);
    (*env)->DeleteLocalRef(env, intArr);// 释放局部变量
    return result;
}

JNIEXPORT jobject JNICALL Java_com_electrocardio_jni_JniCallbackData_EcgGetRealBeatVal
        (JNIEnv *env, jobject obj) {
    int nUpdateRNum;// 实时检测的R个数
    int arynRPos[4] = {0};// 各R波位置
    int arynBeatStyle[4] = {0};// 心搏类型
    int arynRR[4] = {0};// RR间期
    jclass objectClass = (*env)->FindClass(env, "com/electrocardio/jni/RealBeatVal");
    jfieldID nUpdateRNumF = (*env)->GetFieldID(env, objectClass, "nUpdateRnum", "I");
    jfieldID arynRPosF = (*env)->GetFieldID(env, objectClass, "arynRPos", "[I");
    jfieldID arynBeatStyleF = (*env)->GetFieldID(env, objectClass, "arynBeatStyle", "[I");
    jfieldID arynRRF = (*env)->GetFieldID(env, objectClass, "arynRR", "[I");
    Ecg_GetRealBeatVal(&nUpdateRNum, arynRPos, arynBeatStyle, arynRR);
    jobject result = (*env)->AllocObject(env, objectClass);
    (*env)->SetIntField(env, result, nUpdateRNumF, nUpdateRNum);

    jintArray arynRPosArr = (*env)->NewIntArray(env, 4);
    (*env)->SetIntArrayRegion(env, arynRPosArr, 0, 4, arynRPos);
    (*env)->SetObjectField(env, result, arynRPosF, arynRPosArr);

    jintArray arynBeatStyleArr = (*env)->NewIntArray(env, 4);
    (*env)->SetIntArrayRegion(env, arynBeatStyleArr, 0, 4, arynBeatStyle);
    (*env)->SetObjectField(env, result, arynBeatStyleF, arynBeatStyleArr);

    jintArray arynRRArr = (*env)->NewIntArray(env, 4);
    (*env)->SetIntArrayRegion(env, arynRRArr, 0, 4, arynRR);
    (*env)->SetObjectField(env, result, arynRRF, arynRRArr);

    (*env)->DeleteLocalRef(env, arynRPosArr);// 释放局部变量
    (*env)->DeleteLocalRef(env, arynBeatStyleArr);// 释放局部变量
    (*env)->DeleteLocalRef(env, arynRRArr);// 释放局部变量
    return result;
}

//JNIEXPORT jobject JNICALL Java_com_electrocardio_jni_JniCallbackData_getAlgorithmFrom
//        (JNIEnv *env, jobject thiz, jintArray array) {
//    // 实时每s节律事件个数
//    int nRhythmNum;
//    // 实时每s节律事件编号
//    int arynRhythmMark[RHYTHM_SORT];
//    // 实时每s节律事件起止点
//    int arypnRhythmPos[RHYTHM_SORT][2];
//    // 每2小时总R波个数。每够2小时输出为R波个数，否则为0。
//    int nSegRNum = 0;
//
//    jfloatArray returnArray = (*env)->NewFloatArray(env, 13);
//    float aryfDataShow[13] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//    int arynData[10];
//    int len = (*env)->GetArrayLength(env, array);
//    if (len == 0) {
//        return array;
//    }
//    jint *p = (*env)->GetIntArrayElements(env, array, 0);
//
//    int i = 0;
//    for (i; i < 10; i++) {
//        arynData[i] = *(p + i);
//    }
//
//    AnalysisECGWave(arynData, aryfDataShow, &nRhythmNum, arynRhythmMark);
//    jintArray returnIntArray = (*env)->NewIntArray(env, nRhythmNum);
//    //  jintArray  RhyIntArray = (*env)->NewIntArray(env, nRhythmNum*2);
//    int CaseCollection[nRhythmNum];
//    int arypnRhythmPosArray[nRhythmNum * 2];
//    int j = 0;
//    for (; j < nRhythmNum; ++j) {
//        CaseCollection[j] = arynRhythmMark[j];
//    }
//
//    jclass cls = (*env)->FindClass(env, "com/electrocardio/jni/OutObjectResultPacket");
//    jfieldID mArynRhythmMark = (*env)->GetFieldID(env, cls, "arynRhythmMark", "[I");
//    jfieldID intArrID = (*env)->GetFieldID(env, cls, "ecgData", "[F");
//
//    jobject resu = (*env)->AllocObject(env, cls);
//
//    (*env)->SetFloatArrayRegion(env, returnIntArray, 0, nRhythmNum, CaseCollection);
//    (*env)->SetObjectField(env, resu, mArynRhythmMark, returnIntArray);
//
//    (*env)->SetFloatArrayRegion(env, returnArray, 0, 13, aryfDataShow);
//    (*env)->SetObjectField(env, resu, intArrID, returnArray);
//
//    (*env)->ReleaseIntArrayElements(env, array, p, 0);
//    (*env)->DeleteLocalRef(env, returnArray); //释放
//    (*env)->DeleteLocalRef(env, returnIntArray);
//    return resu;
//}