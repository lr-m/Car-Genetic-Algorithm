import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Car_NN extends PApplet {

ArrayList < Car > cars;
int numDead = 0;
int numCars = 50;
int generation = 0;
float keepPercentage = 0.25f;

Slider laserSpeed, speedSlider, trackWidth, carTopSpeed, perlinMapSpeed, perlinMapIntensity;
TickBox laserOn, drawChamp, perlinMode, trackMode, displayRays;

ArrayList < Car > topCars = new ArrayList();

boolean drawingNetwork;
boolean drawingGraph;

ArrayList < Float > topScores = new ArrayList();

int mode = 0;
boolean modeSelected = false;

ArrayList < Colour > topColours = new ArrayList();

Laser laser;

Car currentBest;

boolean trackDrawn = false;
boolean startSet = false;

PImage track;

Perlin_Road perlinRoad;

boolean drawing;

int perlinStartX = 200;

boolean drawingChampion = false;

public void settings() {
    fullScreen();
}

Start start;

public void setup() {
    textAlign(CENTER, CENTER);
    cars = new ArrayList();
    background(0, 255, 0);

    laserSpeed = new Slider(500, height - 50, 150, 25, 0, 5, "Laser Speed: ", 15, false);
    speedSlider = new Slider(50, height - 50, 150, 25, 1, 1000, "Iterations/frame: ", 15, false);
    carTopSpeed = new Slider(275, height - 50, 150, 25, 1, 20, "Car Top Speed: ", 15, false);
    trackWidth = new Slider(50, 75, 150, 20, 20, 60, "Track Width: ", 12, false);
    perlinMapSpeed = new Slider(500, height - 50, 150, 25, 0, 10, "Map Speed: ", 15, false);
    perlinMapIntensity = new Slider(725, height - 50, 150, 25, 0.002f, 0.04f, "Map Difficulty: ", 15, false);

    laserOn = new TickBox(width - 100, height - 75, 25, "Laser");
    perlinMode = new TickBox((width / 2) - 50, height / 2, 25, "Perlin");
    trackMode = new TickBox((width / 2) + 50, height / 2, 25, "Track");
    drawChamp = new TickBox(width - 75, 25, 25, "Best Only");
    displayRays = new TickBox(width - 175, 25, 25, "Show Rays");
    
    laser = new Laser();
}

public void draw() {
    if (modeSelected == false) {
        trackWidth.display();
        perlinMode.Draw();
        trackMode.Draw();
    } else if (trackMode.activated) {
        if (trackDrawn && startSet) {
            noStroke();
            background(track);
            
            if (!drawChamp.activated){
                for (int i = 0; i < speedSlider.currentVal; i++) {
                    getCurrentBest();
                    if (laserOn.activated) {
                        laser.Draw();
                    }
    
                    for (Car car: cars) {
                        car.makeMove();
                    }
    
                    if (numDead >= numCars) {
                        topScores.add(currentBest.score);
                        topColours.add(currentBest.col);
                        createMutantCars();
                        numDead = 0;
                        laser.reset();
                        break;
                    }
                }
    
                for (Car car: cars) {
                    car.Draw();
                }
            } else {
                currentBest.makeMove();
                currentBest.Draw();
                
                if (currentBest.crashed){
                    currentBest.restart(perlinStartX, getSafePerlinY(perlinStartX));
                }
            }

            laserSpeed.display();
            speedSlider.display();
            carTopSpeed.display();

            laserOn.Draw();
            drawChamp.Draw();
            displayRays.Draw();

            drawGeneration();

            if (drawingNetwork) {
                currentBest.network.Draw();
            }

            if (drawingGraph) {
                drawScoreGenerationGraph();
                colorMode(RGB);
            }

        } else if (!trackDrawn && !startSet) {
            if (drawing && !trackWidth.sliderPressed) {
                for (float i = mouseX - trackWidth.currentVal / 2; i < mouseX + trackWidth.currentVal / 2; i++) {
                    for (float j = mouseY - trackWidth.currentVal / 2; j < mouseY + trackWidth.currentVal / 2; j++) {
                        set((int) i, (int) j, color(255, 150, 0));
                    }
                }
            }
        } else if (trackDrawn && !startSet && start != null) {
            start.Draw();
        }
    } else if (perlinMode.activated) {
        background(0, 255, 0);
        perlinRoad.Draw();
        
        speedSlider.display();
        carTopSpeed.display();
        perlinMapSpeed.display();
        perlinMapIntensity.display();
        
        drawChamp.Draw();
        displayRays.Draw();
        
        if (!drawChamp.activated){
            for (int i = 0; i < speedSlider.currentVal; i++) {
                
                getCurrentBest();
    
                for (Car car: cars) {
                    car.makeMove();
                }
    
                if (numDead >= numCars) {
                    topScores.add(currentBest.score);
                    topColours.add(currentBest.col);
                    createMutantCars();
                    numDead = 0;
                    break;
                }
            }
    
            for (Car car: cars) {
                car.Draw();
            }
        } else {
            currentBest.makeMove();
            currentBest.Draw();
            
            if (currentBest.crashed){
                currentBest.restart(perlinStartX, getSafePerlinY(perlinStartX));
            }
            
            if (currentBest.x > 3*width/4){
                perlinRoad.moveLeft(currentBest.x - (3*width/4));
                currentBest.moveLeft(currentBest.x - (3*width/4));
            }
        }

        drawGeneration();

        if (drawingNetwork) {
            currentBest.network.Draw();
        }

        if (drawingGraph) {
            drawScoreGenerationGraph();
            colorMode(RGB);
        }
        
        perlinRoad.speed = perlinMapSpeed.currentVal;
        perlinRoad.incAmount = perlinMapIntensity.currentVal;
    }
}

public void mousePressed() {
    if (!modeSelected) {
        perlinMode.checkForPress();
        trackMode.checkForPress();
        trackWidth.press();

        if (perlinMode.activated || trackMode.activated) {
            modeSelected = true;
            background(0, 255, 0);
        }
        
        if (perlinMode.activated){
            for (int i = 0; i < numCars; i++) {
                cars.add(new Car(perlinStartX, getSafePerlinY(perlinStartX), (int) trackWidth.currentVal / 2, (int) trackWidth.currentVal / 4));
            }
            
            perlinRoad = new Perlin_Road(250, 0.01f, trackWidth.currentVal, 2);
        }
    } else if (trackMode.activated) {
        if (!trackDrawn) {
            drawing = true;
        }

        if (trackDrawn && !startSet) {
            start = new Start(mouseX, mouseY);
            
            for (int i = 0; i < numCars; i++) {
                cars.add(new Car(start.x, start.y, (int) trackWidth.currentVal / 2, (int) trackWidth.currentVal / 4));
            }
            
            startSet = true;
            laserOn.activated = true;
        }
        
        laserOn.checkForPress();
        laserSpeed.press();
        speedSlider.press();
        carTopSpeed.press();
        drawChamp.checkForPress();
        displayRays.checkForPress();
        
    } else if (perlinMode.activated) {
        speedSlider.press();
        carTopSpeed.press();
        perlinMapSpeed.press();
        perlinMapIntensity.press();
        drawChamp.checkForPress();
        displayRays.checkForPress();
    }
}

public void mouseReleased() {
    drawing = false;

    laserSpeed.release();
    speedSlider.release();
    trackWidth.release();
    carTopSpeed.release();
    perlinMapSpeed.release();
    perlinMapIntensity.release();
}

public void keyPressed() {
    if (key == 'n') {
        drawingNetwork = !drawingNetwork;
    }

    if (key == 'g') {
        drawingGraph = !drawingGraph;
    }
    
    if (key == 'l'){
        if (!trackDrawn && !startSet){
            track = loadImage("track.png");
            trackDrawn = true;
            
            background(track);
            return;
        }
    }

    if (key == ' ') {
        if (trackMode.activated){
            if (!trackDrawn) {
                saveFrame("track.png");
                track = loadImage("track.png");
                trackDrawn = true;
            } else if (trackDrawn && !startSet) {
                for (int i = 0; i < numCars; i++) {
                    cars.add(new Car(start.x, start.y, (int) trackWidth.currentVal / 2, (int) trackWidth.currentVal / 4));
                }
                startSet = true;
            }
        }
    }
}

public void getCurrentBest() {
    float max = -1000;
    Car best = null;
    for (Car car: cars) {
        if (car.score > max) {
            best = car;
            max = car.score;
        }
    }

    currentBest = best;

    if (currentBest.score > currentMaxScore) {
        currentMaxScore = currentBest.score;
    }
}

public int getSafePerlinY(int x){
    float startX = -1, endX = -1;
    
    for (int i = 0; i < height; i++){
        if (get(x, i) == color(255, 150, 0) && startX == -1){
            startX = i;
        }
        
        if (get(x, i) == color(0, 255, 0) && startX != -1 && endX == -1){
            endX = i;
            break;
        }
    }
    
    return (int) (startX + endX)/2;
}
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
  
  public void restart(){
      this.x = definedX;
      this.y = definedY;
      
      crashed = false;
      angle = 0;
      score = 0;
      time = 0;
      
      topSpeed = carTopSpeed.currentVal;
  }
  
  public void restart(float x, float y){
      this.x = x;
      this.y = y;
      
      crashed = false;
      angle = 0;
      score = 0;
      time = 0;
      
      topSpeed = carTopSpeed.currentVal;
  }
  
  public void moveLeft(float amount){
      this.x-=amount;
  }
  
  public void randomize(){
      network = new Neural_Network(5, 10, 5);
      
      col = new Colour(color(random(0, 255), random(0, 255), random(0, 255)));
  }
  
  public void makeMove(){
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
  
  public void Draw(){
    
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
  
  public void accelerate(){
      if (vel == 0){
          vel = 0.1f;
      } else if (vel < topSpeed) {
          vel*=1.1f;
      } else {
          vel = topSpeed;
      }
  }
  
  public void decelerate(){
      if (vel > 0){
          vel/=1.1f;
      } else {
          vel = 0;
      }
  }
  
  public void turnRight(){
      angle+=0.2f;
  }
  
  public void turnLeft(){
      angle-=0.2f;
  }
}
class Colour{
    int colour;
    
    Colour(int colour){
        this.colour = colour;
    }
    
    public int getColour(){
        return colour;
    }
}
class Laser{
    float speed;
    float angle = PI;
    
    Laser(){
        this.speed = laserSpeed.currentVal;
    }
    
    public void Draw(){
        strokeWeight(25);
        stroke(255,0,0);
        pushMatrix();
        translate(width/2, height/2);
        rotate(angle);
        line(0, 0, width, 0);
        popMatrix();
        
        angle+=0.01f*speed;
        speed = laserSpeed.currentVal;
    }
    
    public void reset(){
        angle = PI;
        speed = laserSpeed.currentVal;
    }
}
class Neural_Network{
    ArrayList<Edge> edges_ih = new ArrayList();
    ArrayList<Edge> edges_ho = new ArrayList();
    ArrayList<Node> nodes_i = new ArrayList();
    ArrayList<Node> nodes_h = new ArrayList();
    ArrayList<Node> nodes_o = new ArrayList();
    
    Neural_Network(int input, int hidden, int output){
        // input nodes
        for (int i = 0; i < input; i++){
            nodes_i.add(new Node(new ArrayList(), new ArrayList(), width/7, i*(height/input) + height/(2*input), 0));
        }
        
        // hidden nodes
        float hiddenBias = random(-0.5f, 0.5f);
        for (int i = 0; i < hidden; i++){
            nodes_h.add(new Node(new ArrayList(), new ArrayList(), width/2, i*(height/hidden) + height/(2*hidden), hiddenBias));
        }
        
        // ouput nodes
        float outputBias = random(-0.5f, 0.5f);
        for (int i = 0; i < output; i++){
            nodes_o.add(new Node(new ArrayList(), new ArrayList(), 6*width/7, i*(height/output) + height/(2*output), outputBias));
        }
        
        // input to hidden edges
        for (Node node_i : nodes_i){
            for (Node node_h : nodes_h){
                Edge toAdd = new Edge(node_i, node_h);
                
                node_i.addExitEdge(toAdd);
                node_h.addEntryEdge(toAdd);
                
                edges_ih.add(toAdd);
            }
        }
        
        // hidden to output edges
        for (Node node_h : nodes_h){
            for (Node node_o : nodes_o){
                Edge toAdd = new Edge(node_h, node_o);
                
                node_h.addExitEdge(toAdd);
                node_o.addEntryEdge(toAdd);
                
                edges_ho.add(toAdd);
            }
        }
    }
    
    public void Draw(){
        ellipseMode(CENTER);
        colorMode(RGB);
        fill(255, 150);
        rect(0, 0, width, height);
        colorMode(HSB);
        textSize(13);
        for (Edge edge : edges_ih){
            edge.Draw();
        }
        
        for (Edge edge : edges_ho){
            edge.Draw();
        }
        
        for (Node node : nodes_i){
            node.Draw();
        }
        
        for (Node node : nodes_h){
            node.Draw();
        }
        
        Node pickedNode = null;
        float val = -1000;
        for (Node node : nodes_o){
            if (node.value > val){
                pickedNode = node;
                val = pickedNode.value;
            }
        }
        
        for (Node node : nodes_o){
            if (node == pickedNode){
                node.Draw(color(0,255,0));
            } else {
                node.Draw(color(255, 0, 0));
            }
        }
        noStroke();
    }
    
    public ArrayList<Float> guess(ArrayList<Float> inputs){
        ArrayList<Float> toReturn = new ArrayList();
        
        // clear previous node values
        for (Node node : nodes_h){
            node.value = 0;
        }
        
        for (Node node : nodes_o){
            node.value = 0;
        }
        
        // set input values
        for (int i = 0; i < inputs.size(); i++){
            Node toChangeVal = nodes_i.get(i);
            
            toChangeVal.setValue(inputs.get(i));
        }
        
        // calculate value * weight and add to node
        for (Edge edge : edges_ih){
            edge.end.value += edge.start.value * edge.weight;
        }
        
        // pass through activation function
        for (Node node : nodes_h){
            node.value += node.bias;
            node.value = activation(node.value);
        }
        
        // same with remaining edges
        for (Edge edge : edges_ho){
            edge.end.value += edge.start.value * edge.weight;
        }
        
        for (Node node : nodes_o){
            node.value += node.bias;
            node.value = activation(node.value);
        }
        
        // add guess values to return array
        for (Node node : nodes_o){
            toReturn.add(node.value);
        }
        
        return toReturn;
    }
    
    public Edge getEdge(Node start, Node end, int section){
        Edge toReturn = null;
        if (section == 0){
            for (Edge edge : edges_ih){
                if (edge.start == start && edge.end == end){
                    toReturn = edge;
                }
            }
        } else if (section == 1){
            for (Edge edge : edges_ho){
                if (edge.start == start && edge.end == end){
                    toReturn = edge;
                }
            }
        }
       return toReturn;
    }
}

class Edge{
    Node start, end;
    float weight;
    
    Edge(Node start, Node end){
        this.start = start;
        this.end = end;
        
        this.weight = random(-1, 1);
    }
    
    public void Draw(){
        colorMode(RGB);
        strokeWeight(map(activation(weight), 0, 1, 1, 5));
        stroke(lerpColor(color(200,0,0), color(0, 200, 0), activation(weight)));
        line(start.x, start.y, end.x, end.y);
        fill(0);
        strokeWeight(1);
        
        text(weight, (0.8f*start.x+1.2f*end.x)/2, (0.6f*start.y+1.4f*end.y)/2);
    }
}

class Node{
    ArrayList<Edge> enter = new ArrayList();
    ArrayList<Edge> exit = new ArrayList();
    
    int x, y;
    
    float value;
    
    float error;
    
    float bias;
    
    Node(ArrayList<Edge> behind, ArrayList<Edge> ahead, int x, int y, float bias){
        this.enter = behind;
        this.exit = ahead;
        
        this.x = x;
        this.y = y;
        
        this.value = 0;
        
        this.error = 0;
        
        this.bias = bias;
    }
    
    public void setError(float error){
        this.error = error;
    }
    
    public void addExitEdge(Edge output){
        exit.add(output);
    }
    
    public void addEntryEdge(Edge input){
        enter.add(input);
    }
    
    public void setValue(float value){
        this.value = value;
    }
    
    public void Draw(){
        stroke(0);
        strokeWeight(5);
        fill(lerpColor( color(255,0,0), color(0, 255, 0), value));
        ellipse(x, y, 125, 75);
        fill(0);
        
        text("Val: " + (float) (((int) (value*100000)) / 100000f), x, y-10);
        text("Bias: " + (float) (((int) (bias*100000)) / 100000f), x, y+10);
    }
    
    public void Draw(int colour){
        stroke(0);
        strokeWeight(5);
        fill(colour);
        ellipse(x, y, 125, 75);
        fill(0);
        
        text("Val: " + (float) (((int) (value*100000)) / 100000f), x, y-10);
        text("Bias: " + (float) (((int) (bias*100000)) / 100000f), x, y+10);
    }
}

public float activation(float input){
    return (float) (1/(1+Math.exp(-input)));
}
class Perlin_Point{
    float x, y;
    
    Perlin_Point(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    public void moveLeft(float amount){
        this.x -= amount;
    }
}

class Perlin_Road{
    ArrayList <Perlin_Point> points = new ArrayList();
    float currPos = random(0, 1);
    float speed;
    float lineWidth;
    float incAmount;
    
    Perlin_Road(int numPoints, float incAmount, float lineWidth, float defaultSpeed){
        this.lineWidth = lineWidth;
        this.incAmount = incAmount;
        this.speed = defaultSpeed;
        
        for (int i = 0; i < numPoints; i++){
            points.add(new Perlin_Point(width * i/numPoints , map(noise(currPos+=incAmount), 0, 1, 0, height)));
        }
    }
    
    public void Draw(){
        stroke(255, 150, 0);
        strokeWeight(lineWidth);
        moveLeft(speed);
        for (int i = 1; i < points.size(); i++){
            line(points.get(i).x, points.get(i).y, points.get(i-1).x, points.get(i-1).y);
        }
        
        if (points.get(0).x < 0){
            points.remove(points.get(0));
            
            points.add(new Perlin_Point(width, map(noise(currPos+=incAmount), 0, 1, 0, height)));
        }
    }
    
    public void moveLeft(float amount){
        for (Perlin_Point point : points){
            point.moveLeft(amount);
        }
    }
}
class RayCast{
    float startX, startY;
    float angle;
    
    float endX, endY;
    
    float lineLength;
    
    RayCast(int startX, int startY, float angle){
        this.startX = startX;
        this.startY = startY;
        this.angle = angle;
    }
    
    public void cast(float startX, float startY, float angle){
        this.startX = startX;
        this.startY = startY;
        this.angle = angle;
        this.endX = startX;
        this.endY = startY;
        
        lineLength = 0;
        
        while(get((int) this.endX, (int) this.endY) == color(255, 150, 0)){
            this.endX+=3*Math.cos(angle);
            this.endY+=3*Math.sin(angle);
            this.lineLength+=3;
        }
    }
    
    public void Draw(){
        strokeWeight(2);
        stroke(255, 255, 0);
        line(startX, startY, endX, endY);
    }
    
    public void Draw(int col){
        strokeWeight(2);
        stroke(col);
        line(startX, startY, endX, endY);
    }
}
class Slider{
    
    int startX, startY, sliderWidth, sliderHeight;
    float minVal, maxVal;
    
    int labelSize;
    
    float sliderX;
    float currentVal;
    
    String label;
    
    boolean sliderPressed = false;
    
    boolean floatOrInt = false;
    
   Slider(int startX, int startY, int sliderWidth, int sliderHeight, float minVal, float maxVal, String label, int labelSize, boolean floatOrInt){
       this.startX = startX;
       this.startY = startY;
       this.sliderWidth = sliderWidth;
       this.sliderHeight = sliderHeight;
       this.minVal = minVal;
       this.maxVal = maxVal;
       
       this.labelSize = labelSize;
       
       this.currentVal = minVal;
       
       this.label = label;
       
       this.floatOrInt = floatOrInt;
       
       sliderX = startX;
   }
   
   public void display(){
       noStroke();
       fill(255, 175);
       rect(startX - 25, startY - 50, sliderWidth + 50, 90, 25);
       
       strokeWeight(0);
       textAlign(CENTER);
       textSize(labelSize);
       fill(0);
       if (!floatOrInt){
           text(label + (float) ((int) (currentVal * 100)) / 100, startX, startY - 3*labelSize, sliderWidth, sliderWidth);
       } else {
           text(label + Math.round(currentVal), startX, startY - 3*labelSize, sliderWidth, sliderWidth);
       }
       
       if (sliderPressed){
           press();
       }
       
       stroke(0);
       fill(255);
       rect(startX - sliderHeight/2, startY, sliderWidth + sliderHeight, sliderHeight);
       
       fill(100);
       rect(sliderX - sliderHeight/2, startY, sliderHeight, sliderHeight);
   }
   
   public void press(){
       if (mouseX > startX && mouseX < startX + sliderWidth){
           if (mouseY > startY && mouseY < startY + sliderHeight || sliderPressed){
               sliderPressed = true;
           }
       }
       
       if (sliderPressed){
           if (mouseX <= startX + sliderWidth && mouseX >= startX){
               sliderX = mouseX;
               currentVal = map(mouseX, startX, startX + sliderWidth, minVal, maxVal);
               return;
           } else if (mouseX > startX + sliderWidth){
               sliderX = startX + sliderWidth;
               currentVal = maxVal;
               return;
           } else if (mouseX < startX){
               sliderX = startX;
               currentVal = minVal;
               return;
           }
       }
   }
   
   public void release(){
       sliderPressed = false;
   }
   
   public void update(){
       sliderPressed = true;
       sliderX = mouseX;
       currentVal = map(mouseX, sliderX, sliderX + sliderWidth, minVal, maxVal);
   }
    
}
class Start{
    int x, y;
    
    Start(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public void Draw(){
        fill(0, 0, 255);
        ellipseMode(CENTER);
        ellipse(x, y, 10, 10);
    }
}
class TickBox{
    boolean activated = false;
    int x, y;
    String label;
    int size;
    
    TickBox(int x, int y, int size, String label){
        this.x = x;
        this.y = y;
        this.label = label;
        this.size = size;
    }
    
    public void Draw(){
        strokeWeight(0);
        colorMode(RGB);
        
        fill(255, 175);
        rect(x - size, y - size/2, 3*size, 3*size, size/2);
        
        if (activated){
            fill(0, 255, 0);
        } else {
            fill(255, 0 ,0);
        }
        rect(x, y, size, size, size/4);
        fill(0);
        textSize(12);
        text(label, x + size/2, y+1.75f*size);
    }
    
    public void checkForPress(){
        if (mouseX > x && mouseX < x + size){
            if (mouseY > y && mouseY < y + size){
                activated = !activated;
            }
        }
    }
    
    public boolean getState(){
        return activated;
    }
}
float currentMaxScore = -10000;

public void getTopCars(){
    topCars.clear();
    while(topCars.size() < keepPercentage * (float) numCars){
        float topScore = -1000;
        Car top = null;
        for (Car car : cars){
            if (car.score > topScore && !topCars.contains(car)){
                topScore = car.score;
                top = car;
            }
        }
        topCars.add(top);
    }
}

// Displays the current generation
public void drawGeneration(){
    noStroke();
    fill(255, 175);
    rect(width/2 - 125, 10, 250, 45, 25);
    fill(0);
    textSize(24);
    text("Generation: " + (int) generation, width/2, 42);
    textSize(8);
}

public void createMutantCars(){
    getTopCars();
    
    generation++;
    
    int totalScore = 0;
    
    for (Car player : topCars){
        totalScore+=(player.score);
    }
    
    for (Car car : cars){
        
        if (topCars.contains(car)){
            continue;
        }
        
        if (random(0, 1) < 0.3f){
            car.randomize();
            continue;
        }
        
        ArrayList<Float> bestWeights_ih = new ArrayList();
        ArrayList<Float> bestWeights_ho = new ArrayList();
        
        Float bias_h;
        float bias_o;
        
        float random = random(totalScore);
        Car randPickedChamp = topCars.get(0);
        
        float numbersCovered = 0;
        for (Car topPlayer : topCars){
            numbersCovered += topPlayer.score;
            
            if (random <= numbersCovered){
                randPickedChamp = topPlayer;
                break;
            }
        }
        
        for (Edge edge : randPickedChamp.network.edges_ih){
            bestWeights_ih.add(edge.weight);
        }
        
        for (Edge edge : randPickedChamp.network.edges_ho){
            bestWeights_ho.add(edge.weight);
        }
        
        bias_h = randPickedChamp.network.nodes_h.get(0).bias;
        bias_o = randPickedChamp.network.nodes_o.get(0).bias;
        
        for (int i = 0; i < bestWeights_ih.size(); i++){
            car.network.edges_ih.get(i).weight = bestWeights_ih.get(i) + random(-5, 5);
        }
        
        for (int i = 0; i < bestWeights_ho.size(); i++){
            car.network.edges_ho.get(i).weight = bestWeights_ho.get(i) + random(-5, 5);
        }
        
        for (int i = 0; i < randPickedChamp.network.nodes_h.size(); i++){
            car.network.nodes_h.get(i).bias = bias_h;
        }
        
        for (int i = 0; i < randPickedChamp.network.nodes_o.size(); i++){
            car.network.nodes_o.get(i).bias = bias_o;
        }
        
        car.col = randPickedChamp.col;
    }

    int safePerlinY = (int) (width/2);
    if (perlinMode.activated){
        safePerlinY = getSafePerlinY(perlinStartX);
    }

    for (Car car : cars){
        car.restart();
        
        if (perlinMode.activated){
            car.x = perlinStartX;
            car.y = safePerlinY;
        }
    }
}

public void drawScoreGenerationGraph(){
    if (drawingGraph && cars.size() != 0 && generation != 0){       
        colorMode(RGB);
        fill(255, 150);
        rect(0, 0, width, height);
        colorMode(HSB);
        textSize(15);
        
        strokeWeight(5);
        stroke(50);
        line(100, 100, 100, height-100);
        line(100, height-100, width-100, height-100);
        fill(0);

        text("Generation", width/2, height - 20);
        text("Score", 30, height/2);
        textSize(12);
        
        for (int i = 0; i <= generation; i += 1 + topScores.size()/15){
            line(map(i, 0, generation, 100, width-100), height-100, map(i, 0, generation, 100, width-100), height-90);
            fill(0);
            text(i, map(i, 0, generation, 100, width-100), height-70);
        }
        
        for (int i = 0; i <= currentMaxScore; i += 1 + currentMaxScore/9){
            line(100, map(i, 0, currentMaxScore, 100, height-100), 90, map(i, 0, currentMaxScore, 100, height-100));
            fill(0);
            text((int) (currentMaxScore - i), 50, map(i, 0, currentMaxScore, 100, height-100) + 5);
        }
        
        strokeWeight(2);
        stroke(0);
        
            for (int i = 1; i < topScores.size(); i++){
                line(map(i, 0, generation, 100, width-100), map(topScores.get(i), 0, currentMaxScore, height - 103, 100), map(i-1, 0, generation, 100, width-100), map(topScores.get(i-1), 0, currentMaxScore, height - 103, 100));
            }
            
            if (currentBest != null){
                line(width-100, map(currentBest.score, 0, currentMaxScore, height - 103, 100), map(topScores.size()-1, 0, generation, 100, width-100), map(topScores.get(topScores.size()-1), 0, currentMaxScore, height - 103, 100));
                
                if (currentBest.score > currentMaxScore){
                    currentMaxScore = currentBest.score;
                }
            }
        
        
            for (int i = 0; i < topColours.size(); i++){
                fill(topColours.get(i).colour);
                circle(map(i, 0, generation, 100, width-100), map(topScores.get(i), 0, currentMaxScore, height - 103, 100), 10);
            }
            
            if (currentBest != null){
                fill(currentBest.col.colour);
                circle(width-100, map(currentBest.score, 0, currentMaxScore, height - 103, 100), 10);
                
                if (currentBest.score > currentMaxScore){
                    currentMaxScore = currentBest.score;
                }
            }
        
        noStroke();
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--hide-stop", "Car_NN" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
