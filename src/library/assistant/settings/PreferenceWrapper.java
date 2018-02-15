package library.assistant.settings;

import com.google.gson.Gson;
import library.assistant.alert.AlertMaker;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.*;

public class PreferenceWrapper {

    public static final String CONFIG_FILE = "config.txt";

    private int nDaysWithoutFine;
    private float finePerDay;
    private String userName;
    private String password;

    public PreferenceWrapper(){
        nDaysWithoutFine = 14;
        finePerDay = 2;
        userName = "admin";
        setPassword("admin");
    }

    public int getnDaysWithoutFine() {
        return nDaysWithoutFine;
    }

    public void setnDaysWithoutFine(int nDaysWithoutFine) {
        this.nDaysWithoutFine = nDaysWithoutFine;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password.length() < 16) {
            this.password = DigestUtils.shaHex(password);
        } else {
            this.password = password;
        }
    }

    public static void initConfig(){
        Writer writer = null;
        try {
            PreferenceWrapper preferenceWrapper = new PreferenceWrapper();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preferenceWrapper, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    public static PreferenceWrapper getPreference(){
        Gson gson = new Gson();
        PreferenceWrapper preferenceWrapper = new PreferenceWrapper();
        try {
            preferenceWrapper = gson.fromJson(new FileReader(CONFIG_FILE), PreferenceWrapper.class);
        } catch (FileNotFoundException e) {
            initConfig();
            e.printStackTrace();
        }
        return preferenceWrapper;
    }

    public static void writePreferencesToFile(PreferenceWrapper preference){
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);
            AlertMaker.showSimpleAlert("Successes", "Settings updated");
        } catch (IOException e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e, "Failed", "Can't save configuration file");
        } finally {
            try {
                writer.close();
            } catch (IOException e) {e.printStackTrace();}
        }
    }
}
