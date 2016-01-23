package amcd.opm.listeners;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;


/**
 * Created by franzchen on 2016-01-23.
 *
 */
public class PebbleListener extends Activity{

    private static final UUID APP_UUID = UUID.fromString("dummy"); //TODO add actual UUID from app

    private static final int KEY_BUTTON = 1231241; //TODO add values for buttons
    private static final int PANIC_BUTTON = 10101;
    private static final int STOP_BUTTON = 101225;

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

                        // Check which button was pressed
                        final int button = data.getInteger(KEY_BUTTON).intValue();


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
}
