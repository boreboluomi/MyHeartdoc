#ifndef ECG_ALGORITHM_H
#define ECG_ALGORITHM_H

/* 
* 复位/初始化函数
* 调用：App内蓝牙模块初始化时 或 连接到蓝牙设备时 或 导联脱落重新连接
*/
void Ecg_Reset(void);

/*
* 数据处理（单个数据）：模式识别、滤波、去噪等
* 调用：App内对收到的每个数据进行处理
* 参数说明：
source ：源数据，监测设备传过来的原始值
* 返回值说明：
处理后数据，用于App上的波形显示，范围 0 - 4095 或 -2048 - +2047 或其他
*/
int Ecg_SingleDataProcess(int source, int *nRPos);

/*
* 获取心率
* 调用：每秒读取一次
* 返回值说明：
当前心率值，范围  -  ，无效值-100，
*/
int Ecg_GetHeartRate(void);


/*  附:更多函数，后续随着业务需要扩展  */

/*
* 数据处理（批量，比如10个数据一起，最大100个）：模式识别、滤波、去噪等
* 调用：App内对收到的每个数据包进行处理
* 参数说明：
source ：源数据数组，监测设备传过来的原始值，单个数据的值范围 0 - 4095
* 返回值说明：
处理后数据数组，用于App上的波形显示，范围 0 - 4095 或 -2048 - 2047
*/
//int[] Ecg_BatchDataProcess(int[] source);

#endif