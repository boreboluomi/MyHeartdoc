#ifndef ECG_FILTER_H_209345823408102834
#define ECG_FILTER_H_209345823408102834

#include "Rt_ecgfilter.h"

#define AMP_COEF 28
/*******************************

********************************/
#define LEAD_I        0
#define LEAD_II        1
#define LEAD_III    2
#define LEAD_AVR    3
#define LEAD_AVL    4
#define LEAD_AVF    5
#define LEAD_V1        6
#define LEAD_V2        7
#define LEAD_V3        8
#define LEAD_V4        9
#define LEAD_V5        10
#define    LEAD_V6        11
#define LEAD_V      12
#define LEAD_MCL    13


/*******************************

********************************/
#define NOTCH_TYPE_OFF    0x0
#define NOTCH_TYPE_50    0x1
#define NOTCH_TYPE_60    0x2

#define HPF_TYPE_OFF            0x0
#define HPF_TYPE_005            0x1
#define HPF_TYPE_050            0x2
#define HPF_TYPE_1                0x3

#define LPF_TYPE_OFF            0x0
#define LPF_TYPE_20                0x1
#define LPF_TYPE_40                0x2
#define LPF_TYPE_100            0x3

/*******************************

********************************/
#define        MOVE_BIT_LR                2//
#define        MAX_SAMP_RATE            250
#define        MAX_LEAD_NUM            12
#define        MAX_ACH_NUM                1

/*******************************

***************************/
#define ORG_DATA_TYPE  int
#define ECG_DATA_TYPE  int

/*******************************

***************************/
typedef struct {
    unsigned char bNotchOn;    //
    unsigned char nLpType;    //
    unsigned char nHpType;    //
} RTFD_SETTINGS;

/*************************************************
*************************************************/
int Inrest_RtFilter(unsigned char nNotchOnOff, unsigned char nLpType, unsigned char nHpType);

/*************************************************

*************************************************/
int RTFD_Filter(ORG_DATA_TYPE *pDataIn, ORG_DATA_TYPE *pDataOut, int nAchNum);

int wgif_highfilter(int leadx, ORG_DATA_TYPE *pDataInput);

int wgif_no50filter(int leadn, ORG_DATA_TYPE *pDataInput);

int wgif_no50filter1(int leadx, ORG_DATA_TYPE *pDataInput);    // syn[2016/1/16] 调试50hz工频滤波
int wgif_no50filter2(int leadx, ORG_DATA_TYPE *pDataInput);    // syn[2016/1/16] 调试50hz工频滤波
int wgif_no50filter3(int leadx, ORG_DATA_TYPE *pDataInput);    // syn[2016/1/23] 调试50hz工频滤波
int wgif_lowfilter(int leadx, ORG_DATA_TYPE *pDataInput);

int RT_Fil_Display(ORG_DATA_TYPE *pDataIn, ORG_DATA_TYPE *pDataOut, int nAchNum);

#endif
