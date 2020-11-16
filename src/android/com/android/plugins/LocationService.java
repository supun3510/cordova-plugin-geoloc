package com.android.plugins;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.location.Criteria;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
  
import android.content.IntentFilter;
/**
   * Created by Udara Kasun 2019/04/18.
*/

public class LocationService extends Service{  
   public Context timerContext = this;   
   private AutoStart autostart = null;
   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }
   /** Called when the service is being created. */
   @Nullable
   @Override
   public void onCreate() {	    
       IntentFilter intentFilter = new IntentFilter();
	   //intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
	   try{
		  /*  intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			intentFilter.setPriority(100);
			autostart = new AutoStart();
			this.registerReceiver(autostart, intentFilter);
			Log.d("locationservice", "Service onCreate: locationservice is registered.");*/
	   }catch(Exception ex){
		    Toast.makeText(this, "Service onCreate Error "+ ex.toString(), Toast.LENGTH_LONG).show();  
	   }
       
       Toast.makeText(this, "Service onCreate", Toast.LENGTH_LONG).show();  
   }

	
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
	    if(intent != null)
		{			
			if (intent.getAction().equals("start")) {				
				//Start the service		
				Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
				Log.d("in timer", "Started");				 
                stoptimertask();
                startTimer();				
			} else {
				// Stop the service                 
                Toast.makeText(this, "Service stop command", Toast.LENGTH_LONG).show();				
				stopSelf();
			}
		}else{			
			Toast.makeText(this, "Service Restarted", Toast.LENGTH_LONG).show();
			stoptimertask();
            startTimer();
		}
	   
      return START_STICKY;
   }

   @Override
   public void onDestroy() {       
       super.onDestroy(); 
		if(autostart!=null)
        {
            this.unregisterReceiver(autostart);
            Log.d("locationservice", "Service onDestroy: locationservice is unregistered.");
        }	   
       Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
   }
   
    //Timer Task
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;	
    public void startTimer() {
		//Initialize Context		
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 30000, 30000); //
    }

    /**
     * it sets the timer to print the counter every x seconds 
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
			   
			   DatabaseService dbs=new DatabaseService(timerContext); 
				boolean istablexist=dbs.CheckTableIsExist("CurrentUser");
				if(istablexist){
					int UserId = getUserId();
					Log.d("Registration UserID ", " "+ UserId);
					if(UserId != -1){
						GetLocation(UserId);
					}else{
						Log.d("Registration 1", "Not Registered User Found");
					}
			    }else{
				  Log.d("Registration 2", "Not Registered User Found");
				}
			   
               Log.d("in timer", "Timer call");
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
   
   //Get Location Service 
   public void GetLocation(final int UserId){	
   			final LocationListener locationListener =new LocationListener(){
				@Override
				public void onLocationChanged(Location location) {	 
					Log.d("Location Value", String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()));					 
					Toast.makeText(timerContext, "Location "+ String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
					InsertLocation(location.getLatitude(),location.getLongitude(), UserId);
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					Log.d("Status Changed", String.valueOf(status));
				}

				@Override
				public void onProviderEnabled(String provider) {					 
					Log.d("Provider Enabled", provider);
				}

				@Override
				public void onProviderDisabled(String provider) {
					Log.d("Provider Disabled", provider);
				}
				
			};
			try {
				Log.d("Location Request", "Requested");
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);//ACCURACY_COARSE
				criteria.setPowerRequirement(Criteria.POWER_HIGH);//POWER_LOW
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);
				criteria.setSpeedRequired(false);
				criteria.setCostAllowed(true);
				criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
				criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
				
				final LocationManager locationManager = (LocationManager) timerContext.getSystemService(Context.LOCATION_SERVICE);
				// This is the Best And IMPORTANT part				 
				LocationManager mLocationManager = (LocationManager) timerContext.getSystemService(Context.LOCATION_SERVICE);				 
				locationManager.requestSingleUpdate(criteria, locationListener, Looper.getMainLooper()); 
				Log.d("Location Listing", "Listing");
			} catch (java.lang.SecurityException ex){ 
				Log.d("fail to request location update, ignore", ex.getMessage());
			} catch (IllegalArgumentException ex) {
				Log.d("Location Error", "network provider does not exist, " + ex.getMessage());
			}catch(Exception ex){
			    Log.d("Location Error", ex.getMessage());
			}
   }
   
   public void InsertLocation(Double Lat,Double Lng, int UserId){
	   try{
		    Log.d("InsertLocation", "Done");
			DatabaseService dbs=new DatabaseService(timerContext); 
			dbs.UpdateLocation(Lat,Lng ,UserId); 
	   }catch(Exception Ex){
		   Log.d("InsertLocation Error", Ex.getMessage());
	   }
   }
   
   private int getUserId(){
	   DatabaseService dbs = new DatabaseService(timerContext); 
	   int result=dbs.GetUserId();
	   Log.d("Get UserId 0 ", "Id "+result);
	   return (result == -1 || result == -2 || result== -3)? -1 :result;
   }
}