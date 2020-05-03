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
var RNFS = require('react-native-fs');

class HistoryScreen extends React.Component {

    /**
     * init of state for component
     * @param {*} props 
     */
    constructor(props) {
        super(props);
        this.state = {
            loaded: false,
            history: [],
        };
    }

    /**
     * renders the views 
     */
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

    /**
     * called when delete all button is pressed
     * Confirms to delete all
     * Deletes all images
     * then clears database
     */
    deleteAll = () => {
        //confirmation of button press
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
                    //do delete all
                    db.transaction(
                        tx => {
                            tx.executeSql("SELECT * FROM 'places';", [], (_, result) => {
                                var rows = result.rows;
            
                                //deletes all images
                                for(var i = 0; i < rows.length; i++) {
                                    var uri = rows._array[i]['uri'];

                                    RNFS.exists(uri)
                                    .then( (result) => {
                                        //console.log("file exists: ", result);
                                
                                        if(result){
                                        return RNFS.unlink(uri)
                                            .then(() => {
                                                //console.log('FILE DELETED');
                                            })
                                            // `unlink` will throw an error, if the item to unlink does not exist
                                            .catch((err) => {
                                                console.log(err.message);
                                            });
                                        }
                                
                                    })
                                    .catch((err) => {
                                        console.log(err.message);
                                    }); 

                                }

                            },
                            (_, err) => {
                                console.log(err);
                            });
                                
                            //deletes all data in database
                            tx.executeSql("DELETE FROM 'places';", [], (_, success) => {
                                this.setState({
                                    history: [],
                                });
                            },
                            (_, err) => {
                                console.log(err);
                            });

                        },
                        null,
                        null
                    );
                },
            }],
            { cancelable: false }
          );
    }

    /**
     * called when the component mounts to screen
     */
    async componentDidMount(){
        
        //loads data from database to table
        db.transaction(
            tx => {
                tx.executeSql("select * from 'places';", [], (_, result) => {
                    var rows = result.rows;
                    
                    //adds rows to state
                    this.setState({
                        points: rows,
                    });

                    var mk = [];

                    //iterates over rows
                    for(var i = 0; i < rows.length; i++) {
                        var obj = rows._array[i];
                    
                        var info = obj['desc'];
                        var length = 28;

                        //formats info correctly
                        if(info == null){
                            info = "";
                        } else {
                            if(info.length > length){
                                info = obj['desc'].substring(0, length) + "..."
                            }
                        }


                        //format name to remove underscore
                        var nm = obj['name'];

                        if(nm != null){
                            nm = nm.replace("_", " ");
                        } else {
                            nm = "";
                        }

                        //add to temp value
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

                    //add to states
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
