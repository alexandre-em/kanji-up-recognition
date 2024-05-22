import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { load, predict } from 'react-native-kanji-up-recognition';

const urls = [
  'https://firebasestorage.googleapis.com/v0/b/alexandre-em.appspot.com/o/uploads%2Fpictures%2Ftest%2F1.png?alt=media&token=af639ead-422d-4ee7-9dbd-db8c1827d1d6',
  'https://firebasestorage.googleapis.com/v0/b/alexandre-em.appspot.com/o/uploads%2Fpictures%2Ftest%2F2.png?alt=media&token=a77c5c2f-8832-4358-8c27-56f415758880',
  'https://firebasestorage.googleapis.com/v0/b/alexandre-em.appspot.com/o/uploads%2Fpictures%2Ftest%2F3.png?alt=media&token=d6c9467c-c0f1-493d-9565-974d92de849e',
  'https://firebasestorage.googleapis.com/v0/b/alexandre-em.appspot.com/o/uploads%2Fpictures%2Ftest%2F4.png?alt=media&token=64c909e2-8ce2-4fcd-b3d5-22ad4e56d518',
  'https://firebasestorage.googleapis.com/v0/b/alexandre-em.appspot.com/o/uploads%2Fpictures%2Ftest%2F5.png?alt=media&token=ebd21348-15b1-4941-bbb0-22ceb8516c16',
  'https://firebasestorage.googleapis.com/v0/b/alexandre-em.appspot.com/o/uploads%2Fpictures%2Ftest%2F6.png?alt=media&token=731a9fcb-93c0-45a9-8e0b-c8a2d0498a9c',
  'https://firebasestorage.googleapis.com/v0/b/alexandre-em.appspot.com/o/uploads%2Fpictures%2Ftest%2FUntitled.png?alt=media&token=8f99f33d-2221-45b5-8772-31bcd2b96929',
];

function fetchBuffer(url: string) {
  return new Promise((accept, reject) => {
    var req = new XMLHttpRequest();
    req.open('GET', url, true);
    req.responseType = 'arraybuffer';

    req.onload = function () {
      var resp = req.response;
      if (resp) {
        accept(resp);
      }
    };

    req.onerror = (err: any) => {
      console.error('Error occured on fetching image:', err);
      reject(err);
    };

    req.ontimeout = () => reject(new TypeError('Network request failed'));

    req.send(null);
  });
}

export default function App() {
  const [result, setResult] = React.useState<boolean | undefined>(false);

  React.useEffect(() => {
    load()
      .then((res) => {
        console.log('Is the model is loaded the first tim? ', res);
        fetchBuffer(urls[0]!)
          .then((res2) =>
            predict(base64ArrayBuffer(res2 as ArrayBuffer))
              .catch(console.error)
              .then(console.log)
          )
          .catch(console.error);
        setResult(true);
      })
      .catch(console.error);
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
