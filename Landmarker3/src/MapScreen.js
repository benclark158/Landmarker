/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from "react";

import HistoryCard from "./HistoryCard";

import Icon from 'react-native-vector-icons/FontAwesome5';

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

import { ScrollView } from "react-native-gesture-handler";
import MapView, { PROVIDER_GOOGLE } from "react-native-maps";

class MapScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            
        };
    }

    render() {
        var card = <>
            
        </>

        var screen = <>
            <View style={styles.container}>
                <MapView
                    provider={PROVIDER_GOOGLE}
                    style={styles.container}
                    initialRegion={{
                        latitude: 54.5,
                        longitude: -4.5,
                        latitudeDelta: 10,
                        longitudeDelta: 15,

                        //59.682309, -0.445763
                      }}
                />
                <TouchableOpacity style={styles.floatingBtn} onPress={() => this.props.navigation.navigate("HistoryScreen")}>
                    <Icon name="history" size={33} color="rgba(255, 255, 255, 0.8)"
                        style={{alignSelf: "center"}}
                    />
                </TouchableOpacity>
            </View>
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
    floatingBtn: { 
        backgroundColor: "#00d3ec",
        borderColor: "#00d3ec",
        borderWidth: 2,
        color: "#fff",
        position: "absolute",
        display: "flex",
        width: 65,
        height: 65,
        padding: 10,
        margin: 20,
        right: 0,
        bottom: 0,
        borderRadius: 50,
        justifyContent: "center",
        textAlign: "center",
    }
});

export default MapScreen;
