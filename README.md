# Car_Genetic_Algorithm
This Processing sketch allows you to draw your own track, and uses a genetic algorithm to create a car that has learned how to navigate the track. Uses a simple Neural Network as the cars brain.

The sketch is not completed but the fundamentals are there.

How to use:
- Select the width of the track you wish to draw (in pixels)
- Draw the track (slowly) as I need to implement some sort of linked lines implementation to allow drawing as quickly as the heart desires.
- Once the track is drawn (ideally a circular track) then click where you want the cars to begin from (top of the screen is best)
- Once clicked, press space to begin the cars
- The top speed and laser speed of the cars need to be added, the AI needs to be improved so that the cars accelerate and decellerate, this would probably mean getting rid of the laser and adding checkpoints.
- Use the slider to control the number of iterations per second (its pretty quick)
- To see the progress graph press 'g', to see the neural network in the brain of the currently best car, press 'n'
- At the moment the AI isnt saved so its pretty pointless, but its cool to see the car learn 
