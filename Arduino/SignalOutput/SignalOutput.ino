#include <PWM.h>

#define musicPin 10 // the output is on one pin, but there is polyphony due to the 4 timers. 
#define duty_cycle 32767 // default PWM

void setup(){
  Serial.begin(9600);
  pinMode(musicPin, OUTPUT);
  InitTimersSafe();
}

String freq;
String pwm;

void loop(){
  bool is_pwm = false;
  bool is_midi = false;
  while(Serial.available()) {
    char c = Serial.read();
    if(c == 'p'){
      is_pwm = true; 
    }
    else if(c == 'd'){
      is_midi = true;
    }
    else if(is_midi == true){
      pwm += c;
    }
    else{
      freq += c;
    }
    delay(2);
  }
  if(freq.length() > 0){
    int frequency = freq.toInt();
    int pulse_width = pwm.toInt();
      if(frequency != 0){
        if(is_pwm == false && is_midi == false){
          //Serial.println("FREQ CHANGE");
          SetPinFrequencySafe(musicPin, frequency);
          pwmWriteHR(musicPin, duty_cycle);
        }
        else if(is_pwm == true){
          //Serial.println("PWM CHANGE");
          pwmWriteHR(musicPin, frequency);
        }
        else if(is_midi = true){
          //Serial.println("MIDI NOTE");
          SetPinFrequencySafe(musicPin, frequency);
          pwmWriteHR(musicPin, pulse_width);
        }
      }
      else noTone(musicPin);
      freq = "";
      pwm = "";
  }
}
