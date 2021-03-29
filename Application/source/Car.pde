ArrayList<Float> inputs = new ArrayList();

class Car{
  float x, y, w, h;
  int definedX, definedY;
  
  float vel;
  
  PImage car;
  
  float angle = 0;
  
  float topSpeed = carTopSpeed.currentVal;
  
  Colour col = new Colour(color(random(0, 255), random(0, 255), random(0, 255)));
  
  float score = 0;
  
  float time = 0;
  
  RayCast front, left, right, frontLeft, frontRight;
  
  boolean crashed = false;
  
  Neural_Network network;
  
  Car(int x, int y, int w, int h){
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    
    this.definedX = x;
    this.definedY = y;
    
    vel = carTopSpeed.currentVal/2;
    
    car = loadImage("car.png");
    
    front = new RayCast(x, y, angle);
    frontLeft = new RayCast(x, y, angle + PI/4);
    left = new RayCast(x, y, angle + PI/2);
    frontRight = new RayCast(x, y, angle - PI/4);
    right = new RayCast(x, y, angle - PI/2);
    
    network = new Neural_Network(5, 10, 5);
  }
  
  void restart(){
      this.x = definedX;
      this.y = definedY;
      
      crashed = false;
      angle = 0;
      score = 0;
      time = 0;
      
      topSpeed = carTopSpeed.currentVal;
  }
  
  void restart(float x, float y){
      this.x = x;
      this.y = y;
      
      crashed = false;
      angle = 0;
      score = 0;
      time = 0;
      
      topSpeed = carTopSpeed.currentVal;
  }
  
  void moveLeft(float amount){
      this.x-=amount;
  }
  
  void randomize(){
      network = new Neural_Network(5, 10, 5);
      
      col = new Colour(color(random(0, 255), random(0, 255), random(0, 255)));
  }
  
  void makeMove(){
      inputs.clear();
      if (crashed){
          return;
      }
      
      score+=Math.pow(vel, 2)/100;
      
    imageMode(CENTER);
    
    front.cast(x, y, angle);
    left.cast(x, y, angle + PI/2);
    right.cast(x, y, angle - PI/2);
    frontRight.cast(x, y, angle - PI/4);
    frontLeft.cast(x, y, angle + PI/4);
    
    if (get((int) x, (int) y) != color(255, 150, 0)){
        crashed = true;
        numDead++;
    }
    
    inputs.add(map(front.lineLength, 0, width, 0, 10));
    inputs.add(map(right.lineLength, 0, width, 0, 10));
    inputs.add(map(left.lineLength, 0, width, 0, 10));
    inputs.add(map(frontRight.lineLength, 0, width, 0, 10));
    inputs.add(map(frontLeft.lineLength, 0, width, 0, 10));
    
    ArrayList<Float> guesses = network.guess(inputs);
    
    float min = -1;
    for (Float guess : guesses){
        if (guess > min){
            min = guess;
        }
    }
    int ind = guesses.indexOf(min);
    
    switch(ind){
        case 0:
            turnLeft();
            break;
        case 1:
            turnRight();
            break;
        case 2:
            accelerate();
            break;
        case 3: 
            decelerate();
            break;
        case 4:
            break;
    }
    
    x+=vel*Math.cos(angle);
    y+=vel*Math.sin(angle);
    
    if (perlinMode.activated){
        x-=perlinRoad.speed;
    }
    
    time++;
    
    if (vel > topSpeed){
        vel = topSpeed;
    }
  }
  
  void Draw(){
    
    if (crashed){
          return;
      }
      
    pushMatrix();
    translate(x, y);
    rotate(angle);
    image(car, 0, 0, w, h);
    popMatrix();
    
    if (displayRays.activated){
        if (this != currentBest){
            front.Draw();
            right.Draw();
            left.Draw();
            frontRight.Draw();
            frontLeft.Draw();
        } else {
            front.Draw(color(255,0,0));
            right.Draw(color(255,0,0));
            left.Draw(color(255,0,0));
            frontRight.Draw(color(255,0,0));
            frontLeft.Draw(color(255,0,0));
        }
    }
    
    ellipseMode(CENTER);
    noStroke();
    fill(col.colour);
    ellipse(x, y, 10, 10);
  }
  
  void accelerate(){
      if (vel == 0){
          vel = 0.1;
      } else if (vel < topSpeed) {
          vel*=1.1;
      } else {
          vel = topSpeed;
      }
  }
  
  void decelerate(){
      if (vel > 0){
          vel/=1.1;
      } else {
          vel = 0;
      }
  }
  
  void turnRight(){
      angle+=0.2;
  }
  
  void turnLeft(){
      angle-=0.2;
  }
}
