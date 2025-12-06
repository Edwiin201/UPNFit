package com.example.upnfit.fragmentos;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.upnfit.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class GraficaPasosFragment extends Fragment {

    private ImageView btnBackGrafico;
    private BarChart barChart;

    public GraficaPasosFragment() {
        // Constructor vacío requerido por Android
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asegúrate de que este es el nombre correcto del layout XML: fragment_grafica_pasos
        return inflater.inflate(R.layout.fragment_grafica_pasos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Vinculación de la vista
        btnBackGrafico = view.findViewById(R.id.btnBackGrafico);
        barChart = view.findViewById(R.id.barChart);

        // 2. Implementación de la acción de retroceso
        btnBackGrafico.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 3. Cargar y configurar el gráfico
        configurarGrafico();
    }

    private void configurarGrafico() {
        if (barChart == null || getContext() == null) return;

        // 📌 1. Obtener los pasos guardados en SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("pasos", requireContext().MODE_PRIVATE);

        ArrayList<BarEntry> entries = new ArrayList<>();
        // Los días deben coincidir con la forma en que se guardan los datos (0=Dom, 1=Lun, ...)
        String[] dias = new String[]{"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};

        // 📆 Cargar pasos de 7 días (dia0 a dia6)
        for (int i = 0; i < 7; i++) {
            int pasos = prefs.getInt("dia" + i, 0);
            entries.add(new BarEntry(i, pasos));
        }

        // 🟦 2. Dataset del gráfico
        BarDataSet dataSet = new BarDataSet(entries, "Pasos últimos 7 días");
        // Usar ContextCompat para obtener colores en Fragments
        dataSet.setColor(requireContext().getColor(R.color.blue));
        dataSet.setValueTextColor(requireContext().getColor(R.color.black));
        dataSet.setValueTextSize(12f);

        // 📊 3. Asignar datos al gráfico
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.7f); // Opcional: ajustar el ancho de las barras
        barChart.setData(data);

        // 🎨 4. Configurar el Eje X (Días de la semana)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dias));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Asegurar que solo se muestre una etiqueta por día
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(false);
        xAxis.setTextColor(Color.BLACK); // Asegúrate de que se vea sobre fondo blanco

        // 5. Configurar el Eje Y
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false); // Deshabilitar eje derecho

        // 🎨 6. Opciones visuales
        barChart.getDescription().setEnabled(false); // Quitar descripción
        barChart.setFitBars(true);
        barChart.animateY(1200); // Animación

        // 7. Refrescar
        barChart.invalidate();
    }
}