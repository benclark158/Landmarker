import React from 'react';
import {
    StyleSheet,
    View,
    Text,
  } from 'react-native';

import { StackActions } from '@react-navigation/native';
import RNLocation from "react-native-location";

class SplashScreen extends React.Component{

    render(){
        const viewStyles = [
            styles.container, {backgroundColor: 'red'}
        ];
        const textStyles = {
            color: 'white',
            fontSize: 40,
            fontWeight: 'bold'
        };

        return (
            <>
                <View style={viewStyles}>
                    <Text style={textStyles}>
                        Splash Screen
                    </Text>
                </View>
            </>
        );
    }

    waitFor1Sec = async() => {
        return new Promise((resolve) =>
          setTimeout(() => { resolve('result') }, 2000)
        );
    }

    async componentDidMount() {
        RNLocation.requestPermission({
            ios: 'whenInUse', // or 'always'
            android: {
              detail: 'coarse', // or 'fine'
              rationale: {
                title: "We need to access your location",
                message: "We use your location to show where you are on the map",
                buttonPositive: "OK",
                buttonNegative: "Cancel"
              }
            }
        });
        // Preload data from an external API
        // Preload data using AsyncStorage
        const data = await this.waitFor1Sec();
      
        if (data !== null) {
            this.props.navigation.navigate('CameraScreen')
        }
      }
}

const styles = StyleSheet.create({
    container: {
      flex: 1,
      width: '100%',
    },
  });

export default SplashScreen;