/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from 'react';

import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

import CameraScreen from './src/CameraScreen';
import SplashScreen from './src/SplashScreen';

import { fadeOut } from 'react-navigation-transitions';

import {createAppContainer} from 'react-navigation';
import {
  createStackNavigator,
  TransitionPresets
} from 'react-navigation-stack';

const MainNavigator = createStackNavigator({
    Home: {
      screen: SplashScreen,
    },
    CameraScreen: {
      screen: CameraScreen,
      navigationOptions: {
        imgUri: null,
        recData: null,
      }
    },
  },
  {
    headerShown: false,
    initialRouteName: 'Home',
    transitionConfig: () => fadeOut(2000),
  }
);

const App = createAppContainer(MainNavigator);

export default App;
