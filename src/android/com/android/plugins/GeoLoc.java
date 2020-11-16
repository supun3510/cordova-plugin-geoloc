package com.android.plugins;

import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;
import android.content.pm.PackageManager;
import org.apache.cordova.PluginResult;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import android.net.ConnectivityManager; 
import android.provider.Settings;
import android.content.ContentResolver;
import android.content.Intent;


//gps
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import android.location.Criteria;
import android.os.Looper;

import android.app.Activity;



////////////
import android.content.pm.PackageManager;
import android.content.ComponentName;

/**
 * Created by Udara Kasun on 2016/13/12.
 */
public class GeoLoc extends CordovaPlugin {

	public static final int REQ_CODE = 1;
	
	public static final String PERMISSION_DENIED = "DENIED"; 
	public static final String PERMISSION_ERROR = "ERROR"; 
	public static final String PERMISSION_GRANTED = "GRANTED"; 
	public static final String CONNECTION_STATUS = "STATUS"; 
	public static final String GPS_STATUS = "STATUS";
	public static final String GPS_LATITUDE = "LATITUDE";
	public static final String GPS_LONGITUDE = "LONGITUDE";
	
	private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT_PERMISSION = "hasPermission";
	
	public static final String LOCATION_STATUS = "STATUS";
	
    private CallbackContext permissionsCallback;
	private CallbackContext locationsCallback;
    private Context context;
	
