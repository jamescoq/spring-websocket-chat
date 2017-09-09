# spring-websocket-chat

This project represent simple spring websocket chat solution. 
It was created in March 2016.

## Features
* Group chat of all participants
* 1 : 1 chat which can be intialized by double clicking on participant name in the right column
* Incomming message notification marked by tab color highlighting
* Messages persistance when you accidentally close 1:1 messages tab and reopens the conversation again
* Chat reinitialization after page refresh
* Failover to long polling when websockets are not allowed by browser or server

## Technologies used

### Back-end:
* Spring Web MVC
* Spring Websockets
* Spring Security
* Spring Data
* HSQL DB

### Front-end:
* jQuery
* Boostrap
* stomp
* sockJS

## How run the project:
1. clone the repo
2. run> mvn clean tomcat7:run

### Optionally:
2. build the project> mvn clean install
3. rename created war file to chatapp.war 
4. deploy to your tomcat instalation

## Access from browser:
1. http://localhost:8080/chatapp
2. Login by username: whateverSingleWordUsername and password: chat
