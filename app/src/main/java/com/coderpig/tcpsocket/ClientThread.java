package com.coderpig.tcpsocket;


import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientThread implements Runnable {
    private Socket mSocket;
    private String mIp;
    private int mPort;
    private BufferedReader mBufferedReader = null;
    private OutputStream mOutputStream = null;
    private Handler mHandler;
    private Handler nHandler;
    public Handler revHandler;

    public ClientThread(Handler handler,Handler hh,String ip,int port) {
        mHandler = handler;
        nHandler=hh;
        mIp=ip;
        mPort=port;
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket();
            //与服务器连接，超时时间为5s
            mSocket.connect(new InetSocketAddress(mIp,mPort),5000);

            Message msg1=new Message();
            msg1.what=1;
            msg1.obj="连接成功！";
            nHandler.sendMessage(msg1);
            //获取服务端输入流，放到缓存区中
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            //获取客户端输出流
            mOutputStream = mSocket.getOutputStream();

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        String content = null;
                        //读取服务器传来的信息
                        while ((content = mBufferedReader.readLine()) != null) {
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = content;
                            mHandler.sendMessage(msg);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }.start();
            //由于子线程中没有默认初始化Looper，要在子线程中创建Handler，需要自己写
            Looper.prepare();
            revHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        try {
                            mOutputStream.write((msg.obj.toString() + "\r\n").getBytes("utf-8"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();
        } catch (IOException e) {
            e.printStackTrace();
            Message msg1=new Message();
            msg1.what=0;
            msg1.obj="连接失败！";
            nHandler.sendMessage(msg1);
        }
    }
}