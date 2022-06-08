#include <SoftwareSerial.h>

#define RX 3
#define TX 2

#define BAUDRATE 9600

char c = ' ';
String res = "";
boolean new_line = true;

// Instantiation of a Software UART
SoftwareSerial BTserial(RX, TX); // RX | TX

ISR(TIMER1_COMPA_vect)
{
  //BTserial.print("AT");
}



void setup() {  

  // Start Serial Monitor for feedback
  Serial.begin(BAUDRATE);
  
  // HM-10 default speed in AT command mode

  BTserial.begin(9600);

    noInterrupts();
  TCCR1A = 0;
  TCCR1B = 0;
  TCNT1 = 0;
  OCR1A = 80000000;         // elapse = OCR1A_ticks / 80000000 1hz
  TCCR1B |= (1 << WGM12);
  TCCR1B |= (1 << CS12);
  TIMSK1 |= (1 << OCIE1A);
  interrupts();
}

void loop() {
  if(BTserial.available()){
    String s=BTserial.readString();
    //if(c!=10 && c!=13)
    {
      Serial.print(s);
      res += c;
//      Serial.println("---------------");
//      Serial.println(res);
    }
//    else if (c == '\n' || c == '\r')
//    {
//      Serial.println("---------------");
//      Serial.println(res);
//    }
  }
  if(Serial.available()){
    String s=Serial.readString();
    if(c!=10 && c!=13)
    {
      BTserial.print(c);
    }
  }
  
}
