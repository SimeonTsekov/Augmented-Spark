package com.example.miditeslacoilapp.EventListeners

import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOn
import com.leff.midi.util.MidiEventListener
import java.util.*

class EventListener(private val onData: (data: String) -> Unit, private val onPause: () -> Unit) : MidiEventListener {
    private var midiFrequencies = mutableMapOf<Int, Int>()
    private var midiPulseWidth = mutableMapOf<Int, Int>()

    init {
        initMidiFrequencies()
        initMidiPulseWidth()
    }

    private fun initMidiFrequencies() {
        midiFrequencies = HashMap()
        midiFrequencies[24] = 33
        midiFrequencies[25] = 35
        midiFrequencies[26] = 37
        midiFrequencies[27] = 39
        midiFrequencies[28] = 41
        midiFrequencies[29] = 44
        midiFrequencies[30] = 46
        midiFrequencies[31] = 49
        midiFrequencies[32] = 52
        midiFrequencies[33] = 55
        midiFrequencies[34] = 58
        midiFrequencies[35] = 62
        midiFrequencies[36] = 65
        midiFrequencies[37] = 69
        midiFrequencies[38] = 73
        midiFrequencies[39] = 78
        midiFrequencies[40] = 82
        midiFrequencies[41] = 87
        midiFrequencies[42] = 92
        midiFrequencies[43] = 98
        midiFrequencies[44] = 104
        midiFrequencies[45] = 110
        midiFrequencies[46] = 116
        midiFrequencies[47] = 123
        midiFrequencies[48] = 131
        midiFrequencies[49] = 139
        midiFrequencies[50] = 147
        midiFrequencies[51] = 156
        midiFrequencies[52] = 165
        midiFrequencies[53] = 178
        midiFrequencies[54] = 185
        midiFrequencies[55] = 196
        midiFrequencies[56] = 208
        midiFrequencies[57] = 220
        midiFrequencies[58] = 233
        midiFrequencies[59] = 247
        midiFrequencies[60] = 262
        midiFrequencies[61] = 277
        midiFrequencies[62] = 294
        midiFrequencies[63] = 311
        midiFrequencies[64] = 330
        midiFrequencies[65] = 349
        midiFrequencies[66] = 370
        midiFrequencies[67] = 392
        midiFrequencies[68] = 415
        midiFrequencies[69] = 440
        midiFrequencies[70] = 466
        midiFrequencies[71] = 493
        midiFrequencies[72] = 523
        midiFrequencies[73] = 554
        midiFrequencies[74] = 587
        midiFrequencies[75] = 622
        midiFrequencies[76] = 659
        midiFrequencies[77] = 659
        midiFrequencies[78] = 740
        midiFrequencies[79] = 784
        midiFrequencies[80] = 831
        midiFrequencies[81] = 880
        midiFrequencies[82] = 932
        midiFrequencies[83] = 988
        midiFrequencies[84] = 1046
        midiFrequencies[85] = 1109
        midiFrequencies[86] = 1175
        midiFrequencies[87] = 1244
        midiFrequencies[88] = 1318
        midiFrequencies[89] = 1397
        midiFrequencies[90] = 1480
        midiFrequencies[91] = 1568
        midiFrequencies[92] = 1661
        midiFrequencies[93] = 1760
        midiFrequencies[94] = 1865
        midiFrequencies[95] = 1975
        midiFrequencies[96] = 2093
        midiFrequencies[97] = 2217
        midiFrequencies[98] = 2349
        midiFrequencies[99] = 2489
        midiFrequencies[100] = 2637
        midiFrequencies[101] = 2793
        midiFrequencies[102] = 3136
        midiFrequencies[103] = 3322
        midiFrequencies[104] = 3520
        midiFrequencies[105] = 3729
        midiFrequencies[106] = 3951
        midiFrequencies[107] = 4186
    }

    private fun initMidiPulseWidth() {
        for (i in 0..59) {
            midiPulseWidth[i + 1] = 1000 + i * 20
        }
        for (i in 60..127) {
            midiPulseWidth[i] = 1000 + i * 40
        }
    }

    override fun onStart(fromBeginning: Boolean) {}

    override fun onEvent(event: MidiEvent, ms: Long) {
        if (event is NoteOn) {
            val noteValue = event.noteValue
            val noteVelocity = event.velocity
            val freq = midiFrequencies[noteValue]
            val pwm = midiPulseWidth[noteVelocity]
            this.onData(freq.toString() + "m" + pwm.toString())
        }
    }

    override fun onStop(finished: Boolean) = this.onPause()
}