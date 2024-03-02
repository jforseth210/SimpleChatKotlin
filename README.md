# Simple Chat
A simple instant messaging application using Vue, Shadcn UI, TailwindCSS, Ktor, and Exposed. 

## Installation 
Git, Python, and NPM are required to install the server. 
Download the project from github
``` bash
git clone https://github.com/jforseth210/SimpleChatKotlin
cd SimpleChat
```

Build the vue project
```bash
cd web
npm install
npm run build
cd ..
```

This project requires a MariaDB database (other databases may work by editing driverClassName in src/main/kotlin/com/example/dao/DatabaseSingleton.kt, but this is untested).

The connection details can be specified in src/main/resources/application.conf
```bash
cd src/main/resources
cp application.conf.example application.conf
```
Create a MariaDB database and user, and update jdbcUrl, username, and password accordingly.

Run the server
```bash
./gradlew run
```

## Usage
Open http://localhost:8080 in your browser

Create an account (any username and password will do)

Have whoever you want to chat with create one to

Click the plus at the top of the conversation list 
and enter your friend's username. 

Click the text box on the bottom right and send a message!

