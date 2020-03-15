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
});

export default MapScreen;
