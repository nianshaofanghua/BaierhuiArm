package com.tsminfo.android.baierhuiarm.helper;

import android.os.Handler;
import android.util.Log;

import com.tsminfo.android.baierhuiarm.application.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketClientHelper {

    private static SocketClientHelper instance = null;
    static Socket server;
    private final static String TAG = "SocketClientHelper";
    private Handler mHandler = new Handler();

    //IP地址
//    private static String HOST = "120.78.92.247";     //测试IP
    private static String HOST = "129.204.198.206";     //正式IP
//    private static String HOST = "192.168.56.1";
    //端口
    private static int PORT = 8686;

    private static int TIME_OUT = 20000; // 超时

    public static boolean SOCKET_CONNECT = false;

//    private BufferedReader in;
    private ReadThread readThread;

    public void sendMsg(String msg)  {

        if (!MyApplication.mNetStatus){
            // 没有网络连接
            closeServer();
            return;
        }

        try{
            initServer();
            PrintWriter out = new PrintWriter(server.getOutputStream());
            BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));

            out.println(msg);
            out.flush();
            mHandler.postDelayed(disConnect,TIME_OUT);
            if (readThread == null || !SOCKET_CONNECT){
//            if (readThread == null){
                Log.i(TAG,"开启readThread");
                readThread = new ReadThread();
                readThread.start();
            }
            else if (!readThread.isAlive()){
                Log.i(TAG,"readThread挂了！重新开启readThread");
                readThread = new ReadThread();
                readThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                while (true) {
                    String response = in.readLine();
                    if(response != null){
//                    System.out.println(response);
                    mHandler.removeCallbacks(disConnect);
                    onSocketDataReceiveListener.onDataReceive(1, response);
                    }
//                    else{
//                        mHandler.post(reConnect);
//                    }
                }
            } catch(Exception e){
                Log.e("SOCKET","收到异常信息");
                    e.printStackTrace();
                    System.out.println(e.getLocalizedMessage());
                    SOCKET_CONNECT = false;
//                    closeServer();
                    onSocketDataReceiveListener.onDataReceive(-1, "");
            }
        }
    }

    private Runnable disConnect = new Runnable() {
        @Override
        public void run() {
            SOCKET_CONNECT = false;
            closeServer();
            onSocketDataReceiveListener.onDataReceive(-1, "");
        }
    };

    private void closeServer(){
        try {
            if (server != null && server.isConnected()){
                Log.i("Socket","断开连接");
                server.close();
                SOCKET_CONNECT = false;
                server = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 监听器监听接收数据
    public SocketClientHelper.OnSocketDataReceiveListener onSocketDataReceiveListener = null;

    public static interface OnSocketDataReceiveListener {
        public void onDataReceive(Integer code, String receiveData);
    }

    public void setOnDataReceiveListener(SocketClientHelper.OnSocketDataReceiveListener dataReceiveListener) {
        onSocketDataReceiveListener = dataReceiveListener;
    }


    public static synchronized SocketClientHelper getInstance() {
        if (instance == null) {
            instance = new SocketClientHelper();
        }
        return instance;
    }


    private static void initServer() throws Exception {
        if (server == null || !SOCKET_CONNECT) {
            server = new Socket(HOST, PORT);
            server.setSoTimeout(0);
        }
    }

    private Runnable reConnect = new Runnable() {
        @Override
        public void run() {
            try {
                if(server != null) {
                    server.close();
                    server = null;
                }
                server = new Socket(HOST, PORT);
                server.setSoTimeout(0);
                readThread = new ReadThread();
                readThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public static void main(String[] args) throws Exception {

//        sendMsg("{\"from\":\"client\",\"cmd\":\"login\",\"dev_no\":\"3030303030303038\",\"ccid\":\"3839383630374238313031373730323133323331\"}");
        // 7f0000010b5700000189{"from":"server","cmd":"login","dev_no":"3030303030303038","res":"success"}

//        sendMsg("{\"from\":\"client\",\"cmd\":\"heartbeat\",\"dev_no\":\"3030303030303038\"}");
        // {"from":"server","cmd":"heartbeat","dev_no":"3030303030303038","res":"success"}

        String str = "7f0000010b5700000189{\"from\":\"server\",\"cmd\":\"login\",\"dev_no\":\"3030303030303038\",\"res\":\"success\"}";
        String[] strArr = str.split("[{]");
        if (strArr.length > 1) {
            str = str.replace(strArr[0], "");
        }
        System.out.println(str);
//        {"from":"server","cmd":"delivery","order_sn":"RE5c779c07c7c2b934215080","dev_no":"3030303030303037","goods_id":"16","num":"1"}
    }
}
