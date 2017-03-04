#include <stdio.h>
#include "AnalysisWave.h"
#include "ProcessShowWave.h"
#include "Ecg_Interface.h"
#include "GlobVari.h"
#include <math.h>
#include <stdlib.h>

// [2016/4/23] 原数据段索引号
int INDEX_ORG2[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
// [2016/4/23] 插值后数据段索引号
double INDEX_SHOW2[] = {0, 0.75, 1.5, 2.25, 3, 3.75, 4.5, 5.25, 6, 6.75, 7.5, 8.25, 9};

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：算法封装函数，包括波形预处理、心搏检测、节律诊断
% 输入参数：
% 输出参数：
% 编辑日期：2016/3/29
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int AnalysisECGWave(int arynDataOrg[DATALEN_ORG], int bLeadOff, float aryfDataShow[DATALEN_SHOW],
                    int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT], int *bOverLoad) {
    int i;

    // 每10个点返回的基础滤波后波形
    float arydbDataFil[DATALEN_ORG];
    // 每10个点返回的R波位置，没有检测到R则为零
    int nRPos = -1;
    // 2秒基础滤波后波形，作为节律分析的输入
    static int arynData[ECG_ANA_SAMPLE_RATE * 2] = {0};
    // 每秒检测到的R波位置，作为节律分析的输入
    static int arynUpDataRPos[4];
    // 每秒检测到的R波个数
    static int nUpDataRNum = 0;
    // 10个采样点为一段，计数
    static int nCountMult = 0;
    // [2016/3/12] 是否第一个1s
    static int bFirstSecond = 1;

    // 输出初始化
    for (i = 0; i < DATALEN_SHOW; i++) {
        aryfDataShow[i] = 0;
    }
    *nRhythmNum = 0;
    for (i = 0; i < RHYTHM_SORT; i++) {
        arynRhythmMark[i] = 0;
    }
    *bOverLoad = 0;

    // [2016/4/23] 信号过载判断
    *bOverLoad = JudgeOverLoad(arynDataOrg);

    // syn[2016/4/16] 按赵哥添加导联脱落，用于滤波复位
    // 向程序内部传导导联脱落状态
    // 内部左导联代表当前导联状态
    // 实时对导联状态传入以便监测
    ecgLeadlOffState.LaOff = bLeadOff;
    // [2016/4/23] 添加过载标记，用于滤波复位
    ecgLeadlOffState.LaOff = *bOverLoad;

    // 波形处理（滤波+插值）、心搏检测
    ProcessWave(arynDataOrg, arydbDataFil, aryfDataShow, &nRPos);

    // [2016/4/23] 导联脱落则插值后波形返回0，且不做诊断
    if (bLeadOff == 1) {
        // 显示波形返回零
        for (i = 0; i < DATALEN_SHOW; i++) {
            aryfDataShow[i] = 0;
        }
        // 诊断相关参数复位
        for (i = 0; i < ECG_ANA_SAMPLE_RATE * 2; i++) {
            arynData[i] = 0;
        }
        for (i = 0; i < nUpDataRNum; i++) {
            arynUpDataRPos[i] = 0;
        }
        nUpDataRNum = 0;
        nCountMult = 0;
        bFirstSecond = 1;
        IsReDetect = 1;
        return 0;
    }

    // [2016/4/23] 信号过载则返回未滤波只插值波形，且不做诊断
    if (*bOverLoad == 1) {
        // 显示波形返回未滤波的插值数据
        float aryfDataForInterp[DATALEN_ORG] = {0};
        for (i = 0; i < DATALEN_ORG; i++) {
            aryfDataForInterp[i] = (float) arynDataOrg[i];
        }
        LinearInterp(INDEX_ORG2, aryfDataForInterp, INDEX_SHOW2, aryfDataShow);
        // 诊断相关参数复位
        for (i = 0; i < ECG_ANA_SAMPLE_RATE * 2; i++) {
            arynData[i] = 0;
        }
        for (i = 0; i < nUpDataRNum; i++) {
            arynUpDataRPos[i] = 0;
        }
        nUpDataRNum = 0;
        nCountMult = 0;
        bFirstSecond = 1;
        IsReDetect = 1;
        return 0;
    }

    // 记录基础滤波后波形，用于分析诊断
    for (i = 0; i < DATALEN_ORG; i++) {
        if (bFirstSecond == 1) {
            arynData[ECG_ANA_SAMPLE_RATE + nCountMult * DATALEN_ORG + i] = arydbDataFil[i];
        }
        else {
            arynData[nCountMult * DATALEN_ORG + i] = arynData[ECG_ANA_SAMPLE_RATE +
                                                              nCountMult * DATALEN_ORG + i];
            arynData[ECG_ANA_SAMPLE_RATE + nCountMult * DATALEN_ORG + i] = arydbDataFil[i];
        }
    }
    // 记录检测到的R波位置，用于分析诊断
    if (nRPos >= 0) {
        arynUpDataRPos[nUpDataRNum] = nRPos;
        nUpDataRNum++;
    }

    nCountMult++;
    // [2016/1/13] 1s即250点做一次分析诊断
    if (nCountMult == ECG_ANA_SAMPLE_RATE / DATALEN_ORG) {
        AnalysisWave(arynData, arynUpDataRPos, nUpDataRNum,
                     nRhythmNum, arynRhythmMark);
        // 计数归零
        nCountMult = 0;

        // 本秒R波清空
        for (i = 0; i < nUpDataRNum; i++) {
            arynUpDataRPos[i] = 0;
        }
        nUpDataRNum = 0;

        // 不再是第一个1s
        bFirstSecond = 0;
    }

    return 1;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：心电节律分析
% 输入参数：arynDataO    		最新2s心电波形数据段
%           arynUpDataRPos		最新1s检测的R波位置
%           nUpDataRNum			最新1s检测的R波个数
% 输出参数：nRhythmNum			实时每s节律事件个数
%           arynRhythmMark		实时每s节律事件编号
% 编辑日期：2016/3/29
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int AnalysisWave(int arynData[ECG_ANA_SAMPLE_RATE * 2], int arynUpDataRPos[4],
                 int nUpDataRNum, int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]) {
    int i, j;
    __int64_t nSum, nBaseLine;
    int nRIndex;
    int nRPos = 0;
    int nRAmp = 0;
    int nQRSDifAmp = 0;
    FILE *fp6;

    // 分段计数，静态变量
    static int s_nSegCount = 0;
    // 存放每30s的R波位置
    static int arynRPos[MAX_R_NUM];
    // 存放每30s的心搏类型
    static int arynBeatStyle[MAX_R_NUM];
    // 存放每30s的R波幅度
    static int arynRAmp[MAX_R_NUM];
    // 存放每30s的QRS最大幅度差
    static int arynQRSDifAmp[MAX_R_NUM];
    // 每30s的R波个数
    static int s_nRCount = 0;
    // [2016/4/9] 停搏开始标记
    static int s_bStopStart = 0;
    int nDis = 0;

    // 输出初始化
    *nRhythmNum = 0;
    for (i = 0; i < RHYTHM_SORT; i++) {
        arynRhythmMark[i] = 0;
    }

    // 段数计数
    s_nSegCount = s_nSegCount + 1;

    if (1 == s_nSegCount) {
        // 第一段不处理,不返回结果
        return 1;
    }

    // 本秒新检测的R波位置精确定位、记录
    for (i = 0; i < nUpDataRNum; i++) {
        // 根据当前波形矫正RPos
        AdjustRPos(arynData, arynUpDataRPos[i], s_nSegCount, &nRPos);
        // 统计R波位置`
        arynRPos[s_nRCount + i] = nRPos;

        // [2016/4/23] 计算实时心率
        CalcRealTimeHR(arynRPos, s_nRCount + i);

        // [2016/4/17] 计算R幅度和QRS最大幅度差
        CalQRSDifAmp(arynData, nRPos, s_nSegCount, &nRAmp, &nQRSDifAmp);
        // 统计R幅度，与arynRPos对齐
        arynRAmp[s_nRCount + i] = nRAmp;
        // 统计QRS最大幅度差，与arynRPos对齐
        arynQRSDifAmp[s_nRCount + i] = nQRSDifAmp;

        // [2016/4/27] 统计实时检测的各R波位置、RR间期，返回给APP
        gRPos[i] = nRPos;
        if (s_nRCount + i > 0) {
            gRR[i] = nRPos - arynRPos[s_nRCount + i - 1];
        }
    }
    s_nRCount += nUpDataRNum;

    // [2016/4/27] 统计实时检测的R个数，返回给APP
    UpdateRNum = nUpDataRNum;

    // [2016/4/9] 判断停搏
    if (s_nRCount > 0) {
        nDis = s_nSegCount * ECG_ANA_SAMPLE_RATE - arynRPos[s_nRCount - 1];
    }
    if (s_bStopStart == 0
        && (0 == s_nRCount && s_nSegCount > TIME_STOP * 2
            || nDis > TIME_STOP * ECG_ANA_SAMPLE_RATE)) {
        // 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = STOP;
        *nRhythmNum = *nRhythmNum + 1;
        s_bStopStart = 1;
    }
    // 节律判断
    for (i = 0; i < nUpDataRNum; i++) {
        nRIndex = s_nRCount - nUpDataRNum + i;
        if (nRIndex >= 2) {
            // 节律判断
            JudgeRhythm(arynData, arynRPos, nRIndex, s_nSegCount, arynBeatStyle,
                        nRhythmNum, arynRhythmMark, s_bStopStart);

            // [2016/4/9] 已检测到R，停搏开始标志置零。
            s_bStopStart = 0;
        }

        // [2016/4/27] 统计实时检测的各心搏类型
        gBeatStyle[i] = arynBeatStyle[nRIndex];
    }

    // syn[2016/4/17] 每10s计算平均心率，统计最快最慢异常等心率
    UpdataHRStatis(arynRPos, arynBeatStyle, arynRAmp, arynQRSDifAmp, nUpDataRNum, s_nSegCount,
                   s_nRCount);

    // [2015/11/16] 封装函数：每30s更新心搏记录，包括R位置、心搏类型等；记录总R波个数
    UpdataBeatRecord(s_nSegCount, arynRPos, arynBeatStyle, arynRAmp, arynQRSDifAmp, &s_nRCount);

    return 1;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断信号是否过载
% 输入参数：arynDataOrg		当前心电波形，10个采样点
% 输出参数：bOverLoad		信号是否过载
% 编辑日期：2016/4/6
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int JudgeOverLoad(int arynDataOrg[DATALEN_ORG]) {
    int bOverLoad = 0;
    int nMax, nMin, i;

    // 找最大最小值
    nMax = arynDataOrg[0];
    nMin = arynDataOrg[0];
    for (i = 0; i < DATALEN_ORG; i++) {
        if (arynDataOrg[i] > nMax) {
            nMax = arynDataOrg[i];
        }
        if (arynDataOrg[i] < nMin) {
            nMin = arynDataOrg[i];
        }
    }

    // 最大最小幅度差超过10mv，则过载
    if (abs(nMax - nMin) > OVERLOAD_AMP) {
        bOverLoad = 1;
    }

    return bOverLoad;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：调整R波位置
% 输入参数：arynData		当前1s心电波形
%           nRPosTemp		检测到的R波位置
%           nSegCount		1s分段计数
% 输出参数：nRPos			确定后R波位置
% 编辑日期：2016/3/12
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int AdjustRPos(int arynData[ECG_ANA_SAMPLE_RATE * 2], int nRPosTemp, int nSegCount, int *nRPos) {
    // R向后搜索范围，暂定6个采样点
    int c_nFindThr = 0.024 * ECG_ANA_SAMPLE_RATE;
    int nStart, nEnd;
    int i, nMax;

    // 输出初始化
    *nRPos = nRPosTemp;

    // R位置换算与arynData一致
    nRPosTemp = nRPosTemp - (nSegCount - 2) * ECG_ANA_SAMPLE_RATE;

    // [2016/4/13] 异常处理
    if (nRPosTemp < 0 || nRPosTemp >= ECG_ANA_SAMPLE_RATE * 2) {
        return 1;
    }
    // 搜索起点、终点
    nStart = nRPosTemp - c_nFindThr;
    if (nStart < 0) {
        nStart = 0;
    }
    nEnd = nRPosTemp + c_nFindThr;
    if (nEnd >= ECG_ANA_SAMPLE_RATE * 2) {
        nEnd = ECG_ANA_SAMPLE_RATE * 2;
    }
    // 搜索绝对幅度最大值为RPos
    nMax = abs(arynData[nStart]);
    for (i = nStart; i <= nEnd; i++) {
        if (abs(arynData[i]) > nMax) {
            nMax = abs(arynData[i]);
            *nRPos = i;
        }
    }
    // R位置换算与实际一致
    *nRPos = *nRPos + (nSegCount - 2) * ECG_ANA_SAMPLE_RATE;

    return 1;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：计算实时心率
% 输入参数：arynRPos		最新每30s的R波位置
% 输出参数：无
% 编辑日期：2016/4/26
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void CalcRealTimeHR(int arynRPos[MAX_R_NUM], int nRNum) {
    int nRR, nHR;

    if (nRNum <= 0) {
        return;
    }
    // [2016/4/27] 如果过载/导联脱落后重新检测，第一个心搏不更新心率
    if (IsReDetect == 1) {
        IsReDetect = 0;
        return;
    }
    // 计算当前心率
    nRR = arynRPos[nRNum] - arynRPos[nRNum - 1];
    if (nRR <= 0) {
        return;
    }
    nHR = ECG_ANA_SAMPLE_RATE * 60 / nRR;

    // 当前心率超过实时心率的2.5倍（且原实时心率不是特别低）则认为干扰导致，不更新
    if (RealTimeHR > 55
        && nHR > RealTimeHR * 2.5) {
        return;
    }
        // 否则，只要满足30~240范围就更新
    else if (nHR > 30
             && nHR < 240) {
        RealTimeHR = nHR;
        IsHRUpdate = 1;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：计算QRS最大幅度差
% 输入参数：arynData		每段心电波形数据
%           nRPos			当前R波位置
%           nSegCount		当前1s分段的序号
%           nRAmp			R波幅度
%           nQRSDifAmp		QRS最大幅度差
% 输出参数：无
% 编辑日期：2016/4/17
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int CalQRSDifAmp(int arynData[ECG_ANA_SAMPLE_RATE * 2], int nRPos, int nSegCount, int *nRAmp,
                 int *nQRSDifAmp) {
    // R向后搜索范围，暂定100ms，25个采样点
    int c_nFindThr = 0.1 * ECG_ANA_SAMPLE_RATE;
    int nRPosTemp, nStart, nEnd;
    int i, nMax, nMin;

    // 输出初始化
    *nRAmp = 0;
    *nQRSDifAmp = 0;

    // R波位置换算到与arynData一致
    nRPosTemp = nRPos - (nSegCount - 2) * ECG_ANA_SAMPLE_RATE;

    // 搜索起点、终点
    nStart = nRPosTemp - c_nFindThr;
    if (nStart < 0) {
        nStart = 0;
    }
    nEnd = nRPosTemp + c_nFindThr;
    if (nEnd >= ECG_ANA_SAMPLE_RATE * 4 - 1) {
        nEnd = ECG_ANA_SAMPLE_RATE * 4 - 1;
    }

    // 搜索幅度最大值、最小值
    nMax = arynData[nStart];
    nMin = arynData[nStart];
    for (i = nStart; i <= nEnd; i++) {
        if (arynData[i] > nMax) {
            nMax = arynData[i];
        }
        if (arynData[i] < nMin) {
            nMin = arynData[i];
        }
    }

    // R幅度
    *nRAmp = arynData[nRPosTemp];
    // QRS最大幅度差
    *nQRSDifAmp = nMax - nMin;

    return 1;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：统计平均、最快、最慢、异常等心率
% 输入参数：arynRPos		R波位置,30s一更新
%           nUpDataRNum		当前1s分段检测到的R波个数
%           nSegCount		当前1s分段的序号
%           nRNum			每30s的R波个数
% 输出参数：无
% 编辑日期：2016/4/17
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void UpdataHRStatis(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int arynRAmp[MAX_R_NUM],
                    int arynQRSDifAmp[MAX_R_NUM], int nUpDataRNum, int nSegCount, int nRNum) {
    int nHRAvg;
    int nRIndex, nPreRPos, nRPos, nRAmp, nRR;
    int i, nRRCount, nHR;
    double dbRRAvg;
    static int nCalHRNum = 0;
    int nQRSDifAmp = 0;
    // 最小RR间期
    const int c_nRRMinThr = 0.25 * ECG_ANA_SAMPLE_RATE;
    // [2016/4/27] 修改心率统计方式
    int nRStart, nREnd, nSum, nAvg, nRRCount2;
    int arynConfRR[10] = {0};

    // 统计平均、最快、最慢、异常等心率
    if (0 != nSegCount % HRAVG_LEN) {
        return;
    }
    // [2016/4/26] 计算最新10个心搏平均心率
    nRRCount = 0;
    nRStart = nRNum - 1;
    nREnd = nRNum - 10;
    if (nREnd < 1) {
        nREnd = 1;
    }
    nSum = 0;
    nRIndex = nRStart;
    while (nRIndex >= nREnd) {
        // 本R位置
        nRPos = arynRPos[nRIndex];
        // 本R与前R间距
        nRR = nRPos - arynRPos[nRIndex - 1];
        // 本R幅度
        nRAmp = arynRAmp[nRIndex];
        // 本QRS最大幅度差
        nQRSDifAmp = arynQRSDifAmp[nRIndex];
        // [2016/4/17] 满足以下任一条件，不计入心率统计和R总数统计
        if (nRR <= c_nRRMinThr
            || abs(nRAmp) >= 6 * MV_GAIN
            || abs(nQRSDifAmp) >= 4 * MV_GAIN) {
            RNum--;
            // [2016/4/19] 修改bug：防止进入死循环
            nRIndex--;
            continue;
        }
            // X不计入心率统计
        else if (BS_X == arynBeatStyle[nRIndex]
                 || BS_X == arynBeatStyle[nRIndex - 1]) {
            // [2016/4/19] 修改bug：防止进入死循环
            nRIndex--;
            continue;
        }
        else {
            // 统计合格的RR间期和
            arynConfRR[nRRCount] = nRR;
            nSum += nRR;
            nRRCount++;
        }
        nRIndex--;
    }
    if (0 == nRRCount) {
        return;
    }
    // 初步计算平均值
    nAvg = nSum / nRRCount;
    // 再次筛选，求平均
    nSum = 0;
    nRRCount2 = 0;
    for (i = 0; i < nRRCount; i++) {
        if (abs(arynConfRR[i] - nAvg) / nAvg < 0.4) {
            nSum += arynConfRR[i];
            nRRCount2++;
        }
    }
    // 最新10个心搏的平均心率
    if (nRRCount2 == 0) {
        dbRRAvg = nAvg;
    }
    else {
        dbRRAvg = nSum * 1.0 / nRRCount2;
    }

    // syn[2016/4/16] 添加异常处理
    if (0 != dbRRAvg) {
        nHRAvg = 60 * ECG_ANA_SAMPLE_RATE / dbRRAvg;
    }
    else {
        nHRAvg = 0;
    }

    // [2016/4/27] 平均心率
    if (AvgHR == 0) {
        AvgHR = nHRAvg;
    }
    else {
        AvgHR = (AvgHR + nHRAvg) / 2;
    }
    // 最快心率
    if (nHRAvg > FastestHR) {
        FastestHR = nHRAvg;
    }
    // 最慢心率
    if (nHRAvg < SlowestHR) {
        SlowestHR = nHRAvg;
    }
    // 心率过快个数
    if (nHRAvg > 120) {
        FasterHRNum++;
    }
        // 心率稍快个数
    else if (nHRAvg > 100) {
        FastHRNum++;
    }
        // 心率正常个数
    else if (nHRAvg > 60) {
        NormalHRNum++;
    }
        // 心率稍慢个数
    else if (nHRAvg > 50) {
        SlowHRNum++;
    }
        // 心率过慢个数
    else {
        SlowerHRNum++;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：每30s更新心搏记录，包括R位置、心搏类型等
% 输入参数：nSegCount       1s分段计数
%           arynRPos		本30s的R波位置
%           arynBeatStyle	本30s的心搏位置
%           nRCount         本30s的R波个数
%           nRNum           更新前总R波个数
% 输出参数：arynRPos		本30s最后20个R波位置
%           arynBeatStyle	本30s最后20个心搏类型
%           nRCount         20
%           nRNum           更新后总R波个数
% 编辑日期：2016/3/30
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void UpdataBeatRecord(int nSegCount, int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM],
                      int arynRAmp[MAX_R_NUM], int arynQRSDifAmp[MAX_R_NUM], int *nRCount) {
    int i;
    // 每30s段记录上段最后的R个数
    const int c_nRecordRNum = 20;

    // 不足30s返回
    if (0 != nSegCount % SEG_LEN) {
        return;
    }

    // R波总数
    if (SEG_LEN == nSegCount) {
        RNum += *nRCount;
    }
    else {
        RNum += *nRCount - c_nRecordRNum;
    }

    // 记录本30s最后c_nRecordRNum个R，其余清空
    for (i = 0; i < c_nRecordRNum; i++) {
        arynRPos[i] = arynRPos[*nRCount - c_nRecordRNum + i];
        arynBeatStyle[i] = arynBeatStyle[*nRCount - c_nRecordRNum + i];
        arynRAmp[i] = arynRAmp[*nRCount - c_nRecordRNum + i];
        arynQRSDifAmp[i] = arynQRSDifAmp[*nRCount - c_nRecordRNum + i];
    }
    for (i = c_nRecordRNum; i < *nRCount; i++) {
        arynRPos[i] = 0;
        arynBeatStyle[i] = 0;
        arynRAmp[i] = 0;
        arynQRSDifAmp[i] = 0;
    }
    *nRCount = c_nRecordRNum;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：节律判断
% 输入参数：arynData         每段心电波形数据
%           arynRPos     	已检测到的R波位置
%           arynBeatStyle  	每个心搏类型，与arynRPos对应
%           nRIndex  		本次判断的R波序号，与arynRPos对应
%           nSegCount       本分段序号
% 输出参数：arynBeatStyle  	每个心搏类型，与arynRPos对应
%           nRhythmNum		实时每s节律事件个数
%           arynRhythmMark	实时每s节律事件编号
% 编辑日期：2015/11/16
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeRhythm(int arynData[ECG_ANA_SAMPLE_RATE * 2], int arynRPos[MAX_R_NUM], int nRIndex,
                 int nSegCount,
                 int arynBeatStyle[MAX_R_NUM], int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT],
                 int bStopStart) {
    int nRR;
    // [2015/12/2] 房颤开始标记
    static int s_bAfStart = 0;
    int nStart, nEnd;

    // 心搏类型判断
    JudgeBeatStyle(arynData, arynRPos, nRIndex, nSegCount, arynBeatStyle);
    // 判断房性早搏相关
    JudgeAPBPremature(arynBeatStyle, arynRPos, nRIndex, s_bAfStart, nRhythmNum, arynRhythmMark);
    // 判断室性早搏相关
    JudgeVEBPremature(arynBeatStyle, arynRPos, nRIndex, nRhythmNum, arynRhythmMark);

    // [2016/4/9] 判断漏搏
    nRR = arynRPos[nRIndex] - arynRPos[nRIndex - 1];
    if (nRR > TIME_PULSE
        && bStopStart == 0) {
        // [2015/11/23] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = PAUSE;
        *nRhythmNum = *nRhythmNum + 1;
    }

    // 判断窦速/窦缓
    JudgeSinTachyBrady(arynRPos, arynBeatStyle, nRIndex, s_bAfStart, nRhythmNum, arynRhythmMark);

    // [2015/12/2] 判断房颤
    JudgeAfrillation(arynRPos, arynBeatStyle, nRIndex, &s_bAfStart, nRhythmNum, arynRhythmMark);
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：心搏类型判断
% 输入参数：arynData         每段心电波形数据
%           arynRPos     	已检测到的R波位置
%           nRIndex  		本次判断的R波序号，与arynRPos对应
%           nSegCount       本分段序号
%           arynBeatStyle  	每个心搏类型，与arynRPos对应
% 输出参数：arynBeatStyle  	每个心搏类型，与arynRPos对应
% 编辑日期：2015/11/17
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeBeatStyle(int arynData[ECG_ANA_SAMPLE_RATE * 2], int arynRPos[MAX_R_NUM], int nRIndex,
                    int nSegCount, int arynBeatStyle[MAX_R_NUM]) {
    int nRPos, nRR, nRAmp;
    double dbAdvance, dbVShape;
    // 基准间期，静态变量
    static int s_nBaseRR = 0;

    // 当前R波位置
    nRPos = arynRPos[nRIndex];
    // 当前RR间期
    nRR = arynRPos[nRIndex] - arynRPos[nRIndex - 1];
    // 计算基准间期
    CalcBaseRR(arynRPos, arynBeatStyle, nRIndex, &s_nBaseRR);

    // 判断心搏类型
    if (0 == s_nBaseRR) {
        // 窦性N
        arynBeatStyle[nRIndex] = BS_N;
    }
    else {
        // 间期提前率
        dbAdvance = (s_nBaseRR - nRR) * 1.0 / s_nBaseRR;
        // 计算形态参数
        CalcVShape(arynData, nRPos, nSegCount, &dbVShape);
        // R波幅度
        nRAmp = arynData[nRPos - (nSegCount - 2) * ECG_ANA_SAMPLE_RATE];
        // [2016/4/6] 判断室早/房早
        if (dbAdvance > 0.1 && dbVShape > 8  // 【待调】
            || dbVShape > 11 && nRAmp < 0) {
            // 室早V
            arynBeatStyle[nRIndex] = BS_V;
        }
        else if (dbAdvance > 0.15) {
            // 房早A
            arynBeatStyle[nRIndex] = BS_A;
        }
        else {
            // 窦性N
            arynBeatStyle[nRIndex] = BS_N;
        }
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：计算基准间期
% 输入参数：arynRPos     	已检测到的R波位置
%           nRIndex  		本次判断的R波序号，与arynRPos对应
%           nBaseRR          基准RR间期
% 输出参数：nBaseRR          基准RR间期
% 编辑日期：2015/11/17
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void CalcBaseRR(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int nRIndex, int *nBaseRR) {
    int nRR1, nRR2;
    int nRR, nRRSum, nTime, nRTemp;
    int nBaseRRTemp;

    // 计算基准间期
    if (0 == *nBaseRR
        && 2 == nRIndex) {
        nRR1 = arynRPos[nRIndex] - arynRPos[nRIndex - 1];
        nRR2 = arynRPos[nRIndex - 1] - arynRPos[nRIndex - 2];
        // 大于停搏的间期不参与计算基准间期
        if (nRR1 < TIME_PULSE
            && nRR2 < TIME_PULSE) {
            *nBaseRR = (nRR1 + nRR2) / 2;
        }
        else if (nRR1 < TIME_PULSE) {
            *nBaseRR = nRR1;
        }
        else if (nRR2 < TIME_PULSE) {
            *nBaseRR = nRR2;
        }
        else {
            *nBaseRR = (nRR1 + nRR2) / 2;
        }
    }
        // 更新基准间期：最新5个NN的平均
    else {
        nRRSum = 0;
        nTime = 0;
        nRTemp = nRIndex - 1;
        while (nRTemp > 0) {
            if (nTime >= 5) {
                break;
            }
            // 选择窦性RR
            if (BS_N == arynBeatStyle[nRTemp]
                && BS_N == arynBeatStyle[nRTemp - 1]) {
                nRR = arynRPos[nRTemp] - arynRPos[nRTemp - 1];
                // 大于停搏的间期不参与计算基准间期
                if (nRR < TIME_PULSE
                    && nRR < *nBaseRR * 1.8) {
                    nRRSum += nRR;
                    nTime++;
                }
            }
            nRTemp--;
        }
        if (nTime >= 3) {
            nBaseRRTemp = nRRSum / nTime;
            if (nBaseRRTemp < *nBaseRR * 1.8) {
                *nBaseRR = nBaseRRTemp;
            }
        }
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：计算形态参数
% 输入参数：arynData         每段心电波形数据
%           nRPos           当前R波位置
%           nSegCount       本分段序号
% 输出参数：dbVShape         当前心搏形态参数
% 编辑日期：2015/11/17
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void CalcVShape(int arynData[ECG_ANA_SAMPLE_RATE * 2], int nRPos, int nSegCount, double *dbVShape) {
    int i, nRPosTemp, nDataLen;
    int nBegin, nEnd;
    __int64_t nSum;
    // 计算长度
    const int c_nCalcPt = 0.04 * ECG_ANA_SAMPLE_RATE;

    // [2016/4/13] 输出初始化
    *dbVShape = 0;

    // [2016/3/12] 对应本段的R波位置，以及本段数据长度
    nRPosTemp = nRPos - (nSegCount - 2) * ECG_ANA_SAMPLE_RATE;
    nDataLen = ECG_ANA_SAMPLE_RATE * 2;

    // [2016/4/13] 异常处理
    if (nRPosTemp < 0 || nRPosTemp > nDataLen - 1) {
        return;
    }
    // 计算的起止点
    nBegin = nRPosTemp - c_nCalcPt;
    if (nBegin < 0) {
        nBegin = 0;
    }
    nEnd = nRPosTemp + c_nCalcPt;
    if (nEnd > nDataLen - 1) {
        nEnd = nDataLen - 1; // 【待改进：若V出现在本段尾部，可能识别为S】
    }
    // 形态参数
    nSum = 0;
    for (i = nBegin; i <= nEnd; i++) {
        nSum += arynData[i];
    }
    // [2016/4/13] 异常处理
    if (0 != arynData[nRPosTemp]) {
        *dbVShape = nSum * 1.0 / arynData[nRPosTemp];
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断房性早搏相关
% 输入参数：arynBeatStyle  	每个心搏类型，与arynRPos对应
%           arynRPos     	已检测到的R波位置
%           nRIndex  		本次判断的R波序号，与arynRPos对应
%           bAfStart        房颤开始的标记
% 输出参数：房性早搏相关结果，全局变量
%           nRhythmNum		实时每s节律事件个数
%           arynRhythmMark	实时每s节律事件编号
% 编辑日期：2016/3/30
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeAPBPremature(int arynBeatStyle[MAX_R_NUM], int arynRPos[MAX_R_NUM], int nRIndex,
                       int bAfStart,
                       int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]) {
    int nIndex;
    int nStart, nEnd;
    // 房速开始标记
    static int s_bAtachycardia = 0;
    // 房早二联律开始标记
    static int s_bABi = 0;
    // 房早三联律开始标记
    static int s_bATri = 0;

    // 房速终点
    if (1 == s_bAtachycardia
        && (BS_N == arynBeatStyle[nRIndex]
            || BS_V == arynBeatStyle[nRIndex])) {
        s_bAtachycardia = 0;
    }
        // 房早二联律终点
    else if (1 == s_bABi) {
        if ((BS_N == arynBeatStyle[nRIndex]
             && BS_A == arynBeatStyle[nRIndex - 1])
            || (BS_A == arynBeatStyle[nRIndex]
                && BS_N == arynBeatStyle[nRIndex - 1])) {
            s_bABi = 1;
        }
        else {
            s_bABi = 0;
        }
    }
        // 房早三联律终点
    else if (1 == s_bATri) {
        if ((BS_N == arynBeatStyle[nRIndex]
             && BS_A == arynBeatStyle[nRIndex - 1]
             && BS_N == arynBeatStyle[nRIndex - 2])
            || (BS_N == arynBeatStyle[nRIndex]
                && BS_N == arynBeatStyle[nRIndex - 1]
                && BS_A == arynBeatStyle[nRIndex - 2])
            || (BS_A == arynBeatStyle[nRIndex]
                && BS_N == arynBeatStyle[nRIndex - 1]
                && BS_N == arynBeatStyle[nRIndex - 2])) {
            s_bATri = 1;
        }
        else {
            s_bATri = 0;
        }
    }
        // [2015/12/7] 房颤开始后，在结束前不判断房性早搏相关
        // 单发房早
    else if (0 == bAfStart
             && nRIndex >= 2
             && BS_A != arynBeatStyle[nRIndex]
             && BS_A == arynBeatStyle[nRIndex - 1]
             && BS_A != arynBeatStyle[nRIndex - 2]) {
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = SINGLE_ATR;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 成对房早
    else if (0 == bAfStart
             && nRIndex >= 3
             && BS_A != arynBeatStyle[nRIndex]
             && BS_A == arynBeatStyle[nRIndex - 1]
             && BS_A == arynBeatStyle[nRIndex - 2]
             && BS_A != arynBeatStyle[nRIndex - 3]) {
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = PAIR_ATR;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 房速起点
    else if (0 == bAfStart
             && 0 == s_bAtachycardia
             && nRIndex >= 3
             && BS_A == arynBeatStyle[nRIndex]
             && BS_A == arynBeatStyle[nRIndex - 1]
             && BS_A == arynBeatStyle[nRIndex - 2]
             && BS_A != arynBeatStyle[nRIndex - 3]) {
        s_bAtachycardia = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = ATR_TACHY;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 房早二联律起点
    else if (0 == bAfStart
             && 0 == s_bABi
             && nRIndex >= 5
             && BS_A == arynBeatStyle[nRIndex]
             && BS_N == arynBeatStyle[nRIndex - 1]
             && BS_A == arynBeatStyle[nRIndex - 2]
             && BS_N == arynBeatStyle[nRIndex - 3]
             && BS_A == arynBeatStyle[nRIndex - 4]
             && BS_N == arynBeatStyle[nRIndex - 5]) {
        s_bABi = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = APB_BI;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 房早三联律起点
    else if (0 == bAfStart
             && 0 == s_bATri
             && nRIndex >= 8
             && BS_A == arynBeatStyle[nRIndex]
             && BS_N == arynBeatStyle[nRIndex - 1]
             && BS_N == arynBeatStyle[nRIndex - 2]
             && BS_A == arynBeatStyle[nRIndex - 3]
             && BS_N == arynBeatStyle[nRIndex - 4]
             && BS_N == arynBeatStyle[nRIndex - 5]
             && BS_A == arynBeatStyle[nRIndex - 6]
             && BS_N == arynBeatStyle[nRIndex - 7]
             && BS_N == arynBeatStyle[nRIndex - 8]) {
        s_bATri = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = APB_TRI;
        *nRhythmNum = *nRhythmNum + 1;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断室性早搏相关
% 输入参数：arynBeatStyle  	每个心搏类型，与arynRPos对应
%           arynRPos     	已检测到的R波位置
%           nRIndex  		本次判断的R波序号，与arynRPos对应
% 输出参数：室性早搏相关结果，全局变量
%           nRhythmNum		实时每s节律事件个数
%           arynRhythmMark	实时每s节律事件编号
% 编辑日期：2016/3/30
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeVEBPremature(int arynBeatStyle[MAX_R_NUM], int arynRPos[MAX_R_NUM], int nRIndex,
                       int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]) {
    int nIndex;
    int nStart, nEnd;
    // 室速开始标记
    static int s_bVtachycardia = 0;
    // 室早二联律开始标记
    static int s_bVBi = 0;
    // 室早三联律开始标记
    static int s_bVTri = 0;

    // 室速终点
    if (1 == s_bVtachycardia
        && (BS_N == arynBeatStyle[nRIndex]
            || BS_A == arynBeatStyle[nRIndex])) {
        s_bVtachycardia = 0;
    }
        // 室早二联律终点
    else if (1 == s_bVBi) {
        if ((BS_N == arynBeatStyle[nRIndex]
             && BS_V == arynBeatStyle[nRIndex - 1])
            || (BS_V == arynBeatStyle[nRIndex]
                && BS_N == arynBeatStyle[nRIndex - 1])) {
            s_bVBi = 1;
        }
        else {
            s_bVBi = 0;
        }
    }
        // 室早三联律终点
    else if (1 == s_bVTri) {
        if ((BS_N == arynBeatStyle[nRIndex]
             && BS_V == arynBeatStyle[nRIndex - 1]
             && BS_N == arynBeatStyle[nRIndex - 2])
            || (BS_N == arynBeatStyle[nRIndex]
                && BS_N == arynBeatStyle[nRIndex - 1]
                && BS_V == arynBeatStyle[nRIndex - 2])
            || (BS_V == arynBeatStyle[nRIndex]
                && BS_N == arynBeatStyle[nRIndex - 1]
                && BS_N == arynBeatStyle[nRIndex - 2])) {
            s_bVTri = 1;
        }
        else {
            s_bVTri = 0;
        }
    }
        // 单发室早
    else if (nRIndex >= 2
             && BS_V != arynBeatStyle[nRIndex]
             && BS_V == arynBeatStyle[nRIndex - 1]
             && BS_V != arynBeatStyle[nRIndex - 2]) {
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = SINGLE_VEN;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 成对室早
    else if (nRIndex >= 3
             && BS_V != arynBeatStyle[nRIndex]
             && BS_V == arynBeatStyle[nRIndex - 1]
             && BS_V == arynBeatStyle[nRIndex - 2]
             && BS_V != arynBeatStyle[nRIndex - 3]) {
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = PAIR_VEN;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 室速起点
    else if (0 == s_bVtachycardia
             && nRIndex >= 3
             && BS_V == arynBeatStyle[nRIndex]
             && BS_V == arynBeatStyle[nRIndex - 1]
             && BS_V == arynBeatStyle[nRIndex - 2]
             && BS_V != arynBeatStyle[nRIndex - 3]) {
        s_bVtachycardia = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = VEN_TACHY;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 室早二联律起点
    else if (0 == s_bVBi
             && nRIndex >= 5
             && BS_V == arynBeatStyle[nRIndex]
             && BS_N == arynBeatStyle[nRIndex - 1]
             && BS_V == arynBeatStyle[nRIndex - 2]
             && BS_N == arynBeatStyle[nRIndex - 3]
             && BS_V == arynBeatStyle[nRIndex - 4]
             && BS_N == arynBeatStyle[nRIndex - 5]) {
        s_bVBi = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = VEB_BI;
        *nRhythmNum = *nRhythmNum + 1;
    }
        // 室早三联律起点
    else if (0 == s_bVTri
             && nRIndex >= 8
             && BS_V == arynBeatStyle[nRIndex]
             && BS_N == arynBeatStyle[nRIndex - 1]
             && BS_N == arynBeatStyle[nRIndex - 2]
             && BS_V == arynBeatStyle[nRIndex - 3]
             && BS_N == arynBeatStyle[nRIndex - 4]
             && BS_N == arynBeatStyle[nRIndex - 5]
             && BS_V == arynBeatStyle[nRIndex - 6]
             && BS_N == arynBeatStyle[nRIndex - 7]
             && BS_N == arynBeatStyle[nRIndex - 8]) {
        s_bVTri = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = VEB_TRI;
        *nRhythmNum = *nRhythmNum + 1;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断窦速/窦缓
% 输入参数：rynRPos     	已检测到的R波位置
%           arynBeatStyle  	每个心搏类型，与arynRPos对应
%           nRIndex  		本次判断的R波序号，与arynRPos对应
%           bAfStart        房颤开始的标记
% 输出参数：窦速窦缓结果，全局变量
%           nRhythmNum		实时每s节律事件个数
%           arynRhythmMark	实时每s节律事件编号
% 编辑日期：2016/3/30
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeSinTachyBrady(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int nRIndex,
                        int bAfStart,
                        int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]) {
    int nRR1, nRR2, nRR3;
    // 窦速RR间期阈值
    const int c_nSinusRRMin = 60 * ECG_ANA_SAMPLE_RATE / 100;
    // 窦缓RR间期阈值
    const int c_nSinusRRMax = 60 * ECG_ANA_SAMPLE_RATE / 60;
    // 窦速开始标记
    static int s_bNtachy = 0;
    // 窦缓开始标记
    static int s_bNbrady = 0;

    if (nRIndex <= 2) {
        return;
    }
    // 最新3个RR间期
    nRR1 = arynRPos[nRIndex] - arynRPos[nRIndex - 1];
    nRR2 = arynRPos[nRIndex - 1] - arynRPos[nRIndex - 2];
    nRR3 = arynRPos[nRIndex - 2] - arynRPos[nRIndex - 3];
    // [2015/12/7] 房颤开始后，在结束前不判断窦性心率过速
    // 窦性心率过速开始
    if (0 == bAfStart
        && 0 == s_bNtachy
        && BS_N == arynBeatStyle[nRIndex]
        && BS_N == arynBeatStyle[nRIndex - 1]
        && BS_N == arynBeatStyle[nRIndex - 2]
        && BS_N == arynBeatStyle[nRIndex - 3]
        && nRR1 < c_nSinusRRMin
        && nRR2 < c_nSinusRRMin
        && nRR3 < c_nSinusRRMin) {
        s_bNtachy = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = SIN_TACHY;
        *nRhythmNum = *nRhythmNum + 1;
    }
    // 窦性心率过速结束
    if (1 == s_bNtachy
        && (BS_N != arynBeatStyle[nRIndex]
            || nRR1 > c_nSinusRRMin)) {
        s_bNtachy = 0;
    }
    // [2015/12/7] 房颤开始后，在结束前不判断窦性心率过缓
    // 窦性心率过缓开始
    if (0 == bAfStart
        && 0 == s_bNbrady
        && BS_N == arynBeatStyle[nRIndex]
        && BS_N == arynBeatStyle[nRIndex - 1]
        && BS_N == arynBeatStyle[nRIndex - 2]
        && BS_N == arynBeatStyle[nRIndex - 3]
        && nRR1 > c_nSinusRRMax
        && nRR2 > c_nSinusRRMax
        && nRR3 > c_nSinusRRMax) {
        s_bNbrady = 1;
        // [2015/11/21] 添加实时节律结果
        arynRhythmMark[*nRhythmNum] = SIN_BRADY;
        *nRhythmNum = *nRhythmNum + 1;
    }
    // 窦性心率过缓结束
    if (1 == s_bNbrady
        && (BS_N != arynBeatStyle[nRIndex]
            || nRR1 < c_nSinusRRMax)) {
        s_bNbrady = 0;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断房颤
% 输入参数：arynRPos     	已检测到的R波位置
%           arynBeatStyle  	每个心搏类型，与arynRPos对应
%           nRIndex  		本次判断的R波序号，与arynRPos对应
%           bAfStart        房颤开始的标记
%           bLastSeg        最后一个分段的标记
% 输出参数：bAfStart         房颤开始的标记
%           房颤结果，全局变量
% 编辑日期：2016/3/30
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeAfrillation(int arynRPos[MAX_R_NUM], int arynBeatStyle[MAX_R_NUM], int nRIndex,
                      int *bAfStart,
                      int *nRhythmNum, int arynRhythmMark[RHYTHM_SORT]) {
    int nMark, i;
    int nStart, nEnd;
    // 判断房颤的心搏个数
    const int c_nJudgeAfBeatNum = 11;
    // 最大V心搏个数，超过此值则本段不做房颤相关判断
    const int c_nVBeatNum = 3;
    // 判断房颤的RR间期
    static int s_arynRR[50] = {0};
    // RR间期计数
    static int s_nRRCount = 0;
    // V心搏个数计数
    static int s_nVCount = 0;

    // 记录RR间期
    s_arynRR[s_nRRCount] = arynRPos[nRIndex] - arynRPos[nRIndex - 1];
    s_nRRCount = s_nRRCount + 1;
    // 统计V心搏个数
    if (BS_V == arynBeatStyle[nRIndex]) {
        s_nVCount++;
    }
    // 每c_nJudgeAfBeatNum个心搏判断一次房颤
    if (s_nRRCount == c_nJudgeAfBeatNum - 1) {
        nMark = 0;
        if (s_nVCount < c_nVBeatNum) {
            // 判断本段是否满足房颤条件
            JudgeAf(s_arynRR, s_nRRCount, &nMark);
        }
        // 房颤起点【移植时记得添加实时结果反馈】
        if (0 == *bAfStart
            && 1 == nMark) {
            *bAfStart = 1;
            // [2015/12/5] 添加实时节律结果
            arynRhythmMark[*nRhythmNum] = AFRIL;
            *nRhythmNum = *nRhythmNum + 1;
        }
            // 房颤终点
        else if (1 == *bAfStart
                 && 0 == nMark) {
            *bAfStart = 0;
        }
        // RR间期清零
        for (i = 0; i < c_nJudgeAfBeatNum - 1; i++) {
            s_arynRR[i] = 0;
        }
        s_nRRCount = 0;
        // V心搏个数清零
        s_nVCount = 0;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断本段是否满足房颤条件
% 输入参数：arynRR     	本段RR间期
%           nRRCount  	本段RR间期个数
% 输出参数：nMark        满足房颤条件=1，否则=0
% 编辑日期：2015/12/2
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeAf(int arynRR[AF_RR_MAX_NUM], int nRRCount, int *nMark) {
    int nSum, nRRAvg, nHRAvg;
    int bNeighborRRMark, bRRSort;
    int i;

    // 心率绝对阈值，心率低于此值则不满足房颤条件【待调】
    const int c_nHRThr = 55;
    // 输出初始化
    *nMark = 0;

    // 计算本段平均心率
    nSum = 0;
    for (i = 0; i < nRRCount; i++) {
        nSum += arynRR[i];
    }
    nRRAvg = nSum / nRRCount;
    nHRAvg = 60 * ECG_ANA_SAMPLE_RATE / nRRAvg;
    // 满足心率条件
    if (nHRAvg > c_nHRThr) {
        // 判断相邻RR间期
        JudgeNeighborRR(arynRR, nRRCount, &bNeighborRRMark);
        // 统计RR间期种类
        JudgeRRSort(arynRR, nRRCount, nRRAvg, &bRRSort);
        // 满足房颤条件
        if (1 == bNeighborRRMark
            && 1 == bRRSort) {
            *nMark = 1;
        }
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断本段是否满足相邻心率不齐
% 输入参数：arynRR				本段RR间期
%           nRRCount            本段RR间期个数
% 输出参数：bNeighborRRMark  	满足相邻心率不齐=1，否则=0
% 编辑日期：2015/12/3
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeNeighborRR(int arynRR[AF_RR_MAX_NUM], int nRRCount, int *bNeighborRRMark) {
    int nNeighborRRDifNum, nRRDifTemp, dbRRAvgTemp;
    int i;

    // 相邻RR间期差异比例阈值，超过此阈值则认为存在差异
    double c_dbneighborRRDifRatioThr = 0.08;
    // 相邻RR间期差异个数阈值，超过此阈值则认为相邻心率不齐
    double c_dbneighborRRDifNumRatioThr = 0.4;

    // 输出初始化
    *bNeighborRRMark = 0;

    // 统计相邻RR间期差异个数
    nNeighborRRDifNum = 0;
    for (i = 1; i < nRRCount; i++) {
        nRRDifTemp = arynRR[i] - arynRR[i - 1];
        dbRRAvgTemp = (arynRR[i] + arynRR[i - 1]) * 1.0 / 2;
        if (abs(nRRDifTemp * 1.0 / dbRRAvgTemp) > c_dbneighborRRDifRatioThr) {
            nNeighborRRDifNum++;
        }
    }
    // 满足相邻心率不齐
    if (nNeighborRRDifNum >= nRRCount * c_dbneighborRRDifNumRatioThr) {
        *bNeighborRRMark = 1;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：判断本段是否满足心率种类多
% 输入参数：arynRR               本段RR间期
%           nRRCount            本段RR间期个数
%           nRRAvg              本段平均心率
% 输出参数：bRRSort              满足心率种类多=1，否则=0
% 编辑日期：2015/12/3
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void JudgeRRSort(int arynRR[AF_RR_MAX_NUM], int nRRCount, int nRRAvg, int *bRRSort) {
    int i, j, nTemp, bSort, nRRDif;

    // RR间期不同种类分类阈值，平均值的0.06
    double dbRRDifThr = nRRAvg * 0.06;
    // RR间期种类个数阈值，超过此阈值认为满足心率种类多
    const int c_nSortNumThr = 3;
    // RR间期种类数，初始化
    int nRRSortNum = 1;
    // 每类的RR间期个数，初始化
    int arynRRNumInSort[AF_RR_MAX_NUM] = {0};
    // RR间期种类统计，初始化
    int arypnRRSort[AF_RR_MAX_NUM][AF_RR_MAX_NUM] = {0};
    // 输出初始化
    *bRRSort = 0;

    // RR间期从小到大排序
    for (i = 0; i < nRRCount; i++) {
        for (j = i + 1; j < nRRCount; j++) {
            if (arynRR[j] < arynRR[i]) {
                nTemp = arynRR[i];
                arynRR[i] = arynRR[j];
                arynRR[j] = nTemp;
            }
        }
    }
    // 统计RR间期种类
    arynRRNumInSort[0] = 1;
    arypnRRSort[0][0] = arynRR[0];
    for (i = 1; i < nRRCount; i++) {
        bSort = 0;
        // 与已有种类匹配
        for (j = 0; j < nRRSortNum; j++) {
            nRRDif = arynRR[i] - arypnRRSort[j][0];
            // 与本类第一个RR相差小于阈值，归为本类
            if (nRRDif < dbRRDifThr) {
                arypnRRSort[j][arynRRNumInSort[j]] = arynRR[i];
                arynRRNumInSort[j]++;
                bSort = 1;
                break;
            }
            else {
                continue;
            }
        }
        // 未匹配，记入新的一类
        if (0 == bSort) {
            arypnRRSort[nRRSortNum][0] = arynRR[i];
            arynRRNumInSort[nRRSortNum] = 1;
            nRRSortNum++;
        }
    }
    // 判断RR种类数
    if (nRRSortNum > c_nSortNumThr) {
        *bRRSort = 1;
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：获取实时心率
% 输入参数：
% 输出参数：
% 编辑日期：2016/4/26
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int Ecg_GetRealHeartRate() {
    // 未更新实时心率的次数
    static int nCount = 0;

    if (IsHRUpdate == 1) {
        // 是否更新实时心率的标记，置零
        IsHRUpdate = 0;
        // 超过3秒未更新实时心率的标记，置零
        nCount = 0;
        return RealTimeHR;
    }
    else {
        nCount++;
        // 超过3秒未更新实时心率，返回-100
        if (nCount > 3) {
            return -100;
        }
    }
    return RealTimeHR;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：获取实时检测的R个数、各R波位置、心搏类型、RR间期
% 输入参数：
% 输出参数：nUpdateRNum			实时检测的R个数
			arynRPos			各R波位置
			arynBeatStyle		各心搏类型
			arynRR				各RR间期
% 编辑日期：2016/4/27
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void Ecg_GetRealBeatVal(int *nUpdateRNum, int arynRPos[4], int arynBeatStyle[4], int arynRR[4]) {
    int i;

    // 实时检测的R个数
    *nUpdateRNum = UpdateRNum;
    // 各R波位置、心搏类型、RR间期
    for (i = 0; i < UpdateRNum; i++) {
        arynRPos[i] = gRPos[i];
        arynBeatStyle[i] = gBeatStyle[i];
        arynRR[i] = gRR[i];
    }
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：记录结束后，获取总心搏个数、平均心率、最快心率、最慢心率、正常异常心率比例
% 输入参数：
% 输出参数：nRNum			心搏总数
			nHRAvg			平均心率
			nHRMax			最快心率
			nHRMin			最慢心率
			arynHRPercent	正常异常心率比例，包括心率过快、稍快、正常、稍慢、过慢
% 编辑日期：2016/3/31
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
void Ecg_GetStatisitic(int *nRNum, int *nHRAvg, int *nHRMax, int *nHRMin, int arynHRPercent[5]) {
    int nHRNum;

    // 心搏总数
    *nRNum = RNum;
    // 平均心率
    *nHRAvg = AvgHR;
    // 最快心率
    *nHRMax = FastestHR;
    // 最慢心率
    *nHRMin = SlowestHR;

    // 正常异常心率总数
    nHRNum = FasterHRNum + FastHRNum + NormalHRNum + SlowHRNum + SlowerHRNum;
    if (0 == nHRNum) {
        return;
    }
    // 心率过快比例
    arynHRPercent[0] = (int) (FasterHRNum * 100 / nHRNum + 0.5);
    // 心率稍快比例
    arynHRPercent[1] = (int) (FastHRNum * 100 / nHRNum + 0.5);
    // 心率正常比例
    arynHRPercent[2] = (int) (NormalHRNum * 100 / nHRNum + 0.5);
    // 心率稍慢比例
    arynHRPercent[3] = (int) (SlowHRNum * 100 / nHRNum + 0.5);
    // 心率过慢比例
    arynHRPercent[4] = (int) (SlowerHRNum * 100 / nHRNum + 0.5);
}