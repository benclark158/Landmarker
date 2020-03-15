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
  Button,
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
import HistoryScreen from './src/HistoryScreen';

import { fadeOut } from 'react-navigation-transitions';

import {createAppContainer} from 'react-navigation';
import {
  createStackNavigator,
  TransitionPresets
} from 'react-navigation-stack';

const MainNavigator = createStackNavigator({
    Home: {
      screen: SplashScreen,
      navigationOptions:{
        headerShown: false,
    }
    },
    CameraScreen: {
      screen: CameraScreen,
      navigationOptions: {
        imgUri: null,
        recData: null,
        headerShown: false,
      }
    },
    HistoryScreen: {
        screen: HistoryScreen,
        navigationOptions:{
            headerShown: true,
            title: "Search History"
        }
    }
  },
  {
    initialRouteName: 'CameraScreen',
    transitionConfig: () => fadeOut(2000),
  }
);

const App = createAppContainer(MainNavigator);

export default App;
