package com.ugb.controlesbasicos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnCalcular);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempVal = findViewById(R.id.txtnum1);
                double num1 = Double.parseDouble(tempVal.getText().toString());

                tempVal = findViewById(R.id.txtnum2);
                double num2 = Double.parseDouble(tempVal.getText().toString());

                double respuesta = num1 + num2;

                tempVal = findViewById(R.id.lblrespuesta);
                tempVal.setText("Respuesta: "+ respuesta);
            }
        });
    }
}