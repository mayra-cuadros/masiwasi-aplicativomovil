package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import models.Mascota;

public class SharedMascotaViewModel extends ViewModel {

    private final MutableLiveData<List<Mascota>> mascotas = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Mascota>> getMascotas() {
        return mascotas;
    }

    public void addMascota(Mascota mascota) {
        List<Mascota> list = mascotas.getValue();
        list.add(mascota);
        mascotas.setValue(list);
    }
}
