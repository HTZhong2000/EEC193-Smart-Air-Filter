package com.example.iotapp.ui.condition;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.iotapp.R;
import com.example.iotapp.ui.login.LoginFragment;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class ConditionFragment extends Fragment {

    private ConditionViewModel mViewModel;
    private Switch switchfan;
    private String fanstatus;
    private TextView texttempc;
    private TextView texttempf;
    private TextView texthumi;
    private TextView textpm25;
    private TextView textpm10;
    private String stringdata;
    private String str_pm25;
    private String str_pm10;
    private String str_fan;
    private String str_tempc;
    private String str_tempf;
    private String str_humi;
    private TextView texttest;
    private Timer timer;
    private String response;
    private boolean torchbar;
    private boolean fanswi;
    private JSONObject postpack;
    private String fanspeed;
    public static ConditionFragment newInstance() {
        return new ConditionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.condition_fragment, container, false);
    }
    private void settext(){
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        texttempf.setText(str_tempf);
                        texttempc.setText(str_tempc);
                        texthumi.setText(str_humi);
                        textpm10.setText(str_pm10);
                        textpm25.setText(str_pm25);
                        if(torchbar==false){
                            switchfan.setChecked(fanswi);
                        }
                        torchbar=false;
                    }
                });
            }
        });
    }

    private class httptask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void  doInBackground(Void... voids) {
            try {
                URL url= new URL("https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193");
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                int code= connection.getResponseCode();
                if(code==200){
                    InputStream is =connection.getInputStream();
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    int i = is.read();
                    while(i != -1) {
                        bo.write(i);
                        i = is.read();
                    }
                    stringdata= bo.toString();

                    is.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            getContent(stringdata);
            settext();
        }
    }

    /*private void settest(){
        Handler handler=new Handler();
        texttest=(TextView) getActivity().findViewById(R.id.textView10);
        handler.post(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        texttest.setText("");
                    }
                });
            }
        });
    }*/
    private class httppo extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void  doInBackground(Void... voids) {
            try {
                URL url= new URL("https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193_POST");
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/json");
                OutputStream out=connection.getOutputStream();
                String content=String.valueOf(postpack);
                out.write(content.getBytes());
                out.flush();
                out.close();
                int code= connection.getResponseCode();
                if(code==200){
                    InputStream is =connection.getInputStream();
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    int i = is.read();
                    while(i != -1) {
                        bo.write(i);
                        i = is.read();
                    }
                    response= bo.toString();
                    is.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

        }
    }

    private void getContent(String inputstring){
        int start=inputstring.indexOf("body")+7;
        int end=inputstring.indexOf("}")+1;
        String jsonstr=inputstring.substring(start,end);
        try {
            JSONObject json=new JSONObject(jsonstr);
            str_pm25=json.getString("PM25");
            str_pm10=json.getString("PM10");
            str_fan=json.getString("Fan");
            if(str_fan.equals("1")){
                fanswi=true;
            }else{
                fanswi=false;
            }
            str_tempc=json.getString("TemperatureC");
            str_tempf=json.getString("TemperatureF");
            str_humi=json.getString("Humidity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*int index_pm25=inputstring.indexOf("PM25")+8;
        int end_pm25=inputstring.indexOf(",",index_pm25)-1;
        str_pm25=inputstring.substring(index_pm25,end_pm25);
        int index_fan=inputstring.indexOf("Fan")+7;
        int end_fan=inputstring.indexOf(",",index_fan)-1;
        str_fan=inputstring.substring(index_fan,end_fan);
        int index_pm10=inputstring.indexOf("PM10")+8;
        int end_pm10=inputstring.indexOf("}",index_pm10)-1;
        str_pm10=inputstring.substring(index_pm10,end_pm10);
        int index_tempc=inputstring.indexOf("TemperatureC")+20;
        int end_tempc=inputstring.indexOf(",",index_tempc)-1;
        str_tempc=inputstring.substring(index_tempc,end_tempc);
        int index_tempf=inputstring.indexOf("TemperatureF")+20;
        int end_tempf=inputstring.indexOf(",",index_tempf)-1;
        str_tempf=inputstring.substring(index_tempf,end_tempf);
        int index_humi=inputstring.indexOf("Humidity")+12;
        int end_humi=inputstring.indexOf(",",index_humi)-1;
        str_humi=inputstring.substring(index_humi,end_humi);*/
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ConditionViewModel.class);
        // TODO: Use the ViewModel

    }

    public void onResume () {
        super.onResume();
        SharedPreferences readdata =getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        Boolean iflog=readdata.getBoolean("iflog",false);
        timer= new Timer();
        if(iflog==false){
            Navigation.findNavController(this.getView()).navigate(R.id.nav_log);
        }else {
            texttempc = (TextView) getActivity().findViewById(R.id.texttempc);
            texttempf = (TextView) getActivity().findViewById(R.id.texttempf);
            texthumi = (TextView) getActivity().findViewById(R.id.texthumi);
            textpm25 = (TextView) getActivity().findViewById(R.id.textpm25);
            textpm10 = (TextView) getActivity().findViewById(R.id.textpm10);
            switchfan = (Switch) getActivity().findViewById(R.id.switch2);
            switchfan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (switchfan.isChecked() == true) {
                        fanstatus = "1";
                    } else if (switchfan.isChecked() == false) {
                        fanstatus = "0";
                    }
                    postpack = new JSONObject();
                    try {
                        postpack.put("Fan", fanstatus);
                        postpack.put("Device", "test");
                        postpack.put("tmpF", "0");
                        postpack.put("Client", "Web");
                        postpack.put("Timestamp", "200001112");
                        postpack.put("tmpC", "0");
                        postpack.put("humid", "0");
                        postpack.put("PM25", "0");
                        postpack.put("PM10", "0");
                        postpack.put("PIR", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new httppo().execute();
                    torchbar = true;

                }
            });

            TimerTask conhttp = new TimerTask() {
                @Override
                public void run() {
                    new httptask().execute();
                }
            };
            timer.schedule(conhttp, 0, 1000);
        }
    }
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

}