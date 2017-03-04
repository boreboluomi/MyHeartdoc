#include "GlobVari.h"
#include "Rt_ecgfilter.h"
#include "DetFuns.h"

#define LF_ORDER 2    // syn[2016/1/9] 调试低通滤波
#define HF_ORDER 1
#define ZF_ORDER 1
#define NF_ORDER 2
#define NF_ORDER2 4    // syn[2016/1/16] 调试50hz工频滤波
#define NF_ORDER3 6    // syn[2016/1/23] 调试50hz工频滤波

// syn[2016/4/16] 按赵工修改低通滤波为40hz
int g50bLowPass150[LF_ORDER + 1] = {42149720, 84299440, 42149720};
int g50aLowPass150[LF_ORDER + 1] = {268435456, -163764549, 63927973};
// 原系数
// int g50bLowPass150[LF_ORDER+1] = {105048397,210096793,105048397};
// int g50aLowPass150[LF_ORDER+1] = {268435456,99194250,52563880};
int g50150x[MAX_ACH_NUM][LF_ORDER + 1] = {0};
long long g50150y[MAX_ACH_NUM][LF_ORDER + 1] = {0};

// syn[2016/1/5] 高通滤波器由0.26hz改为0.05hz，用于送检
double g50bHighPass005[HF_ORDER + 1] = {0.9994, -0.9994};    // 0.05hz
double g50aHighPass005[HF_ORDER + 1] = {1.0000, -0.9987};
// double g50bHighPass005[HF_ORDER+1] = {0.9967,-0.9967};	// 0.26hz
// double g50aHighPass005[HF_ORDER+1] = {1.0000,-0.9935};
// syn[2016/3/5] 高通滤波器改为0.5hz，用于实际人体数据
// double g50bHighPass005[HF_ORDER+1] = {0.9938,-0.9938};		// 0.5hz
// double g50aHighPass005[HF_ORDER+1] = {1.0000,-0.9875};
double g50005x[MAX_ACH_NUM][HF_ORDER + 1] = {0};
double g50005y[MAX_ACH_NUM][HF_ORDER + 1] = {0};


// syn[2016/1/16] 调试50hz工频滤波
// 49~51衰减3，40~60衰减0.5，2阶
double g51bNotch1[NF_ORDER + 1] = {1.00000, -0.63808, 1.00000};
double g51aNotch1[NF_ORDER + 1] = {1.00000, -0.61626, 0.93160};
double g51x1[MAX_ACH_NUM][NF_ORDER + 1] = {0};
double g51y1[MAX_ACH_NUM][NF_ORDER + 1] = {0};
// 48~52衰减3，45~55衰减0.5，4阶
double g51bNotch2[NF_ORDER2 + 1] = {0.9287, -1.1570, 2.2177, -1.1570, 0.9287};
double g51aNotch2[NF_ORDER2 + 1] = {1.0000, -1.1999, 2.2126, -1.1141, 0.8624};
double g51x2[MAX_ACH_NUM][NF_ORDER2 + 1] = {0};
double g51y2[MAX_ACH_NUM][NF_ORDER2 + 1] = {0};
// // 48~52衰减10，45~55衰减0.5，6阶
// double g51bNotch3[NF_ORDER3+1]={0.7476,-1.2528, 2.9425,-2.6358, 2.9425,-1.2528, 0.7476};
// double g51aNotch3[NF_ORDER3+1]={1.0000,-1.5144, 3.1900,-2.6002, 2.6312,-1.0267, 0.5588};
// double g51x3[MAX_ACH_NUM][NF_ORDER3+1]={0};
// double g51y3[MAX_ACH_NUM][NF_ORDER3+1]={0};
// 50~51.5衰减3，49.5~52衰减0.5，6阶
double g51bNotch3[NF_ORDER3 + 1] = {0.96284529196545521, -1.6821668659367519, 3.8681620555356857,
                                    -3.5544986656200965, 3.8681620555356857, -1.6821668659367519,
                                    0.96284529196545521};
double g51aNotch3[
        NF_ORDER3 + 1] = {1.0000, -1.7250320987838257, 3.9162746032424103, -3.5536947342031477,
                          3.8186690355425066, -1.6401055645066118, 0.92707105621734787};
double g51x3[MAX_ACH_NUM][NF_ORDER3 + 1] = {0};
double g51y3[MAX_ACH_NUM][NF_ORDER3 + 1] = {0};

