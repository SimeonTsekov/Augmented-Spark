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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.example.miditeslacoilapp.EventListeners.EventListener
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.viewModels.BluetoothViewModel
import com.leff.midi.MidiFile
import com.leff.midi.event.MidiEvent
import com.leff.midi.util.MidiProcessor
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.NullPointerException

class MidiFragment : Fragment() {
    private val viewModel: BluetoothViewModel by activityViewModels()
    private var midiFiles: ListView? = null
    private var play: ImageButton? = null
    private val handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_midi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        midiFiles = view.findViewById(R.id.midiSongs)
        play = view.findViewById(R.id.on)
    }

    override fun onStart() {
        super.onStart()
        val songs = readMidiFiles(Environment.getRootDirectory())
        val midiFilesNames = arrayOfNulls<String>(songs.size)
        for (i in songs.indices) {
            midiFilesNames[i] = songs[i].name.replace(".mid", "")
        }
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.midi_file_layout, R.id.singleMidi, midiFilesNames)
        midiFiles!!.adapter = arrayAdapter
        midiFiles!!.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val file = songs[position]
            if (file.isFile) {
                val midiListener = MidiListenerThread(file)
                Thread(midiListener).start()
            }
        }
    }

        private fun readMidiFiles(root: File): MutableList<File> {
            val arrayList = mutableListOf<File>()
            var files: MutableList<File>? = null
            try {
                val files = root.listFiles()
            } catch (e: Exception){
            when (e) {
                is NullPointerException -> print("w r y y y y y ")
            }
            }
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

    private inner class MidiListenerThread(private val file: File) : Runnable {
        private var processor: MidiProcessor? = null

        override fun run() {
            val midi: MidiFile
            midi = try {
                MidiFile(file)
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            processor = MidiProcessor(midi)
            // method reference OVERDURAIVUUU
            processor!!.registerEventListener(EventListener(viewModel::writeData, viewModel::onPause), MidiEvent::class.java)
            handler.post { play!!.setOnClickListener { onClick() } }
        }

        private fun onClick() {
            if (!processor!!.isRunning) {
                processor!!.start()
                play!!.background = resources.getDrawable(R.drawable.ic_baseline_pause_24)
            } else {
                processor!!.stop()
                play!!.background = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }
}