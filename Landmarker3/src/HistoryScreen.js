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

import { RNCamera } from "react-native-camera";
import { ScrollView } from "react-native-gesture-handler";
        
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
                imagePath: "imgs",
                googleID: "test",
                googleUrl: "https://google.com/"
            }
        };
    }

    render() {
        var screen = <>
            <ScrollView>

            </ScrollView>
        </>

        return screen;
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        width: "100%",
        height: "100%",
    }
});

export default CameraScreen;
