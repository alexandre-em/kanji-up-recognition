#import "KanjiUpRecognition.h"
#import "ImageClassifierHelper.h"

@implementation KanjiUpRecognition
RCT_EXPORT_MODULE()

// Example method
// See // https://reactnative.dev/docs/native-modules-ios
RCT_EXPORT_METHOD(resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSString *modelPath = [[NSBundle mainBundle] pathForResource:@"kanji_model"
                                                          ofType:@"tflite"];

    ImageClassifierHelper* ich = [ImageClassifierHelper init:modelPath];

    resolve(result);
}


@end
