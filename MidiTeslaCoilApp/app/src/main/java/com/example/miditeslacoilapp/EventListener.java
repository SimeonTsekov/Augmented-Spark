package com.example.miditeslacoilapp;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.util.MidiEventListener;

import java.util.HashMap;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class EventListener implements MidiEventListener{
    private BluetoothSPP bluetooth;
    private HashMap<Integer, Integer> midiFrequencies;
    private HashMap<Integer, Integer> midiPulseWidth;

    public EventListener() {
        initMidiFrequencies();
        initMidiPulseWidth();
    }

    public void setBluetooth(BluetoothSPP bluetooth){
        this.bluetooth = bluetooth;
    }

    private void initMidiFrequencies() {
        midiFrequencies = new HashMap<>();
        midiFrequencies.put(24, 33);
        midiFrequencies.put(25, 35);
        midiFrequencies.put(26, 37);
        midiFrequencies.put(27, 39);
        midiFrequencies.put(28, 41);
        midiFrequencies.put(29, 44);
        midiFrequencies.put(30, 46);
        midiFrequencies.put(31, 49);
        midiFrequencies.put(32, 52);
        midiFrequencies.put(33, 55);
        midiFrequencies.put(34, 58);
        midiFrequencies.put(35, 62);
        midiFrequencies.put(36, 65);
        midiFrequencies.put(37, 69);
        midiFrequencies.put(38, 73);
        midiFrequencies.put(39, 78);
        midiFrequencies.put(40, 82);
        midiFrequencies.put(41, 87);
        midiFrequencies.put(42, 92);
        midiFrequencies.put(43, 98);
        midiFrequencies.put(44, 104);
        midiFrequencies.put(45, 110);
        midiFrequencies.put(46, 116);
        midiFrequencies.put(47, 123);
        midiFrequencies.put(48, 131);
        midiFrequencies.put(49, 139);
        midiFrequencies.put(50, 147);
        midiFrequencies.put(51, 156);
        midiFrequencies.put(52, 165);
        midiFrequencies.put(53, 178);
        midiFrequencies.put(54, 185);
        midiFrequencies.put(55, 196);
        midiFrequencies.put(56, 208);
        midiFrequencies.put(57, 220);
        midiFrequencies.put(58, 233);
        midiFrequencies.put(59, 247);
        midiFrequencies.put(60, 262);
        midiFrequencies.put(61, 277);
        midiFrequencies.put(62, 294);
        midiFrequencies.put(63, 311);
        midiFrequencies.put(64, 330);
        midiFrequencies.put(65, 349);
        midiFrequencies.put(66, 370);
        midiFrequencies.put(67, 392);
        midiFrequencies.put(68, 415);
        midiFrequencies.put(69, 440);
        midiFrequencies.put(70, 466);
        midiFrequencies.put(71, 493);
        midiFrequencies.put(72, 523);
        midiFrequencies.put(73, 554);
        midiFrequencies.put(74, 587);
        midiFrequencies.put(75, 622);
        midiFrequencies.put(76, 659);
        midiFrequencies.put(77, 659);
        midiFrequencies.put(78, 740);
        midiFrequencies.put(79, 784);
        midiFrequencies.put(80, 831);
        midiFrequencies.put(81, 880);
        midiFrequencies.put(82, 932);
        midiFrequencies.put(83, 988);
        midiFrequencies.put(84, 1046);
        midiFrequencies.put(85, 1109);
        midiFrequencies.put(86, 1175);
        midiFrequencies.put(87, 1244);
        midiFrequencies.put(88, 1318);
        midiFrequencies.put(89, 1397);
        midiFrequencies.put(90, 1480);
        midiFrequencies.put(91, 1568);
        midiFrequencies.put(92, 1661);
        midiFrequencies.put(93, 1760);
        midiFrequencies.put(94, 1865);
        midiFrequencies.put(95, 1975);
        midiFrequencies.put(96, 2093);
        midiFrequencies.put(97, 2217);
        midiFrequencies.put(98, 2349);
        midiFrequencies.put(99, 2489);
        midiFrequencies.put(100, 2637);
        midiFrequencies.put(101, 2793);
        midiFrequencies.put(102, 3136);
        midiFrequencies.put(103, 3322);
        midiFrequencies.put(104, 3520);
        midiFrequencies.put(105, 3729);
        midiFrequencies.put(106, 3951);
        midiFrequencies.put(107, 4186);
    }

    private void initMidiPulseWidth(){
        midiPulseWidth = new HashMap<>();

        for(int i = 0; i < 60; i++){
            midiPulseWidth.put(i+1, 1000 + ((i)*20));
        }

        for(int i = 60; i <= 127; i++){
            midiPulseWidth.put(i, 1000 + (i) * 40);
        }

    }

    @Override
    public void onStart(boolean fromBeginning) {

    }

    @Override
    public void onEvent(MidiEvent event, long ms) {
        if(event instanceof NoteOn){
            Integer noteValue = ((NoteOn) event).getNoteValue();
            Integer noteVelocity = ((NoteOn) event).getVelocity();
            Integer freq = midiFrequencies.get(noteValue);
            Integer pwm = midiPulseWidth.get(noteVelocity);
            bluetooth.send(freq.toString() + "m" + pwm.toString() , true);
        }
    }

    @Override
    public void onStop(boolean finished) {
        bluetooth.send("0", true);
    }

}

