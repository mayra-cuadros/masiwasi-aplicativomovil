package pe.edu.idat.mozitaapp.fragments

import adapters.MascotaAdapter
import adapters.MascotaAdapter.OnMascotaClickListener
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import models.Mascota
import pe.edu.idat.mozitaapp.R
import pe.edu.idat.mozitaapp.activities.DetailActivity
import viewmodels.HomeViewModel

class HomeFragments : Fragment() {

    private var homeViewModel: HomeViewModel? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: MascotaAdapter? = null

    private var chipAll: Chip? = null
    private var chipPerro: Chip? = null
    private var chipGato: Chip? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPublications)
        chipAll = view.findViewById(R.id.chipAll)
        chipPerro = view.findViewById(R.id.chipPerro)
        chipGato = view.findViewById(R.id.chipGato)

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        adapter = MascotaAdapter(
            requireContext(),
            mutableListOf(),
            false,
            object : OnMascotaClickListener {
                override fun onVerDetalles(mascota: Mascota) {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("nombre", mascota.getNombre())
                    intent.putExtra("descripcion", mascota.getDescripcion())
                    intent.putExtra("imagenUrl", mascota.getImageUrl())
                    intent.putExtra("sexo", mascota.getSexo())
                    intent.putExtra("edad", mascota.getEdad())
                    intent.putExtra("categoria", mascota.getCategoria())
                    intent.putExtra("color", mascota.getColor())

                    intent.putExtra("duenoId", mascota.getDuenoId())

                    startActivity(intent)
                }
            }
        )

        recyclerView?.adapter = adapter

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // CORREGIDO AQUÍ
        homeViewModel?.getMascotas()?.observe(viewLifecycleOwner) { mascotas ->
            if (mascotas != null) {
                adapter?.updateList(mascotas.toMutableList())
            }
        }

        chipAll?.setOnClickListener {
            homeViewModel?.filterBy("Todos")
        }

        chipPerro?.setOnClickListener {
            homeViewModel?.filterBy("Perro")
        }

        chipGato?.setOnClickListener {
            homeViewModel?.filterBy("Gato")
        }

        return view
    }
}