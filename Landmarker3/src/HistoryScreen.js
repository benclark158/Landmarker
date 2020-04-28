/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from "react";

import HistoryCard from "./HistoryCard";
import DeleteAllCard from "./DeleteAllCard";

import {
    StyleSheet,
    Alert,
    ActivityIndicator
} from "react-native";

import { ScrollView } from "react-native-gesture-handler";
import * as SQLite from 'expo-sqlite';

const db = SQLite.openDatabase("db.db");

class HistoryScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loaded: false,
            history: [],
        };
    }

    render() {
        var card = <>
            <ScrollView>
                <DeleteAllCard
                    deleteAll={this.deleteAll}/>

                    <ActivityIndicator
                        style={{paddingTop:"15%",}}
                        size={50}
                        color="#00d3ec" />
                
            </ScrollView>
        </>

        var screen = <>
            <ScrollView>
                <DeleteAllCard
                    deleteAll={this.deleteAll}/>
            
                {this.state.history.map(hist => (
                    <HistoryCard
                        imgUrl={hist.uri}
                        title={hist.name}
                        info={hist.desc}
                        td={hist.date}
                        gps={hist.position}
                    />
                ))}
            </ScrollView>
        </>

        if(this.state.loaded){
            return screen;
        } else {
            return card;
        }
    }

    deleteAll = () => {
        Alert.alert(
            "Delete Entire History",
            "Are you sure you want to delete your entire search history? (This cannot be undone)",
            [{
                text: "No",
                onPress: () => console.log("Cancel Pressed"),
                style: "cancel"
            },{ 
                text: "Yes, delete it all", 
                onPress: () => {
                    db.transaction(
                        tx => {
                            tx.executeSql("DELETE FROM 'places';", [], (_, success) => {
                                    this.setState({
                                        history: [],
                                    });
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
                },
            }],
            { cancelable: false }
          );
    }

    async componentDidMount(){
        
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
                    
                        var info = obj['desc'];
                        var length = 28;

                        if(info == null){
                            info = "";
                        } else {
                            if(info.length > length){
                                info = obj['desc'].substring(0, length) + "..."
                            }
                        }

                        var nm = obj['name'];

                        if(nm != null){
                            nm = nm.replace("_", " ");
                        } else {
                            nm = "";
                        }

                        mk[i] = {
                            position: String(obj['latitude']).substring(0, 6) +
                                ", " + 
                                String(obj['longitude']).substring(0, 6),
                            name: nm,
                            desc: info,
                            uri: obj['uri'],
                            date: obj['time'],
                        }
                        
                    }

                    this.setState({
                        history: mk,
                        loaded: true,
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
    title: {
        color: 'black',
        fontSize: 30,
        fontWeight: 'bold'
    }
});

export default HistoryScreen;
