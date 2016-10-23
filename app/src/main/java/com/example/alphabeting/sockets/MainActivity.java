package com.example.alphabeting.sockets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {

    Socket socket;

    DatagramSocket socket2;

    private TextView  Receiver;

    private EditText sendText;

    private Button send,clean_1,clean_2;

    static String ip,patterns;

    static int port_num;

    public Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0x11){
                Bundle bundle = msg.getData();
                if(bundle.getString("tip") == null){
                Receiver.append(bundle.getString("receive")+"\n");}
                else
                    Toast.makeText(MainActivity.this,bundle.getString("tip"), Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendText = (EditText)findViewById(R.id.sendText);
        Receiver = (TextView)findViewById(R.id.receiver);
        Receiver.setMovementMethod(ScrollingMovementMethod.getInstance());
        send = (Button)findViewById(R.id.send);
        clean_1 = (Button)findViewById(R.id.clean_1);
        clean_2 = (Button)findViewById(R.id.clean_2);
        clean_1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                sendText.setText("");
            }
        });
        clean_2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Receiver.setText("");
            }
        });
        send.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String sendtext = sendText.getText().toString();
                Receiver.append("通信模式：" + patterns + "\n");
                Receiver.append("目标IP：" + ip + "\n");
                Receiver.append("目标端口：" + port_num + "\n");
                new MyThread(sendtext).start();
            }

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_setting){
            Intent intent = new Intent(this,SettingActivity.class);
            startActivityForResult(intent,1);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    String ip_adress =  data.getStringExtra("ip_adress");
                    String port = data.getStringExtra("port_num");
                    String pattern = data.getStringExtra("pattern");
                    ip = ip_adress;
                    port_num = Integer.parseInt(port);
                    patterns = pattern;
                }
                break;
            default:
        }
    }
    class MyThread extends Thread{

        private  String text;

        public MyThread(String str){
            text = str;
        }

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            if (patterns.equals("TCP")) {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port_num),10000);
                    OutputStream writer = socket.getOutputStream();
                    writer.write(text.getBytes("UTF-8"));
                    writer.flush();
                    InputStream reader = socket.getInputStream();
                    byte[] buf = new byte[1024*4];
                    int receives = reader.read(buf);
                    String receive = new String(buf,0,receives);
                    bundle.putString("receive",receive);
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                    reader.close();
                    writer.close();
                    socket.close();
                } catch (SocketTimeoutException aa) {
                    bundle.putString("tip", "服务器连接失败！请检查网络是否打开");
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(patterns.equals("UDP")){
                try{
                    if(socket2 == null){
                        socket2 = new DatagramSocket(null);
                        socket2.setReuseAddress(true);
                        socket2.bind(new InetSocketAddress(port_num));
                    }
                    InetAddress serverAddress = InetAddress.getByName(ip);
                    byte output_data[] = text.getBytes();
                    DatagramPacket outputPacket = new DatagramPacket(output_data,output_data.length,serverAddress,port_num);
                    socket2.send(outputPacket);
                    byte input_data[] = new byte[1024*4];
                    DatagramPacket inputPacket = new DatagramPacket(input_data,input_data.length);
                    socket2.receive(inputPacket);
                    String receive = new String(inputPacket.getData(),inputPacket.getOffset(),inputPacket.getLength());
                    bundle.putString("receive",receive);
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                    socket2.close();
                } catch(SocketTimeoutException e){
                   e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }

            }
        }
    }
}

