package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import models.Mascota;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Mascota>> mascotasLiveData = new MutableLiveData<>();
    private final List<Mascota> mascotasOriginal = new ArrayList<>();

    public HomeViewModel() {
        // Datos de ejemplo
//        mascotasOriginal.add(new Mascota("1","Demi", "8 meses", "Hembra", "Juguetona y dulce", "Perro", "Marrón", ""));
//        mascotasOriginal.add(new Mascota("2","Fevi", "2 meses", "Macho", "Muy curioso y activo", "Gato", "Gris", ""));
//        mascotasOriginal.add(new Mascota("3","Luna", "1 año", "Hembra", "Cariñosa y tranquila", "Perro", "Blanco", ""));
//        mascotasLiveData.setValue(new ArrayList<>(mascotasOriginal));
    }

    public LiveData<List<Mascota>> getMascotas() {
        return mascotasLiveData;
    }

    public void filterBy(String categoria) {
        if (categoria.equalsIgnoreCase("Todos") || categoria.equalsIgnoreCase("all")) {
            mascotasLiveData.setValue(new ArrayList<>(mascotasOriginal));
        } else {
            List<Mascota> filtradas = new ArrayList<>();
            for (Mascota m : mascotasOriginal) {
                if (m.getCategoria().equalsIgnoreCase(categoria)) {
                    filtradas.add(m);
                }
            }
            mascotasLiveData.setValue(filtradas);
        }
    }
}
