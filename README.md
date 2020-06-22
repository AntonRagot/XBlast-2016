# XBlast-2016

XBlast is a Bomberman-like game where four players, each controlling a separate character, to compete on a common game board. In an attempt to eliminate his opponents, a player has the option of dropping bombs. These explode after a certain time, causing, among other things, the death of any unfortunate people affected by the explosion. If a player succeeds in eliminating all of his opponents before the end of the two regulation minutes, he is declared victorious. Otherwise, the game ends in a draw.

## Installing

The implementation of XBlast is separated into two distinct programs: the server and the client. In a four-player game, one copy of the server and four copies of the client — one per player — work simultaneously, on four — or possibly five — different computers. Each client communicates with the server via the network.

### Prerequisite (Setting up the environment)

Here, we use Eclipse as IDE, if you have another one you might need to research on to perfom the following steps:

 - Installing ``` Sq.jar ``` : Select your project in the Package Explorer. Select the Properties entry from the Project menu. In the dialog that opens, select the Java Build Path entry. Activate the Libraries tab and click on Add External JARs…. In the dialog box that opens, choose the JAR file located in the ``` External JAR ``` folder, then click Ok.
 - Add images folder as source folder : In the package explorer, select the ```images``` directory then right click to open the context menu, in which you will choose the Build Path submenu then the entry "Use as Source Folder". At this time, the icon associated with the data directory will become identical to that associated with the src directory, indicating that both are source directories.

### Running the server

Main for the server is located in the package ``` ch.epfl.xblast.server ```. Note that the server uses the port 2016 to receive and send packets to the clients. If the port is already in use by another application, either kill the application that uses this port or change the global constant ``` PORT ``` located at the top of the ``` main.java ``` file (/!\ The clients uses the port 2016 by default, make sure to warn them when you change it!).

**To run it** using the IDE, right click on ```main.java``` > Run as ... > Java Application

_Argument_ : The server have an optional argument: the number of players. If no argument is given, the will automatically wait for 4 clients before lauching the game. 


### Running the client

Main for the client is located in the package ``` ch.epfl.xblast.client ```. Note that the client connects to the port 2016 of the server. If the person running the server is using another port, change the global constant ``` PORT ``` located at the top of the ``` main.java ``` file.

**To run it** using the IDE, right click on ```main.java``` > Run as ... > Java Application

_Argument_ : The client have an optional argument: the server's IP. If no argument is given, the client will automaticaly connect to ```localhost```. 


Now that you know how to run the server and the client, enjoy your game 🎮

## Screenshot

![Screenshot](images/screenshot/ex1.png?raw=true)

## Authors

 - [Andra Bisca](https://github.com/Amyst25)
 - [Anton Ragot](https://github.com/AntonRagot)

