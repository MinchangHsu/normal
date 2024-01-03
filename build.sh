#!/bin/bash

function start_(){
  mvn clean package -Dmaven.test.skip=true

  cp -f ".\target\normal-0.0.1-SNAPSHOT.jar" "C:\Users\caster.hsu\Documents\workFile\dockerData\normalProject\service\normal-0.0.1-SNAPSHOT.jar"

}

count=1
while [ true ]
do
    if [[ $count -eq 1 ]]; then
        start_
        count=$((count+1))
    else
        read -p "Need deploy other project? (1:y,0:n): " needDepolyOtherProject

        if [[ $needDepolyOtherProject -eq 0 ]]; then
            exit 1
        else
            clear
            start_
        fi
    fi
done