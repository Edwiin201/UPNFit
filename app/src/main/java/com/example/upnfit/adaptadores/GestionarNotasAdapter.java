package com.example.upnfit.adaptadores;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.modelos.Alumno;
import java.util.List;

public class GestionarNotasAdapter extends RecyclerView.Adapter<GestionarNotasAdapter.ViewHolder> {

    private List<Alumno> alumnos;

    public GestionarNotasAdapter(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno_nota, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alumno alumno = alumnos.get(position);
        holder.tvNombre.setText(alumno.getNombre());
        holder.etNota.setText(String.valueOf(alumno.getNota()));

        holder.etNota.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        double nota = Double.parseDouble(s.toString());
                        if (nota >= 0 && nota <= 20) {
                            alumno.setNota(nota);
                        }
                    }
                } catch (NumberFormatException e) {
                    alumno.setNota(0.0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        EditText etNota;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAlumnoNota);
            etNota = itemView.findViewById(R.id.etNotaAlumno);
        }
    }
}
