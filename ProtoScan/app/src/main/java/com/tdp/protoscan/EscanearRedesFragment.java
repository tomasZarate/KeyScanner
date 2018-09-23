package com.tdp.protoscan;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.tdp.protoscan.OCR.OcrCaptureActivity;
import com.tdp.protoscan.database.WifiNetworkContract;
import com.tdp.protoscan.database.WifiNetworksDB;

import java.util.ArrayList;
import java.util.List;

public class EscanearRedesFragment extends Fragment {

    //Views
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
    private static final int RC_CAMERA_PERMISSION = 1333;
    private static final int RC_LOCATION_PERMISSION = 1444;
    private String redActual;


    public EscanearRedesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EscanearRedesFragment newInstance() {
        EscanearRedesFragment fragment = new EscanearRedesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_escanear_redes, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvRedes = getActivity().findViewById(R.id.listViewRE);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                redActual=listaRedes.get(position).getNombreRed();
                seleccionarEntrada();
            }
        });
        listaRedes = new ArrayList<>();

        adaptador = new WifiAdapter(getActivity().getApplicationContext(), listaRedes);
        lvRedes.setAdapter(adaptador);
        FloatingActionButton btnScan = getActivity().findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarLista();
            }
        });
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiScanResultReceiver= new WifiScanReceiver();

        mDbHelper = new WifiNetworksDB(getActivity().getApplicationContext());

        actualizarLista();
        /*final Handler hand = new Handler();
        Runnable hilo = new Runnable() {
            @Override
            public void run() {
                actualizarLista();
                hand.postDelayed(this,1000);
            }
        };
        hand.postDelayed(hilo,1000);*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void actualizarLista() {
        List<ScanResult> redes=getWifi();
        listaRedes.clear();
        adaptador.notifyDataSetChanged();
        for(ScanResult e: redes){
            listaRedes.add(new ElementoRed(e.SSID,"Intensidad: "+calcularIntensidad(e.level) + " RSSI: "+e.level));
            adaptador.notifyDataSetChanged();
        }
    }

    private String calcularIntensidad(int i) {

        int numberOfLevels = 4;
        int level = WifiManager.calculateSignalLevel(i,numberOfLevels);
        level++;

        switch (level) {
            case 1:
                return "Baja";
            case 2:
                return "Regular";
            case 3:
                return "Buena";
            case 4:
                return "Excelente";
            default:
                return "";
        }
    }

    private List<ScanResult> getWifi() {

        List<ScanResult> resultados=new ArrayList<>();
        IntentFilter scanIntent = new IntentFilter();
        scanIntent.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(mWifiScanResultReceiver, scanIntent);

        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_PERMISSION);
        }
        else{
            if(wifiManager.startScan()){
                resultados = wifiManager.getScanResults();
            }
        }
        if (!wifiManager.isWifiEnabled() && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            wifiManager.setWifiEnabled(true);
        }

        LocationManager lm= (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (lm != null && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getContext(), "Por favor, active la ubicacion", Toast.LENGTH_LONG).show();
        }

        return resultados;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_LOCATION_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            getWifi();
        }
        if(requestCode==RC_CAMERA_PERMISSION){
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

        }
    }

    private void seleccionarEntrada(){
        String [] items={"Escanear imagen","Escanear QR","Ingresar manualmante"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        lanzarQR(); //Deberia retornar un String
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

        Intent intent=new Intent(getActivity(),EditarPasswordActivity.class);
        intent.putExtra("password",input);
        startActivityForResult(intent,RC_EDITOR);

    }

    private void lanzarQR() {

        Intent intent = new Intent(getActivity(),QRScanActivity.class);
        startActivityForResult(intent, RC_QR_CAPTURE);

    }

    private void lanzarOCR(){

        Intent intent = new Intent(getActivity(), OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, true);
        intent.putExtra(OcrCaptureActivity.UseFlash, false);
        startActivityForResult(intent, RC_OCR_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode){
            case RC_OCR_CAPTURE:
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null && redActual!=null) {
                        //Cuadro de texto
                        final String password = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                        String [] items={"Confirmar","Editar","Volver a escanear"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                            Toast.makeText(getActivity().getApplicationContext(),"Conexion exitosa", Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(getActivity().getApplicationContext(),"Fallo la conexion", Toast.LENGTH_LONG).show();
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
                if(data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    if(conectarRed(redActual,barcode.displayValue))
                        Toast.makeText(getActivity().getApplicationContext(),"Conexion exitosa", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity().getApplicationContext(),"Fallo la conexion", Toast.LENGTH_LONG).show();

                    break;
                }
                break;

            case RC_EDITOR:
                if(data!=null){
                    String resultado = data.getStringExtra("resultado");
                    if (!resultado.equals("")){
                        if(conectarRed(redActual,resultado))
                            Toast.makeText(getActivity().getApplicationContext(),"Conexion exitosa", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity().getApplicationContext(),"Fallo la conexion", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"No se conectó", Toast.LENGTH_LONG).show(); }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean conectarRed(String ssid,String pass){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        conf.preSharedKey="\""+ pass +"\"";
        int id= wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(id == i.networkId) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                boolean conecto = wifiManager.reconnect();
                if(conecto && i.status==0){

                    if(!existeEnBD(ssid)) {
                        agregar(ssid, pass);
                        actualizarPantalla();

                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void actualizarPantalla() {
        int orientacion =  ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        switch (orientacion) {
            case Surface.ROTATION_0:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
            case Surface.ROTATION_90:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
            case Surface.ROTATION_180:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

            case Surface.ROTATION_270:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
        }

    }


    //Metodo para agregar a la base de datos
    public void agregar(String nombre, String pass){
        db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE, nombre);
        values.put(WifiNetworkContract.FeedEntry.COLUMN_NAME_SUBTITLE, pass);

        db.insert(WifiNetworkContract.FeedEntry.TABLE_NAME, null, values); //-1 si hubo error en insertar
        db.close();

    }

    public boolean existeEnBD(String key){

        db = mDbHelper.getReadableDatabase();

        String[] projection = {WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE, WifiNetworkContract.FeedEntry.COLUMN_NAME_SUBTITLE};

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
