package com.example.upnfit.actividades;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class GraficoPasosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grafico_pasos);

        // 🔙 BOTÓN RETROCEDER
        findViewById(R.id.btnBackGrafico).setOnClickListener(v -> finish());

        // 📊 Referencia al BarChart
        BarChart chart = findViewById(R.id.barChart);

        // 📌 Obtener los pasos guardados en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("pasos", MODE_PRIVATE);

        ArrayList<BarEntry> entries = new ArrayList<>();

        // 📆 Cargar pasos de 7 días (dia0 a dia6)
        for (int i = 0; i < 7; i++) {
            int pasos = prefs.getInt("dia" + i, 0);
            entries.add(new BarEntry(i, pasos));
        }

        // 🟦 Dataset del gráfico
        BarDataSet dataSet = new BarDataSet(entries, "Pasos últimos 7 días");
        dataSet.setColor(getColor(R.color.blue));   // Color de barras
        dataSet.setValueTextColor(getColor(R.color.black));
        dataSet.setValueTextSize(12f);

        // 📊 Asignar datos al gráfico
        BarData data = new BarData(dataSet);
        chart.setData(data);

        // 🎨 Opciones visuales
        chart.getDescription().setEnabled(false); // Quitar descripción
        chart.setFitBars(true);
        chart.animateY(1200); // Animación

        chart.invalidate(); // Refrescar
    }
}
