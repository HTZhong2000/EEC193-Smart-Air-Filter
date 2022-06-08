package com.example.iotapp.ui.options;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.iotapp.R;

public class OptionsFragment extends Fragment {

    private OptionsViewModel mViewModel;

    public static OptionsFragment newInstance() {
        return new OptionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.options_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OptionsViewModel.class);
        // TODO: Use the ViewModel
        Button button1=(Button) getActivity().findViewById(R.id.logout);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences savedata= getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=savedata.edit();
                editor.putBoolean("iflog",false);
                editor.commit();
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });
    }
    public void onResume () {
        super.onResume();
        SharedPreferences readdata = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        Boolean iflog = readdata.getBoolean("iflog", false);
        if (iflog == false) {
            Navigation.findNavController(this.getView()).navigate(R.id.nav_log);
        }
    }
}