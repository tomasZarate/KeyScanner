package com.tdp.protoscan;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.tdp.protoscan.database.WifiNetworkContract;
import com.tdp.protoscan.database.WifiNetworksDB;

import java.util.ArrayList;

import io.paperdb.Paper;

public class RedesGuardadasFragment extends Fragment {

    protected Button btnCambiarPatron;

    protected ArrayList<ElementoBBDD> listaBBDD; //Lista con password
    protected DataBaseAdapter adaptador;
    private ListView lvRedes;
    private ElementoBBDD redActual;
    private static final int RC_PATRON = 8001;

    //Base de datos
    protected WifiNetworksDB mDbHelper;
    protected SQLiteDatabase db;

    //QR
    private final static int QRcodeWidth = 500 ;

    private ImageView imagenqr;

    public RedesGuardadasFragment() {
    }

    public static RedesGuardadasFragment newInstance() {
        RedesGuardadasFragment fragment = new RedesGuardadasFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_redes_guardadas, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        lvRedes = getActivity().findViewById(R.id.listViewRG);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                redActual = listaBBDD.get(position);
                Intent intent = new Intent(getContext(),PatronActivity.class);
                startActivityForResult(intent,RC_PATRON);
                //seleccionarOpcion();

            }
        });
        listaBBDD = new ArrayList<>();
        adaptador = new DataBaseAdapter(getActivity().getApplicationContext(), listaBBDD);
        lvRedes.setAdapter(adaptador);

        mDbHelper = new WifiNetworksDB(getActivity().getApplicationContext());

        imagenqr= getActivity().findViewById(R.id.qrImage);
        cargarRedes();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RC_PATRON:

                if(data!=null) {
                    int resultadoPatron = data.getExtras().getInt("resultado");
                    if (resultadoPatron == 1)
                        seleccionarOpcion();
                }
        }
    }

    private void seleccionarOpcion() {

        String [] items={"Generar QR","Compartir contraseña","Eliminar red"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        mostrarPassword();
                        break;
                    case 2:
                        eliminarRed();
                }
            }
        });
        builder.create().show();

    }

    private void eliminarRed() {

        //Aca va todo

        
        adaptador.notifyDataSetChanged(); //Actualiza el view
    }

    private void mostrarPassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Contraseña: "+redActual.getNombre());
        builder.setMessage(redActual.getPassword());
        builder.show();

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
