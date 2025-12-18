package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query; // Importante para ordenar

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

        db.collection("mascotas")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
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

                        mascotasLiveData.setValue(new ArrayList<>(mascotasOriginal));
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

                if (m.getCategoria() != null && m.getCategoria().trim().equalsIgnoreCase(categoria)) {
                    filtradas.add(m);
                }
            }
            mascotasLiveData.setValue(filtradas);
        }
    }
}