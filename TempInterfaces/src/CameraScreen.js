'use strict';
import React, { PureComponent } from 'react';
import { AppRegistry, StyleSheet, Text, TouchableOpacity, View, Image } from 'react-native';
import { RNCamera } from 'react-native-camera';

class CameraScreen extends PureComponent {
  static navigationOptions = {
    title: 'Camera',
    headerLeft: null,
    headerVisible: false,
  };

  constructor(props) {
    super(props);

    this.camera = null;

    this.state = {
      camera: {      
        type: RNCamera.Constants.Type.front,
        flashMode: RNCamera.Constants.FlashMode.on
      }
    };
  }

  render() {
    const {navigate} = this.props.navigation;
    return (
      <View style={styles.container}>
        <RNCamera
          ref={ref => {
            this.camera = ref;
          }}
          style={styles.preview}
          type={this.state.camera.type}
          flashMode={this.state.camera.flashMode}
          captureAudio={false}
        />
        <View style={styles.buttonContainer}>
          <View style={styles.sideBContainer}>
            <TouchableOpacity style={styles.utilButton} onPress={this.rotateCamera.bind(this)}>
              <Image
              style={{flex: 1}}
              source={{uri: 'https://scontent-lht6-1.xx.fbcdn.net/v/t1.0-1/p40x40/39467330_1312329488901090_3737371444514914304_n.jpg?_nc_cat=100&_nc_oc=AQk42TwXyVXhjv0xtRvk1ZJlXNf9K1cIEOElAItmpTJJ8e5iwbmYkKhYxO7RM7-VsjM&_nc_ht=scontent-lht6-1.xx&oh=07062071816ae6b86897bd7636cdef81&oe=5E5562CC'}}
            />
            </TouchableOpacity>
          </View>
          <View style={styles.mainBContainer}>
            <TouchableOpacity onPress={this.takePicture.bind(this)} style={styles.capButton}>
              <Image
              style={{flex: 1}}
              source={{uri: 'https://facebook.github.io/react-native/img/tiny_logo.png'}}
            />
            </TouchableOpacity>
          </View>
          <View style={styles.sideBContainer}>
            <TouchableOpacity style={styles.utilButton} onPress={() => navigate('InfoScreen')}>
              <Image
              style={{flex: 1}}
              source={{uri: 'https://facebook.github.io/react-native/img/tiny_logo.png'}}
            />
            </TouchableOpacity>
          </View>
        </View>
      </View>
    );
  }

  rotateCamera = () => {
    console.log(this.camera.type);
    if(this.state.camera.type == RNCamera.Constants.Type.front){
      this.setState({
        camera: {      
          type: RNCamera.Constants.Type.back,
          flashMode: RNCamera.Constants.FlashMode.on
        }
      });
    } else {
      this.setState({
        camera: {      
          type: RNCamera.Constants.Type.front,
          flashMode: RNCamera.Constants.FlashMode.on
        }
      });
    }
  }

  takePicture = async() => {
    if (this.camera) {
      const options = { quality: 0.5, base64: true };
      const data = await this.camera.takePictureAsync(options);
      console.log(data.uri);
    }
  };
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: 'black',
  },
  preview: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'center',
    zIndex: 0,
  },
  capture: {
    flex: 0,
    backgroundColor: '#fff',
    borderRadius: 5,
    padding: 15,
    paddingHorizontal: 20,
    alignSelf: 'center',
    margin: 20,
  },
  buttonContainer: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    display: 'flex',
    flex: 0,
    zIndex: 5,
    flexDirection: 'row',
    alignItems: "center",
    position: 'absolute',
  },
  capButton: {
    height: 75,
    width: 75,
    margin: 10,
    flex: 2,
    alignSelf: 'center',
    borderColor: "white",
    borderWidth: 2,
    bottom: 0
  },
  mainBContainer: {
    flex: 4
  },
  sideBContainer: {
    flex: 1,
    flexDirection: 'row',
    bottom: 0,
    margin: 0,
    padding: 0,
    alignItems: 'center',
    alignSelf: 'flex-end'
  },
  utilButton: {
    height: 40,
    width: 40,
    maxWidth: 40,
    maxWidth: 40,
    bottom: 0,
    margin: 10,
    flex: 1,
    borderColor: "white",
    borderWidth: 2,
    bottom: 0,
    right: 0
  }
});

export default CameraScreen;