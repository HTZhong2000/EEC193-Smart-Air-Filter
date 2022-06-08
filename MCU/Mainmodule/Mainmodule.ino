#include <UTFT.h>
#include <URTouch.h>
#include "DHT.h"
#include <avr/pgmspace.h>

#define DHTPIN 9     // Digital pin connected to the DHT sensor
#define DHTTYPE DHT11   // DHT 11

//==== Creating Objects
UTFT    myGLCD(ILI9341_16, 38, 39, 40, 41);
URTouch  myTouch( 6, 5, 4, 3, 2);

//==== Defining Variables
extern uint8_t SmallFont[];
extern uint8_t BigFont[];
extern uint8_t SevenSegNumFont[];
extern uint8_t arial_bold[];
extern uint8_t arial_normal[];
extern uint8_t Arial_round_16x24[];
extern unsigned short BT[2440];

int x, y;

char currentPage, selectedUnit;

//PM Sensor
int stream[10];
int i = 0;

long duration;
int distanceInch, distanceCm;
bool humidState = false;
bool BTMode = false;
bool ATMode = false;


const int fanRelay = 8;
const int PIRpin = 10;
const int humidPin = 11;
const int interruptPin = 21;
int xR = 120;
bool ctrl = false;
int cnt = 0;
bool fan = false;
bool pir = false;
bool fanold = false;
bool cd = false;
float tmpf, tmpc, humid, pm25, pm10;
String fetched_data;

char BTid[3];
String BTtag[3];

// ====== Custom Funtions ======
// drawHomeScreen - Custom Function
void drawHomeScreen() {

  // Title
  myGLCD.setBackColor(0, 0, 0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(Arial_round_16x24); // Sets font to big
  myGLCD.print("Smart Air Filter", CENTER, 0); // Prints the string on the screen
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0, 32, 319, 32); // Draws the red line
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(Arial_round_16x24); // Sets the font to small
  myGLCD.print("Team RSQ", CENTER, 41); // Prints the string


  // Button - FAN
  if (fan == true)
  {
    myGLCD.setColor(255, 102, 102);
    myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
    myGLCD.setColor(255, 255, 255);
    myGLCD.drawRoundRect (35, 80, 215, 150);
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setBackColor(255, 102, 102);
    myGLCD.print("FAN OFF", 80 , 105);
    digitalWrite(fanRelay, HIGH);
  }
  else
  {
    myGLCD.setColor(16, 167, 103);
    myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
    myGLCD.setColor(255, 255, 255);
    myGLCD.drawRoundRect (35, 80, 215, 150);
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setBackColor(16, 167, 103);
    myGLCD.print("FAN ON", 80 , 105);
    digitalWrite(fanRelay, LOW);
  }


  // Button - STATUS
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (35, 160, 215, 230);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (35, 160, 215, 230);
  myGLCD.setFont(Arial_round_16x24);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("STATUS", 80, 185);


  // Button - BT
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (225, 80, 285, 230);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (225, 80, 285, 230);
  myGLCD.setBackColor(3, 58, 146);
  myGLCD.drawBitmap (235, 125, 40, 61, BT, 1);

}

// Highlights the button when pressed
void drawFrame(int x1, int y1, int x2, int y2) {
  myGLCD.setColor(255, 0, 0);
  myGLCD.drawRoundRect (x1, y1, x2, y2);
  while (myTouch.dataAvailable())
    myTouch.read();
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (x1, y1, x2, y2);
}


//====================================================

void drawTempSensor() {
  myGLCD.setColor(100, 285, 203);
  myGLCD.fillRoundRect (10, 10, 60, 41);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (10, 10, 60, 41);
  myGLCD.setFont(Arial_round_16x24);
  myGLCD.setBackColor(100, 285, 203);
  myGLCD.print("<-", 18, 15);
  myGLCD.setBackColor(0, 0, 0);
  myGLCD.setFont(Arial_round_16x24);
  myGLCD.print("Status", 70, 15);
  myGLCD.setColor(255, 0, 0);
  myGLCD.drawLine(0, 50, 319, 50);
  myGLCD.setColor(223, 77, 55);
  myGLCD.fillRoundRect (10, 60, 80, 140);
  myGLCD.setColor(225, 255, 255);
  myGLCD.drawRoundRect (10, 60, 80, 140);
  myGLCD.setBackColor(223, 77, 55);
  myGLCD.setColor(255, 255, 255);
  myGLCD.print("`C", 27, 90);
  myGLCD.setColor(223, 77, 55);
  myGLCD.fillRoundRect (10, 150, 80, 230);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (10, 150, 80, 230);
  myGLCD.setBackColor(223, 77, 55);
  myGLCD.setColor(255, 255, 255);
  myGLCD.print("`F", 27, 180);
}

