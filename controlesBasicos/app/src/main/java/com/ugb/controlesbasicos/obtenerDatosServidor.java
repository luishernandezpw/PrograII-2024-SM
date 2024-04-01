package com.ugb.controlesbasicos;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class obtenerDatosServidor extends AsyncTask<String, String, String> {
    HttpURLConnection httpURLConnection;
    @Override
    protected String doInBackground(String... strings) {
        StringBuilder respuesta = new StringBuilder();
        try{
            URL url = new URL(utilidades.urlConsulta);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", "Basic "+ utilidades.credencialesCodificadas );

            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String linea;
            while((linea=reader.readLine())!=null){
                respuesta.append(linea);
            }
        }catch (Exception e){
            return "Error al conectarse al servidor: "+ e.getMessage();
        }
        return respuesta.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
