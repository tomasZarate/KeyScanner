package com.tdp.protoscan;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
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

            Preference pref = (Preference) findPreference("pref_key_pattern_settings");

            Preference userButton = (Preference) findPreference("Cambiar patron");

            userButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intentPatron = new Intent(getActivity(),PatronActivity.class);
                    startActivityForResult(intentPatron, 5544);
                    return true;
                }
            });

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode){
                case 5544:
                    Intent intentPatron = new Intent(getActivity(),PatronActivity.class);
                    intentPatron.putExtra("Eliminar Patron", "nulo");
                    startActivity(intentPatron);
            }
        }
    }

}
