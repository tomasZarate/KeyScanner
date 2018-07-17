package com.tdp.protoscan;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tdp.protoscan.database.WifiNetworksDB;

import java.util.ArrayList;

public class RedesGuardadasActivity extends AppCompatActivity {

    protected ArrayList<ElementoRed> lista;
    protected WifiAdapter adaptador;
    private ListView lvRedes;

    //Base de datos
    protected WifiNetworksDB mDbHelper;
    protected SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_guardadas);

        Toolbar toolbar=findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvRedes = findViewById(R.id.listViewRG);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });
        lista = new ArrayList<>();

        adaptador = new WifiAdapter(getApplicationContext(), lista);
        lvRedes.setAdapter(adaptador);

        mDbHelper = new WifiNetworksDB(getApplicationContext());



    }

    private void cargarRedes(){

        db = mDbHelper.getWritableDatabase();

        adaptador.notifyDataSetChanged();

    }

}