void drawBT() {
  myGLCD.setColor(100, 285, 203);
  myGLCD.fillRoundRect (10, 10, 60, 41);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (10, 10, 60, 41);
  myGLCD.setFont(Arial_round_16x24);
  myGLCD.setBackColor(100, 285, 203);
  myGLCD.print("<-", 18, 15);
  myGLCD.setBackColor(0, 0, 0);
  myGLCD.setFont(Arial_round_16x24);
  myGLCD.print("Bluetooth", 70, 15);
  myGLCD.setColor(255, 0, 0);
  myGLCD.drawLine(0, 50, 319, 50);
}

//====================================================
void getStat() {

  myGLCD.setFont(Arial_round_16x24);
  myGLCD.setColor(255, 255, 255);
  myGLCD.setBackColor(0, 0, 0);
  myGLCD.print("Temperature:", 90, 60);
  myGLCD.print("Humidity:", 90, 120);
  myGLCD.setColor(0, 255, 0);
  myGLCD.setBackColor(0, 0, 0);
  myGLCD.printNumF(humid, 2, 90, 150);
  myGLCD.setColor(255, 255, 255);
  myGLCD.print("%", 180, 150);
  myGLCD.print("PM2.5:", 90, 180);
  myGLCD.print("PM10:", 90, 210);
  myGLCD.setColor(0, 255, 0);
  myGLCD.setBackColor(0, 0, 0);
  myGLCD.printNumF(pm25, 2, 190, 180);
  myGLCD.printNumF(pm10, 2, 190, 210);

  if (selectedUnit == '0') {
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setColor(0, 255, 0);
    myGLCD.setBackColor(0, 0, 0);
    myGLCD.printNumF(tmpc, 2, 90, 90);
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setColor(255, 255, 255);
    myGLCD.print("`C", 180, 90);
  }

  if (selectedUnit == '1') {
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setColor(0, 255, 0);
    myGLCD.setBackColor(0, 0, 0);
    myGLCD.printNumF(tmpf, 2, 90, 90);
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setColor(255, 255, 255);
    myGLCD.print("`F", 180, 90);
  }

  delay(10);
}


void getPIR() {
  if (pir == HIGH)
  {
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setColor(255, 255, 255);
    myGLCD.setBackColor(255, 0, 0);
    myGLCD.print("Detected!!!!", 10, 90);
  }
  else
  {
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setColor(255, 255, 255);
    myGLCD.setBackColor(0, 255, 0);
    myGLCD.print("Not Detected", 10, 90);
  }
}


void getBT() {
  if (!BTMode) {
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setColor(255, 255, 255);
    myGLCD.setBackColor(0, 0, 0);
    myGLCD.print("Not Connected", 10, 90);
  }
  else
  {
    myGLCD.print("NMSL", 10, 90);
  }
}

void printFloat(float f2, bool t) {
  int higher = int(f2 * 100 - int(f2 * 100) % 100) / 100;
  int lower = int(f2 * 100) % 100;

  if (t)
  {
    if (higher < 16)
      Serial2.print("000");
    else if ( higher > 15 && higher < 256)
      Serial2.print("00");
    else if ( higher > 255 && higher < 4096)
      Serial2.print("0");
    Serial2.print(higher, HEX);
    if (lower < 16 )
      Serial2.print('0');
    Serial2.print(lower, HEX);
  }
  else
  {
    if (higher < 16)
      Serial2.print("0");
    Serial2.print(higher, HEX);
    if (lower < 16 )
      Serial2.print('0');
    Serial2.print(lower, HEX);
  }
}

void printInt(int dec) {
  if (dec < 16)
    Serial2.print('0');
  Serial2.print(dec, HEX);
}

void encode() {

  printInt(fan);
  printInt(pir);
  printFloat(tmpf, 0);
  printFloat(tmpc, 0);
  printFloat(humid, 0);
  printFloat(pm25, 1);
  printFloat(pm10, 1);
  printInt(ctrl);
  Serial2.println('#');

  Serial.print(fan);
  Serial.print(", ");
  Serial.print(pir);
  Serial.print(", ");
  Serial.print(tmpf);
  Serial.print(", ");
  Serial.print(tmpc);
  Serial.print(", ");
  Serial.print(humid);
  Serial.print(", ");
  Serial.print(pm25);
  Serial.print(", ");
  Serial.print(pm10);
  Serial.println(". ");
  Serial.println("done");
}

