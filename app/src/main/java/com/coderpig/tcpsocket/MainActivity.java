package com.coderpig.tcpsocket;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etMain,etIp,etSo;
    private Button btnMain,con;
    private TextView tvMain;
    private ClientThread mClientThread;
    //在主线程中定义Handler传入子线程用于更新TextView
    private Handler mHandler,nHandler;
    String ip,po;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initView();
        //显示从服务器获取的信息
        mHandler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    tvMain.append("\n"+"Server:"+ msg.obj.toString());
                }
            }
        };
        //显示与服务器连接的信息
        nHandler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                //连接成功
                if(msg.what==1){
                    progressDialog.dismiss();
                    tvMain.append("\n"+msg.obj.toString());
                }
                //连接失败
                else if (msg.what==0){
                    progressDialog.dismiss();
                    tvMain.append("\n"+msg.obj.toString());
                }
            }
        };
        //连接按钮
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取编辑文本框的ip地址信息
                ip=etIp.getText().toString();
                //端口信息
                po=etSo.getText().toString();
                //判断是否为空
                if(!po.equals("")&&po!=null&&!ip.equals("")) {
                    //进度窗口显示
                    progressDialog.show();
                    //端口转为int类型
                    int port = Integer.valueOf(po);
                    //开始线程
                    mClientThread = new ClientThread(mHandler,nHandler,ip,port);
                    new Thread(mClientThread).start();
                }else {
                    Toast.makeText(MainActivity.this,"请输入ip地址和端口号！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击button时，获取EditText中string并且调用子线程的Handler发送到服务器
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = etMain.getText().toString();
                    tvMain.append("\n"+"Client:"+ msg.obj.toString());
                    mClientThread.revHandler.sendMessage(msg);
                    etMain.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        etMain =  findViewById(R.id.et_main);
        etIp=findViewById(R.id.edit_ip);
        etSo=findViewById(R.id.edit_so);
        btnMain = findViewById(R.id.btn_main);
        tvMain =  findViewById(R.id.tv_main);
        con=findViewById(R.id.con);
        tvMain.setTextColor(Color.BLACK);
        tvMain.setMovementMethod(new ScrollingMovementMethod());
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("连接中。。。");
        progressDialog.setCancelable(false);
    }
}