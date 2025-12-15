# Galaxy Trucker - IngSw 2025 (Group AM49)

![Java](https://img.shields.io/badge/Java-23-orange)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-23-green)
![Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen)

Implementation of the board game **Galaxy Trucker** (Cranio Creations) for the "Software Engineering" course at Politecnico di Milano (Academic Year 2024-2025).

## 🚀 Project Overview

This project is a distributed Java application that faithfully reproduces the rules and mechanics of *Galaxy Trucker*. Players build spaceships from sewer pipes, face cosmic hazards (meteors, pirates, smugglers), and race to deliver goods.

The system is built using a **Distributed MVC (Model-View-Controller)** architecture, ensuring a clear separation of concerns and scalability.

### Key Features

*   **Multiplayer Support:** Up to 4 players in a single lobby.
*   **Network Architecture:**
    *   **Socket:** Custom TCP protocol using object serialization.
    *   **RMI:** Remote Method Invocation for seamless object interaction.
    *   *Both protocols are fully interchangeable and supported.*
*   **User Interfaces:**
    *   **GUI (Graphical User Interface):** Built with **JavaFX**, offering a rich, interactive experience with drag-and-drop mechanics for ship building.
    *   **TUI (Textual User Interface):** Built with **JLine**, providing a fully functional command-line interface with ANSI colors and keyboard navigation.
*   **Advanced Features:**
    *   Resilience to disconnections (Reconnection support).

## 📂 Project Structure

*   `src/main/java`: Source code for Client, Server, and Shared logic.
*   `src/test/java`: Unit and integration tests (JUnit 5).
*   `Deliverables/`:
    *   `Jar/`: Pre-compiled executable JARs.
    *   `Javadoc/`: Complete code documentation.
    *   `UML/`: High-level and detailed architecture diagrams.

## 🛠️ Prerequisites

Ensure you have the following installed:

*   **Java JDK 23** (or higher)
*   **Maven 3.6+**

## 📦 Installation & Building

To build the project from source, clone the repository and run the following command in the root directory:

```bash
mvn clean package
```

This will generate the executable JAR file with dependencies in the `target/` directory:
`IS25-AM49-1.0-SNAPSHOT-jar-with-dependencies.jar`

## 🎮 How to Run

The project includes a unified launcher (`GTLauncher`) that allows you to start either the Server or the Client (CLI/GUI).

### 1. Start the Server
First, launch the server on a machine:

```bash
java -jar target/IS25-AM49-1.0-SNAPSHOT-jar-with-dependencies.jar
```
*Select option `1` (SERVER) when prompted.*

### 2. Start the Client
Launch the client on the same or a different machine:

```bash
java -jar target/IS25-AM49-1.0-SNAPSHOT-jar-with-dependencies.jar
```
*Select option `2` (CLIENT) when prompted.*

Follow the on-screen instructions to:
1.  Enter the Server IP (use `localhost` or `127.0.0.1` for local testing).
2.  Choose your Interface:
    *   **1. TUI:** For terminal-based gameplay.
    *   **2. GUI:** For the graphical experience.

## 👥 Group Members (AM49)

*   **Davis Crippa**
*   **Michele De Matteo**
*   **Andrea Compagnoni**
*   **Confalonieri Daniele**

## 📄 License

This project is developed for educational purposes at Politecnico di Milano.
Original game *Galaxy Trucker* is a property of **Cranio Creations**.

---
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
From the root of the repository, execute on your system terminal: mvn clean package. After building, navigate to the target/ directory: cd target
Alternatively you can run it without building with maven, insert in your terminal cd Deliverables, after that insert cd Jar.
To start the launcher insert on your terminal when you are in the correct folder java -jar IS25-AM49.jar
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

