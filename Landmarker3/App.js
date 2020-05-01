/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

 import CameraScreen from './src/CameraScreen';
import SplashScreen from './src/SplashScreen';
import HistoryScreen from './src/HistoryScreen';
import MapScreen from './src/MapScreen';

import {createAppContainer} from 'react-navigation';
import {
  createStackNavigator,
} from 'react-navigation-stack';

const MainNavigator = createStackNavigator({
    SplashScreen: {
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
    },
    MapScreen: {
        screen: MapScreen,
        navigationOptions: {
            headerShown: true,
            title: "Map"
        },
       
    }
  },
  {
    initialRouteName: 'SplashScreen',
  }
);

const App = createAppContainer(MainNavigator);

export default App;
