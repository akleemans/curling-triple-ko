# curling-triple-ko

Simulation and optimization of the Triple knockout system in Curling.
The goal is to minimize the amount of "double encounters", so that a team will not play against the
same opponent team multiple times. This can quite likely not be avoided completely, but depending on
the tournament structure, there will be more or less of those.

## Usage

Run the latest docker image (from DockerHub):

    docker run -d -p 8080:8080 adrianus/curling-triple-knockout:latest

Then, open your browser at `http://localhost:8080/`.

## Features

There are two main features:

* **Tournament simulation**: For a given tournament configuration, find out how many "double
  encounters" will happen in average. It can be specified how many times the tournament should be
  simulated.
* **Optimizer**: Try to optimize a given tournament by exchanging matches (where a team will go to
  when a game is lost/won).

There are examples of 16, 24 and 32 teams, with basic configuration used in various tournaments, and
their optimized variants. See `resources` for tournament structures (brackets / roads).

## Configuration format

Example of a configuration file:

```
*A01 A09 B01
*A02 A09 B01
*A03 A10 B02
*A04 A10 B02
*A05 A11 B03
*A06 A11 B03
*A07 A12 B04
*A08 A12 B04

A09 A13 B07
A10 A13 B07
A11 A14 B08
A12 A14 B08

A13 - B10
A14 - B09
...
```

* Each row has 3 components: Name of the game, follow-up game for winner, follow-up game for loser.
  These are separated by a space. For example "A09 A13 B07" means, the name of the game is A09,
  the winner will go to A13, the loser to B07.
* If the row starts with an asterisk, it will be "seeded" with teams on Simulation start.
* An empty line means that a new "block" starts, which is only used for optimization. It defines the
  games that can be exchanged. For example from A01, the winner team could go to A12 (instead of
  A09) as above, but not to A13.
* A "-" means, the team is out or no longer relevant for the simulation (e.g. D-Road or
  consolidation cup).

## Development

Build image:

    (cd frontend) npm run release:local
    ./gradlew build
    docker build -t triple-ko-simulation .
    docker run -d -p 8080:8080 triple-ko-simulation

Push to Docker Hub:

    docker tag triple-ko-simulation adrianus/curling-triple-knockout:latest
    docker push adrianus/curling-triple-knockout:latest
