import React from 'react';
import {
    StyleSheet,
    View,
    Text,
  } from 'react-native';

  import WebView from 'react-native-webview';

class InformationView extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            title: "Loading...",
            information: '<div class="sys_record-control sys_news-record" style="fontSize: 50">' +
            '<p>Work continues on the redevelopment of the Portland Building and your brand new Students’ Union.</p><p>On Monday 25 September – at the beginning of Welcome week – Portland Coffee Co. opened its doors to students. If you haven’t already stopped by to have a look, you’ll find it at the end of the level B corridor as you enter the building from East Drive. Grab a 200 Degrees coffee, a pecan plait and find a sofa.</p><h2>What’s next?</h2><p>You will have no doubt noticed that the area behind Portland is still under construction. This will be modelled into an amphitheatre with landscaped gardens and fountains. It’s due to be completed this semester. There will also be a skybridge above The Atrium connecting the two sides of the building and a number of units are yet to come on B floor.</p><p>The Students’ Union is currently reviewing its strategy with student consultations taking place this week. This includes reviewing progress, looking at student feedback and insight from the last three years, and asking future students about their expectations of University and what support they would like.</p><p>To keep up to date with progress, visit the <a title="Students Union website" href="https://www.su.nottingham.ac.uk/unionrefresh/home/">Students’ Union website</a>.</p><div class="sys_news-posted-date">Posted on Wednesday 18th October 2017</div></div>'
            //information: "https://google.com/",
        };
    }


    render(){

        return (
            <>
                <View style={styles.container}>
                    <Text style={styles.heading}>
                        {this.state.title}
                    </Text>
                    <View style={styles.infoContainer}> 
                        <WebView style={styles.information} source={{ html: this.state.information }} />
                    </View>
                </View>
            </>
        );
    }
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

export default InformationView;