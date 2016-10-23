package com.example.alphabeting.sockets;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;

/**
 * Created by alphabeting on 2016/10/1.
 */

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private Toolbar mToolbar;

    private SelectWindow selectWindow;

    private EditText ip_1,ip_2,ip_3,ip_4,port;

    static boolean dataStore = true;

    static String patterns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        initToolbar();
        final TableLayout pattern = (TableLayout) this.findViewById(R.id.pattern);
        final TableLayout about = (TableLayout) this.findViewById(R.id.about);
        ip_1 = (EditText)findViewById(R.id.ip_1);
        ip_2 = (EditText)findViewById(R.id.ip_2);
        ip_3 = (EditText)findViewById(R.id.ip_3);
        ip_4 = (EditText)findViewById(R.id.ip_4);
        port = (EditText)findViewById(R.id.port);
        pattern.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectWindow = new SelectWindow(SettingActivity.this,itemsOnClick);
                selectWindow.showAtLocation(SettingActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        about.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final AlertDialog.Builder aboutDialog = new AlertDialog.Builder(SettingActivity.this);
                aboutDialog.setIcon(R.drawable.ic_launcher);
                aboutDialog.setTitle(R.string.app_name);
                aboutDialog.setMessage("版本:1.0(Pikachu)\n" +
                                       "反馈:微博@AlphaBeting\n\n\n\n\n\n\n" +
                                       " （づ￣3￣）づ╭❤～向开源致敬(ーー゛)\n"+
                                       "      Copyright© 2016 By AlphaBeting");
                aboutDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                    }
                });
                aboutDialog.show();
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String ip1 = ip_1.getText().toString();
                String ip2 = ip_2.getText().toString();
                String ip3 = ip_3.getText().toString();
                String ip4 = ip_4.getText().toString();
                String ip = ip1+"."+ip2+"."+ip3+"."+ip4;
                String ports = port.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("ip_adress",ip);
                intent.putExtra("port_num",ports);
                intent.putExtra("pattern",patterns);
                setResult(RESULT_OK,intent);

                editor = pref.edit();
                editor.putString("ip1",ip1);
                editor.putString("ip2",ip2);
                editor.putString("ip3",ip3);
                editor.putString("ip4",ip4);
                editor.putString("ports",ports);
                editor.putString("patterns",patterns);
                editor.commit();
                finish();
                dataStore = true;

                onBackPressed();
            }
        });

        if(dataStore){
            String ip11 = pref.getString("ip1","");
            String ip22 = pref.getString("ip2","");
            String ip33 = pref.getString("ip3","");
            String ip44 = pref.getString("ip4","");
            String portss = pref.getString("ports","");
            ip_1.setText(ip11);
            ip_2.setText(ip22);
            ip_3.setText(ip33);
            ip_4.setText(ip44);
            port.setText(portss);
        }
    }

    @Override
    public void onBackPressed(){

        ip_1 = (EditText)findViewById(R.id.ip_1);
        ip_2 = (EditText)findViewById(R.id.ip_2);
        ip_3 = (EditText)findViewById(R.id.ip_3);
        ip_4 = (EditText)findViewById(R.id.ip_4);
        port = (EditText)findViewById(R.id.port);
        String ip1 = ip_1.getText().toString();
        String ip2 = ip_2.getText().toString();
        String ip3 = ip_3.getText().toString();
        String ip4 = ip_4.getText().toString();
        String ip = ip1+"."+ip2+"."+ip3+"."+ip4;
        String ports = port.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("ip_adress",ip);
        intent.putExtra("port_num",ports);
        intent.putExtra("pattern",patterns);
        setResult(RESULT_OK,intent);

        editor = pref.edit();
        editor.putString("ip1",ip1);
        editor.putString("ip2",ip2);
        editor.putString("ip3",ip3);
        editor.putString("ip4",ip4);
        editor.putString("ports",ports);
        editor.putString("patterns",patterns);
        editor.commit();
        finish();
        dataStore = true;

    }

    private void initToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("R.string.setting");
    }

    private  View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public  void  onClick(View v){
            selectWindow.dismiss();
            switch(v.getId()){
                case R.id.UDP:
                    patterns = "UDP";
                    break;
                case R.id.TCP:
                    patterns = "TCP";
                    break;
                case R.id.cancel:
                    selectWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    };




}
