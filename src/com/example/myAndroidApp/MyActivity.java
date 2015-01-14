package com.example.myAndroidApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MyActivity extends Activity {

    EditText  inputIP;
    TextView show;
    TextView showDevice;
    TextView showTime;
    TextView showLatitude;
    TextView showLongitude;
    TextView showAcceleration;
    TextView showHeartbeat;

    String ip =null ;
    boolean flag = true;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                String textFrom = msg.obj.toString();
                String device = textFrom.substring(8,16);
                String time = textFrom.substring(16,18)+"点"+textFrom.substring(18,20)+"分"+textFrom.substring(20,22)+"秒";
                String latitude = textFrom.substring(22,24)+"度"+textFrom.substring(24,26)+"."+textFrom.substring(27,32)+"分";
                String longgitude = textFrom.substring(32,35)+"度"+textFrom.substring(35,37)+"."+textFrom.substring(38,44)+"分";
                String acceleration = textFrom.substring(44, 50);
                String heartbeat =String.valueOf( Integer.parseInt(textFrom.substring(50,52),16));

                show.setText(textFrom);
                showDevice.setText(device);
                showTime.setText(time);
                showLatitude.setText(latitude);
                showLongitude.setText(longgitude);
                showAcceleration.setText(acceleration);
                showHeartbeat.setText(heartbeat);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        inputIP = (EditText)findViewById(R.id.InputIP);

        inputIP.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.'};
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_PHONE;
            }
        });

        show = (TextView) findViewById(R.id.Show);
        showDevice = (TextView) findViewById(R.id.ShowDevice);
        showTime = (TextView) findViewById(R.id.ShowTime);
        showLatitude = (TextView) findViewById(R.id.ShowLatitude);
        showLongitude = (TextView) findViewById(R.id.ShowLongitude);
        showAcceleration = (TextView) findViewById(R.id.ShowAcceleration);
        showHeartbeat = (TextView) findViewById(R.id.ShowHeartbeat);
        
    }

    public void Connection(View v)
    {
        ip=inputIP.getText().toString();
        flag = true;

        Thread connection = new Thread();
        connection.start();

        new Thread() {
            public void run() {
                InputStream is;
                try {
                    Socket socket = new Socket( ip , 8899);
                    is= socket.getInputStream();
                    byte []b=new byte[60];
                    int n;
                    while(((n=is.read(b))>=0 ) && flag )
                    {
                        Message message = Message.obtain();
                        message.arg1=1;
                        message.obj = new String(b,0,n);
                        handler.sendMessage(message);
                    }
                    is.close();
                    socket.close();
                } catch(UnknownHostException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void Disconnect(View v)
    {
        flag = false;
    }

}