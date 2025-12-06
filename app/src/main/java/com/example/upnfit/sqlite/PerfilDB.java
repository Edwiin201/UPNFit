package com.example.upnfit.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PerfilDB extends SQLiteOpenHelper {

    //  Constantes de la Base de Datos
    private static final String DATABASE_NAME = "upnfit_perfil.db";
    private static final int DATABASE_VERSION = 1;

    //  Constantes de la Tabla de Perfil
    public static final String TABLE_PERFIL = "datos_perfil";
    public static final String COL_ID = "id";
    public static final String COL_USUARIO_ID = "usuario_id"; // Clave
    public static final String COL_NOMBRE = "nombre_completo";
    public static final String COL_SEDE = "sede_id";
    public static final String COL_GENERO = "genero";
    public static final String COL_EDAD = "edad";
    public static final String COL_ALTURA = "altura_cm";
    public static final String COL_PESO = "peso_kg";

    // 📝 SQL para crear la tabla
    private static final String CREATE_TABLE_PERFIL =
            "CREATE TABLE " + TABLE_PERFIL + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USUARIO_ID + " INTEGER UNIQUE, " + // UNIQUE: Solo un registro por usuario
                    COL_NOMBRE + " TEXT, " +
                    COL_SEDE + " TEXT, " +
                    COL_GENERO + " TEXT, " +
                    COL_EDAD + " INTEGER, " +
                    COL_ALTURA + " REAL, " +
                    COL_PESO + " REAL);";

    // --- Constructor ---
    public PerfilDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // --- Métodos de Ciclo de Vida de la BD ---
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PERFIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERFIL);
        onCreate(db);
    }

    // --- MÉTODOS DE OPERACIÓN (CRUD) ---

    /**
     * Guarda o actualiza todos los datos del perfil para un usuario.
     */
    public void guardarPerfil(int usuarioID, String nombre, String sede, String genero,
                              int edad, double altura, double peso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USUARIO_ID, usuarioID);
        values.put(COL_NOMBRE, nombre);
        values.put(COL_SEDE, sede);
        values.put(COL_GENERO, genero);
        values.put(COL_EDAD, edad);
        values.put(COL_ALTURA, altura);
        values.put(COL_PESO, peso);

        // Intentar actualizar la fila existente (upsert)
        int rowsAffected = db.update(
                TABLE_PERFIL,
                values,
                COL_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioID)}
        );

        if (rowsAffected == 0) {
            db.insert(TABLE_PERFIL, null, values);
        }
        db.close();
    }

    /**
     * Obtiene todos los datos del perfil para un usuario.
     * @return Cursor con los datos o null si no hay datos.
     */
    public Cursor obtenerPerfil(int usuarioID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PERFIL,
                null, // Selecciona todas las columnas
                COL_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioID)},
                null, null, null, "1"
        );
        return cursor;
    }
}