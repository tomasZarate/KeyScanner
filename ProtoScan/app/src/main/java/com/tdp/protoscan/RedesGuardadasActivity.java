package com.tdp.protoscan;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.tdp.protoscan.database.WifiNetworkContract;
import com.tdp.protoscan.database.WifiNetworksDB;

import java.util.ArrayList;

public class RedesGuardadasActivity extends AppCompatActivity {

    protected ArrayList<ElementoBBDD> listaBBDD; //Lista con password
    protected DataBaseAdapter adaptador;
    private ListView lvRedes;
    private ElementoBBDD redActual;

    //Base de datos
    protected WifiNetworksDB mDbHelper;
    protected SQLiteDatabase db;

    //QR
    private final static int QRcodeWidth = 500 ;

    private ImageView imagenqr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_guardadas);

        lvRedes = findViewById(R.id.listViewRG);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                redActual = listaBBDD.get(position);
                seleccionarOpcion();

            }
        });
        listaBBDD = new ArrayList<>();
        adaptador = new DataBaseAdapter(getApplicationContext(), listaBBDD);
        lvRedes.setAdapter(adaptador);

        mDbHelper = new WifiNetworksDB(getApplicationContext());

        imagenqr= findViewById(R.id.qrImage);
        cargarRedes();
    }

    private void seleccionarOpcion() {

        String [] items={"Generar QR","Compartir contraseña"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Compartir red");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do anything you want here
                //El parametro witch indica que opción se tocó (Usar un case)
                switch(which){
                    case 0:
                        generarQR();
                        break;
                    case 1:
                        //Compartir contraseña
                        break;
                }
            }
        });
        builder.create().show();

    }

    private void generarQR() {

        try {
            Bitmap bitmap;

            bitmap = TextToImageEncode(redActual.getPassword());

            imagenqr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void cargarRedes(){

        db = mDbHelper.getReadableDatabase();

        String[] projection = {WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE, WifiNetworkContract.FeedEntry.COLUMN_NAME_SUBTITLE};
        //String selection = WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";


        Cursor cursor = db.query(WifiNetworkContract.FeedEntry.TABLE_NAME,
                projection,
                null,//selection,
                null,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE));
            String pass = cursor.getString(
                    cursor.getColumnIndexOrThrow(WifiNetworkContract.FeedEntry.COLUMN_NAME_SUBTITLE));
            listaBBDD.add(new ElementoBBDD(name,pass));
        }

        cursor.close();
        adaptador.notifyDataSetChanged();
    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
