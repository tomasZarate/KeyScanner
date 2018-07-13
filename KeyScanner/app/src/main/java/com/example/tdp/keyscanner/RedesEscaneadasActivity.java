package com.example.tdp.keyscanner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tdp.keyscanner.OCR.OcrCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;

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

    //Constantes
    private static final int RC_OCR_CAPTURE = 9003;
    private static final int RC_QR_CAPTURE = 9004;
    private String redActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_escaneadas);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lvRedes = findViewById(R.id.listView);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                redActual=lista.get(position).getNombreRed();
                seleccionarEntrada();
            }
        });
        lista = new ArrayList<>();

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

    private void seleccionarEntrada(){
        String [] items={"Escanear imagen","Escanear QR","Ingresar manualmante"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Titulo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do anything you want here
                //El parametro witch indica que opción se tocó (Usar un case)
                switch(which){
                    case 0:
                        lanzarOCR(); //Deberia retornar un String
                        break;
                    case 1:
                        //lanzarQR(); //Deberia retornar un String
                        break;
                    case 2:
                        //Colocar manualmente la contraseña
                        break;
                }
            }
        });
        builder.create().show();
        //tomar el String para un metodo conectarRed(String password)
    }

    private void lanzarOCR(){
        Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, true);
        intent.putExtra(OcrCaptureActivity.UseFlash, false);
        startActivityForResult(intent, RC_OCR_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case RC_OCR_CAPTURE:
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null && redActual!=null) {
                        String text="Conexion exitosa";
                        String password = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                        //statusMessage.setText(R.string.ocr_success);
                        //textValue.setText(text);
                        Log.d("MainActivity", "Text read: " + text);
                        if(conectarRed(redActual,password))
                            Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(),"Fallo la conexion", Toast.LENGTH_LONG).show();
                    } else {
                        //statusMessage.setText(R.string.ocr_failure);
                        Log.d("MainActivity", "No Text captured, intent data is null");
                    }
                } else {
                    // statusMessage.setText(String.format(getString(R.string.ocr_error), CommonStatusCodes.getStatusCodeString(resultCode)));
                }
                break;
            case RC_QR_CAPTURE:
                //Completar
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean conectarRed(String ssid,String pass){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        conf.preSharedKey="\""+ pass +"\"";
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                return true;
            }
        }
        return false;
    }
}
