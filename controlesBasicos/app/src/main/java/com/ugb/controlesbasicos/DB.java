package com.ugb.controlesbasicos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private static final String dbname = "amigos";
    private static final int v =1;
    private static final String SQLdb = "CREATE TABLE amigos(id text, rev text, idAmigo text, " +
            "nombre text, direccion text, telefono text, email text, dui text, foto text, actualizado text)";
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, v);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLdb);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //cambiar estructura de la BD
    }
    public String administrar_amigos(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "";
            if( accion.equals("nuevo") ){
                sql = "INSERT INTO amigos(id,rev,idAmigo,nombre,direccion,telefono,email,dui,foto,actualizado) VALUES('"+ datos[0] +"','"+ datos[1] +"','"+ datos[2] +"', '"+
                        datos[3] +"', '"+ datos[4] +"','"+ datos[5] +"','"+ datos[6] +"', '"+ datos[7] +"', '"+ datos[8] +"', '"+ datos[9] +"' )";
            } else if (accion.equals("modificar")) {
                sql = "UPDATE amigos SET id='"+ datos[0] +"',rev='"+ datos[1] +"',nombre='"+ datos[3] +"', direccion='"+ datos[4] +"', telefono='"+ datos[5] +"', email=" +
                        "'"+ datos[6] +"', dui='"+ datos[7] +"', foto='"+ datos[8] +"', actualizado='"+ datos[9] +"' WHERE idAmigo='"+ datos[2] +"'";
            } else if (accion.equals("eliminar")) {
                sql = "DELETE FROM amigos WHERE idAmigo='"+ datos[2] +"'";
            }
            db.execSQL(sql);
            return "ok";
        }catch (Exception e){
            return e.getMessage();
        }
    }
    public Cursor obtener_amigos(){
        Cursor cursor;
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM amigos ORDER BY nombre", null);
        return cursor;
    }
    public Cursor pendientesActualizar(){
        Cursor cursor;
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM amigos WHERE actualizado='no'", null);
        return cursor;
    }
}
