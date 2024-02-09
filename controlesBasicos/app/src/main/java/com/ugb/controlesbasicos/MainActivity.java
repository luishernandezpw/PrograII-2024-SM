package com.ugb.controlesbasicos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TabHost tbh;
    TextView tempVal;
    Spinner spn;
    Button btn;
    conversores miObj = new conversores();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbh = findViewById(R.id.tbhConversores);
        tbh.setup();
        tbh.addTab(tbh.newTabSpec("LON").setContent(R.id.tabLongitud).setIndicator("LONGITUD",null));
        tbh.addTab(tbh.newTabSpec("ALM").setContent(R.id.tabAlmacenamiento).setIndicator("ALMACENAMIENTO",null));
        tbh.addTab(tbh.newTabSpec("MON").setContent(R.id.tabMonedas).setIndicator("MONEDAS",null));

        btn=findViewById(R.id.btnConvertirLongitud);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spn = findViewById(R.id.spnDeLongitud);
                int de = spn.getSelectedItemPosition();

                spn = findViewById(R.id.spnALongitud);
                int a = spn.getSelectedItemPosition();

                tempVal = findViewById(R.id.txtCantidadLongitud);
                double cantidad = Double.parseDouble(tempVal.getText().toString());

                double respuesta = miObj.convertir(0, de, a, cantidad);
                Toast.makeText(getApplicationContext(), "Respuesta: "+ respuesta, Toast.LENGTH_LONG).show();
            }
        });
    }
}
class conversores{
    double[][] valores ={
            {1, 100, 39.3701, 3.28084, 1.193, 1.09361, 0.001, 0.000621371},
            {}
    };
    public double convertir(int opcion, int de, int a, double cantidad){
        return valores[opcion][a]/valores[opcion][de]*cantidad;
    }
}