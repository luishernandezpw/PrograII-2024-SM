package com.ugb.controlesbasicos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class adaptadorImagenes extends BaseAdapter {
    Context context;
    ArrayList<amigos> datosAmigosArrayList;
    amigos datosAmigos;
    LayoutInflater layoutInflater;
    public adaptadorImagenes(Context context, ArrayList<amigos> datosAmigosArrayList) {
        this.context = context;
        this.datosAmigosArrayList = datosAmigosArrayList;
    }
    @Override
    public int getCount() {
        return datosAmigosArrayList.size();
    }
    @Override
    public Object getItem(int i) {
        return datosAmigosArrayList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return Long.parseLong(datosAmigosArrayList.get(i).getIdAmigo());
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        try{
            datosAmigos = datosAmigosArrayList.get(i);

            TextView tempVal = itemView.findViewById(R.id.lblnombre);
            tempVal.setText(datosAmigos.getNombre());

            tempVal = itemView.findViewById(R.id.lbltelefono);
            tempVal.setText(datosAmigos.getTelefono());

            tempVal = itemView.findViewById(R.id.lblemail);
            tempVal.setText(datosAmigos.getEmail());
        }catch (Exception e){
            Toast.makeText(context, "Error al mostrar los datos: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return itemView;
    }
}
