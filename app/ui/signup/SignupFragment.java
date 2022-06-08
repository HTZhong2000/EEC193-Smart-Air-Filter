package com.example.iotapp.ui.signup;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.iotapp.R;
import com.example.iotapp.ui.login.LoginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupFragment extends Fragment {

    private SignupViewModel mViewModel;
    private EditText password;
    private EditText rpassword;
    private EditText username;
    private String response;
    private String ip;
    private Button signupbutton;
    private TextView texttest;
    private JSONObject postpack;
    private String userstr;
    public static SignupFragment newInstance() {
        return new SignupFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment, container, false);
    }

    private void setloginfo(String inputstr){
        Handler handler=new Handler();
        texttest=(TextView) getActivity().findViewById(R.id.textView10);
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
            if(information.equals("ACC_EXISt")){
                setloginfo("Account Exist");
            }
            if(information.equals("ACC_CREATE")){
                SharedPreferences savedata= getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=savedata.edit();
                editor.putBoolean("iflog",true);
                editor.putString("username",userstr);
                editor.commit();

                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        }
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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
        // TODO: Use the ViewModel
    }
    public void onResume () {
        super.onResume();
        int time =(int) (System.currentTimeMillis());
        String timestr=String.valueOf(time);
        signupbutton=(Button) getActivity().findViewById(R.id.signbutton);
        username=(EditText) getActivity().findViewById((R.id.username2));
        password=(EditText) getActivity().findViewById((R.id.password2));
        rpassword=(EditText) getActivity().findViewById((R.id.rpassword));

        new getip().execute();
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passstr=password.getText().toString();
                String rpassstr=rpassword.getText().toString();
                userstr=username.getText().toString();
                if(passstr.equals(rpassstr)) {
                    postpack = new JSONObject();
                    try {
                        postpack.put("Type", 1);
                        postpack.put("IP", ip);
                        postpack.put("Password", passstr);
                        postpack.put("Username", userstr);
                        postpack.put("Timestamp", timestr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new httppost().execute();
                }
                else{
                    setloginfo("Your Second Password Doesn't Match First one");
                }
            }
        });
    }
}