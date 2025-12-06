package com.example.upnfit.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class AlimentosDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alimentos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ALIMENTOS = "Alimentos";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIPO = "tipo";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_PREPARACION = "preparacion";
    private static final String COLUMN_CALORIAS = "calorias";
    private static final String COLUMN_PROTEINAS = "proteinas";
    private static final String COLUMN_GRASAS = "grasas";
    private static final String COLUMN_CARBOHIDRATOS = "carbohidratos";

    public AlimentosDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_ALIMENTOS =
            "CREATE TABLE " + TABLE_ALIMENTOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIPO + " TEXT NOT NULL, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_PREPARACION + " TEXT, " +
                    COLUMN_CALORIAS + " REAL, " +
                    COLUMN_PROTEINAS + " REAL, " +
                    COLUMN_GRASAS + " REAL, " +
                    COLUMN_CARBOHIDRATOS + " REAL" +
                    ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALIMENTOS);
        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALIMENTOS);
        onCreate(db);
    }

    // ============================
    //   INSERTA LOS 55+ ALIMENTOS
    // ============================
    private void insertarDatosIniciales(SQLiteDatabase db) {
        insertarAlimentoInicial(db, "Desayuno", "Avena con leche y banana", "Cocer avena con leche y añadir banana", 350, 12, 8, 60);
        insertarAlimentoInicial(db, "Desayuno", "Huevos revueltos con pan integral", "2 huevos + 2 rebanadas de pan integral", 400, 20, 15, 40);
        insertarAlimentoInicial(db, "Desayuno", "Batido de proteína y avena", "Mezclar proteína en polvo, avena y leche", 450, 25, 10, 55);
        insertarAlimentoInicial(db, "Desayuno", "Tostadas con aguacate y huevo", "Pan integral con aguacate y huevo pochado", 380, 18, 14, 42);
        insertarAlimentoInicial(db, "Desayuno", "Yogur con frutos secos y miel", "Yogur natural + frutos secos + miel", 320, 15, 12, 40);
        insertarAlimentoInicial(db, "Desayuno", "Pan integral con mantequilla de maní", "2 rebanadas con mantequilla de maní", 370, 14, 16, 38);
        insertarAlimentoInicial(db, "Desayuno", "Smoothie bowl con frutas y avena", "Frutas + avena + yogur", 360, 10, 8, 65);

        insertarAlimentoInicial(db, "Almuerzo", "Pechuga de pollo con arroz integral", "150g de pollo + arroz integral", 500, 35, 10, 55);
        insertarAlimentoInicial(db, "Almuerzo", "Ensalada de atún", "Atún + verduras", 400, 30, 12, 35);
        insertarAlimentoInicial(db, "Almuerzo", "Salmón al horno con quinoa", "150g salmón + quinoa", 480, 32, 15, 45);
        insertarAlimentoInicial(db, "Almuerzo", "Pasta integral con verduras y pollo", "Pasta integral + pollo", 520, 28, 12, 60);
        insertarAlimentoInicial(db, "Almuerzo", "Wrap de pollo con vegetales", "Tortilla + pollo + vegetales", 450, 25, 10, 50);
        insertarAlimentoInicial(db, "Almuerzo", "Arroz con huevo y vegetales", "Arroz + huevos + vegetales", 470, 22, 14, 55);
        insertarAlimentoInicial(db, "Almuerzo", "Lentejas con arroz", "Lentejas + arroz", 430, 20, 8, 60);

        insertarAlimentoInicial(db, "Cena", "Sopa de verduras con pollo", "Sopa + pollo", 350, 20, 6, 45);
        insertarAlimentoInicial(db, "Cena", "Ensalada de salmón y aguacate", "Salmón + aguacate", 400, 25, 18, 35);
        insertarAlimentoInicial(db, "Cena", "Tortilla de claras con vegetales", "Claras + vegetales", 300, 22, 5, 40);
        insertarAlimentoInicial(db, "Cena", "Pescado al horno con vegetales", "Pescado + verduras", 380, 28, 8, 42);
        insertarAlimentoInicial(db, "Cena", "Pollo a la plancha con ensalada", "Pollo + ensalada", 360, 30, 10, 40);
        insertarAlimentoInicial(db, "Cena", "Quinoa con verduras", "Quinoa + verduras", 340, 12, 6, 50);
        insertarAlimentoInicial(db, "Cena", "Wrap integral con pavo", "Tortilla + pavo", 370, 25, 8, 45);

        insertarAlimentoInicial(db, "Snacks", "Frutos secos", "30g mezcla frutos secos", 180, 6, 16, 6);
        insertarAlimentoInicial(db, "Snacks", "Yogur natural con miel", "Yogur + miel", 120, 5, 3, 15);
        insertarAlimentoInicial(db, "Snacks", "Barra de proteína", "Barra comercial", 200, 20, 8, 20);
        insertarAlimentoInicial(db, "Snacks", "Fruta fresca", "1 manzana o plátano", 90, 1, 0, 22);
        insertarAlimentoInicial(db, "Snacks", "Palitos de zanahoria con hummus", "Zanahoria + hummus", 150, 4, 7, 18);
        insertarAlimentoInicial(db, "Snacks", "Galletas integrales", "2 galletas", 110, 3, 4, 20);
        insertarAlimentoInicial(db, "Snacks", "Batido pequeño de frutas", "Frutas licuadas", 130, 2, 2, 28);
    }

    private void insertarAlimentoInicial(SQLiteDatabase db, String tipo, String nombre, String prep,
                                         double cal, double pro, double gra, double carb) {

        ContentValues valores = new ContentValues();

        valores.put(COLUMN_TIPO, tipo);
        valores.put(COLUMN_NOMBRE, nombre);
        valores.put(COLUMN_PREPARACION, prep);
        valores.put(COLUMN_CALORIAS, cal);
        valores.put(COLUMN_PROTEINAS, pro);
        valores.put(COLUMN_GRASAS, gra);
        valores.put(COLUMN_CARBOHIDRATOS, carb);

        db.insert(TABLE_ALIMENTOS, null, valores);
    }

    public Cursor obtenerAlimentosPorTipo(String tipo) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ALIMENTOS,
                null,
                COLUMN_TIPO + " = ?",
                new String[]{tipo},
                null, null, null);
    }
}

