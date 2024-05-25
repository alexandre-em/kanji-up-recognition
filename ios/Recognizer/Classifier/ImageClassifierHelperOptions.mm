//
//  ModelHelperOptions.mm
//  react-native-kanji-up-recognition
//
//  Created by Alexandre Em on 25/05/2024.
//

#import "ImageClassifierHelperOptions.h"

@implementation ImageClassifierHelperOptions

@synthesize maxResults;
@synthesize nbThreads;
@synthesize threshold;

@synthesize imageWidth;
@synthesize imageHeight;

- (id) init
{
    int threads = [[NSProcessInfo processInfo] activeProcessorCount];
    
    return [self init:DEFAULT_THRESHOLD threads:threads maxResults:DEFAULT_MAX_RESULT];
}

- (id) init: (float)threshold threads:(int)threads maxResults:(int)maxResults
{
    self = [super init];
    if (self) {
        maxResults = maxResults;
        nbThreads = threads;
        threshold = threshold;
        imageWidth = DEFAULT_IMAGE_WIDTH;
        imageHeight = DEFAULT_IMAGE_HEIGHT;
    }
    return self;
}

@end
