package com.tdp.protoscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tomi on 22/5/2018.
 */

public class WifiAdapter extends ArrayAdapter{

    private Context context;
    private ArrayList<ElementoRed> datos;

    public WifiAdapter(@NonNull Context context, ArrayList<ElementoRed> datos) {
        super(context, R.layout.layout_item_listview, datos);

        this.context = context;
        this.datos = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // En primer lugar "inflamos" una nueva vista, que será la que se
        // mostrará en la celda del ListView. Para ello primero creamos el
        // inflater, y después inflamos la vista.
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.layout_item_listview, null);

        // A partir de la vista, recogeremos los controles que contiene para
        // poder manipularlos.

        // Recogemos el TextView para mostrar el nombre y establecemos el
        // nombre.
        TextView nombre = (TextView) item.findViewById(R.id.ssidField);
        nombre.setText(datos.get(position).getNombreRed());

        // Recogemos el TextView para mostrar el número de celda y lo
        // establecemos.
        TextView seguridad = (TextView) item.findViewById(R.id.signalContent);
        seguridad.setText(datos.get(position).getSignal());

        // Devolvemos la vista para que se muestre en el ListView.
        return item;
    }
}

