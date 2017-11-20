package com.sst.anouncements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//Service Bootloader
//Start Service on Device Boot
public class Bootloader extends BroadcastReceiver {
    private static String TAG = "Bootloader";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(Bootloader.TAG, "Scheduling Update Service ...");
            UpdateService.schedule(context, 0);
        }
    }
}
