package com.example.miditeslacoilapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class MidiFileViewModel : ViewModel() {
    private var _midiFileChosen: MutableLiveData<File> = MutableLiveData()
        val midiFileChosen: LiveData<File>
            get() = _midiFileChosen

    fun choseFile(file: File){
        _midiFileChosen.postValue(file)
    }

}