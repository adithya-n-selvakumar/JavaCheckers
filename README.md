This project is a Java implementation of the classic board game Checkers, developed as a final project for a Java programming course. It features a graphical user interface using Swing, game state management, and unit tests.

## Features
Complete implementation of Checkers game rules
Graphical user interface using Java Swing
Game state persistence (save and load functionality)
Undo move capability
Unit tests for game logic

## Structure
The project is organized into several key classes:

Game: Main entry point for the application
RunCheckers: Sets up the game window
CheckersMainPanel: Primary GUI component, handling user interactions and game rendering
CheckersBoard: Manages game state, including piece positions and move validation
CheckersMove: Represents individual moves
FileDataService: Handles file I/O for saving and loading game states

## How to Run

Ensure you have Java installed on your system
Clone this repository
Compile the Java files
Run the Game class to start the application

## Testing
Unit tests are provided in the CheckersBoardTest and CheckersMoveTest classes. These tests cover core game logic such as board setup, move legality, and game state management.

## Acknowledgments
This project was developed as part of CIS 1200, a Java programming course at the University of Pennsylvania.
Special thanks to Professor Swapneel Sheth and TAs for guidance and support.
