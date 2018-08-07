package com.tdp.protoscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DataBaseAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<ElementoBBDD> datos;

    public DataBaseAdapter(@NonNull Context context, ArrayList<ElementoBBDD> datos) {
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
        View item = inflater.inflate(R.layout.layout_item_database, null);

        // A partir de la vista, recogeremos los controles que contiene para
        // poder manipularlos.

        // Recogemos el TextView para mostrar el nombre y establecemos el
        // nombre.
        TextView nombre = item.findViewById(R.id.nameField);
        nombre.setText(""+datos.get(position).getNombre());

        // Recogemos el TextView para mostrar el número de celda y lo
        // establecemos.
        /*
        TextView signal = item.findViewById(R.id.signalContent);
        signal.setText(datos.get(position).getSignal());
        */
        // Devolvemos la vista para que se muestre en el ListView.
        return item;
    }

}
