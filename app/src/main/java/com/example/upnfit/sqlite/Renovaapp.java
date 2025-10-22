package com.example.upnfit.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class Renovaapp extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "renovaapp.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USUARIO = "Usuario";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_CORREO = "correo";
    private static final String COLUMN_CLAVE = "clave";
    private static final String COLUMN_GENERO = "genero";
    private static final String COLUMN_ALTURA = "altura";
    private static final String COLUMN_PESO = "peso";

    public Renovaapp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_USUARIO = "CREATE TABLE " + TABLE_USUARIO + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOMBRE + " TEXT NOT NULL, "
            + COLUMN_CORREO + " TEXT NOT NULL UNIQUE, "
            + COLUMN_CLAVE + " TEXT NOT NULL, "
            + COLUMN_GENERO + " TEXT, "
            + COLUMN_ALTURA + " REAL, "
            + COLUMN_PESO + " REAL"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

    public boolean agregarUsuarioCompleto(String nombre, String correo, String clave,
                                          String genero, float altura, float peso) {
        if (existeCorreo(correo)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_NOMBRE, nombre);
        valores.put(COLUMN_CORREO, correo);
        valores.put(COLUMN_CLAVE, clave);
        valores.put(COLUMN_GENERO, genero);
        valores.put(COLUMN_ALTURA, altura);
        valores.put(COLUMN_PESO, peso);

        long resultado = db.insert(TABLE_USUARIO, null, valores);
        db.close();
        return resultado != -1;
    }

    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIO,
                new String[]{COLUMN_ID},
                COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public boolean validarLogin(String correo, String clave) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIO,
                new String[]{COLUMN_ID},
                COLUMN_CORREO + " = ? AND " + COLUMN_CLAVE + " = ?",
                new String[]{correo, clave},
                null, null, null);
        boolean valido = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return valido;
    }

    public String obtenerNombrePorCorreo(String correo) {
        String nombre = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIO,
                new String[]{COLUMN_NOMBRE},
                COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
        }
        cursor.close();
        db.close();
        return nombre;
    }
}
