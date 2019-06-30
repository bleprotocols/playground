//Blatantly stolen from https://github.com/buttshock/buttshock-et302r
//Thanks qdot :)

const byte UP=1;
const byte DOWN=2;
const byte ON=4;
const byte OFF=8;



void setup() {
  Serial.begin(9600);
  DDRD = 0;  // all inputs
}

void sendCommand(int x1) {
  unsigned long count=0;
  unsigned long ticks=millis()+100;
  //Giving the poor et302r some time greatly improves accuracy of button presses
  while(true){
      while((PIND&B1000) == 0 && millis() < ticks) {};
      if(count>15 || millis() > ticks)break;
      while((PIND&B1000)  != 0 && millis() < ticks) {}; 
      count++;
  }


  DDRD = DDRD | B100; // pin 2 output
  DDRD = DDRD | B1000; // pin 3 output
  delayMicroseconds(7);
  PORTD = B1000; // pin 3 high
  delayMicroseconds(8);
  PORTD = 0; // pin 3 low
  delayMicroseconds(7);
  PORTD |= B100; // pin 2 high
  DDRD = DDRD & B11100111; // pin 3,4 input


  while((PIND&B10000) != 0) {}; // wait for clock L

  for (int i=0; i<8; i++) {
    PORTD = (x1&1)<<2;
    x1=x1>>1;
    while((PIND&B10000) == 0) {};     
    if (i<7) while((PIND&B10000) != 0) {}; // wait for clock L->H
  }
  
  PORTD=0;
  DDRD = DDRD & B11111011; // pin 2 input
}

void pressButton( byte button, int duration){
  unsigned long until=millis() + duration;

  while(millis() < until){
    sendCommand(button);
  }
}

void loop() {
 if(Serial.available()>=5){
    while(Serial.read()!=0xff); //Read until we encounter a "start command"
    byte button=Serial.read();
    unsigned long count=(Serial.read()<<8) | Serial.read();
    
    //Valid command
    if(Serial.read()==0xff){
      switch(button){
        case 1:  pressButton(OFF | UP,count);      break; //mode ++
        case 2:  pressButton(OFF | DOWN,count);    break; //mode --
        case 3:  pressButton(UP,count);            break; //powerA++
        case 4:  pressButton(DOWN,count);          break; //powerA--
        case 5:  pressButton(ON | UP,count);       break; //powerB++
        case 6:  pressButton(ON | DOWN,count);     break; //powerB--
        case 7:  pressButton(ON ,count);           break; //training button 1
        case 8:  pressButton(DOWN ,count);         break; //training button 2
        case 9:  pressButton(UP ,count);           break; //training button 3
      }

     
      Serial.write(0xfe);
      Serial.write(button);
      Serial.write((byte)(count>>8));
      Serial.write((byte)(count&0xff));
      Serial.write(0xfe);
    }else{
      Serial.write(0xff);
    }
   
 }
}
