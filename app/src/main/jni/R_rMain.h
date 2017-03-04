#ifndef    ECG_ARRMAIN_H
#define    ECG_ARRMAIN_H

#include  "DataType.h"

#define    SATURATION_HI        (+65245)
#define    SATURATION_LO        (-65245)
#define    FASTCHANGE_TIME    5
#define    FASTCHANGING        5
#define ECGLOST_THRESHOLD    (250*4 + 125)//
#define    MINIMAL_CROSSPOINT  15

void Init_QrsDetection(void);

void rr_ana(int *nRPos);

INT32 IsSaturation(INT32 sample);

CHAR IsECGLost(INT32 sample);

INT32 GetHeartRate(void);

INT32 CalculateHeartRate(INT32 rrInterval);

#define _1200MS  (ECG_ANA_SAMPLE_RATE+ECG_ANA_SAMPLE_RATE/5)

BOOL HeartSound(INT32 *wave, INT32 *wpos);

#endif
