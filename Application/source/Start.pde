class Start{
    int x, y;
    
    Start(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    void Draw(){
        fill(0, 0, 255);
        ellipseMode(CENTER);
        ellipse(x, y, 10, 10);
    }
}
