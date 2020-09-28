#bin/bash!

mvn package shade:shade

FILE=$(find ./target/server-*SNAPSHOT.jar)
echo "STARTING $FILE"

java -jar $FILE