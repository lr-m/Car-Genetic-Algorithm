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
   
   void display(){
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
   
   void press(){
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
   
   void release(){
       sliderPressed = false;
   }
   
   void update(){
       sliderPressed = true;
       sliderX = mouseX;
       currentVal = map(mouseX, sliderX, sliderX + sliderWidth, minVal, maxVal);
   }
    
}
