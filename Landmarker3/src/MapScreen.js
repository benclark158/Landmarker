/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from "react";

import Icon from 'react-native-vector-icons/FontAwesome5';

import * as SQLite from 'expo-sqlite';

import InformationView from './InformationView';

import {
    StyleSheet,
    TouchableOpacity,
    View,
    Image,
    Animated,
    Text,
    Dimensions,
    NativeModules,
} from "react-native";

import MapView, { PROVIDER_GOOGLE } from "react-native-maps";

var TensorflowImage = NativeModules.TensorflowImage;

const db = SQLite.openDatabase("db.db");

class MapScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            points: null,
            markers: [],
            landmarks: [],
            img: null,
            tracker: true,
            result: {
                title: "Loading",
                info: "",
                hasAdditional: false,
            },
            bounceValue: new Animated.Value(500),
            isHidden: true,
            viewHeight: 0,
            viewHeightInner: 0,
            viewHightOuter: 0,
            webheight: 1000,
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
                    showsUserLocation={true}
                    tracksViewChanges={this.state.tracker}>

                        {this.state.landmarks.map(marker => (
                            <MapView.Marker
                                coordinate={marker.coord}
                                title={marker.name}
                                description={marker.desc}
                                pinColor={"cyan"}
                                icon={""}
                                tracksViewChanges={this.state.tracker}
                                onCalloutPress={() => this.calloutPress(marker.name)}
                            >
                                <Image source={this.state.img} style={{height: 35, width:35 }} />
                            </MapView.Marker>
                        ))}

                        {this.state.markers.map(marker => (
                            <MapView.Marker
                                style={styles.markers}
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

            <Animated.View
            style={[
                styles.subView,
                {
                    transform: [{ translateY: this.state.bounceValue }],
                    height: this.state.viewHeight,
                }
            ]}>
                <TouchableOpacity
                    style={[
                        styles.hider, {
                            height: this.state.viewHightOuter,
                        }
                    ]}
                    onPress={() => this.toggleSubview()}
                />
                <View
                    style={[
                        styles.background, {
                            height: this.state.viewHeightInner,
                        }
                    ]}>
                    <View style={styles.subViewView}>
                        <TouchableOpacity onPress={() => this.toggleSubview()}>
                            <Text style={styles.subViewText}>Close</Text>
                        </TouchableOpacity>
                    </View>
                    
                    <InformationView 
                        title={this.state.result.title}
                        info={this.state.result.info}
                        hasAdditional={this.state.result.hasAdditional}
                        pw={" "}
                        webheight={this.state.webheight}
                        webFunction={event => {
                            this.setState({
                                webheight: parseInt(event.nativeEvent.data),
                            });
                        }}
                        refFunction={ref => (this.webref = ref)}
                        navigation={this.handleNavigationChange}
                        isHidden={this.state.isHidden}
                    />
                </View>
            </Animated.View>
        </>
        return screen;
    }

    handleNavigationChange = newNavState => {
        var runFirst = `
            setInterval(function(){
                window.ReactNativeWebView.postMessage(document.documentElement.scrollHeight);
            }, 2000);
            true; // note: this is required, or you'll sometimes get silent failures
            `;

        this.webref.injectJavaScript(runFirst);
    }

    calloutPress(name){
        var id = name.replace(/ /g, "_");

        this.toggleSubview();

        TensorflowImage.getInfo(id, (name, inf, additional, succ)=>{
            
            this.setState({
                result: {
                    title: name.replace(/_/g, " "),
                    info: inf,
                    hasAdditional: additional,
                },
            });
        },
        (err) => {
            console.log(err);
        })        
    }

    toggleSubview() {
        var hiddenVal = !this.state.isHidden;
        this.setState({
            isHidden: hiddenVal,
        });

        var size = Dimensions.get("window").height;
        var innerSize = size * 0.7;
        var outerSize = size * 0.3;
        

        this.setState({
            buttonText: !hiddenVal ? "Show Subview" : "Hide Subview",
            viewHeight: size,
            viewHeightInner: innerSize,
            viewHightOuter: outerSize,
        });

        var toValue = 0;

        if (hiddenVal) {
            toValue = size;
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

    componentWillUnmount(){
        //console.log("unmount");
        this._unsubscribe.remove();
    }

    componentDidMount(){
        
        this._unsubscribe = this.props.navigation.addListener('focus', () => {
            this.updatePoints();
        });

        this.updatePoints();

        this.setState({
            img: require('./imgs/icon_small.png'),
        })
    }

    updatePoints(){
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
                            title: obj['name'].replace(/_/g, " "),
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

        TensorflowImage.getLandmarkGPS((str) => {
            var json = JSON.parse(str);
            var mk = [];

            for(var i = 0; i < json.length; i++){
                var obj = json[i];

                mk[i] = {
                    name: obj['name'].replace(/_/g, " "),
                    coord: {
                        latitude: obj['latitude'],
                        longitude: obj['longitude'],
                    },
                    desc: "Tap for more information!"
                }
            }
            this.setState({
                landmarks: mk,
                tracker: false,
            });
        },
        (err) => {
            console.log(err);
        })
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
    },
    markers: {
        zIndex: 100,
    },
    subView: {
        position: "absolute",
        bottom: 0,
        left: 0,
        right: 0,
        zIndex: 1000,
        backgroundColor: 'rgba(0,0,0,0)',
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
    hider: {
        backgroundColor: "#000",
        opacity: 0.0,
    },
    background: {
        backgroundColor: "#fff",
        borderTopRightRadius: 20,
        borderTopLeftRadius: 20,
    },

});

export default MapScreen;
