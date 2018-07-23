package com.tdp.protoscan;

import android.support.v7.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import io.paperdb.Paper;


public class CrearPatronActivity extends AppCompatActivity {

    String save_pattern_key = "pattern_code";
    String final_pattern = "";
    PatternLockView mPatternLockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_patron);
        Paper.init(this);
        final String save_pattern = Paper.book().read(save_pattern_key);

        if(save_pattern != null && !save_pattern.equals("null"))
        {
            setContentView(R.layout.activity_verificar_patron); //actividad para confirmar el patron
            mPatternLockView = findViewById(R.id.pattern_lock_view);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView,pattern);
                    if(final_pattern.equals(save_pattern)){
                        Toast.makeText(CrearPatronActivity.this, "Password Correct!", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(CrearPatronActivity.this,ActividadDesbloqueada.class); //Aca iria el cuadro flotante
                        //startActivity(intent);


                    }else{ Toast.makeText(CrearPatronActivity.this, "Password Incorrect!", Toast.LENGTH_SHORT).show();}


                }

                @Override
                public void onCleared() {

                }
            });
        }
        else
        {
            setContentView(R.layout.activity_crear_patron);
            mPatternLockView = findViewById(R.id.pattern_lock_view);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView,pattern);

                }

                @Override
                public void onCleared() {

                }
            });




            Button btnSetup = (Button)findViewById(R.id.btnSetearPatron);
            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paper.book().write(save_pattern_key, final_pattern);
                    Toast.makeText(CrearPatronActivity.this, "Save pattern okay!", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(CrearPatronActivity.this,Principal.class); //clase de la activity para confirmar patron
                    //startActivity(intent);
                }
            });
        }
    }
}
