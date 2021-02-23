String voice;

int led1 = 2;
int led2 = 3;
int led3 = 4;

void allon() {
  digitalWrite (led1, HIGH);
  digitalWrite (led2, HIGH);
  digitalWrite (led3, HIGH);
}

void alloff() {
  digitalWrite (led1, LOW);
  digitalWrite (led2, LOW);
  digitalWrite (led3, LOW);
}

void setup() {
  Serial.begin(9600);
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);
}

void loop() {
  while(Serial.available()) {
    delay(10);
    char c=Serial.read();
    if(c=='#')
    {break; }
    voice += c;
}

if (voice.length() > 0) {
  Serial.println(voice);
  if (voice == "*all on")
  {allon() ; }
  else if (voice == "*all off")
  {alloff() ; }
  
voice="";
}
}
