package com.tsminfo.android.baierhuiarm.utils;

import android.os.SystemClock;

import com.serialportlibrary.service.impl.SerialPortBuilder;
import com.serialportlibrary.service.impl.SerialPortService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * 通过串口接收发送数据
 */
public class SerialPortUtil {

    private static final String TAG = "SerialPortUtil";

    private static String PATH_NAME = "/dev/ttyS4";
    private static int BAUD_RATE = 9600;
    private static Long TIME_OUT = 20000L;


    public static SerialPortService serialPortService = null;


    /**
     * 发送数据
     */
    public byte[] sendData(byte[] sendData) {

//        if (serialPortService != null) {
//
//            serialPortService.close();
//            SystemClock.sleep(500);
//        }

        if(serialPortService == null )
        serialPortService = new SerialPortBuilder()
                .setTimeOut(TIME_OUT)
                .setBaudrate(BAUD_RATE)
                .setDevicePath(PATH_NAME)
                .createService();


        serialPortService.isOutputLog(true);
        byte[] receiveData = serialPortService.sendData(sendData);

//        serialPortService.close();

        return receiveData;
    }


    // 监听器监听接收数据
    public OnDataReceiveListener onDataReceiveListener = null;

    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }


}
