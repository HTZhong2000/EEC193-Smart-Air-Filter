/**
   BasicHTTPSClient.ino

    Created on: 20.08.2018

*/

#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

#include <WiFiClientSecureBearSSL.h>
// Fingerprint for demo URL, expires on June 2, 2021, needs to be updated well before this date
const uint8_t fingerprint[20] = {0x40, 0xaf, 0x00, 0x6b, 0xec, 0x90, 0x22, 0x41, 0x8e, 0xa3, 0xad, 0xfa, 0x1a, 0xe8, 0x25, 0x41, 0x1d, 0x1a, 0x54, 0xb3};

int fan, pir, humid_sw,ctrl;
int fan_old = 0;
int ctrlmode = 0;
float tmpf, tmpc, tmpfr, tmpcr, humid, pm25, pm10;

const int pinout = 0;

ESP8266WiFiMulti WiFiMulti;

void setup() {

  Serial.begin(115200);
  // Serial.setDebugOutput(true);


  for (uint8_t t = 4; t > 0; t--) {
    Serial.printf("[SETUP] WAIT ...\n");
    Serial.flush();
    delay(1000);
  }

  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP("A65", "5309657854");

  pinMode(0, OUTPUT);
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
  fan = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(2, 4);
  //Serial.print(substr);
  pir = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(4, 6);
  //Serial.print(substr);
  tmpf = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(6, 8);
  //Serial.print(substr);
  tmpf += str2dec(substr) / 100.0;
  //Serial.print(", ");

  substr = input.substring(8, 10);
  //Serial.print(substr);
  tmpc = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(10, 12);
  //Serial.print(substr);
  tmpc += str2dec(substr) / 100.0;
  //Serial.print(", ");

  substr = input.substring(12, 14);
  //Serial.print(substr);
  humid = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(14, 16);
  //Serial.print(substr);
  humid += str2dec(substr) / 100.0;
  //Serial.print(", ");

  substr = input.substring(16, 20);
  //Serial.print(substr);
  pm25 = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(20, 22);
  //Serial.print(substr);
  pm25 += str2dec(substr) / 100.0;
  ////Serial.print(", ");

  substr = input.substring(22, 26);
  ////Serial.print(substr);
  pm10 = str2dec(substr);
  //Serial.print(", ");

  substr = input.substring(26, 28);
  //Serial.print(substr);
  pm10 += str2dec(substr) / 100.0;
  //Serial.println(". ");
  
  substr=input.substring(28,30);
  ctrl=str2dec(substr);
  
//  Serial.print(fan);
//  Serial.print(", ");
//  Serial.print(pir);
//  Serial.print(", ");
//  Serial.print(tmpf);
//  Serial.print(", ");
//  Serial.print(tmpc);
//  Serial.print(", ");
//  Serial.print(humid);
//  Serial.print(", ");
//  Serial.print(pm25);
//  Serial.print(", ");
//  Serial.print(pm10);
//  Serial.println(". ");

}

String payloadFormat() {    //TODO here
  String payload =    " {\"Device\": \"test\", \"Client\": \"MCU\", \"Timestamp\": ";
  payload = payload + " \"12345\", ";
  payload = payload + " \"Fan\": \"";
  payload = payload + String(fan);
  payload = payload + "\", \"tmpF\": \"";
  payload = payload + String(tmpf);
  payload = payload + "\", \"ctrlMode\": \"";
  payload = payload + String(ctrl);
  payload = payload + "\", \"tmpC\": \"";
  payload = payload + String(tmpc);
  payload = payload + "\", \"humid\": \"";
  payload = payload + String(humid);
  payload = payload + "\", \"PM25\": \"";
  payload = payload + String(pm25);
  payload = payload + "\", \"PM10\": \"";
  payload = payload + String(pm10);
  payload = payload + "\", \"PIR\": \"";
  payload = payload + String(pir);
  payload = payload + "\" ";
  payload = payload + " } ";

  return payload;
}

void loop() {

  // wait for WiFi connection
  if ((WiFiMulti.run() == WL_CONNECTED)) {
    Serial.println("Connected to AP!");
    std::unique_ptr<BearSSL::WiFiClientSecure>client(new BearSSL::WiFiClientSecure);

    //client->setFingerprint(fingerprint);
    // Or, if you happy to ignore the SSL certificate, then use the following line instead:
    client->setInsecure();

    HTTPClient https;

    Serial.print("[HTTPS] begin...\n");
    if (https.begin(*client, "https://7a1reou5uk.execute-api.us-west-2.amazonaws.com/default/EEC193_POST")) {  // HTTPS

      https.addHeader("Content-Type", "application/json");
      Serial.print("Wait for input...\n");
      while (Serial.available() == 0) {
      }
      String fetched_data = Serial.readString();
      Serial.print("[HTTPS] POST...\n");
      // start connection and send HTTP header
      decodePayload(fetched_data);



      int httpCode = https.POST(payloadFormat());

      // httpCode will be negative on error
      if (httpCode > 0) {
        // HTTP header has been send and Server response header has been handled
//        Serial.printf("[HTTPS] POST... code: %d\n", httpCode);

        // file found at server
        if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
          String payload = https.getString();
          DynamicJsonDocument doc(1024);
          deserializeJson(doc, payload);

          String response = doc["body"];

          Serial.println(response);


        }
      } else {
        Serial.printf("[HTTPS] GET... failed, error: %s\n", https.errorToString(httpCode).c_str());
      }

      https.end();
    }
    else {
      Serial.printf("[HTTPS] Unable to connect\n");
    }
  }
  fan_old = fan;
  Serial.println("Wait 3s before next round...");
  delay(3);
}
