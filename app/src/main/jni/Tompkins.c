//#include "stdafx.h"
#include "stdio.h"
#include "stdlib.h"
#include "DataType.h"
#include "EcgTypes.h"
#include "DetFuns.h"
#include "GlobVari.h"
#include "Tompkins.h"
#include "Detector.h"
#include "R_rMain.h"
#include "AnalysisWave.h"

INT32 MIN_ORGSLOPE[4] = {20, 40, 80, 160};

/////////////////////////////////////////////////////////////////////////////
INT32 BPF(INT32 data, INT32 *lowpassResult) {
    static INT32 originData[13] = {128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128};
    static INT32 lowpassData[33];
    static INT32 highpassData = 0;
    static CHAR ord_ptr0 = 0;
    static CHAR lpd_ptr0 = 0;
    CHAR ord_ptr6, ord_ptr12;
    CHAR lpd_ptr1, lpd_ptr2, lpd_ptr16, lpd_ptr17, lpd_ptr32;

    ord_ptr0 = LOOP_DEC(ord_ptr0, 13);
    ord_ptr6 = ord_ptr0 + 6 >= 13 ? ord_ptr0 - 7 : ord_ptr0 + 6;
    ord_ptr12 = ord_ptr0 + 12 >= 13 ? ord_ptr0 - 1 : ord_ptr0 + 12;

    lpd_ptr0 = LOOP_DEC(lpd_ptr0, 33);
    lpd_ptr1 = LOOP_INC(lpd_ptr0, 33);
    lpd_ptr2 = LOOP_INC(lpd_ptr1, 33);
    lpd_ptr16 = lpd_ptr0 + 16 >= 33 ? lpd_ptr0 - 17 : lpd_ptr0 + 16;
    lpd_ptr17 = LOOP_INC(lpd_ptr16, 33);
    lpd_ptr32 = lpd_ptr0 + 32 >= 33 ? lpd_ptr0 - 1 : lpd_ptr0 + 32;
    originData[ord_ptr0] = data;
    lowpassData[lpd_ptr0] = originData[ord_ptr0]
                            - 2 * originData[ord_ptr6] + originData[ord_ptr12]
                            + 2 * lowpassData[lpd_ptr1] - lowpassData[lpd_ptr2];
    *lowpassResult = lowpassData[lpd_ptr0];
    highpassData = (-lowpassData[lpd_ptr0] / 32 + lowpassData[lpd_ptr16] - lowpassData[lpd_ptr17] +
                    lowpassData[lpd_ptr32] / 32
                    + highpassData);

    return highpassData;
}

INT32 MWI(INT32 diffData) {
    static INT32 diffs[MWIN_WIDTH];
    static INT32 pointer = 0;
    static INT32 Sum = 0;
    INT32 tmp;
    diffData = (diffData < 0 ? -diffData : diffData);
    tmp = (diffData * diffData);

    Sum -= diffs[pointer];
    Sum += tmp;
    diffs[pointer] = tmp;
    pointer = LOOP_INC(pointer, MWIN_WIDTH);

    return (Sum / (MWIN_WIDTH));
}

