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
    
    void Draw(){
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
        text(label, x + size/2, y+1.75*size);
    }
    
    void checkForPress(){
        if (mouseX > x && mouseX < x + size){
            if (mouseY > y && mouseY < y + size){
                activated = !activated;
            }
        }
    }
    
    boolean getState(){
        return activated;
    }
}
