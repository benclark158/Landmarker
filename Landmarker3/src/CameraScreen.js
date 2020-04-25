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

import RNLocation from "react-native-location";

import { Camera } from 'expo-camera';
import * as Permissions from 'expo-permissions';

import Icon from 'react-native-vector-icons/Ionicons';

import {NativeModules} from 'react-native';

var TensorflowImage = NativeModules.TensorflowImage;

class CameraScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            hasCamPermission: false,
            hasRollPermission: false,
            cameraType: Camera.Constants.Type.back,
            bounceValue: new Animated.Value(500), //This is the initial position of the subview
            isHidden: true,
            imageUri: "",
        };
    }

    renderCamera = () => {
        const { hasCamPermission } = this.state
        const isActive = this.props.navigation.isFocused()

        if (hasCamPermission === null || !isActive) {
            return <View />;
        } else if (hasCamPermission === false || !isActive) {
            return <Text>No access to camera</Text>;
        } else {
            return (
                <View style={{ flex: 1 }}>
                    <Camera 
                        ref={ref => {
                            this.camera = ref;
                        }}
                        type={Camera.Constants.Type.back}
                        style={{ flex: 1 }}
                    />
                </View>
            );
        }
    }

    render() {
        var cameraView = this.renderCamera();

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
            
            <InformationView />
        </Animated.View>
        </>

        //<Image source={this.state.imagePath} /> <- what is this for?

        return [cameraView, screen];
    }

    takePicture = async () => {

        //Get location of phone!

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
/*
        var latitude;
        var longitude;

        RNLocation.getLatestLocation({ timeout: 500 }).then(latestLocation => {
            // Use the location here
            latitude = latestLocation["latitude"];
            longitude = latestLocation["longitude"];
        });
*/
        //take photo

        if (this.camera) {

            this.toggleSubview();

            var options = { quality: 1, base64: false, pauseAfterCapture: true};
            var data = await this.camera.takePictureAsync(options);
            
            this.camera.pausePreview();
            
            this.setState({
                imageUri: data.uri,
            })

            await TensorflowImage.classify("final_model.tflite", data.uri, 52.936593, -1.195524,
                (err) => {console.log(err)},
                (msg) => {
                    console.log(msg)
                }); 
        }
    }

    toggleSubview() {
        var hiddenVal = !this.state.isHidden;
        this.setState({
            isHidden: hiddenVal,
        });

        var size = Dimensions.get("window").height * 0.7;

        this.setState({
            buttonText: !hiddenVal ? "Show Subview" : "Hide Subview",
            viewHeight: size
        });

        var toValue = 0;

        if (hiddenVal) {
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

    async componentDidMount() {
        const { status } = await Permissions.askAsync(Permissions.CAMERA, Permissions.CAMERA_ROLL);
        //const { camRollStatus } = await Permissions.askAsync(Permissions.CAMERA_ROLL);
        this.setState({ 
            hasCamPermission: status === 'granted',
            //hasRollPermission: camRollStatus === 'granted',
        });

        //register back handler!
        this.backHandler = BackHandler.addEventListener(
            "hardwareBackPress",
            this.backAction
        );
    }

    componentWillUnmount(){
        this.backHandler.remove();
    }

    backAction = () => {
        if(this.props.navigation.isFocused()){
            if(this.state.isHidden){
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

    rotateCamera = () => {
        console.log(this.state.cameraType);
        if (this.state.cameraType == Camera.Constants.Type.front) {
            this.setState({
               cameraType: Camera.Constants.Type.back 
            });
        } else {
            this.setState({
                cameraType: Camera.Constants.Type.front
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