/////////////////////////////////////////////////////////////////////////////
INT32 BandPassFilter(INT32 data, INT32 *lowpassResult) {
    static INT32 originData[13] = {128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128};
    static INT32 lowpassData[33];
    static INT32 highpassData = 0;
    static CHAR ord_ptr0 = 0;
    static CHAR lpd_ptr0 = 0;
    CHAR ord_ptr6, ord_ptr12;
    CHAR lpd_ptr1, lpd_ptr2, lpd_ptr16, lpd_ptr17, lpd_ptr32;

    ord_ptr0 = LOOP_DEC(ord_ptr0, 13);
    ord_ptr6 = ord_ptr0 + 6 >= 13 ? ord_ptr0 - 7 : ord_ptr0 + 6;
    ord_ptr12 = ord_ptr0 + 12 >= 13 ? ord_ptr0 - 1 : ord_ptr0 + 12;

    lpd_ptr0 = LOOP_DEC(lpd_ptr0, 33);
    lpd_ptr1 = LOOP_INC(lpd_ptr0, 33);
    lpd_ptr2 = LOOP_INC(lpd_ptr1, 33);
    lpd_ptr16 = lpd_ptr0 + 16 >= 33 ? lpd_ptr0 - 17 : lpd_ptr0 + 16;
    lpd_ptr17 = LOOP_INC(lpd_ptr16, 33);
    lpd_ptr32 = lpd_ptr0 + 32 >= 33 ? lpd_ptr0 - 1 : lpd_ptr0 + 32;
    originData[ord_ptr0] = data;
    lowpassData[lpd_ptr0] = originData[ord_ptr0]
                            - 2 * originData[ord_ptr6] + originData[ord_ptr12]
                            + 2 * lowpassData[lpd_ptr1] - lowpassData[lpd_ptr2];
    *lowpassResult = lowpassData[lpd_ptr0];
    highpassData = (-lowpassData[lpd_ptr0] / 32 + lowpassData[lpd_ptr16] - lowpassData[lpd_ptr17] +
                    lowpassData[lpd_ptr32] / 32
                    + highpassData);

    return highpassData;
}

/////////////////////////////////////////////////////////////////////////////
INT32 Differential(INT32 data[5], INT32 input) {
    INT32 i;

    for (i = 3; i >= 0; i--) {
        data[i + 1] = data[i];
    }
    data[0] = input;

    return (data[0] - data[4]) * 2 + (data[1] - data[3]);
}

/////////////////////////////////////////////////////////////////////////////
INT32 MWIntegration(INT32 diffData) {
    static INT32 diffs[MWIN_WIDTH];
    static INT32 pointer = 0;
    static INT32 Sum = 0;
    unsigned int tmp;
    diffData = (diffData < 0 ? -diffData : diffData);
    tmp = (diffData * diffData);
    Sum -= diffs[pointer];
    Sum += tmp;
    diffs[pointer] = tmp;
    pointer = LOOP_INC(pointer, MWIN_WIDTH);

    return (Sum / (MWIN_WIDTH));
}

////////////////////////////////////////////////////////////////////////////
INT32 TompkinsPeakDetect(INT32 value, INT32 pos, INT32 *reset, PEAK *result) {
    static int flag = 0, height, height0, time, time0, x0 = 1000, pos0;
    int watch;

    if (*reset) {
        flag = 0;
        *reset = 0;
    }

    switch (flag) {
        case 0:
            if (value > x0) {
                height0 = x0;
                height = value;
                time = pos;
                time0 = pos0;
                flag = 1;
            }
            break;

        case 1:

            if (mod_once(pos - time0, ECG_ANA_BUF_LEN) > 1000) {
                flag = 0;
                break;
            }

            if (value > x0) {
                if (value > height) {
                    height = value;
                    time = pos;
                }
            }
            else {
                watch = (height0 + height) / 2;
                if ((value < watch) || (mod_once(pos - time, ECG_ANA_BUF_LEN) > 32)) {
                    flag = 0;
                    x0 = value;
                    pos0 = pos;
                    if (mod_once(time - time0, ECG_ANA_BUF_LEN) < MIN_PEAK_WIDTH) {
                        return 0;
                    }
                    else {
                        result->onsetPos = time0;
                        result->onsetHeight = height0;
                        result->peakPos = time;
                        result->peakHeight = height;
                        return 1;
                    }
                }
            }
    }

    x0 = value;
    pos0 = pos;
    return 0;
}

