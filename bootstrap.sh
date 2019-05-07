mvn install
mkdir -p out/
cp -r Database/target/classes out/
cp -r HillClimbing/target/classes out/
cp -r Instrumentation/target/classes out/
cp -r Utils/target/classes out/
cp -r WebServer/target/classes out/

#java -cp out/classes hillClimbing.instrumentation.Instrument out/classes/hillClimbing/solver out/classes/hillClimbing/solver
java -XX:-UseSplitVerifier -cp out/classes:out/classes/lib/* hillClimbing.webServer.WebServer