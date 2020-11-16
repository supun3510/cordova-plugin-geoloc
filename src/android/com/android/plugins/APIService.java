package com.android.plugins;

import android.content.Context;
import java.util.ArrayList;
 
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;  
import java.net.MalformedURLException; 
import java.io.DataOutputStream;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.Thread;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import java.lang.System;
 
public class APIService{
	Context context;
	//public final String URl= "http://gtautomation.hayflex.com/api/MobileApi/UpdateMerchandizerHistory?";//testGet?";//";
	private final String USER_AGENT = "Cordova";
	 
	public APIService(Context context) { 		
		this.context=context; 
	}
	
	boolean result=false;
	boolean isthreadrunning=true;
	protected boolean SendGPSData(final ArrayList<GPSLocations> GPSLLst,final int UserId){  
	   
	Thread thread = new Thread(new Runnable() {		 
					@Override
					public void run() {
						Looper.prepare();
						Log.d("Thread", "Thread is running");
						try{ 
							Log.d("SendGPSData", "Thread");				
							JSONObject lst= ListToJson(GPSLLst);				   
							URL obj = new URL(URl);
							HttpURLConnection con = (HttpURLConnection) obj.openConnection();

							// optional default is POST
							con.setRequestMethod("POST");
							con.setRequestProperty("User-Agent", USER_AGENT);
							con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

							 
							final String urlParameters = "LocationList="+ lst.toString() +"&UserId="+ UserId;
							
							// Send post request
							con.setDoOutput(true);
							DataOutputStream wr = new DataOutputStream(con.getOutputStream());
							wr.writeBytes(urlParameters);
							wr.flush();
							wr.close();
		 
							int responseCode = con.getResponseCode();					 
							Log.d("API Response 1", "\nSending 'POST' request to URL : " + (URl+ "LocationList="+ lst.toString() + "&UserId="+ UserId));
							Log.d("API Response 2", "Locations : " + lst.toString() + "&UserId="+ UserId);					
							Log.d("API Response 3", "Response Code : " + responseCode);

							BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
							String inputLine;
							StringBuffer response = new StringBuffer();

							while ((inputLine = in.readLine()) != null) {
								response.append(inputLine);
							}
							in.close();
							
							//print result  
							String resultstr = response.toString().replaceAll("^\"|\"$", "");
							String eqto="Saved";
							Log.d("API Response 4", resultstr);
							if(eqto.equals(resultstr)){
								result = true;	
								isthreadrunning	= false;	
								Log.d("API Response 7", "Saved true");								
							}else{
								result = false;	
								isthreadrunning	= false;	
								Log.d("API Response 8", "Saved false");								
							} 							 
						}catch(Exception ex){
							Toast.makeText(context, "API Call Failed", Toast.LENGTH_LONG).show();
							Log.d("API Exception ", ex.toString());
							result = false;
							isthreadrunning =false;
						} 
					}
					
				});
        thread.start();		
		Log.d("API Response 5", "Waiting for Api Response");	
	    long startTime = System.currentTimeMillis();
		while(isthreadrunning){
			long timeInterval = System.currentTimeMillis() - startTime;
			if(timeInterval > 5000){
			    //result=false;
				thread.interrupt();
				break;
			}
		}
		Log.d("API Response 6", "Waiting for Api Response Received");
        thread.interrupt();		
		return result; 
	}
	
	private JSONArray ListToJson(ArrayList<GPSLocations> GPSLLst){
		JSONArray Locationslist = new JSONArray();
		try{			
			  for (int i = 0; i < GPSLLst.size(); i++) {
                  JSONObject jso = new JSONObject();
                  jso.put("Id", GPSLLst.get(i).getId());
				  jso.put("Latitude", GPSLLst.get(i).getLatitude());	
				  jso.put("Longitude", GPSLLst.get(i).getLongitude());	
				  jso.put("DateTime", GPSLLst.get(i).getDateTime());	
				  Locationslist.put(jso);
			  }
			  return Locationslist;
		}catch(Exception ex){
			return Locationslist;
		}
	}
	
}
