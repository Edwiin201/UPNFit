package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

public class EditarcuentaActivity extends AppCompatActivity {

    private EditText editCorreo, editPassword;
    private ImageButton ivVerPassword;
    private Button btnCerrarSesion;
    private boolean isPasswordVisible = false; // Para controlar visibilidad de contraseña

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarcuenta);

        // Inicializar elementos UI
        editCorreo = findViewById(R.id.editCorreo);
        editPassword = findViewById(R.id.editPassword);
        ivVerPassword = findViewById(R.id.ivVerPassword);
        btnCerrarSesion = findViewById(R.id.tvCerrarSesion); // botón cerrar sesión

        // Cargar datos guardados
        loadData();

        // Toggle visibilidad contraseña
        ivVerPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivVerPassword.setImageResource(android.R.drawable.ic_menu_view);
                isPasswordVisible = false;
            } else {
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivVerPassword.setImageResource(android.R.drawable.ic_menu_zoom);
                isPasswordVisible = true;
            }
            editPassword.setSelection(editPassword.getText().length());
        });

        // Botón para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            // Limpiar sesión SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Ir a pantalla de sesión y limpiar el stack
            Intent intent = new Intent(EditarcuentaActivity.this, SesionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Botón regreso (X)
        ImageButton btnRegresoConfig = findViewById(R.id.regresoconfi);
        btnRegresoConfig.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(EditarcuentaActivity.this, ConfiguracionActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Guardar datos en SharedPreferences
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("correo", editCorreo.getText().toString());
        editor.putString("contrasena", editPassword.getText().toString());
        editor.apply();
    }

    // Cargar datos de SharedPreferences
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String correo = sharedPreferences.getString("correo", "defaultemail@example.com");
        String contrasena = sharedPreferences.getString("contrasena", "defaultpassword");
        editCorreo.setText(correo);
        editPassword.setText(contrasena);
    }
}
