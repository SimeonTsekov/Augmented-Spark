package com.example.miditeslacoilapp.ui;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.miditeslacoilapp.ViewModel.BluetoothViewModel;
import com.example.miditeslacoilapp.R;
import com.example.miditeslacoilapp.EventListeners.EventListener;
import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.util.MidiProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class MidiFragment extends Fragment {
    private ListView midiFiles;
    private ImageButton play;

    private Handler handler = new Handler();

    private BluetoothSPP bluetoothSPP;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothViewModel bluetoothViewModel = ViewModelProviders.of(getActivity()).get(BluetoothViewModel.class);
        bluetoothSPP = bluetoothViewModel.getBluetoothSPP();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_midi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        midiFiles = view.findViewById(R.id.midiSongs);
        play = view.findViewById(R.id.on);
    }

    @Override
    public void onStart() {
        super.onStart();

        final ArrayList<File> songs = readMidiFiles(Environment.getExternalStorageDirectory());

        String[] midiFilesNames = new String[songs.size()];

        for(int i = 0; i < songs.size(); i++){
            midiFilesNames[i] = songs.get(i).getName().replace(".mid", "");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.midi_file_layout, R.id.singleMidi, midiFilesNames);
        midiFiles.setAdapter(arrayAdapter);

        midiFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = songs.get(position);
                if(file.isFile()) {
                    MidiListenerThread midiListener = new MidiListenerThread(file);
                    new Thread(midiListener).start();
                }
            }
        });
    }


    private ArrayList<File> readMidiFiles(File root){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = root.listFiles();

        for(File file : files){
            if(file.isDirectory()){
                arrayList.addAll(readMidiFiles(file));
            }
            else if(file.getName().endsWith(".mid")){
                arrayList.add(file);
            }
        }
        return arrayList;
    }


    private class MidiListenerThread implements Runnable{
        private File file;
        private MidiProcessor processor;

        MidiListenerThread(File file){
            this.file = file;
        }

        @Override
        public void run() {
            MidiFile midi;
            try {
                midi  = new MidiFile(file);
            }
            catch(IOException e) {
                e.printStackTrace();
                return;
            }

            processor = new MidiProcessor(midi);

            EventListener eventListener = new EventListener();
            eventListener.setBluetooth(bluetoothSPP);
            processor.registerEventListener(eventListener, MidiEvent.class);

            handler.post(new Runnable() {
                @Override
                public void run() {

                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!processor.isRunning()){
                                processor.start();
                                play.setBackground(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                            } else {
                                processor.stop();
                                play.setBackground(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                            }
                        }
                    });
                }
            });
        }
    }

}