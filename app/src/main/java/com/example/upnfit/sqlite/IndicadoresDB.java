package com.example.upnfit.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IndicadoresDB extends SQLiteOpenHelper {

    // 📌 Constantes de la Base de Datos
    private static final String DATABASE_NAME = "upnfit_datos.db";
    private static final int DATABASE_VERSION = 1;

    // 📌 Constantes de la Tabla de Indicadores
    public static final String TABLE_INDICADORES = "indicadores_salud";
    public static final String COL_ID = "id";
    public static final String COL_USUARIO_ID = "usuario_id"; // Para enlazar con el usuario
    public static final String COL_IMC = "imc";
    public static final String COL_GRASA_PCT = "grasa_pct";
    public static final String COL_FECHA_ACTUALIZACION = "fecha_actualizacion";

    // 📝 SQL para crear la tabla
    private static final String CREATE_TABLE_INDICADORES =
            "CREATE TABLE " + TABLE_INDICADORES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USUARIO_ID + " INTEGER UNIQUE, " + // UNIQUE: Solo un registro por usuario
                    COL_IMC + " REAL, " +
                    COL_GRASA_PCT + " REAL, " +
                    COL_FECHA_ACTUALIZACION + " TEXT);";

    // --- Constructor ---
    public IndicadoresDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // --- Métodos de Ciclo de Vida de la BD ---

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ejecuta la creación de la tabla si no existe
        db.execSQL(CREATE_TABLE_INDICADORES);
        // Aquí podrías añadir más tablas si las necesitas
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si hay una nueva versión de la BD, borra la tabla vieja y crea la nueva
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDICADORES);
        onCreate(db);
    }

    // --- MÉTODOS DE OPERACIÓN (CRUD) ---

    /**
     * Guarda o actualiza los indicadores de salud para un usuario específico.
     * Si el usuario ya existe, actualiza los valores.
     */
    public void guardarIndicadores(int usuarioID, double imc, double grasaPct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USUARIO_ID, usuarioID);
        values.put(COL_IMC, imc);
        values.put(COL_GRASA_PCT, grasaPct);
        values.put(COL_FECHA_ACTUALIZACION, String.valueOf(System.currentTimeMillis())); // Guarda el timestamp

        // Intentar actualizar la fila existente (si la hay)
        int rowsAffected = db.update(
                TABLE_INDICADORES,
                values,
                COL_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioID)}
        );

        // Si no se actualizó ninguna fila (porque no existía), la insertamos
        if (rowsAffected == 0) {
            db.insert(TABLE_INDICADORES, null, values);
        }
        db.close();
    }

    /**
     * Obtiene los indicadores de salud más recientes para un usuario.
     * @return Cursor con los datos (IMC, GrasaPct) o null si no hay datos.
     */
    public Cursor obtenerIndicadores(int usuarioID) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para seleccionar la fila por el usuarioID
        Cursor cursor = db.query(
                TABLE_INDICADORES,
                new String[]{COL_IMC, COL_GRASA_PCT, COL_FECHA_ACTUALIZACION},
                COL_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioID)},
                null, null, null, "1" // Limita a 1 resultado
        );

        // Importante: No cierres la BD ni el cursor aquí, el Fragment/Activity que llama debe manejar el cursor.
        return cursor;
    }
}