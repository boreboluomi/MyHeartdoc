// 宏定义
#define    DE_MULT            23        // 心电数据值缩小倍数，以适应APP显示
#define DATALEN_ORG        10        // [2016/1/9] 显示波形延迟时间：10点
#define DATALEN_SHOW    13        // [2016/1/9] 插值后显示长度：DATALEN_ORG*1.3
// [2016/2/27] 最大最小值函数定义
#define max(a, b) ( ((a)>(b)) ? (a):(b) )
#define min(a, b) ( ((a)>(b)) ? (b):(a) )

// 波形处理：滤波，插值
int ProcessWave(int arynData[DATALEN_ORG], float arydbFilData[DATALEN_ORG],
                float aryfDataShow[DATALEN_SHOW], int *nRPos);

// 多项式插值算法：linear内插
int LinearInterp(int xCol[DATALEN_ORG], float yMat[DATALEN_ORG],
                 double xiCol[DATALEN_SHOW], float yi[DATALEN_SHOW]);