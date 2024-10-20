# @kanjiup/recognition

A library for recognizing most of the joyo Kanji characters (around 2600 characters),
for the [KanjiUp](https://github.com/alexandre-em/kanji-up) application

## Installation

This library is not destinated to React Native built with Expo projects,
so you will have to eject your project before installing it.
As this project is using natives functionalities.

### Configuration

#### Android

You will just have to allows the project to use `tflite` files,
by adding to your `android/app/build.gradle`, the following setting in the **`android`** block :

```gradle
    aaptOptions {
        noCompress 'tflite'
    }
```

#### iOS

1. Initialize Pod

```sh
cd ios
pod init
```

2. Add the `TensorflowLiteObjC` dependency by adding to the `Podfile` :

```pod
target '[your project's name]' do
pod 'TensorFlowLite', '1.12.0'
end
```

3. Install the dependency

```sh
pod install
```

### Installing @kanjiup/recognition package

The package has been pushed on a private Github repository so you will have to
first create a `.npmrc` file at the root of your project containing :

```ruby
//github.com/:_authToken=${MY_GITHUB_TOKEN}
```

This config file will allows you to install the library without exposing your token

`MY_GITHUB_TOKEN` is an environment variable,
so next you will have to export it into your terminal
or save it on your .zshrc/.bashrc or anything else :

```bash
export MY_GITHUB_TOKEN="your_token"
```

Then you can finally install the package with npm, yarn :

```sh
npm install @kanjiup/recognition@git+https://github.com/alexandre-em/kanji-up-recognition.git
```

```sh
yarn add @kanjiup/recognition@git+https://github.com/alexandre-em/kanji-up-recognition.git
```

### Linking the package

As said previously, this package contains native codes,
so you will probably need to link the package after installation
depending of your RN version

For React Native versions >= 0.60, autolinking should take care of this.
However, if you encounter issues, you can manually link:

```sh
npx react-native link @kanjiup/recognition
```

### Add models on your project (iOS only)

In XCode, right click on the project folder,
click Add Files to `<your project name>`..., select the model and label files.

> Also be sure that your model file name is `kanji_model.tflite` and labels is `kanji_label.txt`

## Usage

### Example

```js
import { load, predict, urlToBase64 } from '@kanjiup/recognition';

await load();
const imageUrl: string = 'https://storage.kanjiup.alexandre-em/kanji-test-ai.png';
const base64Img: string = await urlToBase64(imageUrl);
// Waiting for the model to load
const predictions: { label: string, confidence: number }[] = await predict(base64Img);
```

### Image classification

- `parameters`: a image in base64 (temporary)
- `returns`: A list of 20 objects containing:
  - `label`: a kanji character
  - `confidence`: the score of recognition of the kanji

outputs:

```js
[
  { confidence: 0.41452574729919434, label: '一' },
  { confidence: 0.000024757748178672045, label: '丁' },
  { confidence: 0.0000012626431953322026, label: '七' },
  { confidence: 3.173502136633033e-7, label: '万' },
  { confidence: 3.3304385915755574e-9, label: '丈' },
  { confidence: 2.132016230227407e-10, label: '三' },
  { confidence: 0.000003578964879125124, label: '上' },
  { confidence: 0.0000018518686601964873, label: '下' },
  { confidence: 5.773828637423151e-10, label: '不' },
  { confidence: 6.501041371897998e-11, label: '与' },
  { confidence: 6.6362562146926596e-12, label: '丑' },
  { confidence: 2.5856167207294334e-10, label: '且' },
  { confidence: 3.3259339479420813e-15, label: '世' },
  { confidence: 1.8014820746259375e-9, label: '丘' },
  { confidence: 1.04574859882689e-9, label: '丙' },
  { confidence: 9.181520696727521e-14, label: '丞' },
  { confidence: 1.1745091661394258e-13, label: '両' },
  { confidence: 7.106326377195394e-16, label: '並' },
  { confidence: 0.00005564207094721496, label: '中' },
  { confidence: 2.1985629462817347e-12, label: '串' },
];
```

## TODO

- Update the `predict` function to take an image path as input,
  and move the previous one to a new `predictAImageBase4`
- Add unit tests
- Improve the model accuracy
- Fix (if possible) the iOS build CI

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
