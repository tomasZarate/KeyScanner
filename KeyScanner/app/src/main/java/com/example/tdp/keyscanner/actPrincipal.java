package com.example.tdp.keyscanner;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

public class actPrincipal extends AppCompatActivity {

    protected Toolbar myToolbar;
    protected MenuItem itemWifi;

    //Wifi
    protected WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_act_principal);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //wifiSwitch= findViewById(R.id.wifiSwitch);
        //wifiSwitch= (Switch) menu.findItem(R.id.wifi_switch);
        /*wifiSwitch.setChecked(wifiManager.isWifiEnabled());
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
                // true if the switch is in the On position
                wifiSwitch.setChecked(!wifiManager.isWifiEnabled());
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        itemWifi = menu.findItem(R.id.action_wifi);
        if(!wifiManager.isWifiEnabled()){
            itemWifi.setIcon(R.drawable.signal_wifi_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_wifi:
                wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
                if(wifiManager.isWifiEnabled()){
                    itemWifi.setIcon(R.drawable.signal_wifi_off);
                }
                else{
                    itemWifi.setIcon(R.drawable.signal_wifi_4);
                }
                return true;
            case R.id.action_about:
                // User chose the "About" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);


        }
    }

}