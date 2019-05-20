#!/usr/bin/env bash
mvn clean install
rm -r webServerOut
rm -r autoScalerLoadBalancerOut

mkdir -p webServerOut/
mkdir -p autoScalerLoadBalancerOut/

cp -r Database/target/classes webServerOut/
cp -r HillClimbing/target/classes webServerOut/
cp -r Instrumentation/target/classes webServerOut/
cp -r Utils/target/classes webServerOut/
cp -r WebServer/target/classes webServerOut/
cp -r AutoScalerLoadBalancer/target/classes autoScalerLoadBalancerOut/

java -cp webServerOut/classes hillClimbing.instrumentation.Instrument webServerOut/classes/hillClimbing/solver webServerOut/classes/hillClimbing/solver

#scp -r -i ./CCV-Project-Free.pem webServerOut ec2-user@ec2-3-84-30-87.compute-1.amazonaws.com:~/
#java -XX:-UseSplitVerifier -cp webServerOut/classes:webServerOut/classes/lib/* hillClimbing.webServer.WebServer