import React from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
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
                <View style={{ width: "100%", height: "100%", flex: 1, alignItems: "center", justifyContent: "center", backgroundColor: '#afe9f0', }}
                    onStartShouldSetResponder={() => this.props.navigation.navigate('CameraScreen')}>
                    <Image source={require('./imgs/splash.png')} style = {{height: "80%", width: "80%"}} resizeMode="contain"/>
                    <Text style={{color: 'white'}}>
                        Tap to start
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
            //this.props.navigation.navigate('CameraScreen')
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