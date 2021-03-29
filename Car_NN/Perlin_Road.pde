class Perlin_Point{
    float x, y;
    
    Perlin_Point(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    void moveLeft(float amount){
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
    
    void Draw(){
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
    
    void moveLeft(float amount){
        for (Perlin_Point point : points){
            point.moveLeft(amount);
        }
    }
}
