# sar-pdf-parser
Student Aid Report PDF parser

## Prerequisites
You needs Java 8 and Maven. You should use Intellij for development but I can't stop you from using other tools if you want.

A working knowledge of Kotlin is recommended. It's basically Java with better syntax.

## Getting Started
- Clone the repo
- Ensure `mvn test` is successful
- `mvn package`
- Run the jar with `java -jar target/sar-pdf-parser-1.0-SNAPSHOT.jar`
	- Add any CLI arguments you have to the end of the above command
- (non)profit

## Development
Again, you should probably use Intellij because it's good and Jetbrains made Kotlin.
Create an "Application" run configuration and choose `Main` as the main class.
Add any CLI arguments you want to the run configuration. Running and debugging the app and the tests should work pretty much out of the box.

