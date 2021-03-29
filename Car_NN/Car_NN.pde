ArrayList < Car > cars;
int numDead = 0;
int numCars = 50;
int generation = 0;
float keepPercentage = 0.25;

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

void settings() {
    fullScreen();
}

Start start;

void setup() {
    textAlign(CENTER, CENTER);
    cars = new ArrayList();
    background(0, 255, 0);

    laserSpeed = new Slider(500, height - 50, 150, 25, 0, 5, "Laser Speed: ", 15, false);
    speedSlider = new Slider(50, height - 50, 150, 25, 1, 1000, "Iterations/frame: ", 15, false);
    carTopSpeed = new Slider(275, height - 50, 150, 25, 1, 20, "Car Top Speed: ", 15, false);
    trackWidth = new Slider(50, 75, 150, 20, 20, 60, "Track Width: ", 12, false);
    perlinMapSpeed = new Slider(500, height - 50, 150, 25, 0, 10, "Map Speed: ", 15, false);
    perlinMapIntensity = new Slider(725, height - 50, 150, 25, 0.002, 0.04, "Map Difficulty: ", 15, false);

    laserOn = new TickBox(width - 100, height - 75, 25, "Laser");
    perlinMode = new TickBox((width / 2) - 50, height / 2, 25, "Perlin");
    trackMode = new TickBox((width / 2) + 50, height / 2, 25, "Track");
    drawChamp = new TickBox(width - 75, 25, 25, "Best Only");
    displayRays = new TickBox(width - 175, 25, 25, "Show Rays");
    
    laser = new Laser();
}

void draw() {
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

void mousePressed() {
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
            
            perlinRoad = new Perlin_Road(250, 0.01, trackWidth.currentVal, 2);
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

void mouseReleased() {
    drawing = false;

    laserSpeed.release();
    speedSlider.release();
    trackWidth.release();
    carTopSpeed.release();
    perlinMapSpeed.release();
    perlinMapIntensity.release();
}

void keyPressed() {
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

void getCurrentBest() {
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

int getSafePerlinY(int x){
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
