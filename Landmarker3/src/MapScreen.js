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

import * as SQLite from 'expo-sqlite';

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

const db = SQLite.openDatabase("db.db");

class MapScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            points: null,
            markers: [],
        };
    }

    render() {
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
                    showsUserLocation={true}>

                        {this.state.markers.map(marker => (
                            <MapView.Marker
                                coordinate={marker.coord}
                                title={marker.title}
                               // description={marker.description}
                            />
                        ))}
                </MapView>
                
                
                <TouchableOpacity style={styles.floatingBtn} onPress={() => this.props.navigation.navigate("HistoryScreen")}>
                    <Icon name="history" size={33} color="rgba(255, 255, 255, 0.8)"
                        style={{alignSelf: "center"}}
                    />
                </TouchableOpacity>
            </View>
        </>
        return screen;
    }

    componentWillUnmount(){
        //console.log("unmount");
        this._unsubscribe.remove();
    }

    componentDidMount(){
        
        this._unsubscribe = this.props.navigation.addListener('focus', () => {
            this.updatePoints();
        });

        this.updatePoints();
    }

    updatePoints(){
        console.log("update");

        db.transaction(
            tx => {
                tx.executeSql("select * from 'places';", [], (_, result) => {
                    var rows = result.rows;
                    this.setState({
                        points: rows,
                    });

                    var mk = [];

                    for(var i = 0; i < rows.length; i++) {
                        var obj = rows._array[i];
                    
                        mk[i] = {
                            coord: {
                                latitude: obj['latitude'], 
                                longitude: obj['longitude']
                            },
                            title: obj['name'],
                        }
                        
                    }

                    this.setState({
                        markers: mk
                    });
                },
                (_, err) => {
                    console.log(err);
                });
            },
            null,
            null
        );
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
