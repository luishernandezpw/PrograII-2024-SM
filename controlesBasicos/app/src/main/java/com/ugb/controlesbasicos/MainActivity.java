package com.ugb.controlesbasicos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btn;
    FloatingActionButton fab;
    TextView tempVal;
    String accion = "nuevo";
    String id="", rev="", idAmigo="";
    String urlCompletaFoto;
    String getUrlCompletaFotoFirestore;
    Intent tomarFotoIntent;
    ImageView img;
    utilidades utls;
    detectarInternet di;
    String miToken = "";
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        di = new detectarInternet(getApplicationContext());
        utls = new utilidades();
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
                subirFotoFirestore();
            }
        });
        img = findViewById(R.id.btnImgAmigo);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotoAmigo();
            }
        });
        obtenerToken();
        mostrarDatosAmigos();
    }
    private void subirFotoFirestore(){
        mostrarMsg("Subiendo Foto...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(urlCompletaFoto));
        final StorageReference reference = storageReference.child("foto/"+file.getLastPathSegment());

        final UploadTask tareaSubir = reference.putFile(file);
        tareaSubir.addOnFailureListener(e->{
            mostrarMsg("Error al subir la foto: "+ e.getMessage());
        });
        tareaSubir.addOnSuccessListener(tareaInstantanea->{
            mostrarMsg("Foto subida con exito.");
            Task<Uri> descargarUri = tareaSubir.continueWithTask(tarea->reference.getDownloadUrl()).addOnCompleteListener(tarea->{
                if( tarea.isSuccessful() ){
                    getUrlCompletaFotoFirestore = tarea.getResult().toString();
                    guardarAmigo();
                }else{
                    mostrarMsg("Error al descargar la ruta de la imagen");
                }
            });
        });
    }
    private void obtenerToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if( !task.isSuccessful() ){
                return;
            }
            miToken = task.getResult();
        });
    }
    private void guardarAmigo(){
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

            databaseReference = FirebaseDatabase.getInstance().getReference("amigos");
            String key = databaseReference.push().getKey();

            if(miToken.equals("") || miToken==null){
                obtenerToken();
            }
            if( miToken!=null && miToken!="" ){
                amigos amigo = new amigos(idAmigo,nombre,direccion,tel,email,dui,urlCompletaFoto,getUrlCompletaFotoFirestore,miToken);
                if(key!=null){
                    databaseReference.child(key).setValue(amigo).addOnSuccessListener(aVoid->{
                        mostrarMsg("Amigo registrado con exito.");
                        abrirActividad();
                    });
                }else{
                    mostrarMsg("Error nose pudo guardar en la base de datos");
                }
            }else {
                mostrarMsg("Tu dispositivo no soporta la aplicacion");
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void tomarFotoAmigo(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoAmigo = null;
        try{
            fotoAmigo = crearImagenAmigo();
            if( fotoAmigo!=null ){
                Uri uriFotoamigo = FileProvider.getUriForFile(MainActivity.this,
                        "com.ugb.controlesbasicos.fileprovider", fotoAmigo);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoamigo);
                startActivityForResult(tomarFotoIntent, 1);
            }else{
                mostrarMsg("No se pudo creaar la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al abrir la camara: "+ e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==1 && resultCode==RESULT_OK){
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imageBitmap);
            }else{
                mostrarMsg("El usuario cancelo la toma de la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error añ obtener la foto de la camara");
        }
    }
    private File crearImagenAmigo() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
            fileName = "imagen_"+ fechaHoraMs +"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( dirAlmacenamiento.exists()==false ){
            dirAlmacenamiento.mkdirs();
        }
        File imagen = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = imagen.getAbsolutePath();
        return imagen;
    }
    private void mostrarDatosAmigos(){
        try{
            Bundle parametros = getIntent().getExtras();//Recibir los parametros...
            accion = parametros.getString("accion");

            if(accion.equals("modificar")){
                JSONObject jsonObject = new JSONObject(parametros.getString("amigos")).getJSONObject("value");
                id = jsonObject.getString("_id");
                rev = jsonObject.getString("_rev");
                idAmigo = jsonObject.getString("idAmigo");

                tempVal = findViewById(R.id.txtnombre);
                tempVal.setText(jsonObject.getString("nombre"));

                tempVal = findViewById(R.id.txtdireccion);
                tempVal.setText(jsonObject.getString("direccion"));

                tempVal = findViewById(R.id.txtTelefono);
                tempVal.setText(jsonObject.getString("telefono"));

                tempVal = findViewById(R.id.txtemail);
                tempVal.setText(jsonObject.getString("email"));

                tempVal = findViewById(R.id.txtdui);
                tempVal.setText(jsonObject.getString("dui"));

                urlCompletaFoto = jsonObject.getString("urlCompletaFoto");
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imageBitmap);
            }else{//nuevo registro
                idAmigo = utls.generarIdUnico();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+ e.getMessage());
        }
    }
    private void mostrarMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    private void abrirActividad(){
        Intent abrirActividad = new Intent(getApplicationContext(), lista_amigos.class);
        startActivity(abrirActividad);
    }
}