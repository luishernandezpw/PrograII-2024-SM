package com.ugb.controlesbasicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import kotlin.contracts.Returns;

public class lista_amigos extends AppCompatActivity {
    Bundle paramatros = new Bundle();
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
                paramatros.putString("accion","nuevo");
                abrirActividad(paramatros);
            }
        });
        obtenerAmigos();
        buscarAmigos();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        cAmigos.moveToPosition(info.position);
        menu.setHeaderTitle("Que deseas hacer con "+ cAmigos.getString(1));//1 es el campo nombre
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try{
            switch (item.getItemId()){
                case R.id.mnxAgregar:
                    paramatros.putString("accion", "nuevo");
                    abrirActividad(paramatros);
                    break;
                case R.id.mnxModificar:
                    String[] amigos = {
                            cAmigos.getString(0), //idAmigo
                            cAmigos.getString(1), //nombre
                            cAmigos.getString(2), //direccion
                            cAmigos.getString(3), //telefono
                            cAmigos.getString(4), //email
                            cAmigos.getString(5), //dui
                    };
                    paramatros.putString("accion", "modificar");
                    paramatros.putStringArray("amigos", amigos);
                    abrirActividad(paramatros);
                    break;
                case R.id.mnxEliminar:
                    eliminarAmigo();
                    break;
            }
            return true;
        }catch (Exception e){
            mostrarMsg("Error al seleccionar el item: "+ e.getMessage());
            return super.onContextItemSelected(item);
        }
    }
    private void eliminarAmigo(){
        try {
            AlertDialog.Builder confirmar = new AlertDialog.Builder(lista_amigos.this);
            confirmar.setTitle("Esta seguro de eliinar a: ");
            confirmar.setMessage(cAmigos.getString(1));
            confirmar.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String respuesta = db.administrar_amigos("eliminar", new String[]{cAmigos.getString(0)});
                    if( respuesta.equals("ok") ){
                        mostrarMsg("Amigo eliminado con exito");
                        obtenerAmigos();
                    }else{
                        mostrarMsg("Error al eliminar el amigo: "+ respuesta);
                    }
                }
            });
            confirmar.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            confirmar.create().show();
        }catch (Exception e){
            mostrarMsg("Error al eliminar: "+ e.getMessage());
        }
    }
    private void buscarAmigos(){
        TextView tempVal = findViewById(R.id.txtBuscarAmigos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    alAmigos.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if( valor.length()<=0 ){
                        alAmigos.addAll(alAmigosCopy);
                    }else{
                        for (amigos amigo : alAmigosCopy){
                            String nombre = amigo.getNombre();
                            String direccion = amigo.getDireccion();
                            String tel = amigo.getTelefono();
                            String email = amigo.getEmail();
                            if( nombre.trim().toLowerCase().contains(valor) ||
                                direccion.trim().toLowerCase().contains(valor) ||
                                tel.trim().contains(valor) ||
                                email.trim().toLowerCase().contains(valor)){
                                alAmigos.add(amigo);
                            }
                        }
                        adaptadorImagenes adImagenes=new adaptadorImagenes(getApplicationContext(), alAmigos);
                        lts.setAdapter(adImagenes);
                    }
                }catch (Exception e){
                    mostrarMsg("Error al buscar: "+ e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void abrirActividad(Bundle parametros){
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        abrirActividad.putExtras(parametros);
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

                registerForContextMenu(lts);
            }else {
                paramatros.putString("accion", "nuevo");
                abrirActividad(paramatros);
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