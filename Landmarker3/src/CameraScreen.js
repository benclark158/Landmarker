import React from "react";

import InformationView from "./InformationView.js";

import {
    StyleSheet,
    Animated,
    Text,
    TouchableOpacity,
    View,
    Dimensions,
    BackHandler,
    NativeModules,
    Linking
} from "react-native";

import RNLocation from "react-native-location";

import { Camera } from 'expo-camera';
import * as Permissions from 'expo-permissions';

import * as SQLite from 'expo-sqlite';

import Icon from 'react-native-vector-icons/Ionicons';

var TensorflowImage = NativeModules.TensorflowImage;

const db = SQLite.openDatabase("db.db");

class CameraScreen extends React.Component {

    /**
     * builds initial state of component
     * @param {*} props 
     */
    constructor(props) {
        super(props);
        this.state = {
            hasCamPermission: false,
            hasRollPermission: false,
            cameraType: Camera.Constants.Type.back,
            bounceValue: new Animated.Value(500), //This is the initial position of the subview
            viewHeight: 0,
            isHidden: true,
            imageUri: "",
            result: {
                title: "Loading...",
                hasAdditional: false,
                info: "",
            },
            dateTime: "",
            location: {
                latitude: 0,
                longitude: 0,
                accuracy: 0,
            },
        };
    }

