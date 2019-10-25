/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from 'react';

import CameraScreen from './src/CameraScreen';

import MainScreen from './src/MainScreen';

import {createAppContainer} from 'react-navigation';
import {createStackNavigator} from 'react-navigation-stack';

const MainNavigator = createStackNavigator({
  Home: {screen: CameraScreen},
  InfoScreen: {screen: MainScreen},
});

const App = createAppContainer(MainNavigator);

export default App;
