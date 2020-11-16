package com.android.plugins;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import android.net.ConnectivityManager; 
import java.util.Calendar;
import android.content.ContentValues;
import java.util.ArrayList;

public class DatabaseService extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "AgriAutomation.db";
	Context DbContext;
	public DatabaseService(Context context) {
      super(context, DATABASE_NAME , null, 1);
      this.DbContext=context;	  
	  
	}
	
	@Override
    public void onCreate(SQLiteDatabase db) {
	  // TODO Auto-generated method stub
	   //db.execSQL(
		// "create table contacts " +
		// "(id integer primary key, name text,phone text,email text, street text,place text)"
	   //);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // TODO Auto-generated method stub
       //db.execSQL("DROP TABLE IF EXISTS contacts");
       //onCreate(db);
    }
	
	public void UpdateLocation(Double Lat,Double Lng, int UserId){
		ConnectivityManager cm = (ConnectivityManager) DbContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() != null){
				//Api Call Upload Existing Data
				ArrayList<GPSLocations> GPSLLst= getOfflineGPSLocations();
				GPSLocations CGPS=new GPSLocations();
				CGPS.setId("0");
				CGPS.setLatitude(Lat.toString());
				CGPS.setLongitude(Lng.toString());
				CGPS.setDateTime(Calendar.getInstance().getTime().toString());	
                GPSLLst.add(CGPS);	
				Log.d("UpdateLocation", "Done");
				//GPSLLst send to the api				 
				boolean APIresult= new APIService(DbContext).SendGPSData(GPSLLst, UserId);
				Log.d("API Call", "Call to API Result" + APIresult);
				if(APIresult){
					if(deleteAllGPSLocations()){
						Toast.makeText(DbContext, "GPS Record Updated", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(DbContext, "GPS Record is Deleting Failed", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(DbContext, "API Call Failed", Toast.LENGTH_LONG).show();
					if(InsertLocation(Lat.toString(), Lng.toString())){
						Toast.makeText(DbContext, "Off-line GPS Updated", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(DbContext, "Off-line GPS is Updating Field", Toast.LENGTH_LONG).show();
					} 
				}				
			}else{				 
				if(CheckTableIsExist("GPSLocations")){
					//InsertData To The Table
					if(InsertLocation(Lat.toString(), Lng.toString())){
						Toast.makeText(DbContext, "Off-line GPS Updated", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(DbContext, "Off-line GPS is Updating Field", Toast.LENGTH_LONG).show();
					}					
				}else{
					//No Exist Table
					Toast.makeText(DbContext, "All Aborted", Toast.LENGTH_LONG).show();
				}
			}
	}
	
	public boolean CheckTableIsExist(String tableName){
		try{			 
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
			if (!cursor.moveToFirst())
			{				
				cursor.close();
				Log.d("iDB", "1 FALSE");
				return false;				
			}
			int count = cursor.getInt(0);
			cursor.close();
			Log.d("iDB", "Count" + count);
			return count > 0;
		}catch(Exception ex){
			Log.d("iDB", "3 FALSE");
			return false;
		}
	}

	public boolean InsertLocation(String Lati,String Lng) {
		try{
		  SQLiteDatabase db = this.getWritableDatabase();
		  ContentValues contentValues = new ContentValues();
		  contentValues.put("Latitude", Lati);
		  contentValues.put("Longitude", Lng);
		  contentValues.put("DateTime", Calendar.getInstance().getTime().toString());	
		  contentValues.put("Sync", "0");  
		  db.insert("GPSLocations", null, contentValues);
		  return true;
		}catch(Exception ex){
			 return false;
		}     
   }

	public boolean deleteAllGPSLocations() {
       try{
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL("DELETE FROM GPSLocations");
			db.close();
			return  true;
	   }catch(Exception ex){
			return false;
	   }
    }
   
    public ArrayList<GPSLocations> getOfflineGPSLocations() {
     
      ArrayList<GPSLocations> array_list = new ArrayList<GPSLocations>();
      try{
		  SQLiteDatabase db = this.getReadableDatabase();
		  Cursor res =  db.rawQuery("SELECT * FROM GPSLocations", null );
		  res.moveToFirst();
		  
		  while(res.isAfterLast() == false){
			 GPSLocations GPSL=new GPSLocations();
			 GPSL.setId(res.getString(res.getColumnIndex("Id")));
			 GPSL.setLatitude(res.getString(res.getColumnIndex("Latitude")));
			 GPSL.setLongitude(res.getString(res.getColumnIndex("Longitude")));
			 GPSL.setDateTime(res.getString(res.getColumnIndex("DateTime")));		  
			 array_list.add(GPSL);
			 res.moveToNext();
		  }
		  //db.close();
	  }catch(Exception ex){
		  Log.d("getOfflineGPSLocations", ex.toString());
	  }
      return array_list;
   }
   
    public int GetUserId(){
	   try{
		  SQLiteDatabase db = this.getReadableDatabase();
		  Cursor res =  db.rawQuery("SELECT * FROM CurrentUser", null );
		    if(res != null){
                if (res.moveToFirst()) {
                  return  res.getInt(1);                     
                }else{
					return -1;
				}                
            }else{
				return -2;
			}
            //db.close();             
	  }catch(Exception ex){
		  return -3;
	  }
   }
}

