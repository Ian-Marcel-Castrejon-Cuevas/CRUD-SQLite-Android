package com.example.crudsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AyudanteBD extends SQLiteOpenHelper {

    String sentenciaCreacionSQL="CREATE TABLE productos (id varchar(5) primary key, nombre varchar(15), precio varchar(15), cat varchar(15))";
    public AyudanteBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sentenciaCreacionSQL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En la práctica deberemos migrar datos de la tabla antigua
        // a la nueva, por lo que este método deberá ser más elaborado.
        //Eliminamos la versión anterior de la tabla
        //db.execSQL("DROP TABLE IF EXISTS autos");
        //y luego creamos la nueva
        //db.execSQL(sqlCreate);
    }

}
