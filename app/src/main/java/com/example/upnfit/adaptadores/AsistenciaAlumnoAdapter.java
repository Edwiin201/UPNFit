package com.example.upnfit.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.modelos.Alumno;
import java.util.List;

public class AsistenciaAlumnoAdapter extends RecyclerView.Adapter<AsistenciaAlumnoAdapter.ViewHolder> {

    private List<Alumno> alumnos;

    public AsistenciaAlumnoAdapter(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno_asistencia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alumno alumno = alumnos.get(position);
        holder.tvNombre.setText(alumno.getNombre());
        holder.cbAsistencia.setChecked(alumno.isAsistio());
        holder.cbAsistencia.setOnCheckedChangeListener((buttonView, isChecked) -> alumno.setAsistio(isChecked));
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        CheckBox cbAsistencia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAlumno);
            cbAsistencia = itemView.findViewById(R.id.cbAsistencia);
        }
    }
}
