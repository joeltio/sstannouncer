package com.sst.anouncements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//Service Bootloader
//Start Service on Device Boot
public class Bootloader extends BroadcastReceiver {
    private static String TAG = "Bootloader"l;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, UpdateService.class);
            Log.d(Bootloader.TAG, "Starting Update Service ...");
            context.startService(serviceIntent);
        }
    }
}
