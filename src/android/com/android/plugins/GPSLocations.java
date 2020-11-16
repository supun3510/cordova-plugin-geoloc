package com.android.plugins;

public class GPSLocations{
	private String Id;
	private String Latitude;	
	private String Longitude;	
	private String DateTime;

	public String getId() {
		return Id;
	}
  
    public void setId(String Id) {
		this.Id = Id;
	}

	public String getLatitude() {
		return Latitude;
	}
  
    public void setLongitude(String Longitude) {
		this.Longitude = Longitude;
	}
	
	public String getLongitude() {
		return Longitude;
	}
  
    public void setLatitude(String Latitude) {
		this.Latitude = Latitude;
	}
	
	public String getDateTime() {
		return DateTime;
	}
  
    public void setDateTime(String DateTime) {
		this.DateTime = DateTime;
	}
}