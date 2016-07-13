# PokeMock

Allows you to control your Pokemon GO character using the arrow keys in a terminal. The client is an Android app and the server is written in JavaScript for Node.js. Communication is done with websockets.

Tested working on LG G5 running 6.0.1.

Video: https://www.youtube.com/watch?v=eBBiNYdUzvQ

### To run the server (Port 9001 by default)
* `cd server`
* `npm install`
* Change the initial coordinates (lat,long) in server/server.js (I'll be automating this in the future to grab your current location)
* `node server`

### To run the client
* **REQUIRED**: Rooted device with the XPosed framework installed along with the [Mock Mock Locations](http://repo.xposed.info/module/com.brandonnalls.mockmocklocations) module (Or equivalent, as lonjg as you can hide the status of mock locations from the Pokemon GO app).
* Build the APK or just "Run" the app from Android Studio
* Get the IP and port the server is running on and enter it in the box
* Click start, minimize the app and run Pokemon GO

### To use
* Press the required arrow keys on the server console

### Current issues
* If the app crashes, there's a good chance you haven't set it as the mock location app in developer settings.
