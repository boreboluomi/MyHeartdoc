#include "Ecg_Interface.h"
#include "ProcessShowWave.h"

// 宏定义
#define SEG_LEN                30        // 只记录30s的R位置和心搏类型，避免占内存过大
#define MAX_R_NUM            120        // 30s最大心搏个数
#define HRAVG_LEN            10        // 计算10s平均心率，用于统计最快最慢异常等心率。

#define    MV_GAIN                3466    // [2016/4/9] 1mv对应增益值

#define OVERLOAD_AMP        34660        // [2016/4/20] 过载信号幅度阈值：10mv * 3466

#define AF_RR_MAX_NUM    50        // [2015/12/2] 房颤判断RR间期最大个数

#define TIME_PULSE        375        // 漏搏时间阈值：1.5s
#define TIME_STOP        3        // [2016/4/9] 停搏时间阈值：3s

// [2015/11/17] 心搏类型宏定义
#define BS_N            0        // 窦性
#define BS_A            1        // 房早
#define BS_V            2        // 室早
#define BS_X            3        // 干扰

#define RHYTHM_SORT        15        // 节律诊断种类
// [2015/11/25] 节律编号定义
#define SIN_TACHY        1        // 窦性心动过速
#define SIN_BRADY        2        // 窦性心动过缓
#define SINGLE_VEN        3        // 单发室早
#define PAIR_VEN        4        // 成对室早
#define VEN_TACHY        5        // 室速
#define VEB_BI            6        // 室早二联律
#define    VEB_TRI            7        // 室早三联律
#define SINGLE_ATR        8        // 单发房早
#define    PAIR_ATR        9        // 成对房早
#define    ATR_TACHY        10        // 房速
#define APB_BI            11        // 房早二联律
#define APB_TRI            12        // 房早三联律
#define PAUSE            13        // 漏搏
#define AFRIL            14        // [2015/12/2] 房颤
#define STOP            15        // [2016/4/9] 停搏


// [2016/3/10] 算法封装函数，包括波形预处理、心搏检测、节律诊断
int AnalysisECGWave(int arynData[DATALEN_ORG], int bLeadOff, float aryfDataShow[DATALEN_SHOW],
                    int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT], int *bOverLoad);

// [2016/3/29] 心电节律分析
int AnalysisWave(int arynData[ECG_ANA_SAMPLE_RATE * 2], int arynUpDataRPos[4], int nUpDataRNum,
                 int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]);

// [2016/4/6] 信号过载判断
int JudgeOverLoad(int arynDataOrg[DATALEN_ORG]);

// [2016/3/12] 根据当前波形确定R顶点位置
int AdjustRPos(int arynData[ECG_ANA_SAMPLE_RATE * 2], int nRPosTemp, int nSegCount, int *nRPos);

// [2016/4/23] 计算实时心率
void CalcRealTimeHR(int arynRPos[MAX_R_NUM], int nRNum);

// [2016/4/17] 计算QRS最大幅度差
int CalQRSDifAmp(int arynData[ECG_ANA_SAMPLE_RATE * 2], int nRPos, int nSegCount, int *nRAmp,
                 int *nQRSDifAmp);

// [2016/4/17] 统计平均、最快、最慢、异常等心率
void UpdataHRStatis(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int arynRAmp[MAX_R_NUM],
                    int arynQRSDifAmp[MAX_R_NUM], int nUpDataRNum, int nSegCount, int nRNum);

// [2016/3/30] 每30s更新心搏记录，包括R位置、心搏类型等
void UpdataBeatRecord(int nSegCount, int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM],
                      int arynRAmp[MAX_R_NUM], int arynQRSDifAmp[MAX_R_NUM], int *nRCount);

// [2015/11/16] 节律判断
void JudgeRhythm(int arynData[ECG_ANA_SAMPLE_RATE * 2], int arynRPos[MAX_R_NUM], int nRIndex,
                 int nSegCount,
                 int arynBeatStyle[MAX_R_NUM], int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT],
                 int bStopStart);

// [2015/11/17] 心搏类型判断
void JudgeBeatStyle(int arynData[ECG_ANA_SAMPLE_RATE * 2], int arynRPos[MAX_R_NUM], int nRIndex,
                    int nSegCount, int arynBeatStyle[MAX_R_NUM]);

// [2015/11/17] 计算基准间期
void CalcBaseRR(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int nRIndex, int *nBaseRR);

// [2015/11/17] 计算形态参数
void CalcVShape(int arynData[ECG_ANA_SAMPLE_RATE * 2], int nRPos, int nSegCount, double *dbVShape);

// [2016/3/30] 判断房性早搏相关
void JudgeAPBPremature(int arynBeatStyle[MAX_R_NUM], int arynRPos[MAX_R_NUM], int nRIndex,
                       int bAfStart,
                       int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]);

// [2016/3/30] 判断室性早搏相关
void JudgeVEBPremature(int arynBeatStyle[MAX_R_NUM], int arynRPos[MAX_R_NUM], int nRIndex,
                       int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]);

// [2016/3/30] 判断窦速/窦缓
void JudgeSinTachyBrady(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int nRIndex,
                        int bAfStart,
                        int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]);

// [2016/3/30] 判断房颤
void JudgeAfrillation(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int nRIndex,
                      int *bAfStart,
                      int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]);

// [2015/12/2] 判断本段是否满足房颤条件
void JudgeAf(int arynRR[AF_RR_MAX_NUM], int nRRCount, int *nMark);

// [2015/12/3] 判断本段是否满足相邻心率不齐
void JudgeNeighborRR(int arynRR[AF_RR_MAX_NUM], int nRRCount, int *bNeighborRRMark);

// [2015/12/3] 判断本段是否满足心率种类多
void JudgeRRSort(int arynRR[AF_RR_MAX_NUM], int nRRCount, int nRRAvg, int *bRRSort);

// [2016/3/31] 记录结束后，获取总心搏个数、平均心率、最快心率、最慢心率、正常异常心率比例
void Ecg_GetStatisitic(int *nRNum, int *nHRAvg, int *nHRMax, int *nHRMin, int arynHRPercent[5]);

// [2016/4/26] 获取实时心率
int Ecg_GetRealHeartRate();

// [2016/4/27] 获取实时检测的R个数、各R波位置、心搏类型、RR间期
void Ecg_GetRealBeatVal(int *nUpdateRNum, int arynRPos[4], int arynBeatStyle[4], int arynRR[4]);