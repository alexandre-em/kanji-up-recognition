package com.recognizer;

import android.graphics.Bitmap;

public interface IModelHelper<T> {
  public void init() throws Exception;
  public void clear();
  public T predict(Bitmap image) throws Exception;
}
