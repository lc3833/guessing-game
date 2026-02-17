# GUESS THE ASCII - ULTIMATE GUESSING GAME

* **University of Belgrade** - **Faculty of Organizational Sciences**
* **Subject:** Tools and Methods of Artificial Intelligence and Software Engineering
* **Author:** Lazar Cvetković 2025/3833
* **Professor:** dr. Dragan Đurić
* **Year:** 2025/2026

## 1. Project Overview

This repository contains the implementation of the interactive console-based application "Guessing Game",
developed as part of a seminar project.

The primary objective of this project is to demonstrate the practical application of the functional
programming paradigm through the design and implementation of a modular, deterministic, and maintainable
software system using the programming language Clojure on the Java Virtual Machine (JVM).

The application is designed as a functional interactive game that integrates algorithmic logic, state
management, persistence mechanisms, and Java interoperability within a functional architecture.

## 2. Concept and Game Mechanics

The Guessing Game is a console-based interactive application in which the player is presented with
ASCII-based visual representations of hidden concepts. The player’s objective is to correctly identify
the concept depicted by the ASCII drawing.

Each round of the game follows this structure:

* 2.1. The system selects a concept from a predefined database.
* 2.2. An ASCII representation of the concept is rendered in the console.
* 2.3. The player submits a textual guess.
* 2.4. The system evaluates the answer using a similarity algorithm.
* 2.5. Points are awarded or penalties applied depending on correctness and response time.

The game incorporates the following mechanics:

* A limited number of lives.
* Time-sensitive scoring.
* A streak-based multiplier system.
* Persistent high scores.
* A deterministic seed-based challenge mode.
* A structured achievements system.
* Audio feedback implemented via Java interoperability.

## 3. Technical Stack

The application is implemented using:

* Clojure as the primary programming language.
* Java Virtual Machine (JVM) as the execution environment.
* EDN (Extensible Data Notation) for data persistence.
* clojure.test for automated testing.
* Java Standard Library for audio playback and deterministic randomization.

## 4. Code Structure and Logic

The project follows the rules of functional programming. The code is organized into separate modules
to keep the logic clean and easy to maintain.

### Core Concepts
Instead of changing variables directly, the game uses **immutable data** and **pure functions** where it is possible.
To handle the changing parts of the game (like score, lives, and timer), I used **Clojure Atoms** (`swap!`, `reset!`)
which allow safe state changes. The game flow relies on **recursion** (`loop/recur`) instead of standard imperative loops.

### Project Files Description
The system is divided into specific files, where each one has a single job:

* **core.clj** – The main file that starts the application and the menu loop.
* **game_logic.clj** – Contains the rules for checking answers and shuffling puzzles.
* **game_flow.clj** – Manages how a single round is played (gameplay sequence).
* **scoring_engine.clj** – Calculates points based on speed and streaks.
* **db.clj** – Handles reading and writing to the `.edn` files (database).
* **ui.clj** – Draws the ASCII art and text to the console.
* **state.clj** – Keeps track of the global game state (lives, score, timer).
* **sound.clj** – Plays sound effects using Java libraries.
* **achievements.clj** – Checks if the player unlocked any new badges.


### In-game Commands
While playing, you can type these special commands:

* **/hint** – Reveals 2 random letters (costs one hint).
* **/sound [on/off/number]** – Controls audio (e.g., `/sound 50`, `/sound off`).
* **/help** – Shows the rules and commands list.
* **exit** – Quits the current game immediately.

## 5. AI Assistance in Project

This section transparently documents the use of AI tools during the development of this project.

AI tools were used strictly as supportive programming assistants for syntax clarification, refactoring suggestions,
and repetitive code generation. 

All architectural decisions, algorithm design, scoring logic, deterministic seed
implementation, state management strategy, and overall system structure were independently designed and implemented by the author.

### 5.1 Syntax Clarification and Refactoring Support

