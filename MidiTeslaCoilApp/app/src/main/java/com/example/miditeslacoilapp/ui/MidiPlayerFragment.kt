package com.example.miditeslacoilapp.ui

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.miditeslacoilapp.EventListeners.EventListener
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.viewModels.BluetoothViewModel
import com.example.miditeslacoilapp.viewModels.MidiFileViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leff.midi.MidiFile
import com.leff.midi.event.MidiEvent
import com.leff.midi.util.MidiProcessor
import com.leff.midi.util.VariableLengthInt
import java.io.File
import java.io.IOException
import java.util.*

class MidiPlayerFragment : Fragment() {
    private val midiFileViewModel: MidiFileViewModel by activityViewModels()
    private val bluetoothViewModel: BluetoothViewModel by activityViewModels()

    private lateinit var playFab: FloatingActionButton
    private lateinit var pauseFab: FloatingActionButton
    private lateinit var songProgress: ProgressBar

    private var midiFileLength: Long? = null

    private val handler = Handler()

    @Volatile
    var processor: MidiProcessor? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playFab = view.findViewById(R.id.fab_play)
        pauseFab = view.findViewById(R.id.fab_pause)
        songProgress = view.findViewById(R.id.midi_file_length)

        midiFileViewModel.midiFileChosen.observe(viewLifecycleOwner, {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = it.name
            songProgress.progress = 0
            if (processor != null) {
                if (processor!!.isRunning) processor!!.stop()
            }
            Thread(MidiListenerThread(it)).start()
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_midi_player, container, false)
    }

    private inner class MidiListenerThread(private val file: File) : Runnable {
        override fun run() {
            val midi: MidiFile
            midi = try {
                MidiFile(file)
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            midiFileLength = midi.lengthInTicks
            processor = MidiProcessor(midi)
            processor!!.registerEventListener(
                    EventListener(
                            bluetoothViewModel::writeData,
                            bluetoothViewModel::onPause,
                            this::onTicksPassed),
                    MidiEvent::class.java)

            handler.post {
                playFab.setOnClickListener { start() }
                pauseFab.setOnClickListener { pause() }
            }
        }

        private fun onTicksPassed(ticksPassed: Long) {
            songProgress.progress = ((midiFileLength?.let { ticksPassed.div(it) })?.times(100))?.toInt() ?: 0
        }

        private fun start() {
            if (!processor?.isRunning!!) processor!!.start()
        }

        private fun pause() {
            if (processor?.isRunning!!) processor!!.stop()
        }
    }

    override fun onPause() {
        super.onPause()
        bluetoothViewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
        val curSongName: String? = midiFileViewModel.midiFileChosen.value?.name
        val actionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar

        if(curSongName != null) actionBar?.title = curSongName
        else actionBar?.title = "No song loaded"
    }
}