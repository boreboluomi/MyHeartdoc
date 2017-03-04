#ifndef ECG_DETECTOR_H
#define ECG_DETECTOR_H

#include "DataType.h"
#include "GlobVari.h"

#define   INITLEN     3
#define      PEAKNUM      8

void InitDetect(void);

void QRSDetector(INT32, int *nRPos);

void ECGProcess(INT32);

void InitThreshold(Threshold *threshold);

INT32 MakeThd(INT32, INT32 length, INT32 *);

#endif