* **game_logic.clj**
  * AI was used to review and suggest minor refactoring improvements to an already implemented Levenshtein Distance function.
The algorithm design, optimization decisions, and answer classification logic were developed independently.

* **sound.clj**
  * AI was used to clarify Java interoperability syntax when working with the `javax.sound.sampled` package.
The integration approach and asynchronous execution logic were implemented by the author.

### 5.2 Formatting and Repetitive Code Assistance

* **db.clj**
  * AI assisted with structuring repetitive string formatting patterns and minor `StringBuilder` usage improvements.
The data model, EDN persistence logic, sorting mechanisms, and atomic updates were implemented by the author.

* **game_flow.clj**
  * AI was occasionally used to review input-cleaning patterns and suggest syntactic improvements. The control flow
design, command handling structure, and gameplay logic were independently developed.

### 5.3 Automated Testing Support

* **achievements_test.clj & db_test.clj**

  * AI was used to generate initial boilerplate structures for unit tests. All test cases, edge-case definitions,
assertions, and validation logic were written, adapted, and verified manually.

AI was additionally used for minor visual refinements of the console output and for selecting appropriate emojis used in the interface.

AI was not used to generate the overall project architecture, scoring model, deterministic seed system,
state management design, achievement system logic, or the conceptual structure of the game.

**AI Tool Used:** Google Gemini

**IMPORTANT:** This project was developed independently. No external student projects or third-party implementations were consulted during development.
The concept, architecture, and implementation are the result of the author’s own design and work.

## 6. Application Interface and Gameplay Preview

### Main Menu
The central hub where players enter their nickname and navigate through game options.

<img width="465" height="473" alt="main_menu" src="https://github.com/user-attachments/assets/2e8c5621-6bad-446d-99dd-17a83abad253" />

### Game Setup
This screenshot shows how to start a new game by picking your seed, difficulty level, and category.

<img width="707" height="662" alt="game_setup" src="https://github.com/user-attachments/assets/d0c943d9-a7e5-44ff-8676-5ea6cec0ccad" />

### Help and Rules Preview
This screenshot shows the game rules, all available commands, and the guide for unlocking every achievement.

<img width="710" height="880" alt="help_screen" src="https://github.com/user-attachments/assets/ecaf9e3e-2ec6-466a-9cf9-f5b0599f224f" />

### Sound Management
Control audio playback by adjusting the volume level or enabling and disabling sound effects through dedicated console commands.

<img width="403" height="358" alt="sound_settings" src="https://github.com/user-attachments/assets/1453ab6a-ec3a-4b1a-b4e2-3802b0ebaf7e" />

### Gameplay – Correct Answer Example
This screenshot illustrates a successful guess ("bear"), where the system awards points and updates the current streak within the active round.

<img width="541" height="417" alt="gameplay_success" src="https://github.com/user-attachments/assets/f9f7d402-0de6-45bd-be73-bcc1109bcda5" />

### Game Over and Leaderboard
The game over screen displays your final score and the leaderboard after you lose all lives.

<img width="546" height="795" alt="game_over" src="https://github.com/user-attachments/assets/33bf4122-d138-4d94-be12-fa348f08eba6" />

## 7. Project Structure

<img width="509" height="936" alt="project_structure" src="https://github.com/user-attachments/assets/be7d3e5f-6605-4e34-becd-032f144511e0" />

## 8. How to Run

Before starting the game, make sure you have the following installed:
* **Java (JVM):** The application runs on the Java Virtual Machine, so you need at least Java 8 or newer installed on your system.
* **Windows Terminal:** For the best visual experience with emojis and colors, it is recommended to run the game in Windows Terminal or a similar modern console.

### Launching the Game
* **For Windows:** Double-click the **PLAY.bat** file found in the project root folder. It will launch the game on Windows Terminal.

## Additional Information

Any further details not included in this README can be found in the author's seminar paper.

© 2025-2026 Lazar Cvetković. All rights reserved.














