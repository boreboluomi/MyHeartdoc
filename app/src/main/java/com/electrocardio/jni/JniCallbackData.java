package com.electrocardio.jni;

/**
 * Created by yangzheng on 2016/2/14.
 */
public class JniCallbackData {
    static {
        System.loadLibrary("JniAlgorithm");
    }

    public native float[] getFloatFromJni(int[] array);

    /**
     * 获取心率
     *
     * @return
     */
    public native int getHeartRate();

    /**
     * 对算法进行初始化
     */
    public native void getInitFromJni();

    public native OutObjectResultPacket getAlgorithmFrom(int[] array);

    /**
     * 解析ECG波形
     *
     * @param ecgWave
     * @return
     */
    public native ECGResultBean AnalysisECGWave(int[] ecgWave, int bLeadOff);

    /**
     * 连续测量完成后获取测量结果
     *
     * @return
     */
    public native CompleteECGResultBean EcgGetStatisitic();

    /**
     * 获取R波信息
     *
     * @return
     */
    public native RealBeatVal EcgGetRealBeatVal();

}
