package amcd.opm;

import java.util.ArrayList;

/**
 * Created by amcd on 1/24/16.
 */
public class EventProfile {
    private String name;
    private ArrayList<String> phoneNumbers;
    private String message;
    private boolean useGps;

    public EventProfile(String name, ArrayList<String> phoneNumbers, String message, boolean useGps){
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.message = message;
        this.useGps = useGps;
    }

    public String getName(){
        return this.name;
    }
    public ArrayList<String> getPhoneNumbers(){
        return this.phoneNumbers;
    }
    public String getMessage(){
        return this.message;
    }
    public boolean isUseGps(){
        return useGps;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPhoneNumbers(ArrayList<String> phoneNumbers){
        this.phoneNumbers = phoneNumbers;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setUseGps(boolean useGps){
        this.useGps = useGps;
    }
}
