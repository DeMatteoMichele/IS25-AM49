This repository contains a Java-based multiplayer implementation of the board game *Galaxy Trucker* (Cranio Creations), developed at Politecnico di Milano for the Final Exam of Software Engineering.
Galaxy Trucker is set in a space-themed universe, where players build shipboards and face cosmic hazards during intergalactic travel.
The project allows up to four players to play faithfully reproducing all the game rules such as card-based encounters. 

It employs a distributed Model-View-Controller (MVC) design; 

The client encompasses a significant portion of the game logic, enabling it to operate autonomously and manage key functionalities independently. Despite this, the client maintains continuous and real-time synchronization with the server to ensure data consistency, state coherence, and timely propagation of updates across the system. This approach balances local processing and server authority, improving responsiveness while preserving the integrity of the overall game state.

The system uses two communication mechanisms—plain TCP sockets and Java RMI—for all network interactions. All messages exchanged implement the java.io.Serializable interface, allowing objects to be marshalled and unmarshalled over both transports. On the sending side, an invoker component instantiates the appropriate Command object and dispatches it via socket or RMI; on the receiving side, a handler retrieves the serialized Command instance and invokes its callMethod() method on the local game state. This mechanism ensures that every operation is encapsulated in a self-describing object, which is sent as it is and then executed by the remote endpoint.

The system offers the choice for two types of user interfaces: a TextualUserInterface (TUI) that runs in a terminal environment and a GraphicalUserInterface (GUI) implemented with JavaFX.

The testing was performed using Junit5.

**Prerequisites**
Java JDK 23 must be installed on your pc and available in the system PATH.
Apache Maven (version 3.6 or later) must be installed.

***How to Build and Run the Executable JAR***
This project uses Apache Maven to compile the source code and generate an executable .jar file in the target/ directory of this project.
From the root of the repository, execute on your system terminal: mvn clean install
After building, navigate to the target/ directory: cd target
To start the launcher insert on your terminal java -jar IS25-AM49.jar
The launcher will prompt you to select whether to start the server or one of the client modes (CLI or GUI) and then you are ready to play!


The project was developed by students of the Software Engineering course at Politecnico di Milano; 
Team members: Michele De Matteo, Andrea Compagnoni, Daniele Confalonieri, Davis Crippa.


| Feature                              | Status      |

| ✅ Basic rules                       : Implemented |
| ✅ Complete rules                    : Implemented |
| ✅ CLI interface                     : Implemented |
| ✅ GUI interface (JavaFX)            : Implemented |
| ✅ Socket-based networking           : Implemented |
| ✅ RMI-based networking              : Implemented |
| ✅ Disconnection resilience          : Implemented |
