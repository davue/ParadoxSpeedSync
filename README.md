# ParadoxSpeedSync [![Jenkins](https://jenkins.stammgruppe.eu/job/ParadoxSpeedSync/job/master/badge/icon)](https://jenkins.stammgruppe.eu/blue/organizations/jenkins/ParadoxSpeedSync/activity?branch=master)
A cross-platform compatible tool to overcome the limitations of the speed controls of Clausewitz Engine games in multiplayer.

## What
In games like Crusader Kings II and Hearts of Iron IV, only the host of a multiplayer session is able to control the game speed
and I always thought that it would be a nice idea to simply run the game at the speed of the slowest player. That way,
you don't have to ask for permission all the time if you want to adjust the game speed.

## How
This small overlay keeps track of the desired game speed of each player and then simulates the key presses needed to achieve the slowest speed of all players on the host.
The protocol is totally **not secure** as everything is sent in plaintext over TCP including the server password. So you should not use any passwords you are using anywhere else. But why would you do that anyways?

## Usage
When you start the application you first have to setup the key bindings. You can still edit those later by clicking the settings button on top.

![](https://raw.githubusercontent.com/davue/ParadoxSpeedSync/master/images/setup.gif)

The first two are the buttons you will press to change your speed suggestion. **If you are not hosting**, you can click on Save now.

If you are hosting:

The last two are the keys of the speed controls in-game.
On the bottom you can choose one of the Presets used to determine the maximum speed and default speed or choose "Custom" and set your own values.

Then you can either connect to an already running server or host one yourself.  
**Important:** At the moment, the player which hosts the in-game session also has to host the speed sync server.

After connecting, it will look like this:

![](https://raw.githubusercontent.com/davue/ParadoxSpeedSync/master/images/running.gif)

Your own speed is on top while all other clients are below. A red client shows you that this client is running slower than you.

## Download
You can either:
* Get the latest master build directly from [Jenkins](https://jenkins.stammgruppe.eu/job/ParadoxSpeedSync/job/master/lastSuccessfulBuild/artifact/target/ParadoxSpeedSync.jar).
* Or get the latest release here from GitHub.

## Troubleshooting
* Make sure that your server is reachable through the given port, you might need to set up port forwarding in your router settings.
* The overlay will not be visible in fullscreen mode. You have to run your game in windowed or borderless windowed mode or move the overlay to another screen.
* If you're hosting, do not use the in-game speed control key bindings for your client.

## Known Issues
* Stellaris somehow does not support keybound speed controls in multiplayer, so this will not work until they fix it.

## Planned Features
* Server does not try to control speed when game is out of focus
* Dedicated server mode (such that the client that hosts the game can be on another device as the speed sync server)
* Different speed agreement strategies

## Additional Information
To be able to listen to global key events I used the [jnativehook](https://github.com/kwhat/jnativehook) library.

