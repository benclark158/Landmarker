import React from 'react';

import styles from './style';

import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  Button,
  Image,
  StatusBar,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

class MainScreen extends React.Component {
    static navigationOptions = {
      title: 'Welcome'
    };

    constructor(props) {
      super(props);
  
      this.camera = null;
  
      this.state = {
        items: {
          item1: "item 1!",
          item2: "item 2!",
          item3: "item 3!",
          item4: "item 4!",
          item5: "item 5!",
        }
      };
    }

    render() {
      return (
        <>
          <StatusBar barStyle="dark-content" />
          <SafeAreaView>
            <ScrollView
              contentInsetAdjustmentBehavior="automatic"
              style={styles.scrollView}>
              <View style={styles.body}>
                <Image 
                  style={{width: 300, height: 400, alignSelf: "center"}}
                  source={{uri: this.props.navigation.getParam('imgUri', 'https://facebook.github.io/react-native/img/tiny_logo.png')}} />
                <View>
                  <Text>{ this.props.navigation.getParam('recData1', '') }</Text>
                  <Text>{ this.props.navigation.getParam('recData2', '') }</Text>
                  <Text>{ this.props.navigation.getParam('recData3', '') }</Text>
                  <Text>{ this.props.navigation.getParam('recData4', '') }</Text>
                  <Text>{ this.props.navigation.getParam('recData5', '') }</Text>
                </View>
                <View>
                  <Button title="Back to camera" color="red" onPress={() => this.props.navigation.navigate('Home')}/>
                </View>
              </View>
            </ScrollView>
          </SafeAreaView>
        </>
      );
    }
  }

export default MainScreen;