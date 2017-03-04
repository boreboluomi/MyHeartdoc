#ifndef  ECG_TYPES_H
#define  ECG_TYPES_H

#include  "DataType.h"
#include  "Ecg_InterFace.h"

#define  TMP_WIDTH          ECG_ANA_SAMPLE_RATE
#define  QRS_IN_MIN         40
#define  MAX_TEMPLATE       6
#define  _200MS           (ECG_ANA_SAMPLE_RATE/ 5)
#define  _1000MS          ECG_ANA_SAMPLE_RATE
#define  _4SECONDS        (_1000MS * 4+125)
//
#define  QRS_SAVED         10
#define   MAX_STATE        25
#define  MAX_QRS_NUM       40
//
#define  MAX_PEAK2END      50
#define  CHECKED_WIDTH     100

typedef enum QRSType {
    QT_DOMINANT = 0, QT_ABNORMAL, QT_UNCLASSIFIED, QT_LEARN
} QRSType;


typedef struct {
    //
    //
    UCHAR lastArrCode;
    //
    //
    UCHAR arrCode;
    CHAR Yh_arrCode;
    //
    //
    INT16 pvcPerMin;
    //
    //
    INT16 pvcNumbers[60];
    //
    //
    INT32 position;
    INT32 lastTime;
    //
    //
    INT32 isSaved;
    //
    //
    INT32 pvcOfCurrentSecond;
} ArrAnalysisInfoType;

typedef struct {
    INT32 qrsDetected;
    //
    INT32 qrsPtr;
    //
    INT32 qrsClassified;
    //
    INT32 classifiedQrsPtr;
    //
    INT32 qrsStCaled;
    //
    INT32 stCalQrsPtr;
    //
    INT32 lastRPos;   //
    INT32 lastOnset;
    INT32 aveRRi;
    INT32 rrml;
    INT32 thd;
    INT32 lastSlope;
    INT32 aveSlope;
    INT32 pageCount;
    INT32 timePast;
    INT32 initLength;
    INT32 initialized;
    INT32 counter;            //
    INT32 resetPeakDetect;
    INT32 validRRi;
    INT32 maxSlope;

} QrsGlobals;

typedef struct {
    BOOL inLearning;
    INT32 heartRate;
    INT32 delay;
    BOOL isEcgLost;

} QrsAnalysisInfoType;

typedef struct {
    INT32 type;   //
    INT32 rri;    //
} RTypePos;

typedef struct {
    INT32 normal;       //
    INT32 backed;       //
} Threshold;

typedef struct {
    INT32 onsetPos, onsetHeight;
    INT32 peakPos, peakHeight;
} PEAK;

typedef struct {
    INT32 qrsPtrs[QRS_SAVED];
    INT32 rri[QRS_SAVED];
    INT32 matched[QRS_SAVED];
    INT32 ptr;
    INT32 current;
    INT32 last;
    INT32 averageRRi;
} STATES;

typedef struct {
    INT32 onsetPos, time;
    INT32 pos, height;
    INT32 buf_pos, pages;
    INT32 searched;
} PEAKINFO;

typedef struct {
    INT32 left;   //
    INT32 right;  //
} RSquare;

typedef struct {
    INT32 peakPos;     //
    INT32 rri;         //
    INT32 onset;       //
    INT32 width;       //
    INT32 actualWidth;
    INT32 fiducial;
    INT32 coef;
    INT32 onRRi;
    INT32 morph;       //
    CHAR updated;
    QRSType type;
    CHAR matched;
    INT32 noise;
    INT32 peak2end;
    INT32 threshold;
    BOOL is_peak;
} QRSComplex;

typedef struct {
    INT32 channel;
    INT32 lead;
    INT32 mGain;
    BOOL isCalMode;

} EcgSettingType;

#endif
