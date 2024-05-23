package com.kanjiuprecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.IResult;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.recognizer.IModelHelper;
import com.recognizer.ModelHelperOptions;
import com.recognizer.classifier.ImageClassifierResult;
import com.recognizer.classifier.ImageClassifierHelper;
import com.recognizer.classifier.ImageClassifierHelperOptions;
import com.recognizer.classifier.JoyoKanjiRecognition;

import java.util.List;

@ReactModule(name = KanjiUpRecognitionModule.NAME)
public class KanjiUpRecognitionModule extends ReactContextBaseJavaModule {
  public static final String NAME = "KanjiUpRecognition";
  private IModelHelper model;

  public KanjiUpRecognitionModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private Bitmap getBitmap(String base64) {
    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);

    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void predict(String base64, Promise promise) {
    if (this.model == null) {
      promise.reject(new Exception("Missing model, please load it before calling predict function"));
    }

    try {
      Bitmap image = getBitmap(base64);

      this.model.predict(image);

      float[][] probArrays = ((ImageClassifierHelper) this.model).getProbArray();
      List<String> labels = ((ImageClassifierHelper) this.model).getLabels();
      ModelHelperOptions options = ((ImageClassifierHelper) this.model).getOptions();

      IResult results = new ImageClassifierResult(probArrays[0], labels, (ImageClassifierHelperOptions) options);

      promise.resolve(results.toWritable());
    } catch (Exception e) {
      promise.reject(e);
    }
  }
  @ReactMethod
  public void load(Promise promise) throws Exception {
    if (super.getReactApplicationContext() == null) {
      promise.reject(new Exception("No android context detected"));
    }

    this.model = new JoyoKanjiRecognition(this.getReactApplicationContext());

    try {
      promise.resolve(this.model != null);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void loadCustomModel(String modelPath, String labelPath, Promise promise) {
    try {
      promise.resolve(true);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void isLoaded(Promise promise) {
    if (super.getReactApplicationContext() == null) {
      promise.reject(new Exception("No android context detected"));
    }

    promise.resolve(this.model != null);
  }

  @ReactMethod
  public void clear(Promise promise) {
    try {
      this.model.clear();
      promise.resolve(true);
    } catch (Exception e) {
      promise.reject(e);
    }
  }
}
