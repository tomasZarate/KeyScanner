package com.tdp.protoscan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.tdp.protoscan.database.FavsNetworksDB;
import com.tdp.protoscan.database.WifiNetworkContract;
import com.tdp.protoscan.database.WifiNetworksDB;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RedesFavoritasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RedesFavoritasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RedesFavoritasFragment extends Fragment {


    protected ArrayList<ElementoBBDD> listaBBDD; //Lista con password
    protected DataBaseAdapter adaptador;
    private ListView lvRedes;
    private ElementoBBDD redActual;
    private static final int RC_PATRON = 8001;

    //Base de datosx|
    protected FavsNetworksDB mDbHelper;
    protected SQLiteDatabase db;

    //QR
    private final static int QRcodeWidth = 500 ;

    private ImageView imagenqr;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RedesFavoritasFragment() {
        // Required empty public constructor
    }

    public static RedesFavoritasFragment newInstance() {
        RedesFavoritasFragment fragment = new RedesFavoritasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redes_favoritas, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        lvRedes = getActivity().findViewById(R.id.listViewRF);
        lvRedes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                redActual = listaBBDD.get(position);
                Intent intent = new Intent(getContext(),PatronActivity.class);
                startActivityForResult(intent,RC_PATRON);

            }
        });
        listaBBDD = new ArrayList<>();
        adaptador = new DataBaseAdapter(getActivity().getApplicationContext(), listaBBDD);
        lvRedes.setAdapter(adaptador);

        mDbHelper = new FavsNetworksDB(getActivity().getApplicationContext());

        imagenqr= getActivity().findViewById(R.id.qrImage);
        cargarRedesFavoritas();
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
        String [] items={"Generar QR","Compartir contraseña","Eliminar de favoritos"};
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
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void cargarRedesFavoritas() {

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
        listaBBDD.clear();
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

    public void eliminarRed() {
        db = mDbHelper.getWritableDatabase();
        try{
            db.delete(WifiNetworkContract.FeedEntry.TABLE_NAME,
                    " title = ?",
                    new String[] { String.valueOf (redActual.getNombre()) });
            db.close();

        }catch(Exception ex){}

        cargarRedesFavoritas();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
