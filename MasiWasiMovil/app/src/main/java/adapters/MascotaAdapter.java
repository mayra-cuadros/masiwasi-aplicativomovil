package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.masiwasimovil.R;

import java.util.List;

import models.Mascota;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private List<Mascota> mascotaList;
    private OnMascotaClickListener listener;
    private Context context;
    private boolean modoEdicion;

    // 1. INTERFAZ LIMPIA (Sin código de Intent aquí dentro)
    public interface OnMascotaClickListener {
        void onVerDetalles(Mascota mascota);
    }

    public MascotaAdapter(Context context, List<Mascota> mascotaList, boolean modoEdicion, OnMascotaClickListener listener) {
        this.context = context;
        this.mascotaList = mascotaList;
        this.listener = listener;
        this.modoEdicion = modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = mascotaList.get(position);

        // Llenado de textos
        holder.txtNombre.setText(mascota.getNombre());
        holder.txtEdad.setText(mascota.getEdad());
        holder.txtSexo.setText(mascota.getSexo());
        holder.txtDescripcion.setText(mascota.getDescripcion());

        // 2. CARGA DE IMAGEN (Revisa que getImageUrl() coincida con el nombre en Firebase)
        Glide.with(context)
                .load(mascota.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground) // Imagen mientras carga
                .error(android.R.drawable.stat_notify_error)     // Imagen si falla
                .into(holder.imgMascota);

        // Configuración del botón
        holder.btnDetalles.setText(modoEdicion ? "Editar" : "Ver/Detalles");

        // 3. EVENTO CLIC (Envía la mascota al listener)
        holder.btnDetalles.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerDetalles(mascota);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mascotaList != null ? mascotaList.size() : 0;
    }

    public static class MascotaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMascota;
        TextView txtNombre, txtEdad, txtSexo, txtDescripcion;
        Button btnDetalles;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMascota = itemView.findViewById(R.id.imgMascota);
            txtNombre = itemView.findViewById(R.id.txtNombreMascota);
            txtEdad = itemView.findViewById(R.id.txtEdadMascota);
            txtSexo = itemView.findViewById(R.id.txtSexoMascota);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionMascota);
            btnDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}