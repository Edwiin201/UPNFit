package com.example.upnfit.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.modelos.Tarea;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {

    private List<Tarea> tareas;

    public TareaAdapter(List<Tarea> tareas) {
        this.tareas = tareas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarea tarea = tareas.get(position);
        holder.tvCurso.setText(tarea.getCurso());
        holder.tvFecha.setText("Entrega: " + tarea.getFechaEntrega());
        holder.tvDescripcion.setText(tarea.getDescripcion());
        holder.tvProfesor.setText(tarea.getProfesor());

        if (tarea.isCompletada()) {
            holder.tvEstado.setText("COMPLETADA");
            holder.tvEstado.setTextColor(0xFF4CAF50); // Verde
            holder.tvEstado.setBackgroundColor(0xFFE8F5E9);
        } else {
            holder.tvEstado.setText("PENDIENTE");
            holder.tvEstado.setTextColor(0xFFE53935); // Rojo
            holder.tvEstado.setBackgroundColor(0xFFFFEBEE);
        }
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurso, tvFecha, tvDescripcion, tvProfesor, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurso = itemView.findViewById(R.id.tvCursoTarea);
            tvFecha = itemView.findViewById(R.id.tvFechaTarea);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionTarea);
            tvProfesor = itemView.findViewById(R.id.tvProfesorTarea);
            tvEstado = itemView.findViewById(R.id.tvEstadoTarea);
        }
    }
}
