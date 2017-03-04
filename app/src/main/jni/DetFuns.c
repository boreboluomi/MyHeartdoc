///#include "stdafx.h"
#include "GlobVari.h"

///////////////////////////////////////////////////////////////////////////////
void EcgVarInit(void) {
    int i;
    HeartRate = -100;
    gRGlobals.initLength = 0;
    gRGlobals.initialized = 0;
    gRGlobals.qrsPtr = 0;
    gRGlobals.resetPeakDetect = 1;
    gRGlobals.validRRi = 0;
    gRGlobals.maxSlope = 128;
    for (i = 0; i < RRINTERVAL_NUM; i++) {
        rrIntervals[i] = 0;
    }
}

/////////////////////////////////////////////////////////////////////////////
INT32 mod(INT32 i, INT32 j) {
    while (i < 0) {
        i += j;
    }

    while (i >= j) {
        i -= j;
    }

    return i;
}

///////////////////////////////////////////////////////////
INT32 mod_once(INT32 i, INT32 j) {
    if (i >= j) {
        i -= j;
    }
    else if (i < 0) {
        i += j;
    }

    return i;
}
////////////////////////////////////////////////////////////////////////////
