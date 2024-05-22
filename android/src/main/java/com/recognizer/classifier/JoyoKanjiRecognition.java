package com.recognizer.classifier;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;


import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.recognizer.IModelHelper;
import com.recognizer.ModelHelperOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class JoyoKanjiRecognition implements IModelHelper<ClassifierResult> {
  private ImageClassifierHelper<ClassifierResult> model;

  public static final String MODEL_FILE_PATH = "kanji_model.tflite";
  public static final String LABEL_FILE_PATH = "kanji_label.txt";


  public JoyoKanjiRecognition(Context context) throws Exception {
    MappedByteBuffer mbb = loadRecognitionModel(context);
    List<String> labels = loadRecognitionLabels(context);

    this.model = new ImageClassifierHelper<>(mbb, labels);
  }

  private MappedByteBuffer loadRecognitionModel(Context context) throws IOException {
    AssetFileDescriptor afd = context.getAssets().openFd(MODEL_FILE_PATH);
    FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
    FileChannel fileChannel = fis.getChannel();

    return fileChannel.map(
      FileChannel.MapMode.READ_ONLY, afd.getStartOffset(), afd.getDeclaredLength());
  }

  private List<String> loadRecognitionLabels(Context context) throws IOException {
    List<String> labels = new ArrayList<>();
    BufferedReader reader =
      new BufferedReader(
        new InputStreamReader(context.getAssets().open(LABEL_FILE_PATH)));

    String line = reader.readLine();
    while (line != null) {
      labels.add(line);
      line = reader.readLine();
    }

    return labels;
  }

  @Override
  public void init() throws Exception {
    this.model.init();
  }

  @Override
  public void clear() {
    this.model.clear();
  }

  @Override
  public void predict(Bitmap image) throws Exception {
    this.model.predict(image);
  }

  @Override
  public WritableArray toWritable() {
    PriorityQueue<WritableMap> pq =
      new PriorityQueue<>(
        1,
        new Comparator<WritableMap>() {
          @Override
          public int compare(WritableMap lhs, WritableMap rhs) {
            return Double.compare(rhs.getDouble("confidence"), lhs.getDouble("confidence"));
          }
        });

    List<String> labels = this.model.getLabels();
    float[] probArray = this.model.getProbArray()[0];
    ModelHelperOptions options = this.model.getOptions();

    for (int i = 0; i < labels.size(); ++i) {
      float confidence = probArray[i];
      if (confidence > options.getThreshold()) {
        WritableMap res = Arguments.createMap();
        res.putInt("index", i);
        res.putString("label", labels.size() > i ? labels.get(i) : "unknown");
        res.putDouble("confidence", confidence);
        pq.add(res);
      }
    }

    WritableArray results = Arguments.createArray();
    int recognitionsSize = Math.min(pq.size(), options.getMaxResults());
    for (int i = 0; i < recognitionsSize; ++i) {
      results.pushMap(pq.poll());
    }

    return results;
  }
}
