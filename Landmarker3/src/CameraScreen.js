/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from "react";

import InformationView from "./InformationView.js";

import {
    StyleSheet,
    Animated,
    Text,
    TouchableOpacity,
    View,
    Dimensions,
    Image,
    Button,
    Alert,
    BackHandler
} from "react-native";

import Icon from 'react-native-vector-icons/Ionicons';

import { RNCamera } from "react-native-camera";

import RNLocation from "react-native-location";

import * as tf from '@tensorflow/tfjs';

var isHidden = true;

class CameraScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isTFReady: false,
            model: null,
            responseURI: "",
            bounceValue: new Animated.Value(500), //This is the initial position of the subview
            buttonText: "Show Subview",
            viewHeight: 500,
            camera: {
                type: RNCamera.Constants.Type.back,
                flashMode: RNCamera.Constants.FlashMode.off
            },
            results: {
                items: {
                    item1: "item 1!",
                    item2: "item 2!",
                    item3: "item 3!",
                    item4: "item 4!",
                    item5: "item 5!"
                },
                finalName: "name",
                imagePath: "imgs"
            }
        };
    }

    backAction = () => {
        if(this.props.navigation.isFocused()){
            if(isHidden){
                BackHandler.exitApp();
                return true;
            } else {
                this.toggleSubview();
                return true;
            }
        } else {
            return false;
        }
    }

    async componentDidMount(){
        this.backHandler = BackHandler.addEventListener(
            "hardwareBackPress",
            this.backAction
        );

        RNLocation.requestPermission({
            ios: "whenInUse",
            android: {
              detail: "fine",
              rationale: {
                title: "Location permission",
                message: "We use your location to demo the library",
                buttonPositive: "OK",
                buttonNegative: "Cancel"
              }
            }
        });

        await tf.ready();

        this.setState({
            isTFReady: true,
        });

        //const model = await tf.loadLayersModel('localstorage://model-1,json');

        //this.setState({
        //    model: model,
        //});
    }

    componentWillUnmount(){
        this.backHandler.remove();
    }
    
    renderCamera = () => {
        const isActive = this.props.navigation.isFocused()
        if(isActive == true){
            return(
                <RNCamera
                    ref={ref => {
                        this.camera = ref;
                    }}
                    style={styles.camera}
                    type={this.state.camera.type}
                    flashMode={this.state.camera.flashMode}
                    captureAudio={false}
                />
            );
        } else {
            return null
        }
    };

    render() {
        var cView = this.renderCamera();

        var screen = <>
        <View style={styles.buttonContainer}>
            <View style={styles.sideBContainer}>
                <TouchableOpacity
                    style={styles.utilButton}
                    onPress={this.rotateCamera.bind(this)}>
                    <Icon name="ios-reverse-camera" size={40} color="rgba(255, 255, 255, 0.8)"
                        style={{alignSelf: "center"}}
                    />
                </TouchableOpacity>
            </View>
            <View style={styles.mainBContainer}>
                <TouchableOpacity
                    onPress={this.takePicture.bind(this)}
                    style={styles.capButton}>
                    <Icon name="ios-camera" size={80} color="#ed6a5a"
                        style={{alignSelf: "center", display: 'none'}}
                    />
                </TouchableOpacity>
            </View>
            <View style={styles.sideBContainer}>
                <TouchableOpacity
                    style={styles.utilButton}
                    onPress={() => this.props.navigation.navigate('MapScreen')}>
                    <Icon name="md-map" size={40} color="rgba(255, 255, 255, 0.8)"
                        style={{alignSelf: "center"}}
                    />
                </TouchableOpacity>
            </View>
        </View>

        <Animated.View
            style={[
                styles.subView,
                {
                    transform: [{ translateY: this.state.bounceValue }],
                    height: this.state.viewHeight
                }
            ]}>
            <View style={styles.subViewView}>
                <TouchableOpacity onPress={() => this.toggleSubview()}>
                    <Text style={styles.subViewText}>Close</Text>
                </TouchableOpacity>
            </View>
            <Image source={this.state.imagePath} />
            <InformationView />
        </Animated.View>
        </>
    
        return [cView, screen];
    
    }

    toggleSubview() {
        isHidden = !isHidden;

        var size = Dimensions.get("window").height * 0.7;

        this.setState({
            buttonText: !isHidden ? "Show Subview" : "Hide Subview",
            viewHeight: size
        });

        var toValue = 0;

        if (isHidden) {
            toValue = size;
            this.camera.resumePreview();
        }

        //This will animate the transalteY of the subview between 0 & 100 depending on its current state
        //100 comes from the style below, which is the height of the subview.
        Animated.spring(this.state.bounceValue, {
            toValue: toValue,
            velocity: 6.0,
            tension: 2.0,
            friction: 8.0,
            useNativeDriver: true,
        }).start();
    }

    takePicture = async () => {
        var ready = this.state.isTFReady;

        console.log(ready);

        const model = this.state.model;

        RNLocation.configure({
            distanceFilter: 50, // Meters
            desiredAccuracy: {
            ios: "best",
            android: "balancedPowerAccuracy"
            },
            // Android only
            androidProvider: "auto",
            interval: 5000, // Milliseconds
            fastestInterval: 500, // Milliseconds
            maxWaitTime: 5000, // Milliseconds
            // iOS Only
            activityType: "other",
            allowsBackgroundLocationUpdates: false,
            headingFilter: 1, // Degrees
            headingOrientation: "portrait",
            pausesLocationUpdatesAutomatically: false,
            showsBackgroundLocationIndicator: false,
        });

        var latitude;
        var longitude;

        RNLocation.getLatestLocation({ timeout: 500 }).then(latestLocation => {
            // Use the location here
            latitude = latestLocation["latitude"];
            longitude = latestLocation["longitude"];
        });

        if (this.camera) {

            this.toggleSubview();

            var options = { quality: 0.0001, base64: false, fixOrientation: true, pauseAfterCapture: true, width: 244};
            var data = await this.camera.takePictureAsync(options);

            var imagePath = data.uri;
            
            console.log(latitude + " : " + longitude);
            console.log(imagePath);

            /*
            await ImageResizer.createResizedImage(imagePathBig, 224, 224, 'JPEG', 10, 0, "data/user/0/com.tempinterfaces/cache/Camera/smaller").then((response) => {
                        // response.uri is the URI of the new image that can now be displayed, uploaded...
                        // response.path is the path of the new image
                        // response.name is the name of the new image with the extension
                        // response.size is the size of the new image
            console.log('test :-' + response.uri);

            this.setState({
                responseURI: response.uri,
            });

            }).catch((err) => {
                // Oops, something went wrong. Check that the filename is correct and
                // inspect err to get more details.
                console.error(err);
            });*/

            const resp = await fetch(imagePath, {}, {isBinary: true});
            const rawImgData = await resp.arrayBuffer();
            const image = decodeJpeg(rawImgData);

            const tensorInput = tf.Tensor([[latitude, longitude], image]);
            const output = model.predict(tensorInput);

            console.log(output.toString())
        
            /*var jRes = result;

            this.setState({
                items: {
                    item1: jRes[0].detectedClass + ": " + jRes[0].confidenceInClass,
                    item2: jRes[1].detectedClass + ": " + jRes[1].confidenceInClass,
                    item3: jRes[2].detectedClass + ": " + jRes[2].confidenceInClass,
                    item4: jRes[3].detectedClass + ": " + jRes[3].confidenceInClass,
                    item5: jRes[4].detectedClass + ": " + jRes[4].confidenceInClass
                }
            });*/
        }
    };

    rotateCamera = () => {
        console.log(this.camera.type);
        if (this.state.camera.type == RNCamera.Constants.Type.front) {
            this.setState({
                camera: {
                    type: RNCamera.Constants.Type.back
                }
            });
        } else {
            this.setState({
                camera: {
                    type: RNCamera.Constants.Type.front
                }
            });
        }
    };
}

