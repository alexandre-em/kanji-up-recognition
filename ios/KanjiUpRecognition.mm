#import "KanjiUpRecognition.h"
#import "ImageClassifierHelper.h"
#import "KanjiRecognitionConst.h"

@implementation KanjiUpRecognition
RCT_EXPORT_MODULE()

@synthesize model;
@synthesize labels;
// Example method
// See // https://reactnative.dev/docs/native-modules-ios
RCT_EXPORT_METHOD(load:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  @try {
    NSString *modelPath = [[NSBundle mainBundle] pathForResource:@"kanji_model"
                                                          ofType:@"tflite"];
    
    ImageClassifierHelper* ich = [ImageClassifierHelper alloc];
    model = [ich initWithModel:modelPath];
    
    [self loadLabels];
    
    [model initialize];
    
    resolve(modelPath);
  }
  @catch (NSException *e) {
    reject(@"load_failure", e.reason, nil);
  }
}


RCT_EXPORT_METHOD(predict:(NSString *)base64Image
                  resolver:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    @try {
      if (!self.model) {
        reject(@"model_error", @"Model is not loaded, please call load method", [NSError init]);
      }
      // Convert Base64 image into a UIImage
      NSData *imageData = [[NSData alloc] initWithBase64EncodedString:base64Image options:NSDataBase64DecodingIgnoreUnknownCharacters];
      UIImage *image = [UIImage imageWithData:imageData];
      
      if (!image) {
        reject(@"image_error", @"Impossible de décoder l'image Base64", nil);
        return;
      }
      
      const float* outputData = [self.model predict:image];
      
      NSArray<NSDictionary *> *results = [self getTopNScores:outputData n:DEFAULT_MAX_RESULT];
      
      resolve(results);
    }
    @catch (NSException *e) {
      reject(@"prediction_failure", e.reason, nil);
    }
  });
}

- (void) loadLabels {
  NSError *error;
  
  // NSLog(@"Loading labels...");
  
  NSString *labelsPath = [[NSBundle mainBundle] pathForResource:@"kanji_label" ofType:@"txt"];
  NSString *labelsContent = [NSString stringWithContentsOfFile:labelsPath encoding:NSUTF8StringEncoding error:&error];
  if (error) {
    [NSException raise:@"Copy output failed" format:@"cause: %@", error];
  }
  self.labels = [labelsContent componentsSeparatedByString:@"\n"];
}

- (NSArray<NSDictionary *> *)getTopNScores:(const float *)output n:(int)n {
  // Create an array to store index and scores
  NSMutableArray<NSDictionary *> *results = [NSMutableArray array];
  
  // Adding to array each label and output's scores
  for (int i = 0; i < [self.labels count]; i++) {
    // NSLog(@"label: %@; score: %f", self.labels[i], output[i]);
    [results addObject:@{
      @"label": self.labels[i],
      @"confidence": @(output[i])
    }];
  }
  
  // Sort the array by score
  NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"score" ascending:NO];
  NSArray *sortedResults = [results sortedArrayUsingDescriptors:@[sortDescriptor]];
  
  // Getting the `n` first results
  NSRange topNRange = NSMakeRange(0, MIN(n, sortedResults.count));
  return [sortedResults subarrayWithRange:topNRange];
}

@end
