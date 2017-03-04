///////////////////////////////////////////////////////////////////////////////
#include "EcgDiagnosis.h"
#include "Ecg_Interface.h"
#include "GlobVari.h"
#include "DetFuns.h"
#include "stdio.h"
#include "stdlib.h"
#include "string.h"

////////////////////////////////////////////////////////////////////////////////
void Ecg_Reset(void) {
    EcgInitConfig();
}

////////////////////////////////////////////////////////////////////////////////
int Ecg_SingleDataProcess(int source, int *nRPos) {
    YRT_Parameter EcgYrt;
    int wave = 0;
    static short laii = -100, raiii = -100, leadoffstate = 0;
    //
    EcgYrt.EcgWave = source;
    if ((ecgLeadlOffState.LaOff == TRUE) || (ecgLeadlOffState.RaOff == TRUE)) {
        EcgYrt.EcgWave = 0;
        BlankLength = 0;
        gRGlobals.timePast = 0;
        gQrsAnaInfo.isEcgLost = FALSE;
        EcgVarInit();
    }
    gEcgAnaConfig.mArrLead = 0;
    EcgRTFilDeAn(&EcgYrt, nRPos, &laii, &raiii, &leadoffstate);
    //
    wave = EcgYrt.EcgFilt;
    // syn[2016/4/23] 移到R波检测前处理。
// 	if ((ecgLeadlOffState.RaOff!=raiii)||(ecgLeadlOffState.LaOff!=laii))
// 	{
// 		laii=ecgLeadlOffState.LaOff;
// 		raiii=ecgLeadlOffState.RaOff;
// 		leadoffstate=ECG_DELAYTDISPLAY;
// 	}
// 	if (leadoffstate>0)
// 	{
// 		wave=0;      
// 		leadoffstate--;
// 	}
// 	//
// 	if ((ecgLeadlOffState.RaOff==TRUE)||(ecgLeadlOffState.LaOff==TRUE))
// 	{
// 		wave=0;
// 	}
    //
    return wave;
}

////////////////////////////////////////////////////////////////////////////////
int Ecg_GetHeartRate(void) {
    return HeartRate;
}

////////////////////////////////////////////////////////////////////////////////
int Ecg_GetDiagnosisResult(void) {
    int arr = 0;
    arr = gArrAnalysisInfo.Yh_arrCode;

    return (int) arr;
}