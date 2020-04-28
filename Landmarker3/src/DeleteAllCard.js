/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from "react";

import {
    StyleSheet,
    Animated,
    Text,
    TouchableOpacity,
    View,
} from "react-native";
        
export default function DeleteAllCard(props) {
        return ( 
        <>
            <TouchableOpacity onPress={props.deleteAll}>
                <View style={styles.container}>
                    <View style={styles.imageStack}>
                        <Text style={styles.title}>Delete History</Text>
                    </View>
                </View>
            </TouchableOpacity>
        </>
    );
}

const styles = StyleSheet.create({

      cardContent: {
        
      },

      container: {
        flex: 1,
        borderRadius: 6,
        elevation: 3,
        backgroundColor: '#ff0000',
        shadowOffset: { width: 1, height: 1 },
        shadowColor: '#333',
        shadowOpacity: 0.3,
        shadowRadius: 2,
        marginHorizontal: 4,
        marginVertical: 6,
      },
      image: {
        top: 0,
        left: 0,
        width: 84,
        height: 84,
        position: "absolute",
        borderRadius: 10,
        borderColor: "#000000",
        borderWidth: 0,
        overflow: "hidden"
      },
      title: {
        top: 0,
        left: 95,
        color: "#fff",
        position: "absolute",
        fontWeight: 'bold',
        fontSize: 20,
        fontFamily: "roboto-700"
      },
      data: {
        top: 52,
        left: 95,
        color: "#121212",
        position: "absolute",
        opacity: 0.65,
        fontSize: 14,
        fontFamily: "roboto-regular",
        lineHeight: 16
      },
      info: {
        top: 26,
        left: 95,
        color: "#121212",
        position: "absolute",
        fontSize: 16,
        fontFamily: "roboto-regular",
        lineHeight: 16
      },
      imageStack: {
        //width: 226,
        height: 34,
        //marginTop: 53,
        //marginLeft: 28
        marginHorizontal: 18,
        marginVertical: 20,
      }
});