int str2dec(String str)
{
  int res = 0;
  int len = str.length();
  for (int i = len - 1; i >= 0; i--)
  {
    if (isDigit(str[i]))
      res += (int(str[i]) - int('0')) * pow(16, len - 1 - i);
    else
      res += (int(str[i]) - int('A') + 10) * pow(16, len - 1 - i);
  }
  return res;
}

void decodePayload(String input) {
  String substr;

  substr = input.substring(0, 2);
  //Serial.print(substr);
  tmpf = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(2, 4);
  //Serial.print(substr);
  tmpf += str2dec(substr) / 100.0;
  //Serial.print(", ");

  substr = input.substring(4, 6);
  //Serial.print(substr);
  tmpc = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(6, 8);
  //Serial.print(substr);
  tmpc += str2dec(substr) / 100.0;
  //Serial.print(", ");


  substr = input.substring(8, 10);
  //Serial.print(substr);
  humid = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(10, 12);
  //Serial.print(substr);
  humid += str2dec(substr) / 100.0;
  //Serial.print(", ");

  substr = input.substring(12, 16);
  //Serial.print(substr);
  pm25 = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(16, 18);
  //Serial.print(substr);
  pm25 += str2dec(substr) / 100.0;
  ////Serial.print(", ");

  substr = input.substring(18, 22);
  ////Serial.print(substr);
  pm10 = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(22, 24);
  //Serial.print(substr);
  pm10 += str2dec(substr) / 100.0;
  //Serial.println(". ");

}

ISR(TIMER1_COMPA_vect)
{

  if (cnt >= 300)
  {

    if (fan == fanold) {
      ctrl = 0;
    } else {
      ctrl = 1;
    }
    encode();
    Serial2.print(" ");


    cnt = 0;
  }
  cnt++;
}

//====================================================
void ISRHandler()
{
  fan = !fan;
  if (fan == true)
  {
    myGLCD.setColor(255, 102, 102);
    myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
    myGLCD.setColor(255, 255, 255);
    myGLCD.drawRoundRect (35, 80, 215, 150);
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setBackColor(255, 102, 102);
    myGLCD.print("FAN OFF", 80 , 105);
    digitalWrite(fanRelay, HIGH);
  }
  else
  {
    myGLCD.setColor(16, 167, 103);
    myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
    myGLCD.setColor(255, 255, 255);
    myGLCD.drawRoundRect (35, 80, 215, 150);
    myGLCD.setFont(Arial_round_16x24);
    myGLCD.setBackColor(16, 167, 103);
    myGLCD.print("FAN ON", 80 , 105);
    digitalWrite(fanRelay, LOW);
  }
}


//====================================================

void setup() {
  noInterrupts();
  TCCR1A = 0;
  TCCR1B = 0;
  TCNT1 = 0;
  OCR1A = 1999;
  TCCR1B |= (1 << WGM12);
  TCCR1B |= (1 << CS12);
  TIMSK1 |= (1 << OCIE1A);
  interrupts();

  // Initial setup
  myGLCD.InitLCD();
  myGLCD.clrScr();
  myTouch.InitTouch();
  myTouch.setPrecision(PREC_MEDIUM);

  Serial.begin(115200);
  Serial1.begin(9600);
  Serial2.begin(115200);

  //TCCR0B = TCCR0B & B11111000 | B00000001; // for PWM frequency of 62500 Hz
  //TCCR4B = TCCR4B & B11111000 | B00000001;   // for PWM frequency of 31372.55 Hz

  pinMode(fanRelay, OUTPUT);
  pinMode(PIRpin, INPUT);

  drawHomeScreen();  // Draws the Home Screen
  currentPage = '0'; // Indicates that we are at Home Screen
  selectedUnit = '0'; // Indicates the selected unit for the first example, cms or inches

  digitalWrite(fanRelay, LOW);
  pinMode(interruptPin, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(interruptPin), ISRHandler, FALLING);
}

