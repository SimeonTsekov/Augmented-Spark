package com.example.miditeslacoilapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.viewModels.BluetoothViewModel

class InterruptorFragment : Fragment() {
    private val viewModel: BluetoothViewModel by activityViewModels()

    companion object {
        private const val frequencyThreshold = 60
        private const val pwmThreshold = 100
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interruptor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frequency = view.findViewById<TextView>(R.id.freq)
        val pulseWidth = view.findViewById<TextView>(R.id.volume)
        val freqSeekBar = view.findViewById<SeekBar>(R.id.freqSeekBar)
        val pwmSeekBar = view.findViewById<SeekBar>(R.id.pwmSeekBar)

        freqSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val freq = seekBar.progress + frequencyThreshold
                frequency.text = "Frequency: $freq"
                viewModel.writeData(freq.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        pwmSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val pwm = seekBar.progress + pwmThreshold
                pulseWidth.text = "Pulse width: $pwm"
                viewModel.writeData(pwm.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}