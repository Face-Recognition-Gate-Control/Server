#bin/bash!
FILE=$(find ./target/server-*SNAPSHOT.jar)

echo "STARTING $FILE"

mvn package shade:shade && java -jar $FILE