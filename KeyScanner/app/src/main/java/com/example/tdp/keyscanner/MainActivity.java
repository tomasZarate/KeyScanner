package com.example.tdp.keyscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    protected Toolbar myToolbar;
    protected MenuItem itemWifi;
    protected Button btn;
    protected ListView lvRedes;
    protected ArrayList<String> lista;
    protected ArrayAdapter<String> adaptador;
    //Wifi
    protected WifiManager wifiManager;
    protected int cantRedes;
    protected WifiScanReceiver mWifiScanResultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        lvRedes = findViewById(R.id.listView);
        lista= new ArrayList<>();
        adaptador= new ArrayAdapter<>(getApplicationContext(), R.layout.texto_negro, lista);
        lvRedes.setAdapter(adaptador);
        cantRedes=0;
        btn=findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int redes=getWifi();
                lista.add(""+redes);
                adaptador.notifyDataSetChanged();
            }
        });

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiScanResultReceiver= new WifiScanReceiver();
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
                    itemWifi.setIcon(R.drawable.signal_wifi);
                }
                return true;
            case R.id.action_about:
                // User chose the "About" item, show the app settings UI...
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);


        }
    }

    private int getWifi() {

        int resultado;
        IntentFilter scanIntent = new IntentFilter();
        scanIntent.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(mWifiScanResultReceiver, scanIntent);

        if (!wifiManager.isWifiEnabled() && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            wifiManager.setWifiEnabled(true);
        }
        if(wifiManager.startScan()){
            List<ScanResult> resultados= wifiManager.getScanResults();
            resultado= resultados.size();
        }
        else
            resultado=21;
        return resultado;
    }
}