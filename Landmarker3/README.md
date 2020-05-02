# Landmarker3

This is the offical app folder for Landmarker3

## Function

The purpose of this app is to allow users to take photos of a given UK landmark. The app will then tell the user what the landmark is. More specifically the app is tarketed at University of Nottingham students.

The app will recognise any (dependant on training) building on a UoN campus (UK based). This will be used to help new and international students as well as visitors and open day guests to find their way around university, even if they cannot see the signs, or are approaching the building from an unknown angle.

## Integration

This app is intended to have cross-platform support, however, iOS support has not been tested yet. However, the app is fully tested using Android.

## How to run the app

If you want to build the app from the source code then you must have node.js and React Native properly configured on your device.

For use on a Windows PC is strongly suggest using Visual Studio Code for this.

To run the app on an Android device, that is connected to your computer, simply run `react-native run-android`, using `react-native run-ios` will run the application on a iOS device. For more information on this please visit the React Native website.

## Reporting Issues

If you are having issues with the application, then please report them to the issues page.

## Future Development

This app is open to future development. The following features are planned future developments:
- iOS support (when access to iOS is available)
- Improved tensorflow model
- Improved dynamic data I/O for Tensorflow (currently hardcoded)

### How to update the Tensorflow model?

The tensorflow model can be updated by replacing the current `.tlite` file in the android assets folder. If more outputs are being added then the tensorflow package may have to updated accordingly.