void loop() {

  pir = digitalRead(PIRpin);
  if (Serial1.available()) {
    fetched_data = Serial1.readString();
    //    Serial.println("rec");
    //    if (!ATMode) {
    //      if (fetched_data.equals("OK+Get:1"))
    //      {
    //        Serial.println("BT NoConnect");
    //        ATMode = true;
    //      }
    //    }
    //    else
    //    {
    //      Serial.println(fetched_data);
    //      if (fetched_data.substring(0, 14).equals("OK+DISCSOK+DIS"))
    //        for (int i = 0; i < fetched_data.length() - 14; i ++)
    //          Serial.println(fetched_data.substring(i, i + 14));
    //    }
    //    if (BTMode)
    decodePayload(fetched_data);
  }

  if (Serial2.available()) {
    char inByte = Serial2.read();
    Serial.write(inByte);
    if (inByte == '0' || inByte == '1')
    {

      if (cd == false) {

        if (inByte == '0') {
          fan = false;
          Serial.println(fan);
        }
        else if (inByte == '1' ) {
          fan = true;
          Serial.println(fan);
        }
        if (currentPage == '0')
        {
          if (fan == true)
          {
            myGLCD.setColor(255, 102, 102);
            myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
            myGLCD.setColor(255, 255, 255);
            myGLCD.drawRoundRect (35, 80, 215, 150);
            myGLCD.setFont(Arial_round_16x24);
            myGLCD.setBackColor(255, 102, 102);
            myGLCD.print("FAN OFF", 80 , 105);
            digitalWrite(fanRelay, HIGH);

          }
          else
          {
            myGLCD.setColor(16, 167, 103);
            myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
            myGLCD.setColor(255, 255, 255);
            myGLCD.drawRoundRect (35, 80, 215, 150);
            myGLCD.setFont(Arial_round_16x24);
            myGLCD.setBackColor(16, 167, 103);
            myGLCD.print("FAN ON", 80 , 105);
            digitalWrite(fanRelay, LOW);
          }
        }
      }
      else {
        cd = false;

      }
    }
    fanold = fan;
  }




  // Home Screen
  if (currentPage == '0') {
    if (myTouch.dataAvailable()) {
      myTouch.read();
      x = myTouch.getX(); // X coordinate where the screen has been pressed
      y = myTouch.getY(); // Y coordinates where the screen has been pressed

      //FAN
      if ((x >= 35) && (x <= 215) && (y >= 80) && (y <= 150)) {
        fan = !fan;
        drawFrame(35, 80, 215, 150); // Custom Function -Highlighs the buttons when it's pressed
        cd = true;
      }

      if (fan == true)
      {
        myGLCD.setColor(255, 102, 102);
        myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
        myGLCD.setColor(255, 255, 255);
        myGLCD.drawRoundRect (35, 80, 215, 150);
        myGLCD.setFont(Arial_round_16x24);
        myGLCD.setBackColor(255, 102, 102);
        myGLCD.print("FAN OFF", 80 , 105);
        digitalWrite(fanRelay, HIGH);

      }
      else
      {
        myGLCD.setColor(16, 167, 103);
        myGLCD.fillRoundRect (35, 80, 215, 150); //175 285
        myGLCD.setColor(255, 255, 255);
        myGLCD.drawRoundRect (35, 80, 215, 150);
        myGLCD.setFont(Arial_round_16x24);
        myGLCD.setBackColor(16, 167, 103);
        myGLCD.print("FAN ON", 80 , 105);
        digitalWrite(fanRelay, LOW);
      }

      //STATUS
      if ((x >= 35) && (x <= 215) && (y >= 160) && (y <= 230)) {
        drawFrame(35, 160, 215, 230);
        currentPage = '1';
        myGLCD.clrScr();
        drawTempSensor();
      }

      if ((x >= 225) && (x <= 285) && (y >= 80) && (y <= 230)) {
        drawFrame(225, 285, 80, 230);
        currentPage = '2';
        myGLCD.clrScr();
        drawBT();
      }

    }
  }


  // Temp
  if (currentPage == '1') {
    getStat();
    if (myTouch.dataAvailable()) {
      myTouch.read();
      x = myTouch.getX();
      y = myTouch.getY();
      // F
      if ((x >= 10) && (x <= 100) && (y >= 150) && (y <= 230)) {
        drawFrame(10, 150, 80, 230);
        selectedUnit = '1';
      }
      // C
      if ((x >= 10) && (x <= 100) && (y >= 60) && (y <= 140)) {
        drawFrame(10, 60, 80, 140);
        selectedUnit = '0';
      }
      if ((x >= 10) && (x <= 60) && (y >= 10) && (y <= 41)) {
        drawFrame(10, 10, 60, 41);
        currentPage = '0';
        myGLCD.clrScr();
        drawHomeScreen();

      }
    }
  }

  if (currentPage == '2') {
    getBT();
    if (myTouch.dataAvailable()) {
      myTouch.read();
      x = myTouch.getX();
      y = myTouch.getY();

      if ((x >= 10) && (x <= 60) && (y >= 10) && (y <= 41)) {
        drawFrame(10, 10, 60, 41);
        currentPage = '0';
        myGLCD.clrScr();
        drawHomeScreen();

      }
    }
  }


}
