//#include "stdafx.h"
#include <stdio.h> 
#include  "GlobVari.h"
#include  "EcgTypes.h"
#include  "Detector.h"
#include  "DetFuns.h" 
#include  "Ecg_InterFace.h"
#include  "R_rMain.h"
#include  "Tompkins.h"
#include  "GlobVari.h"
#include  "stdlib.h"

INT32 delaynoise = 0;

void Init_QrsDetection(void) {
    EcgVarInit();
    gLastEcgSetting.mGain = 1;
}

void rr_ana(int *nRPos) {
    gRDetEcgBuf = gOrigBuf;
    gRDetEcgBufPtr = timing * ECG_ANA_SAMPLE_RATE;
    gRGlobals.qrsClassified = 0;
    ECGProcess(ECG_ANA_SAMPLE_RATE);
    if (0) { ;
    }
    else {
        delaynoise = 0;
        if (gQrsAnaInfo.delay > 0) {
            gQrsAnaInfo.delay--;
        }
        else {
            if (IsECGLost(ECG_ANA_SAMPLE_RATE)) {
                gRGlobals.timePast = 0;
                gQrsAnaInfo.isEcgLost = TRUE;
                gRGlobals.validRRi = 0;
                gRGlobals.resetPeakDetect = 1;
                gStates.current = MAX_STATE;
                gRGlobals.counter = 0;
                if (gRGlobals.initialized == 0) {
                    EcgVarInit();
                }
            }
            else {
                gQrsAnaInfo.isEcgLost = FALSE;
            }
            if (gRGlobals.initialized == 0) {
                if (TRUE == gQrsAnaInfo.isEcgLost) {
                    gRGlobals.initLength = 0;
                    gRGlobals.initialized = 0;
                    gRGlobals.qrsPtr = 0;
                }
                else {
                    InitDetect();
                    if (1 == gRGlobals.initialized) {
                        if (FALSE == gEcgAnaConfig.mArrMonitor) {
                            gQrsAnaInfo.inLearning = FALSE;
                        }
                        else
                            gEcgAnaStatus.mEcgAnalysisStatus = ECG_ANA_STATUS_ARR_LRN;
                    }
                    else
                        gEcgAnaStatus.mEcgAnalysisStatus = ECG_ANA_STATUS_QRS_LEARN;
                }
            }
            else {
                if (FALSE == gQrsAnaInfo.isEcgLost) {
                    QRSDetector(ECG_ANA_SAMPLE_RATE, nRPos);
                }
                else
                    gRGlobals.qrsDetected = 0;
            }
        }
    }
    if (TimerFlag == 1) {
        gQrsAnaInfo.heartRate = GetHeartRate();
    }
}

INT32 GetHeartRate(void) {
    static INT32 HRHR = -100, lastHeartRate = -100;
    static INT32 previousMatched = 1;
    static INT32 outHR = 0;

    INT32 i, ptr;

    if ((TRUE == gQrsAnaInfo.isEcgLost) || (gRGlobals.timePast > (ECGLOST_THRESHOLD * 4)) ||
        (gRGlobals.initialized == 0)) {
        HRHR = -100;
    }
    else if (gQrsAnaInfo.delay > 0) { ;
    }
    else {
        for (i = gRGlobals.qrsDetected - 1; i >= 0; i--) {
            ptr = mod_once(gRGlobals.qrsPtr - i, MAX_QRS_NUM);

            if (previousMatched && gQrsComplex[ptr].matched && gRGlobals.validRRi) {
                HRHR = CalculateHeartRate(gQrsComplex[ptr].rri);
            }
            else {
                gRGlobals.validRRi = 1;
            }
            previousMatched = gQrsComplex[ptr].matched;

        }
    }
    if (outHR == 1 || lastHeartRate == -100) {
        lastHeartRate = HRHR;
        outHR = 0;
    }
    else {
        outHR = 1;
    }

    HeartRate = lastHeartRate;
    return HeartRate;
}

///////////////////////////////////////////////////////////////////////////
INT32 CalculateHeartRate(INT32 rrInterval) {
    static INT32 HR = -100;

    INT32 i, max, min, total, rri, validRRI;

    for (i = RRINTERVAL_NUM - 1; i > 0; i--) {
        rrIntervals[i] = rrIntervals[i - 1];
    }
    rrIntervals[0] = rrInterval;

    if ((_1200MS <= rrIntervals[0])
        && (_1200MS <= rrIntervals[1])
        && (_1200MS <= rrIntervals[2])) {
        rri = 4;
    }
    else
        rri = RRINTERVAL_NUM;

    validRRI = 0;
    for (i = 0; i < RRINTERVAL_NUM; i++) {
        if (0 < rrIntervals[i])
            validRRI++;
    }

    if (5 > validRRI)
        return HR;

    total = 0;
    max = 0;
    min = 5120;

    if (RRINTERVAL_NUM == rri)
        rri = validRRI;

    for (i = 0; i < rri; i++) {
        if (rrIntervals[i] > max)
            max = rrIntervals[i];

        if (rrIntervals[i] < min)
            min = rrIntervals[i];

        total += rrIntervals[i];
    }

    if (rri > 4) {
        total -= min + max;
        rri -= 2;
    }

    if (total != 0) {
        HR = 600 * rri * ECG_ANA_SAMPLE_RATE / total;
        HR = (HR + 4) / 10;
    }
    else {
        HR = -100;
    }
    return HR;
}

///////////////////////////////////////////////////////////////////////////
INT32 IsSaturation(INT32 sample) {
    INT32 i, ptr, delay, result;
    INT32 data;

    delay = 0;
    ptr = mod_once(gRDetEcgBufPtr - sample, ECG_ANA_BUF_LEN);
    for (i = 0; i < sample; i++) {
        data = *(gRDetEcgBuf + ptr);
        ptr += 1;
        if (ptr >= ECG_ANA_BUF_LEN)
            ptr = 0;

        if (abs(data) < 10000) {
            delay++;
        }
    }
    //
    if (delay > (FASTCHANGE_TIME * 20)) {
        result = 0;
    }
    else {
        result = 1;
    }
    return result;
}

////////////////////////////////////////////////////////////////////////////
CHAR IsECGLost(INT32 sample) {
    INT32 ABSV = 0;
    ABSV = abs(gOrigBuf[mod_once(ThreeSecond - 1, ECG_ANA_BUF_LEN)]);
    if (40 >= ABSV) {
        BlankLength++;
    }
    else {
        BlankLength = 0;
    }

    if (ECGLOST_THRESHOLD < BlankLength)
        return TRUE;
    else
        return FALSE;
}

////////////////////////////////////////////////////////////////////////////
void InitDetect(void) {
    INT32 i;

    InitThreshold(&gThreshold);

    if (gRGlobals.initialized == 1) {
        gRGlobals.thd = gThreshold.normal;
        gRGlobals.lastRPos = gRDetEcgBufPtr;
        gRGlobals.lastOnset = gRDetEcgBufPtr;

        for (i = 0; i < 8; i++) {
            gRRIntervals[i] = 250;
        }

        gRGlobals.aveRRi = 1000 / 4;
        gRGlobals.rrml = gRGlobals.aveRRi * 3 / 2;
        gRGlobals.timePast = 0;
        gRGlobals.qrsDetected = 0;
    }
}