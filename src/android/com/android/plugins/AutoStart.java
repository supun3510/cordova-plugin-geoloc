package com.android.plugins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Build;
import android.widget.Toast;
import java.lang.Thread;
import android.content.pm.PackageManager;

import com.android.plugins.LocationService;


public class AutoStart extends BroadcastReceiver { 
	@Override
    public void onReceive(Context context, Intent intent) {
		try{		  
			if(intent!=null){
				 if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
					Log.d("Intent", "Intent Received");
					Toast.makeText(context, "Device Started", Toast.LENGTH_LONG).show(); 
					Intent myintent = new Intent(context,LocationService.class); 
					myintent.setAction("start");
					context.startService(myintent);  
				}
			}
		}catch(Exception ex){
			Log.d("Intent Error", ex.toString());
		}
    }	 
}