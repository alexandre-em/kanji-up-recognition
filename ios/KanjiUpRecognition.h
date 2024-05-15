
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNKanjiUpRecognitionSpec.h"

@interface KanjiUpRecognition : NSObject <NativeKanjiUpRecognitionSpec>
#else
#import <React/RCTBridgeModule.h>

@interface KanjiUpRecognition : NSObject <RCTBridgeModule>
#endif

@end
