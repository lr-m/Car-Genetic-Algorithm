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
        float hiddenBias = random(-0.5, 0.5);
        for (int i = 0; i < hidden; i++){
            nodes_h.add(new Node(new ArrayList(), new ArrayList(), width/2, i*(height/hidden) + height/(2*hidden), hiddenBias));
        }
        
        // ouput nodes
        float outputBias = random(-0.5, 0.5);
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
    
    void Draw(){
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
    
    ArrayList<Float> guess(ArrayList<Float> inputs){
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
    
    Edge getEdge(Node start, Node end, int section){
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
    
    void Draw(){
        colorMode(RGB);
        strokeWeight(map(activation(weight), 0, 1, 1, 5));
        stroke(lerpColor(color(200,0,0), color(0, 200, 0), activation(weight)));
        line(start.x, start.y, end.x, end.y);
        fill(0);
        strokeWeight(1);
        
        text(weight, (0.8*start.x+1.2*end.x)/2, (0.6*start.y+1.4*end.y)/2);
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
    
    void setError(float error){
        this.error = error;
    }
    
    void addExitEdge(Edge output){
        exit.add(output);
    }
    
    void addEntryEdge(Edge input){
        enter.add(input);
    }
    
    void setValue(float value){
        this.value = value;
    }
    
    void Draw(){
        stroke(0);
        strokeWeight(5);
        fill(lerpColor( color(255,0,0), color(0, 255, 0), value));
        ellipse(x, y, 125, 75);
        fill(0);
        
        text("Val: " + (float) (((int) (value*100000)) / 100000f), x, y-10);
        text("Bias: " + (float) (((int) (bias*100000)) / 100000f), x, y+10);
    }
    
    void Draw(color colour){
        stroke(0);
        strokeWeight(5);
        fill(colour);
        ellipse(x, y, 125, 75);
        fill(0);
        
        text("Val: " + (float) (((int) (value*100000)) / 100000f), x, y-10);
        text("Bias: " + (float) (((int) (bias*100000)) / 100000f), x, y+10);
    }
}

float activation(float input){
    return (float) (1/(1+Math.exp(-input)));
}
