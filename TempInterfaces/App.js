/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from 'react';

import CameraScreen from './src/CameraScreen';
import TensorScreen from './src/TensorScreen'
import MainScreen from './src/MainScreen';

import {createAppContainer} from 'react-navigation';
import {createStackNavigator} from 'react-navigation-stack';

const MainNavigator = createStackNavigator({
  //Home: {screen: TensorScreen},
  Home: {screen: CameraScreen},
  InfoScreen: {
    screen: MainScreen,
    navigationOptions: {
      header: null,
      imgUri: null,
      recData: null
     }},
  //CameraScreen: {screen: TensorScreen}
});

const App = createAppContainer(MainNavigator);

export default App;
