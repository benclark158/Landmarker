/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from "react";

import HistoryCard from "./HistoryCard";

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

class HistoryScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            
        };
    }

    render() {
        var card = <>
            
        </>

        var screen = <>
            <ScrollView>
                <HistoryCard
                    imgUrl={"https://cdn-ep19.pressidium.com/wp-content/uploads/2018/07/Aspect-ratio-photography-ras-ul-had-beach-Oman-1.jpg"}
                    title={"Card1"}
                    info={"test"}
                    td={"time and date"}
                    gps={"gps"}>
                </HistoryCard>
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
    },
    title: {
        color: 'black',
        fontSize: 30,
        fontWeight: 'bold'
    }
});

export default HistoryScreen;
