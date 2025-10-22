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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        String url = "http://renovaapp.atwebpages.com/Services/Frasealeatoria.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            String frase = data.getString("frase");
                            String autor = data.getString("autor");

                            String textoFinal = "\"" + frase + "\"\n\n- " + autor;

                            txtFrase.setText("\"" + frase + "\"\n\n- " + autor);
                            txtFrase.setAlpha(0f); // Comienza invisible
                            txtFrase.setText(textoFinal);
                            txtFrase.animate().alpha(1f).setDuration(1000).start();
                        } else {
                            txtFrase.setAlpha(0f);
                            txtFrase.setText("Prepárate para alcanzar tus metas 💪");
                            txtFrase.animate().alpha(1f).setDuration(1000).start();
                        }
                    } catch (JSONException e) {
                        txtFrase.setAlpha(0f);
                        txtFrase.setText("Inicia tu día con energía ✨");
                        txtFrase.animate().alpha(1f).setDuration(1000).start();
                    }
                },
                error -> {
                    txtFrase.setAlpha(0f);
                    txtFrase.setText("Hoy es un buen día para mejorar.");
                    txtFrase.animate().alpha(1f).setDuration(1000).start();
                }
        );
        queue.add(request);
    }
}