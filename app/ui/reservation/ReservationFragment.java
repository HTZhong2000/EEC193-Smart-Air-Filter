package com.example.iotapp.ui.reservation;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.iotapp.R;
import com.example.iotapp.ui.records.RecordsFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ReservationFragment extends Fragment {

    private ReservationViewModel mViewModel;
    public static ReservationFragment newInstance() {
        return new ReservationFragment();
    }
    private JSONObject postpack;
    private String response;
    private String DeviceID;
    private String strget;
    private Switch switch3;
    private Timer timer;
    private CheckBox threshcheck;
    private CheckBox schedulecheck;
    private Switch planswitch;
    private EditText setthresh10;
    private EditText setthresh25;
    private EditText startt;
    private EditText endt;
    private Button savebut;
    private boolean stateall;
    private boolean ifthresh;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.reservation_fragment, container, false);
    }
    private class httppo extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void  doInBackground(Void... voids) {
            try {
                URL url= new URL("https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193_SCHEDULE");
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                String content=String.valueOf(postpack);
                OutputStream out=connection.getOutputStream();

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

    private class httpgetplan extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void  doInBackground(Void... voids) {
            try {
                URL url= new URL("https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193_Getplan");
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
                    strget= bo.toString();
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
            int start=strget.indexOf("body")+7;
            int end=strget.indexOf("}")+1;
            String jsonstr=strget.substring(start,end);
            try {
                JSONObject json=new JSONObject(jsonstr);
                String thresh10=json.getString("Threshold_pm10");
                String thresh25=json.getString("Threshold_pm25");
                String starttime=json.getString("Start_time");
                String endtime=json.getString("End_time");
                int state=Integer.parseInt(json.getString("Statec"));
                String typeplan=json.getString("Typec");
                if(state==0){
                    planswitch.setChecked(false);
                    stateall=false;
                    threshcheck.setEnabled(false);
                    schedulecheck.setEnabled(false);
                    setthresh10.setEnabled(false);
                    setthresh25.setEnabled(false);
                    startt.setEnabled(false);
                    endt.setEnabled(false);
                    if(typeplan.equals("thresh")){
                        schedulecheck.setChecked(false);
                        threshcheck.setChecked(true);
                        setthresh10.setText(thresh10);
                        setthresh25.setText(thresh25);
                        ifthresh=true;

                    }else if(typeplan.equals("schedule")){
                        schedulecheck.setChecked(true);
                        threshcheck.setChecked(false);
                        startt.setText(starttime);
                        endt.setText(endtime);
                        ifthresh=false;
                    }
                }
                if(state==1){
                    planswitch.setChecked(true);
                    stateall=true;
                    threshcheck.setEnabled(true);
                    schedulecheck.setEnabled(true);
                    if(typeplan.equals("thresh")){
                        setthresh10.setEnabled(true);
                        setthresh25.setEnabled(true);
                        startt.setEnabled(false);
                        endt.setEnabled(false);
                        schedulecheck.setChecked(true);
                        threshcheck.setChecked(false);
                        setthresh10.setText(thresh10);
                        setthresh25.setText(thresh25);
                        ifthresh=true;

                    }else if(typeplan.equals("schedule")){
                        setthresh10.setEnabled(false);
                        setthresh25.setEnabled(false);
                        startt.setEnabled(true);
                        endt.setEnabled(true);
                        schedulecheck.setChecked(false);
                        threshcheck.setChecked(true);
                        startt.setText(starttime);
                        endt.setText(endtime);
                        ifthresh=false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    public void onResume () {
        super.onResume();
        SharedPreferences readdata = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        Boolean iflog = readdata.getBoolean("iflog", false);
        if (iflog == false) {
            Navigation.findNavController(this.getView()).navigate(R.id.nav_log);
        } else {
            threshcheck = (CheckBox) getActivity().findViewById(R.id.checkBox);
            schedulecheck = (CheckBox) getActivity().findViewById(R.id.checkBox2);
            planswitch = (Switch) getActivity().findViewById(R.id.switch1);
            setthresh10 = (EditText) getActivity().findViewById(R.id.editTextNumberDecimal);
            setthresh25 = (EditText) getActivity().findViewById(R.id.editTextNumberDecimal2);
            startt = (EditText) getActivity().findViewById(R.id.editTextNumberDecimal3);
            endt = (EditText) getActivity().findViewById(R.id.editTextNumberDecimal4);
            savebut = (Button) getActivity().findViewById(R.id.button);
            TextView warning = (TextView) getActivity().findViewById((R.id.textView25));
            new httpgetplan().execute();
            planswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        stateall = true;
                        threshcheck.setEnabled(true);
                        schedulecheck.setEnabled(true);
                        if (schedulecheck.isChecked() == true) {
                            setthresh10.setEnabled(false);
                            setthresh25.setEnabled(false);
                            startt.setEnabled(true);
                            endt.setEnabled(true);
                            ifthresh = false;

                        } else if (threshcheck.isChecked() == true) {
                            setthresh10.setEnabled(true);
                            setthresh25.setEnabled(true);
                            startt.setEnabled(false);
                            endt.setEnabled(false);
                            ifthresh = true;
                        }
                    } else if (b == false) {
                        stateall = false;
                        threshcheck.setEnabled(false);
                        schedulecheck.setEnabled(false);
                        setthresh10.setEnabled(false);
                        setthresh25.setEnabled(false);
                        startt.setEnabled(false);
                        endt.setEnabled(false);
                    }
                }
            });
            threshcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        setthresh10.setEnabled(true);
                        setthresh25.setEnabled(true);
                        startt.setEnabled(false);
                        endt.setEnabled(false);
                        ifthresh = true;
                        schedulecheck.setChecked(false);
                    } else if (b == false) {
                        setthresh10.setEnabled(false);
                        setthresh25.setEnabled(false);
                        startt.setEnabled(true);
                        endt.setEnabled(true);
                        ifthresh = false;
                        schedulecheck.setChecked(true);
                    }
                }
            });
            schedulecheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == false) {
                        setthresh10.setEnabled(true);
                        setthresh25.setEnabled(true);
                        startt.setEnabled(false);
                        endt.setEnabled(false);
                        ifthresh = true;
                        threshcheck.setChecked(true);
                    } else if (b == true) {
                        setthresh10.setEnabled(false);
                        setthresh25.setEnabled(false);
                        startt.setEnabled(true);
                        endt.setEnabled(true);
                        ifthresh = false;
                        threshcheck.setChecked(false);
                    }
                }
            });
            savebut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String thresh10 = setthresh10.getText().toString();
                    String thresh25 = setthresh25.getText().toString();
                    String starttime = startt.getText().toString();
                    String endtime = endt.getText().toString();

                    String type1;
                    String statestr;
                    if(ifthresh==true){
                        type1="thresh";
                        starttime="0";
                        endtime="0";
                    }else{
                        type1="schedule";
                        thresh10="0";
                        thresh25="0";
                    }
                    if(stateall==true){
                        statestr="1";
                    }else{
                        statestr="0";
                    }
                    int startint=Integer.parseInt(starttime);
                    int endint=Integer.parseInt(endtime);
                    if (startint > endint || startint > 24 || endint > 24) {
                        warning.setText("Invalid Input, Please set time between 0-24");
                    } else {
                        postpack = new JSONObject();
                        try {
                            postpack.put("State", statestr);
                            postpack.put("Device", "test");
                            postpack.put("Type", type1);
                            postpack.put("Threshold_pm10", thresh10);
                            postpack.put("Threshold_pm25", thresh25);
                            postpack.put("Start_time", starttime);
                            postpack.put("End_time", endtime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new httppo().execute();
                        warning.setText("Plan Saved");
                    }
                }
            });
        }
    }

}