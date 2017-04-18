package sstinc.sstannouncer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.net.Inet4Address;


/**
 * Boot Android Service
 * Starts Android Service on boot of device.
 * Starts <code>AndroidServiceAdaptor</code> service.
 */
public class AndroidServiceBoot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent startServiceIntent = new Intent(context, AndroidServiceAdaptor.class);
        context.startService(startServiceIntent);
    }
}
