# Landmarker

This repo is the store for all Landmarker related files. This includes all of the Tensorflow and CNN files as well as the full source code. 

## Function

The purpose of this app is to allow users to take photos of a given UK landmark. The app will then tell the user what the landmark is. More specifically the app is tarketed at University of Nottingham students.

The app will recognise any (dependant on training) building on a UoN campus (UK based). This will be used to help new and international students as well as visitors and open day guests to find their way around university, even if they cannot see the signs, or are approaching the building from an unknown angle.

## Structure

This repo is made up of an assortment of sub-directories where specific parts can be found;

- **Landmarker3** - React native project for the current version of the app
- **Tensorflow** - Tensorflow project code, this includes training code as well as the network structure
- **TempInterfaces** - This is a react native project in which test development was made for the project. For example, testing plugins and user interace functionality.
- **TrainingCompilation** - This is a Java project for downloading and formatting all of images required for training. Most of the images used to train the CNN are publicly accessable images. However, some are private images taken to train the CNN for University of Nottingham buildings, these are not included in the repository.

