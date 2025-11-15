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

        BarChart chart = findViewById(R.id.barChart);

        SharedPreferences prefs = getSharedPreferences("pasos", MODE_PRIVATE);

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, prefs.getInt("dia" + i, 0)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Pasos últimos 7 días");
        chart.setData(new BarData(dataSet));
        chart.invalidate();
    }
}
