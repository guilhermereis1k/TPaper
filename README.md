# TPaper

A lightweight Paper plugin that adds custom teleport items with settable locations, delay, limited uses, and cooldown.

## Features

- Custom teleport item

- Set location by right-clicking the top of a block

- Teleport delay (cancels if player moves)

- Cooldown only if teleport succeeds

- Limited uses, item gets removed when empty

- Simple and fast to use

## Commands

### /tpaper getMainPaper

Gives the player the MainTown Teleport Paper.
OP only.


### /tpaper getCustomPaper

Gives the player the Custom Teleport Paper, which allows defining a teleport location by right-clicking a block.
OP only.


### /tpaper setCoords <x> <y> <z>

- Sets the MainTown coordinates in the config.yml.

- Automatically saves the player’s current world

- Updates X / Y / Z

- Used by the MainTown teleport item

- OP only.

## Requirements

PaperMC 1.20+

Java 17+

## How it works

Player gets the item

Right-click a block → location saved

Use item → starts delay

If delay ends → teleports

If player moves → cancels (no cooldown)

## Build
./gradlew build


## Output JAR:
build/libs/TPaper-1.0-SNAPSHOT.jar

## Install

Drop the JAR into your server’s plugins folder and restart.
