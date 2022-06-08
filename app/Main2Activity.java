package com.example.iotapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.iotapp.ui.reservation.ReservationFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapp.databinding.ActivityMain2Binding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main2Activity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;
    private ImageButton button_res;
    private ImageButton button_con;
    private ReservationFragment reservationFragment;
    private String result;
    private boolean log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain2.toolbar);
        SharedPreferences readdata =getSharedPreferences("data", Context.MODE_PRIVATE);
        Boolean iflog=readdata.getBoolean("iflog",false);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        TextView userdis=(TextView) navigationView.getHeaderView(0).findViewById(R.id.userdis);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        if(iflog){
            navigationView.getMenu().setGroupVisible(R.id.loggroup, false);
            String userstr=readdata.getString("username","None");
            userdis.setText(userstr);
        }
        else{
            navigationView.getMenu().setGroupVisible(R.id.loggroup, true);
            userdis.setText("Guest");
        }
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_condition, R.id.nav_reservation, R.id.nav_options, R.id.nav_log, R.id.nav_records,R.id.nav_sign)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        button_res= (ImageButton) findViewById((R.id.reservationButton));
        button_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_reservation);
            }
        });
        button_con= (ImageButton) findViewById((R.id.controlButton));
        button_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_condition);
            }
        });


    }



    public String is2String(InputStream is) throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is, "utf-8"));
        String line="";
        StringBuilder stringBuilder=new StringBuilder();
        while((line=bufferedReader.readLine())!=null){
            stringBuilder.append(line);
        }
        String response = stringBuilder.toString().trim();
        return response;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
