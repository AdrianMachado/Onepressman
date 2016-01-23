package amcd.opm.listeners;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by franzchen on 2016-01-23.
 *
 */
public class PebbleListener extends Activity{

    private static final UUID APP_UUID = UUID.fromString("3783cff2-5a14-477d-baee-b77bd423d079"); //TODO add actual UUID from app

    //TODO add values for buttons
    private static final int PANIC_BUTTON = 0;
    private static final int STOP_BUTTON = 1;

    private PebbleDataReceiver listenerReceiver;
    private Context context;
    // Set to not panicking by default
    private boolean panicking = false;

    public PebbleListener(Context context) {

        this.context = context;

        if (listenerReceiver == null) {
            listenerReceiver = new PebbleDataReceiver(APP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary data) {

                    PebbleKit.sendAckToPebble(context, transactionId);

                    // Read button input
                    if (data.getInteger(PANIC_BUTTON) != null && !panicking) {
                        //Panic
                        //panicHandle();
                        Log.d("listener", "triggered");
                        panicking = true;
                    }

                    if (data.getInteger(STOP_BUTTON) != null && panicking) {
                        //Stop panic
                        Log.d("listener", "stop triggered");
                        panicking = false;
                    }

                }

            };
        }

        PebbleKit.registerReceivedDataHandler(context, listenerReceiver);
    }

    public void pause() {

        if (listenerReceiver != null) {
            unregisterReceiver(listenerReceiver);
            listenerReceiver = null;
        }
    }

    private void panicHandle() {

        // Intent parameters
        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sendPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                            break;
                    }
            }
        }, new IntentFilter(SMS_SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));

        // TODO Get user parameters
        String smsBody = "test"; // Get SMS body
        String[] phoneNumbers = {"6477802969", "2269786695"}; // Get #(s) to be sent

        SmsManager smsManager = SmsManager.getDefault();

        for (String number : phoneNumbers) {
            smsManager.sendTextMessage(number, null, smsBody, sendPendingIntent, deliveredPendingIntent);
        }

    }
}
