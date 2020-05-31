#include <PWM.h>

#define musicPin 10 // the output is on one pin, but there is polyphony due to the 4 timers. 
#define duty_cycle 32767 // default PWM

void setup(){
  Serial.begin(9600);
  pinMode(musicPin, OUTPUT);
  InitTimersSafe();
}

String freq;

int current_pwm_value = 0;

const char midi_identifier[] = "m";

void loop(){
  bool is_pwm = false;
  bool is_midi = false;
  while(Serial.available()) {
    char c = Serial.read();
    if(c == 'p'){
      is_pwm = true; 
    }
    else if(c == 'm'){
      is_midi = true;
      freq += c;
    }
    else{
      freq += c;
    }
    delay(2);
  }
  if(freq.length() > 0){
      int frequency = freq.toInt(); // handles cases of pure frequency or  pwm change. 
      if(frequency != 0){
        if(is_pwm == false && is_midi == false){
          //Serial.println("FREQ CHANGE");
          SetPinFrequencySafe(musicPin, frequency);
          if(current_pwm_value == 0) pwmWriteHR(musicPin, duty_cycle); // use the default one if no current_pwm_value set
          else pwmWriteHR(musicPin, current_pwm_value); // otherwise use the global variable
        }
        else if(is_pwm == true){
          //Serial.println("PWM CHANGE");
          current_pwm_value = frequency;
          pwmWriteHR(musicPin, current_pwm_value);
        }
        else if(is_midi == true){
          int Frequency;
          int Pwm;
          //Serial.println("MIDI NOTE");
          char *token;
          token = strtok(&freq[0], midi_identifier);
          int counter = 0;
          while(token != NULL){
             counter++;
             if(counter == 1) Frequency = atoi(token);
             else Pwm = atoi(token);
             token = strtok(NULL, midi_identifier);
          }
          SetPinFrequencySafe(musicPin, Frequency);
          pwmWriteHR(musicPin, Pwm);
        }
      }
      else noTone(musicPin);
      freq = "";
  }
}