/////////////////////////////////////////////////////////////////////////////
INT32 median(INT32 list[], INT32 n, INT32 m) {
    INT32 left, right, pivot, temp;
    INT32 i, j;
    INT32 LIST[100];

    for (i = 0; i < n; i++) {
        *(LIST + i) = *(list + i);
    }

    left = 0;
    right = n - 1;
    while (left < right) {
        pivot = LIST[m - 1];
        i = left;
        j = right;

        do {
            while (pivot < LIST[j]) j--;
            while (pivot > LIST[i]) i++;
            if (i <= j) {
                temp = LIST[i];
                LIST[i] = LIST[j];
                LIST[j] = temp;
                i++;
                j--;
            }
        } while (i <= j);

        if (j < m - 1) left = i;
        if (i > m - 1) right = j;
    }
    return LIST[m - 1];
}

/////////////////////////////////////////////////////////////////////////////
void UpdateLists(INT32 rrw, INT32 speak, INT32 npeak) {
    INT32 i;
    for (i = 6; i >= 0; i--) {
        gSignalPeaks[i + 1] = gSignalPeaks[i];
        gNoisePeaks[i + 1] = gNoisePeaks[i];
        gRRIntervals[i + 1] = gRRIntervals[i];
    }
    gSignalPeaks[0] = speak;
    gNoisePeaks[0] = npeak;
    gRRIntervals[0] = rrw;

    gRGlobals.aveRRi = median(gRRIntervals, 8, 4);
    gRGlobals.rrml = gRGlobals.aveRRi * 7 / 4;
}

/////////////////////////////////////////////////////////////////////////////
INT32 UpdateThd(Threshold *threshold) {
    INT32 pm, nm;
    INT32 result;

    result = 1;
    pm = median(gSignalPeaks, 8, 4);
    nm = median(gNoisePeaks, 8, 4);

    threshold->normal = ((pm - nm) / 8) + nm;
    threshold->backed = threshold->normal / 2;
    if (threshold->normal < MIN_THRESHOLD) {
        threshold->normal = MIN_THRESHOLD;
        result = 1;
    }
    if (threshold->backed < MIN_THRESHOLD / 2) {
        threshold->backed = MIN_THRESHOLD;
        result = 1;
    }
    return result;
}

/////////////////////////////////////////////////////////////////////////////
INT32 Localize(INT32 pos, INT32 *exgPos, INT32 *maxSlopePos) {
    INT32 i, maxNPos, maxPPos, startPos, endPos, lastData, maxPValue, maxNValue;
    INT32 lastValue, currValue, maxPos;

    startPos = mod_once(pos - SEARCH_WIDTH, ECG_ANA_BUF_LEN);
    if ((startPos >= ECG_ANA_BUF_LEN) || (startPos < 0)) {
        startPos = 0;
    }


    maxPPos = startPos;
    maxNPos = startPos;
    maxPValue = maxNValue = 0;

    lastValue = 0;

    for (i = 0; i < SEARCH_WIDTH; i++) {
        startPos = mod(startPos, ECG_ANA_BUF_LEN);
        currValue = *(gDiffBuf + startPos);

        if (currValue > maxPValue) {
            if (lastValue <= 0) {
                if (currValue > maxPValue * 3 / 2) {
                    maxPPos = startPos;
                    maxPValue = currValue;
                    lastValue = currValue;
                }
            }
            else {
                maxPPos = startPos;
                maxPValue = currValue;
            }
        }
        else if (currValue < maxNValue) {
            if (lastValue >= 0) {
                if (currValue < maxNValue * 3 / 2) {
                    maxNPos = startPos;
                    maxNValue = currValue;
                    lastValue = currValue;
                }
            }
            else {
                maxNPos = startPos;
                maxNValue = currValue;
            }
        }
        startPos = LOOP_INC(startPos, ECG_ANA_BUF_LEN);
    }


    if (((*(gDiffBuf + maxPPos) <= 0) && (*(gDiffBuf + maxNPos) <= 0)) ||
        ((*(gDiffBuf + maxPPos) > 0) && (*(gDiffBuf + maxNPos) > 0))) {
        return -1;
    }

    maxPos = maxNPos;
    if (*(gDiffBuf + maxPPos) > -*(gDiffBuf + maxPos))
        maxPos = maxPPos;

    if (*(gDiffBuf + maxPPos) > -*(gDiffBuf + maxNPos) * 9 / 10) {
        *maxSlopePos = maxPPos;
    }
    else {
        *maxSlopePos = maxNPos;
    }

    if (((maxPPos - maxNPos) > 0 && (maxPPos - maxNPos) < SEARCH_WIDTH)
        || ((maxPPos - maxNPos) < 0 && (maxNPos - maxPPos) > SEARCH_WIDTH)) {
        startPos = maxNPos;
        endPos = maxPPos;
    }
    else {
        startPos = maxPPos;
        endPos = maxNPos;
    }

    *exgPos = startPos;
    lastData = *(gDiffBuf + startPos);
    while (startPos != endPos) {
        startPos = LOOP_INC(startPos, ECG_ANA_BUF_LEN);
        if ((*(gDiffBuf + startPos) >> 8) * (lastData >> 8) <= 0) {
            *exgPos = startPos;
            break;
        }
        lastData = *(gDiffBuf + startPos);
    }

    return abs(*(gDiffBuf + maxPos));
}

