package com.example.miditeslacoilapp.ui

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.miditeslacoilapp.Extensions.isStoragePermissionGranted
import com.example.miditeslacoilapp.Extensions.requestStoragePermission
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.activities.MainActivity
import com.example.miditeslacoilapp.viewModels.BluetoothViewModel
import com.example.miditeslacoilapp.viewModels.MidiFileViewModel
import java.io.File

class MidiFilesFragment : Fragment() {
    private val midiFileViewModel: MidiFileViewModel by activityViewModels()

    private lateinit var midiFiles: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_midi_files, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        midiFiles = view.findViewById(R.id.midiSongs)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.midi_songs)
    }

    override fun onStart() {
        super.onStart()
        if(!isStoragePermissionGranted()){
            requestStoragePermission()
        }
        val songs = readMidiFiles(Environment.getExternalStorageDirectory())
        val midiFilesNames = arrayOfNulls<String>(songs.size)
        for (i in songs.indices) {
            midiFilesNames[i] = songs[i].name.replace(".mid", "")
        }
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.midi_file_layout, R.id.singleMidi, midiFilesNames)
        midiFiles.adapter = arrayAdapter
        midiFiles.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            midiFileViewModel.choseFile(songs[position])
            (requireActivity() as MainActivity).navigateToPlayer()
        }
    }

    private fun readMidiFiles(root: File): MutableList<File> {
        val arrayList = mutableListOf<File>()
        val files = root.listFiles()
        if (files != null) {
            for (file in files)
                if (file.isDirectory) {
                    arrayList.addAll(readMidiFiles(file))
                } else if (file.name.endsWith(".mid") || file.name.endsWith(".midi")) {
                    arrayList.add(file)
                }
        }
        return arrayList
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.midi_songs)
    }
}