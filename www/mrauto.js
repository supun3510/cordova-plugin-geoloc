 var exec = require('cordova/exec');
 function GeoLoc() { 
	        this.ACCESS_COARSE_LOCATION = 'android.permission.ACCESS_COARSE_LOCATION';
            this.ACCESS_FINE_LOCATION = 'android.permission.ACCESS_FINE_LOCATION';
            this.ACCESS_LOCATION_EXTRA_COMMANDS = 'android.permission.ACCESS_LOCATION_EXTRA_COMMANDS';
            this.ACCESS_NETWORK_STATE = 'android.permission.ACCESS_NETWORK_STATE';
            this.CAMERA = 'android.permission.CAMERA'; 
            
 }
   
 GeoLoc.prototype = { 
    RequestPermission: function(arg0, success, error) {	 
        exec(success, error, 'GeoLoc', 'RequestPermission', [arg0]);
    },
	CheckPermission: function(arg0, success, error) { 
        exec(success, error, 'GeoLoc', 'CheckPermission', [arg0]);
    },
	CheckConnectivity: function(success, error) { 	    
        exec(success, error, 'GeoLoc', 'CheckConnectivity',[""]);
    },
	GetGPSStatus: function(success, error) { 	    
        exec(success, error, 'GeoLoc', 'GetGPSStatus',[""]);
    },
	turnGPSOn: function(success, error) { 	    
        exec(success, error, 'GeoLoc', 'turnGPSOn',[""]);
    },
	turnGPSOff: function(success, error) { 	    
        exec(success, error, 'GeoLoc', 'turnGPSOff',[""]);
    },
	GetGpsLocation: function(success, error) { 	    
        exec(success, error, 'GeoLoc', 'GetGpsLocation',[""]);
    },
	startLocationService: function(success, error) { 	    
        exec(success, error, 'GeoLoc', 'startLocationService',[""]);
    },
	stopLocationService: function(success, error) { 	    
        exec(success, error, 'GeoLoc', 'stopLocationService',[""]);
    }
}; 
 
module.exports = new GeoLoc();
 