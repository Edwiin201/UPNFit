package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.upnfit.R;

import org.json.JSONException;
import org.json.JSONObject;


public class CargaActivity extends AppCompatActivity {
    ProgressBar barCarga;
    TextView txtFrase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carga);
        
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        barCarga = findViewById(R.id.carBarCarga);
        txtFrase = findViewById(R.id.txtFrase);

        cargarFraseMotivacional();

        Thread tCarga=new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= barCarga.getMax(); i++) {
                    barCarga.setProgress(i);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Intent iSesion = new Intent(getApplicationContext(), SesionActivity.class);
                startActivity(iSesion);
                finish();
                }
            });
            tCarga.start();

    }

    private void cargarFraseMotivacional() {
        // Frases académicas en lugar de fitness
        String[] frasesAcademicas = {
            "La educación es el arma más poderosa para cambiar el mundo.",
            "El éxito es la suma de pequeños esfuerzos repetidos día tras día.",
            "Iniciando sesión segura en el Portal Académico...",
            "Cargando tus cursos y actividades del semestre...",
            "La excelencia no es un acto, sino un hábito."
        };
        
        int randomIndex = (int) (Math.random() * frasesAcademicas.length);
        String fraseElegida = frasesAcademicas[randomIndex];

        txtFrase.setAlpha(0f);
        txtFrase.setText(fraseElegida);
        txtFrase.animate().alpha(1f).setDuration(1000).start();
    }
}