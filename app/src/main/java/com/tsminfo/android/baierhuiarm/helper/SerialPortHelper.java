package com.tsminfo.android.baierhuiarm.helper;

import android.util.Log;

import com.tsminfo.android.baierhuiarm.utils.DataUtils;

public class SerialPortHelper {


    /**
     * 取锁控板基本参数和类型
     *
     * @return
     */
    public static byte[] searchParams() {

        byte[] data = {0x55, 0x04, (byte) 0x80};

        return sendData(data, 4);
    }

    /**
     * 出彩票
     *
     * @param num 张数
     * @return
     */
    public static byte[] outLotteryTicket(Integer num) {

        byte[] data = {0x55, 0x05, (byte) 0x81, num.byteValue()};

        return sendData(data, 5);
    }


    /**
     * 出纸巾
     *
     * @param num 纸巾包数
     * @return
     */
    public static byte[] outPaper(Integer num) {
        byte[] data = {0x55, 0x05, (byte) 0x82, num.byteValue()};

        return sendData(data, 5);
    }


    /**
     * USB充电控制
     *
     * @param no  USB接口号
     * @param num 充电时间 单位: 分钟
     * @return
     */
    public static byte[] usbCharging(Integer no, Integer num) {
        byte[] data = {0x55, 0x07, (byte) 0x83, no.byteValue(), 0, 0};

        byte lower = num.byteValue();
        Integer n = num >> 8;
        byte higher = n.byteValue();

        data[4] = higher;
        data[5] = lower;

        return sendData(data, 7);
    }


    /**
     * 控制电磁锁开门
     *
     * @return
     */
    public static byte[] lockOpen() {

        byte[] data = {0x55, 0x04, (byte) 0x84};

        return sendData(data, 4);
    }


    /**
     * 控制照明
     *
     * @param status 00:关闭照明; 01:打开照明
     * @return
     */
    public static byte[] light(Integer status) {
        byte[] data = {0x55, 0x05, (byte) 0x85, status.byteValue()};

        return sendData(data, 5);
    }

    /**
     * 查询是否缺彩票
     *
     * @return
     */
    public static byte[] searchLackTicket() {
        byte[] data = {0x55, 0x04, (byte) 0x86};

        return sendData(data, 4);
    }


    /**
     * 查询是否缺纸巾
     *
     * @return
     */
    public static byte[] searchLackPaper() {
        byte[] data = {0x55, 0x04, (byte) 0x87};

        return sendData(data, 4);
    }


    /**
     * 设置彩票类型
     *
     * @param inch 彩票尺寸
     * @return
     */
    public static byte[] setTicketType(int inch) {
        String inchStr = "0119";
        switch (inch) {
            case 2:
                inchStr = "0033";
                break;
            case 4:
                inchStr = "0066";
                break;
            case 8:
                inchStr = "014B";
                break;
            case 10:
                inchStr = "017F";
                break;
            default:
                inchStr = "0119";
                break;
        }

        byte[] inchByte = DataUtils.hexToByteArr(inchStr);

        byte[] data = {0x55, 0x06, (byte) 0x88, inchByte[0], inchByte[1]};

//        return sendData(data, 7);
        return sendData(data, 6);
    }


    /**
     * 发送数据
     *
     * @param data
     * @param length
     * @return
     */
    public static byte[] sendData(byte[] data, int length) {

        byte[] response = new byte[length];

        byte[] crc = {crc(data)};

        System.arraycopy(data, 0, response, 0, data.length);
        System.arraycopy(crc, 0, response, data.length, crc.length);

        return response;
    }


    /**
     * 校验
     *
     * @param b
     * @return
     */
    public static byte crc(byte[] b) {
        byte crc = b[0];
        for (int i = 1; i < b.length; i++) {
            crc ^= b[i];
        }
        return crc;
    }



    public static void main(String[] args) {

        byte[] test = {0x55, 0x04, (byte) 0x80, (byte) 0xD1};
        byte a = (byte) 0xd5;

        byte[] sendData =  lockOpen();

        Log.i("TAG", "----");

    }

}
