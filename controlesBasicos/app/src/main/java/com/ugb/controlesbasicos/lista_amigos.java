package com.ugb.controlesbasicos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class lista_amigos extends AppCompatActivity {

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
    }
    private void abrirActividad(){
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(abrirActividad);
    }
}