	private Location currentBestLocation = null;
	 
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("RequestPermission")) {
            String message = args.getString(0);
            this.RequestPermission(message, callbackContext);
            return true;
        }else if(action.equals("CheckPermission")){
			String message = args.getString(0);
            this.CheckPermission(message, callbackContext);
            return true;
		}else if(action.equals("CheckConnectivity")){	
			this.CheckConnectivity(callbackContext);
            return true;
		}else if(action.equals("GetGPSStatus")){	
			this.GetGPSStatus(callbackContext);
            return true;
		}else if(action.equals("turnGPSOn")){	
			this.turnGPSOn(callbackContext);
            return true;
		}else if(action.equals("turnGPSOff")){	
			this.turnGPSOff(callbackContext);
            return true;
		}else if(action.equals("GetGpsLocation")){	
			this.GetGpsLocation(callbackContext);
            return true;
		}else if(action.equals("startLocationService")){	
			this.startLocationService(callbackContext);
            return true;
		}else if(action.equals("stopLocationService")){	
			this.stopLocationService(callbackContext);
            return true;
		}
        return false;
    }
    /*Permisision*/
    private void RequestPermission(String message, CallbackContext callbackContext) {		 
        if (message != null && message.length() > 0) {		
			try{
				if(cordova.hasPermission(message))
				{
					callbackContext.success(PERMISSION_GRANTED);
				}
				else
				{ 
			         permissionsCallback = callbackContext;	
					 cordova.requestPermission(this, REQ_CODE, message);
                    		 
				}
			}catch(Exception ex){
					callbackContext.error(PERMISSION_ERROR); 
			} 
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
	
	private void CheckPermission(String message, CallbackContext callbackContext) {	 
             if (message == null && message.length() > 0) {
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, KEY_ERROR, "CheckPermission");
				addProperty(returnObj, KEY_MESSAGE, "One time one permission only.");
				callbackContext.error(returnObj);
			} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, KEY_RESULT_PERMISSION, true);
				callbackContext.success(returnObj);
			} else {
				try {
					JSONObject returnObj = new JSONObject();
					addProperty(returnObj, KEY_RESULT_PERMISSION, cordova.hasPermission(message));
					callbackContext.success(returnObj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}
	
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		
		if (permissionsCallback == null) {
            return;
        }
		
		for(int r:grantResults)
		{
			if(r == PackageManager.PERMISSION_DENIED)
			{ 
		        JSONObject returnObj = new JSONObject();
				addProperty(returnObj, KEY_RESULT_PERMISSION, false);
                permissionsCallback.success(returnObj);
		        //Toast.makeText(this.cordova.getActivity().getApplicationContext(), "Oops you just denied the permission",  Toast.LENGTH_LONG).show();
				return;
			}else if(r == PackageManager.PERMISSION_GRANTED){
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, KEY_RESULT_PERMISSION, true);
                permissionsCallback.success(returnObj);
				 //Toast.makeText(this.cordova.getActivity().getApplicationContext(), "Oops you just denied the permission",  Toast.LENGTH_LONG).show();
			}else{
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, KEY_RESULT_PERMISSION, PERMISSION_ERROR);
                permissionsCallback.success(returnObj);
			}
		}
		 
        permissionsCallback = null;
		 
	}
	/*Permisision*/
	
	/*Internate*/
	private void CheckConnectivity(CallbackContext callbackContext) {	 
		try {			
			ConnectivityManager cm = (ConnectivityManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() != null){
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, CONNECTION_STATUS, "CONNECTION_AVAILABLE");
				callbackContext.success(returnObj);
			}else{
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, CONNECTION_STATUS, "NO_CONNECTION");
				callbackContext.success(returnObj); 
			} 
		}catch (Exception e) {
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, CONNECTION_STATUS, "CONNECTION_ERROR");			
				callbackContext.error(returnObj);		
		}
	} 
	/*Internate*/
	
	/*GPS*/
	private void GetGPSStatus(CallbackContext callbackContext){
		try{
			ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();
			String provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			JSONObject returnObj = new JSONObject();
			if(!provider.contains("gps")){ //if gps is disabled 
				addProperty(returnObj, GPS_STATUS, "DISABLED");
				callbackContext.success(returnObj); 
			}else{
				addProperty(returnObj, GPS_STATUS, "ENABLED");
				LocationManager mLocationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
				callbackContext.success(returnObj); 
			}
		}catch(Exception ex){
			JSONObject returnObj = new JSONObject();
			addProperty(returnObj, GPS_STATUS, "ERROR");			
			callbackContext.error(returnObj);
		}	
	}
	
	private void turnGPSOn(CallbackContext callbackContext){
		try{
			ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();
			String provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED); 
			
			if(!provider.contains("gps")){ //if gps is disabled
				//Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
				//intent.putExtra("enabled", true);
				//this.cordova.getActivity().sendBroadcast(intent);
				
				context = this.cordova.getActivity().getApplicationContext();
			    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.putExtra("enabled", true);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				 
				JSONObject returnObj = new JSONObject();
			    addProperty(returnObj, GPS_STATUS, "ENABLED");			
			    callbackContext.success(returnObj);
			}else{
				JSONObject returnObj = new JSONObject();
			     addProperty(returnObj, GPS_STATUS, "ALREADY_ENABLED");			
			     callbackContext.success(returnObj);
			}
		}catch(Exception ex){
			JSONObject returnObj = new JSONObject();
			addProperty(returnObj, GPS_STATUS, "ERROR");
            addProperty(returnObj, KEY_MESSAGE, ex.toString());			
			callbackContext.error(returnObj);
		}	
		
	}

	private void turnGPSOff(CallbackContext callbackContext){
		try{
			ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();
			 String provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED); 
			
			 if(provider.contains("gps")){ //if gps is enabled
				 Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
				 intent.putExtra("enabled", false);
				 this.cordova.getActivity().sendBroadcast(intent);
				 
				 JSONObject returnObj = new JSONObject();
			     addProperty(returnObj, GPS_STATUS, "DISABLED");			
			     callbackContext.success(returnObj);
			 }else{
				 JSONObject returnObj = new JSONObject();
			     addProperty(returnObj, GPS_STATUS, "ALREADY_DISABLED");			
			     callbackContext.success(returnObj);
			 }		 
		}catch(Exception ex){
			JSONObject returnObj = new JSONObject();
			addProperty(returnObj, GPS_STATUS, "ERROR");			
			callbackContext.error(returnObj);
		}
		 
	}
	
	private void GetGpsLocation(CallbackContext callbackContext){		
			locationsCallback=callbackContext;
			//context= this.cordova.getActivity().getApplicationContext();
			getLastBestLocation();		
	}
	 	  
	private void getLastBestLocation() {
		try{		
			final LocationListener locationListener =new LocationListener(){
				@Override
				public void onLocationChanged(Location location) {
					 
					// Toast.makeText(context, "Receive Location", Toast.LENGTH_SHORT).show();
					JSONObject returnObj = new JSONObject();
					addProperty(returnObj, GPS_STATUS, "AVAILABLE");
					addProperty(returnObj, GPS_LATITUDE,String.valueOf(location.getLatitude()));	
					addProperty(returnObj, GPS_LONGITUDE,String.valueOf(location.getLongitude()));					
					locationsCallback.success(returnObj);
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					//Log.d("Status Changed", String.valueOf(status));
				}

				@Override
				public void onProviderEnabled(String provider) {					 
					//Log.d("Provider Enabled", provider);
				}

				@Override
				public void onProviderDisabled(String provider) {
					//Log.d("Provider Disabled", provider);
				}
				
			};
			//Toast.makeText(this.cordova.getActivity().getApplicationContext(), "Call Location",  Toast.LENGTH_LONG).show();
			// Now first make a criteria with your requirements
			// this is done to save the battery life of the device
			// there are various other other criteria you can search for..
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);//ACCURACY_COARSE
			criteria.setPowerRequirement(Criteria.POWER_HIGH);//POWER_LOW
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setSpeedRequired(false);
			criteria.setCostAllowed(true);
			criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
			criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
			
			// Now create a location manager
			final LocationManager locationManager = (LocationManager)cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);

			// This is the Best And IMPORTANT part
			final Looper looper = null;		
			LocationManager mLocationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);		 
			locationManager.requestSingleUpdate(criteria, locationListener, looper); 
			//Toast.makeText(this.cordova.getActivity().getApplicationContext(), "Request Location",  Toast.LENGTH_LONG).show();
		}catch(Exception ex){
			JSONObject returnObj = new JSONObject();
			addProperty(returnObj, GPS_STATUS, "UNAVAILABLE"); 
			addProperty(returnObj, KEY_ERROR, ex.toString()); 
            locationsCallback.error(returnObj);			
		}
	}
	/*GPS*/
	
	/* Json Property */
	private void addProperty(JSONObject obj, String key, Object value) {
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException ignored) {
            //Believe exception only occurs when adding duplicate keys, so just ignore it
        }
    }
	/* Json Property */
	
	/*Location Service */
	public void startLocationService(CallbackContext callbackContext) {
	  Activity activity = cordova.getActivity();
	  Intent intent = new Intent(this.cordova.getActivity(), LocationService.class);
	  intent.setAction("start");
	  ///intent.putExtra("UserId", "012345");
	  
	  activity.getApplicationContext().startService(intent);
      // startService(new Intent(.getBaseContext(), LocationService.class));
	   
	  JSONObject returnObj = new JSONObject();
	  addProperty(returnObj, LOCATION_STATUS, "STARTED");			
	  callbackContext.error(returnObj);
    }

    // Method to stop the service
    public void stopLocationService(CallbackContext callbackContext) {
	  Activity activity = cordova.getActivity();
	  Intent intent = new Intent(this.cordova.getActivity(), LocationService.class);
	  intent.setAction("stop");
	  activity.getApplicationContext().startService(intent);
	  JSONObject returnObj = new JSONObject();
	  addProperty(returnObj, LOCATION_STATUS, "STOPED");			
	  callbackContext.error(returnObj);
    }
}