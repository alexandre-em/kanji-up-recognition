package com.recognizer;

import android.graphics.Bitmap;

import java.util.List;

public interface IModelHelper<T> {
  public void init() throws Exception;
  public void clear();
  public float[][] predict(Bitmap image) throws Exception;
}
