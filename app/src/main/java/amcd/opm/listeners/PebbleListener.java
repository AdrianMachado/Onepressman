package amcd.opm.listeners;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.UUID;

import amcd.opm.EventProfile;


/**
 * Created by franzchen on 2016-01-23.
 *
 */
public class PebbleListener implements LocationListener {

    private static final UUID APP_UUID = UUID.fromString("fae951bf-4f1d-4fc9-b60c-1e67f54f58e8"); //TODO add actual UUID from app

    //TODO add values for buttons
    private static final int PANIC_BUTTON = 0;

    private PebbleDataReceiver listenerReceiver;
    private Context context;
    private LocationManager manager;
    private Location location;
    private EventProfile currentProfile;

    public PebbleListener(Context context, LocationManager locationManager) {

        this.context = context;
        this.manager = locationManager;

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(manager.NETWORK_PROVIDER, 500, 0, this);
            Log.d("constructor", "locations requested");
        }

        Log.d("constructor", "before pebble data");

        if (listenerReceiver == null) {
            listenerReceiver = new PebbleDataReceiver(APP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary data) {

                    PebbleKit.sendAckToPebble(context, transactionId);

                    // Read button input
                    if (data.getInteger(PANIC_BUTTON) != null) {
                        //Panic
                        try {
                            panicHandle();
                        } catch (NullPointerException e) {
                            Log.d("listener", "failed to text");
                            e.printStackTrace();
                        }

                    }

                }
            };

            PebbleKit.registerReceivedDataHandler(context, listenerReceiver);
        }
    }
    

    public void panicHandle() {

        double lon = 0.0;
        double lat = 0.0;
        String textMessage = getMessage();

        if (currentProfile.isUseGps()) {
            if (!location.equals(null)) {
//            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
//            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//                if (manager != null) {
//                    location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    Log.d("panic handle", "last known loc");
//                } else {
//                    Log.d("manager check", "manager exists");
//                }
//            }
                try {
                    lon = location.getLongitude();
                    lat = location.getLatitude();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("location", "null");
            }

            String coordinates = "{" + lon + ", " + lat + "}";
            textMessage = getMessage() + " at " + coordinates;
        }

        ArrayList<String> phoneNumbers = getPhoneNumbers();

// Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
        for(String numbers : getPhoneNumbers()) {
            smsManager.sendTextMessage(numbers, null, textMessage, null, null);
        }
    }

    private ArrayList<String> getPhoneNumbers() {
        ArrayList<String> numbers = currentProfile.getPhoneNumbers();
        return numbers;
    }

    private String getMessage() {
       return currentProfile.getMessage();
    }

    @Override
    public void onLocationChanged(Location loc) {
        this.location = loc;
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void setProfile(EventProfile profile) {
        this.currentProfile = profile;
    }

}
