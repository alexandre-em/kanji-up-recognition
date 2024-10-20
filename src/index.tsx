import { NativeModules, Platform } from 'react-native';
import { base64ArrayBuffer, fetchBuffer } from './utils';

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

export async function urlToBase64(url: string) {
  const data = await fetchBuffer(url);
  return base64ArrayBuffer(data as ArrayBuffer);
}
