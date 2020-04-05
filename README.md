# BrainWaveProject

![Results](/images/graph.gif?raw=true "")

## How It Works
The Arduino takes readings from the Neurosky TGAM1 chip that are attached to electrodes contacting with a person's head. It parses the readings into integer values that we can send to our computer through USB serial. On the Java's side, we take that data and graph in real time.

The project can detect two types of brain waves. How hard a person is concentrating and also how clear their mind is from a meditative state.


## Uses
There already exists commercial headsets with this technology being created for products such as games and meditation devices. I wanted to create my own for much less and also develop my own software that graphs the brainwaves. In the future I could develop much more sophisticated applications with the device if needed.


### Prerequisites

* [Java](https://www.java.com) - Java Runtime Environment
* [Arduino](https://www.arduino.cc/) - Arduino Uno Microcontroller
* [TGAM1](https://store.neurosky.com/products/eeg-tgam) - Neurosky TGAM1
* [jSerialComm-2.5.1](https://fazecast.github.io/jSerialComm/) - Arduino Serial Communication Library for Java
* [Dry Electrodes x 3] - I found them locally in a random store but I assume most electrodes would work


## Programmed In

* [Netbeans](https://netbeans.org/) - Java IDE
* [Arduino](https://https://www.arduino.cc/) - Arduino IDE


## Images

![The Hardware](/images/hardware.png?raw=true "")
![The Software](/images/graph.png?raw=true "")


## Authors

* **[Travis Ledo](https://travisledo.github.io)** - *Initial work* - [BrainWaveProject](https://github.com/TravisLedo)
