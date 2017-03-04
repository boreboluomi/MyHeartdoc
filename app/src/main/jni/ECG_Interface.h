#ifndef  KS_ECG_INTERFACE_H
#define  KS_ECG_INTERFACE_H

#include  "DataType.h"

#define  ECG_ANA_MONITOR_CHAN      8
#define  ECG_ANA_TOTAL_SECOND      4
#define  ECG_STEMP_TOTAL_SECOND    10
#define  ECG_ANA_SAMPLE_RATE       250
#define  ECG_ANA_BUF_LEN           ( ECG_ANA_SAMPLE_RATE * ECG_ANA_TOTAL_SECOND )
#define  ECG_ST_BUF_LEN           ( ECG_ANA_SAMPLE_RATE * ECG_STEMP_TOTAL_SECOND )
#define  ECG_DELAYTDISPLAY         ( ECG_ANA_SAMPLE_RATE )
#define  MIN_NOISESLOPE  64
#define     RRINTERVAL_NUM  5        // syn[2016/1/13] 计算实时心率的心搏个数12修改为5

typedef struct {
    UCHAR mLeadMode;
    //
    UCHAR mGain;
    //
    BOOL mMonitorLead;
    //
    BOOL mSglExist;
} EcgAnaChannelInfoType;

typedef struct {
    //
    UCHAR mPatientType;
    //
    //
    UCHAR mLeadSystemSet;
    //
    EcgAnaChannelInfoType mChannelInfo[ECG_ANA_MONITOR_CHAN];
    //
    UCHAR mFilterMode;
    //
    unsigned short mFs;
    unsigned short mAD_0;
    unsigned short mAD_mV;
    unsigned short mAchNum;
    unsigned short mAD_bits;
    unsigned char mLeadNum;
    unsigned char mNotchType;
    unsigned char mNotchOn;
    unsigned char mLpType;
    unsigned char mHpType;
    //
    //
    //
    //
    //
    BOOL mArrMonitor;
    //
    INT32 mArrLead;
    //
    //
    BOOL mStMonitor;
    INT32 mMonitorLead;
    INT32 mStIsoPos;
    INT32 mStSTPos;
    //
    BOOL mArrSelfLearn;
    //
    BOOL mIsCalMode;//
} EcgAnaConfig;

typedef enum {
    //
            ECG_ANA_STATUS_NORMAL,
    //
            ECG_ANA_STATUS_QRS_LEARN,
    //
            ECG_ANA_STATUS_ARR_LRN,
    //
            ECG_ANA_STATUS_NOISE,
    //
    //
            ECG_ANA_STATUS_UNDETECTABLE,
    //
            ECG_ANA_STATUS_NO_SIGNAL,
    ECG_ANA_STATUS_MAX
} EcgAnaStatusEnum;


typedef struct {
    UINT mEcgAnalysisStatus;
    BOOL mQuickQrsDet;
} EcgAnaStatus;

#define LOOP_INC(x, y) x+1>=y ? 0:x+1
#define LOOP_DEC(x, y) x-1<0 ? y-1:x-1

typedef struct {
    //
    int EcgWave;
    //
    int EcgFilt;
} YRT_Parameter;

int EcgInitConfig(void);

int EcgQrsDetAna(int *nRPos);

// syn [2016/4/23]
int EcgRTFilDeAn(YRT_Parameter *EcgYrt, int *nRPos, short *laii, short *raiii,
                 short *leadoffstate); // syn[2016/1/12] 补充函数声明

#endif