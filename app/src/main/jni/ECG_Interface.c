//#include "stdafx.h"
#include "DataType.h"
#include "stdio.h"
#include "R_rMain.h"
#include "GlobVari.h"
#include "EcgDiagnosis.h"
#include "ECG_Interface.h"
#include "DetFuns.h"
#include "stdlib.h"

//
///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
INT32 EcgInitConfig(void) {
    CHAR FLAG = 0;//
    //
    Init_Config();
    //
    Inrest_RtFilter(gEcgAnaConfig.mNotchOn, gEcgAnaConfig.mLpType, gEcgAnaConfig.mHpType);
    //
    Init_QrsDetection();
    //
    FLAG = 1;
    return FLAG;
}

///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
// syn [2016/4/23] 
INT32 EcgRTFilDeAn(YRT_Parameter *EcgYrt, int *nRPos, short *laii, short *raiii,
                   short *leadoffstate) {
    //
    INT32 i;
    INT32 original[MAX_ACH_NUM] = {0}, fdisplay[MAX_ACH_NUM] = {0}, transferxyz[MAX_ACH_NUM] = {0};
    //FILE *fp1,*fp2,*fp3;

    for (i = 0; i < gEcgAnaConfig.mAchNum; i++) {
        original[i] = (*EcgYrt).EcgWave;

    }
    // 	fp1=fopen("E:\\original.dat","ab");
    // 	fwrite(&original[gEcgAnaConfig.mArrChannel],sizeof(int),1,fp1);
    // 	fclose(fp1);
    //
    RT_Fil_Display(original, fdisplay, gEcgAnaConfig.mAchNum);

    // syn[2016/4/23] 移到R波检测前处理。
    if ((ecgLeadlOffState.RaOff != *raiii) || (ecgLeadlOffState.LaOff != *laii)) {
        *laii = ecgLeadlOffState.LaOff;
        *raiii = ecgLeadlOffState.RaOff;
        *leadoffstate = ECG_DELAYTDISPLAY;
    }
    if (*leadoffstate > 0) {
        fdisplay[0] = 0;
        *leadoffstate = *leadoffstate - 1;
    }
    //
    if ((ecgLeadlOffState.RaOff == TRUE) || (ecgLeadlOffState.LaOff == TRUE)) {
        fdisplay[0] = 0;
    }

    if (gEcgAnaConfig.mAchNum == 1) {
        transferxyz[0] = fdisplay[0];
    }
    for (i = 0; i < gEcgAnaConfig.mAchNum; i++) {
        fdisplay[i] = fdisplay[i];
        (*EcgYrt).EcgFilt = fdisplay[i];
    }

    TimerFlag = 0;
    gOrigBuf[ThreeSecond] = transferxyz[gEcgAnaConfig.mArrLead];
    ThreeSecond++;
    if ((ThreeSecond) >= ECG_ANA_SAMPLE_RATE * ECG_ANA_TOTAL_SECOND) {
        ThreeSecond = 0;
    }
    if ((ThreeSecond % ECG_ANA_SAMPLE_RATE) != 0) {
        EcgQrsDetAna(nRPos);
    }
    else {
        ++timing;
        if ((timing >= 5) || (timing <= 0)) { timing = 1; }
        TimerFlag = 1;
        EcgQrsDetAna(nRPos);
        gRGlobals.qrsDetected = 0;
        // syn[2016/4/9] 计时，单位秒
        SecondCount++;
    }
    return TimerFlag;
}

//
INT32 EcgQrsDetAna(int *nRPos) {
    CHAR FCMD = 0;
    rr_ana(nRPos);
    FCMD = 1;
    return FCMD;
}