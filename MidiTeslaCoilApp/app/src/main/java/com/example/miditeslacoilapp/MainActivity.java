package com.example.miditeslacoilapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.miditeslacoilapp.BroadcastReceivers.BluetoothStateReceiver;
import com.example.miditeslacoilapp.Utils.ConnectionState;
import com.example.miditeslacoilapp.ViewModel.BluetoothViewModel;
import com.example.miditeslacoilapp.ui.InterruptorFragment;
import com.example.miditeslacoilapp.ui.ConnectionFragment;
import com.example.miditeslacoilapp.ui.MidiFragment;
import com.example.miditeslacoilapp.ui.adapters.FragmentPageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
                                                    ViewPager.OnPageChangeListener,
                                                    BluetoothSPP.BluetoothConnectionListener{
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem currentMenuItem;
    private BluetoothSPP bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBluetooth();

        if(!bluetooth.isBluetoothAvailable()){
            broadcastState(ConnectionState.BLUETOOTH_NOT_AVAILABLE);
        }

        setupActivity();

        setupBroadcastReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetooth.stopService();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            onActivityRequestResult(requestCode, resultCode, data, ConnectionFragment.class.getSimpleName());
        }
    }

    private void onActivityRequestResult(int requestCode, int resultCode, Intent data, String fragmentName){
        try {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getFragments().size() > 0) {
                for(int i=0; i<fm.getFragments().size(); i++){
                    Fragment fragment = fm.getFragments().get(i);
                    if (fragment != null && fragment.getClass().getSimpleName().equalsIgnoreCase(fragmentName)) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastState(String action){
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    private void setupBluetooth(){
        bluetooth = new BluetoothSPP(this);
        bluetooth.setBluetoothConnectionListener(this);
        BluetoothViewModel bluetoothViewModel = ViewModelProviders.of(this).get(BluetoothViewModel.class);
        bluetoothViewModel.setBluetoothSPP(bluetooth);
    }

    private void setupActivity(){
        FragmentPageAdapter fragmentPagerAdapter = new FragmentPageAdapter(getSupportFragmentManager(), this);
        fragmentPagerAdapter.addFragment(new ConnectionFragment(), "connectionFragment");
        fragmentPagerAdapter.addFragment(new InterruptorFragment(), "interruptorFragment");
        fragmentPagerAdapter.addFragment(new MidiFragment(), "midiFragment");

        viewPager = findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(fragmentPagerAdapter);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void setupBroadcastReceiver(){
        BluetoothStateReceiver broadcastReceiver;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectionState.BLUETOOTH_NOT_AVAILABLE);
        intentFilter.addAction(ConnectionState.CONNECTION_SUCCESSFUL);
        intentFilter.addAction(ConnectionState.CONNECTION_LOST);
        intentFilter.addAction(ConnectionState.UNABLE_TO_CONNECT);

        broadcastReceiver = new BluetoothStateReceiver();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        if(currentMenuItem != null){
            currentMenuItem.setChecked(false);
        } else {
            bottomNavigationView.getMenu().getItem(1).setChecked(false);
        }
        currentMenuItem = bottomNavigationView.getMenu().getItem(position);
        currentMenuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_connect:
                viewPager.setCurrentItem(0);
                break;
            case R.id.action_interruptor:
                viewPager.setCurrentItem(1);
                break;
            case R.id.action_midi:
                viewPager.setCurrentItem(2);
                break;
        }
        return false;
    }

    @Override
    public void onDeviceConnected(String name, String address) {
        broadcastState(ConnectionState.CONNECTION_SUCCESSFUL);
    }

    @Override
    public void onDeviceDisconnected() {
        broadcastState(ConnectionState.CONNECTION_LOST);
    }

    @Override
    public void onDeviceConnectionFailed() {
        broadcastState(ConnectionState.UNABLE_TO_CONNECT);
    }
}
