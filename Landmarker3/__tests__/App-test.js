/**
 * @format
 */

import 'react-native';
import React from 'react';
import App from '../App';

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';
import SplashScreen from '../src/SplashScreen';
import CameraScreen from '../src/CameraScreen';
import MapScreen from '../src/MapScreen';
import HistoryScreen from '../src/HistoryScreen';

test('splash renders correctly', () => {
  const tree = renderer.create(<App />).toJSON();
  expect(tree).toMatchSnapshot();
});

/*
test('camera renders correctly', () => {
  const tree = renderer.create(<CameraScreen />).toJSON();
  expect(tree).toMatchSnapshot();
});

test('map renders correctly', () => {
  const tree = renderer.create(<MapScreen />).toJSON();
  expect(tree).toMatchSnapshot();
});

test('history renders correctly', () => {
  const tree = renderer.create(<HistoryScreen />).toJSON();
  expect(tree).toMatchSnapshot();
});*/
