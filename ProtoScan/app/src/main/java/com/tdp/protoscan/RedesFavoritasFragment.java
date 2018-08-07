package com.tdp.protoscan;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

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
                //seleccionarOpcion();

            }
        });
        listaBBDD = new ArrayList<>();
        adaptador = new DataBaseAdapter(getActivity().getApplicationContext(), listaBBDD);
        lvRedes.setAdapter(adaptador);

        mDbHelper = new FavsNetworksDB(getActivity().getApplicationContext());

        imagenqr= getActivity().findViewById(R.id.qrImage);
        cargarRedesFavoritas();
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
