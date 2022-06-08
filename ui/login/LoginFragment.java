package com.example.iotapp.ui.login;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.iotapp.Main2Activity;
import com.example.iotapp.R;
import com.example.iotapp.ui.condition.ConditionFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

public class LoginFragment extends Fragment {
    private Button signbutton;
    private Button logbutton;
    private LoginViewModel mViewModel;
    private EditText username;
    private EditText password;
    private String response;
    private String ip;
    private TextView texttest;
    private JSONObject postpack;
    private String userstr;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }
    private class getip extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://api.ipify.org/?format=string");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int code = connection.getResponseCode();

                if (code == 200) {
                    InputStream is = connection.getInputStream();
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    int i = is.read();
                    while (i != -1) {
                        bo.write(i);
                        i = is.read();
                    }
                    ip = bo.toString();

                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void setloginfo(String inputstr){
        Handler handler=new Handler();
        texttest=(TextView) getActivity().findViewById(R.id.textView9);
        handler.post(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        texttest.setText(inputstr);
                    }
                });
            }
        });
    }
    private class httppost extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void  doInBackground(Void... voids) {
            try {
                URL url= new URL("https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193_LOGIN");
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
            int index1=response.indexOf("body")+8;
            int index2=response.indexOf("}")-1;
            String information = response.substring(index1,index2);
            if(information.equals("NOT_EXIST")){
                setloginfo("User Account Doesn't Exist");
            }
            if(information.equals("WRONG_PASSWORD")){
                setloginfo("Wrong Password, Please Try Again");
            }
            if(information.equals("SUCCESS")){
                SharedPreferences savedata= getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=savedata.edit();
                editor.putString("username",userstr);
                editor.commit();

                Navigation.findNavController(getView()).navigate(R.id.nav_veri);

            }


        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel

    }
    public void onResume () {
        super.onResume();
        int time =(int) (System.currentTimeMillis());
        String timestr=String.valueOf(time);
        logbutton=(Button) getActivity().findViewById(R.id.logbutton);
        username=(EditText) getActivity().findViewById((R.id.username));
        password=(EditText) getActivity().findViewById((R.id.password));
        signbutton=(Button) getActivity().findViewById(R.id.button2);

        new getip().execute();
        signbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_sign);
            }
        });
        logbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passstr=password.getText().toString();
                userstr=username.getText().toString();
                postpack=new JSONObject();
                try {
                    postpack.put("Type",0);
                    postpack.put("IP",ip);
                    postpack.put("Password",passstr);
                    postpack.put("Username",userstr);
                    postpack.put("Timestamp",timestr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new httppost().execute();
            }
        });
    }
}