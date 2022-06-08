package com.example.iotapp.ui.records;

import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.StrictMode;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.iotapp.Main2Activity;
import com.example.iotapp.R;
import com.example.iotapp.ui.condition.ConditionFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class RecordsFragment extends Fragment {

    private RecordsViewModel mViewModel;
    private JSONObject postpack;
    private String response;
    private String DeviceID;
    private String str_realtimedata;
    private Switch switch3;
    private Timer timer;
    private LineChart chart1;
    private LineChart chart2;
    private ArrayList responselist;
    private List<Entry> rlist1;
    private List<Entry> rlist2;
    private List<Entry> rlist3;
    private List<Entry> rlist4;
    private boolean ifplot;
    public static RecordsFragment newInstance() {
        return new RecordsFragment();
    }
    /*private class httptask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void  doInBackground(Void... voids) {
            try {
                URL url= new URL("https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193_GETDEVICE");
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
                    DeviceID= bo.toString();
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
            SetSpinnerEntries(DeviceID);
        }
    }
    private void SetSpinnerEntries(String pack){
        ArrayList<String> strlist=new ArrayList<String>();
        String tempstr;
        int front=0;
        int end=pack.indexOf("[")+1;
        while (true) {
            front = pack.indexOf(":", end)+3;
            if(front==-1)
                break;
            end = pack.indexOf("}", front)-1;
            tempstr=pack.substring(front,end);
            strlist.add(tempstr);
        }

    }*/
    private class httpgetrecordrealtime extends AsyncTask<Void,Void,Void> {
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
                    str_realtimedata= bo.toString();
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
            int start=str_realtimedata.indexOf("body")+7;
            int end=str_realtimedata.indexOf("}")+1;
            if(rlist1.size()>50){
                rlist1.remove(0);
                rlist2.remove(0);
                rlist3.remove(0);
                rlist4.remove(0);
            }
            String jsonstr=str_realtimedata.substring(start,end);
            try {
                JSONObject json=new JSONObject(jsonstr);
                float pm25=Float.parseFloat(json.getString("PM25"));
                float pm10=Float.parseFloat(json.getString("PM10"));
                float humid=Float.parseFloat(json.getString("Humidity"));
                float tmp=Float.parseFloat(json.getString("TemperatureC"));
                int tsl=Integer.parseInt(json.getString("Current_time"))-1650000000;
                float ts=tsl;
                rlist1.add(new Entry(ts,pm25));
                rlist2.add(new Entry(ts,pm10));
                rlist3.add(new Entry(ts,humid));
                rlist4.add(new Entry(ts,tmp));
            } catch (JSONException e) {
                e.printStackTrace();

            }
            if(ifplot==true) {
                XAxis x1 = chart1.getXAxis();
                XAxis x2 = chart2.getXAxis();
                LineDataSet dataset1 = new LineDataSet(rlist1, "PM2.5");
                LineDataSet dataset2 = new LineDataSet(rlist2, "PM10");
                dataset1.setColor(Color.BLUE);
                dataset2.setColor(Color.RED);
                x1.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        long ts1 = (long) value+1650000000;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
                        String timestr = LocalDateTime.ofInstant(Instant.ofEpochSecond(ts1), TimeZone.getDefault().toZoneId()).format(formatter);
                        return timestr;
                    }
                });
                x1.setLabelCount(5, false);
                List<ILineDataSet> sets1 = new ArrayList<>();
                sets1.add(dataset1);
                sets1.add(dataset2);
                LineData lineData1 = new LineData(sets1);
                chart1.setData(lineData1);
                chart1.invalidate();

                LineDataSet dataset3 = new LineDataSet(rlist3, "Humidity");
                LineDataSet dataset4 = new LineDataSet(rlist4, "Temperature");
                dataset3.setColor(Color.GRAY);
                dataset4.setColor(Color.CYAN);
                x2.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        long ts1 = (long) value;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
                        String timestr = LocalDateTime.ofInstant(Instant.ofEpochSecond((long) value), TimeZone.getDefault().toZoneId()).format(formatter);
                        return timestr;
                    }
                });
                x1.setLabelCount(5, false);
                List<ILineDataSet> sets2 = new ArrayList<>();
                sets2.add(dataset3);
                sets2.add(dataset4);
                LineData lineData2 = new LineData(sets2);
                chart2.setData(lineData2);
                chart2.invalidate();
            }
        }
    }
    private class httppo extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void  doInBackground(Void... voids) {
            try {
                URL url= new URL("https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193_GETREC");
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
            responselist=String2List(response);
            plotchart(responselist);
        }
    }
    private ArrayList String2List(String pack){
        ArrayList<String> strlist=new ArrayList<String>();
        String tempstr;
        int front=0;
        int end=pack.indexOf("[")+1;
        while (true) {
            front = pack.indexOf("{", end);
            if(front==-1)
                break;
            end = pack.indexOf("}", front)+1;
            tempstr=pack.substring(front,end);
            strlist.add(tempstr);
        }
        return strlist;
    }

    private void plotchart(ArrayList<String> inputlist){
        int listsize= inputlist.size();

        List<Entry> list1=new ArrayList<>();
        List<Entry> list2=new ArrayList<>();
        List<Entry> list3=new ArrayList<>();
        List<Entry> list4=new ArrayList<>();
        XAxis x1=chart1.getXAxis();
        XAxis x2=chart2.getXAxis();
        for (int i=listsize-1;i>=0;i--){
            String strentry=inputlist.get(i);

            try {
                JSONObject json=new JSONObject(strentry);
                float pm25=Float.parseFloat(json.getString("PM25"));
                float pm10=Float.parseFloat(json.getString("PM10"));
                float humid=Float.parseFloat(json.getString("humid"));
                float tmp=Float.parseFloat(json.getString("tmpc"));
                int tsl=Integer.parseInt(json.getString("UpdateTime"));
                float ts=tsl;
                list1.add(new Entry(ts,pm25));
                list2.add(new Entry(ts,pm10));
                list3.add(new Entry(ts,humid));
                list4.add(new Entry(ts,tmp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LineDataSet dataset1=new LineDataSet(list1,"PM2.5");
        LineDataSet dataset2=new LineDataSet(list2,"PM10");
        dataset1.setColor(Color.BLUE);
        dataset2.setColor(Color.RED);
        x1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long ts1= (long) value;
                DateTimeFormatter formatter=DateTimeFormatter.ofPattern("MM-dd HH:mm");
                String timestr=LocalDateTime.ofInstant(Instant.ofEpochSecond((long) value), TimeZone.getDefault().toZoneId()).format(formatter);
                return timestr;
            }
        });
        x1.setLabelCount(5,false);
        List<ILineDataSet> sets1=new ArrayList<>();
        sets1.add(dataset1);
        sets1.add(dataset2);
        LineData lineData1=new LineData(sets1);
        chart1.setData(lineData1);
        chart1.invalidate();

        LineDataSet dataset3=new LineDataSet(list3,"Humidity");
        LineDataSet dataset4=new LineDataSet(list4,"Temperature");
        dataset3.setColor(Color.GRAY);
        dataset4.setColor(Color.CYAN);
        x2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long ts1= (long) value;
                DateTimeFormatter formatter=DateTimeFormatter.ofPattern("MM-dd HH:mm");
                String timestr=LocalDateTime.ofInstant(Instant.ofEpochSecond((long) value), TimeZone.getDefault().toZoneId()).format(formatter);
                return timestr;
            }
        });
        x1.setLabelCount(5,false);
        List<ILineDataSet> sets2=new ArrayList<>();
        sets2.add(dataset3);
        sets2.add(dataset4);
        LineData lineData2=new LineData(sets2);
        chart2.setData(lineData2);
        chart2.invalidate();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.records_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        // TODO: Use the ViewModel
    }
    public void onResume() {
        super.onResume();
        SharedPreferences readdata =getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        Boolean iflog=readdata.getBoolean("iflog",false);
        timer= new Timer();
        if(iflog==false){
            Navigation.findNavController(this.getView()).navigate(R.id.nav_log);
        }else {
            chart1 =(LineChart) getActivity().findViewById(R.id.chart1);
            chart2 =(LineChart) getActivity().findViewById(R.id.chart2);
            rlist1=new ArrayList<>();
            rlist2=new ArrayList<>();
            rlist3=new ArrayList<>();
            rlist4=new ArrayList<>();
            postpack = new JSONObject();
            try {
                postpack.put("Ttype", "one");
                postpack.put("Device", "test");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new httppo().execute();


            TimerTask conhttp =new TimerTask() {
                @Override
                public void run() {
                    new httpgetrecordrealtime().execute();
                }
            };
            timer.schedule(conhttp,0,4000);
            switch3=(Switch) getActivity().findViewById(R.id.switch3);
            switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(b==true){
                        ifplot=true;
                    }else if(b==false){
                        ifplot=false;
                        new httppo().execute();
                    }


                }
            });
        }
    }
    public void onStop() {
        super.onStop();
        timer.cancel();
    }
}