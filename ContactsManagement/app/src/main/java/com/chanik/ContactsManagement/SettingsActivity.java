package com.chanik.ContactsManagement;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.preference.SwitchPreferenceCompat;


public class SettingsActivity extends AppCompatActivity {
    DbSqlite DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        DB = new DbSqlite(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

    }

    public static   class SettingsFragment extends PreferenceFragmentCompat   {
        DbSqlite DB;
        Notification_setting notifi;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            DB = new DbSqlite(getContext());
            SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat) findPreference("switch");
            ListPreference listPreference = findPreference("time");
            //Sending to a method that puts the values stored at the selected time and whether the notification is on or off
            setSummaryandChecked(switchPreferenceCompat,listPreference);
            //   setSummary(listPreference,ob.toString());
            //Listens to a list from which you select an hour to send an alert
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object ob) {
                    //Write the selected time in the appropriate place
                    setSummary(listPreference,ob.toString());
                    String[] select_time_arr;
                    String select_time=ob.toString();
                    //Dividing the selected time into hours and minutes
                    select_time_arr= select_time.split(":");
                    String  select_hour=select_time_arr[0];
                    //Turning the selected hour time to Int
                    int select_hour_int = Integer.parseInt(select_hour.replaceAll("[\\D]", ""));

                    notifi = new Notification_setting(getContext());
                    notifi.Notification_in_Selected_time(select_time);
                    return false;

                }
            });
            //Setting up a listener to swich Whether to allow notification to be sent or not
            switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    // Checked the switch programmatically
                    if(switchPreferenceCompat.isChecked()){
                        notifi = new Notification_setting(getContext());
                        //Send to a method that turns off alerts
                        notifi.Notification_off();
                        switchPreferenceCompat.setChecked(false);
                        setSummary(listPreference,"Not Set Yet ");
                        // Unchecked the switch programmatically
                    }else {
                        // Toast.makeText(mActivity,"Checked",Toast.LENGTH_SHORT).show();
                        notifi = new Notification_setting(getContext());
                        //Send to a method that turns on alerts
                        notifi.Notification_on();
                        switchPreferenceCompat.setChecked(true);
                    }
                    return false;
                }
            });

        }
        // method that puts the stored values at the selected time
        private void setSummary( ListPreference listPreference, String Selected_time) {
            listPreference.setSummaryProvider(new Preference.SummaryProvider() {
                @Override
                public CharSequence provideSummary(Preference preference) {
                    return Selected_time;
                }
            });
        }
        // method that puts the values stored at the selected time and whether the notification is on or off
        private void setSummaryandChecked(SwitchPreferenceCompat switchPreferenceCompat,ListPreference listPreference ) {
            String state = DB.getState("State");
            String time = DB.getTime("State");
            if (state.equals("ON") || state.equals("ON_Selected_Time"))
                switchPreferenceCompat.setChecked(true);
            else
                switchPreferenceCompat.setChecked(false);
            if (time.equals("00"))
                //Sending to a method that puts the stored values at the selected time
                setSummary(listPreference, "Not Set Yet ");
            else
                //Sending to a method that puts the stored values at the selected time
                setSummary(listPreference, time);
        }
    }
}
