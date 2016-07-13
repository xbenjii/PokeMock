# PokeMock

Allows you to control your Pokemon GO character using the arrow keys on a PC.

Video: https://www.youtube.com/watch?v=eBBiNYdUzvQ

### To run the server (Port 9001 by default)
* `cd server`
* `npm install`
* Change the initial coordinates (lat,long) (I'll be automating this in the future to grab your current location)
* `node server`

### To run the client
* **REQUIRED**: Rooted device with the XPosed framework installed along with the [Mock Mock Locations](http://repo.xposed.info/module/com.brandonnalls.mockmocklocations) module.
* Get the IP and port the server is running on and enter it in the box
* Click start, minimize the app and run Pokemon GO

### To use
* Press the required arrow keys on the server console
