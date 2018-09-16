package com.tdp.protoscan;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;


public class SettingsActivity extends AppCompatActivity {

    public static final String
            KEY_AUTOFOCUS_PREF = "switch_autofocus";
    public static final String
            KEY_FLASH_PREF = "switch_flash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        public SettingsFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            //Preference pref = (Preference) findPreference("pref_key_pattern_settings");

            /*pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intentPatron = new Intent(getActivity(),PatronActivity.class);
                    startActivityForResult(intentPatron, 5544);
                    return true;
                }
            });*/

        }
        /*
        * <intent
                android:action="android.intent.action.VIEW"
                android:targetPackage="com.tdp.protoscan"
                android:targetClass="com.tdp.protoscan.PatronActivity"/>
        *
        * */

    }


}
