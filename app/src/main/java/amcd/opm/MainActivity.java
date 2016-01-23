package amcd.opm;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.app.Fragment;

import amcd.opm.listeners.PebbleListener;

public class MainActivity extends AppCompatActivity {
    final String[] screen = {"Welcome","Event Creation","Contact Selection",""};
    String currentScreen;
    String eventName;
    String description;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTranscation = fragmentManager.beginTransaction();
        try {
            currentScreen = savedInstanceState.getString("lastScreen");
        }
        catch(Exception e) {

                currentScreen = screen[0];

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                if(currentScreen.equals(screen[0])) {
                    createEvent fragment = new createEvent();
                    button.setText("SELECT CONTACTS");
                    currentScreen = screen[1];
                    fragmentTranscation.replace(R.id.main_fragment, fragment);
                    // fragmentTranscation.add(R.id.create_event,fragment);
                    //fragmentTranscation.remove(fragmentManager.findFragmentById(R.id.main_fragment));

                    fragmentTranscation.commit();

                }
                else if(currentScreen.equals(screen[1])){
                    try {
                        button.setText(description);
                    }
                    catch (Exception e){
                        button.setText("Error");
                    }

                }

            }
        });

        PebbleListener listener = new PebbleListener(getApplicationContext()); // Link listener to main activity

    }

    public void setEventName(String name){
        this.eventName = name;

    }

    public void setDescription(String desc){
        this.description = desc;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putString("lastScreen", currentScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
