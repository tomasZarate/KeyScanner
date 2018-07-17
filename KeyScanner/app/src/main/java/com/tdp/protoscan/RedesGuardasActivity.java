package com.tdp.protoscan;

import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tdp.protoscan.database.WifiNetworksDB;

import java.util.ArrayList;
import java.util.List;

public class RedesGuardasActivity extends AppCompatActivity {

    protected Toolbar myToolbar;
    private ListView lvRedesGuardadas;
    protected ArrayList<ElementoRed> lista;
    protected WifiAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_guardas);
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        lista = new ArrayList<>();
        adaptador = new WifiAdapter(getApplicationContext(), lista);

        lvRedesGuardadas = findViewById(R.id.listViewRG);
        lvRedesGuardadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Opciones de red al tocar

            }
        });

        WifiNetworksDB mDbHelper = new WifiNetworksDB(getApplicationContext());
        mDbHelper.getReadableDatabase();

        cargarRedes();
    }

    private void cargarRedes() {
        testearLista();
        //Cargar redes desde la base de datos


        List<ElementoRed> redes; //acá se carga lo tomado de la base de datos
        //Para cada elemento de la base de datos, crear un ElementoRed(SSID,password) e insertarlo en la lista redesx

        adaptador.notifyDataSetChanged(); //Actualiza la lista visible con lo cargado arriba
    }

    private void testearLista() {

        lista.add(new ElementoRed("lalala","123"));

    }

}
