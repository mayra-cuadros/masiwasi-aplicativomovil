package viewmodels;

import android.util.Log; // Importante para depurar
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import models.Mascota;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Mascota>> mascotasLiveData = new MutableLiveData<>();
    private final List<Mascota> mascotasOriginal = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeViewModel() {
        cargarDatosDesdeFirestore();
    }

    private void cargarDatosDesdeFirestore() {
        // Usamos SnapshotListener para tiempo real
        db.collection("mascotas")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error al cargar mascotas: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        mascotasOriginal.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Mascota m = doc.toObject(Mascota.class);
                            if (m != null) {
                                m.setId(doc.getId());
                                mascotasOriginal.add(m);
                            }
                        }
                        Log.d("FirestoreData", "Mascotas cargadas: " + mascotasOriginal.size());
                        // Usamos postValue para asegurar que se actualice la UI incluso desde hilos secundarios
                        mascotasLiveData.postValue(new ArrayList<>(mascotasOriginal));
                    }
                });
    }

    public LiveData<List<Mascota>> getMascotas() {
        return mascotasLiveData;
    }

    public void filterBy(String categoria) {
        if (categoria.equalsIgnoreCase("Todos")) {
            mascotasLiveData.setValue(new ArrayList<>(mascotasOriginal));
        } else {
            List<Mascota> filtradas = new ArrayList<>();
            for (Mascota m : mascotasOriginal) {
                // CORRECCIÓN: Evitar NullPointerException si la categoría viene nula de Firestore
                if (m.getCategoria() != null && m.getCategoria().trim().equalsIgnoreCase(categoria.trim())) {
                    filtradas.add(m);
                }
            }
            mascotasLiveData.setValue(filtradas);
        }
    }
}