const styles = StyleSheet.create({
    camera: {
        flex: 1,
        width: "100%"
    },
    subView: {
        position: "absolute",
        bottom: 0,
        left: 0,
        right: 0,
        zIndex: 1000,
        backgroundColor: "#fff",
        borderRadius: 20,
        shadowOffset: {width: 5, height: 5},
        shadowOpacity: 0.5,
        shadowRadius: 5,
        shadowColor: '#000', 
    },
    subViewView: {
        right: 0,
        display: "flex",
        width: "100%",
        marginLeft: "0%",
        height: "10%",
        paddingTop: "2%",
        paddingRight: "2%",
        backgroundColor: "#afe9f0",
        borderBottomWidth: 5,
        borderColor: "#00d3ec",
        borderTopRightRadius: 20,
        borderTopLeftRadius: 20,
    }, 
    subViewText: {
        fontWeight: 'bold',
        color: "#fff",
        textAlign: "right",
        fontSize: 20,
    },
    capture: {
        flex: 0,
        backgroundColor: "rgba(0,0,0,0)",
        borderRadius: 5,
        padding: 15,
        paddingHorizontal: 20,
        alignSelf: "center",
        margin: 20
    },
    buttonContainer: {
        position: "absolute",
        left: 0,
        right: 0,
        bottom: 0,
        display: "flex",
        flex: 0,
        zIndex: 5,
        flexDirection: "row",
        alignItems: "center",
        position: "absolute"
    },
    capButton: {
        height: 75,
        width: 75,
        margin: 10,
        flex: 2,
        alignSelf: "center",
        justifyContent: "center",
        textAlign: "center",
        borderColor: "white",
        borderWidth: 5,
        borderRadius: 50,
        bottom: 0
    },
    mainBContainer: {
        flex: 4
    },
    sideBContainer: {
        flex: 1,
        flexDirection: "row",
        bottom: 0,
        margin: 0,
        padding: 0,
        alignItems: "center",
        alignSelf: "flex-end"
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
        borderWidth: 0,
        bottom: 0,
        right: 0
    }
});

export default CameraScreen;
