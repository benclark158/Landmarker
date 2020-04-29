import 'react-native-get-random-values';
import React from 'react';
import {
    StyleSheet,
    View,
    Text,
    ActivityIndicator,
} from 'react-native';

import AutoHeightWebView from 'react-native-autoheight-webview'
import WebView from 'react-native-webview'
import { ScrollView } from 'react-native-gesture-handler';

export default function InformationView(props) {

    var code = "<body style=\"width:100%\"><p>Hello, this is some information.</p></iframe></body>";
    //props.title

    var name = props.title.replace(/_/g, " ");
    var pleaseWaitTxt = "Please hold the camera still while we take the photo";

    if(props.pw != null && props.pw != ""){
        pleaseWaitTxt = props.pw;
    }

    if(props.isHidden){
        return <></>
    }

    if(props.hasAdditional && props.info != ""){
        return (
            <>  
            <ScrollView style={styles.container}>
                <Text style={styles.heading}>
                    {name}
                </Text>
                <Text style={{paddingLeft: "5%",paddingRight: "5%",}}>
                    {props.info}
                </Text>
                <Text style={styles.subHeading}>
                    Additional Information
                </Text>
                <View style={styles.infoContainer}> 
                    <WebView 
                        ref={props.refFunction}
                        style={[
                            styles.extraInfo, {
                                flex: 0,
                                height: props.webheight,
                            }
                        ]}
                        //onSizeUpdated={props.webFunction}
                        automaticallyAdjustContentInsets={false}
                        scrollEnabled={false}
                        source={{ uri: "https://en.wikipedia.org/wiki/" + props.title  }}
                        onMessage={props.webFunction}
                        onNavigationStateChange={props.navigation}
                        javaScriptEnabled={true}
                        domStorageEnabled={true}
                        useWebKit={true}
                    />
                </View>
            </ScrollView>
            </>
        );
    } else if(!props.hasAdditional && props.info != ""){
        return (
            <>  
            <ScrollView style={styles.container}>
                <Text style={styles.heading}>
                    {name}
                </Text>
                <Text style={{paddingLeft: "5%",paddingRight: "5%",}}>
                    {props.info}
                </Text>
            </ScrollView>
            </>
        );
    } else if(props.hasAdditional && props.info == ""){
        return (
            <>  
            <ScrollView style={styles.container}>
                <Text style={styles.heading}>
                    {name}
                </Text>
                <View style={styles.infoContainer}> 
                    <WebView 
                        ref={props.refFunction}
                        style={[
                            styles.extraInfo, {
                                flex: 0,
                                height: props.webheight,
                            }
                        ]}
                        //onSizeUpdated={props.webFunction}
                        automaticallyAdjustContentInsets={false}
                        scrollEnabled={false}
                        source={{ uri: "https://en.wikipedia.org/wiki/" + props.title  }}
                        onMessage={props.webFunction}
                        onNavigationStateChange={props.navigation}
                        javaScriptEnabled={true}
                        domStorageEnabled={true}
                        useWebKit={true}
                    />
                </View>
            </ScrollView>
            </>
        );
    } else {
        return (
            <>  
            <View style={styles.container}>
                <ActivityIndicator style={{paddingTop:"15%",}} size={50} color="#00d3ec" />
                <Text style={{width: "100%", textAlign: "center", bottom: 0, paddingTop: "5%"}}>
                    Loading Results
                </Text>
                <Text style={{width: "100%", textAlign: "center", bottom: 0, paddingTop: "0%"}}>
                    {pleaseWaitTxt}
                </Text>
            </View>
            </>
        );
    }
}

const styles = StyleSheet.create({
    container: {
      flex: 1,
      height: '100%',
      backgroundColor: 'rgba(0,0,0,0)',
      padding: '2%',
      width: "100%",
    },
    infoContainer: {
        padding: "2%",
        backgroundColor: 'rgba(0,0,0,0)',
        height: "100%",
    },
    heading: {
        color: '#392b58',
        fontSize: 30,
        fontWeight: 'bold',
        paddingBottom: "2%",
    },
    subHeading: {
        paddingTop: "2%",
        color: '#392b58',
        fontSize: 20,
        fontWeight: 'normal'
    },
    information: {
        color: 'black',
        backgroundColor: "rgba(0,0,0,0)",
        padding: "10%",
    },
    extraInfo: {
        color: 'black',
        backgroundColor: "rgba(0,0,0,0)",
        width: "100%",
        height: "100%",
    },
  });