package com.recognizer.classifier;

import com.recognizer.ModelHelperOptions;

public class ImageClassifierHelperOptions implements ModelHelperOptions {

  private float threshold = 0.004f;
  private int maxResults = 20;
  private int nbThreads = 0;
  private boolean useNNAPI = true;
  private long modelInputSize = 4 * 64 * 64 * 1; // We use float32, so it is 4 bytes. default width, height = 64 and by default images are monochrome so 1 channel
  public ImageClassifierHelperOptions() {
  }

  public int getMaxResults() {
    return maxResults;
  }

  public void setMaxResults(int maxResults) {
    this.maxResults = maxResults;
  }

  public long getModelInputSize() {
    return modelInputSize;
  }

  public void setModelInputSize(long modelInputSize) {
    this.modelInputSize = modelInputSize;
  }

  public int getNbThreads() {
    return nbThreads;
  }

  public void setNbThreads(int nbThreads) {
    this.nbThreads = nbThreads;
  }

  public float getThreshold() {
    return threshold;
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }

  public boolean getUseNNAPI() {
    return this.useNNAPI;
  }

  public void setUseNNAPI(boolean useNNAPI) {
    this.useNNAPI = useNNAPI;
  }
}
