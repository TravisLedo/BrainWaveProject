
#define LED 13
#define BAUDRATE 57600
#define DEBUGOUTPUT 0

#define powercontrol 10

// checksum variables
byte generatedChecksum = 0;
byte checksum = 0; 
int payloadLength = 0;
byte payloadData[64] = {
  0};
byte signalQuality = 0;
byte attention = 0;
byte meditation = 0;

// system variables
long lastReceivedPacket = 0;
boolean bigPacket = false;




String addLeadingZeros(int number) //Add leading zeros so every number sent will always be 3 digits long for consistency
{
  String leadingZeros = "";

  if (number < 10)
  {
    leadingZeros = "00";
  }
  else if (number >= 10 && number < 100)
  {
    leadingZeros = "0";
  }
  else
  {
    leadingZeros = "";

  }

  return leadingZeros;
}


void setup() {

  Serial.begin(BAUDRATE);   // USB

}


byte ReadOneByte() {
  int ByteRead;

  while(!Serial.available());
  ByteRead = Serial.read();

#if DEBUGOUTPUT  
  Serial.print((char)ByteRead);   // echo the same byte out the USB serial (for debug purposes)
#endif

  return ByteRead;
}



void loop() {


  // Look for sync bytes
  if(ReadOneByte() == 170) {
    if(ReadOneByte() == 170) {

      payloadLength = ReadOneByte();
      if(payloadLength > 169)                      //Payload length can not be greater than 169
          return;

      generatedChecksum = 0;        
      for(int i = 0; i < payloadLength; i++) {  
        payloadData[i] = ReadOneByte();            //Read payload into memory
        generatedChecksum += payloadData[i];
      }   

      checksum = ReadOneByte();                      //Read checksum byte from stream      
      generatedChecksum = 255 - generatedChecksum;   //Take one's compliment of generated checksum

        if(checksum == generatedChecksum) {    

        signalQuality = 200;
        attention = 0;
        meditation = 0;

        for(int i = 0; i < payloadLength; i++) {    // Parse the payload to get desired values
          switch (payloadData[i]) {
          case 2:
            i++;            
            signalQuality = payloadData[i];
            bigPacket = true;            
            break;
          case 4:
            i++;
            attention = payloadData[i];                        
            break;
          case 5:
            i++;
            meditation = payloadData[i];
            break;
          case 0x80:
            i = i + 3;
            break;
          case 0x83:
            i = i + 25;      
            break;
          default:
            break;
          } // switch
        } // for loop

#if !DEBUGOUTPUT

          
        if(bigPacket) {
          if(signalQuality == 0)
            digitalWrite(LED, HIGH); //show good signal light
          else
            digitalWrite(LED, LOW);
          //int signalQuality = 0;
          //int attention = random(0, 100);
          //int meditation = random(0, 100);

          Serial.print(addLeadingZeros(signalQuality));
          Serial.print(signalQuality); //send value of quality, 0 is good
          Serial.print(",");

          Serial.print(addLeadingZeros(attention));
          Serial.print(attention);  //send value of attention
          Serial.print(",");

          Serial.print(addLeadingZeros(meditation));
          Serial.print(meditation); //send value of meditation
          Serial.print("\n");
                   
        }
#endif        
        bigPacket = false;        
      }
      else {
        // Checksum Error
      }  // end if else for checksum
    } // end if read 0xAA byte
  } // end if read 0xAA byte
}
