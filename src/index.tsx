import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-kanji-up-recognition' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const KanjiUpRecognition = NativeModules.KanjiUpRecognition
  ? NativeModules.KanjiUpRecognition
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function load(): Promise<any> {
  return KanjiUpRecognition.load();
}

export function predict(buffer: any): Promise<any> {
  return KanjiUpRecognition.predict(buffer);
}
