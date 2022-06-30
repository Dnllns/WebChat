#!/bin/bash

export CLASSPATH=target/Practica2ChatRMI-0.0.1-SNAPSHOT.jar

java -cp target/Practica2ChatRMI-0.0.1-SNAPSHOT.jar -Djava.security.policy=registerit.policy -Djava.rmi.server.codebase=http://localhost:8080/Practica2ChatRMI-Web/ es.ubu.lsi.server.ChatServerDynamic
