# Genetasis
A Game/Emulation based upon the concepts of a genetic algorithm built in Java and openGL with LWJGL

Setup:

Clone from the GitHub into preferably IntelliJ IDE
Creat new configutation > java application > main class = main.Main > build and run
Make sure contents root is set to genetasis and resources root is set to resources folder

When running the simulation several graphs will open stacked ontop of each other, drag them elsewhere, preferably to a second monitor to see them all.
Graphs can be resized and the potting area can be dragged to adjust ranges.

Graphs will take 60 seconds after running the program to display data.


Controls:

WASD to pan camera

Z and X to lower/raise camera height

MOUSE:

right click and drag to pan
left click to highlight entities


Settings:

Enter Main class and set polygon mode to true to see raw meshes
Set unlock framerate to true to attempt to run the simulation at a higher framerate

The window has three buttons that control three different gamespeeds, these however will not update the charts any faster.

This Application was designed for a 1920 x 1080 monitor and has not been tested yet for other screen resolutions.

Attempting to convert the project into a .jar file currently results in errors 
JRE: 16
classpath of module : Glevolution
