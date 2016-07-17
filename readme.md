# PokeMock

Allows you to control your Pokemon GO character using a screen overlay.

Tested working on LG G5 running 6.0.1.

### To run the app
* **REQUIRED**: Rooted device with the XPosed framework installed along with the [Mock Mock Locations](http://repo.xposed.info/module/com.brandonnalls.mockmocklocations) module (Or equivalent, as long as you can hide the status of mock locations from the Pokemon GO app).
* Google API key with maps and places enabled.
* Don't forget to add your API key to the AndroidManifest.xml.
* Build the APK or just "Run" the app from Android Studio
* Click start, accept the permissions and run the Pokemon GO app.

### To use
* Use the joystick in the direction you want
* Note: Joystick up means north.

### Current issues
* If the app crashes, there's a good chance you haven't set it as the mock location app in developer settings.

### To DO
* Rebuild the app so it doesn't require a server (floating window with arrow keys?)
