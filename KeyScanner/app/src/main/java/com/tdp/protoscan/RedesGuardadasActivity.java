package com.tdp.protoscan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class RedesGuardadasActivity extends AppCompatActivity {

    protected ArrayList<ElementoRed> lista;
    protected WifiAdapter adaptador;
    private ListView lvRedes;

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

        testearLista();

    }

    private void testearLista() {

        lista.add(new ElementoRed("lalala","123"));
        adaptador.notifyDataSetChanged();

    }


}
