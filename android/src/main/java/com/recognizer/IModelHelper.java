package com.recognizer;

import android.graphics.Bitmap;

import com.facebook.react.bridge.WritableArray;

import java.util.List;

public interface IModelHelper {
  public void init() throws Exception;
  public void clear();
  public void predict(Bitmap image) throws Exception;
}
