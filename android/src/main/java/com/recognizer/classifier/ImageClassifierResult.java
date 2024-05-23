package com.recognizer.classifier;

import com.IResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ImageClassifierResult implements IResult {
  private WritableArray results;
  public ImageClassifierResult(float[] probArray, List<String> labels, ImageClassifierHelperOptions options) {
    PriorityQueue<WritableMap> pq =
      new PriorityQueue<>(
        1,
        new Comparator<WritableMap>() {
          @Override
          public int compare(WritableMap lhs, WritableMap rhs) {
            return Double.compare(rhs.getDouble("confidence"), lhs.getDouble("confidence"));
          }
        });

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

    this.results = results;
  }

  @Override
  public WritableArray toWritable() {
    return null;
  }
}
