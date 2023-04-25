# Diving duck

This project is a flappy-bird like game, but with a duck instead of a generic bird. The game is written in Kotlin and uses the LibGDX framework with the Ashley ECS library.

## How to run

Run the command `gradlew desktop:run` in the root directory of the project. This will start the game in a desktop window. Make sure the gradlew file is executable.

## Structure

The project is structured in the following way:

- `core`: Contains the core game logic, such as the game state, the game screen, the game components, the game entities, and the game systems.
- `desktop`: Contains the desktop launcher for the game.
- `android`: Contains the android launcher for the game.
- `ios`: Contains the ios launcher for the game.

Each of these directories should be located in the root directory of your Gradle project. The `core` directory should contain the core logic of the game, while the `desktop`, `android`, and `ios` directories should each contain the launcher code for the respective platform.