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

import com.example.masiwasi.R;

import java.util.List;

import models.Mascota;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private List<Mascota> mascotaList;
    private OnMascotaClickListener listener;
    private Context context;

    // Listener para manejar el clic en "Ver/Detalles"
    public interface OnMascotaClickListener {
        void onVerDetalles(Mascota mascota);
    }

    public MascotaAdapter(Context context, List<Mascota> mascotaList, OnMascotaClickListener listener) {
        this.context = context;
        this.mascotaList = mascotaList;
        this.listener = listener;
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

//        // imagenes
//        if (mascota.getImagenResId() != 0) {
//            holder.imgMascota.setImageResource(mascota.getImagenResId());
//        }

        holder.txtNombre.setText("Nombre: " + mascota.getNombre());
        holder.txtEdad.setText("Edad: " + mascota.getEdad());
        holder.txtSexo.setText("Sexo: " + mascota.getSexo());
        holder.txtDescripcion.setText(mascota.getDescripcion());

        holder.btnDetalles.setOnClickListener(v -> {
            if (listener != null) listener.onVerDetalles(mascota);
        });
    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
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
