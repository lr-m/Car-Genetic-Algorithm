class Laser{
    float speed;
    float angle = PI;
    
    Laser(){
        this.speed = laserSpeed.currentVal;
    }
    
    void Draw(){
        strokeWeight(25);
        stroke(255,0,0);
        pushMatrix();
        translate(width/2, height/2);
        rotate(angle);
        line(0, 0, width, 0);
        popMatrix();
        
        angle+=0.01*speed;
        speed = laserSpeed.currentVal;
    }
    
    void reset(){
        angle = PI;
        speed = laserSpeed.currentVal;
    }
}
