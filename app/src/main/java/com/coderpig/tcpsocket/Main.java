package com.coderpig.tcpsocket;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2020/7/21 11:41.
 **/
public class Main extends AppCompatActivity {
    private EditText etMain,etIp,etSo;
    private Button btnMain,con;
    private TextView tvMain;
    String ip,po;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMain = findViewById(R.id.et_main);
        etIp = findViewById(R.id.edit_ip);
        etSo = findViewById(R.id.edit_so);
        btnMain = findViewById(R.id.btn_main);
        tvMain = findViewById(R.id.tv_main);
        con = findViewById(R.id.con);
        tvMain.setTextColor(Color.BLACK);
        tvMain.setMovementMethod(new ScrollingMovementMethod());
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip=etIp.getText().toString();
                po=etSo.getText().toString();
                if(po!="") {
                    final int port = Integer.valueOf(po);
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                Socket socket= null;
                                try {
                                    socket = new Socket(ip,port);
                                    InputStream is=socket.getInputStream();
                                    OutputStream os=socket.getOutputStream();
                                    BufferedReader br=new BufferedReader(new InputStreamReader(is));
                                    while (true){
                                        String s=br.readLine();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                               // tvMain.append("\n"+"Server:"+ s);
                                            }
                                        });

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                    // tvMain.append("\n"+"已连接");
                }
            }
        });
    }
}
