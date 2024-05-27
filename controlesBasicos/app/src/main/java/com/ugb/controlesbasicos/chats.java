package com.ugb.controlesbasicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class chats extends AppCompatActivity {
    ImageView imgTemp;
    TextView tempVal;
    String to="", from="", user="", msg = "", urlPhoto = "", urlPhotoFirestore = "";
    DatabaseReference databaseReference;
    private chatsArrayAdapter chatArrayAdapter;
    TextView txtMsg;
    ListView ltsChats;
    Button btnEnviar;

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(MyFirebaseMessagingService.DISPLAY_MESSAGE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(notificacionPush, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificacionPush);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

        try {
            tempVal = findViewById(R.id.lblToChats);
            imgTemp = findViewById(R.id.imgAtras);
            imgTemp.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), lista_amigos.class);
                startActivity(intent);
            });
            Bundle parametros = getIntent().getExtras();
            if (parametros.getString("to") != null && parametros.getString("to") != "") {
                to = parametros.getString("to");
                from = parametros.getString("from");
                user = parametros.getString("nombre");
                urlPhoto = parametros.getString("urlCompletaFoto");
                urlPhotoFirestore = parametros.getString("urlFotoAmigoFirestore");
                tempVal.setText(user);
            }
            txtMsg = findViewById(R.id.txtMsg);
            txtMsg.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    try {
                        guardarMsgFirebsae(txtMsg.getText().toString());
                        sendChatMessage(false, txtMsg.getText().toString());
                    }catch (Exception e){
                        mostrarMsgToast(e.getMessage());
                    }
                }
                return false;
            });
            btnEnviar = findViewById(R.id.btnEnviar);
            btnEnviar.setOnClickListener(v -> {
                try {
                    guardarMsgFirebsae(txtMsg.getText().toString());
                    sendChatMessage(false, txtMsg.getText().toString());
                }catch (Exception e){
                    mostrarMsgToast(e.getMessage());
                }
            });
            ltsChats = findViewById(R.id.ltsChats);

            chatArrayAdapter = new chatsArrayAdapter(getApplicationContext(), R.layout.msgizquierdo);
            ltsChats.setAdapter(chatArrayAdapter);
        }catch (Exception e){
            mostrarMsgToast(e.getMessage());
        }
        mostrarFoto();
        historialMsg();
    }
    void sendChatMessage(Boolean posicion, String msg){
        try{
            chatArrayAdapter.add(new chatMessage(posicion, msg));
            txtMsg.setText("");
        }catch (Exception e){
            mostrarMsgToast("Error al enviar el mensje al chats: "+ e.getMessage());
        }
    }
    void guardarMsgFirebsae(String msg){
        try {
            JSONObject data = new JSONObject();
            data.put("para", to);
            data.put("de", from);
            data.put("msg", msg);
            data.put("user", user);

            JSONObject notificacion = new JSONObject();
            notificacion.put("title", "Mensaje de "+ user);
            notificacion.put("body", data);

            JSONObject miData = new JSONObject();
            miData.put("to", to);
            miData.put("notification", notificacion);
            miData.put("data", data);

            //enviar msj via webservice
            enviarDatos objEnviar = new enviarDatos();
            objEnviar.execute(miData.toString());

            //guardar en firebase
            chats_mensajes chatsMsg = new chats_mensajes(to, from, to + "_"+from, msg);
            String key = databaseReference.push().getKey();
            databaseReference.child(key).setValue(chatsMsg);
        }catch (Exception e){
            mostrarMsgToast("Error al intentar guardar el msg en Firebase: "+ e.getMessage());
        }
    }
    private class enviarDatos extends AsyncTask<String,String,String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();

            String JsonResponse = null;
            String JsonDATA = params[0];
            BufferedReader reader = null;

            try {
                //conexion al servidor...
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "key=AAAAw8k5WGY:APA91bFusYbYLzNPq2ZS63Y8elBq-6EhnmoqcdmEzlbaFFORzM7IVWC3PRpFCNTdeOQ0o5nFjQgTeTdPaisrdtRgi_A4RKyn761Qz7veiF466hYm8-ofSm2iA89-KALXS9rJcjsLReBp");

                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();

                // json data
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();
                return JsonResponse;

            }catch (Exception ex){
                ex.printStackTrace();
                Log.d("URI: ", "Error enviando notificacion: "+ ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if( s!=null ) {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("success") <= 0) {
                        mostrarMsgToast("Error al enviar mensaje");
                    }
                }
            }catch(Exception ex){
                mostrarMsgToast("Error al enviar a la red: "+ ex.getMessage());
            }
        }
    }
    void historialMsg(){
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.getChildrenCount()>0 ){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if( (dataSnapshot.child("de").getValue().equals(from) && dataSnapshot.child("para").getValue().equals(to))
                                || (dataSnapshot.child("de").getValue().equals(to) && dataSnapshot.child("para").getValue().equals(from))) {
                            sendChatMessage(dataSnapshot.child("para").getValue().equals(from), dataSnapshot.child("msg").getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private BroadcastReceiver notificacionPush = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WakeLocker.acquire(getApplicationContext());

            msg = intent.getStringExtra("msg");
            to = intent.getStringExtra("from");
            from = intent.getStringExtra("to");

            sendChatMessage(true, msg);
            WakeLocker.release();
        }
    };
    void mostrarFoto(){
        imgTemp = findViewById(R.id.imgPhotoChat);
        Glide.with(getApplicationContext()).load(urlPhotoFirestore).into(imgTemp);
    }
    private void mostrarMsgToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}