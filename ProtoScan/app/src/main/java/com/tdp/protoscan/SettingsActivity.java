package com.tdp.protoscan;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {

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

            /*Preference pref = (Preference) findPreference("pref_key_pattern_settings");
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
       /* @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
            if (requestCode == 5544) {
                switch (resultCode) {
                    case RESULT_OK:
                        Intent i = new Intent(this.getContext(), PatronActivity.class);
                        i.putExtra("Eliminar Patron", "nulo");
                        startActivity(i);
                }
            }
        }*/

    }

}
