#include "stdio.h"
#include "ProcessShowWave.h"
#include "EcgDiagnosis.h"

// [2016/1/9] 原数据段索引号
int INDEX_ORG[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
// [2016/1/9] 插值后数据段索引号
double INDEX_SHOW[] = {0, 0.75, 1.5, 2.25, 3, 3.75, 4.5, 5.25, 6, 6.75, 7.5, 8.25, 9};

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：波形处理（滤波+插值）、心搏检测
% 输入参数：arynData			原始心电波形，长度10个点
% 输出参数：arydbFilData        基础滤波后波形，长度10个点
%           aryfDataShow    	返回显示的波形，长度13个点
%           nRPos    			本10个点检测到的R波位置，如果没有检测到则为0
% 编辑日期：2016/3/9
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int ProcessWave(int arynData[DATALEN_ORG], float arydbFilData[DATALEN_ORG],
                float aryfDataShow[DATALEN_SHOW], int *nRPos) {
    int i;
    int nSum = 0;
    int nPtIndex = 0;
    int nData, nWave;
    // [2016/1/9] 记录实时心率
    int nHR;

    // 输出初始化
    for (i = 0; i < DATALEN_SHOW; i++) {
        aryfDataShow[i] = 0;
    }

    // 心电数据值缩小倍数，以适应APP显示
    //nData /= DE_MULT;

    // [2016/1/12] 逐点滤波
    for (i = 0; i < DATALEN_ORG; i++) {
        nData = arynData[i];
        // 基础滤波
        nWave = Ecg_SingleDataProcess(nData, nRPos);
        arydbFilData[i] = nWave;
    }

    // 多项式插值：linear内插
    LinearInterp(INDEX_ORG, arydbFilData, INDEX_SHOW, aryfDataShow);

    return 1;
}

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 函数说明：多项式插值算法：linear内插
% 输入参数：xCol     	原始数据索引号，列向量
%           yMat       	原始数据，列向量
%           xiCol       如xCol = 1:1:10，则xiCol = 1:1/1.3:10
%			yi          插值后数据
% 编辑日期：2015/10/26
% 编辑作者：syn
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
int LinearInterp(int xCol[DATALEN_ORG], float yMat[DATALEN_ORG],
                 double xiCol[DATALEN_SHOW], float yi[DATALEN_SHOW]) {
    int i, k;
    double s;

    // 输出初始化
    for (i = 0; i < DATALEN_SHOW; i++) {
        yi[i] = 0;
    }

    for (i = 0; i < DATALEN_SHOW; i++) {
        // syn[2016/2/27] 修改k计算错误
        k = min(max(1 + (int) (xiCol[i] - xCol[0]), 1), DATALEN_ORG - 1) - 1;
        //k = (int)(xiCol[i] - xCol[0]);
        s = xiCol[i] - xCol[k];
        yi[i] = yMat[k] + s * (yMat[k + 1] - yMat[k]);
    }

    return 1;
}
