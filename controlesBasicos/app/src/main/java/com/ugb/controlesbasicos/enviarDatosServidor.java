package com.ugb.controlesbasicos;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class enviarDatosServidor extends AsyncTask<String, String, String> {
    Context context;
    String respuesta;
    HttpURLConnection httpURLConnection;
    public enviarDatosServidor(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... parametros) {
        String jsonRespuesta = null;
        String jsonDatos = parametros[0];
        BufferedReader bufferedReader;
        try{
            URL url = new URL(utilidades.urlMto);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Basic "+ utilidades.credencialesCodificadas );
            //envio de datos al servidor
            Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            writer.write(jsonDatos);
            writer.close();
            //recepcion de datos del server. Obtener datos.
            InputStream inputStream = httpURLConnection.getInputStream();
            if(inputStream==null) return null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            respuesta = bufferedReader.toString();
            //procesamos la respuesta
            String linea;
            StringBuffer stringBuffer = new StringBuffer();
            while((linea=bufferedReader.readLine())!=null){
                stringBuffer.append(linea+"\n");
            }
            if (stringBuffer.length()==0) return null;
            jsonRespuesta = stringBuffer.toString();
        }catch (Exception e){
            return "Error al conectarse al servidor: "+ e.getMessage();
        }
        return jsonRespuesta;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
