//
//  ImageClassifierHelperOptions.mm
//  react-native-kanji-up-recognition
//
//  Created by Alexandre Em on 24/05/2024.
//

#import "ImageClassifierHelper.h"
#import "ImageClassifierHelperOptions.h"
#import "KanjiRecognitionConst.h"
#import "TFLTensorFlowLite.h"

@implementation ImageClassifierHelper {
  NSString *modelPath;
}

@synthesize options;
@synthesize error;
@synthesize modelPath;
@synthesize interpreter;
- (id)initWithModel: (NSString*) model
{
  return [self initWithModel:model options:[[ImageClassifierHelperOptions alloc] init]];
}

- (id)initWithModel: (NSString*) model options:(ImageClassifierHelperOptions*)options
{
  self = [super init];
  if (self) {
    self.modelPath = model;
    self.options = options;
  }
  return self;
}

- (void)initialize {
  error = nil;
  NSError *error;
  
  // Initialize options
  TFLInterpreterOptions* tfOptions = [[TFLInterpreterOptions alloc] init];
  tfOptions.numberOfThreads = self.options.nbThreads;
  
  //    TFLDelegate* delegates = [[TFLDelegate alloc] init];
  //    delegates.
  
  // Initialize an interpreter with the model.
  self.interpreter = [[TFLInterpreter alloc] initWithModelPath:self.modelPath
                                                       options: tfOptions
                                                         error:&error];
  
  if (error != nil) {
    self.error = error;
    return;
  }
  
  // Allocate memory for the model's input `TFLTensor`s.
  [self.interpreter allocateTensorsWithError:&error];
  
  
  if (error != nil) {
    self.error = error;
    [NSException raise:@"Interpreter allocation failed" format:@"cause: %@", error];
  }
}

- (void)clear {
}

- (UIImage*)resizeImage: (UIImage*) image {
  UIGraphicsBeginImageContext(CGSizeMake(self.options.imageWidth, self.options.imageHeight));
  [image drawInRect:CGRectMake(0, 0, self.options.imageWidth, self.options.imageHeight)];
  UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
  
  UIGraphicsEndImageContext();
  return newImage;
}

- (Byte *)convertImageToGrayScale:(UIImage *)image
{
  int channelNb = 1;
  Byte *rawData = (Byte *)calloc(image.size.width * image.size.height * channelNb, sizeof(Byte));
  
  // Create image rectangle with current image width/height
  CGRect imageRect = CGRectMake(0, 0, image.size.width, image.size.height);
  
  // Grayscale color space
  CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceGray();
  
  // Create bitmap content with current image size and grayscale colorspace
  CGContextRef context = CGBitmapContextCreate(rawData, image.size.width, image.size.height, 8, image.size.width, colorSpace, kCGImageAlphaNone);
  
  // Draw image into current context, with specified rectangle
  // using previously defined context (with grayscale colorspace)
  CGContextDrawImage(context, imageRect, [image CGImage]);
  
  // Create bitmap image info from pixel data in current context
  // CGImageRef imageRef = CGBitmapContextCreateImage(context);
  
  // Create a new UIImage object
  // UIImage *newImage = [UIImage imageWithCGImage:imageRef];
  
  // Release colorspace, context and bitmap information
  CGColorSpaceRelease(colorSpace);
  CGContextRelease(context);
  // CFRelease(imageRef);
  //  return newImage;
  
  // Return the raw data of the grayscaled image
  return rawData;
}

- (NSData *)normalizeImage:(Byte *)rawData {
  NSMutableData *data = [NSMutableData dataWithLength:self.options.imageWidth * self.options.imageHeight * sizeof(float)];
  float *floatData = (float *)data.mutableBytes;
  
  for (int i = 0; i < self.options.imageWidth * self.options.imageHeight; i++) {
    // Divide by 255 to have a value between 0 and 1
    floatData[i] = rawData[i] / 255.0f;
  }
  
  return data;
}

- (const float *)predict: (UIImage*) image {
  error = nil;
  NSError *error;
  
  NSData *inputData;  // Should be initialized
  // input data preparation..
  //  NSLog(@"Resizing image");
  UIImage* resizedImage = [self resizeImage:image];
  
  //  NSLog(@"Converting image to grayscale");
  Byte* grayImage = [self convertImageToGrayScale:resizedImage];
  
  //  NSLog(@"UIImagePngRepresentation");
  //  inputData = UIImagePNGRepresentation(grayImage);
  inputData = [self normalizeImage:grayImage];
  
  free(grayImage);
  
  // Get the input `TFLTensor`
  //  NSLog(@"Getting input TFLTensor");
  TFLTensor *inputTensor = [self.interpreter inputTensorAtIndex:0 error:&error];
  
  // Copy the input data to the input `TFLTensor`.
  //  NSLog(@"Copying input data to input");
  [inputTensor copyData:inputData error:&error];
  
  // Run inference by invoking the `TFLInterpreter`.
  //  NSLog(@"Runing inference");
  [self.interpreter invokeWithError:&error];
  if (error != nil) {
    self.error = error;
    [NSException raise:@"Inference failed" format:@"cause: %@", error];
  }
  
  // Get the output `TFLTensor`
  TFLTensor *outputTensor = [interpreter outputTensorAtIndex:0 error:&error];
  if (error != nil || outputTensor == nil) {
    self.error = error;
    [NSException raise:@"Get output tensor failed" format:@"cause: %@", error];
  }
  
  // Copy output to `NSData` to process the inference results.
  NSData *outputData = [outputTensor dataWithError:&error];
  if (error != nil || outputData == nil) {
    self.error = error;
    [NSException raise:@"Copy output failed" format:@"cause: %@", error];
  }
  
  const float *outputBuffer = (const float *)outputData.bytes;
  
  //  // Getting output tensor size (number of classes)
  //  NSArray<NSNumber *> *shape = [outputTensor shapeWithError:&error];
  //  if (error != nil || outputData == nil) {
  //    self.error = error;
  //    [NSException raise:@"Getting output shape failed" format:@"cause: %@", error];
  //  }
  //
  //  // Getting scores array size
  //  NSInteger outputSize = 1;
  //  for (NSNumber *dimension in shape) {
  //    outputSize *= dimension.integerValue;
  //  }
  //
  //  // Print scores
  //  for (int i = 0; i < outputSize; i++) {
  //    NSLog(@"Score %d: %f", i, outputBuffer[i]);
  //  }
  
  return outputBuffer;
}

@end
