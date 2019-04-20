package com.tsminfo.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class WegoSocketClient {

    static Socket server;

    public static void main(String[] args) throws Exception {

        server = new Socket("120.78.92.247", 8686);
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

        PrintWriter out = new PrintWriter(server.getOutputStream());
        BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            String str = "{\"from\":\"client\",\"cmd\":\"login\",\"dev_no\":\"3030303030303038\",\"ccid\":\"3839383630374238313031373730323133323331\"}";
            out.println(str);
            out.flush();

            if (str.equals("end")) {
                break;
            }

            System.out.println(in.readLine());
        }

        server.close();
    }
}
