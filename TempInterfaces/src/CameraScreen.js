'use strict';
import React, { PureComponent } from 'react';
import { AppRegistry, StyleSheet, Text, TouchableOpacity, View, Image } from 'react-native';
import { RNCamera } from 'react-native-camera';
import Tflite from 'tflite-react-native';
import ImageResizer from 'react-native-image-resizer';

let tflite = new Tflite();

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
        type: RNCamera.Constants.Type.back,
        flashMode: RNCamera.Constants.FlashMode.off
      },
      items: {
        item1: "item 1!",
        item2: "item 2!",
        item3: "item 3!",
        item4: "item 4!",
        item5: "item 5!",
      }
    };

    tflite.loadModel({
      model: 'ssd_mobilenet.tflite',// required
      labels: 'ssd_mobilenet.txt',  // required
      numThreads: 1,                              // defaults to 1  
    },
    (err, res) => {
      if(err)
        console.log(err);
      else
        console.log(res);
    });
  }

  render() {
    const {navigate} = this.props.navigation;
    return (
      <View style={styles.container}>
        <View>
          <Text>{ this.state.items.item1 }</Text>
          <Text>{ this.state.items.item2 }</Text>
          <Text>{ this.state.items.item3 }</Text>
          <Text>{ this.state.items.item4 }</Text>
          <Text>{ this.state.items.item5 }</Text>
        </View>

        <RNCamera
          ref={ref => {
            this.camera = ref;
          }}
          style={styles.preview}
          type={this.state.camera.type}
          flashMode={this.state.camera.flashMode}
          captureAudio={false}
          ratio="1:1"
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
        }
      });
    } else {
      this.setState({
        camera: {      
          type: RNCamera.Constants.Type.front,
        }
      });
    }
  }

  takePicture = async() => {
    if (this.camera) {
      var options = { quality: 1, base64: true, width: 224, height: 224 };
      var data = await this.camera.takePictureAsync(options);

      var imagePathBig = data.uri;
      var imagePath = data.uri;
      
      console.log(imagePathBig);
      console.log(imagePath);

      
      /*ImageResizer.createResizedImage(imagePathBig, 224, 224, 'JPEG', 10, 0, "data/user/0/com.tempinterfaces/cache/Camera/smaller").then((response) => {
        // response.uri is the URI of the new image that can now be displayed, uploaded...
        // response.path is the path of the new image
        // response.name is the name of the new image with the extension
        // response.size is the size of the new image
        console.log('test :-' + response.uri);
      }).catch((err) => {
        // Oops, something went wrong. Check that the filename is correct and
        // inspect err to get more details.
        console.error(err);
      });*/

      var result = await tflite.runModelOnImage({
        path: imagePath,
        imageMean: 0,
        imageStd: 0,
        threshold: 0.3,       // defaults to 0.1
        numResultsPerClass: 1,// defaults to 5
      },
        (err, res) => {
          if(err)
            console.log(err);
          else {
            console.log(res);

            var jRes = res;

            this.setState({
              items: {
                item1: jRes[0].detectedClass + ": " + jRes[0].confidenceInClass,
                //item2: jRes[1].detectedClass + ": " + jRes[1].confidenceInClass,
                //item3: jRes[2].detectedClass + ": " + jRes[2].confidenceInClass,
                //item4: jRes[3].detectedClass + ": " + jRes[3].confidenceInClass,
                //item5: jRes[4].detectedClass + ": " + jRes[4].confidenceInClass
              }
            });
          }
        }
      );
      //console.log(result);
    }
  };
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: 'white',
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