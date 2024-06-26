package com.recognizer.classifier;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.recognizer.IModelHelper;
import com.recognizer.ModelHelperOptions;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp;

import java.nio.ByteBuffer;
import java.util.List;

public class ImageClassifierHelper implements IModelHelper {
  private ByteBuffer buffer;
  private Interpreter model;
  private List<String> labels;
  private ImageClassifierHelperOptions options;
  private float[][] probArray;

  public ImageClassifierHelper(ByteBuffer buffer, List<String> labels) {
    this(buffer, labels, new ImageClassifierHelperOptions());
  }

  public ImageClassifierHelper(ByteBuffer buffer, List<String> labels, ModelHelperOptions options) {
    this.buffer = buffer;
    this.labels = labels;
    this.options = (ImageClassifierHelperOptions) options;

    probArray = new float[1][labels.size()];

    this.init();
  }

  @Override
  public void init() {
    Interpreter.Options modelOptions = new Interpreter.Options()
      .setNumThreads(options.getNbThreads() != 0 ? options.getNbThreads() : Runtime.getRuntime().availableProcessors())
      .setUseNNAPI(options.getUseNNAPI());

    this.model = new Interpreter(this.buffer, modelOptions);
  }

  @Override
  public void clear() {
    this.model.close();
    this.model = null;
  }

  @Override
  public void predict(@NonNull Bitmap image) throws Exception {
    if (this.model == null) {
      throw new Exception("Classifier not loaded");
    }

    // Read input shape from model file
    int[] inputShape = this.model.getInputTensor(0).shape();
    int inputImageWidth = inputShape[1];
    int inputImageHeight = inputShape[2];

    // Preprocessing
    ImageProcessor imageProcessor =
      new ImageProcessor.Builder()
        .add(new ResizeOp(inputImageWidth, inputImageHeight, ResizeOp.ResizeMethod.BILINEAR)) // Resizing image to fit the model's input
        .add(new TransformToGrayscaleOp()) // Transforming image to grayscale to fit the model's input
        .add(new NormalizeOp(0, 255)) // Normalize tensor values to be between 0 and 1
        .build();

    TensorImage tensor = new TensorImage(DataType.FLOAT32);

    tensor.load(image);
    tensor = imageProcessor.process(tensor);

    // Inference
    this.model.run(tensor.getBuffer(), probArray);
 }

  public float[][] getProbArray() {
    return probArray;
  }

  public List<String> getLabels() {
    return labels;
  }

  public ModelHelperOptions getOptions() {
    return options;
  }
}
