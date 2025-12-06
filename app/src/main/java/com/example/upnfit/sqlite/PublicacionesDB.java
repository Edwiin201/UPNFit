package com.example.upnfit.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PublicacionesDB extends SQLiteOpenHelper {

    // Constantes de la Base de Datos
    private static final String DATABASE_NAME = "upnfit_comunidad.db";
    private static final int DATABASE_VERSION = 1;

    // Constantes de la Tabla de Publicaciones
    public static final String TABLE_PUBLICACIONES = "publicaciones";
    public static final String COL_ID_PUBLICACION = "publicacion_id"; // ID del servidor
    public static final String COL_TITULO = "titulo";
    public static final String COL_CONTENIDO = "contenido";
    public static final String COL_AUTOR = "autor";
    public static final String COL_FECHA = "fecha_publicacion";
    public static final String COL_CATEGORIA = "categoria";

    private static final String CREATE_TABLE_PUBLICACIONES =
            "CREATE TABLE " + TABLE_PUBLICACIONES + " (" +
                    COL_ID_PUBLICACION + " INTEGER PRIMARY KEY, " + // Usamos el ID del servidor como PK
                    COL_TITULO + " TEXT, " +
                    COL_CONTENIDO + " TEXT, " +
                    COL_AUTOR + " TEXT, " +
                    COL_FECHA + " TEXT, " +
                    COL_CATEGORIA + " TEXT);";

    public PublicacionesDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PUBLICACIONES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En desarrollo, simplemente borramos y recreamos.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUBLICACIONES);
        onCreate(db);
    }

    /**
     * Limpia la tabla y guarda una nueva lista de publicaciones (cache refresh).
     * @param publicacionID ID único de la publicación
     */
    public void guardarPublicacion(int publicacionID, String titulo, String contenido,
                                   String autor, String fecha, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID_PUBLICACION, publicacionID);
        values.put(COL_TITULO, titulo);
        values.put(COL_CONTENIDO, contenido);
        values.put(COL_AUTOR, autor);
        values.put(COL_FECHA, fecha);
        values.put(COL_CATEGORIA, categoria);

        // Intenta actualizar si ya existe. Esto es útil si los datos del servidor cambian.
        int rowsAffected = db.update(
                TABLE_PUBLICACIONES,
                values,
                COL_ID_PUBLICACION + " = ?",
                new String[]{String.valueOf(publicacionID)}
        );

        if (rowsAffected == 0) {
            db.insert(TABLE_PUBLICACIONES, null, values);
        }
        // No cerramos la base de datos aquí si se va a usar en un loop.
        // Pero por simplicidad en Android Studio, la cerraremos en el Fragment.
    }

    /**
     * Limpia completamente la tabla de publicaciones.
     */
    public void limpiarCache() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PUBLICACIONES, null, null);
        db.close();
    }

    /**
     * Obtiene todas las publicaciones, ordenadas por fecha descendente.
     * @return Cursor con los datos.
     */
    public Cursor obtenerPublicaciones() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Ordenamos por la columna de fecha (COL_FECHA) de forma descendente (DESC)
        return db.query(
                TABLE_PUBLICACIONES,
                null, null, null, null, null,
                COL_FECHA + " DESC"
        );
    }
}