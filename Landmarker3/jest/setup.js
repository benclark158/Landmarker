jest.mock('react-native-location', () => ({
    __esModule: true,
    default: {
      requestPermission: jest.fn(() => Promise.resolve(true)),
      getLatestLocation: jest.fn(() => Promise.resolve()),
    },
  }));