package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pe.edu.idat.mozitaapp.R
import models.Mascota

class MascotaAdapter(
    private val context: Context,
    private var mascotaList: MutableList<Mascota>,
    private var modoEdicion: Boolean,
    private val listener: OnMascotaClickListener?
) : RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    interface OnMascotaClickListener {
        fun onVerDetalles(mascota: Mascota)
    }

    fun updateList(newList: MutableList<Mascota>) {
        this.mascotaList = newList
        notifyDataSetChanged()
    }

    fun setModoEdicion(modoEdicion: Boolean) {
        this.modoEdicion = modoEdicion
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotaList[position]

        holder.txtNombre.text = mascota.getNombre()
        holder.txtEdad.text = "Edad: ${mascota.getEdad()}"
        holder.txtSexo.text = "Sexo: ${mascota.getSexo()}"
        holder.txtDescripcion.text = mascota.getDescripcion()

        val imageUrl = mascota.getImageUrl()

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.mipmap.mascota1)
                .centerCrop()
                .into(holder.imgMascota)
        } else {
            holder.imgMascota.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.btnDetalles.text = if (modoEdicion) "Editar" else "Ver Detalles"

        holder.btnDetalles.setOnClickListener {
            listener?.onVerDetalles(mascota)
        }
    }

    override fun getItemCount(): Int {
        return mascotaList.size
    }

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgMascota: ImageView = itemView.findViewById(R.id.imgMascota)
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreMascota)
        val txtEdad: TextView = itemView.findViewById(R.id.txtEdadMascota)
        val txtSexo: TextView = itemView.findViewById(R.id.txtSexoMascota)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcionMascota)
        val btnDetalles: Button = itemView.findViewById(R.id.btnVerDetalles)
    }
}