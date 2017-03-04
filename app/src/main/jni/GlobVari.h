#ifndef ECG_GLOBAL_H
#define ECG_GLOBAL_H

#include "EcgTypes.h"
#include "Rt_ecgfilter.h"
#include "Ecg_InterFace.h"


//
typedef struct {
    BOOL LaOff;
    BOOL RaOff;
} ecg_LeadlOffState;

extern RTFD_SETTINGS gsRtfd;
//
extern INT32 CountTime;//

extern INT16 timing;
extern INT16 TimerFlag;
extern INT16 ThreeSecond;
extern INT32 BlankLength;

extern QrsGlobals gRGlobals;
extern EcgAnaConfig gEcgAnaConfig;
extern INT32 gRDetEcgBufPtr;
//
extern INT32 *gRDetEcgBuf;

//
extern INT32 gOrigBuf[ECG_ANA_BUF_LEN];
//
extern INT32 gLpDiffBuf[ECG_ANA_BUF_LEN];
extern INT32 gDiffBuf[ECG_ANA_BUF_LEN];
extern INT32 gMWIBuf[ECG_ANA_BUF_LEN];
//
//

extern INT32 gLpDiffData[5];   //
extern INT32 gBpDiffData[5];   //

extern INT32 gSignalPeaks[8];
extern INT32 gNoisePeaks[8];
extern INT32 gRRIntervals[8];
extern INT32 gMaxSlopes[8];

extern Threshold gThreshold;

extern EcgAnaStatus gEcgAnaStatus;

extern STATES gStates;
extern PEAKINFO gPeak, gNoises[2];

extern QRSComplex gQrsComplex[MAX_QRS_NUM];//

extern INT32 MWI_THRESHOLD;
//
extern EcgSettingType gLastEcgSetting;

extern INT32 HeartRate;
extern QrsAnalysisInfoType gQrsAnaInfo;
extern ArrAnalysisInfoType gArrAnalysisInfo;

// syn[2016/3/30] 定义心率统计信息
// 心搏总数
extern INT32 RNum;
// 平均心率
extern INT32 AvgHR;
// 最快心率
extern INT32 FastestHR;
// 最慢心率
extern INT32 SlowestHR;
// 心率过快个数
extern INT32 FasterHRNum;
// 心率稍快个数
extern INT32 FastHRNum;
// 心率正常个数
extern INT32 NormalHRNum;
// 心率稍慢个数
extern INT32 SlowHRNum;
// 心率过慢个数
extern INT32 SlowerHRNum;

// syn[2016/4/9] 计时，单位秒
extern INT32 SecondCount;

// syn[2016/4/26] 实时心率，每检测一个合格的心搏，更新
extern INT32 RealTimeHR;
extern INT32 IsHRUpdate;
// syn[2016/4/27] 是否过载/导联脱落后重新检测
extern INT32 IsReDetect;

// [2016/4/27] 获取实时检测的R个数、各R波位置、心搏类型、RR间期
extern INT32 UpdateRNum;
extern INT32 gRPos[4];
extern INT32 gBeatStyle[4];
extern INT32 gRR[4];

extern BOOL CountArrest;
extern ecg_LeadlOffState ecgLeadlOffState;

//
extern INT32 rrIntervals[RRINTERVAL_NUM];

void Init_Config(void);

unsigned char CheckLeadOffState(void);

#endif