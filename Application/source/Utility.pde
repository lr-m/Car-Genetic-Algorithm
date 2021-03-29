float currentMaxScore = -10000;

void getTopCars(){
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
void drawGeneration(){
    noStroke();
    fill(255, 175);
    rect(width/2 - 125, 10, 250, 45, 25);
    fill(0);
    textSize(24);
    text("Generation: " + (int) generation, width/2, 42);
    textSize(8);
}

void createMutantCars(){
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
        
        if (random(0, 1) < 0.3){
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

void drawScoreGenerationGraph(){
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