    /**
     * function for rendering the camera based on permissions and if the screen is active
     */
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
                        type={this.state.cameraType}
                        style={{ flex: 1 }}
                        ratio={"16:9"}
                    />
                </View>
            );
        }
    }

    /**
     * Overrided method
     * renders the screen
     */
    render() {
        var cameraView = this.renderCamera();

        var screen = <>
        <View style={styles.topContainer}>
            <View style={styles.leftContainer}>
                <TouchableOpacity
                    style={styles.topButton}
                    onPress={() => this.openGithubIssues()}>
                    <Icon name="ios-warning" size={40} color="rgba(255, 255, 255, 0.8)"
                        style={{alignSelf: "center"}}
                    />
                </TouchableOpacity>
            </View>
        </View>
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

        //combines the camera view and screen views
        return [cameraView, screen];
    }

    /**
     * Opens github issues page
     */
    openGithubIssues(){
        var url = "https://github.com/benclark158/Landmarker-Issues/issues/new";
        Linking.canOpenURL(url).then(supported => {
            if (supported) {
                Linking.openURL(url);
            } else {
                console.log("Don't know how to open URI: " + url);
            }
          });
    }

    /**
     * Handles injecting of javascript into a newly loaded website
     * This ensures that the webview is kept at the correct size.
     */
    handleNavigationChange = newNavState => {
        var runFirst = `
            setInterval(function(){
                window.ReactNativeWebView.postMessage(document.documentElement.scrollHeight);
            }, 2000);
            true; // note: this is required, or you'll sometimes get silent failures
            `;

        this.webref.injectJavaScript(runFirst);
    }

    /**
     * Gets the current location of the phone
     * Takes the photo
     * Runs it through tensorflow for results
     * Displays the results
     * Saves result to database
     */
    takePicture = async () => {
        
        this.getDataTime();

        this.camera.pausePreview();

        //take photo

        //resets params
        this.setState({
            result: {
                title: "",
                hasAdditional: false,
                info: "",
            }
        });

        if (this.camera) {

            //shows loading screen
            this.toggleSubview();

            //takes photo
            this.camera.resumePreview();
            var options = { quality: 1, base64: false, pauseAfterCapture: true};
            var data = await this.camera.takePictureAsync(options);
            this.camera.pausePreview();
            
            var uri = data.uri;

            this.setState({
                imageUri: uri,
            })

            //get location
            var latitude = this.state.location.latitude;
            var longitude = this.state.location.longitude;
            var accuracy = this.state.location.accuracy;

            //Testing!
            //latitude = 51.505129;
            //longitude = -0.078393;
            //uri = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/A_Tower-h%C3%ADd_%28The_Tower_Bridge%29_-_panoramio.jpg/1024px-A_Tower-h%C3%ADd_%28The_Tower_Bridge%29_-_panoramio.jpg"

            //begins classification
            await TensorflowImage.classify("imgGPS-30-v3.tflite", uri, latitude, longitude, accuracy,
                (err) => {console.log(err)},
                (name, info, hasAdd, probs) => {

                    //saves results into state
                    this.setState({
                        result: {
                            title: name,
                            hasAdditional: hasAdd,
                            info: info,
                        }
                    });

                    var vals = [name, uri, info, 
                        this.state.dateTime, latitude, longitude];

                    //adds values to database
                    db.transaction(
                        tx => {
                            tx.executeSql("insert into 'places' (" + 
                                "'name', 'uri', 'desc', 'time', 'latitude', 'longitude')" + 
                                "values (?, ?, ?, ?, ?, ?)", vals, (_, success) => {
                                    //console.log(success);
                                },
                                (_, err) => {
                                    console.log(err);
                                });
                            
                            //tx.executeSql("select * from items", [], (_, { rows }) =>
                            //    console.log(JSON.stringify(rows))
                            //);
                        },
                        null,
                        null
                    );
                });
        }
    }

    /**
     * Gets the current time and data, adds to state as string
     */
    getDataTime() {
        var that = this;
        var date = new Date().getDate(); //Current Date
        var month = new Date().getMonth() + 1; //Current Month
        var year = new Date().getFullYear(); //Current Year
        var hours = new Date().getHours(); //Current Hours
        var min = new Date().getMinutes(); //Current Minutes

        that.setState({
        //Setting the value of the date time
            dateTime:
                date + '/' + month + '/' + year + ' ' + hours + ':' + min,
            });
    }

    /**
     * Opens or closes the animated results view
     */
    toggleSubview() {
        var hiddenVal = !this.state.isHidden;
        this.setState({
            isHidden: hiddenVal,
        });

        //calcs sizes
        var size = Dimensions.get("window").height;
        var innerSize = size * 0.7;
        var outerSize = size * 0.3;
        
        //sets sizes
        this.setState({
            buttonText: !hiddenVal ? "Show Subview" : "Hide Subview",
            viewHeight: size,
            viewHeightInner: innerSize,
            viewHightOuter: outerSize,
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

    /**
     * Called qwhen the component mounts
     */
    async componentDidMount() {
        //checks permissions
        const { status } = await Permissions.askAsync(Permissions.CAMERA, Permissions.CAMERA_ROLL);
        this.setState({ 
            hasCamPermission: status === 'granted',
        });

        //checks when this screen is in focus
        this.focusListener = this.props.navigation.addListener('focus', () => {
            if(this.state.isHidden){
                this.camera.pausePreview();
            }
        });
        
        //registers location updater
        this.getLocationUpdates();

        //register back handler!
        this.backHandler = BackHandler.addEventListener(
            "hardwareBackPress",
            this.backAction
        );
        
        //creates table if it doesnt exist
        db.transaction(tx => {
            tx.executeSql(
              "CREATE TABLE IF NOT EXISTS 'places' (" + 
                "'id'	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "'name'	TEXT," + 
                "'uri'	TEXT," +
                "'desc'	TEXT," +
                "'time'	TEXT," +
                "'latitude'	REAL," +
                "'longitude'	REAL" +
            ");",
                [],
                (_, result) => {
                    //console.log("CORRECT");
                    //console.log(result);
                },
                (_, err) => {console.log(err)}
            );
        });
    }

    /**
     * Creates location update listener
     */
    getLocationUpdates(){

        //configures location service
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
        
        //subscribes to locations
        this.locationUpdater = RNLocation.subscribeToLocationUpdates(locations => {
            this.setState({
                location:{
                    latitude: locations[0]['latitude'],
                    longitude: locations[0]['longitude'],
                    accuracy: locations[0]['accuracy'],
                }
            });
        });
    }

    /**
     * called when the screen unmounts
     */
    componentWillUnmount(){
        //removes all subscribers
        this.backHandler.remove();
        this.focusListener.remove();
        this.locationUpdater();
    }

    /**
     * Actions for when the user presses the back button
     */
    backAction = () => {
        if(this.props.navigation.isFocused()){
            if(this.state.isHidden){
                //if not showing subview
                BackHandler.exitApp();
                return true;
            } else {
                //remove subview
                this.toggleSubview();
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Called when the rotate camera button is pressed
     * Rotates the camera using states
     */
    rotateCamera = () => {
        //console.log(this.state.cameraType);
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

/**
 * Style sheet for views
 */
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
        flex: 4,
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
    },
    topContainer: {
        position: "absolute",
        left: 0,
        right: 0,
        top: 0,
        display: "flex",
        flex: 0,
        zIndex: 5,
        height: "90%",
        flexDirection: "row",
        alignItems: "center",
        position: "absolute",
        alignSelf: "flex-end",
        height: "10%"
    },
    topButton: {
        height: 40,
        width: 40,
        maxWidth: 40,
        maxWidth: 40,
        margin: 10,
        flex: 1,
        borderColor: "white",
        borderWidth: 0,
        top: 0,
        right: 0
    },
    leftContainer: {
        right: 0,
        top: 0,
        width: "100%",
        alignItems: "flex-end",
    }
});

export default CameraScreen;