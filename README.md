Under construction
# BrainWaveProject

![Results](/screenshots/graph.gif?raw=true "")

## How It Works
The Arduino takes readings from the Neurosky TGAM1 chip that are attached to electrodes contacting with a person's head. It parses the readings into integer values that we can send to our computer through USB serial. On the Java's side, we 


### Prerequisites

* [Java](https://www.java.com) - Java Runtime Environment
* [Arduino](https://www.arduino.cc/) - Arduino Mega Microcontroller
* [TGAM1(UART)](https://store.neurosky.com/products/eeg-tgam) - Neurosky TGAM1
* [jSerialComm-2.5.1](https://fazecast.github.io/jSerialComm/) - Arduino Serial Communication Library for Java
* [Dry Electrodes x 3] - I found them locally in a random store but I assume most electrodes would work


## Programmed In

* [Netbeans](https://netbeans.org/) - Java IDE
* [Arduino](https://https://www.arduino.cc/) - Arduino IDE


## Schematic

![Schematic](/screenshots/schematic.png?raw=true "")

## Screenshots

![The Hardware](/screenshots/hardware.png?raw=true "")
![The Software](/screenshots/graph.png?raw=true "")


## Authors

* **[Travis Ledo](https://travisledo.github.io)** - *Initial work* - [BrainWaveProject](https://github.com/TravisLedo)
