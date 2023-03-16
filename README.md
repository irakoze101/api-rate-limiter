# How to run
## Requirements
1. Install [Docker](https://docs.docker.com/install/)
2. Install Java 17 (or higher)
3. Install gradle (or use gradlew)
4. Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (or any other IDE)

## Run
1. Run `docker-compose up -d` to start the database
2. Run `gradle bootRun` to start the application
3. Open `http://localhost:8080` in your browser
4. Run `docker-compose down` to stop the database
5. Run `gradle clean` to clean the project
6. Run `gradle build` to build the project
7. Run `gradle bootJar` to build the project as a jar

## Run tests
1. Run `docker-compose up -d` to start the database
2. Run `gradle test` to run the tests