// 原系数
int g51bNotch[NF_ORDER + 1] = {268435456, -434371988, 268435456};
int g51aNotch[NF_ORDER + 1] = {268435456, -430425947, 265610449};
int g51x[MAX_ACH_NUM][NF_ORDER + 1] = {0};
long long g51y[MAX_ACH_NUM][NF_ORDER + 1] = {0};
int g52bNotch[NF_ORDER + 1] = {268435456, -434371988, 268435456};
int g52aNotch[NF_ORDER + 1] = {268435456, -433755641, 265650797};
long long g52x[MAX_ACH_NUM][NF_ORDER + 1] = {0};
long long g52y[MAX_ACH_NUM][NF_ORDER + 1] = {0};

/*************************************************
Function:       wgif_highfilter
Description:    整型滤波器实现
Input:          *pf：滤波器内存空间
X: 待滤波数据
Output:         无
Return:         滤波后数据
Others:         内存未分配或者滤波阶数<=0时不滤波
*************************************************/
int wgif_highfilter(int leadx, ORG_DATA_TYPE *pDataInput) {
    static short int LA = -100, RA = -100, timeLa = 0, timeRa = 0;
    static short int leaFlag = -100, filterFlag = -100, notFlag = -100, Flag = 0, delaytime = 0;
    //
    if ((leaFlag != gEcgAnaConfig.mAchNum) || (filterFlag != gEcgAnaConfig.mFilterMode) ||
        (notFlag != gEcgAnaConfig.mNotchOn)) {
        Flag = 1;
        leaFlag = gEcgAnaConfig.mAchNum;
        filterFlag = gEcgAnaConfig.mFilterMode;
        notFlag = gEcgAnaConfig.mNotchOn;
        delaytime = gEcgAnaConfig.mAchNum * 2;
        EcgVarInit();
    }
    if (delaytime > 0) {
        delaytime--;
        Flag = 1;
    }
    else { Flag = 0; }
    if (Flag == 1) {
        g50005x[leadx][1] = (double) pDataInput[leadx];
        g50005y[leadx][1] = 0;
    }
    else {
        if (ecgLeadlOffState.LaOff != LA) {
            LA = ecgLeadlOffState.LaOff;
            timeLa = gEcgAnaConfig.mAchNum * 8;
        }
        if (timeLa > 0) {
            timeLa--;
            if ((leadx == 0) || (leadx == 2) || (leadx == 3)) {
                g50005x[leadx][1] = (double) pDataInput[leadx];
                g50005y[leadx][1] = 0;
            }
        }
        else {
            timeLa = 0;
        }

        if (ecgLeadlOffState.RaOff != RA) {
            RA = ecgLeadlOffState.RaOff;
            timeRa = gEcgAnaConfig.mAchNum * 8;
        }
        if (timeRa > 0) {
            timeRa--;
            if ((leadx == 0) || (leadx == 2) || (leadx == 3)) {
                g50005x[leadx][1] = (double) pDataInput[leadx];
                g50005y[leadx][1] = 0;
            }
        }
        else {
            timeRa = 0;
        }
    }
    //
    if (gsRtfd.nHpType == HPF_TYPE_005) {
        g50005x[leadx][0] = (double) pDataInput[leadx];
        g50005y[leadx][0] = g50bHighPass005[0] * g50005x[leadx][0] + g50bHighPass005[1] *
                                                                     g50005x[leadx][1] -
                            g50aHighPass005[1] * g50005y[leadx][1];
        g50005x[leadx][1] = g50005x[leadx][0];
        g50005y[leadx][1] = g50005y[leadx][0];

        return (int) g50005y[leadx][0];
    }
    else {
        return pDataInput[leadx];
    }
}

