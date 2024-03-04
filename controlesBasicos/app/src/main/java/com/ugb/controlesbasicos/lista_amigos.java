package com.ugb.controlesbasicos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class lista_amigos extends AppCompatActivity {
    DB db;
    ListView lts;
    Cursor cAmigos;
    final ArrayList<amigos> alAmigos = new ArrayList<amigos>();
    final ArrayList<amigos> alAmigosCopy = new ArrayList<amigos>();
    amigos datosAmigos;
    FloatingActionButton btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_amigos);
        btn = findViewById(R.id.fabAgregarAmigos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActividad();
            }
        });
        obtenerAmigos();
    }
    private void abrirActividad(){
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(abrirActividad);
    }
    private void obtenerAmigos(){
        try{
            alAmigos.clear();
            alAmigosCopy.clear();

            db = new DB(getApplicationContext(),"", null, 1);
            cAmigos = db.obtener_amigos();
            if( cAmigos.moveToFirst() ){
                lts = findViewById(R.id.ltsAmigos);
                do{
                    datosAmigos = new amigos(
                            cAmigos.getString(0),//idAmigo
                            cAmigos.getString(1),//nombre
                            cAmigos.getString(2),//direccion
                            cAmigos.getString(3),//telefono
                            cAmigos.getString(4),//email
                            cAmigos.getString(5)//dui
                    );
                    alAmigos.add(datosAmigos);
                }while (cAmigos.moveToNext());
                alAmigosCopy.addAll(alAmigos);

                adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alAmigos);
                lts.setAdapter(adImagenes);

                //registerForContextMenu(lts);
            }else {
                abrirActividad();
                mostrarMsg("No hay Datos de amigos.");
            }
        }catch (Exception e){
            mostrarMsg("Error al obtener los amigos: "+ e.getMessage());
        }
    }
    private void mostrarMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}