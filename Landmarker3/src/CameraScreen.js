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
    Button
} from "react-native";

import { Icon } from 'react-native-elements'

import { RNCamera } from "react-native-camera";
        
var isHidden = true;

class CameraScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
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

    render() {
        var camera = <>
            <RNCamera
                ref={ref => {
                    this.camera = ref;
                }}
                style={styles.camera}
                captureAudio={false}
                type={this.state.camera.type}
                flashMode={this.state.camera.flashMode}
                captureAudio={false}
            />
        </>

        var screen = <>
        <View style={styles.buttonContainer}>
            <View style={styles.sideBContainer}>
                <TouchableOpacity
                    style={styles.utilButton}
                    onPress={this.rotateCamera.bind(this)}>
                    <Image
                        style={{ flex: 1 }}
                        source={{
                            uri: "https://facebook.github.io/react-native/im/tiny_logo.png"
                        }}
                    />
                </TouchableOpacity>
            </View>
            <View style={styles.mainBContainer}>
                <TouchableOpacity
                    onPress={this.takePicture.bind(this)}
                    style={styles.capButton}>
                    <Icon
                        name='sc-telegram'
                        type='evilicon'
                        color='#517fa4'
                        />
                </TouchableOpacity>
            </View>
            <View style={styles.sideBContainer}>
                <TouchableOpacity
                    style={styles.utilButton}
                    onPress={() => this.props.navigation.navigate('HistoryScreen')}>
                    <Image
                        style={{ flex: 1 }}
                        source={{
                            uri:
                                "https://facebook.github.io/react-native/img/tiny_logo.png"
                        }}
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
            <View
                style={{
                    right: 0,
                    display: "flex",
                    width: "25%",
                    marginLeft: "75%"
                }}>
                <TouchableOpacity onPress={() => this.toggleSubview()}>
                    <Text style={{ textAlign: "right" }}>Exit</Text>
                </TouchableOpacity>
            </View>
            <Image source={this.state.imagePath} />
            <InformationView />
        </Animated.View>
        </>
    
        return [camera, screen];
    
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
            friction: 8.0
        }).start();
    }

    takePicture = async () => {
        if (this.camera) {
            var options = { quality: 0.0001, base64: false, fixOrientation: true, pauseAfterCapture: true, width: 244};
            var data = await this.camera.takePictureAsync(options);

            var imagePathBig = data.uri;
            var imagePath = data.uri;

            console.log(imagePathBig);
            console.log(imagePath);

            this.toggleSubview();

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
                    });
        
                    var result = "";
                    await tflite.detectObjectOnImage({
                        path: imagePath,
                        imageMean: 127.5,
                        imageStd: 127.5,
                        threshold: 0.05,             // defaults to 0.1
                        numResultsPerClass: 5,// defaults to 5
                    },
                        (err, res) => {
                            if(err)
                                console.log(err);
                            else {
                                console.log(res);
                                this.props.navigation.navigate('InfoScreen', {
                                    imgUri: imagePath,
                                    recData1: res[0].detectedClass + ": " + res[0].confidenceInClass,
                                    recData2: res[1].detectedClass + ": " + res[1].confidenceInClass,
                                    recData3: res[2].detectedClass + ": " + res[2].confidenceInClass,
                                    recData4: res[3].detectedClass + ": " + res[3].confidenceInClass,
                                    recData5: res[4].detectedClass + ": " + res[4].confidenceInClass,
                                });
                            }
                        }
                    );

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
        backgroundColor: "#e7c9ff"
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
        borderColor: "white",
        borderWidth: 2,
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
        borderWidth: 2,
        bottom: 0,
        right: 0
    }
});

export default CameraScreen;