/*************************************************
Function:       wgif_no50filter
Description:    整型滤波器实现
Input:          *pf：滤波器内存空间
X: 待滤波数据
Output:         无
Return:         滤波后数据
Others:         内存未分配或者滤波阶数<=0时不滤波
*************************************************/
int wgif_no50filter(int leadx, ORG_DATA_TYPE *pDataInput) {
    if (gEcgAnaConfig.mNotchOn != 0) {
        g51x[leadx][0] = pDataInput[leadx];
        g51y[leadx][0] = ((long long) g51bNotch[0] * (long long) g51x[leadx][0] +
                          (long long) g51bNotch[1] * (long long) g51x[leadx][1] +
                          (long long) g51bNotch[2] * (long long) g51x[leadx][2] -
                          (long long) g51aNotch[1] * g51y[leadx][1] -
                          (long long) g51aNotch[2] * g51y[leadx][2]) >> AMP_COEF;
        g51x[leadx][2] = g51x[leadx][1];
        g51x[leadx][1] = g51x[leadx][0];
        g51y[leadx][2] = g51y[leadx][1];
        g51y[leadx][1] = g51y[leadx][0];

        g52x[leadx][0] = (g51y[leadx][0] * 267029356) >> AMP_COEF;
        g52y[leadx][0] = ((long long) g52bNotch[0] * g52x[leadx][0] +
                          (long long) g52bNotch[1] * g52x[leadx][1] +
                          (long long) g52bNotch[2] * g52x[leadx][2] -
                          (long long) g52aNotch[1] * g52y[leadx][1] -
                          (long long) g52aNotch[2] * g52y[leadx][2]) >> AMP_COEF;
        g52x[leadx][2] = g52x[leadx][1];
        g52x[leadx][1] = g52x[leadx][0];
        g52y[leadx][2] = g52y[leadx][1];
        g52y[leadx][1] = g52y[leadx][0];

        return (int) ((g52y[leadx][0] * 267029356) >> AMP_COEF);
    }
    else {
        return pDataInput[leadx];
    }
}

// syn[2016/1/16] 调试50hz工频滤波
int wgif_no50filter1(int leadx, ORG_DATA_TYPE *pDataInput) {
    if (gEcgAnaConfig.mNotchOn != 0) {
        g51x1[leadx][0] = (double) pDataInput[leadx];
        g51y1[leadx][0] = g51bNotch1[0] * g51x1[leadx][0] + g51bNotch1[1] * g51x1[leadx][1] +
                          g51bNotch1[2] * g51x1[leadx][2] -
                          g51aNotch1[1] * g51y1[leadx][1] - g51aNotch1[2] * g51y1[leadx][2];
        g51x1[leadx][2] = g51x1[leadx][1];
        g51x1[leadx][1] = g51x1[leadx][0];
        g51y1[leadx][2] = g51y1[leadx][1];
        g51y1[leadx][1] = g51y1[leadx][0];

        return (int) g51y1[leadx][0];
    }
    else {
        return pDataInput[leadx];
    }
}

// syn[2016/1/16] 调试50hz工频滤波
int wgif_no50filter2(int leadx, ORG_DATA_TYPE *pDataInput) {
    if (gEcgAnaConfig.mNotchOn != 0) {
        g51x2[leadx][0] = (double) pDataInput[leadx];
        g51y2[leadx][0] = g51bNotch2[0] * g51x2[leadx][0] + g51bNotch2[1] * g51x2[leadx][1] +
                          g51bNotch2[2] * g51x2[leadx][2] + g51bNotch2[3] * g51x2[leadx][3] +
                          g51bNotch2[4] * g51x2[leadx][4] -
                          g51aNotch2[1] * g51y2[leadx][1] - g51aNotch2[2] * g51y2[leadx][2] -
                          g51aNotch2[3] * g51y2[leadx][3] - g51aNotch2[4] * g51y2[leadx][4];
        g51x2[leadx][4] = g51x2[leadx][3];
        g51x2[leadx][3] = g51x2[leadx][2];
        g51x2[leadx][2] = g51x2[leadx][1];
        g51x2[leadx][1] = g51x2[leadx][0];
        g51y2[leadx][4] = g51y2[leadx][3];
        g51y2[leadx][3] = g51y2[leadx][2];
        g51y2[leadx][2] = g51y2[leadx][1];
        g51y2[leadx][1] = g51y2[leadx][0];

        return (int) g51y2[leadx][0];
    }
    else {
        return pDataInput[leadx];
    }
}

