package com.tdp.protoscan;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.tdp.protoscan.OCR.OcrCaptureActivity;
import com.tdp.protoscan.database.WifiNetworkContract;
import com.tdp.protoscan.database.WifiNetworksDB;

import java.util.ArrayList;
import java.util.List;

public class RedesEscaneadasActivity extends AppCompatActivity{

    //Views
    protected Button btn;
    protected ListView lvRedes;

    //Wifi
    protected WifiManager wifiManager;
    protected WifiScanReceiver mWifiScanResultReceiver;
    protected ArrayList<ElementoRed> listaRedes;
    protected WifiAdapter adaptador;

    //Base de datos
    protected WifiNetworksDB mDbHelper;
    protected SQLiteDatabase db;
    //Constantes
    private static final int RC_OCR_CAPTURE = 9003;
    private static final int RC_QR_CAPTURE = 9004;
    private static final int RC_EDITOR = 9005;
    private String redActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_escaneadas);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lvRedes = findViewById(R.id.listViewRE);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                redActual=listaRedes.get(position).getNombreRed();
                seleccionarEntrada();
            }
        });
        listaRedes = new ArrayList<>();

        adaptador = new WifiAdapter(getApplicationContext(), listaRedes);
        lvRedes.setAdapter(adaptador);
        btn=findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ScanResult> redes=getWifi();
                listaRedes.clear();
                adaptador.notifyDataSetChanged();
                for(ScanResult e: redes){
                    listaRedes.add(new ElementoRed(e.SSID,"Intensidad: "+e.level));
                    adaptador.notifyDataSetChanged();
                }

            }
        });
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiScanResultReceiver= new WifiScanReceiver();

        mDbHelper = new WifiNetworksDB(getApplicationContext());


    }

    private List<ScanResult> getWifi() {

        List<ScanResult> resultados=new ArrayList<>();;
        IntentFilter scanIntent = new IntentFilter();
        scanIntent.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(mWifiScanResultReceiver, scanIntent);

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
        }
        else{
            if(wifiManager.startScan()){
                resultados = wifiManager.getScanResults();
            }
        }
        if (!wifiManager.isWifiEnabled() && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            wifiManager.setWifiEnabled(true);
        }

        LocationManager lm= (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (lm != null && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "Por favor, active la ubicacion", Toast.LENGTH_LONG).show();
        }

        return resultados;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (requestCode == 0x12345) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            getWifi();
        }
        if(requestCode==0x1333){
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
    }

    private void seleccionarEntrada(){
        String [] items={"Escanear imagen","Escanear QR","Ingresar manualmante"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tipo de conexion");
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
                        ingresarPassword("");
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void ingresarPassword(String input) {

        Intent intent=new Intent(getApplicationContext(),EditarPasswordActivity.class);
        intent.putExtra("password",input);
        startActivityForResult(intent,RC_EDITOR);

    }

    private void lanzarOCR(){

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0x1333);
        }
        Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, true);
        intent.putExtra(OcrCaptureActivity.UseFlash, false);
        startActivityForResult(intent, RC_OCR_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode){
            case RC_OCR_CAPTURE:
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null && redActual!=null) {
                        //Cuadro de texto
                        final String password = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                        String [] items={"Confirmar","Editar","Volver a escanear"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(password);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // Do anything you want here
                                //El parametro witch indica que opción se tocó (Usar un case)
                                switch(which){
                                    case 0:
                                        //Confirmar
                                        //statusMessage.setText(R.string.ocr_success);
                                        //textValue.setText(text);
                                        if(conectarRed(redActual,password))
                                            Toast.makeText(getApplicationContext(),"Conexion exitosa", Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(getApplicationContext(),"Fallo la conexion", Toast.LENGTH_LONG).show();
                                        break;
                                    case 1:
                                        //Editar
                                        ingresarPassword(password);
                                        break;
                                    case 2:
                                        //Volver a escanear
                                        lanzarOCR();
                                        break;
                                }
                            }
                        });
                        builder.create().show();

                    }
                }
                break;
            case RC_QR_CAPTURE:
                //Completar
                break;

            case RC_EDITOR:
                if(data!=null){
                    String resultado = data.getStringExtra("resultado");
                    if (!resultado.equals("")){
                        if(conectarRed(redActual,resultado))
                            Toast.makeText(getApplicationContext(),"Conexion exitosa", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(),"Fallo la conexion", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No se conectó", Toast.LENGTH_LONG).show(); }
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
                if(!existeEnBD(ssid))
                    agregar(ssid, pass);
                return true;
            }
        }
        return false;
    }


    //Metodo para agregar a la base de datos
    public void agregar(String nombre, String pass){
        db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE, nombre);
        values.put(WifiNetworkContract.FeedEntry.COLUMN_NAME_SUBTITLE, pass);

        long id = db.insert(WifiNetworkContract.FeedEntry.TABLE_NAME, null, values); //-1 si hubo error en insertar
        db.close();

    }

    public boolean existeEnBD(String key){

        db = mDbHelper.getReadableDatabase();

        String[] projection = {WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE, WifiNetworkContract.FeedEntry.COLUMN_NAME_SUBTITLE};
        String selection = WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";

        Cursor cursor = db.query(WifiNetworkContract.FeedEntry.TABLE_NAME,
                projection,
                null,//selection,
                null,
                null,
                null,
                null,
                null);

        boolean encontre = false;

        while(cursor.moveToNext() && !encontre) {
            String item = cursor.getString(
                    cursor.getColumnIndexOrThrow(WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE));
            if(item.equals(key)) encontre = true;
        }

        db.close();
        return encontre;
    }
}