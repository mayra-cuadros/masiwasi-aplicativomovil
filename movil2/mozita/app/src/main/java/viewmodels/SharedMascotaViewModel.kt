package viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import models.Mascota

class SharedMascotaViewModel : ViewModel() {

    private val mascotas: MutableLiveData<MutableList<Mascota>> =
        MutableLiveData(ArrayList())

    fun getMascotas(): LiveData<MutableList<Mascota>> {
        return mascotas
    }

    fun addMascota(mascota: Mascota) {
        val list = mascotas.value
        list?.add(mascota)
        mascotas.value = list
    }
}