package com.example.miditeslacoilapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.miditeslacoilapp.BluetoothViewModel;
import com.example.miditeslacoilapp.R;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class InterruptorFragment extends Fragment {
    private BluetoothViewModel bluetoothViewModel;
    private BluetoothSPP bluetoothSPP;
    private static int frequencyThreshold = 60;
    private static int pwmThreshold = 100;

    private SeekBar freqSeekBar;
    private SeekBar pwmSeekBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothViewModel = ViewModelProviders.of(getActivity()).get(BluetoothViewModel.class);
        bluetoothSPP = bluetoothViewModel.getBluetoothSPP();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interruptor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        freqSeekBar = view.findViewById(R.id.freqSeekBar);
        pwmSeekBar = view.findViewById(R.id.pwmSeekBar);

        freqSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                Integer freq = seekBar.getProgress() + frequencyThreshold;
                bluetoothSPP.send(freq.toString(), true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pwmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer pwm = seekBar.getProgress() + pwmThreshold;
                bluetoothSPP.send(pwm.toString() + "p", true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        bluetoothSPP.send("0", true); // make sure to stop playing sound after we exit the fragment
    }
}