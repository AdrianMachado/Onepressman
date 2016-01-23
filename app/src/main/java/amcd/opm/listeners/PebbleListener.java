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
public class PebbleListener {

    private static final UUID APP_UUID = UUID.fromString("3783cff2-5a14-477d-baee-b77bd423d079"); //TODO add actual UUID from app

    //TODO add values for buttons
    private static final int PANIC_BUTTON = 0;

    private PebbleDataReceiver listenerReceiver;
    private Context context;

    public PebbleListener(Context context) {

        this.context = context;

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
        String[] phoneNumbers = getPhoneNumbers();
        String message = getMessage();

// Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
        for(String numbers : phoneNumbers) {
            smsManager.sendTextMessage(numbers, null, message, null, null);
        }
    }

    private String[] getPhoneNumbers() {
        String[] numbers = {"6477802969", "2269786695"};
        return numbers;
    }

    private String getMessage() {
        return "test";
    }
}
