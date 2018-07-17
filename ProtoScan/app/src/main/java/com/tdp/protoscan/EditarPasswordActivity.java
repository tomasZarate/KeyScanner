package com.tdp.protoscan;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditarPasswordActivity extends AppCompatActivity {

    private EditText editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_password);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editor= findViewById(R.id.textEditInput);
        editor.setText(getIntent().getStringExtra("password"));

        Button cancelar=findViewById(R.id.cancelarEdit);
        Button aceptar=findViewById(R.id.aceptarEdit);


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().putExtra("resultado","");
                finish();
            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("resultado",editor.getText().toString());
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }
}