// syn[2016/1/23] 调试50hz工频滤波
int wgif_no50filter3(int leadx, ORG_DATA_TYPE *pDataInput) {
    if (gEcgAnaConfig.mNotchOn != 0) {
        g51x3[leadx][0] = (double) pDataInput[leadx];
        g51y3[leadx][0] = g51bNotch3[0] * g51x3[leadx][0] + g51bNotch3[1] * g51x3[leadx][1] +
                          g51bNotch3[2] * g51x3[leadx][2] + g51bNotch3[3] * g51x3[leadx][3] +
                          g51bNotch3[4] * g51x3[leadx][4] + g51bNotch3[5] * g51x3[leadx][5] +
                          g51bNotch3[6] * g51x3[leadx][6] -
                          g51aNotch3[1] * g51y3[leadx][1] - g51aNotch3[2] * g51y3[leadx][2] -
                          g51aNotch3[3] * g51y3[leadx][3] - g51aNotch3[4] * g51y3[leadx][4] -
                          g51aNotch3[5] * g51y3[leadx][5] - g51aNotch3[6] * g51y3[leadx][6];
        g51x3[leadx][6] = g51x3[leadx][5];
        g51x3[leadx][5] = g51x3[leadx][4];
        g51x3[leadx][4] = g51x3[leadx][3];
        g51x3[leadx][3] = g51x3[leadx][2];
        g51x3[leadx][2] = g51x3[leadx][1];
        g51x3[leadx][1] = g51x3[leadx][0];
        g51y3[leadx][6] = g51y3[leadx][5];
        g51y3[leadx][5] = g51y3[leadx][4];
        g51y3[leadx][4] = g51y3[leadx][3];
        g51y3[leadx][3] = g51y3[leadx][2];
        g51y3[leadx][2] = g51y3[leadx][1];
        g51y3[leadx][1] = g51y3[leadx][0];

        return (int) g51y3[leadx][0];
    }
    else {
        return pDataInput[leadx];
    }
}

/*************************************************
Function:       wgif_lowfilter
Description:    整型滤波器实现
Input:          *pf：滤波器内存空间
X: 待滤波数据
Output:         无
Return:         滤波后数据
Others:         内存未分配或者滤波阶数<=0时不滤波
*************************************************/
int wgif_lowfilter(int leadx, ORG_DATA_TYPE *pDataInput) {
    if (gsRtfd.nLpType == LPF_TYPE_100) {
        g50150x[leadx][0] = pDataInput[leadx];

        g50150y[leadx][0] = ((long long) g50bLowPass150[0] * (long long) g50150x[leadx][0] +
                             (long long) g50bLowPass150[1] * (long long) g50150x[leadx][1] +
                             (long long) g50bLowPass150[2] * (long long) g50150x[leadx][2] -
                             (long long) g50aLowPass150[1] * g50150y[leadx][1] -
                             (long long) g50aLowPass150[2] * g50150y[leadx][2]) >> AMP_COEF;

        // syn[2016/1/12] 补充漏掉的代码
        g50150y[leadx][2] = g50150y[leadx][1];
        g50150y[leadx][1] = g50150y[leadx][0];
        g50150x[leadx][2] = g50150x[leadx][1];
        g50150x[leadx][1] = g50150x[leadx][0];

        return (int) g50150y[leadx][0];
    }
    else {
        return pDataInput[leadx];
    }
}

int Inrest_RtFilter(unsigned char nNotchOnOff, unsigned char nLpType, unsigned char nHpType) {
    if (nLpType > 4 || nHpType > 4) return -1;

    gsRtfd.bNotchOn = (nNotchOnOff ? 1 : 0);
    gsRtfd.nLpType = nLpType;
    gsRtfd.nHpType = nHpType;

    return 1;
}

int RT_Fil_Display(ORG_DATA_TYPE *pDataIn, ORG_DATA_TYPE *pDataOut, int nAchNum) {
    int i, j = 0, temp, pDataInput[MAX_ACH_NUM] = {0};
    //
    for (i = 0; i < nAchNum; i++) {
        pDataInput[i] = pDataIn[i];
    }
    for (i = 0; i < nAchNum; i++) {
        temp = wgif_highfilter(i, pDataInput);
        pDataInput[i] = temp;
        // syn[2016/1/16] 试验工频滤波【可三选一测试看效果】
        // 赵哥原工频滤波
        //temp = wgif_no50filter(i,pDataInput );
        // 49~51衰减3，40~60衰减0.5，2阶
        //temp = wgif_no50filter1(i,pDataInput );
        // 48~52衰减3，45~55衰减0.5，4阶
        //temp = wgif_no50filter2(i,pDataInput );
        // 50~51.5衰减3，49.5~52衰减0.5，6阶
        temp = wgif_no50filter3(i, pDataInput);
        pDataInput[i] = temp;
        temp = wgif_lowfilter(i, pDataInput);
        pDataOut[i] = temp;
    }
    //
    j = 1;
    return j;
}

int RTFD_Filter(ORG_DATA_TYPE *pDataIn, ORG_DATA_TYPE *pDataOut, int nAchNum) {
    return RT_Fil_Display(pDataIn, pDataOut, nAchNum);
}