/////////////////////////////////////////////////////////////////////////////
void findR(INT32 data, INT32 ptr, int *nRPos) {
    INT32 thdNormal, thdBacked, times;
    PEAK result;

    gRGlobals.timePast++;
    thdNormal = gThreshold.normal;
    if (gRGlobals.aveRRi < _1000MS) {
        times = gRGlobals.timePast / gRGlobals.aveRRi;
    }
    else {
        times = gRGlobals.timePast / _1000MS;
    }
    if (times >= 2) {
        if (times > 15 && gThreshold.normal < 10000) {
            times = 15;
        }
        if (gThreshold.normal >= 15000) {
            times = times * 2;
        }
        if (gThreshold.normal >= 30000) {
            times = times * 2 * 2;
        }
        thdNormal = gThreshold.normal / (times * 2);
    }

    thdBacked = thdNormal / 2;
    if (thdNormal < MWI_THRESHOLD / 2)
        thdNormal = MWI_THRESHOLD / 2;
    if (thdBacked < MWI_THRESHOLD / 2)
        thdBacked = MWI_THRESHOLD / 2;
    if (TompkinsPeakDetect(data, ptr, &(gRGlobals.resetPeakDetect), &result)) {
        if (result.peakHeight >= thdNormal) {
            gPeak.onsetPos = result.onsetPos;
            gPeak.pos = result.peakPos;
            gPeak.height = result.peakHeight;
            gPeak.buf_pos = ptr;
            decision(0, &gPeak, times, thdNormal, nRPos);
        }
        else {
            if (result.peakHeight >= gNoises[1].height) {
                gNoises[0] = gNoises[1];
                gNoises[1].onsetPos = result.onsetPos;
                gNoises[1].pos = result.peakPos;
                gNoises[1].height = result.peakHeight;
                gNoises[1].buf_pos = ptr;
                gNoises[1].searched = 0;
            }
            else if (result.peakHeight >= gNoises[0].height) {
                gNoises[0].onsetPos = result.onsetPos;
                gNoises[0].pos = result.peakPos;
                gNoises[0].height = result.peakHeight;
                gNoises[0].buf_pos = ptr;
                gNoises[0].searched = 0;
            }
        }
    }
    else {
        if ((!gNoises[1].searched || !gNoises[0].searched) && gRGlobals.timePast > gRGlobals.rrml) {
            if (!gNoises[1].searched) {
                if (gNoises[1].height > thdBacked) {
                    decision(1, &gNoises[1], times, thdBacked, nRPos);
                }
                gNoises[1].searched = 1;
            }
            else {
                if (gNoises[0].height > thdBacked) {
                    decision(1, &gNoises[0], times, thdBacked, nRPos);
                }
                gNoises[0].searched = 1;
            }
        }
    }
    return;
}

