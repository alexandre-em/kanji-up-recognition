package com.recognizer.classifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.recognizer.IModelHelper;

import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;

import java.util.List;

enum DELEGATION_TYPE {
  DELEGATE_CPU,
  DELEGATE_GPU,
  DELEGATE_NNAPI,
}

public abstract class ImageClassifierHelper<T> implements IModelHelper<T> {
  private String path;
  private String[] labels;
  private Context context;
  private ImageClassifier classifier;
  private DELEGATION_TYPE delegation = DELEGATION_TYPE.DELEGATE_CPU;
  public static final float THRESHOLD = 0.004f;
  public static final int NUM_THREADS = 2;
  public static final int MAX_RESULTS = 16;

  public ImageClassifierHelper(String path, String[] labels, DELEGATION_TYPE delegation) throws Exception {
    this.path = path;
    this.labels = labels;
    this.delegation = delegation;

    init();
  }

  @Override
  public void init() throws Exception {
    ImageClassifier.ImageClassifierOptions.Builder optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
      .setScoreThreshold(THRESHOLD)
      .setMaxResults(MAX_RESULTS);

    BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder().setNumThreads(NUM_THREADS);

    switch (this.delegation) {
      case DELEGATE_GPU:
        if ((new CompatibilityList()).isDelegateSupportedOnThisDevice()) {
          baseOptionsBuilder.useGpu();
        } else {
          throw new Exception("GPU is not supported on this device");
        }
        break;
      case DELEGATE_NNAPI:
        baseOptionsBuilder.useNnapi();
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + this.delegation);
    }

    optionsBuilder.setBaseOptions(baseOptionsBuilder.build());

    try {
      classifier = ImageClassifier.createFromFileAndOptions(this.context, this.path, optionsBuilder.build());
    } catch (IllegalStateException e) {
      Log.e("ImageClassifierHelper", "TFLite failed to load model with error: " + e.getMessage());
      throw new Exception("Image classifier failed to initialize. See error logs for details");
    }
  }

  @Override
  public void clear() {
    classifier = null;
  }

  @Override
  public T predict(Bitmap image) throws Exception {
    if (this.classifier == null) { this.init(); }

    long startTime = SystemClock.uptimeMillis();

    ImageProcessor imageProcessor = new ImageProcessor.Builder().build();

    TensorImage tensorImage = imageProcessor.process(TensorImage.fromBitmap(image));

    ImageProcessingOptions imageProcessingOptions = ImageProcessingOptions.builder()
      .build();

    List<Classifications> results = this.classifier.classify(tensorImage, imageProcessingOptions);

    long inferenceTime = SystemClock.uptimeMillis() - startTime;

    return null;
  }
}
