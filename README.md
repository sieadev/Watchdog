<div align='center'>

<h1>Watchdog🐕️</h1>

<p>Fast. Safe. Effective.</p>

<h4> <a href="https://github.com/sieadev/watchdog/issues"> Report Bug </a> <span> · </span> <a href="https://github.com/sieadev/watchdog/issues"> Request Feature </a> </h4>
<br>
</div>

## Overview

Watchdog is a user-friendly and lightweight Discord bot enabling every user to self-report people whom they may suspect to be engaging in inappropriate or harmful behavior. The bot aims to create a safer and more organized community by allowing members to report various types of infractions directly within Discord.

## Features
- **Slash Command Reporting**: Users can report members through easy-to-use slash commands.
- **Customizable Report Types**: Predefined report categories such as cheating, doxxing, scamming, and more.
- **Rate Limiting**: Prevents abuse by limiting the number of reports a user can submit within a specific time frame.

### Report Types

The bot supports the following report types:
- Cheating in Video Game
- Doxxing (Publicizing Private Information)
- Scamming or Fraudulent Activities 
- Malicious Media (Links, Texts, Visual Material)
- Hate Speech
- Bullying or Harassment
- Threats of Violence
- Illegal Activity

## Getting Started

### Prerequisites

- Java 16 or higher
- Maven
- A MySQL Database
- A Discord bot token (which can be obtained from the [Discord Developer Portal](https://discord.com/developers/applications))

### Installation

1. **Clone the repository:**
   ```SH
   git clone https://github.com/sieadev/watchdog.git
   cd Watchdog
   ```
2. **Compile the Bot**
   ```SH
   mvn clean install
   ```
3. **Execute the jar**  
After the compilation is complete, you can execute the generated JAR file. The exact command might vary depending on your environment, but it generally looks like this:
   ```SH
   java -jar target/watchdog-1.0-SNAPSHOT.jar
   ```

4. **Setup Config**  
The previous step will create a file called `config.yml` in the jar's directory. Open this file with your preferred text editor and add your Discord Bot Token along with your MySQL database credentials. Here is an example of what your `config.yml` should look like
   ```YAML
   token: your-discord-bot-token
   sql:
     ip: localhost
     name: database_name
     user: database_user
     password: database_password
   ```
   Replace the placeholders (`your-discord-bot-token`, `database_name`, `database_user`, `database_password`) with your actual data.
5. **Restart the Bot**  
After configuring the `config.yml` file, restart the bot to apply the changes:
   ```SH
   java -jar target/watchdog-1.0-SNAPSHOT.jar
   ```