/////////////////////////////////////////////////////////////////////////////
void decision(INT32 isnoise, PEAKINFO *peak, INT32 times, INT32 usedThd, int *nRPos) {
    static INT32 qrsWidth = 0, MINIMAL_RRI[3] = {35, 32, 32};
    INT32 rrWidth = 0, rPosition = 0, peakPosition = 0;
    INT32 i, currSlope, maxSlopePos, maxLPSlope, actual_width;
    RSquare rSquare;
    BOOL is_valid_qrs = FALSE;
    // syn[2016/1/30] 记录R波位置
    static int nPreRPos = 0;
    // syn[2016/4/9] 判断是否干扰误检
    int bIsRPos = 0;
    /////////////////////////////////////////////////////////////////////////
    currSlope = Localize(peak->pos, &rPosition, &maxSlopePos);
    peakPosition = mod(rPosition - 2, ECG_ANA_BUF_LEN);
    LPLocalize(peak->pos, &rSquare, &maxLPSlope);
    actual_width = Qrs_Width(
            mod_once(rSquare.left - FILTER_DELAY + HIGHPASS_DELAY + 2, ECG_ANA_BUF_LEN),
            mod_once(rSquare.right - FILTER_DELAY + HIGHPASS_DELAY + 2, ECG_ANA_BUF_LEN));//
    if ((actual_width >= 2) && (gEcgAnaConfig.mFilterMode == 0)) {
        is_valid_qrs = TRUE;
    }
    else {
        is_valid_qrs = FALSE;
    }
    /////////////////////////////////////////////////////////////////////////
    if (currSlope == -1) {
        rrWidth = 0;
    }
    else {
        rPosition = mod_once(rPosition - FILTER_DELAY, ECG_ANA_BUF_LEN);
        maxSlopePos = rPosition;

        // syn[2016/4/8]
// 		rPosition = mod_once(rPosition - FILTER_DELAY, ECG_ANA_BUF_LEN);
// 		maxSlopePos = rPosition;

        rrWidth = mod_once(maxSlopePos - gRGlobals.lastRPos, ECG_ANA_BUF_LEN);

        if ((gRGlobals.timePast >= (ECG_ANA_BUF_LEN - 10)) &&
            (gRGlobals.timePast <= (ECG_ANA_BUF_LEN + 10))) {
            rrWidth = ECG_ANA_BUF_LEN;
        }
        if ((gRGlobals.timePast >= (ECG_ANA_BUF_LEN - 10)) && (maxSlopePos == gRGlobals.lastRPos)) {
            rrWidth = ECG_ANA_BUF_LEN;
        }
    }
    if ((rrWidth < MINIMAL_RRI[gEcgAnaConfig.mPatientType])
        || (rrWidth < 90 && ((currSlope * (2 + isnoise + isnoise) < gRGlobals.lastSlope) ||
                             ((actual_width - qrsWidth) >= 4)))
        || (!isnoise && currSlope > (gRGlobals.lastSlope << 4))
        || (!isnoise && (currSlope << 4) < gRGlobals.lastSlope)
        || (is_valid_qrs == FALSE)
            ) {
        qrsWidth = actual_width;
        if (!isnoise) {
            gNoises[0] = gNoises[1];
            gNoises[1] = *peak;
            gNoises[1].searched = 0;
        }
        return;
    }
    qrsWidth = actual_width;

    // syn[2016/4/9] 判断是否干扰误检
    bIsRPos = ConfirmRPos(gOrigBuf, maxSlopePos);
    if (!bIsRPos) {
        return;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////03
    gRGlobals.qrsDetected++;

    // syn[2016/4/9] 记录检测到的R波位置，作为参数传出给诊断用
// 	if (0 == nPreRPos)
// 	{
// 		*nRPos = maxSlopePos + SecondCount / 4 * ECG_ANA_SAMPLE_RATE * 4;
//  	}
// 	else
// 	{
// 		*nRPos = nPreRPos + rrWidth;
// 	}
    // syn[2016/4/23] 修改R波检测记录
    if (maxSlopePos < ECG_ANA_SAMPLE_RATE * 2) {
        *nRPos = maxSlopePos + SecondCount / 4 * ECG_ANA_SAMPLE_RATE * 4;
    }
    else {
        *nRPos = maxSlopePos + (SecondCount - 1) / 4 * ECG_ANA_SAMPLE_RATE * 4;
    }
    nPreRPos = *nRPos;

    gRGlobals.qrsPtr = mod_once(gRGlobals.qrsPtr + 1, MAX_QRS_NUM);
    gRGlobals.lastRPos = maxSlopePos;
    gQrsComplex[gRGlobals.qrsPtr].is_peak = TRUE;
    gQrsComplex[gRGlobals.qrsPtr].threshold = usedThd;
    gQrsComplex[gRGlobals.qrsPtr].rri = rrWidth;
    gQrsComplex[gRGlobals.qrsPtr].peakPos = 0;

    gQrsComplex[gRGlobals.qrsPtr].peak2end = mod_once(
            gQrsComplex[gRGlobals.qrsPtr].fiducial - peakPosition, ECG_ANA_BUF_LEN);

    if (gQrsComplex[gRGlobals.qrsPtr].peak2end > MAX_PEAK2END)
        gQrsComplex[gRGlobals.qrsPtr].peak2end = MAX_PEAK2END;
    gQrsComplex[gRGlobals.qrsPtr].onRRi = 0;
    gQrsComplex[gRGlobals.qrsPtr].onset = 0;
    gQrsComplex[gRGlobals.qrsPtr].width = mod_once(rSquare.right - rSquare.left, ECG_ANA_BUF_LEN);
    gRGlobals.maxSlope = UpdateMaxSlope(maxLPSlope);

    gQrsComplex[gRGlobals.qrsPtr].actualWidth = 0;
    gQrsComplex[gRGlobals.qrsPtr].matched = 1;

    gQrsComplex[gRGlobals.qrsPtr].noise = 0;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////04
    gStates.averageRRi = (gStates.averageRRi * 3 + gQrsComplex[gRGlobals.qrsPtr].rri) / 4;
    UpdateLists(rrWidth, peak->height, gNoises[1].height);

    if (times >= 8) {
        for (i = 0; i < 3; i++)
            UpdateLists(rrWidth, peak->height, gNoises[1].height);
    }

    gRGlobals.lastSlope = currSlope;

    gNoises[1].height = gNoises[0].height = 0;

    UpdateThd(&gThreshold);

    if (isnoise) {
        gRGlobals.timePast = gRGlobals.timePast - rrWidth;
        if (gRGlobals.timePast < 0)
            gRGlobals.timePast = 0;
    }
    else
        gRGlobals.timePast = 0;

    gRGlobals.lastOnset = 0;
    gRGlobals.aveSlope = (gRGlobals.aveSlope * 7 + gRGlobals.lastSlope) / 8;

    return;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：排除干扰误检
% 输入参数：arynData		当前4s心电波形
%			nRPos			检测到的R波位置
% 输出参数：bIsRPos			是否R波，是1，否0
% 编辑日期：2016/4/9
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int ConfirmRPos(int arynData[ECG_ANA_SAMPLE_RATE * 4], int nRPos) {
    // R向后搜索范围，暂定100ms，25个采样点
    int c_nFindThr = 0.1 * ECG_ANA_SAMPLE_RATE;
    int nStart, nEnd;
    int i, nMax, nMin;

    // 输出初始化
    int bIsRPos = 0;

    // 搜索起点、终点
    nStart = nRPos - c_nFindThr;
    if (nStart < 0) {
        nStart = 0;
    }
    nEnd = nRPos + c_nFindThr;
    if (nEnd >= ECG_ANA_SAMPLE_RATE * 4 - 1) {
        nEnd = ECG_ANA_SAMPLE_RATE * 4 - 1;
    }
    // 搜索幅度最大值、最小值
    nMax = arynData[nStart];
    nMin = arynData[nStart];
    for (i = nStart; i <= nEnd; i++) {
        if (arynData[i] > nMax) {
            nMax = arynData[i];
        }
        if (arynData[i] < nMin) {
            nMin = arynData[i];
        }
    }
    // 幅度最大最小差值小于0.2mv，则为误检
    if (abs(nMax - nMin) <= 0.3 * MV_GAIN) {
        return bIsRPos;
    }
    else {
        bIsRPos = 1;
    }

    return bIsRPos;
}

/////////////////////////////////////////////////////////////////////////////
INT32 UpdateMaxSlope(INT32 maxSlope) {
    INT32 i;
    for (i = 6; i >= 0; i--) {
        gMaxSlopes[i + 1] = gMaxSlopes[i];
    }
    gMaxSlopes[0] = maxSlope;

    return median(gMaxSlopes, 8, 5);
}

//
INT32 gNoiseSlope[8] = {0, 0, 0, 0, 0, 0, 0, 0};

INT32 UpdateNoiseSlope(INT32 NoiseSlope) {
    INT32 i;
    for (i = 6; i >= 0; i--) {
        gNoiseSlope[i + 1] = gNoiseSlope[i];
    }
    gNoiseSlope[0] = NoiseSlope;

    return median(gNoiseSlope, 8, 5);
}

/////////////////////////////////////////////////////////////////////////////
void LPLocalize(INT32 pos, RSquare *rSquare, INT32 *maxSlope) {
    INT32 i, maxNPos, maxPPos, startPos, endPos, counter, slope;


    startPos = mod_once(pos - SEARCH_WIDTH - 16, ECG_ANA_BUF_LEN);
    maxPPos = startPos;
    maxNPos = startPos;

    for (i = 0; i < SEARCH_WIDTH; i++) {
        startPos = LOOP_INC(startPos, ECG_ANA_BUF_LEN);
        if (*(gLpDiffBuf + startPos) > *(gLpDiffBuf + maxPPos)) {
            maxPPos = startPos;
        }
        else if (*(gLpDiffBuf + startPos) < *(gLpDiffBuf + maxNPos)) {
            maxNPos = startPos;
        }

    }

    if (*(gLpDiffBuf + maxPPos) > -*(gLpDiffBuf + maxNPos))
        *maxSlope = *(gLpDiffBuf + maxPPos);
    else
        *maxSlope = -*(gLpDiffBuf + maxNPos);

    if (((maxPPos - maxNPos) > 0 && (maxPPos - maxNPos) < EDGE_SEARCH_WIDTH)
        || ((maxPPos - maxNPos) < 0 && (maxNPos - maxPPos) > EDGE_SEARCH_WIDTH)) {
        startPos = maxNPos;
        endPos = maxPPos;
    }
    else {
        startPos = maxPPos;
        endPos = maxNPos;
    }

    slope = abs(*(gLpDiffBuf + startPos) / 5);
    if (slope < MIN_SLOPE) slope = MIN_SLOPE;
    counter = 0;
    for (i = 0; i < EDGE_MAX_WIDTH; i++) {
        startPos = LOOP_DEC(startPos, ECG_ANA_BUF_LEN);
        if (abs(*(gLpDiffBuf + startPos)) <= slope) {
            if (counter == (WIDTH_SEARCHED - 3))
                break;
            else
                counter++;
        }
        else {
            counter = 0;
        }
    }
    rSquare->left = mod_once(startPos + WIDTH_SEARCHED + 1, ECG_ANA_BUF_LEN);

    slope = abs(*(gLpDiffBuf + endPos) / 5);
    if (slope < MIN_SLOPE) slope = MIN_SLOPE;
    counter = 0;
    for (i = 0; i < EDGE_MAX_WIDTH; i++) {
        endPos = LOOP_INC(endPos, ECG_ANA_BUF_LEN);
        if (abs(*(gLpDiffBuf + endPos)) <= slope) {
            if (counter == (WIDTH_SEARCHED - 3)) {
                break;
            }
            else {
                counter++;
            }
        }
        else {
            counter = 0;
        }
    }
    rSquare->right = mod_once(endPos - WIDTH_SEARCHED - 1, ECG_ANA_BUF_LEN);
}

/////////////////////////////////////////////////////////////////////////////
INT32 Qrs_Width(INT32 left, INT32 right) {
    INT32 i;
    INT32 baseline, width, baseline2, width2;
    INT32 max, min, pos_max, pos_min;
    max = *(gRDetEcgBuf + left);
    min = max;
    pos_max = left;
    pos_min = left;
    i = left;
    while (i != right) {
        if (max < *(gRDetEcgBuf + i)) {
            max = *(gRDetEcgBuf + i);
            pos_max = i;
        }
        else if (min > *(gRDetEcgBuf + i)) {
            min = *(gRDetEcgBuf + i);
            pos_min = i;
        }

        i = LOOP_INC(i, ECG_ANA_BUF_LEN);
    }
    baseline = *(gRDetEcgBuf + left)
               + *(gRDetEcgBuf + mod(left - 1 + ECG_ANA_BUF_LEN, ECG_ANA_BUF_LEN))
               + *(gRDetEcgBuf + mod(left + 1 + ECG_ANA_BUF_LEN, ECG_ANA_BUF_LEN))
               + *(gRDetEcgBuf + right)
               + *(gRDetEcgBuf + mod(right - 1 + ECG_ANA_BUF_LEN, ECG_ANA_BUF_LEN))
               + *(gRDetEcgBuf + mod(right + 1 + ECG_ANA_BUF_LEN, ECG_ANA_BUF_LEN));
    baseline /= 6;
    if ((max - baseline) > (baseline - min)) {
        baseline2 = baseline + (max - baseline) / 3;
        baseline += (max - baseline) / 4;
        //
        width = 0;
        i = pos_max;
        while ((i != right) && (*(gRDetEcgBuf + i) >= baseline)) {
            width++;
            i = LOOP_INC(i, ECG_ANA_BUF_LEN);
        }
        i = pos_max;
        while ((i != left) && (*(gRDetEcgBuf + i) >= baseline)) {
            width++;
            i = LOOP_DEC(i, ECG_ANA_BUF_LEN);
        }
        if ((pos_max != left) && (pos_max != right)) {
            width -= 1;
        }
        i = left;
        width2 = 0;
        while (i != right) {
            if (*(gRDetEcgBuf + i) >= baseline2) {
                width2++;
            }
            i = LOOP_INC(i, ECG_ANA_BUF_LEN);
        }
    }
    else {
        baseline2 = baseline + (min - baseline) / 3;
        baseline += (min - baseline) / 4;
        //
        width = 0;
        i = pos_min;
        while ((i != right) && (*(gRDetEcgBuf + i) <= baseline)) {
            width++;
            i = LOOP_INC(i, ECG_ANA_BUF_LEN);
        }
        i = pos_min;
        while ((i != left) && (*(gRDetEcgBuf + i) <= baseline)) {
            width++;
            i = LOOP_DEC(i, ECG_ANA_BUF_LEN);
        }
        if ((pos_min != left) && (pos_min != right)) {
            width -= 1;
        }
        i = left;
        width2 = 0;
        while (i != right) {
            if (*(gRDetEcgBuf + i) <= baseline2) {
                width2++;
            }
            i = LOOP_INC(i, ECG_ANA_BUF_LEN);
        }
    }

    return width > width2 ? width : width2;
}