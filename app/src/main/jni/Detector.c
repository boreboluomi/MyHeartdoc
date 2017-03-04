//#include "stdafx.h"
#include "Detector.h"
#include "DataType.h"
#include "EcgTypes.h"
#include "DetFuns.h"
#include "GlobVari.h"
#include "Tompkins.h"
#include "stdio.h"
#include "stdlib.h"

/////////////////////////////////////////////////////////////////////////////
void ECGProcess(INT32 datanum) {
    INT32 tmp, ptr, lowpassResult;
    //FILE *fp0,*fp1,*fp2,*fp3;
    ptr = mod_once(ThreeSecond - 1, ECG_ANA_BUF_LEN);
    // 	fp0=fopen("E:\\09TestDataCode\\MATALB_DataBase\\gOringBuf.dat","ab");
    // 	fp1=fopen("E:\\09TestDataCode\\MATALB_DataBase\\gLpDiffBuf.dat","ab");
    // 	fp2=fopen("E:\\09TestDataCode\\MATALB_DataBase\\gDiffBuf.dat","ab");
    // 	fp3=fopen("E:\\09TestDataCode\\MATALB_DataBase\\gMWIBuf.dat","ab");

    tmp = BandPassFilter((*(gRDetEcgBuf + ptr)) / 2, &lowpassResult);
    *(gDiffBuf + ptr) = tmp;
    //fwrite((gRDetEcgBuf + ptr),sizeof(int),1,fp0);

    tmp = Differential(gLpDiffData, lowpassResult);
    *(gLpDiffBuf + ptr) = tmp >> 3;
    //fwrite((gLpDiffBuf + ptr),sizeof(int),1,fp1);

    tmp = Differential(gBpDiffData, *(gDiffBuf + ptr));
    *(gDiffBuf + ptr) = tmp >> 6;
    //fwrite((gDiffBuf + ptr),sizeof(int),1,fp2);

    tmp = MWIntegration(*(gDiffBuf + ptr));
    *(gMWIBuf + ptr) = tmp;
    //fwrite((gMWIBuf + ptr),sizeof(int),1,fp3);
    // 	fclose(fp0);
    // 	fclose(fp1);
    // 	fclose(fp2);
    // 	fclose(fp3);
}

/////////////////////////////////////////////////////////////////////////////
void InitThreshold(Threshold *threshold) {
    INT32 max, maxPos, maxSlopePos;
    INT32 i, len, length;

    if (ThreeSecond >= (ECG_ANA_SAMPLE_RATE * 3)) {
        if (gRGlobals.initLength >= INITLEN) {
            length = INITLEN * ECG_ANA_SAMPLE_RATE;
            len = mod_once(gRDetEcgBufPtr - length, ECG_ANA_BUF_LEN);
            max = MakeThd(len, length, &maxPos);
            for (i = 0; i < 8; i++) {
                gSignalPeaks[i] = max;
                gNoisePeaks[i] = max / 8;
            }
            if (UpdateThd(threshold)) {
                gRGlobals.lastSlope = Localize(maxPos, &maxPos, &maxSlopePos);
                gRGlobals.aveSlope = gRGlobals.lastSlope;

                for (i = 0; i < 8; i++) {
                    gMaxSlopes[i] = gRGlobals.maxSlope;
                }
                if (gRGlobals.lastSlope > 0)
                    gRGlobals.initialized = 1;
            }
        }
        gRGlobals.initLength++;
    }
}

////////////////////////////////////////////////////////////////////////////
INT32 MakeThd(INT32 startPos, INT32 length, INT32 *peakPosition) {
    PEAK result;

    INT32 i, j, ptr, data, peakSelected, secondMaxPeak;
    INT32 peaks[2][PEAKNUM], minPeakPos, peakNum;

    for (i = 0; i < PEAKNUM; i++) {
        peaks[0][i] = 0;
        peaks[1][i] = 0;
    }

    ptr = startPos;
    for (j = 0; j < length; j++) {
        data = *(gMWIBuf + ptr);
        if (TompkinsPeakDetect(data, ptr, &(gRGlobals.resetPeakDetect), &result)) {
            minPeakPos = 0;
            for (i = 0; i < PEAKNUM; i++) {
                if (peaks[0][i] <= 0) {
                    minPeakPos = i;
                    break;
                }
                if (peaks[0][i] < peaks[0][minPeakPos])
                    minPeakPos = i;
            }

            if (peaks[0][minPeakPos] < result.peakHeight) {
                peaks[0][minPeakPos] = result.peakHeight;
                peaks[1][minPeakPos] = result.peakPos;
            }
        }
        ptr = LOOP_INC(ptr, ECG_ANA_BUF_LEN);
    }

    peakNum = 0;
    for (i = 0; i < PEAKNUM; i++) {
        if (peaks[0][i] > 0)
            peakNum++;
    }

    if (peakNum < 6)
        peakSelected = median(peaks[0], peakNum, (peakNum + 1) / 2);
    else
        peakSelected = median(peaks[0], peakNum, (peakNum - 1) / 2);

    secondMaxPeak = median(peaks[0], peakNum, peakNum - 1);

    if (peakSelected * 20 < secondMaxPeak) {
        peakSelected = secondMaxPeak;
    }

    for (i = 0; i < peakNum; i++) {
        if (peaks[0][i] == peakSelected)
            break;
    }

    *peakPosition = peaks[1][i];

    return peaks[0][i];
}

////////////////////////////////////////////////////////////////////////////
void QRSDetector(INT32 datanum, int *nRPos) {
    INT32 ptr;
    ptr = mod_once(ThreeSecond - 1, ECG_ANA_BUF_LEN);
    findR(*(gMWIBuf + ptr), ptr, nRPos);
}

////////////////////////////////////////////////////////////////////////////////