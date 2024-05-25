//
//  ImageClassifierHelperOptions.mm
//  react-native-kanji-up-recognition
//
//  Created by Alexandre Em on 24/05/2024.
//

#import "ImageClassifierHelper.h"
#import "ImageClassifierHelperOptions.h"
#import "TFLTensorFlowLite.h"

@implementation ImageClassifierHelper {
    NSString *modelPath;
}

@synthesize results;

- (id)init: (NSString*) model
{
    return [self init:model options:[[ImageClassifierHelperOptions alloc] init]];
}

- (id)init: (NSString*) model options:(ImageClassifierHelperOptions*)options
{
    self = [super init];
    if (self) {
        modelPath = model;
        options = options;
    }
    return self;
}

- (void)initialize {
    error = nil;
    NSError *error;
    
    // Initialize options
    TFLInterpreterOptions* tfOptions = [[TFLInterpreterOptions alloc] init];
    tfOptions.numberOfThreads = options.nbThreads;
    
//    TFLDelegate* delegates = [[TFLDelegate alloc] init];
//    delegates.

    // Initialize an interpreter with the model.
    interpreter = [[TFLInterpreter alloc] initWithModelPath:modelPath
                                                    options: tfOptions
                                                      error:&error];
    
    if (error != nil) {
        error = error;
        return;
    }
    
    // Allocate memory for the model's input `TFLTensor`s.
    [interpreter allocateTensorsWithError:&error];
    
    
    if (error != nil) {
        error = error;
        return;
    }
}

- (void)clear {
}

- (UIImage*)resizeImage: (UIImage*) image {
    UIGraphicsBeginImageContext(CGSizeMake(options.imageWidth, options.imageHeight));
    [image drawInRect:CGRectMake(0, 0, options.imageWidth, options.imageHeight)];
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    return newImage;
}

- (UIImage *)convertImageToGrayScale:(UIImage *)image
{
  // Create image rectangle with current image width/height
  CGRect imageRect = CGRectMake(0, 0, image.size.width, image.size.height);
 
  // Grayscale color space
  CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceGray();
 
  // Create bitmap content with current image size and grayscale colorspace
  CGContextRef context = CGBitmapContextCreate(nil, image.size.width, image.size.height, 8, 0, colorSpace, kCGImageAlphaNone);
 
  // Draw image into current context, with specified rectangle
  // using previously defined context (with grayscale colorspace)
  CGContextDrawImage(context, imageRect, [image CGImage]);
 
  // Create bitmap image info from pixel data in current context
  CGImageRef imageRef = CGBitmapContextCreateImage(context);
 
  // Create a new UIImage object
  UIImage *newImage = [UIImage imageWithCGImage:imageRef];
 
  // Release colorspace, context and bitmap information
  CGColorSpaceRelease(colorSpace);
  CGContextRelease(context);
  CFRelease(imageRef);
 
  // Return the new grayscale image
  return newImage;
}

- (void)predict: (UIImage*) image {
    error = nil;
    NSError *error;
    
    NSData *inputData;  // Should be initialized
    // input data preparation...
    UIImage* resizedImage = [self resizeImage:image];
    UIImage* grayImage = [self convertImageToGrayScale:resizedImage];
    
    inputData = UIImagePNGRepresentation(grayImage);

    // Get the input `TFLTensor`
    TFLTensor *inputTensor = [interpreter inputTensorAtIndex:0 error:&error];

    // Copy the input data to the input `TFLTensor`.
    [inputTensor copyData:inputData error:&error];
    
    // Run inference by invoking the `TFLInterpreter`.
    [interpreter invokeWithError:&error];
    
    if (error != nil) {
        error = error;
        return;
    }

    // Get the output `TFLTensor`
    TFLTensor *outputTensor = [interpreter outputTensorAtIndex:0 error:&error];
    if (error != nil) {
        error = error;
        return;
    }
    
    // Copy output to `NSData` to process the inference results.
    results = [outputTensor dataWithError:&error];
    if (error != nil) {
        error = error;
        return;
    }
}

@end
