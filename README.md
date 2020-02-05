#ParadoxSpeedSync
A cross-platform tool to overcome the limitations of the speed controls of Paradox Interactive games.

##What
In games like Crusader Kings II and Hearts of Iron IV, only the host of a multiplayer session is able to control the game speed
and I always thought that it would be a nice idea to simply run the game at the speed of the slowest player. That way,
you don't have to ask for permission all the time if you want to adjust the game speed.

##Usage
Currently it is only a command line application, so you have to run it in your terminal. I'm planning to add a UI later.

####Client
A client can connect to a server as follows:
```
java -jar ParadoxSpeedSync.jar -c <host> <port>
```
While starting a client, you will be asked to set up three key bindings:
* A speed up key. This will be the key you press to suggest a speed increase.
* A speed down key. This will be the key you press to suggest a speed decrease.
* A sync key. Sometimes the actual in-game speed will go out of sync with the server speed, then you can press this key to force a re-sync.

Make sure that none of these keys conflict with in-game key bindings.

####Host
The host of the multiplayer session has to start a server using:
```
java -jar ParadoxSpeedSync.jar -s <port>
```
While starting a server, you will be asked to set up two key bindings:
* The actual speed up key binding used in-game. This is the key the server will simulate when speeding up.
* The actual speed down key binding used in-game. This is the key the server will simulate when slowing down.

Make sure that your server is reachable through the given port, you might need to set up port forwarding in your router settings.
In addition to that, the host also has to connect to it's own server by starting a client as shown above.

##Planned Features
* Small UI to simplify setup
* In-game overlay to see your current speed
* Different speed agreement strategies

##Additional Information
To be able to listen to global key events I used the [jnativehook](https://github.com/kwhat/jnativehook) library.

