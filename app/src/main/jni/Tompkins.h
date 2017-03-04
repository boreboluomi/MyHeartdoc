#ifndef ECG_TOMPKINS_H
#define ECG_TOMPKINS_H

#include "DataType.h"
#include "GlobVari.h"

#define   HIGHPASS_DELAY    16
#define      FILTER_DELAY      24
#define   MIN_THRESHOLD     10
#define   MWIN_WIDTH        32

//
#define MIN_PEAK_WIDTH     4
#define TC                 0.1875
#define SEARCH_WIDTH       36
#define BACKED_WIDTH       25
#define EDGE_MAX_WIDTH     25
#define WIDTH_SEARCHED     6
#define EDGE_SEARCH_WIDTH  32
#define MIN_SLOPE          7

INT32 TompkinsPeakDetect(INT32 value, INT32 pos, INT32 *reset, PEAK *result);

INT32 median(INT32 list[], INT32 n, INT32 m);

void findR(INT32 data, INT32 ptr, int *nRPos);

void decision(INT32 isnoise, PEAKINFO *peak, INT32 times, INT32 usedThd, int *nRPos);

INT32 UpdateThd(Threshold *threshold);

INT32 BandPassFilter(INT32 data, INT32 *lowpassResult);

INT32 Differential(INT32 data[5], INT32 input);

INT32 MWIntegration(INT32 diffData);

INT32 Localize(INT32 pos, INT32 *exgPos, INT32 *maxSlopePos);

void LPLocalize(INT32 pos, RSquare *rSquare, INT32 *maxSlope);

INT32 UpdateMaxSlope(INT32 maxSlope);

INT32 UpdateNoiseSlope(INT32 NoiseSlope);

INT32 BPF(INT32 data, INT32 *lowpassResult);

INT32 MWI(INT32 diffData);

INT32 Qrs_Width(INT32 left, INT32 right);

// [2016/4/9] 排除干扰误检
int ConfirmRPos(int arynData[ECG_ANA_SAMPLE_RATE * 4], int nRPos);

#endif