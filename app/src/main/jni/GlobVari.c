#include "GlobVari.h"

RTFD_SETTINGS gsRtfd;

INT32 CountTime;

INT16 timing = 0;
INT16 TimerFlag = 0;
INT16 ThreeSecond = 0;
INT32 BlankLength = 0;

INT32 gOrigBuf[ECG_ANA_BUF_LEN];
INT32 gLpDiffBuf[ECG_ANA_BUF_LEN];
INT32 gDiffBuf[ECG_ANA_BUF_LEN];
INT32 gMWIBuf[ECG_ANA_BUF_LEN];

QrsGlobals gRGlobals;
EcgAnaConfig gEcgAnaConfig;
INT32 gRDetEcgBufPtr;
INT32 *gRDetEcgBuf;
INT32 gLpDiffData[5];
INT32 gBpDiffData[5];

INT32 gSignalPeaks[8];
INT32 gNoisePeaks[8];
INT32 gRRIntervals[8];
INT32 gMaxSlopes[8];
Threshold gThreshold;
EcgAnaStatus gEcgAnaStatus;
STATES gStates;
PEAKINFO gPeak, gNoises[2];
QRSComplex gQrsComplex[MAX_QRS_NUM];
INT32 MWI_THRESHOLD = 600;
EcgSettingType gLastEcgSetting;

INT32 HeartRate;
QrsAnalysisInfoType gQrsAnaInfo;
ArrAnalysisInfoType gArrAnalysisInfo;

// syn[2016/3/30] 定义心率统计信息
// 心搏总数
INT32 RNum = 0;
// 平均心率
INT32 AvgHR = 0;
// 最快心率
INT32 FastestHR = 0;
// 最慢心率
INT32 SlowestHR = 1000;
// 心率过快个数
INT32 FasterHRNum = 0;
// 心率稍快个数
INT32 FastHRNum = 0;
// 心率正常个数
INT32 NormalHRNum = 0;
// 心率稍慢个数
INT32 SlowHRNum = 0;
// 心率过慢个数
INT32 SlowerHRNum = 0;

// syn[2016/4/9] 计时，单位秒
INT32 SecondCount = 0;

// syn[2016/4/26] 实时心率，每检测一个合格的心搏，更新
INT32 RealTimeHR = -100;
INT32 IsHRUpdate = 0;
// syn[2016/4/27] 是否过载/导联脱落后重新检测
INT32 IsReDetect = 0;

// [2016/4/27] 获取实时检测的R个数、各R波位置、心搏类型、RR间期
INT32 UpdateRNum = 0;
INT32 gRPos[4] = {0};
INT32 gBeatStyle[4] = {0};
INT32 gRR[4] = {0};

BOOL CountArrest;

ecg_LeadlOffState ecgLeadlOffState;

// syn[2016/1/13] 计算实时心率的心搏个数12修改为5
//INT32 rrIntervals[RRINTERVAL_NUM] ={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
INT32 rrIntervals[RRINTERVAL_NUM] = {0, 0, 0, 0, 0};

void Init_Config(void) {
    gEcgAnaConfig.mPatientType = 0;
    gEcgAnaConfig.mLeadSystemSet = 0;
    gEcgAnaConfig.mFilterMode = 0;
    gEcgAnaConfig.mFs = 250;
    gEcgAnaConfig.mAD_0 = 0;
    gEcgAnaConfig.mAD_mV = 0;
    gEcgAnaConfig.mAchNum = 1;
    gEcgAnaConfig.mAD_bits = 12;
    gEcgAnaConfig.mLeadNum = 1;
    gEcgAnaConfig.mNotchType = NOTCH_TYPE_50;
    gEcgAnaConfig.mNotchOn = NOTCH_TYPE_50;
    gEcgAnaConfig.mLpType = LPF_TYPE_100;
    gEcgAnaConfig.mHpType = HPF_TYPE_005;
    gEcgAnaConfig.mArrMonitor = TRUE;
    gEcgAnaConfig.mArrLead = 0;
    gEcgAnaConfig.mStMonitor = TRUE;
    gEcgAnaConfig.mMonitorLead = 0;
    gEcgAnaConfig.mStIsoPos = -80;
    gEcgAnaConfig.mStSTPos = 108;
    gEcgAnaConfig.mArrSelfLearn = FALSE;
    gEcgAnaConfig.mIsCalMode = FALSE;
    ecgLeadlOffState.LaOff = FALSE;
    ecgLeadlOffState.RaOff = FALSE;
}