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
    
    void cast(float startX, float startY, float angle){
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
    
    void Draw(){
        strokeWeight(2);
        stroke(255, 255, 0);
        line(startX, startY, endX, endY);
    }
    
    void Draw(color col){
        strokeWeight(2);
        stroke(col);
        line(startX, startY, endX, endY);
    }
}
