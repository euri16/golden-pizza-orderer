# Golden Pizza Orderer - Android Project

The Golden Pizza Orderer is an Android application that simplifies your pizza ordering process. Users can choose up to two pizza flavors and the price will be automatically calculated as the sum of half the price of each selected flavor.

<div>
    <img src="https://github.com/euri16/golden-pizza-orderer/blob/main/golden_pizza.png" alt="Screenshot 1" width="31%" />
    <img src="https://github.com/euri16/golden-pizza-orderer/blob/main/golden_pizza2.png" alt="Screenshot 1" width="31%" />
    <img src="https://github.com/euri16/golden-pizza-orderer/blob/main/golden_pizza3.png" alt="Screenshot 1" width="31%" />
</div>

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Testing](#testing)
- [Getting Started](#getting-started)

## Features

- Intuitive ordering process.
- Choice of up to two pizza flavors.
- Automatic calculation of the final pizza price.

## Technologies

The Pizza Orderer employs several modern Android technologies and libraries:

- **Kotlin**: The project is fully written in Kotlin.
- **MVVM and MVI**: Both Model-View-ViewModel (MVVM) and Model-View-Intent (MVI) architectural patterns are used.
- **Jetpack Compose**: Used for UI design and interactions.
- **Hilt**: Provides dependency injection.
- **Kotlin Coroutines and Flow**: Manages asynchronous tasks and real-time data.
- **Retrofit**: Handles network operations.
- **Glide**: Loads and displays images efficiently.
- **Version Catalog**: Manages dependencies in a centralized manner.

## Architecture

The application follows clean architecture principles and is modularized into three main layers:

- **Presentation Layer**: Responsible for displaying data to the user and interpreting user interactions.
- **Domain Layer**: Contains all use cases of the application, representing the business logic.
- **Data Layer**: Manages the application's data, hiding the complexity of data retrieval and storage from the rest of the application.

## Testing

The Pizza Orderer is thoroughly tested using the following libraries:

- **Mockk**: Provides mocking for Kotlin.
- **Turbine**: Facilitates testing of Kotlin Coroutines and Flow.

## Getting Started

To clone and run this application, you'll need [Git](https://git-scm.com) and [Android Studio](https://developer.android.com/studio) installed on your computer.

From your command line:

```bash
# Clone this repository
$ git clone https://github.com/yourusername/pizza-orderer.git

# Go into the repository
$ cd pizza-orderer
```

# Open in Android Studio
# You can open the project directory in Android Studio to begin development.

## Building and Running the App

After you have cloned the repository and opened it in Android Studio, you can build and run the application.

From Android Studio:

1. Click on `Build -> Make Project` from the top menu to build the project.
2. To run the application on an Android device connected to your machine, click on `Run -> Run 'app'`.
3. To run the application on an emulator, you need to first set up an Android Virtual Device (AVD) by clicking on `Tools -> AVD Manager -> + Create Virtual Device`. Follow the wizard to create an AVD, and then run the application as described in step 2.

## Testing the App

Unit tests are located in the `src/test` directory under each module. To run the tests:

1. Click on `Run -> Run 'All Tests'` from the top menu.

## Contact

Your Name - [@_euryperez](https://twitter.com/_euryperez) - YourEmail

Project Link: [https://github.com/euri16/github-repository-explorer/](https://github.com/euri16/github-repository-explorer/)