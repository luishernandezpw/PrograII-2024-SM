package com.ugb.controlesbasicos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    Button btn;
    FloatingActionButton fab;
    TextView tempVal;
    String accion = "nuevo";
    String id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fabListarAmigos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActividad();
            }
        });
        btn = findViewById(R.id.btnGuardarAgendaAmigos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tempVal = findViewById(R.id.txtnombre);
                    String nombre = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtdireccion);
                    String direccion = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtTelefono);
                    String tel = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtemail);
                    String email = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtdui);
                    String dui = tempVal.getText().toString();

                    DB db = new DB(getApplicationContext(), "",null, 1);
                    String[] datos = new String[]{id,nombre,direccion,tel,email,dui};
                    String respuesta = db.administrar_amigos(accion, datos);
                    if(respuesta.equals("ok")){
                        Toast.makeText(getApplicationContext(), "Amigo guardado con exito", Toast.LENGTH_LONG).show();
                        abrirActividad();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error al intentar guardar el amigo: "+ respuesta, Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void abrirActividad(){
        Intent abrirActividad = new Intent(getApplicationContext(), lista_amigos.class);
        startActivity(abrirActividad);
    }
}