package com.tdp.protoscan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tdp.protoscan.database.WifiNetworksDB;

public class RedesGuardasActivity extends AppCompatActivity {

    protected Toolbar myToolbar;
    private ListView lvRedesGuardadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_guardas);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvRedesGuardadas = findViewById(R.id.listViewRE);
        lvRedesGuardadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


            }
        });

        WifiNetworksDB mDbHelper = new WifiNetworksDB(getApplicationContext());
        mDbHelper.getReadableDatabase();
    }

}
