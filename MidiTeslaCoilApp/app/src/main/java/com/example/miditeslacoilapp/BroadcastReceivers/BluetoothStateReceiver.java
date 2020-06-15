package com.example.miditeslacoilapp.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothStateReceiver extends BroadcastReceiver {

    public BluetoothStateReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        toast(context, intent.getAction());
    }

    private void toast(Context context, String action){
        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
    }
}
