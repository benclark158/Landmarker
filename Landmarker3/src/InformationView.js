import 'react-native-get-random-values';
import React from 'react';
import {
    StyleSheet,
    View,
    Text,
  } from 'react-native';

  import WebView from 'react-native-webview';

  export default function InformationView(props) {
        return (
            <>
                <View style={styles.container}>
                    <Text style={styles.heading}>
                        {props.title}
                    </Text>
                    <View style={styles.infoContainer}> 
                        <WebView style={styles.information} source={{ uri: "https://benclark158.github.io/mobile.html"  }} />
                    </View>
                </View>
            </>
        );
    }

const styles = StyleSheet.create({
    container: {
      flex: 1,
      width: '100%',
      backgroundColor: 'rgba(0,0,0,0)',
      padding: '2%',
    },
    infoContainer: {
        padding: "2%",
        width: "100%",
        height: "90%",
        backgroundColor: 'rgba(0,0,0,0)'
    },
    heading: {
        color: '#392b58',
        fontSize: 30,
        fontWeight: 'bold'
    },
    information: {
        color: 'black',
        backgroundColor: "rgba(0,0,0,0)",
        fontSize: 14,
        fontWeight: 'normal',
    }
  });