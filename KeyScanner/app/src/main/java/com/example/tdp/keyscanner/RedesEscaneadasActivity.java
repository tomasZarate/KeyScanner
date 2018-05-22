package com.example.tdp.keyscanner;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RedesEscaneadasActivity extends AppCompatActivity {

    protected Button btn;
    protected ListView lvRedes;
    protected ArrayList<ElementoRed> lista;
    protected WifiAdapter adaptador;
    //Wifi
    protected WifiManager wifiManager;
    protected WifiScanReceiver mWifiScanResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_escaneadas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lvRedes = findViewById(R.id.listView);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getApplicationContext(), "Click en la posición "  + position, Toast.LENGTH_SHORT).show();
            }
        });
        lista = new ArrayList<ElementoRed>();

        adaptador = new WifiAdapter(getApplicationContext(), lista);
        lvRedes.setAdapter(adaptador);
        btn=findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ScanResult> redes=getWifi();
                lista.clear();
                adaptador.notifyDataSetChanged();
                for(ScanResult e: redes){
                    lista.add(new ElementoRed(e.SSID,e.level+""));
                    adaptador.notifyDataSetChanged();
                }

            }
        });
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiScanResultReceiver= new WifiScanReceiver();
    }

    private List<ScanResult> getWifi() {

        List<ScanResult> resultados;
        IntentFilter scanIntent = new IntentFilter();
        scanIntent.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(mWifiScanResultReceiver, scanIntent);

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
        }
        if (!wifiManager.isWifiEnabled() && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            wifiManager.setWifiEnabled(true);
        }

        LocationManager lm= (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Toast.makeText(getApplicationContext(),"Por favor, active la ubicacion", Toast.LENGTH_LONG).show();
        if(wifiManager.startScan()){
            resultados= wifiManager.getScanResults();
        }
        else
            resultados= new ArrayList<>();



        return resultados;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0x12345) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            getWifi();
        }
    }

}
