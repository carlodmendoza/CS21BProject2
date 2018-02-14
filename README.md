# Memory Game

Undertale-Themed Tile Matching Memory Game

## Authorship

Project by: Carlo Mendoza and Gener Angelo Lopez
CS 21B, 2015-2016
Theme based on UNDERTALE by Toby Fox. Assets under fair use.

## Description

MEMORY GAME is exactly what it is - players match pairs of tiles. In 2-Player mode, the players race against each other, and the first player to match all 20 tiles wins.

## Usage

### Build Instructions

Run `BuildAll.bat`.

### Server Instructions (only if playing in 2-Player)

1. Run `runServer.bat`.

2. Keep the server program open for the clients to connect.

### Client Instructions:

1. Run `runPlayer.bat`.

2. A menu will pop up giving an option between Single Player and 2-Player modes. Select the desired game mode.

3. Enter the information needed.
For Single Player: player name only
For 2-Player: server IP address and player name

4. Once the name has been entered or both players have connected, the game will start.

5. Gameplay will end once a player has won.

## Game Mechanics

In Single Player mode, the player simply matches tiles until they match all. In 2-Player mode, the winning player is the one who matched all tiles first. Both players get the same tile arrangement. Gameplay ends for both players once a player has won. The server notifies the winner that they have won; conversely, the loser that they have lost.